import {
    Button,
    Drawer,
    DrawerBody,
    DrawerCloseButton,
    DrawerContent,
    DrawerFooter,
    DrawerHeader,
    DrawerOverlay,
    useDisclosure
} from "@chakra-ui/react";
import UpdateCustomerForm from "./UpdateCustomerForm";
const CloseIcon = () => "X";

const UpdateCustomerDrawer = ({fetchCustomers, initialValues, customerId}) => {
    const { isOpen, onOpen, onClose } = useDisclosure()
    return (
    <>
    <Button
    colorScheme="cyan"
    onClick={onOpen}
    ml={4}
    mr={-5}
    >
        Update customer
    </Button>
    <Drawer isOpen={isOpen} onClose={onClose} size="xl">
          <DrawerOverlay />
          <DrawerContent>
            <DrawerCloseButton />
            <DrawerHeader>Update customer</DrawerHeader>
  
            <DrawerBody>
              <UpdateCustomerForm fetchCustomers={fetchCustomers} initialValues={initialValues} customerId={customerId}/>
            </DrawerBody>
  
            <DrawerFooter>
            <Button
              leftIcon={<CloseIcon/>}
              colorScheme="cyan"
              onClick={onClose}
            >
            Close
            </Button>
            </DrawerFooter>
          </DrawerContent>
        </Drawer>
    </>
    )
}

export default UpdateCustomerDrawer;