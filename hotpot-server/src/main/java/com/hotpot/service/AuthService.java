package com.hotpot.service;

import com.hotpot.dto.LoginDTO;
import com.hotpot.entity.User;
import java.util.Map;

public interface AuthService {
    Map<String, Object> login(LoginDTO dto);
    void register(User user);
    User getCurrentUser();
}
