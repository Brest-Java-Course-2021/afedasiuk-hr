package com.epam.brest.service.rest_app;

import com.epam.brest.model.Department;
import com.epam.brest.service.DepartmentService;
import com.epam.brest.service.rest_app.exception.DepartmentNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class DepartmentController {

    private DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping(value = "/departments")
    public Collection<Department> departments() {
        return departmentService.findAll();
    }

    @GetMapping(value = "/departments/{id}")
    public Department findById(@PathVariable Integer id) {
        return departmentService.findById(id).orElseThrow(() -> new DepartmentNotFoundException(id));
    }
}
