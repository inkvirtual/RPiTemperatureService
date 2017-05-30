package Resources;

/**
 * Created by fanta on 5/27/17.
 */
public class ResourcesHelper {
    private String resourcesPath;

    public ResourcesHelper(String resourcesPath) {
        if (resourcesPath == null || resourcesPath.length() == 0)
            throw new IllegalArgumentException(
                    "Null or empty \"resourcesPath\" argument");

        init(resourcesPath);
    }

    private void init(String resourcesPath) {
        this.resourcesPath = normalizePath(resourcesPath);
    }

    public String normalizePath(String path) {
        if (path.charAt(path.length() - 1) != '/')
            path += '/';
        return path;
    }

    public String getResourcesPath() {
        return resourcesPath;
    }

    public String getFullPath(String fileName) {
        if (null == fileName || fileName.length() == 0) {
            System.err.println("null or empty filename");
            return null;
        }

        return resourcesPath + fileName;
    }

    public boolean checkPropsFileNameValid(String propsFileName) {
        if (propsFileName == null || propsFileName.length() == 0)
            throw new IllegalArgumentException("Null or empty properties file name");

        try {
            String[] tokens = propsFileName.split(".");

            if (tokens.length == 1)
                throw new IllegalArgumentException("Invalid properties file name:" + propsFileName);

            if (!propsFileName.endsWith(".properties"))
                throw new IllegalArgumentException("Invalid properties file name extension");

        } catch (Exception ex) {
            throw ex;
        }

        return true;
    }
}
