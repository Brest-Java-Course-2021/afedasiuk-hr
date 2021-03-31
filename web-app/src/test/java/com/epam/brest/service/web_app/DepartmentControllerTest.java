package com.epam.brest.service.web_app;

import com.epam.brest.model.Department;
import com.epam.brest.model.dto.DepartmentDto;
import com.epam.brest.service.DepartmentDtoService;
import com.epam.brest.service.DepartmentService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static com.epam.brest.model.constants.DepartmentConstants.DEPARTMENT_NAME_SIZE;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(DepartmentController.class)
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DepartmentService departmentService;

    @MockBean
    private DepartmentDtoService departmentDtoService;

    @Captor
    private ArgumentCaptor<Department> captor;

    @Test
    public void shouldReturnDepartmentsPage() throws Exception {
        DepartmentDto d1 = createDepartmentDto(1, "IT", BigDecimal.valueOf(150));
        DepartmentDto d2 = createDepartmentDto(2, "SECURITY", BigDecimal.valueOf(400));
        DepartmentDto d3 = createDepartmentDto(3, "MANAGEMENT", null);
        when(departmentDtoService.findAllWithAvgSalary()).thenReturn(Arrays.asList(d1, d2, d3));
        mockMvc.perform(
                MockMvcRequestBuilders.get("/departments")
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("departments"))
                .andExpect(model().attribute("departments", hasItem(
                        allOf(
                                hasProperty("departmentId", is(d1.getDepartmentId())),
                                hasProperty("departmentName", is(d1.getDepartmentName())),
                                hasProperty("avgSalary", is(d1.getAvgSalary()))
                        )
                )))
                .andExpect(model().attribute("departments", hasItem(
                        allOf(
                                hasProperty("departmentId", is(d2.getDepartmentId())),
                                hasProperty("departmentName", is(d2.getDepartmentName())),
                                hasProperty("avgSalary", is(d2.getAvgSalary()))
                        )
                )))
                .andExpect(model().attribute("departments", hasItem(
                        allOf(
                                hasProperty("departmentId", is(d3.getDepartmentId())),
                                hasProperty("departmentName", is(d3.getDepartmentName())),
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
        verifyNoMoreInteractions(departmentDtoService, departmentService);
    }

    @Test
    public void shouldAddNewDepartment() throws Exception {
        String testName = RandomStringUtils.randomAlphabetic(DEPARTMENT_NAME_SIZE);
        mockMvc.perform(
                MockMvcRequestBuilders.post("/department")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("departmentName", testName)
        ).andExpect(status().isFound())
                .andExpect(view().name("redirect:/departments"))
                .andExpect(redirectedUrl("/departments"));

        verify(departmentService).create(captor.capture());

        Department d = captor.getValue();
        assertEquals(testName, d.getDepartmentName());
    }

    @Test
    public void shouldOpenEditDepartmentPageById() throws Exception {
        Department d = createDepartment(1, "IT");
        when(departmentService.findById(any())).thenReturn(Optional.of(d));
        mockMvc.perform(
                MockMvcRequestBuilders.get("/department/" + d.getDepartmentId())
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("department"))
                .andExpect(model().attribute("isNew", is(false)))
                .andExpect(model().attribute("department", hasProperty("departmentId", is(d.getDepartmentId()))))
                .andExpect(model().attribute("department", hasProperty("departmentName", is(d.getDepartmentName()))));
    }

    @Test
    public void shouldReturnToDepartmentsPageIfDepartmentNotFoundById() throws Exception {
        int id = 99999;
        mockMvc.perform(
                MockMvcRequestBuilders.get("/department/" + id)
        ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isFound())
                .andExpect(MockMvcResultMatchers.redirectedUrl("departments"));
        verify(departmentService).findById(id);
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

        verify(departmentService).update(captor.capture());

        Department d = captor.getValue();
        assertEquals(testName, d.getDepartmentName());
    }

    @Test
    public void shouldDeleteDepartment() throws Exception {

        mockMvc.perform(
                MockMvcRequestBuilders.get("/department/3/delete")
        ).andExpect(status().isFound())
                .andExpect(view().name("redirect:/departments"))
                .andExpect(redirectedUrl("/departments"));

        verify(departmentService).delete(3);
    }

    private DepartmentDto createDepartmentDto(int id, String name, BigDecimal avgSalary) {
        DepartmentDto departmentDto = new DepartmentDto();
        departmentDto.setDepartmentId(id);
        departmentDto.setDepartmentName(name);
        departmentDto.setAvgSalary(avgSalary);
        return departmentDto;
    }

    private Department createDepartment(int id, String name) {
        Department department = new Department();
        department.setDepartmentId(id);
        department.setDepartmentName(name);
        return department;
    }
}

