package com.epam.brest.dao.jdbc;

import com.epam.brest.model.Department;
import com.epam.brest.dao.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class DepartmentDaoJdbc implements DepartmentDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentDaoJdbc.class);

    private static final String SQL_GET_ALL_DEPARTMENTS =
            "SELECT D.DEPARTMENT_ID, D.DEPARTMENT_NAME FROM DEPARTMENT AS D ORDER BY D.DEPARTMENT_NAME";

    private static final String SQL_GET_DEPARTMENT_BY_ID =
            "SELECT D.DEPARTMENT_ID, D.DEPARTMENT_NAME FROM DEPARTMENT AS D WHERE D.DEPARTMENT_ID = :DEPARTMENT_ID";

    private static final String SQL_CREATE_DEPARTMENT =
            "INSERT INTO DEPARTMENT (DEPARTMENT_NAME) VALUES ( :DEPARTMENT_NAME );";

    private static final String SQL_CHECK_DEPARTMENT_NAME =
            "SELECT COUNT(DEPARTMENT_ID) FROM DEPARTMENT WHERE lower(DEPARTMENT_NAME) = lower(:DEPARTMENT_NAME)";

    private static final String SQL_UPDATE_DEPARTMENT =
            "UPDATE DEPARTMENT SET DEPARTMENT_NAME = :DEPARTMENT_NAME WHERE DEPARTMENT_ID = :DEPARTMENT_ID;";

    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    RowMapper rowMapper = BeanPropertyRowMapper.newInstance(Department.class);

    public DepartmentDaoJdbc(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public List<Department> findAll() {
        LOGGER.debug("Find all departments");
        return namedParameterJdbcTemplate.query(SQL_GET_ALL_DEPARTMENTS, rowMapper);
    }

    @Override
    public Optional<Department> findById(Integer departmentId) {
        LOGGER.debug("Find department by id: {}", departmentId);
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("DEPARTMENT_ID", departmentId);
        return Optional.ofNullable((Department) namedParameterJdbcTemplate.queryForObject(SQL_GET_DEPARTMENT_BY_ID, sqlParameterSource, rowMapper));
    }

    @Override
    public Integer create(Department department) {
        long startTime = System.nanoTime();
        LOGGER.debug("Create department: {}", department);
        if (!isDepartmentNameUnique(department)) {
            LOGGER.warn("Department with the same name already exists in DB: {}", department);
            throw new IllegalArgumentException("Department with the same name already exists in DB.");
        }
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("DEPARTMENT_NAME", department.getDepartmentName());
        namedParameterJdbcTemplate.update(SQL_CREATE_DEPARTMENT, sqlParameterSource, keyHolder);
        Integer departmentId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        department.setDepartmentId(departmentId);
        long stopTime = System.nanoTime();
        LOGGER.debug("Execution time: {}", stopTime - startTime);
        return departmentId;
    }

    private boolean isDepartmentNameUnique(Department department) {
        return namedParameterJdbcTemplate.queryForObject(SQL_CHECK_DEPARTMENT_NAME,
                new MapSqlParameterSource("DEPARTMENT_NAME", department.getDepartmentName()), Integer.class) == 0;
    }

    @Override
    public Integer update(Department department) {
        LOGGER.debug("Update department: {}", department);
        SqlParameterSource sqlParameterSource =
                new MapSqlParameterSource("DEPARTMENT_NAME", department.getDepartmentName())
                        .addValue("DEPARTMENT_ID", department.getDepartmentId());
        return namedParameterJdbcTemplate.update(SQL_UPDATE_DEPARTMENT, sqlParameterSource);
    }

    @Override
    public Integer delete(Integer departmentId) {
        LOGGER.debug("Delete department by id: {}", departmentId);
        return null;
    }
}
