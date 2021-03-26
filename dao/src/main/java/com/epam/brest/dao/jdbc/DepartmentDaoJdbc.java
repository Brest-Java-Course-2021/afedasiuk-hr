package com.epam.brest.dao.jdbc;

import com.epam.brest.dao.DepartmentDao;
import com.epam.brest.model.Department;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 *  Implementation of DepartmentDao interface.
 */
@Repository
public class DepartmentDaoJdbc implements DepartmentDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(DepartmentDaoJdbc.class);

    @Value("${department.select}")
    private String selectSql;

    @Value("${department.create}")
    private String createSql;

    @Value("${department.update}")
    private String updateSql;

    @Value("${department.findById}")
    private String findByIdSql;

    @Value("${department.check}")
    private String checkSql;

    @Value("${department.count}")
    private String countSql;

    @Value("${department.delete}")
    private String deleteSql;

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private RowMapper rowMapper = BeanPropertyRowMapper.newInstance(Department.class);

    public DepartmentDaoJdbc(DataSource dataSource) {
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * Returns all Departments.
     *
     * @return list of Departments objects.
     */
    @Override
    public List<Department> findAll() {
        LOGGER.debug("Find all departments");
        return namedParameterJdbcTemplate.query(selectSql, rowMapper);
    }

    @Override
    public Optional<Department> findById(Integer departmentId) {
        LOGGER.debug("Find department by id: {}", departmentId);
        SqlParameterSource sqlParameterSource = new MapSqlParameterSource("DEPARTMENT_ID", departmentId);
        // Note: don't use queryForObject to reduce exception handling
        List<Department> results = namedParameterJdbcTemplate.query(findByIdSql, sqlParameterSource, rowMapper);
        return Optional.ofNullable(DataAccessUtils.uniqueResult(results));
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
        namedParameterJdbcTemplate.update(createSql, sqlParameterSource, keyHolder);
        Integer departmentId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        department.setDepartmentId(departmentId);
        long stopTime = System.nanoTime();
        LOGGER.debug("Execution time: {}", stopTime - startTime);
        return departmentId;
    }

    private boolean isDepartmentNameUnique(Department department) {
        return namedParameterJdbcTemplate.queryForObject(checkSql,
                new MapSqlParameterSource("DEPARTMENT_NAME", department.getDepartmentName()), Integer.class) == 0;
    }

    @Override
    public Integer update(Department department) {
        LOGGER.debug("Update department: {}", department);
        SqlParameterSource sqlParameterSource =
                new MapSqlParameterSource("DEPARTMENT_NAME", department.getDepartmentName())
                        .addValue("DEPARTMENT_ID", department.getDepartmentId());
        return namedParameterJdbcTemplate.update(updateSql, sqlParameterSource);
    }

    @Override
    public Integer delete(Integer departmentId) {
        LOGGER.debug("Delete department by id: {}", departmentId);
        return namedParameterJdbcTemplate.update(deleteSql, new MapSqlParameterSource()
                .addValue("DEPARTMENT_ID", departmentId));
    }

    @Override
    public Integer count() {
        LOGGER.debug("count()");
        return namedParameterJdbcTemplate.queryForObject(countSql, new HashMap<>(), Integer.class);
    }
}
