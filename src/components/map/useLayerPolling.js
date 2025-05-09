import { useEffect } from 'react';

export const useLayerPolling = (layers, intervalMs = 10000) => {
    useEffect(() => {
        const interval = setInterval(() => {
            layers.forEach((layer) => {
                console.log("polling layer: " + layer.id);
                if (layer.type === 'VECTOR' && (layer.visible ?? true)) {
                    window.dispatchEvent(new CustomEvent('vectorObjectUpdated', {
                        detail: { layerId: layer.id },
                    }));
                }
            });
        }, intervalMs);

        return () => clearInterval(interval);
    }, [layers, intervalMs]);
};
