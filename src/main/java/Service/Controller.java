package Service;

import CPU.Cpu;
import Fan.Fan;
import RaspberryPi.PiBash;
import Resources.ResourcesHelper;

/**
 * Created by fanta on 5/27/17.
 */
public class Controller {
    private Cpu cpu;
    private Fan fan;

    public Controller(ResourcesHelper resourcesHelper) {
        cpu = new Cpu(resourcesHelper);
        fan = new Fan(resourcesHelper);
    }

    public boolean startFan() {
        return fan.start();
    }

    public void stopFan() {
        fan.stop();
    }

    public boolean isFanOn() {
        return fan.isFanOn();
    }

    public int getCpuUsage() {
        return cpu.getUsage();
    }

    public int getCpuFrequency() {
        return cpu.getFrequency();
    }

    public boolean isCpuThrottling() {
        return cpu.isCpuThrottling();
    }

    public double getCpuTemperature() {
        return cpu.getTemperature();
    }
}
