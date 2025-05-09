import React, { useState } from 'react';
import {
    TextField,
    MenuItem,
    Button,
    Box,
    Accordion,
    AccordionSummary,
    AccordionDetails,
    Typography
} from '@mui/material';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';

const LayerCreateForm = ({ expanded, onToggleExpanded, onCreate, order }) => {
    const [form, setForm] = useState({ name: '', type: 'VECTOR' });

    const handleSubmit = () => {
        if (!form.name.trim()) return;
        onCreate({
            name: form.name,
            type: form.type,
            geometryType: 'POLYGON',
            order,
        });
        setForm({ name: '', type: 'VECTOR' });
    };

    return (
        <Accordion expanded={expanded} onChange={onToggleExpanded} sx={{ mb: 1 }}>
            <AccordionSummary
                expandIcon={<ExpandMoreIcon />}
                aria-controls="panel1a-content"
                id="panel1a-header"
            >
                <Typography variant="body2" fontWeight="medium">
                    {expanded ? 'Отмена' : 'Добавить слой'}
                </Typography>
            </AccordionSummary>
            <AccordionDetails>
                <Box sx={{ px: 2, py: 1 }}>
                    <TextField
                        label="Название слоя"
                        size="small"
                        fullWidth
                        value={form.name}
                        onChange={(e) => setForm({ ...form, name: e.target.value })}
                        sx={{ mb: 1 }}
                    />
                    <TextField
                        label="Тип слоя"
                        size="small"
                        fullWidth
                        select
                        value={form.type}
                        onChange={(e) => setForm({ ...form, type: e.target.value })}
                    >
                        <MenuItem value="VECTOR">Векторный</MenuItem>
                        <MenuItem value="RASTER">Растровый</MenuItem>
                    </TextField>

                    <Button
                        variant="contained"
                        size="small"
                        fullWidth
                        sx={{ mt: 1 }}
                        onClick={handleSubmit}
                    >
                        Создать
                    </Button>
                </Box>
            </AccordionDetails>
        </Accordion>
    );
};

export default LayerCreateForm;