package com.epam.brest.dao.jdbc;

import com.epam.brest.model.Department;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class DepartmentDaoJdbcMockTest {

    @InjectMocks
    private DepartmentDaoJdbc departmentDao;

    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Test
    public void findAllTest() {

        List<Department> result = departmentDao.findAll();

        Assert.assertNotNull(result);
    }
}
