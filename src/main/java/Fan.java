/*
 * Created by dev on 28.03.2017.
 */
public class Fan {
    private boolean on;
    private static Fan instance;

    public static Fan getInstance() {
        if (null == instance)
            instance = new Fan();
        return instance;
    }

    public boolean start() {
        try {
//            PiBash.execute(RPi.getResource("fan_start.sh"));
            on = true;
        } catch (Exception e) {
            //TODO: log error
            return false;
        }
        return true;
    }

    public void stop() {
//        PiBash.execute(RPi.getResource("fan_stop.sh"));
        on = false;
    }

    public boolean getStatus() {
        return on;
    }

    private Fan() {
        on = false;
    }
}
