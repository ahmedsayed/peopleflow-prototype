package com.pplflw.prototype.web.rest;

import com.pplflw.prototype.domains.Employee;
import com.pplflw.prototype.domains.enums.EmployeeStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@ExtendWith(SpringExtension.class)
@WebMvcTest(EmployeesController.class)
public class EmployeesControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private EmployeesController employeesController;

    @Test
    public void findEmployeesTest() throws Exception {
        
        Employee employee = new Employee("ahmed", "sayed", EmployeeStatus.ADDED);
        
        Answer<ResponseEntity<List<Employee>>> allEmployees = setupDummyListAnswer(employee);

        BDDMockito.given(employeesController.findAll()).willAnswer(allEmployees);
        
//        mvc.perform(MockMvcRequestBuilders.get(VERSION + ARRIVAL + "all")
        mvc.perform(MockMvcRequestBuilders.get("/api/employees/all")
//                .with(user("blaze").password("Q1w2e3r4"))
                .contentType(APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].firstName", is(employee.getFirstName())));
    }

//    @Test
//    public void getArrivalsById() throws Exception {
//        Employee employee = new Employee();
//        employee.setFirstName("Yerevan");
//
//       BDDMockito.given(employeesController.findEmployee(employee.getId())).willReturn(employee);
//
//        mvc.perform(get(VERSION + ARRIVAL + arrival.getId())
//                .with(user("blaze").password("Q1w2e3r4"))
//                .contentType(APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("city", is(arrival.getCity())));
//    }
    
    private <N extends Object> Answer<ResponseEntity<List<N>>> setupDummyListAnswer(N... values) {
    final List<N> someList = new ArrayList<N>();

    someList.addAll(Arrays.asList(values));

    Answer<ResponseEntity<List<N>>> answer = new Answer<ResponseEntity<List<N>>>() {
        public ResponseEntity<List<N>> answer(InvocationOnMock invocation) throws Throwable {
            return ResponseEntity.ok().body(someList);
        }   
    };
    return answer;
}
}
