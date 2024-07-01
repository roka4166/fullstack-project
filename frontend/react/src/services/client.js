import axios from 'axios';

const getAuthConfig = () => ({
    headers: {
        Authorization: `Bearer ${localStorage.getItem("access_token")}`
    }
})

export const getCustomers = async (page) => {
    try {
       return (await axios.get(`${import.meta.env.VITE_API_BASE_URL}/api/v1/customers`, {
           params: { page },
           ...getAuthConfig()
       }));
    } catch (e) {
        console.error("Error fetching customers:", e);
        throw e;
    }
};

export const updateCustomer = async (customerId, updatedCustomer) => {
    try {
       return (await axios.put(`${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${customerId}`, updatedCustomer, getAuthConfig()));
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
       return (await axios.delete(`${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${customerId}`, getAuthConfig()));
    } catch (e) {
        console.error("Error deleting customer:", e);
        throw e;
    }
};
export const login = async (usernameAndPassword) => {
    try {
        return await axios.post(`${import.meta.env.VITE_API_BASE_URL}/api/v1/auth/login`, usernameAndPassword);
    } catch (e) {
        console.error("Error logging in: something is wrong:", e);
        throw e;
    }
};

export const uploadCustomerProfilePicture = async (id, formData) => {
    try {
        return axios.post(
            `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${id}/profile-image`,
            formData,
            {
                ...getAuthConfig(),
                'Content-Type' : 'multipart/form-data'
            }
        );
    } catch (e) {
        throw e;
    }
};
export const customerProfilePictureUrl = (id) =>
    `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${id}/profile-image`;

export const fetchCustomerProfilePicture = async (id) => {
    const url = `${import.meta.env.VITE_API_BASE_URL}/api/v1/customers/${id}/profile-image`;
    const response = await axios.get(url, { responseType: 'blob', ...getAuthConfig() });
    const blob = response.data;

    return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onloadend = () => resolve(reader.result);
        reader.onerror = reject;
        reader.readAsDataURL(blob);
    });
};
