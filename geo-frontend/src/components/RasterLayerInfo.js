import React, { useEffect, useState } from 'react';
import {
    Box,
    CircularProgress,
    Typography,
    Alert,
    Paper,
    Divider,
    Button,
} from '@mui/material';
import { fetchRasterLayerTileJson } from '../api/layers';

const RasterLayerInfo = ({ layerId, token }) => {
    const [tileJson, setTileJson] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [showRaw, setShowRaw] = useState(false);

    useEffect(() => {
        const loadTileJson = async () => {
            setLoading(true);
            setError(null);
            try {
                const data = await fetchRasterLayerTileJson(token, layerId);
                setTileJson(data);
            } catch (err) {
                setError('Ошибка при загрузке TileJSON');
                console.error(err);
            } finally {
                setLoading(false);
            }
        };

        loadTileJson();
    }, [layerId, token]);

    return (
        <Box sx={{ width: '100%' }}>
            {loading && <CircularProgress sx={{ display: 'block', mx: 'auto', my: 2 }} />}
            {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

            {tileJson && (
                <Paper sx={{ p: 2 }}>
                    <Typography variant="h6">Информация о растровом слое</Typography>
                    <Divider sx={{ my: 1 }} />

                    <Typography variant="body2" gutterBottom>
                        <strong>Название:</strong> {tileJson.name}
                    </Typography>
                    <Typography variant="body2" gutterBottom>
                        <strong>Тип:</strong> {tileJson.tilejson}
                    </Typography>
                    <Typography variant="body2" gutterBottom>
                        <strong>Размеры:</strong> {tileJson.bounds?.join(', ') || '—'}
                    </Typography>
                    <Typography variant="body2" gutterBottom>
                        <strong>Минимальный зум:</strong> {tileJson.minzoom}
                    </Typography>
                    <Typography variant="body2" gutterBottom>
                        <strong>Максимальный зум:</strong> {tileJson.maxzoom}
                    </Typography>
                    <Typography variant="body2" gutterBottom>
                        <strong>Tiles URL:</strong> {tileJson.tiles?.[0] || '—'}
                    </Typography>

                    <Button
                        size="small"
                        variant="outlined"
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
                                p: 1,
                                fontSize: '0.75rem',
                                bgcolor: '#f5f5f5',
                                borderRadius: 1,
                                border: '1px solid #ccc',
                                overflow: 'auto',
                                maxHeight: 200,
                                whiteSpace: 'pre-wrap',
                            }}
                        >
                            {JSON.stringify(tileJson, null, 2)}
                        </Box>
                    )}
                </Paper>
            )}
        </Box>
    );
};

export default RasterLayerInfo;