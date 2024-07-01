'use client'

import {
  Heading,
  Avatar,
  Box,
  Center,
  Image,
  Flex,
  Text,
  Stack,
  Tag,
  useColorModeValue,
  Button,
  AlertDialogContent,
  AlertDialog,
  AlertDialogHeader,
  AlertDialogBody,
  AlertDialogFooter,
  useDisclosure,
  AlertDialogOverlay,
} from '@chakra-ui/react'

import {useEffect, useRef, useState} from 'react';
import { deleteCustomer, fetchCustomerProfilePicture} from '../services/client';
import { errorNotification, successNotification } from '../services/notification';
import UpdateCustomerDrawer from './UpdateCustomerDrawer';

export default function CardWithImage({id, name, email, age, gender, fetchCustomers}) {
  const imageGender = gender === 'MALE' ? 'men' : 'women';
  const { isOpen, onOpen, onClose } = useDisclosure()
  const cancelRef = useRef()

  const [profilePictureUrl, setProfilePictureUrl] = useState(null);

  useEffect(() => {
    const fetchProfilePicture = async () => {
      try {
        const url = await fetchCustomerProfilePicture(id);
        setProfilePictureUrl(url);
      } catch (error) {
        console.error('Error fetching profile picture:', error);
        errorNotification('Failed to fetch profile picture');
      }
    };

    fetchProfilePicture();
  }, [id]);

  return (
    <Center py={6}>
      <Box
        maxW={'300px'}
        w={'full'}
        bg={useColorModeValue('white', 'gray.800')}
        boxShadow={'2xl'}
        rounded={'md'}
        overflow={'hidden'}>
        <Image
          h={'120px'}
          w={'full'}
          src={
            'https://images.pexels.com/photos/255379/pexels-photo-255379.jpeg?cs=srgb&dl=pexels-padrinan-255379.jpg&fm=jpg'
          }
          objectFit="cover"
          alt="#"
        />
        <Flex justify={'center'} mt={-12}>
          <Avatar
            size={'xl'}
            src={
              profilePictureUrl
            }
            css={{
              border: '2px solid white',
            }}
          />
        </Flex>

        <Box p={6}>
          <Stack spacing={0} align={'center'} mb={5}>
          <Tag borderRadius={"full"}>{id}</Tag>
            <Heading fontSize={'2xl'} fontWeight={500} fontFamily={'body'}>
              {name}
            </Heading>
            <Text color={'gray.500'}>{email}</Text>
            <Text color={'gray.500'}> Age : {age} | {gender}</Text>
          </Stack>
        </Box>
        <Stack direction={'row'} justify={'center'} spacing={6}>
          <Stack>
            <UpdateCustomerDrawer initialValues={{name, email, age}} customerId={id} fetchCustomers={fetchCustomers}/>
          </Stack>
          <Stack>
          <Button colorScheme='red' mb={3} ml={3} mr={3} onClick={onOpen}>Delete</Button>
            <AlertDialog
            isOpen={isOpen}
            leastDestructiveRef={cancelRef}
            onClose={onClose}
          >
            <AlertDialogOverlay>
              <AlertDialogContent>
                <AlertDialogHeader fontSize='lg' fontWeight='bold'>
                  Delete Customer
                </AlertDialogHeader>

                <AlertDialogBody>
                  Are you sure? You can't undo this action afterwards.
                </AlertDialogBody>

                <AlertDialogFooter>
                  <Button ref={cancelRef} onClick={onClose}>
                    Cancel
                  </Button>
                  <Button 
                    colorScheme='red' 
                    onClick={() => {
                        deleteCustomer(id)
                        .then(res => {
                        console.log(res)
                        successNotification("Customer deleted", `${name} was successfully deleted`);
                        fetchCustomers();
                        })
                        .catch(err => {
                          errorNotification(err.code, err.response.data.message);
                        })
                        .finally(() => {
                          onClose();
                        });
                    }} 
                    ml={3}>
                    Delete
                  </Button>
                </AlertDialogFooter>
              </AlertDialogContent>
            </AlertDialogOverlay>
          </AlertDialog>
        </Stack>
        </Stack>
      </Box>
    </Center>
  )
}