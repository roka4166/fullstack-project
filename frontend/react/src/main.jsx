import React from 'react'
import ReactDOM from 'react-dom/client'
import App from './App.jsx'
import './index.css'
import { ChakraProvider } from '@chakra-ui/react';
import { createStandaloneToast } from '@chakra-ui/react'

const { ToastContainer} = createStandaloneToast()


const rootElement = document.getElementById('root')
ReactDOM.createRoot(rootElement).render(
  <React.StrictMode>
      <ChakraProvider>
          <App></App>
          <ToastContainer/>
      </ChakraProvider>
  </React.StrictMode>,
)
