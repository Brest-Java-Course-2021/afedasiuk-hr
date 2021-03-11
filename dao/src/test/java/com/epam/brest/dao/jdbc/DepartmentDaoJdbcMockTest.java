package com.epam.brest.dao.jdbc;

import com.epam.brest.model.Department;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@RunWith(MockitoJUnitRunner.class)
public class DepartmentDaoJdbcMockTest {

    @InjectMocks
    private DepartmentDaoJdbc departmentDao;

    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Captor
    private ArgumentCaptor<RowMapper<Department>> captor;

    @Test
    public void findAllTest() {

        String sql = "select";
        ReflectionTestUtils.setField(departmentDao, "selectSql", sql);
        Department department = new Department();
        List<Department> list = Collections.singletonList(department);

        Mockito.when(namedParameterJdbcTemplate.query(any(), ArgumentMatchers.<RowMapper<Department>>any()))
                .thenReturn(list);

        List<Department> result = departmentDao.findAll();

        Assert.assertNotNull(result);
        Assert.assertFalse(result.isEmpty());
        Assert.assertSame(result.get(0), department);

        Mockito.verify(namedParameterJdbcTemplate).query(eq(sql), captor.capture());

        RowMapper<Department> mapper = captor.getValue();
        Assert.assertNotNull(mapper);

        Mockito.verifyNoMoreInteractions(namedParameterJdbcTemplate);
    }

    @Test
    public void createTest() {

        Department department = new Department();

        Mockito.when(namedParameterJdbcTemplate.queryForObject(any(), any(SqlParameterSource.class), eq(Integer.class)))
                .thenReturn(0);

        //departmentDao.create(department);
    }
}
