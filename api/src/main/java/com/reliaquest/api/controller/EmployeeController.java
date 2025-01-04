package com.reliaquest.api.controller;

import com.reliaquest.api.model.CreateMockEmployeeInput;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.service.EmployeeService;
import com.reliaquest.api.exception.EmployeeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/employee")
public class EmployeeController implements IEmployeeController<Employee, CreateMockEmployeeInput> {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private EmployeeService employeeService;

    @Override
    @GetMapping()
    public ResponseEntity<List<Employee>> getAllEmployees() {
        try {
            logger.info("Fetching all employees.");
            List<Employee> employees = employeeService.getAllEmployees();
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            logger.error("Error fetching all employees", e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @Override
    @GetMapping("/highestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        try {
            logger.info("Fetching highest salary.");
            int highestSalary = employeeService.getHighestSalaryOfEmployees();
            return ResponseEntity.ok(highestSalary);
        } catch (Exception e) {
            logger.error("Error fetching highest salary", e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @Override
    @GetMapping("/topTenHighestEarningEmployeeNames")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        try {
            logger.info("Fetching top 10 highest earning employee names.");
            List<String> topEmployees = employeeService.getTop10HighestEarningEmployeeNames();
            return ResponseEntity.ok(topEmployees);
        } catch (Exception e) {
            logger.error("Error fetching top 10 highest earning employees", e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @Override
    @GetMapping("/search/{searchString}")
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String searchString) {
        try {
            logger.info("Searching employees by name fragment: {}", searchString);
            List<Employee> employees = employeeService.getEmployeesByNameSearch(searchString);
            return ResponseEntity.ok(employees);
        } catch (Exception e) {
            logger.error("Error searching employees by name", e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
        try {
            logger.info("Fetching employee with ID: {}", id);
            if (!isValidUUID(id)) {
                return ResponseEntity.badRequest().body(null);
            }
            Employee employee = employeeService.getEmployeeById(id);
            return ResponseEntity.ok(employee);
        } catch (EmployeeNotFoundException e) {
            logger.error("Employee not found with ID: {}", id, e);
            return ResponseEntity.status(404).body(null);
        } catch (Exception e) {
            logger.error("Error fetching employee by ID", e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @Override
    @PostMapping()
    public ResponseEntity<Employee> createEmployee(@RequestBody CreateMockEmployeeInput employeeInput) {
        try {
            logger.info("Creating new employee: {}", employeeInput);
            Employee createdEmployee = employeeService.createEmployee(employeeInput);
            return ResponseEntity.status(201).body(createdEmployee);
        } catch (Exception e) {
            logger.error("Error creating employee", e);
            return ResponseEntity.status(500).body(null);
        }
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable String id) {
        try {
            logger.info("Deleting employee with ID: {}", id);
            if (!isValidUUID(id)) {
                return ResponseEntity.badRequest().body("Invalid UUID format: " + id);
            }
            String result = employeeService.deleteEmployeeById(id);
            return ResponseEntity.ok(result);
        } catch (EmployeeNotFoundException e) {
            logger.error("Employee not found with ID: {}", id, e);
            return ResponseEntity.status(404).body("Employee not found with ID: " + id);
        } catch (Exception e) {
            logger.error("Error deleting employee", e);
            return ResponseEntity.status(500).body("Failed to delete employee");
        }
    }

    private boolean isValidUUID(String id) {
        try {
            UUID.fromString(id);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
