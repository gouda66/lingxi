package com.lingxi.isi.controller;

import com.lingxi.isi.common.result.R;
import com.lingxi.isi.service.ISysDictDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController {

    private final ISysDictDataService dictDataService;

    public SysDictDataController(ISysDictDataService dictDataService) {
        this.dictDataService = dictDataService;
    }

    @GetMapping("/type/{dictType}")
    public R getDictData(@PathVariable String dictType) {
        return dictDataService.getDictDataByType(dictType);
    }
}
