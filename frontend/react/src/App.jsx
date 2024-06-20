import React, { useState, useEffect } from 'react';
import SidebarWithHeader from './components/shared/SideBar';
import { Wrap, WrapItem, Spinner, Text} from '@chakra-ui/react';
import { getCustomers } from './services/client';
import CardWithImage from './components/Card';
function App() {
  
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    getCustomers()
      .then(res => {
        console.log('Fetched customers:', res.data);
        setCustomers(res.data);
      })
      .catch(err => {
        console.error('Error fetching customers:', err);
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  console.log('Customers state:', customers);

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
      <Wrap>
        {(() => {
          const items = [];
          for (let i = 0; i < customers.length; i++) {
            items.push(
              <WrapItem key={customers[i].id}>
                <CardWithImage {...customers[i]} />
              </WrapItem>
            );
          }
          return items;
        })()}
      </Wrap>
    </SidebarWithHeader>
  );
}

export default App;