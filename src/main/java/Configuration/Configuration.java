package Configuration;

import RaspberryPi.RPi;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by dev on 28.03.2017.
 */
public class Configuration {
    private static Configuration instance;
    private RPi pi;
    private Map<String, String> properties;

    private Configuration() {
        pi = new RPi();
        properties = new HashMap<String, String>();

        try {
            Properties propsFile = new Properties();
            propsFile.load(new FileInputStream(pi.getResourceContent("rpi_temperature_service.properties")));

            for (Map.Entry<Object, Object> entry : propsFile.entrySet()) {
                properties.put(entry.getKey().toString(), entry.getValue().toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Configuration getInstance() {
        if (null == instance)
            instance = new Configuration();
        return instance;
    }

    public Map<String, String> getProperties() {
        return this.properties;
    }


    @Override
    public String toString() {
        return "Configuration.Configuration{" +
                "properties=" + properties +
                '}';
    }
}
