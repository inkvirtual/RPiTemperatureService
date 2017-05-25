import RaspberryPi.PiBash;
import RaspberryPi.RPi;

/*
 * Created by dev on 28.03.2017.
 */
public class Fan {
    private static Fan instance;
    private PiBash bash;
    private RPi pi;
    private boolean fanIsOn;

    private Fan() {
        bash = new PiBash();
        pi = new RPi();
        fanIsOn = false;
    }

    public static Fan getInstance() {
        if (null == instance)
            instance = new Fan();
        return instance;
    }

    public boolean start() {
        try {
            bash.execute(pi.getResourceContent("fan_start.sh"));
            fanIsOn = true;
        } catch (Exception e) {
            //TODO: log error
            return false;
        }
        return true;
    }

    public void stop() {
        bash.execute(pi.getResourceContent("fan_stop.sh"));
        fanIsOn = false;
    }

    public boolean getStatus() {
        return fanIsOn;
    }
}
