import React, { useState, useEffect } from 'react';
import SidebarWithHeader from './components/shared/SideBar';
import { Wrap, WrapItem, Spinner, Text } from '@chakra-ui/react';
import { getCustomers } from './services/client';
import CardWithImage from './components/Card';
import DrawerForm from './components/CreateCustomerDrawer';
import { errorNotification } from './services/notification';

function App() {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [currentPage, setCurrentPage] = useState(1); // Track current page

  const fetchCustomers = (page) => {
    setLoading(true);
    getCustomers(page)
      .then(res => {
        setCustomers(res.data);
        setCurrentPage(page); // Update current page in state
      })
      .catch(err => {
        setError(err.response.data.message);
        errorNotification(err.code, err.response.data.message);
      })
      .finally(() => {
        setLoading(false);
      });
  };

  useEffect(() => {
    fetchCustomers(currentPage); // Fetch initial customers when component mounts
  }, []);

  console.log(customers);

  const handlePaginationClick = (page) => {
    fetchCustomers(page); // Fetch customers for the selected page
  };

  if (loading) {
    return (
      <SidebarWithHeader>
        <Spinner
          thickness="4px"
          speed="0.65s"
          emptyColor="gray.200"
          color="blue.500"
          size="xl"
        />
      </SidebarWithHeader>
    );
  }

  if (error) {
    return (
      <SidebarWithHeader>
        <DrawerForm fetchCustomers={fetchCustomers}></DrawerForm>
        <Text mt={5}>An error occurred: {error}</Text>
      </SidebarWithHeader>
    );
  }

  if (customers.length === 0) {
    return (
      <SidebarWithHeader>
        <DrawerForm fetchCustomers={fetchCustomers}></DrawerForm>
        <Text mt={5}>No customers found</Text>
      </SidebarWithHeader>
    );
  }

  return (
    <SidebarWithHeader>
      <DrawerForm fetchCustomers={fetchCustomers}></DrawerForm>
      <Wrap justify={"center"} spacing={"30px"}>
        {customers.map((customer, index) => (
          <WrapItem key={index}>
            <CardWithImage {...customer} fetchCustomers={fetchCustomers} />
          </WrapItem>
        ))}
      </Wrap>
      {/* Pagination controls */}
      <div style={{ textAlign: 'center', marginTop: '20px' }}>
        <button onClick={() => handlePaginationClick(currentPage - 1)} disabled={currentPage === 1}>Previous</button>
        <span style={{ margin: '0 10px' }}>Page {currentPage}</span>
        <button onClick={() => handlePaginationClick(currentPage + 1)} disabled={customers.length < 5}>Next</button>
      </div>
    </SidebarWithHeader>
  );
}

export default App;
