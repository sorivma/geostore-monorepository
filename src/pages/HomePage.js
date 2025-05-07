import React, { useEffect, useMemo, useState } from 'react';
import {
    Container,
    Typography,
    TextField,
    ToggleButton,
    ToggleButtonGroup,
    Box,
    IconButton,
    Tooltip,
} from '@mui/material';
import { useAuth } from 'react-oidc-context';
import AddIcon from '@mui/icons-material/Add';

import {
    fetchOwnedProjects,
    fetchVisibleProjects,
    createProject,
    deleteProject,
} from '../api/projects';

import ProjectTable from '../components/ProjectTable';
import CreateProjectDialog from '../components/CreateProjectDialog';
import SnackbarWithTimer from '../components/SnackbarWithTimer';
import {useNavigate} from "react-router-dom";

const HomePage = () => {
    const auth = useAuth();
    const token = auth.user?.access_token;

    const [owned, setOwned] = useState([]);
    const [visible, setVisible] = useState([]);
    const [filter, setFilter] = useState('');
    const [ownershipFilter, setOwnershipFilter] = useState('all');
    const [loading, setLoading] = useState(true);
    const [openDialog, setOpenDialog] = useState(false);

    const [successMessage, setSuccessMessage] = useState(null);
    const [error, setError] = useState(null);
    const [snackbarId, setSnackbarId] = useState(0);

    const [page, setPage] = useState(0);
    const [rowsPerPage, setRowsPerPage] = useState(5);

    const showSuccess = (msg) => {
        setSuccessMessage(msg);
        setSnackbarId((id) => id + 1);
    };

    const showError = (msg) => {
        setError(msg);
        setSnackbarId((id) => id + 1);
    };

    useEffect(() => {
        if (!token) return;

        (async () => {
            try {
                setLoading(true);
                const ownedRaw = await fetchOwnedProjects(token);
                const visibleRaw = await fetchVisibleProjects(token);

                const ownedIds = new Set(ownedRaw.map((p) => p.id));
                const visibleFiltered = visibleRaw
                    .filter((p) => !ownedIds.has(p.id))
                    .map((p) => ({ ...p, isOwned: false }));
                const ownedMapped = ownedRaw.map((p) => ({
                    ...p,
                    isOwned: true,
                }));

                setOwned(ownedMapped);
                setVisible(visibleFiltered);
            } catch (err) {
                showError(err.message);
            } finally {
                setLoading(false);
            }
        })();
    }, [token]);

    const allProjects = useMemo(() => [...owned, ...visible], [owned, visible]);

    const filteredProjects = useMemo(() => {
        return allProjects.filter((p) => {
            if (ownershipFilter === 'owned' && !p.isOwned) return false;
            if (ownershipFilter === 'visible' && p.isOwned) return false;
            return p.name.toLowerCase().includes(filter.toLowerCase());
        });
    }, [allProjects, filter, ownershipFilter]);

    const handleCreateProject = async (name, description) => {
        try {
            const newProject = await createProject(token, name, description);
            setOwned((prev) => [...prev, { ...newProject, isOwned: true }]);
            showSuccess('Проект успешно создан');
        } catch (err) {
            showError(err.message);
        }
    };

    const handleDeleteProject = async (id) => {
        try {
            await deleteProject(token, id);
            setOwned((prev) => prev.filter((p) => p.id !== id));
            showSuccess('Проект удалён');
        } catch (err) {
            showError(err.message);
        }
    };

    const navigate = useNavigate();

    const handleOpenProject = (id) => {
        navigate(`/projects/${id}/map`);
    };

    useEffect(() => setPage(0), [filter, ownershipFilter]);

    return (
        <Container>
            <Typography variant="h4" sx={{ mt: 4, mb: 3 }}>
                Геопроекты
            </Typography>

            <Box
                sx={{
                    display: 'flex',
                    gap: 2,
                    flexWrap: 'wrap',
                    alignItems: 'center',
                    mb: 2,
                    justifyContent: 'space-between',
                }}
            >
                <Box
                    sx={{
                        display: 'flex',
                        gap: 2,
                        flexWrap: 'wrap',
                        alignItems: 'center',
                    }}
                >
                    <TextField
                        size="small"
                        label="Поиск по названию"
                        value={filter}
                        onChange={(e) => setFilter(e.target.value)}
                    />
                    <ToggleButtonGroup
                        size="small"
                        exclusive
                        value={ownershipFilter}
                        onChange={(e, v) => v && setOwnershipFilter(v)}
                    >
                        <ToggleButton value="all">Все</ToggleButton>
                        <ToggleButton value="owned">Мои</ToggleButton>
                        <ToggleButton value="visible">Доступные</ToggleButton>
                    </ToggleButtonGroup>
                </Box>

                <Tooltip title="Создать проект">
                    <IconButton onClick={() => setOpenDialog(true)} color="primary">
                        <AddIcon />
                    </IconButton>
                </Tooltip>
            </Box>

            <ProjectTable
                projects={filteredProjects}
                isLoading={loading}
                page={page}
                rowsPerPage={rowsPerPage}
                onPageChange={(_, newPage) => setPage(newPage)}
                onRowsPerPageChange={(e) => {
                    setRowsPerPage(parseInt(e.target.value, 10));
                    setPage(0);
                }}
                onDelete={handleDeleteProject}
                onOpen={handleOpenProject}
            />

            <CreateProjectDialog
                open={openDialog}
                onClose={() => setOpenDialog(false)}
                onCreate={handleCreateProject}
            />

            <SnackbarWithTimer
                key={`success-${snackbarId}`}
                message={successMessage}
                onClose={() => setSuccessMessage(null)}
                severity="success"
                duration={4000}
            />

            <SnackbarWithTimer
                key={`error-${snackbarId}`}
                message={error}
                onClose={() => setError(null)}
                severity="error"
                duration={6000}
            />
        </Container>
    );
};

export default HomePage;