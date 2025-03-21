export type Employee = {
  employeeId: number;
  firstName: string;
  lastName: string;
  position: string;
  positionValid: boolean;
};

export type Inventory = {
  name: string;
  sku: string;
  quantity: number;
};

export type Policy = {
  policyId: number;
  employee: Employee;
  inventory: Inventory;
  quantity: number;
  date: Date;
};
