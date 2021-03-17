package com.epam.brest.service.rest_app.exception;

public class DepartmentNotFoundException extends RuntimeException {
    public DepartmentNotFoundException(Integer id) {
        super("Department not found for id: " + id);
    }
}
