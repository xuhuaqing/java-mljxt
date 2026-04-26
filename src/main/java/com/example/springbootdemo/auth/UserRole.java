package com.example.springbootdemo.auth;

public enum UserRole {
    USER(1, "用户"),
    TEACHER(2, "老师"),
    MERCHANT(3, "商家"),
    DEVELOPER(4, "开发");

    private final int code;
    private final String label;

    UserRole(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public static String labelOf(int code) {
        for (UserRole role : values()) {
            if (role.code == code) {
                return role.label;
            }
        }
        throw new IllegalArgumentException("身份类型无效，必须是1-4");
    }
}
