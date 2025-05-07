import { Box, CircularProgress, Typography } from '@mui/material';

export default function PageLoader() {
    return (
        <Box
            sx={{
                height: '100vh',
                display: 'flex',
                flexDirection: 'column',
                gap: 2,
                justifyContent: 'center',
                alignItems: 'center',
                backgroundColor: '#f5f5f5',
            }}
        >
            <CircularProgress size={60} />
            <Typography variant="h6" color="textSecondary">
                Приложение загружается
            </Typography>
        </Box>
    );
}