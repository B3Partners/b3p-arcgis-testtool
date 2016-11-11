package nl.b3p.gis.arcgis;

import org.apache.commons.cli.*;

public class ListWorkspaceMain {

    private static Options buildOptions() {
        Options options = new Options();
        options.addOption(OptionBuilder.withDescription("ArcObjects home directory (optioneel)").withArgName("dir").hasArg(true).create("home"));
        options.addOption(OptionBuilder.withDescription("Open file geodatabase").withArgName("dir").hasArg(true).create("fgdb"));
        options.addOption(OptionBuilder.withDescription("Open shapefiles in dir").withArgName("dir").hasArg(true).create("shape"));
        options.addOption(OptionBuilder.withDescription("Open SDE connectie op basis van .sde bestand").withArgName("bestand").hasArg(true).create("sdefile"));
        options.addOption(OptionBuilder.withDescription("Open SDE connectie op basis van connection string").withArgName("connection string").hasArg(true).create("sde"));
        return options;
    }

    public static void main(String[] args) throws Exception {

        Options options = buildOptions();
        CommandLine cl = null;
        try {
            CommandLineParser parser = new PosixParser();

            cl = parser.parse(options, args);
        } catch(ParseException e) {
            System.out.printf("%s\n\n", e.getMessage());

            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("b3p-arcgis-testtool", options );
            System.exit(1);
        }    

        int c = (cl.hasOption("fgdb") ? 1 : 0) + (cl.hasOption("shape") ? 1 : 0) + (cl.hasOption("sdefile") ? 1 : 0) + (cl.hasOption("sde") ? 1 : 0);

        if(c != 1) {
            System.out.println("Exactly one of the -fgdb, -shape, -sdefile or -sde options must be specified.\n");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("b3p-arcgis-testtool", options );
            System.exit(1);
        }

        if(cl.hasOption("home")) {
            ArcGISInitializer.init(cl.getOptionValue("home"));
        } else {
            ArcGISInitializer.init();
        }
        System.out.println("");

        try {
            new WorkspaceDatasetLister().list(cl);
        } finally {
            ArcGISInitializer.shutdown();
        }
    }
}

