import { Inventory } from '@/types';
import axios from 'axios';
import dotenv from 'dotenv';

dotenv.config();
// const urlBase = process.env.API_ROUTE;
const urlBase = 'http://localhost:8080';

export const findAllInventory = async () => {
  const { data } = await axios.get(`${urlBase}/inventory`, {
    headers: {
      'Content-Type': 'application/json'
    }
  });
  return data;
}

export const createInventory = async (inventory: Partial<Inventory>) => {
delete inventory.inventoryId;
  const { data } = await axios.post(`${urlBase}/inventory`, inventory, {
    headers: {
      'Content-Type': 'application/json'
    }
  });
  return data;
}

export const updateInventory = async (inventoryId: number, inventory: Partial<Inventory>) => {
  const { data } = await axios.put(`${urlBase}/inventory/${inventoryId}`, inventory, {
    headers: {
      'Content-Type': 'application/json'
    }
  });
  return data;
}

