package com.jesustest.coppel.repository;

import com.jesustest.coppel.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query(value = "SELECT insert_employee(:firstName, :lastName, :position)", nativeQuery = true)
    void createEmployee(
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("position") String position
    );

    @Query(value = "SELECT update_employee(:id, :firstName, :lastName, :position)", nativeQuery = true)
    void updateEmployee(
            @Param("id") Long id,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            @Param("position") String position
    );

    @Query(value = "SELECT get_last_employee_id()", nativeQuery = true)
    Long getLastEmployeeId();
}