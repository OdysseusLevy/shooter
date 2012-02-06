package org.cosmosgame.mapbuilder;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Author: odysseus
 * Date: 2/4/12
 */
public class Logger
{
    public String log(String level, String info)
    {
        String out = level + ":" + info + "\n";
        System.out.print (out);
        
        return out;
    }
    
    public String error(String error)
    {
        return error(new Exception(error));    
    }
    
    public String error( Exception e)
    {
        System.out.print(e.getMessage() + "\n");
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));

        e.printStackTrace();
        
        return writer.toString();
    }
}
