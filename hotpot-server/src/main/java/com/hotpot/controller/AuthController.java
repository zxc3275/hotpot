package com.hotpot.controller;

import com.hotpot.common.Result;
import com.hotpot.dto.LoginDTO;
import com.hotpot.entity.User;
import com.hotpot.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public Result<?> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(authService.login(dto));
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody User user) {
        authService.register(user);
        return Result.success("注册成功");
    }

    @GetMapping("/userinfo")
    public Result<?> userinfo() {
        return Result.success(authService.getCurrentUser());
    }
}
