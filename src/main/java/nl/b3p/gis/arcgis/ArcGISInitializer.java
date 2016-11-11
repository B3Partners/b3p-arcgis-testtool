package nl.b3p.gis.arcgis;

import com.esri.arcgis.system.AoInitialize;
import com.esri.arcgis.system.EngineInitializer;
import com.esri.arcgis.system.esriLicenseProductCode;
import com.esri.arcgis.system.esriLicenseStatus;
import java.io.File;
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
        
        // these belong to arcgis 10.0.0 (arcobjects lin included)
//        if (aoInit.isProductCodeAvailable(esriLicenseProductCode.esriLicenseProductCodeArcEditor)
//                == esriLicenseStatus.esriLicenseAvailable) {
//            System.out.println("ArcGIS License used: ArcEditor");
//            aoInit.initialize(esriLicenseProductCode.esriLicenseProductCodeArcEditor);
//        } else if (aoInit.isProductCodeAvailable(esriLicenseProductCode.esriLicenseProductCodeArcView)
//                == esriLicenseStatus.esriLicenseAvailable) {
//            System.out.println("ArcGIS License used: ArcView");
//            aoInit.initialize(esriLicenseProductCode.esriLicenseProductCodeArcView);
//        } else if (aoInit.isProductCodeAvailable(esriLicenseProductCode.esriLicenseProductCodeArcInfo)
//                == esriLicenseStatus.esriLicenseAvailable) {
//            System.out.println("ArcGIS License used: ArcInfo");
//            aoInit.initialize(esriLicenseProductCode.esriLicenseProductCodeArcInfo);
        
        // these belong to 10.3.1 (arcobjects lin included)
        if (aoInit.isProductCodeAvailable(esriLicenseProductCode.esriLicenseProductCodeBasic)
                == esriLicenseStatus.esriLicenseAvailable) {
            System.out.println("ArcGIS License used: Basic");
            aoInit.initialize(esriLicenseProductCode.esriLicenseProductCodeBasic);
        } else if (aoInit.isProductCodeAvailable(esriLicenseProductCode.esriLicenseProductCodeStandard)
                == esriLicenseStatus.esriLicenseAvailable) {
            System.out.println("ArcGIS License used: Standard");
            aoInit.initialize(esriLicenseProductCode.esriLicenseProductCodeStandard);
        } else if (aoInit.isProductCodeAvailable(esriLicenseProductCode.esriLicenseProductCodeAdvanced)
                == esriLicenseStatus.esriLicenseAvailable) {
            System.out.println("ArcGIS License used: Advanced");
            aoInit.initialize(esriLicenseProductCode.esriLicenseProductCodeAdvanced);
            
        } else if (aoInit.isProductCodeAvailable(esriLicenseProductCode.esriLicenseProductCodeEngine)
                == esriLicenseStatus.esriLicenseAvailable) {
            System.out.println("ArcGIS License used: Engine");
            aoInit.initialize(esriLicenseProductCode.esriLicenseProductCodeEngine);
        } else if (aoInit.isProductCodeAvailable(esriLicenseProductCode.esriLicenseProductCodeEngineGeoDB)
                == esriLicenseStatus.esriLicenseAvailable) {
            System.out.println("ArcGIS License used: EngineGeoDB");
            aoInit.initialize(esriLicenseProductCode.esriLicenseProductCodeEngineGeoDB);
        } else if (aoInit.isProductCodeAvailable(esriLicenseProductCode.esriLicenseProductCodeArcServer)
                == esriLicenseStatus.esriLicenseAvailable) {
            System.out.println("ArcGIS License used: ArcServer");
            aoInit.initialize(esriLicenseProductCode.esriLicenseProductCodeArcServer);            
        } else {
            throw new Exception("Could not initialize any ESRI license.");
        }
    }
}
