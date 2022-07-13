package com.example.splitwise;

public class ModelGroup {

    String groupID, groupName, createdBy,orderTime;

    public ModelGroup() {
    }

    public ModelGroup(String groupID, String groupName, String createdBy, String orderTime) {
        this.groupID = groupID;
        this.groupName = groupName;
        this.createdBy = createdBy;
        this.orderTime = orderTime;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }
}
