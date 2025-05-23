export async function fetchOwnedProjects(token) {
    const res = await fetch('http://localhost:8088/projects/owned', {
        method: 'GET',
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });

    if (!res.ok) {
        const text = await res.text().catch(() => '');
        throw new Error(`GET /projects/owned failed: ${res.status} ${text}`);
    }

    return res.json();
}

export async function fetchVisibleProjects(token) {
    const res = await fetch('http://localhost:8088/projects/visible', {
        method: 'GET',
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });

    if (!res.ok) {
        const text = await res.text().catch(() => '');
        throw new Error(`GET /projects/visible failed: ${res.status} ${text}`);
    }

    return res.json();
}

export async function createProject(token, name, description) {
    const createRes = await fetch('http://localhost:8088/projects', {
        method: 'POST',
        headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ name, description }),
    });

    if (!createRes.ok) {
        const text = await createRes.text().catch(() => '');
        throw new Error(`POST /projects failed: ${createRes.status} ${text}`);
    }

    const { id } = await createRes.json();

    const getRes = await fetch(`http://localhost:8088/projects/${id}`, {
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });

    if (!getRes.ok) {
        const text = await getRes.text().catch(() => '');
        throw new Error(`GET /projects/${id} failed: ${getRes.status} ${text}`);
    }

    return getRes.json();
}

export async function deleteProject(token, id) {
    const res = await fetch(`http://localhost:8088/projects/${id}`, {
        method: 'DELETE',
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });

    if (!res.ok) {
        const text = await res.text().catch(() => '');
        throw new Error(`DELETE /projects/${id} failed: ${res.status} ${text}`);
    }
}