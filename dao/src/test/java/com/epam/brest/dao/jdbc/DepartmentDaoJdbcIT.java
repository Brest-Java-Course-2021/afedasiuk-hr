package com.epam.brest.dao.jdbc;

import com.epam.brest.dao.DepartmentDao;
import com.epam.brest.model.Department;
import com.epam.brest.testdb.SpringJdbcConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJdbcTest
@Import({DepartmentDaoJdbc.class})
@PropertySource({"classpath:dao.properties"})
@ContextConfiguration(classes = SpringJdbcConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DepartmentDaoJdbcIT {

    @Autowired
    private DepartmentDao departmentDao;

    @Test
    public void findAllTest() {
        List<Department> departments = departmentDao.findAll();
        Assertions.assertNotNull(departments);
        assertTrue(departments.size() > 0);
    }

    @Test
    public void findByIdTest() {
        List<Department> departments = departmentDao.findAll();
        Assertions.assertNotNull(departments);
        assertTrue(departments.size() > 0);

        Integer departmentId = departments.get(0).getDepartmentId();
        Department expDepartment = departmentDao.findById(departmentId).get();
        Assertions.assertEquals(departmentId, expDepartment.getDepartmentId());
        Assertions.assertEquals(departments.get(0).getDepartmentName(), expDepartment.getDepartmentName());
        Assertions.assertEquals(departments.get(0), expDepartment);
    }

    @Test
    public void findByIdExceptionalTest() {

        Optional<Department> optionalDepartment = departmentDao.findById(999);
        assertTrue(optionalDepartment.isEmpty());
    }

    @Test
    public void createDepartmentTest() {
        List<Department> departments = departmentDao.findAll();
        Assertions.assertNotNull(departments);
        assertTrue(departments.size() > 0);

        departmentDao.create(new Department("HR"));

        List<Department> realDepartments = departmentDao.findAll();
        Assertions.assertEquals(departments.size() + 1, realDepartments.size());
    }

    @Test
    public void createDepartmentWithSameNameTest() {
        List<Department> departments = departmentDao.findAll();
        Assertions.assertNotNull(departments);
        assertTrue(departments.size() > 0);

        Assertions.assertThrows(IllegalArgumentException.class, ()-> {
            departmentDao.create(new Department("HR"));
            departmentDao.create(new Department("HR"));
        });
    }

    @Test
    public void createDepartmentWithSameNameDiffCaseTest() {
        List<Department> departments = departmentDao.findAll();
        Assertions.assertNotNull(departments);
        assertTrue(departments.size() > 0);
        Assertions.assertThrows(IllegalArgumentException.class, ()-> {
            departmentDao.create(new Department("HR"));
            departmentDao.create(new Department("Hr"));
        });
    }

    @Test
    public void updateDepartmentTest() {
        List<Department> departments = departmentDao.findAll();
        Assertions.assertNotNull(departments);
        assertTrue(departments.size() > 0);

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
