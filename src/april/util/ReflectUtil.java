package april.util;

import java.util.*;
import java.io.*;
import java.lang.reflect.*;

/** Utility classes for java reflection. **/
public class ReflectUtil
{
    public static Object createObject(String className)
    {
        try {
            Class cls = Class.forName(className);
            Object o = cls.getConstructor().newInstance();
            return o;
        } catch (Exception ex) {
//            System.out.println("ReflectUtil.createObject ex: "+ex);
//            ex.printStackTrace();
            return null;
        }
    }


    public static Object createObject(String className, Object ... args)
    {
        try {
            Class cls = Class.forName(className);
            Class clss[] = new Class[args.length];
            for (int i = 0; i < args.length; i++)
                clss[i] = args[i].getClass();

            Object o = cls.getConstructor(clss).newInstance(args);
            return o;
        } catch (Exception ex) {
           // System.out.println("ReflectUtil.createObject ex: "+ex);
           // ex.printStackTrace();
            return null;
        }
    }


}
