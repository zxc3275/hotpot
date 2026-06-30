package com.hotpot.controller.client;

import com.hotpot.common.Result;
import com.hotpot.service.HotpotBaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client/base")
@RequiredArgsConstructor
public class BaseController {
    private final HotpotBaseService baseService;

    @GetMapping("/types")
    public Result<?> types() {
        return Result.success(baseService.getTypes());
    }

    @GetMapping("/list")
    public Result<?> list(@RequestParam(required = false) Long typeId) {
        return Result.success(baseService.getBases(typeId));
    }

    @GetMapping("/detail/{id}")
    public Result<?> detail(@PathVariable Long id) {
        return Result.success(baseService.getBaseDetail(id));
    }
}
