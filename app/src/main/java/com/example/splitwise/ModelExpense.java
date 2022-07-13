package com.example.splitwise;

public class ModelExpense {

    String Uid,Amount,Description,By,expense_id,Time;

    public ModelExpense() {
    }

    public ModelExpense(String uid, String amount, String description, String by, String expense_id, String time) {
        Uid = uid;
        Amount = amount;
        Description = description;
        By = by;
        this.expense_id = expense_id;
        Time = time;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getBy() {
        return By;
    }

    public void setBy(String by) {
        By = by;
    }

    public String getExpense_id() {
        return expense_id;
    }

    public void setExpense_id(String expense_id) {
        this.expense_id = expense_id;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }
}
