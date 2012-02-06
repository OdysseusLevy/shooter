package org.cosmosgame.mapbuilder;

/**
 * Author: odysseus
 * Date: 2/4/12

 Adapted from code by by Lee Wilson
 http://www.devnullsoftware.com 1998/11/30 02:21:07
 (c) Devnull Software, LLC. (http://www.devnullsoftware.com)

*/


import java.awt.*;

public class MapParams
{

    //-------------------------------------------------------------------
    // Global Constants
    //
    public static final byte WORLDSIZE_TINY = 0;
    public static final byte WORLDSIZE_SMALL = 1;
    public static final byte WORLDSIZE_NORMAL = 2;
    public static final byte WORLDSIZE_HUGE = 3;
    public static final String [] sWorldSizeStrings = {
            "Tiny", "Small", "Normal", "Huge"
    };
    public static final short WORLDSIZE_DIM_TINY = 32;
    public static final short WORLDSIZE_DIM_SMALL = 64;
    public static final short WORLDSIZE_DIM_NORMAL = 128;
    public static final short WORLDSIZE_DIM_HUGE = 256;


    //-------------------------------------------------------------------
    // Instance Variables
    //


    public String sName;

    public int dimension = 512;

    public void setDimension(int dimension)
    {
        this.dimension = dimension;
    }
    
    public int getDimension()
    {
        return this.dimension;
    }

    /**
     Number of Seasons in a year for this world.
     */
    public int iNumSeasons = 4;
    public byte bMaxRange = 15;
    public int TEMPSCALE = 10;

    //
    // Elevation
    //

    /**
     the number of Iterations to use for initialization.  Default 3;
     */
    public int iIterations = 2;

    /**
     The roughness parameter.  Default 0.7;
     */
    public float fRoughness = 0.7f;
    public float fPercentDeepOcean;
    public float fPercentOcean = 0.7f ;
    public float fPercentHills = 0.96f;
    public float fPercentMountains = 0.985f;
    public float fPercentImpassableMountains = 0.995f;
    public int iMinInlandSeaSize = 5;

    //
    // Temperature
    //

    /**
     This is the tilt of the planet with respect to
     its plane of orbit, in degrees.  Smaller numbers produce less
     seasonality; numbers above 45 violate some of the assumptions
     of the models used.  Default is 23.0
     */
    public double dAxialTilt = 23.0;

    /**
     The Orbital Eccentricity of the planet's orbit; this parameter affects
     seasonality as well.  Numbers above 0.5 are probably unrealistic.
     Default 0.0.
     */
    public double dOrbitalEccentricity = 0.0;

    /**
     This parameter describes the phase offset of the dOrbitalEccentricity with
     respect to the dAxialTilt, in radians.  You can produce climates with
     complicated seasonality by varying this.  Default 0.0.
     */
    public double dEccentricityPhase = 0.0;

    /**
     The basic temperature for land squares (in Kelvins), assuming no
     dAxialTilt, dOrbitalEccentricity, or nearby ocean.  Default is 275.0.
     */
    public double dBaseLandTemp = 275.0;

    /**
     The amount by which land temperatures should vary from north pole to
     equator.  Land temperature, ignoring ocean effects, varies from
     dBaseLandTemp - dLandTempVariance/2 at the poles to
     dBaseLandTemp + dLandTempVariance/2 at the equator.
     Default 45.0.
     */
    public double dLandTempVariance = 45.0;

    /**
     The fraction of the dAxialTilt parameter that should be applied to
     temperature adjustment for land.  Typically, land temperatures vary
     more from season to season than the ocean temperatures do, so
     dLandTempTilt should be higher than dOceanTempTilt.  Default 1.0.
     */
    public double dLandTempTilt = 1.0;

    /**
     One equation governs the effect of land on ocean temperatures and vice
     versa.  The equation involves dLandSmooth, dLandDiv,
     dOceanSmooth and dOceanDiv.  Given the land and sea temperatures, and
     the number of land squares in a 11 x 5 box around the square, the final
     temperature is a weighted sum of the two temperatures.  The weights
     are related to dLandSmooth and dOceanSmooth, and the importance of
     nearby land is diminished by increasing dLandDiv or dOceanDiv.
     Default 0.6.
     */
    public double dLandSmooth = 0.6f;

    /**
     See above.  Default 180.0.
     */
    public double dLandDiv = 180.0;

    /**
     Same as dBaseLandTemp, only for the ocean.  Default 275.0.
     */
    public double dBaseOceanTemp = 275.0;

    /**
     Same as dLandTempVariance, only for the ocean.  Default 30.0.
     */
    public double dOceanTempVariance = 30.0;

    /**
     See dLandTempTilt.  Default 0.2.
     */
    public double dOceanTempTilt = 0.2f;

    /**
     See dLandSmooth.  Default 0.2.
     */
    public double dOceanSmooth = 0.2f;

    /**
     See dLandSmooth.  Default 250.0.
     */
    public double dOceanDiv = 250.0;

    //
    // Pressure
    //

    /**
     Ocean pressure zones essentially ignore land masses whose radius is equal
     to or less than this number, like islands.  Default 1.
     */
    public byte OLTHRESH = 1;

    /**
     Ocean pressure zones must be at least this many squares away from the
     nearest (non-ignored) land.  Default 5.
     */
    public byte OOTHRESH = 5;

    /**
     If the unscaled temperature of an ocean square is greater than OLMIN
     and less than OLMAX, then that square is a low pressure zone.
     Default 40.
     */
    public byte OLMIN = 40;

    /**
     See above.  Default 65.
     */
    public byte OLMAX = 65;

    /**
     If the unscaled temperature of an ocean square is greater than OHMIN
     and less than OHMAX, then that square is a high pressure zone.
     Default 130.
     */
    public short OHMIN = 130;

    /**
     See above.  Default 180.
     */
    public short OHMAX = 180;

    /**
     Land pressure zones essentially ignore ocean bodies whose radius is less
     than or equal to this number, like lakes.  Default 3.
     */
    public short LOTHRESH = 3;

    /**
     Land pressure zones must be at least this many squares away from the
     nearest (non-ignored) ocean.
     Default 7.
     */
    public short LLTHRESH = 7;

    /**
     If the unscaled temperature of a land square is greater than LLMIN and
     less than LLMAX, then that square is a low pressure zone.
     Default 220.
     */
    public short LLMIN = 220;

    /**
     See above.  Default 255.
     */
    public short LLMAX = 255;

    /**
     If the unscaled temperature of a land square is greater than LHMIN and
     less than LHMAX, then that square is a high pressure zone.  Default 0.
     */
    public byte LHMIN = 0;

    /**
     See above.  Default 20.
     */
    public byte LHMAX = 20;

    //
    // Wind
    //

    public short iMaxPressure = 255;

    /**
     Winds are determined from pressure; a smooth pressure map ranging from
     0..255 is built by interpolating between highs and lows.  Wind lines are
     contour lines on this map, and BARSEP indicates the pressure difference
     between lines on the map.  Default 16.
     */
    public byte BARSEP = 16;

    //
    // Rainfall
    //

    /**
     Fetch is the term that describes how many squares a given wind line
     travels over water.  A high fetch indicates a moist wind.  This number
     is the maximum depth for the tree walking algorithm which finds fetch;
     the effect of wind in one square can travel at most this number of squares
     before stopping.  Default 5.
     */
    public byte MAXFETCH = 5;

    /**
     This is the base amount of rainfall in each square.
     Default 32.
     */
    public byte RAINCONST = 32;

    /**
     This is the amount by which rainfall is increased in every land or
     mountain square; that is, rainfall goes down.
     Default 10.
     */
    public byte LANDEL = 10;

    /**
     For each unit of fetch which is stopped by a mountain, rainfall in the
     mountain square increases by this amount.
     Default 32.
     */
    public byte MOUNTDEL = 32;

    /**
     The amount of rainfall in a square is increased by this number for each
     unit of fetch in the square.
     Default 4.
     */
    public byte FETCHDEL = 4;

    /**
     The amount of rainfall in a square is increased by this amount if the
     square is on the heat equator.
     Default 32.
     */
    public byte HEQDEL = 32;

    /**
     The amount of rainfall in a square is increased by this amount if the
     square is next to a square on the heat equator.
     Default 24.
     */
    public byte NRHEQDEL = 24;

    /**
     The amount of rainfall in a square is increased by this amount if the
     square is on the "flank" of a circular wind pattern.  This happens when
     the wind blows south.    Default -24.
     */
    public byte FLANKDEL = -24;

    /**
     The amount of rainfall in a square is increased by this amount for each
     adjacent square which is on a "flank".
     Default 3.
     */
    public byte NRFDEL = 3;


    //
    // Climate
    //

    /**
     If an ocean square is below this temperature (measured in deg Kelvin) all
     year round, then the ocean square is icebergs.
     Default 263.
     */
    public short ICEBERGK = 263;

    /**
     The climate array found in climate.c/climkey is 4 x 5; the first index
     is based on average annual temperature.  The temperature is relative, based
     on the range 0..255; this vector determines the cutoff points.
     For example, with the default vector, a scaled temperature of 20
     falls into the first "bin" and 121 falls into the fourth.
     Default is {0,40,90,120}.
     */
    public short [] TEMPCUT = {
            // 0,40,90,120
            0,65,100,140
    };

    /**
     The second index of the climate array is based on average annual rainfall,
     scaled into the range 0..255.  This vector determines the cutoff points.
     For example, rainfall of 35 falls into the first "bin".
     Default is {40,60,110,160,180}.
     */
    public short [] RAINCUT = {
            // 40,60,110,160,180
            37, 58, 85, 150, 170
    };

    /**
     These are the cutoffs for elevation in Elevation-View
     */
    public float [] ELEVATIONCUT = {
            0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f
    };

    /**
     This is the amount, in degrees Fahrenheit, by which temperature in the
     mountains is decreased before the climate lookup is performed.
     Default 20.
     */
    public byte MTDELTA = 20;

    // Terrain Images
    public String [] sTerrainImageLocation =  {
            "img/NoType.gif",
            "img/DeepOcean.gif",
            "img/Ocean.gif",
            "img/Mountains.gif",
            "img/ImpassableMountains.gif",
            "img/Volcano.gif",
            "img/Ice.gif",
            "img/Tundra.gif",
            "img/Steppe.gif",
            "img/Prairie.gif",
            "img/Savannah.gif",
            "img/Forest.gif",
            "img/Jungle.gif",
            "img/Swamp.gif",
            "img/Desert.gif"
    };
    public Image [] pTerrainImage = new Image [Hex.NUM_TERRAIN+1];



    //-------------------------------------------------------------------
    // Constructors
    //
    MapParams()  {
        init();
    }

    private void init ()  {
        sName = "Map 1";

    }

}


