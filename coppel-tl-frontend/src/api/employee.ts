import { Employee } from '@/types';
import axios from 'axios';
import dotenv from 'dotenv';

dotenv.config();
// const urlBase = process.env.API_ROUTE;
const urlBase = 'http://localhost:8080';

export const findAllEmployees = async () => {
  const { data } = await axios.get(`${urlBase}/employee`, {
    headers: {
      'Content-Type': 'application/json'
    }
  });
  return data;
}

export const createEmployee = async (employee: Partial<Employee>) => {
  const { data } = await axios.post(`${urlBase}/employee`, employee, {
    headers: {
      'Content-Type': 'application/json'
    }
  });
  return data;
}

export const updateEmployee = async (employeeId: number, employee: Partial<Employee>) => {
  const { data } = await axios.put(`${urlBase}/employee/${employeeId}`, employee, {
    headers: {
      'Content-Type': 'application/json'
    }
  });
  return data;
}

export const deleteEmployee = async (employeeId: number) => {
  const { data } = await axios.delete(`${urlBase}/employee/${employeeId}`, {
    headers: {
      'Content-Type': 'application/json'
    }
  });
  return data;
}
