package com.epam.brest.service.rest;

import com.epam.brest.model.dto.DepartmentDto;
import com.epam.brest.service.DepartmentDtoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class DepartmentDtoServiceRest implements DepartmentDtoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentDtoServiceRest.class);

    private final String url;

    private final RestTemplate restTemplate;

    public DepartmentDtoServiceRest(String url, RestTemplate restTemplate) {
        this.url = url;
        this.restTemplate = restTemplate;
    }

    @Override
    public List<DepartmentDto> findAllWithAvgSalary() {

        LOGGER.debug("findAllWithAvgSalary()");
        ResponseEntity responseEntity = restTemplate.getForEntity(url, List.class);
        return (List<DepartmentDto>) responseEntity.getBody();
    }
}
