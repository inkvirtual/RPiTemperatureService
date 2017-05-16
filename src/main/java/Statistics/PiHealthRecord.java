package Statistics;

/**
 * Created by dev on 16.05.2017.
 */
public class PiHealthRecord {
    private int cpuTemperature;
    private int cpuFrequency;
    private boolean fanIsOn;

    public PiHealthRecord() {
    }

    public PiHealthRecord(int cpuTemperature, int cpuFrequency, boolean fanIsOn) {
        this.cpuTemperature = cpuTemperature;
        this.cpuFrequency = cpuFrequency;
        this.fanIsOn = fanIsOn;
    }

    public int getCpuTemperature() {
        return cpuTemperature;
    }

    public int getCpuFrequency() {
        return cpuFrequency;
    }

    public boolean isFanIsOn() {
        return fanIsOn;
    }
}
