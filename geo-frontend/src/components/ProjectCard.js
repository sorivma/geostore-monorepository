import React from 'react';
import {
    Table,
    TableHead,
    TableBody,
    TableRow,
    TableCell,
    Paper,
    Typography,
    TableContainer,
    Button,
    Skeleton
} from '@mui/material';

const ProjectTable = ({ title, projects }) => {
    const isLoading = !projects;

    return (
        <>
            <Typography variant="h5" sx={{ mt: 4, mb: 2 }}>
                {title}
            </Typography>
            <TableContainer component={Paper} sx={{ mb: 4 }}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Название</TableCell>
                            <TableCell>Описание</TableCell>
                            <TableCell>Владелец</TableCell>
                            <TableCell>Дата создания</TableCell>
                            <TableCell align="right">Действия</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {(isLoading ? Array.from({ length: 3 }) : projects).map((project, i) => (
                            <TableRow key={project?.id || i}>
                                <TableCell>
                                    {project?.name || <Skeleton width="80%" />}
                                </TableCell>
                                <TableCell>
                                    {project?.description || <Skeleton width="60%" />}
                                </TableCell>
                                <TableCell>
                                    {project?.owner || <Skeleton width="40%" />}
                                </TableCell>
                                <TableCell>
                                    {project?.createdAt
                                        ? new Date(project.createdAt).toLocaleDateString()
                                        : <Skeleton width="30%" />}
                                </TableCell>
                                <TableCell align="right">
                                    {project ? (
                                        <Button size="small" href={`/projects/${project.id}`}>
                                            Открыть
                                        </Button>
                                    ) : (
                                        <Skeleton width="40%" />
                                    )}
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>
        </>
    );
};

export default ProjectTable;