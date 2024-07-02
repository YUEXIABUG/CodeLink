package com.codelink.clbackend.mapper;

import com.codelink.clbackend.model.domain.WebExample;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
* @author yuexiabug
* @description 针对表【web_example(网站样例表)】的数据库操作Mapper
* @createDate 2024-03-08 11:51:07
* @Entity generator.domain.WebExample
*/
@Mapper
@Repository
public interface WebExampleMapper extends BaseMapper<WebExample> {
    @Select("select * from web_example")
    public List<WebExample> getWebExamples();
}




