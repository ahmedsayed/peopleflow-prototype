package com.pplflw.prototype.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pplflw.prototype.domains.Employee;
import com.pplflw.prototype.domains.EmployeeContract;
import com.pplflw.prototype.domains.Employer;
import com.pplflw.prototype.domains.enums.EmployeeStatus;
import com.pplflw.prototype.domains.enums.EmployeeStatusEvent;
import com.pplflw.prototype.exceptions.BusinessException;
import com.pplflw.prototype.exceptions.ResourceNotFoundException;
import com.pplflw.prototype.services.EmployeesService;
import org.hamcrest.core.CombinableMatcher;
import org.hamcrest.core.IsNot;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EmployeesController.class)
public class EmployeesControllerTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public static final String API_URL_PREFIX = "/api/employees/";
    public static final String USERNAME = "user";
    public static final String PASSWORD = "pass";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private EmployeesService employeesService;

    @Test
    public void findAll_IsAuthenticated_ReturnsListOfEmployees() throws Exception {

        Employee employee = new Employee("Ahmed", "Sayed");

        Answer<List<Employee>> allEmployees = setupDummyListAnswer(employee);

        BDDMockito.given(employeesService.findEmployees()).willAnswer(allEmployees);

        mvc.perform(MockMvcRequestBuilders.get(API_URL_PREFIX + "all")
                .with(user(USERNAME).password(PASSWORD))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName", is(employee.getFirstName())));
    }

    @Test
    public void findAll_IsNotAuthenticated_ThrowsException() throws Exception {

        mvc.perform(MockMvcRequestBuilders.get(API_URL_PREFIX + "all")
                .contentType(APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void findEmployee_NotExistingEmployeeId_ThrowsException() throws Exception {
        long requestedEmployeeId = 50L;

        BDDMockito.given(employeesService.findEmployee(requestedEmployeeId)).willThrow(
                new ResourceNotFoundException("Record not found with id: " + requestedEmployeeId));

        mvc.perform(MockMvcRequestBuilders.get(API_URL_PREFIX + requestedEmployeeId)
                .with(user(USERNAME).password(PASSWORD))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ResourceNotFoundException))
                .andExpect(result -> assertEquals("Record not found with id: " + requestedEmployeeId,
                        result.getResolvedException().getMessage()));
    }

    @Test
    public void findEmployee_ExistingEmployeeId_ReturnEmployee() throws Exception {

        long requestedEmployeeId = 5L;

        Employee employee = new Employee("Ahmed", "Sayed");
        employee.setId(requestedEmployeeId);

        Answer<Employee> oneEmployee = setupDummySingleAnswer(employee);

        BDDMockito.given(employeesService.findEmployee(requestedEmployeeId)).willAnswer(oneEmployee);

        mvc.perform(MockMvcRequestBuilders.get(API_URL_PREFIX + requestedEmployeeId)
                .with(user(USERNAME).password(PASSWORD))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id", is(employee.getId()), Long.class));
    }

    @Test
    public void addEmployee_ValidEmployee_AddedSuccessfullyWithCorrectStatus() throws Exception {

        Employee requestedEmployee = new Employee("Ahmed", "Sayed");

        Employee createdEmployee = new Employee("Ahmed", "Sayed");
        createdEmployee.setId(5L);
        createdEmployee.setStatus(EmployeeStatus.ADDED);
        createdEmployee.setCreatedBy(USERNAME);
        
        BDDMockito.given(employeesService.addEmployee(requestedEmployee)).willReturn(createdEmployee);

        mvc.perform(MockMvcRequestBuilders.post(API_URL_PREFIX + "add")
                .with(user(USERNAME).password(PASSWORD))
                .contentType(APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestedEmployee)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id",
                        CombinableMatcher.both(IsNull.notNullValue()).and(IsNot.not(0l)), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("createdBy", is(USERNAME)))
                .andExpect(MockMvcResultMatchers.jsonPath("status", is(EmployeeStatus.ADDED.toString())));
    }
    
    @Test
    public void addEmployee_InvalidEmployee_ThrowsException() throws Exception {

        Employee requestedEmployee = new Employee("Ahmed", "Sayed");
        requestedEmployee.setContracts(new ArrayList<>());
        EmployeeContract dummyContract = new EmployeeContract(null, null, BigDecimal.TEN,
                true, requestedEmployee, new Employer(2l, "test", true));
        requestedEmployee.getContracts().add(dummyContract);
        requestedEmployee.getContracts().add(dummyContract);

        Employee createdEmployee = new Employee("Ahmed", "Sayed");
        createdEmployee.setId(5L);
        createdEmployee.setCreatedBy(USERNAME);
        
        BDDMockito.given(employeesService.addEmployee(requestedEmployee)).willThrow(new BusinessException("", ""));

        mvc.perform(MockMvcRequestBuilders.post(API_URL_PREFIX + "add")
                .with(user(USERNAME).password(PASSWORD))
                .contentType(APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(requestedEmployee)))
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BusinessException));
    }
    
    @Test
    public void updateEmployeeStatus_ValidEmployeeIdAndValidTransition_StatusUpdatedSuccessfully() throws Exception {

        long requestedEmployeeId = 50L;
        EmployeeStatusEvent requestedEvent = EmployeeStatusEvent.START_CHECK;

        Employee updatedEmployee = new Employee("Ahmed", "Sayed");
        updatedEmployee.setStatus(EmployeeStatus.IN_CHECK);
        updatedEmployee.setId(requestedEmployeeId);
        updatedEmployee.setLastUpdatedBy(USERNAME);
        
        BDDMockito.given(employeesService.updateEmployeeStatus(requestedEmployeeId, requestedEvent))
                .willReturn(updatedEmployee);

        mvc.perform(MockMvcRequestBuilders.put(API_URL_PREFIX + requestedEmployeeId + "/" + requestedEvent.toString())
                .with(user(USERNAME).password(PASSWORD))
                .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id", is(requestedEmployeeId), Long.class))
                .andExpect(MockMvcResultMatchers.jsonPath("lastUpdatedBy", is(USERNAME)))
                .andExpect(MockMvcResultMatchers.jsonPath("status", is(EmployeeStatus.IN_CHECK.toString())));
    }

    private <N extends Object> Answer<List<N>> setupDummyListAnswer(N... values) {
        final List<N> someList = new ArrayList<N>();

        someList.addAll(Arrays.asList(values));
        Answer<List<N>> answer = invocation -> someList;

        return answer;
    }

    private <N extends Object> Answer<N> setupDummySingleAnswer(N value) {

        Answer<N> answer = invocation -> value;
        return answer;
    }
}
