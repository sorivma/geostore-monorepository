import React, { useState } from 'react';
import {
    Dialog,
    DialogTitle,
    DialogContent,
    DialogActions,
    TextField,
    Button,
} from '@mui/material';

function CreateProjectDialog({ open, onClose, onCreate }) {
    const [name, setName] = useState('');
    const [description, setDescription] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async () => {
        if (!name.trim()) return;
        setLoading(true);
        try {
            await onCreate(name.trim(), description.trim());
            setName('');
            setDescription('');
            onClose();
        } finally {
            setLoading(false);
        }
    };

    const handleClose = () => {
        if (!loading) {
            setName('');
            setDescription('');
            onClose();
        }
    };

    return (
        <Dialog open={open} onClose={handleClose} fullWidth maxWidth="sm">
            <DialogTitle>Создание проекта</DialogTitle>
            <DialogContent>
                <TextField
                    fullWidth
                    required
                    label="Название проекта"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
                    margin="normal"
                />
                <TextField
                    fullWidth
                    label="Описание"
                    value={description}
                    onChange={(e) => setDescription(e.target.value)}
                    margin="normal"
                    multiline
                    rows={3}
                />
            </DialogContent>
            <DialogActions>
                <Button onClick={handleClose} disabled={loading}>
                    Отмена
                </Button>
                <Button
                    onClick={handleSubmit}
                    disabled={!name.trim() || loading}
                    variant="contained"
                >
                    Создать
                </Button>
            </DialogActions>
        </Dialog>
    );
}

export default CreateProjectDialog;