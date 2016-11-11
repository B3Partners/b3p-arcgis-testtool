package nl.b3p.gis.arcgis;

import com.esri.arcgis.datasourcesGDB.FileGDBWorkspaceFactory;
import com.esri.arcgis.datasourcesGDB.SdeWorkspaceFactory;
import com.esri.arcgis.datasourcesfile.ShapefileWorkspaceFactory;
import com.esri.arcgis.geodatabase.IDataset;
import com.esri.arcgis.geodatabase.IEnumDataset;
import com.esri.arcgis.geodatabase.IWorkspaceFactory;
import com.esri.arcgis.geodatabase.Workspace;
import com.esri.arcgis.geodatabase.esriDatasetType;
import com.esri.arcgis.system.IPropertySet;
import org.apache.commons.cli.CommandLine;

public class WorkspaceDatasetLister {

    public void list(CommandLine cl) throws Exception {
        Workspace ws = null;

        if(cl.hasOption("fgdb")) {
            String fgdb = cl.getOptionValue("fgdb");
            System.out.printf("Opening FGDB workspace from file \"%s\"...\n", fgdb);
            IWorkspaceFactory factory = new FileGDBWorkspaceFactory();
            ws = new Workspace(factory.openFromFile(fgdb, 0));
        } else if(cl.hasOption("shape")) {
            String shape = cl.getOptionValue("shape");
            System.out.printf("Opening shapefile workspace from directory \"%s\"...\n", shape);
            IWorkspaceFactory factory = new ShapefileWorkspaceFactory();
            ws = new Workspace(factory.openFromFile(shape, 0));
        } else if(cl.hasOption("sdefile")) {
            String file = cl.getOptionValue("sdefile");
            System.out.printf("Opening SDE workspace from connection file \"%s\"...\n", file);
            IWorkspaceFactory factory = new SdeWorkspaceFactory();
            ws = new Workspace(factory.openFromFile(file, 0));
        } else if(cl.hasOption("sde")) {
            String connectionString = cl.getOptionValue("sde");
            System.out.printf("Opening SDE workspace using connection string \"%s\"...\n", connectionString);
            SdeWorkspaceFactory factory = new SdeWorkspaceFactory();
            ws = new Workspace(factory.openFromString(connectionString, 0));
        }

        System.out.println("Workspace: " + ws.getName());

        System.out.println("Connection properties: ");
        IPropertySet ps = ws.getConnectionProperties();
        Object[] names = new Object[1], values = new Object[1];
        ps.getAllProperties(names, values);
        names = (Object[])names[0];
        values = (Object[])values[0];
        for(int i = 0; i < ps.getCount(); i++) {
            System.out.printf("%s%s=%s", i > 0 ? ";" : "", names[i], values[i]);
        }
        System.out.println("\n");

        listDatasets(ws);

        ws.release();

    }

    private static void listDatasets(Workspace ws) throws Exception {
        IEnumDataset enumDataset = ws.getSubsets();
        IDataset ds;
        System.out.println("Listing datasets:");
        while ((ds = enumDataset.next()) != null) {
            System.out.printf("  %s (type %s)\n", ds.getName(), FieldFinder.findConstantFieldByValue(esriDatasetType.class, ds.getType()));
        }
    }
}
