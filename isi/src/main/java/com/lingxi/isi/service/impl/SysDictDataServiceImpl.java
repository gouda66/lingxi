package com.lingxi.isi.service.impl;

import com.lingxi.isi.common.result.R;
import com.lingxi.isi.service.ISysDictDataService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class SysDictDataServiceImpl implements ISysDictDataService {

    @Override
    public R<List<Map<String, Object>>> getDictDataByType(String dictType) {
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
