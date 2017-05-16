/**
 * Created by dev on 28.03.2017.
 */
public class RPi {
    public static void shutdown() {
//        PiBash.execute("shutdown.sh");
    }

    private static String getResourcesPath() {
        try {
            String resorcesPath = System.getProperty("rpi_temp_service_resources_path");
            if (null == resorcesPath || resorcesPath.length() == 0) {
                System.err.println("null or empty \"rpi_temp_service_resources_path\" property");
                return null;
            }

            if (resorcesPath.charAt(resorcesPath.length() - 1) != '/')
                resorcesPath += '/';

            return resorcesPath;
        } catch (Exception ex) {
            System.err.println(ex.toString());
            return null;
        }
    }

    public static String getResource(String fileName) {
        if (null == fileName || fileName.length() == 0) {
            System.err.println("null or empty filename");
            return null;
        }

        return getResourcesPath() + fileName;
    }
}
