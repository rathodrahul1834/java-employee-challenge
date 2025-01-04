package com.reliaquest.api.dto;

import com.reliaquest.api.model.Employee;
import java.util.List;

public class ApiResponse {
    private List<Employee> data;
    private String status;

    public ApiResponse(List<Employee> mockEmployees) {
        this.data = mockEmployees;
    }

    public ApiResponse() {

    }

    // Getters and Setters
    public List<Employee> getData() {
        return data;
    }

    public void setData(List<Employee> data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
