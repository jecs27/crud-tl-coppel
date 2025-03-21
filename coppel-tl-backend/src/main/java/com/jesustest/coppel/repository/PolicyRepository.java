package com.jesustest.coppel.repository;

import com.jesustest.coppel.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {

    @Query(value = "SELECT insert_policy(:employee_id, :sku, :quantity, :date)", nativeQuery = true)
    void createPolicy(
            @Param("employee_id") Integer employeeId,
            @Param("sku") String sku,
            @Param("quantity") Integer quantity,
            @Param("date") String date);

    @Query(value = "SELECT update_policy(:p_policy_id, :p_employee_id, :p_sku, :p_quantity, :p_date)", nativeQuery = true)
    void updatePolicy(
            @Param("p_policy_id") Integer policyId,
            @Param("p_employee_id") Integer employeeId,
            @Param("p_sku") String sku,
            @Param("p_quantity") Integer quantity,
            @Param("p_date") String date);

    @Query(value = "SELECT delete_policy(:p_policy_id)", nativeQuery = true)
    void deletePolicy(@Param("p_policy_id") Integer policyId);

    @Query(value = "SELECT get_last_policy_id()", nativeQuery = true)
    Integer getLastPolicyId();

    @Query(value = "SELECT update_policy_fields(:p_policy_id, :p_employee_id, :p_sku, :p_quantity)", nativeQuery = true)
    void updatePolicyFields(
            @Param("p_policy_id") Integer policyId,
            @Param("p_employee_id") Integer employeeId,
            @Param("p_sku") String sku,
            @Param("p_quantity") Integer quantity);
}