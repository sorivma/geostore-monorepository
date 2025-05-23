import React, { useState } from 'react';
import { Box, IconButton, Tooltip } from '@mui/material';
import LayersIcon from '@mui/icons-material/Layers';

const BasemapToggleButton = () => {
    const [visible, setVisible] = useState(true);

    const toggleBasemap = () => {
        const newVisible = !visible;
        setVisible(newVisible);
        window.dispatchEvent(new CustomEvent('updateBasemapVisibility', {
            detail: { visible: newVisible }
        }));
    };

    return (
        <Box sx={{ position: 'absolute', top: 8, left: 8, zIndex: 1 }}>
            <Tooltip title={visible ? 'Скрыть подложку' : 'Показать подложку'}>
                <IconButton
                    size="small"
                    onClick={toggleBasemap}
                    sx={{
                        backgroundColor: 'white',
                        '&:hover': { backgroundColor: '#eee' },
                        boxShadow: 1,
                    }}
                >
                    <LayersIcon fontSize="small" />
                </IconButton>
            </Tooltip>
        </Box>
    );
};

export default BasemapToggleButton;