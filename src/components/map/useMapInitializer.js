import { useRef, useEffect } from 'react';
import maplibregl from 'maplibre-gl';

export const useMapInitializer = ({ basemapVisible }) => {
    const mapRef = useRef(null);
    const map = useRef(null);

    useEffect(() => {
        map.current = new maplibregl.Map({
            container: mapRef.current,
            style: { version: 8, sources: {}, layers: [] },
            center: [37.6, 55.75],
            zoom: 4,
        });

        map.current.on('load', () => {
            map.current.addSource('basemap', {
                type: 'raster',
                tiles: ['https://a.tile.openstreetmap.org/{z}/{x}/{y}.png'],
                tileSize: 256,
            });

            map.current.addLayer({
                id: 'basemap-layer',
                type: 'raster',
                source: 'basemap',
                layout: { visibility: basemapVisible ? 'visible' : 'none' },
            });
        });

        window.addEventListener('resize', () => {
            requestAnimationFrame(() => {
                if (map.current) {
                    map.current.resize();
                }
            });
        });

        return () => map.current?.remove();
    }, [basemapVisible]);

    return { mapRef, map };
};