import RaspberryPi.PiBash;
import RaspberryPi.RPi;

/*
 * Created by dev on 28.03.2017.
 */
public class Cpu {
    private static final int LOW_FREQUENCY = 600000;
    private static final int HIGH_FREQUENCY = 1200000;

    private PiBash bash;
    private RPi pi;

    public Cpu() {
        this.bash = new PiBash();
        this.pi = new RPi();
    }

    public int getUsage() {
        return Integer.parseInt(bash.execute(pi.getResourceContent("get_cpu_usage.sh")));
    }

    public double getTemperature() {
        return Double.parseDouble(bash.execute(pi.getResourceContent("get_cpu_temperature.sh")));
    }

    public int getFrequency() {
        return Integer.parseInt(bash.execute(pi.getResourceContent("get_cpu_frequency.sh")));
    }

    public boolean isThrottling() {
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
