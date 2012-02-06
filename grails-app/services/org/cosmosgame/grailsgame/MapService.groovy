package org.cosmosgame.grailsgame

import org.cosmosgame.mapbuilder.*

class MapService {

    MapBuilder mapBuilder; // injected bean
    MapParams mapParams;   // injected bean

    TerrainMap createWorld()
    {
        
        mapBuilder.init(mapParams);
        mapBuilder.generateWorld();

        List hexes = mapBuilder.hex;

        List result = []

        hexes.each { result << new Terrain(gridX: it.getX(), gridY: it.getY(), terrainType: it.getTerrainType(), terrainName: it.getTerrainName())}
        TerrainMap map = new TerrainMap(terrains: result);
        return map;
    }
}
