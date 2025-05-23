import React, { useState } from 'react';
import {
    Box,
    IconButton,
    InputBase,
    Paper,
    List,
    ListItem,
    ListItemText,
    Collapse,
    Tooltip
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import CloseIcon from '@mui/icons-material/Close';

const SearchField = ({ token, projectId }) => {
    const [open, setOpen] = useState(false);
    const [query, setQuery] = useState('');
    const [results, setResults] = useState([]);

    const handleSearch = async (e) => {
        const value = e.target.value;
        setQuery(value);

        if (!value.trim()) {
            setResults([]);
            return;
        }

        try {
            const response = await fetch(`http://localhost:8088/search/project/${projectId}`, {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ query: value }),
            });

            if (!response.ok) throw new Error('Ошибка при поиске');

            const data = await response.json();
            setResults(data);
        } catch (err) {
            console.error('Ошибка при поиске:', err.message);
        }
    };

    const handleSelect = (result) => {
        const { layerId, geoJsonGeometry, anyText } = result;

        const fakeFeature = {
            geometry: geoJsonGeometry,
            properties: { anyText },
        };

        window.dispatchEvent(new CustomEvent('vectorFeatureSelected', {
            detail: { feature: fakeFeature.geometry, layerId },
        }));

        window.dispatchEvent(new CustomEvent('centerMapOnObject', {
            detail: { geometry: geoJsonGeometry },
        }));

        setOpen(false);
        setQuery('');
        setResults([]);
    };

    const getSecondaryText = (anyText, geometry) => {
        const words = anyText.trim().split(/\s+/);
        const lastWord = words[words.length - 1] ?? '';
        const type = geometry?.type ?? '';
        return `${lastWord} • ${type}`;
    };

    return (
        <Box
            sx={{
                position: 'absolute',
                top: 16,
                left: '50%',
                transform: 'translateX(-50%)',
                zIndex: 2,
                width: 400,
                pointerEvents: 'none',
            }}
        >
            {open ? (
                <Paper
                    sx={{
                        width: '100%',
                        display: 'flex',
                        flexDirection: 'column',
                        p: 1,
                        boxShadow: 3,
                        pointerEvents: 'auto',
                    }}
                >
                    <Box sx={{ display: 'flex', alignItems: 'center' }}>
                        <SearchIcon sx={{ mr: 1 }} />
                        <InputBase
                            fullWidth
                            placeholder="Поиск по объектам..."
                            value={query}
                            onChange={handleSearch}
                            sx={{ flex: 1 }}
                        />
                        <IconButton onClick={() => { setOpen(false); setQuery(''); setResults([]); }}>
                            <CloseIcon />
                        </IconButton>
                    </Box>
                    <Collapse in={results.length > 0}>
                        <List dense>
                            {results.map((item, index) => (
                                <ListItem button key={`${item.layerId}-${index}`} onClick={() => handleSelect(item)}>
                                    <ListItemText
                                        primary={item.anyText}
                                        secondary={getSecondaryText(item.anyText, item.geoJsonGeometry)}
                                    />
                                </ListItem>
                            ))}
                        </List>
                    </Collapse>
                </Paper>
            ) : (
                <Box sx={{ display: 'flex', justifyContent: 'center', pointerEvents: 'auto' }}>
                    <Tooltip title="Поиск">
                        <IconButton
                            onClick={() => setOpen(true)}
                            sx={{ backgroundColor: 'white', boxShadow: 1 }}
                            size="small"
                        >
                            <SearchIcon />
                        </IconButton>
                    </Tooltip>
                </Box>
            )}
        </Box>
    );
};

export default SearchField;