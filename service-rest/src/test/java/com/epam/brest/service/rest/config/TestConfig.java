package com.epam.brest.service.rest.config;

import com.epam.brest.service.DepartmentDtoService;
import com.epam.brest.service.DepartmentService;
import com.epam.brest.service.rest.DepartmentDtoServiceRest;
import com.epam.brest.service.rest.DepartmentServiceRest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class TestConfig {

    public static final String DEPARTMENT_DTOS_URL = "http://localhost:8088/department-dtos";
    public static final String DEPARTMENTS_URL = "http://localhost:8088/departments";

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate(new SimpleClientHttpRequestFactory());
    }

    @Bean
    DepartmentDtoService departmentDtoService() {
        return new DepartmentDtoServiceRest(DEPARTMENT_DTOS_URL, restTemplate());
    }

    @Bean
    DepartmentService DepartmentService() {
        return new DepartmentServiceRest(DEPARTMENTS_URL, restTemplate());
    }

}
