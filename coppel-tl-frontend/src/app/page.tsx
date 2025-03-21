'use client';

import { DashboardLayout } from "@/components/dashboard-layout";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { useState, useEffect } from "react";
import { findAllEmployees, createEmployee, updateEmployee, deleteEmployee } from "@/api/employee";
import { findAllPolicies, createPolicy, updatePolicy, deletePolicy } from "@/api/policy";
import { Employee, Inventory, Policy } from "@/types";
import { PoliciesTable } from "@/components/policies-table";
import { EmployeesTable } from "@/components/employees-table";
import { findAllInventory } from "@/api/inventory";
import { LoadingDialog } from "@/components/LoadingDialog";

export default function Home() {
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [inventory, setInventory] = useState<Inventory[]>([]);
  const [policies, setPolicies] = useState<Policy[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  
  useEffect(() => {
    const fetchEmployees = async () => {
      setIsLoading(true);
      try {
        const employeesData = await findAllEmployees();
        setEmployees(employeesData.data);
      } catch (error) {
        console.error("Error fetching employees:", error);
        setEmployees([]);
      }
      setIsLoading(false);
    };

    fetchEmployees();
  }, []);

  useEffect(() => {
    const fetchPolicies = async () => {
      setIsLoading(true);
      try {
        const policiesData = await findAllPolicies();
        setPolicies(policiesData.data ?? []);
      } catch (error) {
        console.error("Error fetching policies:", error);
        setPolicies([]);
      }
      setIsLoading(false);
    };

    fetchPolicies();
  }, []);

  useEffect(() => {
    const fetchInventory = async () => {
      setIsLoading(true);
      try {
        const inventoryData = await findAllInventory();
        setInventory(inventoryData.data);
      } catch (error) {
        console.error("Error fetching inventory:", error);
        setInventory([]);
      }
      setIsLoading(false);
    };

    fetchInventory();
  }, []);

  const handleAddEmployee = async (employee: Partial<Employee>) => {
    setIsLoading(true);
    await createEmployee(employee);
    const employeesData = await findAllEmployees();
    setEmployees(employeesData.data);
    setIsLoading(false);
  };

  const handleEditEmployee = async (employeeId: number, employee: Partial<Employee>) => {
    setIsLoading(true);
    await updateEmployee(employeeId, employee);
    const employeesData = await findAllEmployees();
    setEmployees(employeesData.data);
    setIsLoading(false);
  };

  const handleDeleteEmployee = async (employeeId: number) => {
    setIsLoading(true);
    await deleteEmployee(employeeId);
    const employeesData = await findAllEmployees();
    setEmployees(employeesData.data);
    setIsLoading(false);
  };

  const handleAddPolicy = async (policy: Partial<Policy>) => {
    setIsLoading(true);
    await createPolicy(policy);
    const policiesData = await findAllPolicies();
    setPolicies(policiesData.data);
    setIsLoading(false);
  };

  const handleEditPolicy = async (policyId: number, policy: Partial<Policy>) => {
    setIsLoading(true);
    await updatePolicy(policyId, policy);
    const policiesData = await findAllPolicies();
    setPolicies(policiesData.data);
    setIsLoading(false);
  };

  const handleDeletePolicy = async (policyId: number) => {
    setIsLoading(true);
    await deletePolicy(policyId);
    const policiesData = await findAllPolicies();
    setPolicies(policiesData.data);
    setIsLoading(false);
  };


  return (
    <DashboardLayout>
      <LoadingDialog isOpen={isLoading} message="Procesando su solicitud..." />
      <div className="flex flex-col gap-8 p-6">
        <h1 className="text-3xl font-bold">Sistema de Gestión de Pólizas y Empleados</h1>
        
        <Tabs defaultValue="polizas" className="w-full">
          <TabsList className="grid w-full grid-cols-2">
            <TabsTrigger value="polizas">Pólizas</TabsTrigger>
            <TabsTrigger value="empleados">Empleados</TabsTrigger>
          </TabsList>
          <TabsContent value="polizas">
            <PoliciesTable
              policies={policies}
              onAddPolicy={handleAddPolicy}
              onEditPolicy={handleEditPolicy}
              onDeletePolicy={handleDeletePolicy}
              employees={employees}
              inventory={inventory}
              />
          </TabsContent>
          <TabsContent value="empleados">
            <EmployeesTable 
              employees={employees} 
              onAddEmployee={handleAddEmployee} 
              onEditEmployee={handleEditEmployee} 
              onDeleteEmployee={handleDeleteEmployee} 
            />
          </TabsContent>
        </Tabs>
      </div>
    </DashboardLayout>
  );
}