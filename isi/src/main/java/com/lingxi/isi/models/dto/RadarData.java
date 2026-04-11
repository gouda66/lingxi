package com.lingxi.isi.models.dto;

import lombok.Data;

import java.util.Arrays;
import java.util.List;

/**
 * 雷达图数据 DTO
 */
@Data
public class RadarData {
    private List<String> indicators = Arrays.asList(
            "专业准确性", "逻辑清晰度", "知识深度",
            "表达流畅度", "项目结合度", "拓展思考力"
    );
    private List<Integer> values;
}
