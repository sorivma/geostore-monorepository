import React from 'react';
import {
    List,
    ListItem,
    ListItemButton,
    ListItemText,
    ListItemIcon,
    IconButton,
    Box,
    Tooltip
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import VisibilityIcon from '@mui/icons-material/Visibility';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import LayersOutlinedIcon from '@mui/icons-material/LayersOutlined';
import PhotoIcon from '@mui/icons-material/Photo';

const LayerList = ({ layers, onDeleteLayer, onToggleVisibility, onSelectLayer }) => {
    return (
        <List dense disablePadding>
            {layers.map((layer) => (
                <ListItem key={layer.id} disablePadding>
                    <ListItemButton onClick={() => onSelectLayer(layer.id)}>
                        <ListItemIcon>
                            {layer.type === 'VECTOR' ? (
                                <LayersOutlinedIcon fontSize="small" />
                            ) : (
                                <PhotoIcon fontSize="small" />
                            )}
                        </ListItemIcon>
                        <ListItemText
                            primary={layer.name}
                            secondary={layer.type === 'VECTOR' ? 'Векторный' : 'Растровый'}
                        />
                    </ListItemButton>
                    <Box sx={{ display: 'flex', gap: 1 }}>
                        <Tooltip title={layer.visible ? 'Скрыть слой' : 'Показать слой'}>
                            <IconButton size="small" onClick={() => onToggleVisibility(layer.id)}>
                                {layer.visible ? (
                                    <VisibilityIcon fontSize="small" />
                                ) : (
                                    <VisibilityOffIcon fontSize="small" />
                                )}
                            </IconButton>
                        </Tooltip>
                        <Tooltip title="Удалить слой">
                            <IconButton size="small" sx={{ color: 'error.main' }} onClick={() => onDeleteLayer(layer.id)}>
                                <DeleteIcon fontSize="small" />
                            </IconButton>
                        </Tooltip>
                    </Box>
                </ListItem>
            ))}
        </List>
    );
};

export default LayerList;