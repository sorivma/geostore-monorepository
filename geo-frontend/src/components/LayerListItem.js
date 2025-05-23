import React, { useState } from 'react';
import {
    Accordion,
    AccordionSummary,
    AccordionDetails,
    ListItemIcon,
    ListItemText,
    IconButton,
    Box,
    Tooltip,
    Typography,
} from '@mui/material';
import LayersOutlinedIcon from '@mui/icons-material/LayersOutlined';
import PhotoIcon from '@mui/icons-material/Photo';
import DeleteIcon from '@mui/icons-material/Delete';
import VisibilityIcon from '@mui/icons-material/Visibility';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';

const LayerListItem = ({ layer, onDelete, onSelect, children }) => {
    const [expanded, setExpanded] = useState(false);
    const [visible, setVisible] = useState(layer.visible ?? true);

    const handleExpand = (event, isExpanded) => {
        setExpanded(isExpanded);
        event.stopPropagation();
    };

    const handleSelectLayer = () => {
        onSelect(layer.id);
    };

    const handleToggleVisibility = (e) => {
        e.stopPropagation();
        const newVisible = !visible;
        setVisible(newVisible);

        window.dispatchEvent(
            new CustomEvent('layerVisibilityToggled', {
                detail: {
                    layerId: layer.id,
                    visible: newVisible,
                },
            })
        );
    };

    return (
        <Accordion expanded={expanded} onChange={handleExpand} sx={{ mb: 1 }}>
            <AccordionSummary
                expandIcon={<ExpandMoreIcon />}
                aria-controls={`panel-${layer.id}-content`}
                id={`panel-${layer.id}-header`}
                onClick={handleSelectLayer}
            >
                <Box sx={{ display: 'flex', alignItems: 'center', width: '100%' }}>
                    <ListItemIcon sx={{ minWidth: 32 }}>
                        {layer.type === 'VECTOR' ? (
                            <LayersOutlinedIcon fontSize="small" />
                        ) : (
                            <PhotoIcon fontSize="small" />
                        )}
                    </ListItemIcon>

                    <ListItemText
                        primary={
                            <Typography variant="body2" fontWeight="medium">
                                {layer.name}
                            </Typography>
                        }
                        secondary={
                            <Typography variant="caption" color="text.secondary">
                                {layer.type === 'VECTOR' ? 'Векторный' : 'Растровый'}
                            </Typography>
                        }
                    />

                    <Box sx={{ display: 'flex', gap: 1, ml: 'auto' }}>
                        <Tooltip title={visible ? 'Скрыть слой' : 'Показать слой'}>
                            <IconButton size="small" onClick={handleToggleVisibility}>
                                {visible ? (
                                    <VisibilityIcon fontSize="small" />
                                ) : (
                                    <VisibilityOffIcon fontSize="small" />
                                )}
                            </IconButton>
                        </Tooltip>

                        <Tooltip title="Удалить слой">
                            <IconButton
                                size="small"
                                sx={{ color: 'error.main' }}
                                onClick={(e) => {
                                    e.stopPropagation();
                                    onDelete(layer.id);
                                }}
                            >
                                <DeleteIcon fontSize="small" />
                            </IconButton>
                        </Tooltip>
                    </Box>
                </Box>
            </AccordionSummary>

            <AccordionDetails>{children}</AccordionDetails>
        </Accordion>
    );
};

export default LayerListItem;