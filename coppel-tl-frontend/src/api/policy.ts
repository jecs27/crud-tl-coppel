import { Policy } from '@/types';
import axios from 'axios';
import dotenv from 'dotenv';

dotenv.config();
// const urlBase = process.env.API_ROUTE;
const urlBase = 'http://localhost:8080';

export const findAllPolicies = async () => {
  try {
    const { data } = await axios.get(`${urlBase}/policy`, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
    return data;
  } catch (error) {
    console.log(error);
    return null;
  }
}

export const createPolicy = async (policy: Partial<Policy>) => {
delete policy.policyId;
  const { data } = await axios.post(`${urlBase}/policy`, policy, {
    headers: {
      'Content-Type': 'application/json'
    }
  });
  return data;
}

export const updatePolicy = async (policyId: number, policy: Partial<Policy>) => {
  const { data } = await axios.put(`${urlBase}/policy/${policyId}`, policy, {
    headers: {
      'Content-Type': 'application/json'
    }
  });
  return data;
}

export const deletePolicy = async (policyId: number) => {
  const { data } = await axios.delete(`${urlBase}/policy/${policyId}`, {
    headers: {
      'Content-Type': 'application/json'
    }
  });
  return data;
}
