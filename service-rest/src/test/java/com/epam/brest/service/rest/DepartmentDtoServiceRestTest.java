package com.epam.brest.service.rest;

import com.epam.brest.model.dto.DepartmentDto;
import com.epam.brest.service.DepartmentDtoService;
import com.epam.brest.service.rest.config.TestConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
class DepartmentDtoServiceRestTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentDtoServiceRestTest.class);

    public static final String DEPARTMENT_DTOS_URL = "http://localhost:8088/department-dtos";

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    DepartmentDtoService departmentDtoService;

    private MockRestServiceServer mockServer;

    private ObjectMapper mapper = new ObjectMapper();


    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void findAllWithAvgSalary() throws Exception {
        LOGGER.debug("shouldFindAllDepartments()");
        // given
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(DEPARTMENT_DTOS_URL)))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(mapper.writeValueAsString(Arrays.asList(
                                createDepartmentDto(0),
                                createDepartmentDto(1))))
                );

        // when
        List<DepartmentDto> departments = departmentDtoService.findAllWithAvgSalary();

        // then
        mockServer.verify();
        assertNotNull(departments);
        assertTrue(departments.size() > 0);
    }

    private DepartmentDto createDepartmentDto(int index) {
        DepartmentDto departmentDto = new DepartmentDto();
        departmentDto.setDepartmentId(index);
        departmentDto.setDepartmentName("d" + index);
        return departmentDto;
    }
}
