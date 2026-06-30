package com.hotpot.controller.admin;

import com.hotpot.common.Result;
import com.hotpot.entity.CookingGuide;
import com.hotpot.entity.HotpotBase;
import com.hotpot.entity.HotpotType;
import com.hotpot.mapper.GuideMapper;
import com.hotpot.service.HotpotBaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/guide")
@RequiredArgsConstructor
public class GuideController {
    private final GuideMapper guideMapper;
    private final HotpotBaseService baseService;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(required = false) String type) {
        if (type != null && !type.isEmpty()) return Result.success(guideMapper.findByType(type));
        return Result.success(guideMapper.findAll());
    }

    @GetMapping("/detail/{id}")
    public Result<?> detail(@PathVariable Long id) { return Result.success(guideMapper.findById(id)); }

    @PostMapping("/save")
    public Result<?> save(@RequestBody CookingGuide guide) {
        if (guide.getId() == null) guideMapper.insert(guide);
        else guideMapper.update(guide);
        return Result.success("保存成功");
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        guideMapper.delete(id);
        return Result.success("删除成功");
    }

    // Hotpot type management
    @GetMapping("/types")
    public Result<?> types() { return Result.success(baseService.getTypes()); }

    @PostMapping("/type/save")
    public Result<?> saveType(@RequestBody HotpotType type) {
        baseService.saveType(type);
        return Result.success("保存成功");
    }

    @DeleteMapping("/type/delete/{id}")
    public Result<?> deleteType(@PathVariable Long id) {
        baseService.deleteType(id);
        return Result.success("删除成功");
    }

    // Hotpot base management
    @GetMapping("/bases")
    public Result<?> bases() { return Result.success(baseService.adminList()); }

    @PostMapping("/base/save")
    public Result<?> saveBase(@RequestBody HotpotBase base) {
        baseService.saveBase(base);
        return Result.success("保存成功");
    }

    @DeleteMapping("/base/delete/{id}")
    public Result<?> deleteBase(@PathVariable Long id) {
        baseService.deleteBase(id);
        return Result.success("删除成功");
    }
}
