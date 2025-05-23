import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { useAuth } from 'react-oidc-context';
import { Typography, Box } from '@mui/material';
import { fetchProjectLayers } from '../api/layers';
import MapContainer from '../components/MapContainer';
import PageLoader from '../components/PageLoader';

const ProjectMapPage = () => {
    const { projectId } = useParams();
    const auth = useAuth();
    const token = auth.user?.access_token;

    const [layers, setLayers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const originalOverflow = document.body.style.overflow;
        document.body.style.overflow = 'hidden';

        return () => {
            document.body.style.overflow = originalOverflow;
        };
    }, []);

    useEffect(() => {
        if (!token) return;

        (async () => {
            try {
                const data = await fetchProjectLayers(token, projectId);
                setLayers(data);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        })();
    }, [projectId, token]);

    if (loading) return <PageLoader />;
    if (error) {
        return (
            <Box sx={{ width: '99%', height: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                <Typography color="error">{error}</Typography>
            </Box>
        );
    }

    return (
        <Box sx={{ width: '100vw', height: '95vh', overflow: 'hidden' }}>
            <MapContainer token={token} layers={layers} setLayers={setLayers} projectId={projectId} />
        </Box>
    );
};

export default ProjectMapPage;