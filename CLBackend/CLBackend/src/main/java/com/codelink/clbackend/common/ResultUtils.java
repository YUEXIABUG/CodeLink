package com.codelink.clbackend.common;

/**
 * 返回工具类
 */
public class ResultUtils {
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(200, data, "success");
    }
}
