package com.epam.brest.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DepartmentTest {

    @Test
    public void getDepartmentNameConstructor() {
        Department department = new Department("IT");
        Assertions.assertEquals("IT", department.getDepartmentName());
    }

    @Test
    public void getDepartmentNameSetter() {
        Department department = new Department();
        department.setDepartmentName("IT");
        Assertions.assertEquals("IT", department.getDepartmentName());
    }
}
