package Configuration;

import RaspberryPi.RPi;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by dev on 28.03.2017.
 */
public class Configuration {
    private static Configuration instance;
    private Map<String, String> properties;

    public static Configuration getInstance() {
        if (null == instance)
            instance = new Configuration();
        return instance;
    }

    private Configuration() {
        properties = new HashMap<String, String>();

        try {
            Properties propsFile = new Properties();
            propsFile.load(new FileInputStream(RPi.getResource("rpi_temperature_service.properties")));

            for (Map.Entry<Object, Object> entry : propsFile.entrySet()) {
                properties.put(entry.getKey().toString(), entry.getValue().toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
