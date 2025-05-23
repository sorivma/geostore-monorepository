import { useEffect, useState } from 'react';
import { fetchRasterLayerTileJson } from '../api/layers';

export function useTileJsonAvailability(layerId, token) {
    const [hasTileJson, setHasTileJson] = useState(null);

    useEffect(() => {
        if (!layerId || !token) return;

        let cancelled = false;

        const check = async () => {
            try {
                const data = await fetchRasterLayerTileJson(token, layerId);
                if (!cancelled) {
                    const hasTiles = Array.isArray(data?.tiles) && data.tiles.length > 0;
                    setHasTileJson(hasTiles);
                }
            } catch (e) {
                if (!cancelled) {
                    setHasTileJson(false);
                }
            }
        };

        check();
        return () => {
            cancelled = true;
        };
    }, [layerId, token]);

    return hasTileJson;
}