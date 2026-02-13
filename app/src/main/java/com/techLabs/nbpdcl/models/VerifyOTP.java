package com.techLabs.nbpdcl.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VerifyOTP {

    @SerializedName("refresh")
    @Expose
    private String refresh;
    @SerializedName("access")
    @Expose
    private String access;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("usertype")
    @Expose
    private String usertype;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Username")
    @Expose
    private String username;
    @SerializedName("permissions")
    @Expose
    private List<Object> permissions;
    @SerializedName("region")
    @Expose
    private String region;
    @SerializedName("zone")
    @Expose
    private String zone;
    @SerializedName("circle")
    @Expose
    private String circle;
    @SerializedName("designation")
    @Expose
    private String designation;
    @SerializedName("date_joined")
    @Expose
    private String dateJoined;
    @SerializedName("place_of_work")
    @Expose
    private String placeOfWork;
    @SerializedName("loginTime")
    @Expose
    private String loginTime;
    @SerializedName("logoutTime")
    @Expose
    private String logoutTime;

    public String getRefresh() {
        return refresh;
    }

    public void setRefresh(String refresh) {
        this.refresh = refresh;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Object> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Object> permissions) {
        this.permissions = permissions;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getCircle() {
        return circle;
    }

    public void setCircle(String circle) {
        this.circle = circle;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDateJoined() {
        return dateJoined;
    }

    public void setDateJoined(String dateJoined) {
        this.dateJoined = dateJoined;
    }

    public String getPlaceOfWork() {
        return placeOfWork;
    }

    public void setPlaceOfWork(String placeOfWork) {
        this.placeOfWork = placeOfWork;
    }

    public String getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(String placeOfWork) {
        this.loginTime = placeOfWork;
    }

    public String getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(String placeOfWork) {
        this.logoutTime = placeOfWork;
    }

}