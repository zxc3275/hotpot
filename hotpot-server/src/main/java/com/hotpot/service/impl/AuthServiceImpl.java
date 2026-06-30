package com.hotpot.service.impl;

import com.hotpot.common.BusinessException;
import com.hotpot.dto.LoginDTO;
import com.hotpot.entity.User;
import com.hotpot.mapper.RoleMapper;
import com.hotpot.mapper.UserMapper;
import com.hotpot.service.AuthService;
import com.hotpot.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @Override
    public Map<String, Object> login(LoginDTO dto) {
        User user = userMapper.findByUsername(dto.getUsername());
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }
        var roles = roleMapper.findByUserId(user.getId());
        List<String> roleCodes = roles.stream().map(r -> r.getCode()).collect(Collectors.toList());
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("roles", roleCodes);
        String token = jwtUtils.generateToken(user.getUsername(), claims);
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        user.setPassword(null);
        result.put("user", user);
        result.put("roles", roleCodes);
        // Get menus for the first role
        if (!roles.isEmpty()) {
            var menus = roleMapper.findMenusByRoleId(roles.get(0).getId());
            result.put("menus", menus);
        }
        return result;
    }

    @Override
    public void register(User user) {
        User exist = userMapper.findByUsername(user.getUsername());
        if (exist != null) throw new BusinessException("用户名已存在");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setBalance(0);
        user.setPoints(0);
        user.setStatus(1);
        userMapper.insert(user);
    }

    @Override
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userMapper.findByUsername(username);
        if (user != null) user.setPassword(null);
        return user;
    }
}
