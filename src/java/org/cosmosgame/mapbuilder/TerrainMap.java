package org.cosmosgame.mapbuilder;

/**
 * Author: odysseus
 * Date: 2/5/12
 */
public class TerrainMap
{
    public Terrain[] terrains;
    
    public TerrainMap(MapBuilder builder)
    {
        terrains = new Terrain[builder.getDimension()* builder.getDimension()];

        int i = 0;
        for(Hex hex : builder.getHex())
        {
            terrains[i++] = new Terrain(hex);
        }

    }

    public Terrain[] getTerrains()
    {
        return terrains;
    }

}
