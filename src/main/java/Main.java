import Configuration.Configuration;
import RaspberryPi.RPi;
import Statistics.PiHealthStatistics;
import sun.plugin2.gluegen.runtime.CPU;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Map;

/**
 * Created by dev on 28.03.2017.
 */
public class Main {
    static boolean fanAlwaysOn;
    static float fanStartTempC;
    static boolean fanShouldStartAtHighCpuUsage;
    static int cpuHighUsagePercent;
    static int fanCollingMinTimeS;
    static FanFailureAction fanFailureAction;
    static long sleepTimeMs;
    static State state;
//    CPU cpu;
//    RPi pi;

    public static void main(String[] args) {
        System.out.println("Starting RaspberryPi.RPi Temperature Service");

        if (!loadResourcesPath(args)) {
            System.err.println("failed to load resources path, closing service");
            return;
        }

        //DEBUG
//        {
//                System.out.println(RaspberryPi.PiBash.execute(RaspberryPi.RPi.getResourceContent("example.bat")));
//        }

//        cpu = new CPU();
//        pi = new RPi();
        Map<String, String> config = Configuration.getInstance().getProperties();
        fanAlwaysOn = config.getOrDefault("fan.always.on", "false").equals("true") ? true : false;
        fanStartTempC = Float.parseFloat(config.getOrDefault("fan.start.temperature.celsius", "70"));
        fanShouldStartAtHighCpuUsage = config.getOrDefault("fan.start.cpu.usage", "false").equals("true") ? true : false;
        cpuHighUsagePercent = Integer.parseInt(config.getOrDefault("fan.start.cpu.usage.percent", "60"));
        fanCollingMinTimeS = Integer.parseInt(config.getOrDefault("fan.colling.min.time.seconds", "60"));
        fanFailureAction = config.getOrDefault("fan.failure.action", FanFailureAction.SHUTDOWN.name()).equals(FanFailureAction.LOG.name()) ? FanFailureAction.LOG : FanFailureAction.SHUTDOWN;
        sleepTimeMs = Integer.parseInt(config.getOrDefault("sleep.time.ms", "1000"));

        System.out.println(config.toString());

        switchState(State.CHECKING_SYSTEM);
        boolean terminated = false;
        long fanStartTime = 0;

        while (!terminated) {

            switch (state) {
                case CHECKING_SYSTEM:
                    double cpuTemp = getCpuTemperature();

                    //DEBUG
                    System.out.println("CPU Temp:" + cpuTemp + "C");

                    if (cpuTemp > fanStartTempC) {
                        System.out.println("CPU Temp:" + cpuTemp + "C, trigger point:" + fanStartTempC + "C");
                        switchState(State.STARTING_FAN);
                        break;
                    }

                    if (fanShouldStartAtHighCpuUsage) {
                        int cpuUsage = getCpuUsage();

                        if (cpuUsage > cpuHighUsagePercent) {
                            System.out.println("CPU Usage:" + cpuUsage + "%, trigger point:" + cpuHighUsagePercent + "%");
                            switchState(State.STARTING_FAN);
                            break;
                        }
                    }

                    if (isCpuThrottling()) {
                        System.err.println("CPU Throttling detected, frequency:" + getCpuFrequency() + "MHz");
                        switchState(State.STARTING_FAN);
                        break;
                    }

                    if (fanIsOn()) {
                        System.out.println("System cooled down:" + cpuTemp + "C, " + getCpuFrequency() + "MHz");
                        switchState(State.STOPPING_FAN);
                    }

                    //Sleep 5s
                    sleep(5000);
                    break;
                case STARTING_FAN:
                    System.out.print("Starting fan:");
                    if (fanIsOn()) {
                        System.out.println("already on");
                        fanStartTime = System.currentTimeMillis();
                        switchState(State.CHECKING_COOLING);
                    } else if (startFan()) {
                        System.out.println("SUCCESS");
                        fanStartTime = System.currentTimeMillis();
                        switchState(State.CHECKING_COOLING);
                    } else {
                        System.out.println("FAIL");
                        switchState(State.TERMINATING);
                    }

                    break;
                case CHECKING_COOLING:
                    while (System.currentTimeMillis() < (fanStartTime + fanCollingMinTimeS * 1000)) {

                        if (!fanIsOn()) {
                            System.err.println("FAN is off");
                            switchState(State.TERMINATING);
                            break;
                        }

                        //Sleep for 10s
                        sleep(10000);

                        if (isCpuThrottling()) {
                            System.err.println("CPU Throttling detected, frequency:" + getCpuFrequency() + "MHz");
                            switchState(State.TERMINATING);
                            break;
                        }
                    }
                    System.out.println("FAN is on for " + ((System.currentTimeMillis() - fanStartTime) / 1000) + "s, trigger point:" + fanCollingMinTimeS + "s");
                    switchState(State.CHECKING_SYSTEM);

                    break;
                case STOPPING_FAN:
                    if (!fanIsOn()) {
                        System.err.println("FAN is off, should be on");
                    } else {
                        System.out.println("Stopping FAN");
                        stopFan();
                    }
                    fanStartTime = 0;
                    switchState(State.CHECKING_SYSTEM);

                    break;
                case TERMINATING:
                    stopFan();

                    if (fanFailureAction.equals(FanFailureAction.SHUTDOWN)) {
                        System.out.println("Service execution terminated, shutting down RaspberryPi.RPi");
                        new RPi().shutdown();
                    }
                    terminated = true;
                    break;

                default:
                    System.err.println("unknown state " + state.name());
                    switchState(State.TERMINATING);
                    break;
            }
            sleep(1000);
        }

        System.out.println("Service execution terminated, closing service");
        return;

//        if (fanAlwaysOn) {
//            startFan();
//            while (true) {
//                if (!fanIsOn()) {
//                    //TODO: log error
//                    stopFan();
//                    if (fanFailureAction.equals(FanFailureAction.SHUTDOWN)) {
//                        RaspberryPi.RPi.shutdown();
//                    }
//                    return;
//                }
//                sleep(sleepTimeMs);
//            }
//        }
//
//        while (true) {
//            if (shouldStartFan()) {
//                if (!fanIsOn())
//                    startFan();
//
//                long fanStopTime = System.currentTimeMillis() + fanCollingMinTimeS * 1000;
//                while (System.currentTimeMillis() < fanStopTime) {
//                    if (!fanIsOn()) {
//                        //TODO: log error
//                        stopFan();
//                        if (fanFailureAction.equals(FanFailureAction.SHUTDOWN)) {
//                            RaspberryPi.RPi.shutdown();
//                        }
////                        return;
//                        break;
//                    }
//                    sleep(sleepTimeMs);
//                }
//            } else {
//                if (fanIsOn())
//                    stopFan();
//            }
//            sleep(sleepTimeMs);
//        }
    }

    protected static int getCpuUsage() {
        return new Cpu().getUsage();
    }

    protected static int getCpuFrequency() {
        return new Cpu().getFrequency();
    }

    protected static boolean isCpuThrottling() {
        return new Cpu().isThrottling();
    }

    protected static double getCpuTemperature() {
        return new Cpu().getTemperature();
    }

    private static void switchState(State newState) {
        //If multiple consecutive switches to the same state, then print only once
        if (!newState.equals(state))
            System.out.println("Switch state from " + state.name() + " to " + newState.name());

        state = newState;
    }

    private static boolean loadResourcesPath(String[] args) {
        if (null != args && args.length > 1) {
            System.err.println("too many arguments provided");
            return false;
        }

        if (null == args || args.length == 0) {
            System.setProperty("rpi_temp_service_resources_path", "/home/pi/HomeAutomation/RPiTemperatureService");
            System.out.print("no argument provided, using default resources path:");
        } else {
            System.setProperty("rpi_temp_service_resources_path", args[0]);
            System.out.print("using resources path:");
        }
        System.out.println(System.getProperty("rpi_temp_service_resources_path"));

        return true;
    }

    private static boolean startFan() {
        return Fan.getInstance().start();
    }

    private static void stopFan() {
        Fan.getInstance().stop();
    }

    private static boolean fanIsOn() {
        return Fan.getInstance().getStatus();
    }

    private static boolean shouldStartFan() {
        if (getCpuTemperature() > fanStartTempC)
            return true;

        if (fanShouldStartAtHighCpuUsage) {
            if (getCpuUsage() >= cpuHighUsagePercent)
                return true;
        }

        return false;
    }

    private static void infiniteWait() {
        while (true)
            sleep(500);
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //TODO: run in a separate thread, move in other class ???
    public void addRecord() {
        boolean fail = false;
        while (!fail) {
            try {
                PiHealthStatistics.getInstance().addRecord(getCpuTemperature(), getCpuFrequency(), Fan.getInstance().getStatus());
            } catch (Exception ex) {
                fail = true;
                //TODO: log error
            }

            sleep(2_000);
        }
    }
}
