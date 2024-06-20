import React, { useState, useEffect } from 'react';
import SidebarWithHeader from './components/shared/SideBar';
import { Wrap, WrapItem, Spinner, Text, ChakraProvider} from '@chakra-ui/react';
import { getCustomers } from './services/client';
import CardWithImage from './components/Card';
import ProductSimple from './components/Test'
function App() {
  
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    getCustomers()
      .then(res => {
        setCustomers(res.data);
      })
      .catch(err => {
        console.error('Error fetching customers:', err);
      })
      .finally(() => {
        setLoading(false);
      });
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

  if (customers.length === 0) {
    return (
      <SidebarWithHeader>
        <Text>No customers found</Text>
      </SidebarWithHeader>
    );
  }

  return (
    <SidebarWithHeader>
      <Wrap justify={"center"} spacing={"30px"}>
          {customers.map((customer, index) => (
          <WrapItem key={index}>
            <CardWithImage {...customer}/>
          </WrapItem>
          ))}
      </Wrap>
      <ProductSimple></ProductSimple>
    </SidebarWithHeader>
  );
}

export default App;