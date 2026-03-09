package com.lingxi.isi.application.dto;

import lombok.Data;

/**
 * 简历详情 DTO
 */
@Data
public class ResumeDTO {

    private Long id;
    private String title;
    private String fileName;
    private String fileType;
    private Integer status;
    private String contentText;
    private String structuredData;
    private String skills;
    private Integer experienceYears;
    private String education;
    private String major;
    private String expectedPosition;
    private String expectedSalary;
    private Double aiScore;
}
