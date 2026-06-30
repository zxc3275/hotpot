package com.hotpot.controller.admin;

import com.hotpot.common.Result;
import com.hotpot.entity.*;
import com.hotpot.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/system")
@RequiredArgsConstructor
public class SystemController {
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final DictMapper dictMapper;
    private final CarouselMapper carouselMapper;
    private final FeedbackMapper feedbackMapper;

    // User management
    @GetMapping("/users")
    public Result<?> users() { return Result.success(userMapper.findAll()); }

    @GetMapping("/user/{id}")
    public Result<?> userDetail(@PathVariable Long id) { return Result.success(userMapper.findById(id)); }

    @PutMapping("/user/status/{id}")
    public Result<?> userStatus(@PathVariable Long id, @RequestBody User user) {
        return Result.success();
    }

    // Role management
    @GetMapping("/roles")
    public Result<?> roles() { return Result.success(roleMapper.findAll()); }

    @PostMapping("/role/save")
    public Result<?> saveRole(@RequestBody Role role) {
        if (role.getId() == null) roleMapper.insert(role);
        else roleMapper.update(role);
        return Result.success("保存成功");
    }

    @DeleteMapping("/role/delete/{id}")
    public Result<?> deleteRole(@PathVariable Long id) {
        roleMapper.delete(id);
        return Result.success("删除成功");
    }

    // Menu management
    @GetMapping("/menus")
    public Result<?> menus() { return Result.success(roleMapper.findAllMenus()); }

    @PostMapping("/menu/save")
    public Result<?> saveMenu(@RequestBody Menu menu) {
        if (menu.getId() == null) roleMapper.insertMenu(menu);
        else roleMapper.updateMenu(menu);
        return Result.success("保存成功");
    }

    @DeleteMapping("/menu/delete/{id}")
    public Result<?> deleteMenu(@PathVariable Long id) {
        roleMapper.deleteMenu(id);
        return Result.success("删除成功");
    }

    // Dict management
    @GetMapping("/dicts")
    public Result<?> dicts() { return Result.success(dictMapper.findAll()); }

    @GetMapping("/dict/{type}")
    public Result<?> dictByType(@PathVariable String type) { return Result.success(dictMapper.findByType(type)); }

    @PostMapping("/dict/save")
    public Result<?> saveDict(@RequestBody Dict dict) {
        if (dict.getId() == null) dictMapper.insert(dict);
        else dictMapper.update(dict);
        return Result.success("保存成功");
    }

    @DeleteMapping("/dict/delete/{id}")
    public Result<?> deleteDict(@PathVariable Long id) {
        dictMapper.delete(id);
        return Result.success("删除成功");
    }

    // Carousel management
    @GetMapping("/carousels")
    public Result<?> carousels() { return Result.success(carouselMapper.findAll()); }

    @PostMapping("/carousel/save")
    public Result<?> saveCarousel(@RequestBody Carousel carousel) {
        if (carousel.getId() == null) carouselMapper.insert(carousel);
        else carouselMapper.update(carousel);
        return Result.success("保存成功");
    }

    @DeleteMapping("/carousel/delete/{id}")
    public Result<?> deleteCarousel(@PathVariable Long id) {
        carouselMapper.delete(id);
        return Result.success("删除成功");
    }

    // Feedback management
    @GetMapping("/feedbacks")
    public Result<?> feedbacks() { return Result.success(feedbackMapper.findAll()); }

    @PutMapping("/feedback/reply/{id}")
    public Result<?> replyFeedback(@PathVariable Long id, @RequestBody Feedback feedback) {
        feedbackMapper.reply(id, feedback.getReply());
        return Result.success("回复成功");
    }
}
