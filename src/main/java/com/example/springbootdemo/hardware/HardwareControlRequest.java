package com.example.springbootdemo.hardware;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class HardwareControlRequest {

    @NotBlank
    @Pattern(regexp = "\\d{11}", message = "客户ID必须是11位数字手机号")
    private String customerId;

    @Min(0)
    @Max(1)
    private int gender;

    @Min(0)
    @Max(255)
    private int height;

    @Min(0)
    @Max(255)
    private int age;

    @Min(0)
    @Max(255)
    private int weight;

    @Min(1)
    @Max(25)
    private int projectCode;

    @Min(0)
    @Max(255)
    private int projectMinutes;

    @Min(0)
    @Max(2)
    private int sportPerformance;

    @Min(0)
    @Max(255)
    private int usageCount;

    @Min(0)
    @Max(9999)
    private Integer machineNo;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(int projectCode) {
        this.projectCode = projectCode;
    }

    public int getProjectMinutes() {
        return projectMinutes;
    }

    public void setProjectMinutes(int projectMinutes) {
        this.projectMinutes = projectMinutes;
    }

    public int getSportPerformance() {
        return sportPerformance;
    }

    public void setSportPerformance(int sportPerformance) {
        this.sportPerformance = sportPerformance;
    }

    public int getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
    }

    public Integer getMachineNo() {
        return machineNo;
    }

    public void setMachineNo(Integer machineNo) {
        this.machineNo = machineNo;
    }
}
