package com.example.springbootdemo.auth;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AdminUserUpsertRequest {

    @NotBlank(message = "姓名不能为空")
    private String name;

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "\\d{11}", message = "手机号必须是11位数字")
    private String phone;

    @NotBlank(message = "密码不能为空")
    private String password;

    @Min(value = 1, message = "角色只能是1-4")
    @Max(value = 4, message = "角色只能是1-4")
    private Integer role;

    private Integer remainingUseCount;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public Integer getRemainingUseCount() {
        return remainingUseCount;
    }

    public void setRemainingUseCount(Integer remainingUseCount) {
        this.remainingUseCount = remainingUseCount;
    }
}
