import React, { useEffect, useState } from 'react';
import { Snackbar, Alert, LinearProgress } from '@mui/material';

function SnackbarWithTimer({ message, onClose, severity = 'success', duration = 4000 }) {
    const [progress, setProgress] = useState(0);
    const [open, setOpen] = useState(false);

    useEffect(() => {
        if (!message) return;

        setProgress(0);
        setOpen(true);

        const interval = 50;
        const step = (interval / duration) * 100;

        const timer = setInterval(() => {
            setProgress((prev) => {
                const next = prev + step;
                if (next >= 100) {
                    clearInterval(timer);
                    setOpen(false);
                }
                return next;
            });
        }, interval);

        return () => clearInterval(timer);
    }, [message, duration]);

    const handleExited = () => {
        onClose?.();
    };

    return (
        <Snackbar
            open={open}
            onClose={() => setOpen(false)}
            TransitionProps={{ onExited: handleExited }}
        >
            <Alert
                severity={severity}
                onClose={() => setOpen(false)}
                sx={{ width: '100%', position: 'relative', pr: 5 }}
            >
                {message}
                <LinearProgress
                    variant="determinate"
                    value={progress}
                    sx={{
                        position: 'absolute',
                        bottom: 0,
                        left: 0,
                        right: 0,
                        height: 2,
                    }}
                />
            </Alert>
        </Snackbar>
    );
}

export default SnackbarWithTimer;