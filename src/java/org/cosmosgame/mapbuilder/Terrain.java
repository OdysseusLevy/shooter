package org.cosmosgame.mapbuilder;

/**
 * Author: odysseus
 * Date: 2/5/12
 */
public class Terrain 
{
    public byte terrainType;
    public int x,y;
    public float height;

    public Terrain(Hex hex)
    {
        this.terrainType = hex.getTerrainType();
        x = hex.getX();
        y = hex.getY();
        height = hex.getElevation();
    }

}
