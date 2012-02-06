package org.cosmosgame.grailsgame

import org.cosmosgame.mapbuilder.MapBuilder
import org.cosmosgame.mapbuilder.MapParams
import org.cosmosgame.mapbuilder.TerrainMap

class MapService {

    MapBuilder mapBuilder; // injected bean
    MapParams mapParams;   // injected bean

    TerrainMap createWorld()
    {
        
        mapBuilder.init(mapParams);
        mapBuilder.generateWorld();

        def hexes = mapBuilder.hex;

        List list;

        hexes.each { list << {}}
        TerrainMap map = new TerrainMap(mapBuilder);
        return map;
    }
}
