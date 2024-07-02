package com.codelink.clbackend.controller;

import com.codelink.clbackend.common.BaseResponse;
import com.codelink.clbackend.model.domain.WebExample;
import com.codelink.clbackend.model.domain.request.*;
import com.codelink.clbackend.service.WebExampleService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/webExample")
public class WebExampleController {

    @Resource
    private WebExampleService webExampleService;

    @PostMapping("/getWebExamples")
    public BaseResponse<List<WebExample>> getWebExamples() {
        List<WebExample> webExamples = webExampleService.getWebExamples();
        if (webExamples == null) {
            return new BaseResponse<>(400, null, "获取网站样例失败");
        }

        return new BaseResponse<>(200, webExamples, "获取网站样例成功");
    }

    @PostMapping("/getWebExampleInfo")
    public BaseResponse<GetWebExampleInfoRequest> getWebExampleInfo(@RequestBody GetWebExampleInfoRequest getWebExampleInfoRequest) {
        // 1、非空校验
        if (getWebExampleInfoRequest == null || getWebExampleInfoRequest.getWid() <= 0) {
            return new BaseResponse<>(500, null, "请求网站详情为空");
        }
        // 2、查询网站样例详情
        long wid = getWebExampleInfoRequest.getWid();
        WebExample webExampleInfo = webExampleService.getWebExampleInfo(wid);
        if (webExampleInfo == null) {
            return new BaseResponse<>(400, null, "获取网站样例详情失败");
        }
        // 3、返回网站样例详情
        GetWebExampleInfoRequest getWebExampleInfoResponse = new GetWebExampleInfoRequest();
        getWebExampleInfoResponse.setWid(webExampleInfo.getWid());
        getWebExampleInfoResponse.setWebname(webExampleInfo.getWebname());
        getWebExampleInfoResponse.setWebDesc(webExampleInfo.getWebDesc());
        getWebExampleInfoResponse.setWebSite(webExampleInfo.getWebSite());
        getWebExampleInfoResponse.setWebPic(webExampleInfo.getWebPic());
        getWebExampleInfoResponse.setWebDownloadSite(webExampleInfo.getWebDownloadSite());
        return new BaseResponse<>(200, getWebExampleInfoResponse, "获取网站样例详情成功");
    }
}
