import { useEffect } from 'react';
import { fetchVectorLayerGeoJson } from '../../api/layers';
import maplibregl from 'maplibre-gl';

export const useEventHandlers = ({ map, token, layers, setSelectedFeature }) => {
    useEffect(() => {
        const onFeatureSelected = async (event) => {
            const { feature, layerId } = event.detail;
            const highlightLayerId = `highlight-${layerId}`;
            const objectId = feature.properties?.objectId;

            if (objectId && map.current?.getLayer(highlightLayerId)) {
                map.current.setFilter(highlightLayerId, ['==', ['get', 'objectId'], objectId]);
            }

            const geojson = await fetchVectorLayerGeoJson(token, layerId);
            const fullFeature = geojson.features.find(f => f.properties?.objectId === objectId);

            if (fullFeature) {
                setSelectedFeature({ ...fullFeature, __layerId: layerId });
            }
        };

        const onVisibilityToggle = (event) => {
            const { layerId, visible } = event.detail;
            const visibility = visible ? 'visible' : 'none';

            const vectorLayerIds = [
                `layer-${layerId}-fill`,
                `layer-${layerId}-line`,
                `layer-${layerId}-point`,
                `highlight-${layerId}`,
            ];

            vectorLayerIds.forEach(id => {
                if (map.current.getLayer(id)) {
                    map.current.setLayoutProperty(id, 'visibility', visibility);
                }
            });

            const rasterId = `raster-layer-${layerId}`;
            if (map.current.getLayer(rasterId)) {
                map.current.setLayoutProperty(rasterId, 'visibility', visibility);
            }
        };

        const onVectorUpdate = async (event) => {
            const { layerId } = event.detail;
            const sourceId = `source-${layerId}`;
            const source = map.current.getSource(sourceId);
            if (source?.setData) {
                const updated = await fetchVectorLayerGeoJson(token, layerId);
                source.setData(updated);
            }
        };

        const getAllCoordinates = (geom) => {
            if (!geom) return [];

            switch (geom.type) {
                case 'Point':
                    return [geom.coordinates];
                case 'MultiPoint':
                case 'LineString':
                    return geom.coordinates;
                case 'MultiLineString':
                case 'Polygon':
                    return geom.coordinates.flat();
                case 'MultiPolygon':
                    return geom.coordinates.flatMap(polygon => polygon.flatMap(ring => ring));
                case 'GeometryCollection':
                    return geom.geometries.flatMap(getAllCoordinates);
                default:
                    return [];
            }
        };

        const onCenterMap = (event) => {
            const geometry = event.detail.geometry;
            if (!map.current || !geometry) return;

            const coordinates = getAllCoordinates(geometry);
            if (!coordinates || coordinates.length === 0) return;

            if (geometry.type === 'Point') {
                const [lng, lat] = coordinates[0];
                map.current.flyTo({ center: [lng, lat], zoom: 12, speed: 1, curve: 1 });
            } else {
                const bounds = coordinates.reduce(
                    (b, [lng, lat]) => b.extend([lng, lat]),
                    new maplibregl.LngLatBounds()
                );
                map.current.fitBounds(bounds, { padding: 20, maxZoom: 12, speed: 1 });
            }
        };

        const onEscape = (e) => {
            if (e.key === 'Escape') {
                layers.forEach((layer) => {
                    const highlightId = `highlight-${layer.id}`;
                    if (map.current?.getLayer(highlightId)) {
                        map.current.setFilter(highlightId, ['==', ['get', 'objectId'], '']);
                    }
                });
                setSelectedFeature(null);
            }
        };

        const onBasemapVisibilityChange = (event) => {
            const { visible } = event.detail;
            const basemapLayerId = 'basemap-layer';

            if (map.current?.getLayer(basemapLayerId)) {
                map.current.setLayoutProperty(basemapLayerId, 'visibility', visible ? 'visible' : 'none');
            }
        };

        const onRemoveLayer = (event) => {
            const { layerId } = event.detail;
            const mapInstance = map.current;

            [
                `layer-${layerId}-fill`,
                `layer-${layerId}-line`,
                `layer-${layerId}-point`,
                `highlight-${layerId}`,
                `raster-layer-${layerId}`,
            ].forEach(id => {
                if (mapInstance.getLayer(id)) {
                    mapInstance.removeLayer(id);
                }
            });

            [
                `source-${layerId}`,
                `raster-source-${layerId}`,
            ].forEach(id => {
                if (mapInstance.getSource(id)) {
                    mapInstance.removeSource(id);
                }
            });
        };

        window.addEventListener('vectorFeatureSelected', onFeatureSelected);
        window.addEventListener('layerVisibilityToggled', onVisibilityToggle);
        window.addEventListener('vectorObjectUpdated', onVectorUpdate);
        window.addEventListener('vectorObjectDeleted', onVectorUpdate);
        window.addEventListener('centerMapOnObject', onCenterMap);
        window.addEventListener('keydown', onEscape);
        window.addEventListener('updateBasemapVisibility', onBasemapVisibilityChange);
        window.addEventListener('removeLayer', onRemoveLayer);

        return () => {
            window.removeEventListener('vectorFeatureSelected', onFeatureSelected);
            window.removeEventListener('layerVisibilityToggled', onVisibilityToggle);
            window.removeEventListener('vectorObjectUpdated', onVectorUpdate);
            window.removeEventListener('vectorObjectDeleted', onVectorUpdate);
            window.removeEventListener('centerMapOnObject', onCenterMap);
            window.removeEventListener('keydown', onEscape);
            window.removeEventListener('updateBasemapVisibility', onBasemapVisibilityChange);
            window.removeEventListener('removeLayer', onRemoveLayer);
        };
    }, [map, token, layers, setSelectedFeature]);
};