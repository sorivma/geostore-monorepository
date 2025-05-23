import React from 'react';
import { useAuth } from 'react-oidc-context';
import PageLoader from './components/PageLoader';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import HomePage from "./pages/HomePage";
import {CssBaseline} from "@mui/material";
import Header from "./components/Header";
import ProjectMapPage from "./pages/ProjectMapPage";


function App() {
    const auth = useAuth();
    if (auth.isLoading) {
        return <PageLoader />;
    }

    if (!auth.isAuthenticated) {
        auth.signinRedirect();
        return <PageLoader />;
    }

    return (
        <>
            <CssBaseline />
            <Header />
            <Router>
                <Routes>
                    <Route path="/" element={<HomePage />} />
                    <Route path="/projects/:projectId/map" element={<ProjectMapPage />} />
                </Routes>
            </Router>
        </>
    );
}

export default App;