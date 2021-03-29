package com.epam.brest.service.rest_app;

import com.epam.brest.model.Department;
import com.epam.brest.service.rest_app.exception.CustomExceptionHandler;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@Transactional
class DepartmentControllerIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentControllerIT.class);

    public static final String DEPARTMENTS_ENDPOINT = "/departments";

    @Autowired
    private DepartmentController departmentController;

    @Autowired
    private CustomExceptionHandler customExceptionHandler;

    @Autowired
    protected ObjectMapper objectMapper;

    protected MockMvc mockMvc;

    protected MockDepartmentService departmentService = new MockDepartmentService();

    @BeforeEach
    void setUp() {
        this.mockMvc = standaloneSetup(departmentController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .setControllerAdvice(customExceptionHandler)
                .alwaysDo(MockMvcResultHandlers.print())
                .build();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldFindAllDepartments() throws Exception {

        LOGGER.debug("shouldFindAllDepartments()");
        List<Department> departments = departmentService.findAll();
        assertNotNull(departments);
        assertTrue(departments.size() > 0);
    }

    @Test
    public void shouldFindById() throws Exception {

        LOGGER.debug("shouldFindById()");
        List<Department> departments = departmentService.findAll();
        Assertions.assertNotNull(departments);
        assertTrue(departments.size() > 0);

        Integer departmentId = departments.get(0).getDepartmentId();
        Department expDepartment = departmentService.findById(departmentId).get();
        Assertions.assertEquals(departmentId, expDepartment.getDepartmentId());
        Assertions.assertEquals(departments.get(0).getDepartmentName(), expDepartment.getDepartmentName());
        Assertions.assertEquals(departments.get(0), expDepartment);
    }

    @Test
    public void shouldReturnNotFoundOnMissedDepartment() throws Exception {

        LOGGER.debug("shouldReturnNotFoundOnMissedDepartment()");
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get(
                DEPARTMENTS_ENDPOINT + "/999999")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound())
                .andReturn().getResponse();
        assertNotNull(response);
    }

    @Test
    public void shouldCreateDepartment() throws Exception {

        LOGGER.debug("shouldCreateDepartment()");
        Integer countBefore = departmentService.count();

        departmentService.create(new Department("HR"));

        // verify database size
        Integer countAfter = departmentService.count();
        Assertions.assertEquals(countBefore + 1, countAfter);
    }

    @Test
    public void createDepartmentWithSameNameTest() throws Exception {

        LOGGER.debug("createDepartmentWithSameNameTest()");

        departmentService.create(new Department("HR"));

        String json = objectMapper.writeValueAsString(new Department("HR"));
        MockHttpServletResponse response =
                mockMvc.perform(post(DEPARTMENTS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isUnprocessableEntity())
                        .andReturn().getResponse();
        assertNotNull(response);
    }

    @Test
    public void createDepartmentWithSameNameDiffCaseTest() throws Exception {

        LOGGER.debug("createDepartmentWithSameNameDiffCaseTest()");

        departmentService.create(new Department("HR"));

        String json = objectMapper.writeValueAsString(new Department("Hr"));
        MockHttpServletResponse response =
                mockMvc.perform(post(DEPARTMENTS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isUnprocessableEntity())
                        .andReturn().getResponse();
        assertNotNull(response);
    }

    @Test
    public void shouldUpdateDepartment() throws Exception {

        LOGGER.debug("shouldUpdateDepartment()");
        List<Department> departments = departmentService.findAll();
        Assertions.assertNotNull(departments);
        assertTrue(departments.size() > 0);

        Department department = departments.get(0);
        department.setDepartmentName("TEST_DEPARTMENT");
        departmentService.update(department);

        Optional<Department> realDepartment = departmentService.findById(department.getDepartmentId());
        Assertions.assertEquals("TEST_DEPARTMENT", realDepartment.get().getDepartmentName());
    }

    @Test
    public void shouldDeleteDepartment() throws Exception {

        LOGGER.debug("shouldDeleteDepartment()");
        Integer id = departmentService.create(new Department("HR"));
        Integer countBefore = departmentService.count();

        departmentService.delete(id);

        // verify database size
        Integer countAfter = departmentService.count();
        Assertions.assertEquals(countBefore - 1, countAfter);
    }

    @Test
    public void shouldReturnNotFoundOnDeleteMissedDepartment() throws Exception {

        LOGGER.debug("shouldReturnNotFoundOnDeleteMissedDepartment()");
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.delete(
                DEPARTMENTS_ENDPOINT + "/999999")
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound())
                .andReturn().getResponse();
        assertNotNull(response);
    }


    /////////////////////////////////////////////////////////////////////////////////

    private class MockDepartmentService {

        public List<Department> findAll() throws Exception {
            LOGGER.debug("findAll()");
            MockHttpServletResponse response = mockMvc.perform(get(DEPARTMENTS_ENDPOINT)
                    .accept(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk())
                    .andReturn().getResponse();
            assertNotNull(response);

            return objectMapper.readValue(response.getContentAsString(), new TypeReference<List<Department>>() {});
        }

        public Optional<Department> findById(Integer departmentId) throws Exception {
            LOGGER.debug("findById({})", departmentId);
            MockHttpServletResponse response = mockMvc.perform(get(DEPARTMENTS_ENDPOINT + "/" + departmentId)
                    .accept(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk())
                    .andReturn().getResponse();
            return Optional.of(objectMapper.readValue(response.getContentAsString(), Department.class));
        }

        public Integer create(Department department) throws Exception {
            LOGGER.debug("create({})", department);
            String json = objectMapper.writeValueAsString(department);
            MockHttpServletResponse response =
                    mockMvc.perform(post(DEPARTMENTS_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json)
                            .accept(MediaType.APPLICATION_JSON)
                    ).andExpect(status().isCreated())
                            .andReturn().getResponse();
            return objectMapper.readValue(response.getContentAsString(), Integer.class);
        }

        public Integer update(Department department) throws Exception {
            LOGGER.debug("update({})", department);
            MockHttpServletResponse response =
                    mockMvc.perform(put(DEPARTMENTS_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(department))
                            .accept(MediaType.APPLICATION_JSON)
                    ).andExpect(status().isOk())
                            .andReturn().getResponse();
            return objectMapper.readValue(response.getContentAsString(), Integer.class);
        }

        public Integer delete(Integer departmentId) throws Exception {
            LOGGER.debug("delete(id:{})", departmentId);
            MockHttpServletResponse response = mockMvc.perform(
                    MockMvcRequestBuilders.delete(new StringBuilder(DEPARTMENTS_ENDPOINT).append("/")
                            .append(departmentId).toString())
                            .accept(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk())
                    .andReturn().getResponse();

            return objectMapper.readValue(response.getContentAsString(), Integer.class);
        }

        public Integer count() throws Exception {
            LOGGER.debug("count()");
            MockHttpServletResponse response = mockMvc.perform(get(DEPARTMENTS_ENDPOINT + "/count")
                    .accept(MediaType.APPLICATION_JSON)
            ).andExpect(status().isOk())
                    .andReturn().getResponse();
            return objectMapper.readValue(response.getContentAsString(), Integer.class);
        }
    }
}
