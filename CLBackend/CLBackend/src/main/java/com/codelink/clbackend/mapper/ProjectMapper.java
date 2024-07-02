package com.codelink.clbackend.mapper;

import com.codelink.clbackend.model.domain.Project;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
* @author yuexiabug
* @description 针对表【project(项目表)】的数据库操作Mapper
* @createDate 2024-03-26 10:39:21
* @Entity generator.domain.Project
*/
public interface ProjectMapper extends BaseMapper<Project> {

    List<Project> getProjectList(long uid);
}




