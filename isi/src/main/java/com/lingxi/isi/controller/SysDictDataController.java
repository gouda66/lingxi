package com.lingxi.isi.controller;

import com.lingxi.isi.common.result.R;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system/dict/data")
public class SysDictDataController {

    @GetMapping("/type/{dictType}")
    public R list(@PathVariable String dictType) {
        List<Map<String, Object>> data;
        
        if ("sys_normal_disable".equals(dictType)) {
            data = Arrays.asList(
                Map.of(
                    "dictLabel", "正常",
                    "dictValue", "0",
                    "dictSort", 1,
                    "cssClass", "",
                    "listClass", "success"
                ),
                Map.of(
                    "dictLabel", "禁用",
                    "dictValue", "1",
                    "dictSort", 2,
                    "cssClass", "",
                    "listClass", "danger"
                )
            );
        } else if ("sys_user_sex".equals(dictType)) {
            data = Arrays.asList(
                Map.of(
                    "dictLabel", "男",
                    "dictValue", "1",
                    "dictSort", 1,
                    "cssClass", "",
                    "listClass", ""
                ),
                Map.of(
                    "dictLabel", "女",
                    "dictValue", "2",
                    "dictSort", 2,
                    "cssClass", "",
                    "listClass", ""
                ),
                Map.of(
                    "dictLabel", "未知",
                    "dictValue", "0",
                    "dictSort", 3,
                    "cssClass", "",
                    "listClass", ""
                )
            );
        } else {
            data = Arrays.asList();
        }
        
        return R.success(data);
    }
}
