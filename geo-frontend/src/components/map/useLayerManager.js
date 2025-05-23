import { useEffect } from 'react';
import { fetchVectorLayerGeoJson, fetchRasterTileJson } from '../../api/layers';

export const useLayerManager = (map, layers, token) => {
    useEffect(() => {
        if (!map.current) return;

        layers.forEach((layer) => {
            if (layer.type === 'VECTOR') {
                const sourceId = `source-${layer.id}`;
                const fillLayerId = `layer-${layer.id}-fill`;
                const lineLayerId = `layer-${layer.id}-line`;
                const pointLayerId = `layer-${layer.id}-point`;
                const highlightId = `highlight-${layer.id}`;

                fetchVectorLayerGeoJson(token, layer.id).then((geojson) => {
                    if (!map.current.getSource(sourceId)) {
                        map.current.addSource(sourceId, { type: 'geojson', data: geojson });

                        map.current.addLayer({
                            id: fillLayerId,
                            type: 'fill',
                            source: sourceId,
                            filter: ['==', '$type', 'Polygon'],
                            layout: {
                                visibility: layer.visible === false ? 'none' : 'visible',
                            },
                            paint: {
                                'fill-color': '#00bcd4',
                                'fill-opacity': 0.4,
                            },
                        });

                        map.current.addLayer({
                            id: lineLayerId,
                            type: 'line',
                            source: sourceId,
                            filter: ['==', '$type', 'LineString'],
                            layout: {
                                visibility: layer.visible === false ? 'none' : 'visible',
                            },
                            paint: {
                                'line-color': '#00bcd4',
                                'line-width': 2,
                            },
                        });

                        map.current.addLayer({
                            id: pointLayerId,
                            type: 'circle',
                            source: sourceId,
                            filter: ['==', '$type', 'Point'],
                            layout: {
                                visibility: layer.visible === false ? 'none' : 'visible',
                            },
                            paint: {
                                'circle-radius': 6,
                                'circle-color': '#00bcd4',
                            },
                        });

                        map.current.addLayer({
                            id: highlightId,
                            type: 'line',
                            source: sourceId,
                            layout: {},
                            paint: {
                                'line-color': '#ff4081',
                                'line-width': 2,
                            },
                            filter: ['==', ['get', 'objectId'], ''],
                        });

                        [fillLayerId, lineLayerId, pointLayerId].forEach((clickLayerId) => {
                            map.current.on('click', clickLayerId, (e) => {
                                if (!e.features || e.features.length === 0) return;
                                const feature = e.features[0];
                                window.dispatchEvent(new CustomEvent('vectorFeatureSelected', {
                                    detail: { feature, layerId: layer.id },
                                }));
                            });
                        });
                    }
                }).catch((err) => {
                    console.warn(`Не удалось загрузить GeoJSON для слоя ${layer.id}`, err.message);
                });
            }

            if (layer.type === 'RASTER') {
                fetchRasterTileJson(token, layer.id)
                    .then((tilejson) => {
                        const sourceId = `raster-source-${layer.id}`;
                        const layerId = `raster-layer-${layer.id}`;

                        if (!map.current.getSource(sourceId)) {
                            map.current.addSource(sourceId, {
                                type: 'raster',
                                tiles: tilejson.tiles,
                                tileSize: tilejson.tileSize || 256,
                                bounds: tilejson.bounds,
                            });

                            map.current.addLayer({
                                id: layerId,
                                type: 'raster',
                                source: sourceId,
                                paint: {
                                    'raster-brightness-min': 0.7,
                                    'raster-brightness-max': 1.0,
                                    'raster-contrast': 0.8,
                                    'raster-saturation': 0.0,
                                    'raster-opacity': 1
                                },
                                layout: {
                                    visibility: layer.visible === false ? 'none' : 'visible',
                                },
                            });
                        }
                    })
                    .catch((err) => {
                        console.warn(`Не удалось загрузить tilejson для слоя ${layer.id}:`, err.message);
                    });
            }
        });
    }, [map, layers, token]);
};