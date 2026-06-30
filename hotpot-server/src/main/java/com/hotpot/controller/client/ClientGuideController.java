package com.hotpot.controller.client;

import com.hotpot.common.Result;
import com.hotpot.mapper.GuideMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client/guide")
@RequiredArgsConstructor
public class ClientGuideController {
    private final GuideMapper guideMapper;

    @GetMapping("/list")
    public Result<?> list(@RequestParam(required = false) String type) {
        if (type != null && !type.isEmpty()) {
            return Result.success(guideMapper.findByType(type));
        }
        return Result.success(guideMapper.findAll());
    }

    @GetMapping("/detail/{id}")
    public Result<?> detail(@PathVariable Long id) {
        return Result.success(guideMapper.findById(id));
    }
}
