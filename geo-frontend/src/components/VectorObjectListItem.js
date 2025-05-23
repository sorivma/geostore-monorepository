import React from 'react';
import {
    ListItemButton,
    ListItemText,
    IconButton,
    Tooltip,
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';

const VectorObjectListItem = ({ object, onCenter, onDelete }) => {
    return (
        <ListItemButton onClick={() => onCenter(object)} divider>
            <ListItemText
                primary={`${object.geometry.type}`}
                secondary={`ID: ${object.objectId}`}
                primaryTypographyProps={{ variant: 'subtitle2' }}
                secondaryTypographyProps={{ variant: 'caption' }}
            />
            <Tooltip title="Удалить объект">
                <IconButton
                    edge="end"
                    size="small"
                    color="error"
                    onClick={(e) => {
                        e.stopPropagation();
                        onDelete(object.objectId);
                    }}
                >
                    <DeleteIcon fontSize="small" />
                </IconButton>
            </Tooltip>
        </ListItemButton>
    );
};

export default VectorObjectListItem;