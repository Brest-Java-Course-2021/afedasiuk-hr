package com.epam.brest.dao;

import com.epam.brest.model.Department;

import java.util.List;

public interface DepartmentDao {

    List<Department> findAll();

}
