package com.jesustest.coppel.service;

import com.jesustest.coppel.model.Policy;
import com.jesustest.coppel.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PolicyService {
    @Autowired
    private PolicyRepository policyRepository;

    public List<Policy> getAllPolicies() {
        return policyRepository.findAll();
    }

    public Policy getPolicyById(Long policyId) {
        if (policyRepository.findById(policyId).isEmpty()) {
            return null;
        }
        return policyRepository.findById(policyId).get();
    }

    public void createPolicy(Policy policy) {
        policyRepository.createPolicy(
                policy.getEmployee().getEmployeeId().intValue(),
                policy.getInventory().getSku(),
                policy.getQuantity(),
                policy.getDate()
        );
    }

    public void updatePolicy(Long policyId, Policy policy) {
        policyRepository.updatePolicy(
                policyId.intValue(),
                policy.getEmployee().getEmployeeId().intValue(),
                policy.getInventory().getSku(),
                policy.getQuantity(),
                policy.getDate()
        );
    }

    public void updatePolicyFields(Long policyId, Policy policy) {
        policyRepository.updatePolicyFields(
                policyId.intValue(),
                policy.getEmployee().getEmployeeId().intValue(),
                policy.getInventory().getSku(),
                policy.getQuantity()
        );
    }

    public void deletePolicy(Long policyId) {
        policyRepository.deletePolicy(policyId.intValue());
    }

    public Long getLastPolicyId() {
        Integer lastId = policyRepository.getLastPolicyId();
        return lastId != null ? lastId.longValue() : null;
    }
}