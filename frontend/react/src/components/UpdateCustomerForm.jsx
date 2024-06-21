import React from 'react';
import { Formik, Form, useField } from 'formik';
import * as Yup from 'yup';
import {FormLabel, Input, Alert, AlertIcon, Select, Box, Button, Stack} from '@chakra-ui/react';
import { updateCustomer } from '../services/client';
import { errorNotification, successNotification } from '../services/notification';

const MyTextInput = ({ label, ...props }) => {
  const [field, meta] = useField(props);
  return (
    <Box>
      <FormLabel htmlFor={props.id || props.name}> {label}</FormLabel>
      <Input className="text-input" {...field} {...props} />
      {meta.touched && meta.error ? (
        <Alert className="error" status='error' mt={2}>{meta.error}
        <AlertIcon></AlertIcon>
        </Alert>
      ) : null}
    </Box>
  );
};

const UpdateCustomerForm = ({fetchCustomers, initialValues, customerId}) => {
  return (
    <>
      <Formik
        initialValues={initialValues}
        validationSchema={Yup.object({
          name: Yup.string()
            .max(15, 'Must be 15 characters or less')
            .required('Required'),
          email: Yup.string()
            .email('Invalid email address')
            .required('Required'),
          age: Yup.number()
            .min(16, 'Must be 16 or older')
            .max(100, 'must be 100 or younger')
            .required('Required')
        })}
        onSubmit={(updatedCustomer, { setSubmitting }) => {
          setSubmitting(true);
          updateCustomer(customerId, updatedCustomer)
          .then(res => {
              successNotification("Customer updated", `${updatedCustomer.name} was succefully updated`);
              fetchCustomers();
          }).catch(err => {
              console.log(err);
              errorNotification(err.code, err.response.data.message)
          }).finally(() => {
              setSubmitting(false);
          })
        }}
      >
          {({ isValid, isSubmitting, dirty }) => {
          console.log('isValid:', isValid);
          console.log('isSubmitting:', isSubmitting);
          return (
            <Form>
              <Stack spacing="24px">
                <MyTextInput
                  label="Name"
                  name="name"
                  type="text"
                  placeholder="Jane"
                />
                <MyTextInput
                  label="Email Address"
                  name="email"
                  type="email"
                  placeholder="jane@formik.com"
                />
                <MyTextInput
                  label="Age"
                  name="age"
                  type="number"
                  placeholder="20"
                />
                <Button
                  isDisabled={!(isValid && dirty) || isSubmitting}
                  type="submit"
                  mt={3}
                >
                  Submit
                </Button>
              </Stack>
            </Form>
          );
        }}
      </Formik>
    </>
  );
};

export default UpdateCustomerForm;