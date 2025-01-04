package com.reliaquest.api.service;

import com.reliaquest.api.dto.ApiResponse;
import com.reliaquest.api.dto.ApiResponseForId;
import com.reliaquest.api.model.CreateMockEmployeeInput;
import com.reliaquest.api.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmployeeServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EmployeeService employeeService;

    private List<Employee> mockEmployees;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mocking employee data
        Employee emp1 = new Employee("1", "Alice", 90000, 30, "SE", "abc@xyz.com");
        Employee emp2 = new Employee("2", "Bob", 120000, 30, "SE", "abc@xyz.com");
        Employee emp3 = new Employee("3", "Charlie", 80000, 30, "SE", "abc@xyz.com");
        mockEmployees = Arrays.asList(emp1, emp2, emp3);
    }

    @Test
    void testGetAllEmployees() {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setData(mockEmployees);

        ResponseEntity<ApiResponse> responseEntity = new ResponseEntity<>(apiResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(ApiResponse.class))).thenReturn(responseEntity);

        List<Employee> employees = employeeService.getAllEmployees();
        assertEquals(3, employees.size());
        assertEquals("Alice", employees.get(0).getEmployeeName());

        verify(restTemplate, times(1)).getForEntity(anyString(), eq(ApiResponse.class));
    }

    @Test
    void testGetEmployeesByNameSearch() {
        when(restTemplate.getForEntity(anyString(), eq(ApiResponse.class)))
                .thenReturn(new ResponseEntity<>(new ApiResponse(mockEmployees), HttpStatus.OK));

        List<Employee> result = employeeService.getEmployeesByNameSearch("Ali");
        assertEquals(1, result.size());
        assertEquals("Alice", result.get(0).getEmployeeName());
    }

    @Test
    void testGetEmployeeById_Success() {
        ApiResponseForId apiResponseForId = new ApiResponseForId();
        apiResponseForId.setData(mockEmployees.get(0));

        ResponseEntity<ApiResponseForId> responseEntity = new ResponseEntity<>(apiResponseForId, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(ApiResponseForId.class))).thenReturn(responseEntity);

        Employee employee = employeeService.getEmployeeById("1");
        assertEquals("Alice", employee.getEmployeeName());
        assertEquals(90000, employee.getEmployeeSalary());

        verify(restTemplate, times(1)).getForEntity(anyString(), eq(ApiResponseForId.class));
    }

    @Test
    void testGetEmployeeById_NotFound() {
        ResponseEntity<ApiResponseForId> responseEntity = new ResponseEntity<>(HttpStatus.NOT_FOUND);
        when(restTemplate.getForEntity(anyString(), eq(ApiResponseForId.class))).thenReturn(responseEntity);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> employeeService.getEmployeeById("99"));
        assertEquals("Employee not found with ID: 99", exception.getMessage());
    }

    @Test
    void testGetHighestSalaryOfEmployees() {
        when(restTemplate.getForEntity(anyString(), eq(ApiResponse.class)))
                .thenReturn(new ResponseEntity<>(new ApiResponse(mockEmployees), HttpStatus.OK));

        int highestSalary = employeeService.getHighestSalaryOfEmployees();
        assertEquals(120000, highestSalary);
    }

    @Test
    void testGetTop10HighestEarningEmployeeNames() {
        when(restTemplate.getForEntity(anyString(), eq(ApiResponse.class)))
                .thenReturn(new ResponseEntity<>(new ApiResponse(mockEmployees), HttpStatus.OK));

        List<String> topEarners = employeeService.getTop10HighestEarningEmployeeNames();
        assertEquals(3, topEarners.size());
        assertEquals("Bob", topEarners.get(0));
        assertEquals("Alice", topEarners.get(1));
    }

    @Test
    void testCreateEmployee() {
        CreateMockEmployeeInput input = new CreateMockEmployeeInput("David", 70000, "Engineer", 25);

        ApiResponseForId apiResponseForId = new ApiResponseForId();
        apiResponseForId.setData(new Employee("4", "David", 70000,  30, "SE", "abc@xyz.com"));

        ResponseEntity<ApiResponseForId> responseEntity = new ResponseEntity<>(apiResponseForId, HttpStatus.OK);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), eq(ApiResponseForId.class)))
                .thenReturn(responseEntity);

        Employee createdEmployee = employeeService.createEmployee(input);
        assertEquals("David", createdEmployee.getEmployeeName());
        assertEquals(70000, createdEmployee.getEmployeeSalary());
    }

    @Test
    void testDeleteEmployeeById() {
        //Delete is disabled from the client side hence not able to complete the operation.
    }

    @Test
    void testDeleteEmployeeById_Failure() {
        //Delete is disabled from the client side hence not able to complete the operation.
    }
}
