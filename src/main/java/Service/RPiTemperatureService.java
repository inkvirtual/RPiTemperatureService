package Service;

import Configuration.Configuration;
import Fan.Fan;
import Fan.FanFailureAction;
import RaspberryPi.RPi;
import Statistics.PiHealthStatistics;

import java.util.Map;

/**
 * Created by fanta on 5/27/17.
 */
public class RPiTemperatureService implements IService {

    private boolean fanAlwaysOn;
    private double fanStartTempC;
    private boolean fanShouldStartAtHighCpuUsage;
    private int cpuHighUsagePercent;
    private int fanCollingMinTimeS;
    private FanFailureAction fanFailureAction;
    private long sleepTimeMs;
    private State state;

    private Controller controller;
    private RPi rpi;

    // TODO: switch to interface, not class implementation
    public RPiTemperatureService(Configuration configuration) {
        init(configuration);
    }

    // TODO: switch to interface, not implementation
    private void init(Configuration configuration) {
        Map<String, String> config = configuration.getProperties();
        fanAlwaysOn = config.getOrDefault("fan.always.on", "false").equals("true") ? true : false;
        fanStartTempC = Double.parseDouble(config.getOrDefault("fan.start.temperature.celsius", "70"));
        fanShouldStartAtHighCpuUsage = config.
                getOrDefault("fan.start.cpu.usage", "false").equals("true") ? true : false;
        cpuHighUsagePercent = Integer.parseInt(config.getOrDefault("fan.start.cpu.usage.percent", "60"));
        fanCollingMinTimeS = Integer.parseInt(config.getOrDefault("fan.colling.min.time.seconds", "60"));
        fanFailureAction = config.getOrDefault("fan.failure.action",
                FanFailureAction.SHUTDOWN.name()).equals(FanFailureAction.LOG.name()) ?
                FanFailureAction.LOG : FanFailureAction.SHUTDOWN;
        sleepTimeMs = Integer.parseInt(config.getOrDefault("sleep.time.ms", "1000"));

        controller = new Controller(configuration.getResourcesHelper());
        rpi = new RPi(configuration.getResourcesHelper());

        System.out.println(config.toString());
    }

    @Override
    public void start() {
        System.out.println("Starting RPi Temperature Service");

        switchState(State.CHECKING_SYSTEM);
        boolean terminated = false;
        long fanStartTime = 0;

        while (!terminated) {

            switch (state) {
                case CHECKING_SYSTEM:
                    double cpuTemp = controller.getCpuTemperature();

                    //DEBUG
                    System.out.println("CPU Temp:" + cpuTemp + "C");

                    if (cpuTemp > fanStartTempC) {
                        System.out.println("CPU Temp:" + cpuTemp + "C, trigger point:" + fanStartTempC + "C");
                        switchState(State.STARTING_FAN);
                        break;
                    }

                    if (fanShouldStartAtHighCpuUsage) {
                        int cpuUsage = controller.getCpuUsage();

                        if (cpuUsage > cpuHighUsagePercent) {
                            System.out.println("CPU Usage:" + cpuUsage + "%, trigger point:" + cpuHighUsagePercent + "%");
                            switchState(State.STARTING_FAN);
                            break;
                        }
                    }

                    if (controller.isCpuThrottling()) {
                        System.err.println("CPU Throttling detected, frequency:" + controller.getCpuFrequency() + "MHz");
                        switchState(State.STARTING_FAN);
                        break;
                    }

                    if (controller.isFanOn()) {
                        System.out.println("System cooled down:" + cpuTemp + "C, " + controller.getCpuFrequency() + "MHz");
                        switchState(State.STOPPING_FAN);
                    }

                    //Sleep 5s
                    sleep(5000);
                    break;
                case STARTING_FAN:
                    System.out.print("Starting fan:");
                    if (controller.isFanOn()) {
                        System.out.println("already on");
                        fanStartTime = System.currentTimeMillis();
                        switchState(State.CHECKING_COOLING);
                    } else if (controller.startFan()) {
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

                        if (!controller.isFanOn()) {
                            System.err.println("FAN is off");
                            switchState(State.TERMINATING);
                            break;
                        }

                        //Sleep for 10s
                        sleep(10_000);

                        if (controller.isCpuThrottling()) {
                            System.err.println("CPU Throttling detected, frequency:" + controller.getCpuFrequency() + "MHz");
                            switchState(State.TERMINATING);
                            break;
                        }
                    }
                    System.out.println("FAN is on for " + ((System.currentTimeMillis() - fanStartTime) / 1000) + "s, trigger point:" + fanCollingMinTimeS + "s");
                    switchState(State.CHECKING_SYSTEM);

                    break;
                case STOPPING_FAN:
                    if (!controller.isFanOn()) {
                        System.err.println("FAN is off, should be on");
                    } else {
                        System.out.println("Stopping FAN");
                        controller.stopFan();
                    }
                    fanStartTime = 0;
                    switchState(State.CHECKING_SYSTEM);

                    break;
                case TERMINATING:
                    controller.stopFan();

                    if (fanFailureAction.equals(FanFailureAction.SHUTDOWN)) {
                        System.out.println("RPiTemperatureService execution terminated, shutting down RaspberryPi.RPi");
                        rpi.shutdown();
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

        System.out.println("RPiTemperatureService execution terminated, closing service");
        return;
    }

    private void switchState(State newState) {
        //If multiple consecutive switches to the same state, then print only once
        if (!newState.equals(state))
            System.out.println("Switch state from " + state.name() + " to " + newState.name());

        state = newState;
    }

    private boolean shouldStartFan() {
        if (controller.getCpuTemperature() > fanStartTempC)
            return true;

        if (fanShouldStartAtHighCpuUsage) {
            if (controller.getCpuUsage() >= cpuHighUsagePercent)
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
                PiHealthStatistics.getInstance().
                        addRecord(controller.getCpuTemperature(), controller.getCpuFrequency(), controller.isFanOn());
            } catch (Exception ex) {
                fail = true;
                //TODO: log error
            }

            sleep(2_000);
        }
    }

//    private boolean checkArgs(String[] args) {
//        if (args == null || args.length == 0)
//            throw new IllegalArgumentException("Null or empty arguments provided");
//
//        if (args.length > 1)
//            throw new IllegalArgumentException("Too many arguments provided");
//
//        if (args == null)
//            return true;
//
//        try {
//            String[] tokens = args[0].split(".");
//
//            if (tokens.length == 1)
//                throw new IllegalArgumentException("Invalid properties file name:" + args[0]);
//
//            if (!tokens[0].endsWith(".properties"))
//                throw new IllegalArgumentException("Invalid properties file name extension");
//
//        } catch (Exception ex) {
//            throw ex;
//        }
//
//        return true;
//    }

//    private boolean loadResourcesPath(String[] args) {
//        if (null != args && args.length > 1) {
//            System.err.println("too many arguments provided");
//            return false;
//        }
//
//        if (null == args || args.length == 0) {
//            System.setProperty("rpi_temp_service_resources_path", "/home/pi/HomeAutomation/RPiTemperatureService");
//            System.out.print("no argument provided, using default resources path:");
//        } else {
//            System.setProperty("rpi_temp_service_resources_path", args[0]);
//            System.out.print("using resources path:");
//        }
//        System.out.println(System.getProperty("rpi_temp_service_resources_path"));
//
//        return true;
//    }

}
