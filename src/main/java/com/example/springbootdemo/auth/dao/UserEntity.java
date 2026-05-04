package com.example.springbootdemo.auth.dao;

public class UserEntity {
    private Long id;
    private String name;
    private String phone;
    private String password;
    private Integer role;
    private Integer status;
    private Integer remainingUseCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getRemainingUseCount() {
        return remainingUseCount;
    }

    public void setRemainingUseCount(Integer remainingUseCount) {
        this.remainingUseCount = remainingUseCount;
    }
}
