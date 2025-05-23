import React, { useState, useEffect } from 'react';
import {
    Box,
    Paper,
    Typography,
    Divider,
    IconButton,
    Slide,
    List,
    ListItem,
    ListItemText,
    Button,
} from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import Editor from 'react-simple-code-editor';
import Prism from 'prismjs';
import 'prismjs/components/prism-json';
import 'prismjs/themes/prism.css';

import isEqual from 'lodash.isequal';
import { updateVectorLayerObject } from '../api/layers';

const FeatureCard = ({ feature, onClose, token }) => {
    const [jsonText, setJsonText] = useState('');
    const [original, setOriginal] = useState({});
    const [validJson, setValidJson] = useState(true);
    const [showRawCoords, setShowRawCoords] = useState(false);

    useEffect(() => {
        if (feature?.properties) {
            const jsonStr = JSON.stringify(feature.properties, null, 2);
            setJsonText(jsonStr);
            setOriginal(feature.properties);
            setValidJson(true);
        }
    }, [feature]);

    if (!feature) return null;

    const { properties, geometry, __layerId } = feature;
    const objectId = properties?.objectId;

    const handleUpdate = async () => {
        try {
            const parsed = JSON.parse(jsonText);

            const payload = {
                geometry,
                format: 'geojson',
                sourceSrid: 4326,
                properties: parsed,
                region: null,
                topicCategory: null,
                temporalExtent: null,
            };

            await updateVectorLayerObject(token, __layerId, objectId, payload);
            console.log('Объект обновлён');

            window.dispatchEvent(new CustomEvent('vectorObjectUpdated', {
                detail: { layerId: __layerId },
            }));
        } catch (e) {
            console.error('Ошибка при обновлении:', e.message);
        }
    };

    const isChanged = validJson && (() => {
        try {
            return !isEqual(original, JSON.parse(jsonText));
        } catch {
            return false;
        }
    })();

    return (
        <Slide direction="up" in={!!feature} mountOnEnter unmountOnExit>
            <Paper
                elevation={6}
                sx={{
                    position: 'absolute',
                    bottom: 0,
                    left: 0,
                    right: 0,
                    maxHeight: '40%',
                    bgcolor: 'background.paper',
                    zIndex: 1200,
                    p: 2,
                    borderTopLeftRadius: 12,
                    borderTopRightRadius: 12,
                    boxShadow: 4,
                    overflowY: 'auto',
                    marginBottom: 13,
                }}
            >
                <Box display="flex" justifyContent="space-between" alignItems="center">
                    <Typography variant="h6" fontWeight={600}>Векторный объект</Typography>
                    <IconButton onClick={onClose} size="small">
                        <CloseIcon />
                    </IconButton>
                </Box>

                <Divider sx={{ my: 1 }} />

                <List dense disablePadding>
                    <ListItem disableGutters>
                        <ListItemText primary="ID" secondary={properties?.objectId || '—'} />
                    </ListItem>
                    <ListItem disableGutters>
                        <ListItemText primary="Тип геометрии" secondary={geometry?.type || '—'} />
                    </ListItem>
                </List>

                <Box mt={2}>
                    <Typography variant="caption" color="text.secondary" gutterBottom>
                        <strong>Координаты:</strong>
                    </Typography>

                    <Typography variant="body2" sx={{ mb: 1 }}>
                        {geometry?.type} — {Array.isArray(geometry?.coordinates)
                        ? `массив длины ${geometry.coordinates.length}`
                        : 'нет координат'}
                    </Typography>

                    <Button
                        size="small"
                        variant="outlined"
                        onClick={() => setShowRawCoords((v) => !v)}
                        sx={{ mb: 1 }}
                    >
                        {showRawCoords ? 'Скрыть' : 'Показать все'}
                    </Button>

                    {showRawCoords && (
                        <Box
                            component="pre"
                            sx={{
                                maxHeight: 200,
                                overflow: 'auto',
                                fontFamily: 'monospace',
                                fontSize: '0.75rem',
                                bgcolor: '#f9f9f9',
                                p: 1,
                                border: '1px solid #ccc',
                                borderRadius: 1,
                                whiteSpace: 'pre-wrap',
                                wordBreak: 'break-word',
                            }}
                        >
                            {JSON.stringify(geometry?.coordinates, null, 2)}
                        </Box>
                    )}
                </Box>

                <Box mt={2}>
                    <Typography variant="caption" color="text.secondary" gutterBottom>
                        <strong>Редактировать свойства (JSON):</strong>
                    </Typography>
                    <Editor
                        value={jsonText}
                        onValueChange={(code) => {
                            setJsonText(code);
                            try {
                                JSON.parse(code);
                                setValidJson(true);
                            } catch {
                                setValidJson(false);
                            }
                        }}
                        highlight={(code) => Prism.highlight(code, Prism.languages.json, 'json')}
                        padding={10}
                        style={{
                            fontFamily: '"Fira code", "Fira Mono", monospace',
                            fontSize: 12,
                            backgroundColor: '#f5f5f5',
                            borderRadius: 4,
                            border: validJson ? '1px solid #ccc' : '1px solid red',
                            whiteSpace: 'pre-wrap',
                            minHeight: 120,
                        }}
                    />
                    <Box mt={1} textAlign="right">
                        <Button
                            variant="contained"
                            size="small"
                            onClick={handleUpdate}
                            disabled={!isChanged}
                        >
                            Обновить
                        </Button>
                    </Box>
                </Box>
            </Paper>
        </Slide>
    );
};

export default FeatureCard;