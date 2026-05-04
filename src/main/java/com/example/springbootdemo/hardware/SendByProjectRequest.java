package com.example.springbootdemo.hardware;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 点击某个项目名称时提交：至少包含项目名称与 11 位客户手机号，其余与协议一致字段可选。
 */
public class SendByProjectRequest {

    /**
     * 项目名称（必须与后端项目字典中的名称完全一致，如：腰骶温养呵护）。
     */
    @NotBlank(message = "项目名称不能为空")
    private String projectName;

    /**
     * 客户ID，使用 11 位手机号（协议中的客户ID字段）。
     */
    @NotBlank
    @Pattern(regexp = "\\d{11}", message = "客户ID必须是11位数字手机号")
    private String customerId;

    /**
     * 性别：0=男，1=女（会在协议中编码为 0x30/0x31）。
     */
    @Min(0)
    @Max(1)
    private Integer gender;

    /**
     * 身高（单位：cm），1字节范围 0~255。
     */
    @Min(0)
    @Max(255)
    private Integer height;

    /**
     * 年龄（单位：岁），1字节范围 0~255。
     */
    @Min(0)
    @Max(255)
    private Integer age;

    /**
     * 体重（单位：kg），1字节范围 0~255。
     */
    @Min(0)
    @Max(255)
    private Integer weight;

    /**
     * 项目时长（单位：分钟），1字节范围 0~255。
     */
    @Min(0)
    @Max(255)
    private Integer projectMinutes;

    /**
     * 运动表现：0=经常运动，1=偶尔运动，2=从未运动（协议编码 0x30/0x31/0x32）。
     */
    @Min(0)
    @Max(2)
    private Integer sportPerformance;

    /**
     * 客户使用次数（单位：次），当前与数据库字段范围对齐为 0~127。
     */
    @Min(0)
    @Max(127)
    private Integer usageCount;

    /**
     * 机器编号（0~9999），发布主题将自动拼接为 xxxx/0000~9999。
     */
    @Min(0)
    @Max(9999)
    private Integer machineNo;

    public static HardwareControlRequest toControlRequest(SendByProjectRequest in, int projectCode) {
        HardwareControlRequest r = new HardwareControlRequest();
        r.setCustomerId(in.getCustomerId());
        r.setGender(in.getGender() != null ? in.getGender() : 0);
        r.setHeight(in.getHeight() != null ? in.getHeight() : 170);
        r.setAge(in.getAge() != null ? in.getAge() : 28);
        r.setWeight(in.getWeight() != null ? in.getWeight() : 60);
        r.setProjectCode(projectCode);
        r.setProjectMinutes(in.getProjectMinutes() != null ? in.getProjectMinutes() : 40);
        r.setSportPerformance(in.getSportPerformance() != null ? in.getSportPerformance() : 0);
        r.setUsageCount(in.getUsageCount() != null ? in.getUsageCount() : 1);
        return r;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getProjectMinutes() {
        return projectMinutes;
    }

    public void setProjectMinutes(Integer projectMinutes) {
        this.projectMinutes = projectMinutes;
    }

    public Integer getSportPerformance() {
        return sportPerformance;
    }

    public void setSportPerformance(Integer sportPerformance) {
        this.sportPerformance = sportPerformance;
    }

    public Integer getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
    }

    public Integer getMachineNo() {
        return machineNo;
    }

    public void setMachineNo(Integer machineNo) {
        this.machineNo = machineNo;
    }
}
