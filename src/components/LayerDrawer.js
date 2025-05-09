import React, { useState } from 'react';
import {
    Drawer,
    Box,
    Divider,
    Typography,
    IconButton,
    List
} from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';

import LayerListItem from './LayerListItem';
import VectorLayerObjectsList from './VectorLayerObjectsList';
import LayerCreateForm from './LayerCreateForm';
import { useAuth } from 'react-oidc-context';
import RasterLayerObject from "./RasterLayerObject";

const LayerDrawer = ({ open, onClose, layers, onCreateLayer, onDeleteLayer}) => {
    const [selectedLayerId, setSelectedLayerId] = useState(null);
    const auth = useAuth();

    const handleSelectLayer = (layerId) => {
        setSelectedLayerId(prevLayerId => (prevLayerId === layerId ? null : layerId));
    };

    const handleClose = () => {
        setSelectedLayerId(null);
        onClose();
    };

    return (
        <Drawer
            anchor="right"
            open={open}
            onClose={handleClose}
            PaperProps={{
                sx: {
                    width: 300,
                    display: 'flex',
                    flexDirection: 'column',
                },
            }}
        >
            <Box sx={{ p: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Typography variant="h6" noWrap>
                    Слои проекта
                </Typography>
                <IconButton onClick={handleClose}>
                    <CloseIcon />
                </IconButton>
            </Box>

            <Divider />

            <Box sx={{ flex: 1, overflowY: 'auto', p: 2 }}>
                <List dense disablePadding>
                    {layers.map((layer) => (
                        <LayerListItem
                            key={layer.id}
                            layer={layer}
                            onDelete={() => onDeleteLayer(layer.id)}
                            onSelect={handleSelectLayer}
                        >
                            {selectedLayerId === layer.id && layer.type === "VECTOR" && (
                                <VectorLayerObjectsList
                                    key={layer.id}
                                    layerId={layer.id}
                                    token={auth.user?.access_token}
                                />
                            )}
                            {selectedLayerId === layer.id && layer.type === "RASTER" && (
                                <RasterLayerObject
                                    layerId={layer.id}
                                    selectedLayerId={selectedLayerId}
                                    token={auth.user?.access_token}
                                />
                            )}
                        </LayerListItem>
                    ))}

                    <LayerCreateForm
                        onCreate={onCreateLayer}
                        order={layers.length}
                    />
                </List>
            </Box>
        </Drawer>
    );
};

export default LayerDrawer;