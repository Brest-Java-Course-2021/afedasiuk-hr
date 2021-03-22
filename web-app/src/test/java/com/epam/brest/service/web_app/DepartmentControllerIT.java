package com.epam.brest.service.web_app;

import com.epam.brest.model.Department;
import com.epam.brest.service.DepartmentService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Optional;

import static com.epam.brest.model.constants.DepartmentConstants.DEPARTMENT_NAME_SIZE;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:app-context-test.xml"})
@Transactional
@Disabled // TODO Fix test
class DepartmentControllerIT {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private DepartmentService departmentService;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void shouldReturnDepartmentsPage() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/departments")
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("departments"))
                .andExpect(model().attribute("departments", hasItem(
                        allOf(
                                hasProperty("departmentId", is(1)),
                                hasProperty("departmentName", is("IT")),
                                hasProperty("avgSalary", is(BigDecimal.valueOf(150)))
                        )
                )))
                .andExpect(model().attribute("departments", hasItem(
                        allOf(
                                hasProperty("departmentId", is(2)),
                                hasProperty("departmentName", is("SECURITY")),
                                hasProperty("avgSalary", is(BigDecimal.valueOf(400)))
                        )
                )))
                .andExpect(model().attribute("departments", hasItem(
                        allOf(
                                hasProperty("departmentId", is(3)),
                                hasProperty("departmentName", is("MANAGEMENT")),
                                hasProperty("avgSalary", isEmptyOrNullString())
                        )
                )))
        ;
    }

    @Test
    public void shouldOpenNewDepartmentPage() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/department")
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("department"))
                .andExpect(model().attribute("isNew", is(true)))
                .andExpect(model().attribute("department", isA(Department.class)));
    }

    @Test
    public void shouldAddNewDepartment() throws Exception {

        Integer countBefore = departmentService.count();

        mockMvc.perform(
                MockMvcRequestBuilders.post("/department")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("departmentName", "test")
        ).andExpect(status().isFound())
                .andExpect(view().name("redirect:/departments"))
                .andExpect(redirectedUrl("/departments"));

        // verify database size
        Integer countAfter = departmentService.count();
        Assertions.assertEquals(countBefore + 1, countAfter);
    }

    @Test
    public void shouldOpenEditDepartmentPageById() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders.get("/department/1")
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("department"))
                .andExpect(model().attribute("isNew", is(false)))
                .andExpect(model().attribute("department", hasProperty("departmentId", is(1))))
                .andExpect(model().attribute("department", hasProperty("departmentName", is("IT"))));
    }

    @Test
    public void shouldReturnToDepartmentsPageIfDepartmentNotFoundById() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders.get("/department/99999")
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("departments"));
    }

    @Test
    public void shouldUpdateDepartmentAfterEdit() throws Exception {

        String testName = RandomStringUtils.randomAlphabetic(DEPARTMENT_NAME_SIZE);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/department/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("departmentId", "1")
                        .param("departmentName", testName)
        ).andExpect(status().isFound())
                .andExpect(view().name("redirect:/departments"))
                .andExpect(redirectedUrl("/departments"));

        Optional<Department> optionalDepartment = departmentService.findById (1);
        assertTrue(optionalDepartment.isPresent());
        assertEquals(testName, optionalDepartment.get().getDepartmentName());
    }

    @Test
    public void shouldDeleteDepartment() throws Exception {

        Integer countBefore = departmentService.count();

        mockMvc.perform(
                MockMvcRequestBuilders.get("/department/3/delete")
        ).andExpect(status().isFound())
                .andExpect(view().name("redirect:/departments"))
                .andExpect(redirectedUrl("/departments"));

        // verify database size
        Integer countAfter = departmentService.count();
        Assertions.assertEquals(countBefore - 1, countAfter);
    }
}

