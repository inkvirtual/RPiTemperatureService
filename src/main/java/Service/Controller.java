package Service;

import RaspberryPi.Cpu;
import RaspberryPi.Fan;
import RaspberryPi.PiBash;
import Resources.ResourcesHelper;

/**
 * Created by fanta on 5/27/17.
 */
public class Controller {
    private Cpu cpu;
    private Fan fan;
    private PiBash bash;
    private ResourcesHelper resourcesHelper;

    public Controller(ResourcesHelper resourcesHelper) {
        this.bash = new PiBash();
        this.cpu = new Cpu(bash, resourcesHelper);
        this.fan = new Fan(bash, resourcesHelper);
        this.resourcesHelper = resourcesHelper;
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

    public void shutdown() {
        bash.execute(resourcesHelper.getFullPath("shutdown.sh"));
    }
}
