package com.example.springbootdemo.common;

public record ApiResponse<T>(
        String code,
        String msg,
        T data
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("0", "success", data);
    }

    public static <T> ApiResponse<T> fail(String code, String msg) {
        return new ApiResponse<>(code, msg, null);
    }
}
