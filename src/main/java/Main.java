import Configuration.Configuration;
import Configuration.IConfiguration;
import Resources.ResourcesHelper;
import Service.IService;
import Service.RPiTemperatureService;

/**
 * Created by dev on 28.03.2017.
 */
public class Main {
    private static final String DEFAULT_RESOURCES_PATH = "/home/pi/HomeAutomation/RPiTemperatureService";
    private static final String SERVICE_PROPERTIES = "rpi_temperature_service.properties";

    public static void main(String[] args) {
        Configuration configuration = initConfiguration(args);

        IService service = new RPiTemperatureService(configuration);
        service.start();
    }

    protected static Configuration initConfiguration(String[] args) {
        String resourcesPath = getResourcesPath(args);

        ResourcesHelper resourcesHelper = new ResourcesHelper(resourcesPath);
        String propertiesFullPath = resourcesHelper.getFullPath(SERVICE_PROPERTIES);

        return new Configuration(resourcesHelper, propertiesFullPath);
    }

    private static String getResourcesPath(String[] args) {
        boolean useDefaultResPath = false;

        if (null != args && args.length > 1) {
            System.err.println("too many arguments provided");
            useDefaultResPath = true;
        } else if (null == args || args.length == 0) {
            System.out.print("no argument provided");
            useDefaultResPath = true;
        } else {
            System.out.print("using resources path:" + args[0]);
        }

        if (useDefaultResPath) {
            System.out.println("Using default resources path:" + DEFAULT_RESOURCES_PATH);
            return DEFAULT_RESOURCES_PATH;
        }

        return args[0];
    }
}