package com.epam.brest.service.rest_app;

import com.epam.brest.model.Department;
import com.epam.brest.service.DepartmentService;
import com.epam.brest.service.rest_app.exception.DepartmentNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
public class DepartmentController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentController.class);

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

    @PostMapping(value = "/departments", consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Integer> createDepartment(@RequestBody Department department) {
        LOGGER.debug("createDepartment({})", department);
        Integer id = departmentService.create(department);
        return new ResponseEntity<>(id, HttpStatus.CREATED);
    }

    @PutMapping(value = "/departments", consumes = {"application/json"}, produces = {"application/json"})
    public ResponseEntity<Integer> updateDepartment(@RequestBody Department department) {
        LOGGER.debug("updateDepartment({})", department);
        Integer id = departmentService.update(department);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @DeleteMapping(value = "/departments/{id}", produces = {"application/json"})
    public ResponseEntity<Integer> deleteDepartment(@PathVariable Integer id) {
        LOGGER.debug("deleteDepartment({})", id);
        Integer result = departmentService.delete(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
