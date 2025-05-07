import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import oidcConfig from "./auth/oidcConfig";
import {AuthProvider} from "react-oidc-context";

const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <React.StrictMode>
        <AuthProvider {...oidcConfig}>
            <App/>
        </AuthProvider>
    </React.StrictMode>
);