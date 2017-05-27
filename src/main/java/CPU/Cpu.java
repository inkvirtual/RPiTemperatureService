package CPU;

import RaspberryPi.PiBash;
import RaspberryPi.RPi;
import Resources.ResourcesHelper;

/*
 * Created by dev on 28.03.2017.
 */
public class Cpu {
    private static final int LOW_FREQUENCY = 600_000;
    private static final int HIGH_FREQUENCY = 1_200_000;

    private PiBash bash;
    private ResourcesHelper resourcesHelper;

    public Cpu(ResourcesHelper resourcesHelper) {
        this.resourcesHelper = resourcesHelper;
        this.bash = new PiBash();
    }

    public int getUsage() {
        return Integer.parseInt(bash.execute(resourcesHelper.getFullPath("get_cpu_usage.sh")));
    }

    public double getTemperature() {
        return Double.parseDouble(bash.execute(resourcesHelper.getFullPath("get_cpu_temperature.sh")));
    }

    public int getFrequency() {
        return Integer.parseInt(bash.execute(resourcesHelper.getFullPath("get_cpu_frequency.sh")));
    }

    public boolean isCpuThrottling() {
        int frequency = getFrequency();

        //if frequency  = (600_010,1_199_990)
        if (frequency > (LOW_FREQUENCY + 10) && frequency < (HIGH_FREQUENCY - 10))
            return true;

        //if frequency < 599_990
        if (frequency < (LOW_FREQUENCY - 10))
            return true;

        return false;
    }
}
