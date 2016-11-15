package nl.b3p.gis.arcgis;

import com.esri.arcgis.system.AoInitialize;
import com.esri.arcgis.system.EngineInitializer;
import com.esri.arcgis.system.esriLicenseStatus;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.List;

public class ArcGISInitializer {
    private static AoInitialize aoInit = null;

    private static List<String> homeEnvVars = Arrays.asList(new String[] {
        "AGSENGINEJAVA", "AGSDESKTOPJAVA", "ARCGISHOME"});

    public static void init() throws Exception {
        String arcObjectsHome = null;
        for(String s: homeEnvVars) {
            arcObjectsHome = System.getenv(s);
            if(arcObjectsHome != null) {
                break;
            }
        }

        if (arcObjectsHome == null) {
            throw new Exception("Could not find ArcObjects home in environment variables " + homeEnvVars + ". "
                        + (System.getProperty("os.name").toLowerCase().indexOf("win") > -1
                        ? "ArcGIS Engine Runtime or ArcGIS Desktop must be installed"
                        : "ArcGIS Engine Runtime must be installed"));
        }

        init(arcObjectsHome);
    }

    public static void init(String arcObjectsHome) throws Exception {

        String jarPath = arcObjectsHome + File.separator + "java" + File.separator + "lib" + File.separator + "arcobjects.jar";
        File jarFile = new File(jarPath);

        if(!jarFile.exists()) {
            throw new Exception("Error: could not find arcobjects.jar at path \"" + jarFile.getAbsolutePath() + "\"");
        }

        System.out.printf("Using ArcObjects home \"%s\"\n", arcObjectsHome);

        //Helps load classes and resources from a search path of URLs
        URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
        Class<URLClassLoader> sysclass = URLClassLoader.class;

        try {
            Method method = sysclass.getDeclaredMethod("addURL", new Class[]{URL.class});
            method.setAccessible(true);
            method.invoke(sysloader, new Object[]{jarFile.toURI().toURL()});
        } catch (Throwable throwable) {
            throw new Exception("Could not add arcobjects.jar to system classloader", throwable);
        }

        //Initialize engine console application
        EngineInitializer.initializeEngine();

        //Initialize ArcGIS license
        aoInit = new AoInitialize();
        initializeArcGISLicenses();
    }

    public static void shutdown() throws Exception {
        //Ensure any ESRI libraries are unloaded in the correct order
        if (aoInit != null) {
            aoInit.shutdown();
        }
    }

    private static void initializeArcGISLicenses() throws Exception {
        System.out.println("Searching ArcGIS license...");
        
        // these belong to arcgis 10.0.0 (arcobjects included)
        if (hasLicense("esriLicenseProductCodeArcEditor")) {
            return;
        } else if (hasLicense("esriLicenseProductCodeArcView")) {
            return;
        } else if (hasLicense("esriLicenseProductCodeArcInfo")) {
            return;
            
        // these belong to 10.3.1 (arcobjects included)            
        } else if (hasLicense("esriLicenseProductCodeBasic")) {
            return;
        } else if (hasLicense("esriLicenseProductCodeStandard")) {
            return;
        } else if (hasLicense("esriLicenseProductCodeAdvanced")) {
            return;
            
        // these are general     
        } else if (hasLicense("esriLicenseProductCodeEngine")) {
            return;
        } else if (hasLicense("esriLicenseProductCodeEngineGeoDB")) {
            return;
        } else if (hasLicense("esriLicenseProductCodeArcServer")) {
            return;
        }
            
        throw new Exception("Could not initialize any ESRI license.");
    }
    
    private static boolean hasLicense(String l) throws ClassNotFoundException {
        Class c = Class.forName("com.esri.arcgis.system.esriLicenseProductCode");
//        System.out.println("Trying ArcGIS License: " + l);
        try {
            Field f = c.getDeclaredField(l);
            if (aoInit.isProductCodeAvailable(f.getInt(null)) == esriLicenseStatus.esriLicenseAvailable) {
                System.out.println("ArcGIS License used: " + l);
                aoInit.initialize(f.getInt(null));
                return true;
            }
        } catch (Exception ex) {}
        return false;
    }
}
