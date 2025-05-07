import React, { useState } from 'react';
import {
    AppBar,
    Toolbar,
    Typography,
    Box,
    IconButton,
    Menu,
    MenuItem,
    Avatar,
    Tooltip,
    Divider,
    ListItemText,
    ListItemIcon
} from '@mui/material';
import LogoutIcon from '@mui/icons-material/Logout';
import { useAuth } from 'react-oidc-context';

const Header = () => {
    const auth = useAuth();
    const [anchorEl, setAnchorEl] = useState(null);

    const open = Boolean(anchorEl);
    const handleClick = (event) => setAnchorEl(event.currentTarget);
    const handleClose = () => setAnchorEl(null);
    const handleLogout = () => {
        handleClose();
        auth.signoutRedirect();
    };

    const profile = auth.user?.profile;
    const username = profile?.preferred_username || profile?.email || profile?.sub;
    const email = profile?.email;

    return (
        <AppBar position="static" color="primary">
            <Toolbar>
                <Typography variant="h6" sx={{ flexGrow: 1 }}>
                    🌍 Geo Platform
                </Typography>

                {auth.isAuthenticated && (
                    <Box sx={{ flexGrow: 0 }}>
                        <Tooltip title="Открыть меню профиля">
                            <IconButton onClick={handleClick} sx={{ p: 0 }}>
                                <Avatar alt={username} src="/static/images/avatar/1.jpg" />
                            </IconButton>
                        </Tooltip>
                        <Menu
                            anchorEl={anchorEl}
                            open={open}
                            onClose={handleClose}
                            onClick={handleClose}
                            PaperProps={{
                                elevation: 4,
                                sx: {
                                    mt: 1.5,
                                    minWidth: 220,
                                    overflow: 'visible',
                                    filter: 'drop-shadow(0px 4px 12px rgba(0,0,0,0.15))',
                                    '& .MuiAvatar-root': {
                                        width: 32,
                                        height: 32,
                                        ml: -0.5,
                                        mr: 1,
                                    },
                                    '&:before': {
                                        content: '""',
                                        display: 'block',
                                        position: 'absolute',
                                        top: 0,
                                        right: 14,
                                        width: 10,
                                        height: 10,
                                        bgcolor: 'background.paper',
                                        transform: 'translateY(-50%) rotate(45deg)',
                                        zIndex: 0,
                                    },
                                },
                            }}
                            anchorOrigin={{
                                vertical: 'bottom',
                                horizontal: 'right',
                            }}
                            transformOrigin={{
                                vertical: 'top',
                                horizontal: 'right',
                            }}
                        >
                            <MenuItem disabled>
                                <Avatar /> <ListItemText primary={username} secondary={email} />
                            </MenuItem>
                            <Divider />
                            <MenuItem onClick={handleLogout}>
                                <ListItemIcon>
                                    <LogoutIcon fontSize="small" />
                                </ListItemIcon>
                                Выйти
                            </MenuItem>
                        </Menu>
                    </Box>
                )}
            </Toolbar>
        </AppBar>
    );
};

export default Header;
