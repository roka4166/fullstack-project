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
import CreateCustomerForm from "./shared/CreateCustomerForm";

const AddIcon = () => "+";
const CloseIcon = () => "X";

const DrawerForm = ({fetchCustomers}) => {
    const { isOpen, onOpen, onClose } = useDisclosure()
    return (
    <>
    <Button
    leftIcon={<AddIcon/>}
    colorScheme="cyan"
    onClick={onOpen}
    >
        Create customer
    </Button>
    <Drawer isOpen={isOpen} onClose={onClose} size="xl">
          <DrawerOverlay />
          <DrawerContent>
            <DrawerCloseButton />
            <DrawerHeader>Create new customer</DrawerHeader>
  
            <DrawerBody>
              <CreateCustomerForm onSuccess={fetchCustomers}/>
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

export default DrawerForm;