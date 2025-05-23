import React from 'react';
import { useDropzone } from 'react-dropzone';
import { Box, Button } from '@mui/material';

const GeoJsonDropzone = ({ uploading, onUpload }) => {
    const { getRootProps, getInputProps } = useDropzone({
        accept: { 'application/geo+json': ['.geojson'] },
        onDrop: onUpload,
    });

    return (
        <Box
            {...getRootProps()}
            sx={{
                border: '2px dashed',
                borderColor: 'primary.main',
                borderRadius: 2,
                p: 2,
                mt: 2,
                textAlign: 'center',
                bgcolor: 'background.paper',
                cursor: 'pointer',
            }}
        >
            <input {...getInputProps()} />
            <Button variant="contained" disabled={uploading}>
                {uploading ? 'Загрузка...' : 'Загрузить .geojson'}
            </Button>
        </Box>
    );
};

export default GeoJsonDropzone;