package RaspberryPi;

/**
 * Created by dev on 28.03.2017.
 */
public class RPi {
    private PiBash bash;

    public RPi() {
        this.bash = new PiBash();
    }

    public void shutdown() {
        bash.execute(getResourceContent("shutdown.sh"));
    }

    private String getResourcesPath() {
        try {
            String resorcesPath = System.getProperty("rpi_temp_service_resources_path");
            if (null == resorcesPath || resorcesPath.length() == 0) {
                System.err.println("ERROR: null or empty \"rpi_temp_service_resources_path\" property");
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

    public String getResourceContent(String fileName) {
        if (null == fileName || fileName.length() == 0) {
            System.err.println("null or empty filename");
            return null;
        }

        return getResourcesPath() + fileName;
    }
}
