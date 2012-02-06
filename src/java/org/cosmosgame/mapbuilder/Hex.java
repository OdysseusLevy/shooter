package org.cosmosgame.mapbuilder;

/**
 * The Hex class is the basic element in the terrain.  It contains the
 * terrains elevation, the amount of water in the hex, the amount
 * of water generated in the hex, etc
 *
 * @author Odysseus Levy
 *
 *  Adapted from code by Lee Wilson
 *  http://www.devnullsoftware.com 1998/11/30 02:21:07
 * (c) Devnull Software, LLC. (http://www.devnullsoftware.com)
 */


public class Hex {

    //-------------------------------------------------------------------
    // Global Constants
    //
    public static final byte N = 0;
    public static final byte NE = 1;
    public static final byte SE = 2;
    public static final byte S = 3;
    public static final byte SW = 4;
    public static final byte NW = 5;
    public static final String [] sShortDirectionString = {
            "N","NE","SE","S","SW","NW"
    };
    public static final String [] sLongDirectionString = {
            "North","NorthEast","SouthEast","South","SouthWest","NorthWest"
    };
    public static final byte [] BW_DIRS = {
            1,2,4,8,16,32
    };
    public static final byte WIND_N = 1;
    public static final byte WIND_S = 2;
    public static final byte WIND_E = 4;
    public static final byte WIND_W = 8;
    public static final byte PRESSURE_LOW = 1;
    public static final byte PRESSURE_HIGH = 2;
    public static final byte PRESSURE_HEQ = 3;
    public static final byte CLIMATE_TUNDRA = 1;
    public static final byte CLIMATE_STEPPE = 2;
    public static final byte CLIMATE_DECIDUOUS = 3;
    public static final byte CLIMATE_DESERT = 4;
    public static final byte CLIMATE_SAVANNAH = 5;
    public static final byte CLIMATE_JUNGLE = 6;
    public static final byte CLIMATE_SWAMP = 7;
    public static final byte CLIMATE_OCEAN = 8;
    public static final byte CLIMATE_OCEANICE = 9;
    public static final byte CLIMATE_PRAIRIE = 10;

    public static final byte NUM_TERRAIN = 14;
    public static final byte TERRAIN_NOTYPE = 0;
    public static final byte TERRAIN_DEEPOCEAN = 1;
    public static final byte TERRAIN_OCEAN = 2;
    public static final byte TERRAIN_MOUNTAINS = 3;
    public static final byte TERRAIN_IMPASSABLEMOUNTAINS = 4;
    public static final byte TERRAIN_VOLCANO = 5;
    public static final byte TERRAIN_ICE = 6;
    public static final byte TERRAIN_TUNDRA = 7;
    public static final byte TERRAIN_STEPPE = 8;
    public static final byte TERRAIN_PRAIRIE = 9;
    public static final byte TERRAIN_SAVANNAH = 10;
    public static final byte TERRAIN_FOREST = 11;
    public static final byte TERRAIN_JUNGLE = 12;
    public static final byte TERRAIN_SWAMP = 13;
    public static final byte TERRAIN_DESERT = 14;
    public static final String [] sTerrainLongString = {
            "NONE",
            "DeepOcean", "Ocean", "Mountains", "ImpassableMountains", "Volcano",
            "Ice", "Tundra", "Steppe", "Prairie", "Savannah",
            "Forest", "Jungle", "Swamp", "Desert",

    };
    public static final String [] sTerrainShortString = {
            "!",
            " ", " ", "^", "M", "V",
            "I", "#", "-","=", "+",
            "*", "&", "Y", "D"
    };

    //-------------------------------------------------------------------
    // Instance Variables
    //
    private String sName = null;
    private int iIDNum;
    private int iX;
    private int iY;
    private float fElevation = 0.0f;
    private float [] fTemperature;
    private float [] fScaledTemperature;
    private byte [] iPressure;
    private short [] iWind;
    private short [] iRainfall;
    private byte iClimate;
    private byte iTerrainType = TERRAIN_NOTYPE;
    private Hex [] pNeighbors;
    private byte iShoreline;
    private byte iRiver;
    private MapParams pParams;


    //---------------------------------------------------------------------------
    // Constructors
    //
    Hex(int x, int y, String sName, MapParams pParams) {
        this.iX = x;
        this.iY = y;
        this.iIDNum = (y * pParams.dimension) + x;
        this.sName = sName;
        this.pParams = pParams;
        init ();
    };

    private void init ()  {
        pNeighbors = new Hex[6];
        fTemperature = new float [pParams.iNumSeasons];
        fScaledTemperature = new float [pParams.iNumSeasons];
        iPressure = new byte [pParams.iNumSeasons];
        iWind = new short [pParams.iNumSeasons];
        iRainfall = new short [pParams.iNumSeasons];
    }


    //---------------------------------------------------------------------------
    // Accessors and Mutators
    //
    public String toString ()  {
        return (sName+": ("+getElevation()+","+ getTerrainName()+")");
    }

    public String getTerrainName()
    {
        return sTerrainLongString[getTerrainType()];
    }

    public String getTerrainShortName()
    {
        return sTerrainShortString[getTerrainType()];
    }

    public String getName ()  {
        return sName;
    }

    public void setName (String newName)  {
        if (newName != null && !newName.equals(""))  {
            sName = newName;
        }
    }

    public byte getTerrainType ()  {
        return iTerrainType;
    }

    public void setTerrainType (byte newTerrainType)  {
        if (newTerrainType >= 0 && newTerrainType <= NUM_TERRAIN)  {
            iTerrainType = newTerrainType;
        }
    }

    public Hex getNeighbor (int iDirection)  {
        if (iDirection >= 0 && iDirection < 6)  {
            return pNeighbors[iDirection];
        }
        return null;
    }

    public void setNeighbor (int iDirection, Hex pHex)  {
        if (iDirection >= 0 && iDirection < 6)  {
            pNeighbors[iDirection] = pHex;
        }
    }

    public float getElevation() {
        return (fElevation);
    };

    public void setElevation (float dValue) {
        fElevation = dValue;
    };

    public float getTemperature(int iSeason) {
        if (iSeason >= 0 && iSeason < pParams.iNumSeasons)  {
            return (fTemperature[iSeason]);
        }
        else  {
            return 0.0f;
        }
    }

    public void setTemperature (int iSeason, float fTemp) {
        if (iSeason >= 0 && iSeason < pParams.iNumSeasons)  {
            fTemperature[iSeason] = fTemp;
        }
    }

    public float getAvgTemperatureF ()  {
        float fAvgTemp = 0.0f;
        for (int i = 0; i < pParams.iNumSeasons; i++)  {
            fAvgTemp += getTemperature(i);
        }
        fAvgTemp /= pParams.iNumSeasons;
        fAvgTemp = ((fAvgTemp / 10.0f) - 273.0f) * 1.8f + 32.0f;
        return fAvgTemp;
    }

    public float getScaledTemperature(int iSeason) {
        if (iSeason >= 0 && iSeason < pParams.iNumSeasons)  {
            return (fScaledTemperature[iSeason]);
        }
        else  {
            return 0.0f;
        }
    }

    public void setScaledTemperature (int iSeason, float fTemp) {
        if (iSeason >= 0 && iSeason < pParams.iNumSeasons)  {
            fScaledTemperature[iSeason] = fTemp;
        }
    }

    public byte getPressure(int iSeason) {
        if (iSeason >= 0 && iSeason < pParams.iNumSeasons)  {
            return (iPressure[iSeason]);
        }
        else  {
            return 0;
        }
    }

    public void setPressure (int iSeason, byte bPres) {
        if (iSeason >= 0 && iSeason < pParams.iNumSeasons)  {
            iPressure[iSeason] = bPres;
        }
    }

    public short getWind(int iSeason) {
        if (iSeason >= 0 && iSeason < pParams.iNumSeasons)  {
            return (iWind[iSeason]);
        }
        else  {
            return 0;
        }
    }

    public void setWind (int iSeason, short iW) {
        if (iSeason >= 0 && iSeason < pParams.iNumSeasons)  {
            iWind[iSeason] = iW;
        }
    }

    public short getRainfall(int iSeason) {
        if (iSeason >= 0 && iSeason < pParams.iNumSeasons)  {
            return (iRainfall[iSeason]);
        }
        else  {
            return 0;
        }
    }

    public float getAvgRainfall ()  {
        float fAvgRain = 0.0f;
        for (int i = 0; i < pParams.iNumSeasons; i++)  {
            fAvgRain += getRainfall(i);
        }
        fAvgRain /= pParams.iNumSeasons;
        return fAvgRain;
    }

    public void setRainfall (int iSeason, short iRF) {
        if (iSeason >= 0 && iSeason < pParams.iNumSeasons)  {
            iRainfall[iSeason] = iRF;
        }
    }

    public byte getClimate() {
        return (iClimate);
    }

    public void setClimate (byte iClim) {
        iClimate = iClim;
    }

    public int getX ()  {
        return iX;
    }

    public int getY ()  {
        return iY;
    }

    public int getIDNum ()  {
        return iIDNum;
    }

    public boolean isWater ()  {
        boolean bWater;
        switch (getTerrainType())  {
            case TERRAIN_DEEPOCEAN:
            case TERRAIN_OCEAN:
                bWater = true;
                break;
            default:
                bWater = false;
                break;
        }
        return bWater;
    }

    public boolean isLand ()  {
        boolean bLand;
        switch (getTerrainType())  {
            case TERRAIN_DEEPOCEAN:
            case TERRAIN_OCEAN:
                bLand = false;
                break;
            default:
                bLand = true;
                break;
        }
        return bLand;
    }

    public void clearShorelines ()  {
        iShoreline = 0;
    }

    public void setShoreline (byte iDir, boolean value)  {
        if (value)  {
            iShoreline |= iDir;
        }
        else  {
            iShoreline ^= iDir;
        }
    }

    public boolean isShoreline (byte iDir)  {
        return ((iShoreline & iDir) > 0);
    }

}


