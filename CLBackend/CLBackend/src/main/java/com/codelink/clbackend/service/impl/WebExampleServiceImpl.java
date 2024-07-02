package com.codelink.clbackend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codelink.clbackend.model.domain.WebExample;
import com.codelink.clbackend.service.WebExampleService;
import com.codelink.clbackend.mapper.WebExampleMapper;
import jakarta.annotation.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author yuexiabug
* @description 针对表【web_example(网站样例表)】的数据库操作Service实现
* @createDate 2024-03-08 11:51:07
*/
@Service
public class WebExampleServiceImpl extends ServiceImpl<WebExampleMapper, WebExample> implements WebExampleService{
    @Resource
    private WebExampleMapper webExampleMapper;

    @Override
    public List<WebExample> getWebExamples() {
        return webExampleMapper.getWebExamples();
    }

    @Override
    public WebExample getWebExampleInfo(long wid) {
        // 1、非空校验
        if (wid <= 0) {
            return null;
        }
        // 2、查询网站样例详情
        QueryWrapper<WebExample> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("wid", wid);
        WebExample webExample = webExampleMapper.selectOne(queryWrapper);
        if (queryWrapper == null) {
            return null;
        }
        // 3、返回网站样例详情
        WebExample webExampleInfo = new WebExample();
        webExampleInfo.setWid(webExample.getWid());
        webExampleInfo.setWebname(webExample.getWebname());
        webExampleInfo.setWebDesc(webExample.getWebDesc());
        webExampleInfo.setWebSite(webExample.getWebSite());
        webExampleInfo.setWebPic(webExample.getWebPic());
        webExampleInfo.setWebDownloadSite(webExample.getWebDownloadSite());
        return webExampleInfo;
    }
}




