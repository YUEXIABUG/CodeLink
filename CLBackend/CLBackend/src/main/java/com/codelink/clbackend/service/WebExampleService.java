package com.codelink.clbackend.service;

import com.codelink.clbackend.model.domain.WebExample;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
* @author yuexiabug
* @description 针对表【web_example(网站样例表)】的数据库操作Service
* @createDate 2024-03-08 11:51:07
*/
public interface WebExampleService extends IService<WebExample> {

    /**
     * 获取所有网站
     */
    List<WebExample> getWebExamples();

    /**
     * 获取网站样例详情
     * @param wid               网站样例id
     * @return 网站样例
     */
    WebExample getWebExampleInfo(long wid);
}
