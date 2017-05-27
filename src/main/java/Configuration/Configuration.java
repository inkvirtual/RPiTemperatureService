package Configuration;

import Resources.ResourcesHelper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by dev on 28.03.2017.
 */
public class Configuration implements IConfiguration {
    private Map<String, String> properties;
    private ResourcesHelper resourcesHelper;

    public Configuration(ResourcesHelper resourcesHelper, String propertiesFileName) {
        this.resourcesHelper = resourcesHelper;
        Properties propsFile = new Properties();
        properties = new HashMap<>();
        InputStream fis = null;

        try {
            checkPropsFileNameValid(propertiesFileName);

            fis = new FileInputStream(propertiesFileName);
            propsFile.load(fis);

            for (Map.Entry<Object, Object> entry : propsFile.entrySet()) {
                properties.put(entry.getKey().toString(), entry.getValue().toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeInputStream(fis);
        }
    }

    private void closeInputStream(InputStream is) {
        if (is != null)
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    @Override
    public Map<String, String> getProperties() {
        return this.properties;
    }

    private boolean checkPropsFileNameValid(String propsFileName) {
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

    public ResourcesHelper getResourcesHelper() {
        return resourcesHelper;
    }

    @Override
    public String toString() {
        return "Configuration.Configuration{" +
                "properties=" + properties +
                '}';
    }
}
