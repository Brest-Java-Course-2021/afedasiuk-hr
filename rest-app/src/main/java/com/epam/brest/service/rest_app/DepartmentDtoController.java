package com.epam.brest.service.rest_app;

import com.epam.brest.model.dto.DepartmentDto;
import com.epam.brest.service.DepartmentDtoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

//@CrossOrigin
@RestController
public class DepartmentDtoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentDtoController.class);

    private DepartmentDtoService departmentDtoService;

    public DepartmentDtoController(DepartmentDtoService departmentDtoService) {
        this.departmentDtoService = departmentDtoService;
    }

    @GetMapping(value = "/department-dtos")
    public Collection<DepartmentDto> findAllWithAvgSalary() {
        LOGGER.debug("findAllWithAvgSalary()");
        return departmentDtoService.findAllWithAvgSalary();
    }

}
