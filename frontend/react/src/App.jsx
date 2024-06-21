import React, { useState, useEffect } from 'react';
import SidebarWithHeader from './components/shared/SideBar';
import { Wrap, WrapItem, Spinner, Text} from '@chakra-ui/react';
import { getCustomers } from './services/client';
import CardWithImage from './components/Card';
import DrawerForm from './components/CreateCustomerDrawer';
import { errorNotification } from './services/notification';
function App() {
  
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const fetchCustomers = () => {
    setLoading(true);
    getCustomers()
      .then(res => {
        setCustomers(res.data);
      })
      .catch(err => {
        setError(err.response.data.message)
        errorNotification(err.code, err.response.data.message)
      })
      .finally(() => {
        setLoading(false);
      });
  }

  useEffect(() => {
    fetchCustomers();
  }, []);

  console.log(customers);

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

  if(error){
    return (
      <SidebarWithHeader>
      <DrawerForm fetchCustomers={fetchCustomers}></DrawerForm>
        <Text mt={5}>an error occured</Text>
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
            <CardWithImage {...customer} fetchCustomers={fetchCustomers}/>
          </WrapItem>
          ))}
      </Wrap>
    </SidebarWithHeader>
  );
}

export default App;