import axios from 'axios';

export const getCustomers = async () => {
    try {
       return (await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/v1/customers`));
    } catch (e) {
        console.error("Error fetching customers:", e);
        throw e;
    }
};

export const updateCustomer = async (customerId, updatedCustomer) => {
    try {
       return (await axios.put(`${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${customerId}`, updatedCustomer));
    } catch (e) {
        console.error("Error updating customer customers:", e);
        throw e;
    }
};

export const saveCustomer = async (customer) => {
    try {
       return (await axios.post(`${import.meta.env.VITE_API_BASE_URL}/api/v1/customers`, customer));
    } catch (e) {
        console.error("Error saving customer:", e);
        throw e;
    }
};

export const deleteCustomer = async (customerId) => {
    try {
       return (await axios.delete(`${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${customerId}`));
    } catch (e) {
        console.error("Error deleting customer:", e);
        throw e;
    }
};
