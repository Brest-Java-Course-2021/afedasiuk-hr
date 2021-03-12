package com.epam.brest.dao.jdbc;

import com.epam.brest.dao.DepartmentDao;
import com.epam.brest.model.Department;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath*:test-db.xml", "classpath*:test-dao.xml", "classpath*:dao.xml"})
public class DepartmentDaoJdbcTest {

    @Autowired
    private DepartmentDao departmentDao;

    @Test
    public void findAllTest() {
        List<Department> departments = departmentDao.findAll();
        Assertions.assertNotNull(departments);
        Assertions.assertTrue(departments.size() > 0);
    }

    @Test
    public void findByIdTest() {
        List<Department> departments = departmentDao.findAll();
        Assertions.assertNotNull(departments);
        Assertions.assertTrue(departments.size() > 0);

        Integer departmentId = departments.get(0).getDepartmentId();
        Department expDepartment = departmentDao.findById(departmentId).get();
        Assertions.assertEquals(departmentId, expDepartment.getDepartmentId());
        Assertions.assertEquals(departments.get(0).getDepartmentName(), expDepartment.getDepartmentName());
        Assertions.assertEquals(departments.get(0), expDepartment);
    }

    @Test
    public void findByIdExceptionalTest() {
        Assertions.assertThrows(EmptyResultDataAccessException.class, ()-> {
            departmentDao.findById(999).get();
        });
    }

    @Test
    public void createDepartmentTest() {
        List<Department> departments = departmentDao.findAll();
        Assertions.assertNotNull(departments);
        Assertions.assertTrue(departments.size() > 0);

        departmentDao.create(new Department("HR"));

        List<Department> realDepartments = departmentDao.findAll();
        Assertions.assertEquals(departments.size() + 1, realDepartments.size());
    }

    @Test
    public void createDepartmentWithSameNameTest() {
        List<Department> departments = departmentDao.findAll();
        Assertions.assertNotNull(departments);
        Assertions.assertTrue(departments.size() > 0);

        Assertions.assertThrows(IllegalArgumentException.class, ()-> {
            departmentDao.create(new Department("HR"));
            departmentDao.create(new Department("HR"));
        });
    }

    @Test
    public void createDepartmentWithSameNameDiffCaseTest() {
        List<Department> departments = departmentDao.findAll();
        Assertions.assertNotNull(departments);
        Assertions.assertTrue(departments.size() > 0);
        Assertions.assertThrows(IllegalArgumentException.class, ()-> {
            departmentDao.create(new Department("HR"));
            departmentDao.create(new Department("Hr"));
        });
    }

    @Test
    public void updateDepartmentTest() {
        List<Department> departments = departmentDao.findAll();
        Assertions.assertNotNull(departments);
        Assertions.assertTrue(departments.size() > 0);

        Department department = departments.get(0);
        department.setDepartmentName("TEST_DEPARTMENT");
        departmentDao.update(department);

        Optional<Department> realDepartment = departmentDao.findById(department.getDepartmentId());
        Assertions.assertEquals("TEST_DEPARTMENT", realDepartment.get().getDepartmentName());
    }

//    @Test
//    public void updateDepartmentNotUniqueNameTest() {
//        List<Department> departments = departmentDao.findAll();
//        Assertions.assertNotNull(departments);
//        Assertions.assertTrue(departments.size() > 0);
//
//        Department department = departments.get(0);
//        department.setDepartmentName(departments.get(1).getDepartmentName());
//        departmentDao.update(department);
//    }

}
