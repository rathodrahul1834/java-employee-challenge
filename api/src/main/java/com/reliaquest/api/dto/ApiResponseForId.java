package com.reliaquest.api.dto;

import com.reliaquest.api.model.Employee;

public class ApiResponseForId {
    private Employee data;
    private String status;

    // Getters and Setters
    public Employee getData() {
        return data;
    }

    public void setData(Employee data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
