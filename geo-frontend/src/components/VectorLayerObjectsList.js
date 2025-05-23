import React, { useState, useEffect } from 'react';
import { Box, List, ListItem, ListItemText, CircularProgress, Alert } from '@mui/material';
import { fetchVectorLayerObjects, uploadGeoJson, deleteVectorLayerObject } from '../api/layers';
import VectorObjectListItem from './VectorObjectListItem';
import GeoJsonDropzone from './GeoJsonDropzone';

const VectorLayerObjectsList = ({ layerId, token }) => {
    const [objects, setObjects] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [uploading, setUploading] = useState(false);

    useEffect(() => {
        const loadObjects = async () => {
            setLoading(true);
            try {
                const data = await fetchVectorLayerObjects(token, layerId);
                setObjects(Array.isArray(data) ? data : []);
                setError(null);
            } catch (err) {
                setError('Ошибка при загрузке объектов');
                console.error(err);
            } finally {
                setLoading(false);
            }
        };

        loadObjects();
    }, [layerId, token]);

    const handleDeleteObject = async (objectId) => {
        try {
            await deleteVectorLayerObject(token, layerId, objectId);
            setObjects((prev) => prev.filter((o) => o.objectId !== objectId));

            window.dispatchEvent(new CustomEvent('vectorObjectDeleted', {
                detail: { layerId, objectId },
            }));
        } catch (err) {
            setError('Ошибка при удалении объекта');
            console.error(err);
        }
    };

    const handleObjectClick = (object) => {
        window.dispatchEvent(new CustomEvent('centerMapOnObject', {
            detail: object
        }));
    };

    const handleGeoJsonUpload = async (files) => {
        const file = files[0];
        if (!file) return;

        const formData = new FormData();
        formData.append('file', file);

        setUploading(true);
        setError(null);
        try {
            const data = await uploadGeoJson(token, layerId, formData);
            setObjects(data);
        } catch (err) {
            setError('Ошибка при загрузке GeoJSON');
            console.error(err);
        } finally {
            setUploading(false);
        }
    };

    return (
        <Box sx={{ width: '100%' }}>
            {loading && <CircularProgress sx={{ display: 'block', mx: 'auto', my: 2 }} />}
            {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

            <List dense>
                {objects.length > 0 ? (
                    objects.map((object) => (
                        <VectorObjectListItem
                            key={object.objectId}
                            object={object}
                            onCenter={handleObjectClick}
                            onDelete={handleDeleteObject}
                        />
                    ))
                ) : (
                    <ListItem>
                        <ListItemText primary="Нет векторных объектов" />
                    </ListItem>
                )}
            </List>

            <GeoJsonDropzone uploading={uploading} onUpload={handleGeoJsonUpload} />
        </Box>
    );
};

export default VectorLayerObjectsList;
