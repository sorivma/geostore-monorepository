import React, { useState } from 'react';
import { Box } from '@mui/material';
import { useMapInitializer } from './useMapInitializer';
import { useLayerManager } from './useLayerManager';
import { useEventHandlers } from './useEventHandlers';
import FeatureCard from '../FeatureCard';
import {useLayerPolling} from "./useLayerPolling";

const MapView = ({ layers, token, basemapVisible }) => {
    const { mapRef, map } = useMapInitializer({ basemapVisible });
    const [selectedFeature, setSelectedFeature] = useState(null);

    useLayerManager(map, layers, token);
    useEventHandlers({ map, token, layers, setSelectedFeature });
    useLayerPolling(layers, 1500);

    return (
        <Box sx={{ width: '100%', height: '100%', position: 'relative' }}>
            <div ref={mapRef} style={{ width: '100%', height: '100%' }} />
            <FeatureCard
                feature={selectedFeature}
                onClose={() => setSelectedFeature(null)}
                token={token}
            />
        </Box>
    );
};

export default MapView;