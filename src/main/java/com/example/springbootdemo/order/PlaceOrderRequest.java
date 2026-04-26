package com.example.springbootdemo.order;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class PlaceOrderRequest {

    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "\\d{11}", message = "手机号必须是11位数字")
    private String phone;

    @NotNull(message = "性别不能为空")
    @Min(value = 0, message = "性别只能是0或1")
    @Max(value = 1, message = "性别只能是0或1")
    private Integer gender;

    @NotNull(message = "年龄不能为空")
    @Min(value = 1, message = "年龄范围为1-120")
    @Max(value = 120, message = "年龄范围为1-120")
    private Integer age;

    @NotNull(message = "身高不能为空")
    @Min(value = 50, message = "身高范围为50-250")
    @Max(value = 250, message = "身高范围为50-250")
    private Integer height;

    @NotNull(message = "体重不能为空")
    @Min(value = 20, message = "体重范围为20-300")
    @Max(value = 300, message = "体重范围为20-300")
    private Integer weight;

    @NotNull(message = "运动表现不能为空")
    @Min(value = 0, message = "运动表现只能是0/1/2")
    @Max(value = 2, message = "运动表现只能是0/1/2")
    private Integer sportPerformance;

    @NotBlank(message = "项目名称不能为空")
    private String projectName;

    @NotNull(message = "项目时长不能为空")
    @Min(value = 1, message = "项目时长必须大于0")
    @Max(value = 255, message = "项目时长不能超过255")
    private Integer projectDuration;

    @NotNull(message = "商家ID不能为空")
    private Long merchantId;

    private Long deviceId;

    @NotNull(message = "使用次数不能为空")
    @Min(value = 1, message = "使用次数范围为1-255")
    @Max(value = 255, message = "使用次数范围为1-255")
    private Integer usageCount;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getSportPerformance() {
        return sportPerformance;
    }

    public void setSportPerformance(Integer sportPerformance) {
        this.sportPerformance = sportPerformance;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Integer getProjectDuration() {
        return projectDuration;
    }

    public void setProjectDuration(Integer projectDuration) {
        this.projectDuration = projectDuration;
    }

    public Long getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(Long merchantId) {
        this.merchantId = merchantId;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Integer getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
    }
}
