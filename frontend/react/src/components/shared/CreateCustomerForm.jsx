import React from 'react';
import { Formik, Form, useField } from 'formik';
import * as Yup from 'yup';
import {FormLabel, Input, Alert, AlertIcon, Select, Box, Button, Stack} from '@chakra-ui/react';
import { saveCustomer } from '../../services/client';
import { errorNotification, successNotification } from '../../services/notification';

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

const MySelect = ({ label, ...props }) => {
  const [field, meta] = useField(props);
  return (
    <Box>
      <FormLabel htmlFor={props.id || props.name}>{label}</FormLabel>
      <Select {...field} {...props} />
      {meta.touched && meta.error ? (
        <Alert className="error" status='error' mt={2}>{meta.error}
        <AlertIcon></AlertIcon>
        </Alert>
      ) : null}
    </Box>
  );
};

const CreateCustomerForm = ({onSuccess}) => {
  return (
    <>
      <Formik
        initialValues={{
          name: '',
          email: '',
          age: '0',
          gender: '',
          password: ''
        }}
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
            .required('Required'),
          gender: Yup.string()
            .oneOf(
              ['MALE', 'FEMALE'],
              'Invalid gender type'
            )
            .required('Required'),
            password: Yup.string()
            .max(20, 'Must be 20 characters or less')
            .required('Required'),
        })}
        onSubmit={(customer, { setSubmitting }) => {
          setSubmitting(true);
          saveCustomer(customer)
          .then(res => {
              successNotification("Customer saved", `${customer.name} was succefully saved`);
              onSuccess(res.headers["authorization"]);
          }).catch(err => {
              console.log(err);
              errorNotification(err.code, err.response.data.message)
          }).finally(() => {
              setSubmitting(false);
          })
        }}
      >
          {({ isValid, isSubmitting }) => {
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
                <MySelect label="Select gender" name="gender">
                  <option value="">Select gender</option>
                  <option value="MALE">MALE</option>
                  <option value="FEMALE">FEMALE</option>
                </MySelect>
                <MyTextInput
                  label="Password"
                  name="password"
                  type="text"
                  placeholder="choose password"
                />
                <Button
                  isDisabled={!isValid || isSubmitting}
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

export default CreateCustomerForm;