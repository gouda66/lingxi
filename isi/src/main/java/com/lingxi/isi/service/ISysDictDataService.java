package com.lingxi.isi.service;

import com.lingxi.isi.common.result.R;

import java.util.List;
import java.util.Map;

public interface ISysDictDataService {

    /**
     * 根据字典类型获取字典数据
     */
    R<List<Map<String, Object>>> getDictDataByType(String dictType);
}
