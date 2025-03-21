package com.jesustest.coppel.controller;

import com.jesustest.coppel.model.Policy;
import com.jesustest.coppel.response.Response;
import com.jesustest.coppel.response.ResponseCode;
import com.jesustest.coppel.service.PolicyService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping(value = "/policy")
public class PolicyController {
    private static final Logger logger = LoggerFactory.getLogger(PolicyController.class);
    @Autowired
    private PolicyService policyService;

    @GetMapping
    public ResponseEntity<?> getAllPolicies() {
        List<Policy> policies = policyService.getAllPolicies();
        if (policies.isEmpty()) {
            return new ResponseEntity<>(new Response(ResponseCode.OK, "No policies registered"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(new Response(ResponseCode.OK, policies), HttpStatus.OK);
    }

    @GetMapping("/{policy_id}")
    public ResponseEntity<?> getPolicyById(@PathVariable(name = "policy_id") Long policyId) {
        try {
            Policy existingPolicy = policyService.getPolicyById(policyId);
            if (existingPolicy == null) {
                throw new Exception("Policy with id " + policyId + " does not exist");
            }

            return new ResponseEntity<>(new Response(ResponseCode.OK, createPolicyResponse(existingPolicy)), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while trying to retrieve the specified policy");
            return new ResponseEntity<>(new Response(ResponseCode.FAILURE, "An error occurred while retrieving the policy"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/create", consumes = "application/json")
    public ResponseEntity<?> createPolicy(@Valid @RequestBody Policy newPolicy, BindingResult validation) {
        try {
            if (validation.hasErrors()) {
                HashMap<String, String> errors = new HashMap<>();
                validation.getFieldErrors().forEach(error -> {
                    errors.put("ERROR " + error.hashCode(), error.getDefaultMessage());
                });
                return new ResponseEntity<>(new Response(ResponseCode.FAILURE, errors), HttpStatus.BAD_REQUEST);
            }
            policyService.createPolicy(newPolicy);

            Policy recentlyCreatedPolicy = policyService.getPolicyById(policyService.getLastPolicyId());
            return new ResponseEntity<>(new Response(ResponseCode.OK, createPolicyResponse(recentlyCreatedPolicy)), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while trying to create the policy");
            return new ResponseEntity<>(new Response(ResponseCode.FAILURE, "An error occurred while saving the policy"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{policy_id}")
    public ResponseEntity<?> updatePolicy(@PathVariable(name = "policy_id") Long policyId, @Valid @RequestBody Policy updatedPolicy, BindingResult validation) {
        try {
            if (validation.hasErrors()) {
                HashMap<String, String> errors = new HashMap<>();
                validation.getFieldErrors().forEach(error -> {
                    errors.put("ERROR " + error.hashCode(), error.getDefaultMessage());
                });
                return new ResponseEntity<>(new Response(ResponseCode.FAILURE, errors), HttpStatus.BAD_REQUEST);
            }

            Policy existingPolicy = policyService.getPolicyById(policyId);
            if (existingPolicy == null) {
                return new ResponseEntity<>(new Response(ResponseCode.FAILURE, "Policy " + policyId + " does not exist"), HttpStatus.NOT_FOUND);
            }

            policyService.updatePolicy(policyId, updatedPolicy);
            return new ResponseEntity<>(new Response(ResponseCode.OK, "Policy " + policyId + " was successfully updated"), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while trying to update the policy");
            return new ResponseEntity<>(new Response(ResponseCode.FAILURE, "An error occurred while trying to update the policy"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/update-fields/{policy_id}", consumes = "application/json")
    public ResponseEntity<?> updatePolicyFields(@PathVariable(name = "policy_id") Long policyId, @RequestBody Policy updatedPolicy) {
        try {
            Policy existingPolicy = policyService.getPolicyById(policyId);
            if (existingPolicy == null) {
                return new ResponseEntity<>(new Response(ResponseCode.FAILURE, "Policy " + policyId + " does not exist"), HttpStatus.NOT_FOUND);
            }

            policyService.updatePolicyFields(policyId, updatedPolicy);
            return new ResponseEntity<>(new Response(ResponseCode.OK, "Policy " + policyId + " was successfully updated"), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while trying to update the policy");
            return new ResponseEntity<>(new Response(ResponseCode.FAILURE, "An error occurred while trying to update the policy"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{policy_id}")
    public ResponseEntity<?> deletePolicy(@PathVariable(name = "policy_id") Long policyId) {
        try {
            Policy existingPolicy = policyService.getPolicyById(policyId);
            if (existingPolicy == null) {
                return new ResponseEntity<>(new Response(ResponseCode.FAILURE, "Policy " + policyId + " does not exist"), HttpStatus.NOT_FOUND);
            }

            policyService.deletePolicy(policyId);
            return new ResponseEntity<>(new Response(ResponseCode.OK, "Policy " + policyId + " was successfully deleted"), HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("An error occurred while trying to delete the policy");
            return new ResponseEntity<>(new Response(ResponseCode.FAILURE, "An error occurred while trying to delete the policy"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private HashMap<String, HashMap> createPolicyResponse(Policy policy) {
        HashMap<String, Object> policyInfo = new HashMap<>();
        policyInfo.put("policyId", policy.getPolicyId());
        policyInfo.put("quantity", policy.getQuantity());

        HashMap<String, String> employeeInfo = new HashMap<>();
        employeeInfo.put("firstName", policy.getEmployee().getFirstName());
        employeeInfo.put("lastName", policy.getEmployee().getLastName());

        HashMap<String, String> itemDetails = new HashMap<>();
        itemDetails.put("sku", policy.getInventory().getSku());
        itemDetails.put("name", policy.getInventory().getName());

        HashMap<String, HashMap> response = new HashMap<>();
        response.put("policy", policyInfo);
        response.put("employee", employeeInfo);
        response.put("itemDetails", itemDetails);

        return response;
    }
}
