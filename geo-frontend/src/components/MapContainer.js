import React, { useState } from 'react';
import { Box } from '@mui/material';
import MapView from './map/MapView';
import MapControls from './map/controls/MapControls';
import LayerDrawer from './LayerDrawer';
import SnackbarWithTimer from './SnackbarWithTimer';
import { createLayer, deleteLayer } from '../api/layers';
import SearchField from "./SearchField";

const MapContainer = ({ layers, setLayers, token, projectId }) => {
    const [basemapVisible, setBasemapVisible] = useState(true);
    const [drawerOpen, setDrawerOpen] = useState(false);
    const [snackbar, setSnackbar] = useState({ message: '', severity: 'success' });

    const handleCreateLayer = async (newLayerData) => {
        try {
            const result = await createLayer(token, projectId, newLayerData);
            const fullLayer = {
                id: result.id,
                name: newLayerData.name,
                type: newLayerData.type,
                order: newLayerData.order,
                visible: true,
            };
            setLayers((prev) => [...prev, fullLayer]);
            setSnackbar({ message: 'Слой успешно создан', severity: 'success' });
        } catch (err) {
            console.error('Ошибка при создании слоя:', err);
            setSnackbar({ message: 'Ошибка при создании слоя', severity: 'error' });
        }
    };

    const handleDeleteLayer = async (layerId) => {
        try {
            await deleteLayer(token, projectId, layerId);
            setLayers((prev) => prev.filter((l) => l.id !== layerId));
            window.dispatchEvent(new CustomEvent('removeLayer', {
                detail: { layerId },
            }));
            setSnackbar({ message: 'Слой удалён', severity: 'success' });
        } catch (err) {
            console.error('Ошибка при удалении слоя:', err);
            setSnackbar({ message: 'Ошибка при удалении слоя', severity: 'error' });
        }
    };

    const handleToggleVisibility = (layerId) => {
        setLayers((prev) =>
            prev.map((layer) =>
                layer.id === layerId ? { ...layer, visible: !layer.visible } : layer
            )
        );
    };

    return (
        <Box
            sx={{
                width: '100%',
                height: '100%',
                display: 'flex',
                flexDirection: 'column',
                overflow: 'hidden',
            }}
        >
            <Box sx={{ flex: 1, position: 'relative' }}>
                <MapView
                    layers={layers}
                    token={token}
                    basemapVisible={basemapVisible}
                />

                <MapControls
                    basemapVisible={basemapVisible}
                    toggleBasemap={() => setBasemapVisible((v) => !v)}
                    openDrawer={() => setDrawerOpen(true)}
                />

                <SearchField
                    token={token}
                    projectId={projectId}
                />


                <LayerDrawer
                    open={drawerOpen}
                    onClose={() => setDrawerOpen(false)}
                    layers={layers}
                    onCreateLayer={handleCreateLayer}
                    onDeleteLayer={handleDeleteLayer}
                    onToggleVisibility={handleToggleVisibility}
                />
            </Box>

            <SnackbarWithTimer
                message={snackbar.message}
                onClose={() => setSnackbar({ message: '', severity: 'success' })}
                severity={snackbar.severity}
                duration={4000}
            />
        </Box>
    );
};

export default MapContainer;