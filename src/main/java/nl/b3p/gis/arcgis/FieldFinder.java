package nl.b3p.gis.arcgis;

import java.lang.reflect.Field;

public class FieldFinder {

    public static String findConstantFieldByValue(Class clazz, Object value) throws Exception {
        Field[] fields = clazz.getDeclaredFields();
        for(int i = 0; i < fields.length; i++) {
            if(fields[i].get(null).equals(value)) {
                return fields[i].getName();
            }
        }
        return null;
    }
}
