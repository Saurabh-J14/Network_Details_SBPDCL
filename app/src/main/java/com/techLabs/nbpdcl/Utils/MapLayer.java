package com.techLabs.nbpdcl.Utils;

import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.util.MapTileIndex;

public class MapLayer {

    public static OnlineTileSourceBase OpenStreetMap() {
        OnlineTileSourceBase openStreetMapHybrid = new OnlineTileSourceBase("OpenStreetMap Hybrid", 0, 19, 256, "",
                new String[]{"https://a.tile.openstreetmap.org/"}) {
            @Override
            public String getTileURLString(long pMapTileIndex) {
                return getBaseUrl()
                        + MapTileIndex.getZoom(pMapTileIndex)
                        + "/" + MapTileIndex.getX(pMapTileIndex)
                        + "/" + MapTileIndex.getY(pMapTileIndex)
                        + ".png";
            }
        };
        return openStreetMapHybrid;
    }

    public static OnlineTileSourceBase HybridMap() {
        OnlineTileSourceBase googleHybrid = new OnlineTileSourceBase("Google Hybrid", 0, 19, 256, "",
                new String[]{
                        "https://mt1.google.com/vt/lyrs=y&x=",
                        "https://mt2.google.com/vt/lyrs=y&x=",
                        "https://mt3.google.com/vt/lyrs=y&x=",
                        "https://mt0.google.com/vt/lyrs=y&x="
                }) {
            @Override
            public String getTileURLString(long pMapTileIndex) {
                return getBaseUrl()
                        + MapTileIndex.getX(pMapTileIndex)
                        + "&y=" + MapTileIndex.getY(pMapTileIndex)
                        + "&z=" + MapTileIndex.getZoom(pMapTileIndex);
            }
        };
        return googleHybrid;
    }

    public static OnlineTileSourceBase RoadMap() {
        OnlineTileSourceBase googleTileSource = new OnlineTileSourceBase("Google Maps", 0, 18, 256, "",
                new String[]{"https://mt0.google.com/vt/lyrs=m&hl=en&x=",
                        "https://mt1.google.com/vt/lyrs=m&hl=en&x="}) {
            @Override
            public String getTileURLString(long pMapTileIndex) {
                return getBaseUrl()
                        + MapTileIndex.getX(pMapTileIndex)
                        + "&y=" + MapTileIndex.getY(pMapTileIndex)
                        + "&z=" + MapTileIndex.getZoom(pMapTileIndex)
                        + "&s=Ga";
            }
        };
        return googleTileSource;
    }

}
