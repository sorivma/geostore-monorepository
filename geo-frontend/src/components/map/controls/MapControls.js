import React from 'react';
import { Box, IconButton, Tooltip } from '@mui/material';
import LayersIcon from '@mui/icons-material/Layers';
import MenuIcon from '@mui/icons-material/Menu';
import BaseMapToggleButton from "./BaseMapToggleButton";

const MapControls = ({ basemapVisible, toggleBasemap, openDrawer }) => (
    <>
        <BaseMapToggleButton/>

        <Box sx={{ position: 'absolute', top: 8, right: 8, zIndex: 1 }}>
            <Tooltip title="Слои проекта">
                <IconButton
                    size="small"
                    onClick={openDrawer}
                    sx={{
                        backgroundColor: 'white',
                        '&:hover': { backgroundColor: '#eee' },
                        boxShadow: 1,
                    }}
                >
                    <MenuIcon fontSize="small" />
                </IconButton>
            </Tooltip>
        </Box>
    </>
);

export default MapControls;