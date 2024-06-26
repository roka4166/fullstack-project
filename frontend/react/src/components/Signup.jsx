import { useNavigate } from "react-router-dom";
import { useAuth } from "./context/AuthContext";
import CreateCustomerForm from "./shared/CreateCustomerForm";
import { useEffect } from "react";
import { Flex, Heading, Image, Link, Stack, Text } from "@chakra-ui/react";

const Signup = () => {
    const { customer, setCustomerFromToken } = useAuth();
    const navigate = useNavigate();

    useEffect(() => {
        if (customer) {
            navigate("/dashboard");
        }
    }, [customer, navigate]);

    return (
        <Stack minH={'100vh'} direction={{ base: 'column', md: 'row' }}>
            <Flex p={8} flex={1} alignItems={'center'} justifyContent={'center'}>
                <Stack spacing={4} w={'full'} maxW={'md'}>
                    <Heading fontSize={'2xl'} mb={15}>Register an account</Heading>
                    <CreateCustomerForm onSuccess={(token) => {
                        localStorage.setItem("access_token", token);
                        setCustomerFromToken()
                        navigate("/dashboard")
                    }} />
                    <Link color={"blue.500"} href={"/"}>
                        Have an account? Login.
                    </Link>
                </Stack>
            </Flex>
            <Flex
                flex={1}
                p={10}
                flexDirection={"column"}
                alignItems={"center"}
                justifyContent={"center"}
                bgGradient={{ sm: 'linear(to-r, blue.600, purple.600)' }}
            >
                <Text fontSize={"6xl"} color={'white'} fontWeight={"bold"} mb={5}>
                    <Link target={"_blank"} href={"/signup"}>
                        Enroll Now
                    </Link>
                </Text>
                <Image
                    alt={'Login Image'}
                    objectFit={'scale-down'}
                    src={
                        'https://img.freepik.com/free-photo/glowing-spaceship-orbits-planet-starry-galaxy-generated-by-ai_188544-9655.jpg'
                    }
                />
            </Flex>
        </Stack>
    );
}

export default Signup;