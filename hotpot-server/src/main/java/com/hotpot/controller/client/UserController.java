package com.hotpot.controller.client;

import com.hotpot.common.Result;
import com.hotpot.entity.*;
import com.hotpot.mapper.*;
import com.hotpot.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/client/user")
@RequiredArgsConstructor
public class UserController {
    private final UserMapper userMapper;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final RechargeMapper rechargeMapper;
    private final FeedbackMapper feedbackMapper;
    private final UserFavoriteMapper favoriteMapper;

    @GetMapping("/info")
    public Result<?> info() {
        return Result.success(authService.getCurrentUser());
    }

    @PutMapping("/update")
    public Result<?> update(@RequestBody User user) {
        user.setId(authService.getCurrentUser().getId());
        userMapper.update(user);
        return Result.success("更新成功");
    }

    @PutMapping("/password")
    public Result<?> password(@RequestBody Map<String, String> params) {
        var user = authService.getCurrentUser();
        if (!passwordEncoder.matches(params.get("oldPassword"), user.getPassword())) {
            return Result.error("原密码错误");
        }
        userMapper.updatePassword(user.getId(), passwordEncoder.encode(params.get("newPassword")));
        return Result.success("密码修改成功");
    }

    @PostMapping("/recharge")
    public Result<?> recharge(@RequestBody Map<String, Object> params) {
        var user = authService.getCurrentUser();
        Integer amount = Integer.valueOf(params.get("amount").toString());
        Integer payMethod = Integer.valueOf(params.get("payMethod").toString());
        String tradeNo = "R" + System.currentTimeMillis();
        RechargeRecord record = new RechargeRecord();
        record.setUserId(user.getId()); record.setAmount(amount);
        record.setPayMethod(payMethod); record.setStatus(1); record.setTradeNo(tradeNo);
        rechargeMapper.insert(record);
        userMapper.addBalance(user.getId(), amount);
        return Result.success("充值成功");
    }

    @GetMapping("/recharge-records")
    public Result<?> rechargeRecords() {
        var user = authService.getCurrentUser();
        return Result.success(rechargeMapper.findByUser(user.getId()));
    }

    @PostMapping("/feedback")
    public Result<?> feedback(@RequestBody Feedback feedback) {
        feedback.setUserId(authService.getCurrentUser().getId());
        feedbackMapper.insert(feedback);
        return Result.success("提交成功");
    }

    @GetMapping("/feedback")
    public Result<?> myFeedback() {
        var user = authService.getCurrentUser();
        return Result.success(feedbackMapper.findByUser(user.getId()));
    }

    // Favorites
    @GetMapping("/favorites")
    public Result<?> favorites() {
        var user = authService.getCurrentUser();
        return Result.success(favoriteMapper.findByUser(user.getId()));
    }

    @PostMapping("/favorite/{dishId}")
    public Result<?> addFavorite(@PathVariable Long dishId) {
        var user = authService.getCurrentUser();
        UserFavorite fav = new UserFavorite();
        fav.setUserId(user.getId()); fav.setDishId(dishId);
        favoriteMapper.insert(fav);
        return Result.success("已收藏");
    }

    @DeleteMapping("/favorite/{dishId}")
    public Result<?> removeFavorite(@PathVariable Long dishId) {
        var user = authService.getCurrentUser();
        favoriteMapper.delete(user.getId(), dishId);
        return Result.success("已取消收藏");
    }
}
