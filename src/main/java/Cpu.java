import java.util.Random;

/*
 * Created by dev on 28.03.2017.
 */
public class Cpu {
    private static final int LOW_FREQUENCY = 600000;
    private static final int HIGH_FREQUENCY = 1200000;

    public static int getUsage() {
        //return Integer.parseInt(PiBash.execute(RPi.getResource("get_cpu_usage.sh")));
        return 21;
    }

    public static float getTemperature() {
//        return Float.parseFloat(PiBash.execute(RPi.getResource("get_cpu_temperature.sh")));
        //Rand [40,81)
        return Float.valueOf(new Random().nextInt(41) + 40);
    }

    public static int getFrequency() {
//        return Integer.parseInt(PiBash.execute(RPi.getResource("get_cpu_frequency.sh")));
        return 1200000;
    }

    public static boolean isThrottling() {
        int frequency = getFrequency();

        //if frequency  = (600010,1199990)
        if (frequency > (LOW_FREQUENCY + 10) && frequency < (HIGH_FREQUENCY - 10))
            return true;

        //if frequency < 599990
        if (frequency < (LOW_FREQUENCY - 10))
            return true;

        return false;
    }
}
