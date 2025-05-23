export async function fetchProjectLayers(token, projectId) {
    const res = await fetch(`http://localhost:8088/projects/${projectId}/layers`, {
        method: 'GET',
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });

    if (!res.ok) {
        const text = await res.text().catch(() => '');
        throw new Error(`GET /projects/${projectId}/layers failed: ${res.status} ${text}`);
    }

    return res.json();
}

export async function fetchVectorLayerGeoJson(token, layerId, srid = 4326) {
    const url = new URL(`http://localhost:8088/vector/layers/${layerId}/objects/formatted/geojson`);
    url.searchParams.set('srid', srid);
    url.searchParams.set('includeMetadata', 'true');

    const res = await fetch(url.toString(), {
        method: 'GET',
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });

    if (!res.ok) {
        const text = await res.text().catch(() => '');
        throw new Error(`GET /vector/layers/${layerId}/objects failed: ${res.status} ${text}`);
    }

    const json = await res.json();

    console.info(json);

    return json;
}

export async function createLayer(token, projectId, payload) {
    const res = await fetch(`http://localhost:8088/projects/${projectId}/layers`, {
        method: 'POST',
        headers: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
    });

    if (!res.ok) {
        const text = await res.text().catch(() => '');
        throw new Error(`POST /projects/${projectId}/layers failed: ${res.status} ${text}`);
    }

    return res.json();
}

export async function deleteLayer(token, projectId, layerId) {
    const res = await fetch(`http://localhost:8088/projects/${projectId}/layers/${layerId}`, {
        method: 'DELETE',
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });

    if (!res.ok) {
        const text = await res.text().catch(() => '');
        throw new Error(`DELETE /projects/${projectId}/layers/${layerId} failed: ${res.status} ${text}`);
    }
}

export async function fetchVectorLayerObjects(token, layerId) {
    const url = `http://localhost:8088/vector/layers/${layerId}/objects?includeMetadata=true&format=geojson&srid=4326`;

    console.log('Request URL:', url);

    const res = await fetch(url, {
        method: 'GET',
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });

    if (!res.ok) {
        const text = await res.text();
        throw new Error(`Ошибка при загрузке объектов: ${text}`);
    }

    return res.json();
}

export async function fetchRasterLayerTileJson(token, layerId) {
    const url = `http://localhost:8088/raster/layers/${layerId}/tilejson`;

    const res = await fetch(url, {
        method: 'GET',
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });

    if (!res.ok) {
        const text = await res.text();
        throw new Error(`Ошибка при загрузке TileJSON: ${text}`);
    }

    return res.json();
}

export async function uploadGeoJson(token, layerId, formData) {
    const res = await fetch(`http://localhost:8088/vector/layer/${layerId}/import/geojson`, {
        method: 'POST',
        headers: {
            Authorization: `Bearer ${token}`,
        },
        body: formData,
    });

    if (!res.ok) {
        const text = await res.text().catch(() => '');
        throw new Error(`POST /vector/layer/${layerId}/import/geojson failed: ${res.status} ${text}`);
    }

    return res;
}

export async function deleteVectorLayerObject(token, layerId, objectId) {
    const res = await fetch(`http://localhost:8088/vector/layers/${layerId}/objects/${objectId}`, {
        method: 'DELETE',
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });

    if (!res.ok) {
        const text = await res.text().catch(() => '');
        throw new Error(`DELETE /vector/layers/${layerId}/objects/${objectId} failed: ${res.status} ${text}`);
    }

    return res
}

export async function updateVectorLayerObject(token, layerId, objectId, requestBody) {
    const res = await fetch(`http://localhost:8088/vector/layers/${layerId}/objects/${objectId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(requestBody),
    });

    if (!res.ok) {
        const text = await res.text().catch(() => '');
        throw new Error(`PUT /vector/layers/${layerId}/objects/${objectId} failed: ${res.status} ${text}`);
    }

    return res;
}

export async function uploadRasterFile(token, layerId, file) {
    const res = await fetch(`http://localhost:8088/raster/layers/${layerId}/upload`, {
        method: 'POST',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/octet-stream',
        },
        body: file,
    });

    if (!res.ok) {
        const text = await res.text().catch(() => '');
        throw new Error(`POST /raster/layers/${layerId}/upload failed: ${res.status} ${text}`);
    }

    return await res.json();
}

export async function fetchRasterTileJson(token, layerId) {
    const res = await fetch(`http://localhost:8088/raster/layers/${layerId}/tilejson`, {
        method: 'GET',
        headers: {
            Authorization: `Bearer ${token}`,
        }
    });

    if (!res.ok) {
        const text = await res.text().catch(() => '');
        throw new Error(`GET /raster/layers/${layerId}/tilejson failed: ${res.status} ${text}`);
    }

    const json = await res.json();

    const patchedTiles = json.tiles.map((url) =>
        url.replace('titiler-render-server', 'localhost:8000')
    );

    return {
        ...json,
        tiles: patchedTiles,
    };
}