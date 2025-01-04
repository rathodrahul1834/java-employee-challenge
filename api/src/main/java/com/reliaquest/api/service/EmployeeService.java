package com.reliaquest.api.service;

import com.reliaquest.api.dto.ApiResponse;
import com.reliaquest.api.dto.ApiResponseForId;
import com.reliaquest.api.model.CreateMockEmployeeInput;
import com.reliaquest.api.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    private static final String BASE_URL = "http://localhost:8112/api/v1/employee";

    @Autowired
    private RestTemplate restTemplate;

    public List<Employee> getAllEmployees() {
        logger.info("Fetching all employees from the API.");
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(BASE_URL, ApiResponse.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            logger.info("Successfully fetched employees.");
            return response.getBody().getData();
        } else {
            logger.error("Failed to fetch employees: {}", response.getStatusCode());
            throw new RuntimeException("Failed to fetch employees: " + response.getStatusCode());
        }
    }

    public List<Employee> getEmployeesByNameSearch(String nameFragment) {
        List<Employee> allEmployees = getAllEmployees();
        return allEmployees.stream()
                .filter(emp -> emp.getEmployeeName().toLowerCase().contains(nameFragment.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Employee getEmployeeById(String id) {
        String url = BASE_URL + "/" + id;
        ResponseEntity<ApiResponseForId> response = restTemplate.getForEntity(url, ApiResponseForId.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return (Employee) response.getBody().getData();
        } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
            throw new RuntimeException("Employee not found with ID: " + id);
        } else {
            throw new RuntimeException("Error fetching employee by ID: " + response.getStatusCode());
        }
    }

    public int getHighestSalaryOfEmployees() {
        List<Employee> allEmployees = getAllEmployees();
        return allEmployees.stream()
                .mapToInt(Employee::getEmployeeSalary)
                .max()
                .orElseThrow(() -> new RuntimeException("No employees found."));
    }

    public List<String> getTop10HighestEarningEmployeeNames() {
        List<Employee> allEmployees = getAllEmployees();
        return allEmployees.stream()
                .sorted(Comparator.comparingInt(Employee::getEmployeeSalary).reversed())
                .limit(10)
                .map(Employee::getEmployeeName)
                .collect(Collectors.toList());
    }

    public Employee createEmployee(CreateMockEmployeeInput employee) {
        logger.info("Creating employee: {}", employee);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateMockEmployeeInput> entity = new HttpEntity<>(employee, headers);

        ResponseEntity<ApiResponseForId> response = restTemplate.exchange(BASE_URL, HttpMethod.POST, entity, ApiResponseForId.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody().getData();
        } else {
            throw new RuntimeException("Failed to create employee: " + response.getStatusCode());
        }
    }

    public String deleteEmployeeById(String id) {

        //Delete is disabled from the client side hence not able to complete the operation and could not test it.
        //postman request fails with error - {
        //    "status": "Failed to process request.",
        //    "error": "Request method 'DELETE' is not supported"
        //}

        String url = BASE_URL + "/" + id;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateMockEmployeeInput> entity = new HttpEntity<>(headers);

        ResponseEntity<ApiResponseForId> response = restTemplate.exchange(
                url, // URI with path parameter
                HttpMethod.DELETE, // Method type
                entity, // Entity with headers
                ApiResponseForId.class, // Response type
                id // Path variable for {name}
        );
        if (response.getStatusCode() == HttpStatus.OK) {
            return "Employee with ID " + id + " has been deleted.";
        } else {
            throw new RuntimeException("Failed to delete employee: " + response.getStatusCode());
        }
    }
}
