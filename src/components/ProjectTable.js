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
    IconButton,
    Skeleton,
    TablePagination,
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';

const ProjectTable = ({
                          projects = [],
                          isLoading = false,
                          page = 0,
                          rowsPerPage = 5,
                          onPageChange = () => {},
                          onRowsPerPageChange = () => {},
                          onDelete = () => {},
                          onOpen = () => {},
                      }) => {
    const paginated = projects.slice(
        page * rowsPerPage,
        page * rowsPerPage + rowsPerPage
    );

    const renderRow = (project, index) => (
        <TableRow
            key={project.id || index}
            hover
            sx={{ cursor: 'pointer' }}
            onClick={() => onOpen(project.id)}
        >
            <TableCell>{project.name}</TableCell>
            <TableCell>{project.description || '—'}</TableCell>
            <TableCell>{project.ownerId}</TableCell>
            <TableCell>{project.id}</TableCell>
            <TableCell align="right" onClick={(e) => e.stopPropagation()}>
                <IconButton
                    size="small"
                    onClick={() => onDelete(project.id)}
                    sx={{
                        color: 'error.main',
                        '&:hover': { bgcolor: 'error.light', color: 'white' },
                    }}
                >
                    <DeleteIcon />
                </IconButton>
            </TableCell>
        </TableRow>
    );

    const renderSkeletonRow = (i) => (
        <TableRow key={`skeleton-${i}`}>
            {[...Array(5)].map((_, j) => (
                <TableCell key={j}><Skeleton width="80%" /></TableCell>
            ))}
        </TableRow>
    );

    const renderPlaceholder = () => (
        <TableRow>
            <TableCell colSpan={5} align="center">
                <Typography variant="body1" sx={{ py: 3, color: 'text.secondary' }}>
                    Нет проектов для отображения.
                </Typography>
            </TableCell>
        </TableRow>
    );

    return (
        <Paper sx={{ mt: 2 }}>
            <TableContainer>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Название</TableCell>
                            <TableCell>Описание</TableCell>
                            <TableCell>ID владельца</TableCell>
                            <TableCell>ID проекта</TableCell>
                            <TableCell align="right">Действия</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {isLoading
                            ? Array.from({ length: rowsPerPage }).map((_, i) => renderSkeletonRow(i))
                            : (projects.length > 0
                                ? paginated.map(renderRow)
                                : renderPlaceholder())}
                    </TableBody>
                </Table>
            </TableContainer>
            {!isLoading && (
                <TablePagination
                    component="div"
                    count={projects.length}
                    page={page}
                    onPageChange={onPageChange}
                    rowsPerPage={rowsPerPage}
                    onRowsPerPageChange={onRowsPerPageChange}
                    rowsPerPageOptions={[5, 10, 25]}
                />
            )}
        </Paper>
    );
};

export default ProjectTable;