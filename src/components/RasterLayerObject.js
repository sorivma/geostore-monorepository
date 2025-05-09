import React, { useEffect, useState } from 'react';
import {
    Box,
    Typography,
    Button,
    CircularProgress,
    Alert,
} from '@mui/material';
import { useTileJsonAvailability } from '../hooks/useTileJsonAvailability';
import { fetchRasterLayerTileJson } from '../api/layers';
import RasterLayerUploader from './RasterLayerUploader';

const RasterLayerObject = ({ layerId, selectedLayerId, token }) => {
    const tileJsonExists = useTileJsonAvailability(layerId, token);
    const [tileJson, setTileJson] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [showRaw, setShowRaw] = useState(false);

    useEffect(() => {
        if (tileJsonExists) {
            setLoading(true);
            setError(null);
            fetchRasterLayerTileJson(token, layerId)
                .then(setTileJson)
                .catch((e) => {
                    console.error(e);
                    setError('Ошибка при получении TileJSON');
                })
                .finally(() => setLoading(false));
        }
    }, [tileJsonExists, layerId, token]);

    if (selectedLayerId !== layerId) return null;

    if (tileJsonExists === null) {
        return (
            <Typography variant="body2" color="text.secondary" sx={{ ml: 2 }}>
                Проверка наличия TileJSON...
            </Typography>
        );
    }

    if (tileJsonExists === false) {
        return (
            <RasterLayerUploader
                key={layerId}
                layerId={layerId}
                token={token}
                onUploaded={(response) => {
                    console.log('TileJSON загружен:', response.tileJsonUrl);
                }}
            />
        );
    }

    return (
        <Box sx={{ p: 1, bgcolor: '#f9f9f9', borderRadius: 1, border: '1px solid #ccc', mt: 1 }}>
            {loading && <CircularProgress size={20} />}
            {error && <Alert severity="error">{error}</Alert>}

            {tileJson && !loading && (
                <>
                    <Typography variant="body2"><strong>Название:</strong> {tileJson.name}</Typography>
                    <Typography variant="body2"><strong>Тип:</strong> {tileJson.tilejson}</Typography>
                    <Typography variant="body2"><strong>Минимальный зум:</strong> {tileJson.minzoom}</Typography>
                    <Typography variant="body2"><strong>Максимальный зум:</strong> {tileJson.maxzoom}</Typography>
                    <Typography variant="body2"><strong>Bounds:</strong> {tileJson.bounds?.join(', ')}</Typography>
                    
                    <Button
                        variant="outlined"
                        size="small"
                        sx={{ mt: 1 }}
                        onClick={() => setShowRaw((v) => !v)}
                    >
                        {showRaw ? 'Скрыть TileJSON' : 'Показать TileJSON'}
                    </Button>

                    {showRaw && (
                        <Box
                            component="pre"
                            sx={{
                                mt: 1,
                                maxHeight: 200,
                                overflow: 'auto',
                                fontFamily: 'monospace',
                                fontSize: '0.75rem',
                                backgroundColor: '#fff',
                                border: '1px solid #ddd',
                                borderRadius: 1,
                                p: 1,
                                whiteSpace: 'pre-wrap',
                            }}
                        >
                            {JSON.stringify(tileJson, null, 2)}
                        </Box>
                    )}
                </>
            )}
        </Box>
    );
};

export default RasterLayerObject;