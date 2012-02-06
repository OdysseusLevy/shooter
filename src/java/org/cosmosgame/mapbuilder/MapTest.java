package org.cosmosgame.mapbuilder;

import java.io.*;

/**
 * Author: odysseus
 * Date: 2/4/12
 */
public class MapTest
{
    
    public static void main(String args[]) throws IOException {

        MapParams params = new MapParams();
        MapBuilder builder = new MapBuilder();
        builder.init(params);

        builder.generateWorld();

        File f = new File("dump.text");
        f.createNewFile();

        FileWriter writer = new FileWriter(f);
        dump(writer, builder);
    }

    public static void dump(OutputStreamWriter out, MapBuilder builder)
    {
        PrintWriter writer = new PrintWriter(out);

        int x = 0;
        int y = 0;
        for( y = 0; y < builder.getDimension(); y++)
        {
            writer.print(y + "\t");
            for (x = 0; x < builder.getDimension(); x++)
            {
                Hex h = builder.getHex(x, y);
                writer.print(h.getTerrainShortName());
            }

            writer.println();
        }
    }
}
