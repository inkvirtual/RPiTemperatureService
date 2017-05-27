package Resources;

/**
 * Created by fanta on 5/27/17.
 */
public class ResourcesHelper {
//    private static final String DEFAULT_RESOURCES_PATH = "/home/pi/HomeAutomation/RPiTemperatureService/";
    private String resourcesPath;

//    public ResourcesHelper() {
//        init(DEFAULT_RESOURCES_PATH);
//    }

    public ResourcesHelper(String resourcesPath) {
        init(resourcesPath);
    }

    private void init(String resourcesPath) {
        this.resourcesPath = normalizePath(resourcesPath);
    }

    private String normalizePath(String path) {
        if (path.charAt(path.length() - 1) != '/')
            path += '/';
        return path;
    }

    public String getResourcesPath() {
        return resourcesPath;
    }

    //    private String getResourcesPath() {
//        try {
//            String resorcesPath = System.getProperty("rpi_temp_service_resources_path");
//            if (null == resorcesPath || resorcesPath.length() == 0) {
//                System.err.println("ERROR: null or empty \"rpi_temp_service_resources_path\" property");
//                return null;
//            }
//
//            if (resorcesPath.charAt(resorcesPath.length() - 1) != '/')
//                resorcesPath += '/';
//
//            return resorcesPath;
//        } catch (Exception ex) {
//            System.err.println(ex.toString());
//            return null;
//        }
//    }

    public String getFullPath(String fileName) {
        if (null == fileName || fileName.length() == 0) {
            System.err.println("null or empty filename");
            return null;
        }

        return resourcesPath + fileName;
    }


}
