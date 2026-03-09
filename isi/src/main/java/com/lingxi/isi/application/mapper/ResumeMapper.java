package com.lingxi.isi.application.mapper;

import com.lingxi.isi.application.command.CreateResumeCommand;
import com.lingxi.isi.application.dto.ResumeDTO;
import com.lingxi.isi.domain.model.entity.Resume;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

/**
 * 简历 Mapper
 * 用于 DTO、Entity、Command 之间的转换
 */
@Mapper
public interface ResumeMapper {

    ResumeMapper INSTANCE = Mappers.getMapper(ResumeMapper.class);

    /**
     * Command 转 Entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "0")
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    Resume toEntity(CreateResumeCommand command);

    /**
     * Entity 转 DTO
     */
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "fileName", source = "fileName")
    @Mapping(target = "fileType", source = "fileType")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "contentText", source = "contentText")
    @Mapping(target = "structuredData", source = "structuredData")
    @Mapping(target = "skills", source = "skills")
    @Mapping(target = "experienceYears", source = "experienceYears")
    @Mapping(target = "education", source = "education")
    @Mapping(target = "major", source = "major")
    @Mapping(target = "expectedPosition", source = "expectedPosition")
    @Mapping(target = "expectedSalary", source = "expectedSalary")
    @Mapping(target = "aiScore", source = "aiScore")
    ResumeDTO toDTO(Resume resume);

    /**
     * DTO 转 Entity
     */
    Resume toEntity(ResumeDTO dto);
}
