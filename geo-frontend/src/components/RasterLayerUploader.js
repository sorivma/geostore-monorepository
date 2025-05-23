import React, { useCallback } from 'react';
import { useDropzone } from 'react-dropzone';
import { Box, Typography, CircularProgress } from '@mui/material';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import { uploadRasterFile } from '../api/layers';

const RasterLayerUploader = ({ layerId, token, onUploaded }) => {
    const [loading, setLoading] = React.useState(false);
    const onDrop = useCallback(async (acceptedFiles) => {
        if (!acceptedFiles.length) return;

        setLoading(true);
        try {
            const response = await uploadRasterFile(token, layerId, acceptedFiles[0]);
            console.log('Загружен COG:', response);
            if (onUploaded) onUploaded(response);
        } catch (e) {
            console.error('Ошибка загрузки:', e.message);
        } finally {
            setLoading(false);
        }
    }, [token, layerId, onUploaded]);

    const { getRootProps, getInputProps, isDragActive } = useDropzone({
        onDrop,
        multiple: false,
        accept: {
            'image/tiff': ['.tif', '.tiff'],
        },
    });

    return (
        <Box
            {...getRootProps()}
            sx={{
                border: '2px dashed #aaa',
                borderRadius: 2,
                p: 2,
                textAlign: 'center',
                bgcolor: isDragActive ? 'action.hover' : 'background.paper',
                cursor: 'pointer',
                mt: 1,
            }}
        >
            <input {...getInputProps()} />
            {loading ? (
                <CircularProgress size={24} />
            ) : (
                <>
                    <CloudUploadIcon sx={{ fontSize: 32, color: 'text.secondary' }} />
                    <Typography variant="body2" color="text.secondary">
                        Перетащите GeoTIFF или нажмите, чтобы выбрать
                    </Typography>
                </>
            )}
        </Box>
    );
};

export default RasterLayerUploader;