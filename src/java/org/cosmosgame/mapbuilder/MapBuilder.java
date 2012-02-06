package org.cosmosgame.mapbuilder;

/*
    Author: Odysseus Levy


   Adapted from code by by Lee Wilson
  http://www.devnullsoftware.com 1998/11/30 02:21:07
  (c) Devnull Software, LLC. (http://www.devnullsoftware.com)
*/

import java.util.*;

/**
 The Map class consists of a collection of hexes and contains
 routines to generate elevation, terrain, temperatures, pressures, winds,
 rainfall, climate, and much more.

 @author Lee Wilson
 @version 1.0
 */

public class MapBuilder {

    //-------------------------------------------------------------------
    // Data
    //
    public MapParams pParams;
    private String sName = null;
    private Hex [] pHex = null;         // The collection of hexes.
    private int iDim;                   // The dimensions of the map
                                        // (note that the map has to be a square)

    private float fElevationDeepOcean;
    private float fElevationOcean;
    private float fElevationMountains;
    private float fElevationImpassableMountains;
    private float fElevationLowest;
    private float fElevationHighest;
    private transient int iProgress;


    public Random generator = new Random();
    public Logger logger = new Logger();
    
    //-------------------------------------------------------------------
    // Constructors
    //

    public void MapBuilder()
    {

    }

    public void init (MapParams pParams)
    {
        this.pParams = pParams;
        this.iDim = pParams.dimension;
        this.sName = pParams.sName;

        pHex = new Hex [iDim * iDim];
        for (int x = 0; x < iDim; x++)  {
            for (int y = 0; y < iDim; y++)  {
                pHex[y*iDim + x] = new Hex (x,y,"("+x+","+y+")", pParams);
            }
        }
        attachHexen();

    }

    //-------------------------------------------------------------------
    // Accessors & Mutators
    //
    public String toString ()  {
        return getName();
    }

    public String getName ()  {
        return sName;
    }

    public void setName (String sName)  {
        if (sName != null && !sName.equals(""))  {
            this.sName = sName;
        }
    }

    public int getDimension ()  {
        return iDim;
    }

    public Hex[] getHex()
    {
        return pHex;
    }

    public Hex getHex (int x, int y)  {
        try  {
            return pHex[y*iDim + x];
        }
        catch (ArrayIndexOutOfBoundsException e)  {
            logger.error("argh!  no pHex("+x+","+y+")");
            return null;
        }
    }

    public Hex getHex (int i)  {
        return pHex[i];
    }

    public MapParams getParams ()  {
        return pParams;
    }

    public float getElevationHighest()  {
        return fElevationHighest;
    }

    public float getElevationLowest()  {
        return fElevationLowest;
    }

    public float getElevationDeepOcean()  {
        return fElevationDeepOcean;
    }

    public float getElevationOcean()  {
        return fElevationOcean;
    }

    public float getElevationMountains()  {
        return fElevationMountains;
    }

    public float getElevationImpassableMountains()  {
        return fElevationImpassableMountains;
    }

    public float getElevation (int x, int y) throws NullPointerException {
        return getHex(x,y).getElevation();
    }


    public void setElevation (int x,
                              int y,
                              float fElevation)
            throws NullPointerException
    {
        getHex(x,y).setElevation(fElevation);
    }


    //-------------------------------------------------------------------
    // Methods
    //
    /**
     Takes the average of the four corners of the square.
     The corners are given by the location and the step size.

     1---2  1 = < x - step, y - step>
     -----  2 = < x + step, y - step>
     --*--  3 = < x - step, y + step>
     -----  4 = < x + step, y + step>
     3---4

     @param x the x location of the center of the square
     @param y the y location of the center of the square
     @param iStep the step size of the sub division.
     @return the average of the elevations of the square.
     */
    private float squareAverage (int x,
                                 int y,
                                 int iStep)
            throws NullPointerException
    {
        float fSum;

        // Surrounding component
        fSum  = getElevation (x - iStep, y - iStep);
        fSum += getElevation ((x + iStep) % iDim, y - iStep);
        fSum += getElevation ((x + iStep) % iDim, (y + iStep) % iDim);
        fSum += getElevation (x - iStep, (y + iStep) % iDim);

        // Return average
        return (fSum / 4.0f);
    }


    /**
     Takes the average of the four corners of the diamond.
     The corners are given by the location and the step size.

     --1--  1 = < x, y - step>
     -----  2 = < x + step, y>
     4-*-2  3 = < x, y + step>
     -----  4 = < x - step, y>
     --3--

     @param x the x location of the center of the diamond
     @param y the y location of the center of the diamond
     @param iStep the step size of the sub division.
     @return the average of the elevations of the diamond.
     */
    private float diamondAverage (int x,
                                  int y,
                                  int iStep)
            throws NullPointerException
    {
        float fSum;

        // Surrounding component
        fSum  = getElevation (x, (y - iStep + iDim) % iDim);
        fSum += getElevation ((x - iStep + iDim) % iDim, y);
        fSum += getElevation ((x + iStep) % iDim, y);
        fSum += getElevation (x, (y + iStep) % iDim);

        // Return average
        return (fSum / 4.0f);
    }


    /**
     Generates a random value in the range from  - fRange / 2 to + fRange /2.

     @param fRange the range of the random number.
     @return the random number ( - fRange/2 .. + fRange/2 ).
     */
    private float Random (float fRange)  {
        return (float)((generator.nextDouble() * (double)fRange) - ((double)fRange / 2.0d));
    }


    private void attachHexen ()  {
        int x, y;
        // Attach neighboring hexen
        for (x = 0; x < iDim; x++)  {
            for (y = 0; y < iDim; y++)  {
                // North
                if (y > 0)  {
                    getHex(x,y).setNeighbor(Hex.N,pHex[((y-1)*iDim)+x]);
                }
                // NorthEast
                if (x < (iDim-1))  {
                    if (x % 2 == 0)  {
                        getHex(x,y).setNeighbor(Hex.NE,pHex[(y*iDim)+(x+1)]);
                    }
                    else {
                        if (y > 0)  {
                            getHex(x,y).setNeighbor(Hex.NE,pHex[((y-1)*iDim)+(x+1)]);
                        }
                    }
                }
                else  {
                    if (x % 2 == 0)  {
                        getHex(x,y).setNeighbor(Hex.NE,pHex[y*iDim]);
                    }
                    else {
                        if (y > 0)  {
                            getHex(x,y).setNeighbor(Hex.NE,pHex[(y-1)*iDim]);
                        }
                    }
                }
                // SouthEast
                if (x < (iDim-1)) {
                    if (x % 2 == 0)  {
                        if (y < (iDim-1))  {
                            getHex(x,y).setNeighbor(Hex.SE,pHex[((y+1)*iDim)+(x+1)]);
                        }
                    }
                    else  {
                        getHex(x,y).setNeighbor(Hex.SE,pHex[(y*iDim)+(x+1)]);
                    }
                }
                else  {
                    if (x % 2 == 0)  {
                        if (y < iDim-1)  {
                            getHex(x,y).setNeighbor(Hex.SE,pHex[(y+1)*iDim]);
                        }
                        else  {
                            getHex(x,y).setNeighbor(Hex.SE,pHex[y*iDim]);
                        }
                    }
                }
                // South
                if (y < (iDim-1))  {
                    getHex(x,y).setNeighbor(Hex.S,pHex[((y+1)*iDim)+x]);
                }
                // SouthWest
                if (x > 0)  {
                    if (x % 2 == 0)  {
                        if (y < (iDim-1))  {
                            getHex(x,y).setNeighbor(Hex.SW,pHex[((y+1)*iDim)+(x-1)]);
                        }
                    }
                    else  {
                        getHex(x,y).setNeighbor(Hex.SW,pHex[(y*iDim)+(x-1)]);
                    }
                }
                else  {
                    if (x % 2 == 0)  {
                        if (y < iDim-1)  {
                            getHex(x,y).setNeighbor(Hex.SW,pHex[((y+1)*iDim)+(iDim-1)]);
                        }
                    }
                    else  {
                        getHex(x,y).setNeighbor(Hex.SW,pHex[(y*iDim)+(iDim-1)]);
                    }
                }
                // NorthWest
                if (x > 0)  {
                    if (x % 2 == 0)  {
                        getHex(x,y).setNeighbor(Hex.NW,pHex[(y*iDim)+(x-1)]);
                    }
                    else  {
                        if (y > 0)  {
                            getHex(x,y).setNeighbor(Hex.NW,pHex[((y-1)*iDim)+(x-1)]);
                        }
                    }
                }
                else  {
                    if (x % 2 == 0)  {
                        getHex(x,y).setNeighbor(Hex.NW,pHex[(y*iDim)+(iDim-1)]);
                    }
                    else  {
                        if (y > 0)  {
                            getHex(x,y).setNeighbor(Hex.NW,pHex[((y-1)*iDim)+(iDim-1)]);
                        }
                    }
                }
            }
        }
    }


    /**
     This function initializes the terrain in the map.  It uses
     the diamond square algorithm described in the description of
     the Map class.  The roughness parameter indicates the roughness
     of the terrain ( values close to 0.0 mean rougher, values close
     to or above 1.0 mean smoother).  The iteration parameter indicates
     how many Iterations of the algorithm are supposed to be used
     purely for initialization.

     The algorithm used to generate the terrain is called the "diamond
     square" algorithm.  It subdivides the map like this:

     Square  Diamond Square  Dimaond Square

     1---1   1---1   1-3-1   1-3-1   15351
     -----   -----   -----   -4-4-   54545
     -----   --2--   3-2-3   3-2-3   35253
     -----   -----   -----   -4-4-   54545
     1---1   1---1   1-3-1   1-3-1   15351

     In the diamond step the four corners of the squares are taken and
     averaged to that we add a random amount to generate a value at the
     center of the squares.  This generates diamonds.  In the square step
     the four corners of the diamond are averaged to generate a value at
     the center of the diamonds.  Again a random amount is added to the
     new value.  This generates squares.  This is repeated until the entire
     map is filled.  With each iteration the range of the random value
     generator is multiplied by 2^H (where H is the roughness value).
     */
    private void generateElevations ()  {
        int x, y, iStep;
        float fRange, fAvg;
        int iIterations = pParams.iIterations;

        try  {
            // Initializations
            fRange = 2.0f;   // So that we get values between -1.0 and 1.0
            iStep = iDim >> 1;

            setElevation (0, 0, Random (fRange));

            // Initialize the map matrix with random elevations

            while (iIterations > 0 && iStep > 0)  {
                // Step through all the squares and diamonds
                y = iStep;
                while (y < iDim)  {
                    x = iStep;
                    while (x < iDim)  {
                        // Generate squares
                        setElevation (x, y, Random (fRange));
                        // Generate diamonds
                        setElevation (x, (y + iStep) % iDim, Random (fRange));
                        x += iStep << 1;   // Go to the next square
                    }
                    y += iStep << 1;
                }

                // Go to the next iteration
                iStep = iStep >> 1;
                iIterations -= 1;
            }

            // Now generate the terrain again using the diamond square step

            while (iStep > 0)  {
                // Diamond Step
                y = iStep;
                while (y < iDim)  {
                    x = iStep;
                    while (x < iDim)  {
                        fAvg  = squareAverage (x, y, iStep);
                        fAvg += Random (fRange);
                        setElevation (x, y, fAvg);
                        x += iStep << 1;
                    }
                    y += iStep << 1;
                }

                // Square step
                y = iStep;

                while (y < iDim)  {
                    x = iStep;
                    while (x < iDim)  {
                        fAvg  = diamondAverage (x, y - iStep, iStep);
                        fAvg += Random (fRange);
                        setElevation (x, y - iStep, fAvg);
                        fAvg = diamondAverage (x - iStep, y, iStep);
                        fAvg += Random (fRange);
                        setElevation (x - iStep, y, fAvg);
                        fAvg = diamondAverage ((x + iStep) % iDim, y, iStep);
                        fAvg += Random (fRange);
                        setElevation ((x + iStep) % iDim, y, fAvg);
                        fAvg = diamondAverage (y, (y + iStep) % iDim, iStep);
                        fAvg += Random (fRange);
                        setElevation (x, (y + iStep) % iDim, fAvg);
                        x += iStep << 1;
                    }
                    y += iStep << 1;
                }

                // Reduce the range, and go to the next iteration
                fRange = fRange * (float)(Math.pow (2, -pParams.fRoughness));
                iStep  = iStep >> 1;
            }
        }
        catch (NullPointerException e)  
        {
            logger.error(e);
        }

        // Normalize all Elevations to [0,1]
        // then cube them (to flatten)
        calcHighLowElevations();
        float fNormElHigh, fNormElFactor = 0.0f;
        if (fElevationLowest < 0.0f)  {
            fNormElFactor = 0.0f - fElevationLowest;
        }
        fNormElHigh = fElevationHighest + fNormElFactor;
        for (x = 0; x < iDim; x++)  {
            for (y = 0; y < iDim; y++)  {
                float fCurEl = getElevation(x,y);
                fCurEl = (float) Math.pow ((double)((fCurEl + fNormElFactor) / fNormElHigh), 2);
                setElevation (x,y,fCurEl);
            }
        }
        calcHighLowElevations();
    }


    private void calcHighLowElevations ()  {
        fElevationHighest = Float.MIN_VALUE;
        fElevationLowest = Float.MAX_VALUE;
        float fCur;
        for (int i = 0; i < (iDim * iDim); i++)  {
            fCur = pHex[i].getElevation();
            if (fCur < fElevationLowest)  {
                fElevationLowest = fCur;
            }
            if (fCur > fElevationHighest)  {
                fElevationHighest = fCur;
            }
        }
    }


    /**
     This function assigns Terrain based on Elevation.  Note that this does
     NOT generate all the terrain - just that which can be determined by
     elevation alone.
     */
    private void assignElevationBasedTerrain()  {
        int [] iElevations = new int[1001];
        int i,j,n,max;
        float fNormFactor;
        float fNormElevation;
        float fNormElLow, fNormElHigh, fNormElFactor;

        logger.log("Info","Generating Elevation Based Terrain");
        pParams.fPercentDeepOcean = pParams.fPercentOcean * 0.75f;

        for (i = 0; i <= 1000; i++)  {
            iElevations[i] = 0;
        }
        fNormElFactor = 0.0f;
        if (fElevationLowest < 0.0f)  {
            fNormElFactor = 0.0f - fElevationLowest;
        }
        fNormElLow = fElevationLowest + fNormElFactor;
        fNormElHigh = fElevationHighest + fNormElFactor;
        fNormFactor = 1000.0f / (fNormElHigh - fNormElLow);
        max = 0;
        n = 0;
        try  {
            for (i = 0; i < (iDim * iDim); i+=5)  {
                n = (int)((float)(pHex[i].getElevation() + fNormElFactor) * fNormFactor);
                iElevations[n] += 1;
                max++;
            }
        }
        catch (ArrayIndexOutOfBoundsException e)  {
            logger.log("Info","OOB: i == "+i+", n == "+n+"");
        }

        n = (int)((float)max * pParams.fPercentDeepOcean);
        i = j = 0;
        while (i < n && j <= 1000)  {
            i += iElevations[j++];
        }
        j--;
        fElevationDeepOcean = ((float)j / fNormFactor) - fNormElFactor;
        n = (int)((float)max * pParams.fPercentOcean);
        i = j = 0;
        while (i < n && j <= 1000)  {
            i += iElevations[j++];
        }
        j--;
        fElevationOcean = ((float)j / fNormFactor) - fNormElFactor;
        n = (int)((float)max * pParams.fPercentMountains);
        i = j = 0;
        while (i < n && j <= 1000)  {
            i += iElevations[j++];
        }
        j--;
        fElevationMountains = ((float)j / fNormFactor) - fNormElFactor;
        n = (int)((float)max * pParams.fPercentImpassableMountains);
        i = j = 0;
        while (i < n && j <= 1000)  {
            i += iElevations[j++];
        }
        j--;
        fElevationImpassableMountains =
                ((float)j / fNormFactor) - fNormElFactor;

        for (i = 0; i < (iDim * iDim); i++)  {
            if (pHex[i].getElevation() < fElevationDeepOcean)  {
                pHex[i].setTerrainType(Hex.TERRAIN_DEEPOCEAN);
            }
            else if (pHex[i].getElevation() < fElevationOcean)  {
                pHex[i].setTerrainType(Hex.TERRAIN_OCEAN);
            }
            else if (pHex[i].getElevation() > fElevationImpassableMountains)  {
                pHex[i].setTerrainType(Hex.TERRAIN_IMPASSABLEMOUNTAINS);
            }
            else if (pHex[i].getElevation() > fElevationMountains)  {
                pHex[i].setTerrainType(Hex.TERRAIN_MOUNTAINS);
            }
        }
        fillPuddles();
    }

    private void fillPuddles()  {
        boolean [] pVisited = new boolean[iDim * iDim];
        int i;
        Hex pTempHex;
        Vector pHexes;

        for (i = 0; i < (iDim * iDim); i++)  {
            pVisited[i] = false;
        }

        for (i = 0; i < (iDim * iDim); i++)  {
            pTempHex = getHex (i);
            if (!pVisited[i])  {
                pVisited[i] = true;
                if (pTempHex.isWater())  {
                    pHexes = new Vector();
                    pHexes.add(pTempHex);
                    findWaterNeighbors(pTempHex, pHexes, pVisited);
                    if (pHexes.size() < pParams.iMinInlandSeaSize)  {
                        for (Enumeration e = pHexes.elements();
                             e.hasMoreElements() ;
                                )  {
                            pTempHex = (Hex)e.nextElement();
                            pTempHex.setTerrainType(Hex.TERRAIN_NOTYPE);
                        }
                    }
                }
            }
        }
    }

    private boolean findWaterNeighbors (Hex pHex,
                                        Vector pHexes,
                                        boolean [] pVisited)  {
        boolean done = false;
        Hex pTempHex;

        if (pHexes.size() >= pParams.iMinInlandSeaSize)  {
            done = true;
        }
        for (int i = 0; i < 6 && !done; i++)  {
            pTempHex = pHex.getNeighbor(i);
            if (pTempHex != null)  {
                if (!pVisited[pTempHex.getIDNum()])  {
                    pVisited[pTempHex.getIDNum()] = true;
                    if (pTempHex.isWater())  {
                        pHexes.add(pTempHex);
                        done = findWaterNeighbors(pTempHex, pHexes, pVisited);
                    }
                }
            }
        }
        return done;
    }



    private void assignClimateBasedTerrain()  {
        Hex pTempHex;

        logger.log("Info","Generating Climate Based Terrain");
                logger.log("Info","Generating Temperatures");
                        computeTemperatures();
        logger.log("Info","Generating Pressure");
                computePressure();
        logger.log("Info","Generating Wind");
                computeWind();
        logger.log("Info","Generating Rainfall");
                computeRainfall();
        logger.log("Info","Generating Climate");
                computeClimate();
        for (int i = 0; i < (iDim * iDim); i++)  {
            pTempHex = getHex(i);
            switch (pTempHex.getTerrainType())  {
                case Hex.TERRAIN_NOTYPE:
                    switch (pTempHex.getClimate())  {
                        case Hex.CLIMATE_TUNDRA:
                            pTempHex.setTerrainType(Hex.TERRAIN_TUNDRA);
                            break;
                        case Hex.CLIMATE_STEPPE:
                            pTempHex.setTerrainType(Hex.TERRAIN_STEPPE);
                            break;
                        case Hex.CLIMATE_DECIDUOUS:
                            pTempHex.setTerrainType(Hex.TERRAIN_FOREST);
                            break;
                        case Hex.CLIMATE_DESERT:
                            pTempHex.setTerrainType(Hex.TERRAIN_DESERT);
                            break;
                        case Hex.CLIMATE_SAVANNAH:
                            pTempHex.setTerrainType(Hex.TERRAIN_SAVANNAH);
                            break;
                        case Hex.CLIMATE_PRAIRIE:
                            pTempHex.setTerrainType(Hex.TERRAIN_PRAIRIE);
                            break;
                        case Hex.CLIMATE_JUNGLE:
                            pTempHex.setTerrainType(Hex.TERRAIN_JUNGLE);
                            break;
                        case Hex.CLIMATE_SWAMP:
                            pTempHex.setTerrainType(Hex.TERRAIN_SWAMP);
                            break;
                        default:
                            logger.log("Info","Hex "+i+": noTerrain.");
                            logger.log("Info","  Elevation: "+pTempHex.getElevation()+"");
                            logger.log("Info","Temp: ("+pTempHex.getTemperature(0)+","
                                    +pTempHex.getTemperature(1)+","
                                    +pTempHex.getTemperature(2)+","
                                    +pTempHex.getTemperature(3)+")");
                            logger.log("Info","Pressure: ("+pTempHex.getPressure(0)+","
                                    +pTempHex.getPressure(1)+","
                                    +pTempHex.getPressure(2)+","
                                    +pTempHex.getPressure(3)+")");
                            logger.log("Info","Rainfall: ("+pTempHex.getRainfall(0)+","
                                    +pTempHex.getRainfall(1)+","
                                    +pTempHex.getRainfall(2)+","
                                    +pTempHex.getRainfall(3)+")");
                            logger.log("Info","Pressure: ("+pTempHex.getPressure(0)+","
                                    +pTempHex.getPressure(1)+","
                                    +pTempHex.getPressure(2)+","
                                    +pTempHex.getPressure(3)+")");
                            logger.log("Info","  Climate: "+pTempHex.getClimate()+"");
                            break;
                    }
                    break;
                case Hex.TERRAIN_OCEAN:
                case Hex.TERRAIN_DEEPOCEAN:
                    switch (pTempHex.getClimate())  {
                        case Hex.CLIMATE_OCEANICE:
                            pTempHex.setTerrainType(Hex.TERRAIN_ICE);
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    }


    private void computeTemperatures ()  {
        int iSeason, i, j;
        double lat, lscl, sscl, x, fact, theta, delth, phase;
        double [] tland;
        double [] tsea;
        float tmax = -32000.0f, tmin = 32000.0f;
        float fTemp;
        Hex pTempHex;
        tland = new double [pParams.iNumSeasons];
        tsea = new double [pParams.iNumSeasons];
        double DEG2RAD = (Math.PI / 180.0);
        float tscale;

        lscl = DEG2RAD * 180.0 / (90.0 + pParams.dLandTempTilt * pParams.dAxialTilt);
        sscl = DEG2RAD * 180.0 / (90.0 + pParams.dOceanTempTilt * pParams.dAxialTilt);
        delth = 2.0 * Math.PI / (double) pParams.iNumSeasons;
        for (j = 0; j < iDim; j++) {
            lat = 90.0 - 180.0 * (double) j / (double) iDim;
            for (iSeason=0, theta=0; iSeason < pParams.iNumSeasons; iSeason++, theta += delth)  {
                phase = theta + pParams.dEccentricityPhase;
                if (phase > 2.0 * Math.PI)  {
                    phase -= (2.0 * Math.PI);
                }
                fact = (1.0 + pParams.dOrbitalEccentricity * Math.cos (phase)) * pParams.TEMPSCALE;
                x = (lat + Math.cos (theta) * pParams.dAxialTilt * pParams.dLandTempTilt) * lscl;
                tland[iSeason] = (pParams.dBaseLandTemp + pParams.dLandTempVariance * Math.cos(x)) * fact;
                x = (lat + Math.cos (theta) * pParams.dAxialTilt * pParams.dOceanTempTilt) * sscl;
                tsea[iSeason] = (pParams.dBaseOceanTemp + pParams.dOceanTempVariance * Math.cos(x)) * fact;
            }
            for (i=0; i < iDim; i++) {
                int iTerrain = getHex(i,j).getTerrainType();
                if (   iTerrain == Hex.TERRAIN_OCEAN
                        || iTerrain == Hex.TERRAIN_DEEPOCEAN)  {
                    x = pParams.dOceanSmooth + (countland (i, j) / pParams.dOceanDiv);
                }
                else  {
                    x = pParams.dLandSmooth + (countland (i, j) / pParams.dLandDiv);
                }
                for (iSeason=0; iSeason < pParams.iNumSeasons; iSeason++)  {
                    fTemp = (float)(tsea[iSeason] + (tland[iSeason] - tsea[iSeason]) * x);
                    getHex(i,j).setTemperature(iSeason, fTemp);
                    if (fTemp < tmin)  {
                        tmin = fTemp;
                    }
                    if (fTemp > tmax)  {
                        tmax = fTemp;
                    }
                }
            }
        }

        int avgScaledTemp = 0;
        // Compute scale; for every Season, fill ts from tt
        tscale = 254.0f / (tmax - tmin);
        for (iSeason=0; iSeason < pParams.iNumSeasons; iSeason++) {
            for (i = 0; i < iDim; i++)  {
                for (j = 0; j < iDim; j++)  {
                    pTempHex = getHex(i,j);
                    pTempHex.setScaledTemperature(iSeason, (pTempHex.getTemperature(iSeason) - tmin) * tscale);
                    avgScaledTemp += (pTempHex.getTemperature(iSeason) - tmin) * tscale;
                }
            }
        }
        avgScaledTemp /= (iDim * iDim);
    }


    /**
     Called by computeTemperatures() for each hex, this function looks
     in a 11 wide by 5 high box and counts the number of land squares
     found there.  It compensates for y values off the map, and wraps x
     values around.
     */
    private int countland (int x, int y)  {
        int sum=0;
        int jmin, jmax, j1, i0, i1;
        Hex pTempHex;

        jmin = y - 2;
        if (jmin < 0)  {
            jmin = 0;
        }
        jmax = y + 2;
        if (jmax >= iDim)  {
            jmax = iDim-1;
        }
        for (j1 = jmin; j1 <= jmax; j1++)  {
            for (i0 = -5; i0 < 6; i0++) {
                i1 = i0 + x;
                if (i1 < 0)  {
                    i1 += iDim;
                }
                if (i1 >= iDim)  {
                    i1 -= iDim;
                }
                pTempHex = getHex(i1,j1);
                switch (pTempHex.getTerrainType())  {
                    case Hex.TERRAIN_OCEAN:
                    case Hex.TERRAIN_DEEPOCEAN:
                        break;
                    case Hex.TERRAIN_MOUNTAINS:
                    case Hex.TERRAIN_IMPASSABLEMOUNTAINS:
                        sum += 2;
                    default:
                        sum += 1;
                }
            }
        }
        return (sum);
    }


    /**
     This function is called by a number of climate routines.  It takes an
     input array with blobs of -1's on a background of 0's.  The function winds
     up replacing each 0 with the distance from that square to the nearest -1.
     The function onerange() does all the work, but it will not compute ranges
     greater than MAXRANGE.  Therefore, after onerange() is called, any
     remaining 0 values must be replaced with MAXRANGE, indicating that that
     square is "very far" from any -1 value.
     The main part of this routine consists of a loop.  Each time through the
     loop, every square is checked.  If the square is zero, it has not yet been
     updated.  In that case, look to see if any adjacent squares were previously
     updated (or if they were initialized to -1).  If so, set the square to the
     current distance value, which happens to be identical to the outer loop
     variable.  If, after one loop iteration, no squares have been updated, the
     matrix must be completely updated.  Stop.  To keep down run-time, a maximum
     distance value, MAXRANGE, is used as the terminating loop value.
     */
    private void computeRange (int [] iRanges)  {
        int i, j;
        int x, k, keepgo;

        for (k = 1; k < pParams.bMaxRange; k++)  {
            for (keepgo=0, j=0; j < iDim; j++)  {
                for (i = 0; i < iDim; i++)  {
                    if (iRanges[(j*iDim)+i] == 0) {
                        keepgo = 1;
                        x = iRanges[(j*iDim) + (i != 0 ? i-1 : iDim - 1)];
                        if (x != 0 && (x != k))  {
                            iRanges[(j*iDim)+i] = k;
                        }
                        x = iRanges[(j*iDim) + ((i<iDim-1) ? i+1 : 0)];
                        if (x != 0 && (x != k))  {
                            iRanges[(j*iDim)+i] = k;
                        }
                        if (j < iDim-1) {
                            x = iRanges[((j+1)*iDim)+i];
                            if (x != 0 && (x != k))  {
                                iRanges[(j*iDim)+i] = k;
                            }
                        }
                        if (j != 0) {
                            x = iRanges[((j-1)*iDim)+i];
                            if (x != 0 && (x != k))  {
                                iRanges[(j*iDim)+i] = k;
                            }
                        }
                    }
                }
            }
        }

        for (j = 0; j < iDim; j++)  {
            for (i = 0; i < iDim; i++)  {
                if (iRanges[(j*iDim)+i] == 0)  {
                    iRanges[(j*iDim)+i] = pParams.bMaxRange;
                }
            }
        }

    }


    private void computePressure ()  {
        int [] iRanges;
        int iSeason;
        int i, j;
        int x;
        Hex pTempHex;
        int sum, jlast = 0, jnext;

        iRanges = new int [iDim * iDim];
        for (iSeason=0; iSeason < pParams.iNumSeasons; iSeason++) {
            // Determine ocean highs and lows.  An ocean high or low must occur over
            // ocean, far away from major land masses.  Two calls to range() are made
            // to find the qualifying ocean areas; then temperature criteria are used
            // to select the actual pressure zones.

            // Set r to the distance on land from the coast.
            for (i = 0; i < iDim; i++)  {
                for (j = 0; j < iDim; j++)  {
                    switch (getHex(i,j).getTerrainType())  {
                        case Hex.TERRAIN_DEEPOCEAN:
                        case Hex.TERRAIN_OCEAN:
                            iRanges[(j * iDim) + i] = -1;
                            break;
                        default:
                            iRanges[(j * iDim) + i] = 0;
                            break;
                    }
                }
            }
            computeRange (iRanges);

            //  Initialize r to contain blobs on land which are at least OLTHRESH
            // squares away from the coast.  Then set r to the distance from these.
            // The result in r is the distance from the nearest big piece of land
            // (ignoring islands).
            for (i=0; i< (iDim * iDim); i++)  {
                iRanges[i] = (iRanges[i] > pParams.OLTHRESH) ? -1 : 0;
            }
            computeRange (iRanges);

            // For each array element, if it is at least OOTHRESH squares from the
            // nearest big piece of land, it might be the center of an ocean pressure
            // zone.  The pressure zones are defined by temperature ranges; if the
            // temperature in ts is between OLMIN and OLMAX, a low is recorded, while
            // if the temperature is between OHMIN and OHMAX, a high is recorded.
            for (j = 0; j < iDim; j++)  {
                for (i = 0; i < iDim; i++)  {
                    pTempHex = getHex(i,j);
                    pTempHex.setPressure(iSeason, (byte)0);
                    x = (int)(pTempHex.getScaledTemperature(iSeason));
                    if (iRanges[(j * iDim) + i] > pParams.OOTHRESH)  {
                        if ((x >= pParams.OLMIN) && (x <= pParams.OLMAX))  {
                            pTempHex.setPressure(iSeason, Hex.PRESSURE_LOW);
                        }
                        if ((x >= pParams.OHMIN) && (x <= pParams.OHMAX))  {
                            pTempHex.setPressure(iSeason, Hex.PRESSURE_HIGH);
                        }
                    }
                }
            }


            // find land highs and lows.  A land high or low must occur over land,
            // far from major oceans.  Two calls to range() are made to find the
            // qualifying land areas; then temperature criteria are used to select
            // the actual pressure zones.

            // Set r to distance on water from coast.
            for (j=0; j<iDim; j++)  {
                for (i=0; i<iDim; i++)  {
                    switch (getHex(i,j).getTerrainType())  {
                        case Hex.TERRAIN_DEEPOCEAN:
                        case Hex.TERRAIN_OCEAN:
                            iRanges[(j * iDim) + i] = 0;
                            break;
                        default:
                            iRanges[(j * iDim) + i] = -1;
                            break;
                    }
                }
            }
            computeRange (iRanges);

            // Initialize r to contain blobs on ocean which are at least LOTHRESH
            // squares away from the coast.  Then set r to the distance from these.
            // The result in r is the distance from the nearest ocean, ignoring
            // lakes.
            for (i=0; i< (iDim * iDim); i++)  {
                iRanges[i] = (iRanges[i] > pParams.LOTHRESH) ? -1 : 0;
            }
            computeRange (iRanges);

            // For each array element, if it is at least LLTHRESH squares from the
            // nearest large ocean, it might be the center of a land pressure zone.
            // The pressure zones are defined by temperature ranges; if the
            // temperature in ts is between LLMIN and LLMAX, a low is recorded,
            // while if the temperature is between LHMIN and LHMAX, a high is
            // recorded.
            for (j = 0; j < iDim; j++)  {
                for (i = 0; i < iDim; i++)  {
                    pTempHex = getHex(i,j);
                    x = (int)(pTempHex.getScaledTemperature(iSeason));
                    if (iRanges[(j*iDim)+i] > pParams.LLTHRESH)  {
                        if ((x >= pParams.LLMIN) && (x <= pParams.LLMAX))  {
                            pTempHex.setPressure(iSeason, Hex.PRESSURE_LOW);
                        }
                        if ((x >= pParams.LHMIN) && (x <= pParams.LHMAX))  {
                            pTempHex.setPressure(iSeason, Hex.PRESSURE_HIGH);
                        }
                    }
                }
            }

            // This function finds the heat equator and marks it in pr.  For each
            // vertical column of ts, the median position is found and marked.  To
            // make the heat equator continuous, jlast is set to the position of the
            // heat equator in the previous column; a connection is made in the
            // present column to ensure continuity.
            sum = 0;
            for (i = 0; i < iDim; i++)  {
                // Find the total of the temperatures in this column
                for (sum=0, j=0; j < iDim; j++)  {
                    sum += (int)(getHex(i,j).getScaledTemperature(iSeason));
                }
            }

            // Step through the column again until the total so far is exactly
            // half the total for the column.  This is the median position.
            for (sum >>= 1, j = 0; j < iDim && sum > 0; j++)  {
                sum -= (int)(getHex(i,j).getScaledTemperature(iSeason));
            }

            // Mark this position and remember it with jnext
            getHex(i,j).setPressure(iSeason, Hex.PRESSURE_HEQ);
            jnext = j;

            // For each column except the first (where i = 0), if the last heat
            // equator is above this one, move upwards to it, marking each square,
            //  to ensure continuity; if below this one, move downwards to it.
            if (i != 0 && (j > jlast))  {
                for (; j >= jlast; j--)  {
                    getHex(i,j).setPressure(iSeason, Hex.PRESSURE_HEQ);
                }
            }
            else if (i != 0 && (j < jlast))  {
                for (; j <= jlast; j++)  {
                    getHex(i,j).setPressure(iSeason, Hex.PRESSURE_HEQ);
                }
            }

            // Remember this position for the next column.  Note that no check is
            // done to ensure continuity at the wraparound point; this is bad.
            jlast = jnext;
        }
    }

    private void computeWind ()  {
        int [] iHighPressure;
        int [] iLowPressure;
        int [] iSmoothedPressure;
        int i, j, x, iSeason;
        int a, b, e, bar;
        iHighPressure = new int [iDim * iDim];
        iLowPressure = new int [iDim * iDim];
        iSmoothedPressure = new int [iDim * iDim];
        Hex pTempHex;

        // This is the main function in this file; it calls getpress() to create
        // a smoothed pressure map, then getwind() to put isobars (wind lines) on
        // the output map.  The last step makes sure that contradictory winds are
        // removed, such as N and S winds in the same square.

        for (iSeason = 0; iSeason < pParams.iNumSeasons; iSeason++) {
            // take the high and low markings from pressure.c and create
            // a smoothed function.  Highs turn into iMaxPressure and lows turn
            // into 0.

            for (j = 0; j < iDim; j++)  {
                for (i=0; i<iDim; i++) {
                    pTempHex = getHex(i,j);
                    // Zero out the arrays to be used
                    pTempHex.setWind(iSeason,(short)0);
                    iLowPressure[j*iDim+i] = 0;
                    iHighPressure[j*iDim+i] = 0;

                    // Fill hl[0] with the low pressure zones, and hl[1] with highs
                    if (pTempHex.getPressure(iSeason) == Hex.PRESSURE_LOW)  {
                        iLowPressure[j*iDim+i] = -1;
                    }
                    else if (pTempHex.getPressure(iSeason) == Hex.PRESSURE_HIGH)  {
                        iHighPressure[j*iDim+i] = -1;
                    }
                    else if (pTempHex.getPressure(iSeason) == Hex.PRESSURE_HEQ)  {
                        iLowPressure[j*iDim+i] = -1;
                    }
                }
            }

            // Set each square in hl[0] to the distance from that square to the
            // nearest low, and each square in hl[1] to the distance to a high.
            computeRange (iLowPressure);
            computeRange (iHighPressure);

            // The final pressure, in array p, is zero if a low is there and
            // iMaxPressure if a high is there.  Otherwise, the pressure in a square
            // is proportional to the ratio of (distance from the square to the
            // nearest low) to (total of distance from nearest high and nearest low).
            // This gives a smooth curve between the extremes.
            for (j = 0; j < iDim; j++)  {
                for (i = 0; i < iDim; i++)  {
                    pTempHex = getHex(i,j);
                    if (iHighPressure[j*iDim+i] == -1)  {
                        iSmoothedPressure[j*iDim+i] = pParams.iMaxPressure;
                    }
                    else if (iLowPressure[j*iDim+i] == -1)  {
                        iSmoothedPressure[j*iDim+i] = 0;
                    }
                    else   {
                        iSmoothedPressure[j*iDim+i] = (pParams.iMaxPressure*iLowPressure[j*iDim+i]) / (iLowPressure[j*iDim+i] + iHighPressure[j*iDim+i]);
                    }
                }
            }

            // Draws isobars around the pressure map created above.  These
            // isobars are the directions of wind flow.  The isobars are given a
            // direction depending on whether the square is above or below the heat
            // equator; north of the heat equator, the winds blow counterclockwise
            // out from a low, while south of it, the opposite is true.

            // Step from 0 to iMaxPressure by BARSEP; bar is the pressure for which
            // this isobar will be drawn.
            for (bar = pParams.BARSEP; bar <= pParams.iMaxPressure; bar += pParams.BARSEP)  {
                for (i = 0; i < iDim; i++)  {
                    for (e=0, j=0; j<iDim; j++) {
                        pTempHex = getHex(i,j);
                        // Set e if this square is south of the heat equator
                        a = iSmoothedPressure[j*iDim+i];
                        if (pTempHex.getPressure(iSeason) == 3)  {
                            e = 1;
                        }

                        // Provided the square is not at the top of the array, compare the
                        // pressure here to the pressure one square up.  This gives the
                        // direction of the wind in terms of east / west flow.
                        if (j != 0) {
                            b = iSmoothedPressure[(j-1)*iDim+i];
                            if ((a < bar) && (b >= bar))  {
                                pTempHex.setWind(iSeason, (short)(pTempHex.getWind(iSeason) | ((e != 0) ? Hex.WIND_E : Hex.WIND_W)));
                            }
                            if ((a >= bar) && (b < bar))  {
                                pTempHex.setWind(iSeason, (short)(pTempHex.getWind(iSeason) | ((e != 0) ? Hex.WIND_W : Hex.WIND_E)));
                            }
                        }

                        // Compare the pressure here to the pressure one square to the
                        // left (including wraparound); this gives the wind direction in
                        // terms of north / south flow.
                        b = (i != 0) ? iSmoothedPressure[j*iDim+(i-1)] : iSmoothedPressure[j*iDim+(iDim-1)];
                        if ((a < bar) && (b >= bar))  {
                            pTempHex.setWind(iSeason, (short)(pTempHex.getWind(iSeason) | ((e != 0) ? Hex.WIND_N : Hex.WIND_S)));
                        }
                        if ((a >= bar) && (b < bar))  {
                            pTempHex.setWind(iSeason, (short)(pTempHex.getWind(iSeason) | ((e != 0) ? Hex.WIND_S : Hex.WIND_N)));
                        }
                    }
                }
            }

            for (j = 0; j < iDim; j++)  {
                for (i = 0; i < iDim; i++) {
                    pTempHex = getHex(i,j);
                    x = pTempHex.getWind(iSeason);
                    if ((x & Hex.WIND_N) != 0)  {
                        x &= (~Hex.WIND_S);
                    }
                    if ((x & Hex.WIND_E) != 0)  {
                        x &= (~Hex.WIND_W);
                    }
                    pTempHex.setWind(iSeason, (short)x);
                }
            }
        }
    }


    /**
     There are two distinct parts to this routine.
     "Fetch" is the term that describes how many squares a given wind line
     travels over water.  It measures how moist the wind is.  The algorithm to
     measure fetch looks like many simultaneous tree walks, where each water
     square is a root square, and every wind edge is a tree edge.  A counter
     for each square determines how many times that square is reached during
     the tree walks; that is the fetch.
     Once the fetch array is computed, this function looks at each square to
     determine the amount of rainfall there.  The above macro is called five
     times, once for the square and each of its four neighbors; this determines
     whether the square is near the ITCZ or the flank of an air cycle.  The
     sum of fetches for the neighbors is also determined.   Finally, each of the
     factors is weighted and added to the rainfall value:  the local fetch
     value, a land factor, the nearness of the heat equator, and the nearness
     of a flank.  Note that while rn is zeroed in getfetch(), it may be
     increased by rain falling on mountains, so it is nonzero when this
     function is called.
     */
    private void computeRainfall()  {
        int [][] fr;
        int [] fs;
        int iSeason;
        int i, j, k;
        int src, dest;
        int x;
        int itcz, flank;
        Hex pTempHex;
        fr = new int[2][iDim*iDim];
        fs = new int [iDim * iDim];

        for (iSeason = 0; iSeason < pParams.iNumSeasons; iSeason++) {
            // Initialize the counter fs to zero.  Array fr, which records the
            // list of active edges in the walks, is set so that all ocean squares
            // are active.  Also, the result array rn is cleared.
            for (i=0; i<iDim; i++)  {
                for (j=0; j<iDim; j++) {
                    switch (getHex(i,j).getTerrainType())  {
                        case Hex.TERRAIN_DEEPOCEAN:
                        case Hex.TERRAIN_OCEAN:
                            fr[0][j*iDim+i] = 1;
                            break;
                        default:
                            fr[0][j*iDim+i] = 0;
                            break;
                    }
                    fs[j*iDim+i] = 0;
                    getHex(i,j).setRainfall(iSeason, (short)0);
                }
            }

            // Each time through the loop, each square is examined.  If it's
            // active, disable the mark in the current time step (thus ensuring
            // that when the buffers are flipped, the new destination is empty).
            // If the square is a mountain, don't pass the mark, but instead add
            // some amount to the square -- implementing rain shadows and rainy
            // mountain squares.  Finally, for each of the eight cardinal
            // directions, if there is wind blowing in that direction, carry a
            // marker to that square using fetchinc(), above.
            for (k = 0; k < pParams.MAXFETCH; k++) {
                src = k % 2;
                dest = 1 - src;
                for (i = 0; i < iDim; i++)  {
                    for (j = 0; j < iDim; j++)  {
                        pTempHex = getHex(i,j);
                        if ((fr[src][j*iDim+i]) != 0)  {
                            fr[src][j*iDim+i] = 0;
                            switch(pTempHex.getTerrainType())  {
                                case Hex.TERRAIN_MOUNTAINS:
                                case Hex.TERRAIN_IMPASSABLEMOUNTAINS:
                                    pTempHex.setRainfall(iSeason, (short)(pTempHex.getRainfall(iSeason) + pParams.MOUNTDEL));
                                    break;
                                default:
                                    switch (pTempHex.getWind(iSeason))  {
                                        case Hex.WIND_N|Hex.WIND_E:
                                            fetchinc (i+1, j-1, fr[dest], fs);
                                            break;
                                        case Hex.WIND_N|Hex.WIND_W:
                                            fetchinc (i-1, j-1, fr[dest], fs);
                                            break;
                                        case Hex.WIND_S|Hex.WIND_E:
                                            fetchinc (i+1, j+1, fr[dest], fs);
                                            break;
                                        case Hex.WIND_S|Hex.WIND_W:
                                            fetchinc (i-1, j+1, fr[dest], fs);
                                            break;
                                        case Hex.WIND_N:
                                            fetchinc (i, j-1, fr[dest], fs);
                                            break;
                                        case Hex.WIND_S:
                                            fetchinc (i, j+1, fr[dest], fs);
                                            break;
                                        case Hex.WIND_E:
                                            fetchinc (i+1, j, fr[dest], fs);
                                            break;
                                        case Hex.WIND_W:
                                            fetchinc (i-1, j, fr[dest], fs);
                                            break;
                                    }
                            }
                        }
                    }
                }
            }

            // For each square around the current one, this
            // simply tests the square for several conditions: if the square is on
            // the heat equator, itcz is set to one; if the wind blows south in this
            // square, it is on the flank of a circular wind zone (and thus less
            // rainy); the local rain sum, x, is increased according to the fetch
            // sum in the square.
            for (i = 0; i < iDim; i++)  {
                for (j = 0; j < iDim; j++) {
                    flank = 0;
                    itcz = 0;
                    x = getHex(i,j).getRainfall(iSeason);
                    if (i < iDim-1)  {
                        pTempHex = getHex(i+1,j);
                        if (pTempHex.getPressure(iSeason) == Hex.PRESSURE_HEQ)  {
                            itcz = 1;
                        }
                        if ((pTempHex.getWind(iSeason) & Hex.WIND_S) != 0)  {
                            flank = 1;
                        }
                        x += fs[j*iDim+(i+1)] + pParams.NRFDEL;
                    }
                    else {
                        pTempHex = getHex(0,j);
                        if (pTempHex.getPressure(iSeason) == Hex.PRESSURE_HEQ)  {
                            itcz = 1;
                        }
                        if ((pTempHex.getWind(iSeason) & Hex.WIND_S) != 0)  {
                            flank = 1;
                        }
                        x += fs[j*iDim] + pParams.NRFDEL;
                    }
                    if (i != 0) {
                        pTempHex = getHex(i-1,j);
                        if (pTempHex.getPressure(iSeason) == Hex.PRESSURE_HEQ)  {
                            itcz = 1;
                        }
                        if ((pTempHex.getWind(iSeason) & Hex.WIND_S) != 0)  {
                            flank = 1;
                        }
                        x += fs[j*iDim+(i-1)] + pParams.NRFDEL;
                    }
                    else {
                        pTempHex = getHex(iDim-1,j);
                        if (pTempHex.getPressure(iSeason) == Hex.PRESSURE_HEQ)  {
                            itcz = 1;
                        }
                        if ((pTempHex.getWind(iSeason) & Hex.WIND_S) != 0)  {
                            flank = 1;
                        }
                        x += fs[j*iDim+(iDim-1)] + pParams.NRFDEL;
                    }
                    if (j < iDim-1) {
                        pTempHex = getHex(i,j+1);
                        if (pTempHex.getPressure(iSeason) == Hex.PRESSURE_HEQ)  {
                            itcz = 1;
                        }
                        if ((pTempHex.getWind(iSeason) & Hex.WIND_S) != 0)  {
                            flank = 1;
                        }
                        x += fs[(j+1)*iDim+i] + pParams.NRFDEL;
                    }
                    if (j != 0) {
                        pTempHex = getHex(i,j-1);
                        if (pTempHex.getPressure(iSeason) == Hex.PRESSURE_HEQ)  {
                            itcz = 1;
                        }
                        if ((pTempHex.getWind(iSeason) & Hex.WIND_S) != 0)  {
                            flank = 1;
                        }
                        x += fs[(j-1)*iDim+i] + pParams.NRFDEL;
                    }
                    pTempHex = getHex(i,j);
                    if (pTempHex.getPressure(iSeason) == Hex.PRESSURE_HEQ)  {
                        itcz = 1;
                    }
                    if ((pTempHex.getWind(iSeason) & Hex.WIND_S) != 0)  {
                        flank = 1;
                    }
                    x += fs[j*iDim+i] + pParams.NRFDEL;

                    x += (pParams.RAINCONST + pParams.FETCHDEL * fs[j*iDim+i]);
                    switch (pTempHex.getTerrainType())  {
                        case Hex.TERRAIN_DEEPOCEAN:
                        case Hex.TERRAIN_OCEAN:
                            break;
                        default:
                            x += pParams.LANDEL;
                            break;
                    }
                    if (pTempHex.getPressure(iSeason) == Hex.PRESSURE_HEQ)  {
                        x += pParams.HEQDEL;
                    }
                    if (itcz != 0)  {
                        x += pParams.NRHEQDEL;
                    }
                    if (flank != 0)  {
                        x += pParams.FLANKDEL;
                    }
                    if (x < 0)  {
                        x = 0;
                    }
                    if (x> 255)  {
                        x = 255;
                    }
                    pTempHex.setRainfall(iSeason, (short)x);
                }
            }
        }
    }


    /**
     This is the workhorse function for rain-fetch(). It is called
     several times per square.  It changes x to account for wraparound.
     If y is out of range it does nothing, else it
     "marks" the new square in fr[dest] and increments fs to record the number
     of times the square has been marked.
     */
    private void fetchinc (int x, int y, int [] fr, int [] fs)  {
        if (x == -1)  {
            x = iDim-1;
        }
        else if (x == iDim)  {
            x = 0;
        }
        if ((y == -1) || (y == iDim))  {
            return;
        }
        fr[y*iDim+x] = 1;
        fs[y*iDim+x]++;
        return;
    }


    /**
     The outer loop looks at each square.  If it is ocean, the climate will
     be ocean unless the temperature is below ICEBREGK degrees all year round.
     If it is land, then the average rainfall and temperature (in Farenheit)
     are computed for the square.  If the square is mountain, it is colder; the
     temperature is decreased.  These two figures are then turned into array
     indices by using the tempcut and raincut parameter vectors.  The climate
     for the square is then simply a table lookup.
     */
    private void computeClimate()  {
        int i, j, iSeason;
        int noice, averain, ttt, r;
        double avetemp;
        // This array is the heart of the climate routine; temperature increases
        // going down the array, and rainfall increases going from left to right.
        byte climkey[][] = {
                { Hex.CLIMATE_TUNDRA, Hex.CLIMATE_TUNDRA, Hex.CLIMATE_TUNDRA, Hex.CLIMATE_TUNDRA, Hex.CLIMATE_TUNDRA },
                { Hex.CLIMATE_STEPPE, Hex.CLIMATE_STEPPE, Hex.CLIMATE_DECIDUOUS, Hex.CLIMATE_DECIDUOUS,  Hex.CLIMATE_DECIDUOUS  },
                { Hex.CLIMATE_DESERT, Hex.CLIMATE_PRAIRIE, Hex.CLIMATE_DECIDUOUS, Hex.CLIMATE_JUNGLE, Hex.CLIMATE_SWAMP  },
                { Hex.CLIMATE_DESERT, Hex.CLIMATE_SAVANNAH, Hex.CLIMATE_JUNGLE, Hex.CLIMATE_SWAMP,  Hex.CLIMATE_SWAMP  }
        };
        short TCSIZE = 4, RCSIZE = 5;
        Hex pTempHex;

        for (j = 0; j < iDim; j++)  {
            for (i = 0; i < iDim; i++) {
                pTempHex = getHex(i,j);
                switch (pTempHex.getTerrainType())  {
                    case Hex.TERRAIN_DEEPOCEAN:
                    case Hex.TERRAIN_OCEAN:
                        for (noice = 0, iSeason = 0; iSeason < pParams.iNumSeasons; iSeason++)  {
                            noice |= ((pTempHex.getTemperature(iSeason) > pParams.TEMPSCALE * pParams.ICEBERGK) ? 1 : 0);
                        }
                        pTempHex.setClimate((noice != 0) ?
                                Hex.CLIMATE_OCEAN : Hex.CLIMATE_OCEANICE);
                        break;
                    default:
                        for (averain = 0, avetemp = 0, iSeason = 0;
                             iSeason < pParams.iNumSeasons;
                             iSeason++)
                        {
                            averain += pTempHex.getRainfall(iSeason);
                            avetemp += (double)pTempHex.getTemperature(iSeason);
                        }
                        averain /= pParams.iNumSeasons;
                        avetemp /= (double)pParams.iNumSeasons;
                        avetemp = ((double) (avetemp / (double)pParams.TEMPSCALE) - 273.0) * 1.8 + 32.0;
                        switch (pTempHex.getTerrainType())  {
                            case Hex.TERRAIN_MOUNTAINS:
                            case Hex.TERRAIN_IMPASSABLEMOUNTAINS:
                                avetemp -= (double)pParams.MTDELTA;
                                break;
                        }
                        ttt = 0;
                        while ((avetemp > pParams.TEMPCUT[ttt]) && (ttt < TCSIZE-1))  {
                            ttt++;
                        }
                        r  = 0;
                        while ((averain > pParams.RAINCUT[r])  && (r  < RCSIZE-1))  {
                            r++;
                        }
                        pTempHex.setClimate(climkey[ttt][r]);
                        break;
                }
            }
        }
    }


    public void createRivers()  {

    }

    public void findShorelines ()  {
        Hex pTempHex, pTempNeighborHex;
        int i,x,y;

        for (x = 0; x < iDim; x++)  {
            for (y = 0; y < iDim; y++)  {
                pTempHex = getHex(x,y);
                pTempHex.clearShorelines();
                for (i = 0; i < 6; i++)  {
                    if (pTempHex.isLand())  {
                        pTempNeighborHex = pTempHex.getNeighbor(i);
                        if (   pTempNeighborHex != null
                                && pTempNeighborHex.isWater())  {
                            pTempHex.setShoreline(Hex.BW_DIRS[i], true);
                        }
                    }
                }
            }
        }
    }


    public void reCreateWorld ()  {
        assignElevationBasedTerrain();
        assignClimateBasedTerrain();
        createRivers();
        findShorelines();
    }


    /**
     * Main routine, generates all of the world information
     */
    public void generateWorld ()
    {
        logger.log("Info", "Generating map of dimension: " + getDimension() + ":" + getDimension());

        logger.log("Info","Generating Elevations");
        generateElevations ();
        iProgress = 20;
        logger.log("Info","Assigning Elevation-based Terrain");
        assignElevationBasedTerrain();
        iProgress = 40;
        logger.log("Info","Assigning Climate-based Terrain");
        assignClimateBasedTerrain();
        iProgress = 60;
        logger.log("Info","Creating River Networks");
        createRivers();
        iProgress = 80;
        logger.log("Info","Find Shorelines");
        findShorelines();
        iProgress = 100;
        logger.log("Info","Done");
    }

}



