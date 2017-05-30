package Service;

import Configuration.Configuration;
import RaspberryPi.Fan;
//import Statistics.PiHealthStatistics;

import java.util.Map;

/**
 * Created by fanta on 5/27/17.
 */
public class RPiTemperatureService implements IService {

    private boolean fanAlwaysOn;
    private double fanStartTempC;
    private boolean shouldFanStartAtHighCpuUsage;
    private int cpuHighUsagePercent;
    private int fanCollingMinTimeS;
    private Fan.FanFailureAction fanFailureAction;
    private long sleepTimeMs;
    private State state;

    private Controller controller;

    // TODO: switch to interface, not class implementation
    public RPiTemperatureService(Configuration configuration) {
        init(configuration);
    }

    // TODO: switch to interface, not implementation
    private void init(Configuration configuration) {
        Map<String, String> config = configuration.getProperties();
        // TODO: fanAlwaysOn is not used
        fanAlwaysOn = config.getOrDefault("fan.always.on", "false").equals("true") ? true : false;
        fanStartTempC = Double.parseDouble(config.getOrDefault("fan.start.temperature.celsius", "70"));
        shouldFanStartAtHighCpuUsage = config.
                getOrDefault("fan.start.cpu.usage", "false").equals("true") ? true : false;
        cpuHighUsagePercent = Integer.parseInt(config.getOrDefault("fan.start.cpu.usage.percent", "60"));
        fanCollingMinTimeS = Integer.parseInt(config.getOrDefault("fan.colling.min.time.seconds", "60"));
        fanFailureAction = config.getOrDefault("fan.failure.action",
                Fan.FanFailureAction.SHUTDOWN.name()).equals(Fan.FanFailureAction.LOG.name()) ?
                Fan.FanFailureAction.LOG : Fan.FanFailureAction.SHUTDOWN;
        sleepTimeMs = Integer.parseInt(config.getOrDefault("sleep.time.ms", "1000"));

        controller = new Controller(configuration.getResourcesHelper());

        System.out.println(config.toString());
    }

    @Override
    public void start() {
        System.out.println("Starting RPi Temperature Service");

//        switchState(State.CHECKING_SYSTEM);
        state = State.CHECKING_SYSTEM;
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
                        switchState(State.STARTING_COOLING);
                        break;
                    }

                    if (shouldFanStartAtHighCpuUsage) {
                        int cpuUsage = controller.getCpuUsage();

                        if (cpuUsage > cpuHighUsagePercent) {
                            System.out.println("CPU Usage:" + cpuUsage + "%, trigger point:" + cpuHighUsagePercent + "%");
                            switchState(State.STARTING_COOLING);
                            break;
                        }
                    }

                    if (controller.isCpuThrottling()) {
                        System.err.println("CPU Throttling detected, frequency:" + controller.getCpuFrequency() + "MHz");
                        switchState(State.STARTING_COOLING);
                        break;
                    }

                    // TODO: poate ar trebui sa treaca ceva timp pina ce sa oprim ventilatorul ???
                    if (controller.isFanOn()) {
                        System.out.println("System cooled down:" + cpuTemp + "C, " + controller.getCpuFrequency() + "MHz");
                        switchState(State.STOPPING_COOLING);
                    }

                    //Sleep 5s
                    sleep(5_000);
                    break;
                case STARTING_COOLING:
                    System.out.print("Starting fan: ");
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
                    while (System.currentTimeMillis() < (fanStartTime + fanCollingMinTimeS * 1_000)) {

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
                    System.out.println("FAN is on for " + ((System.currentTimeMillis() - fanStartTime) / 1_000) +
                            "s, trigger point:" + fanCollingMinTimeS + "s");
                    switchState(State.CHECKING_SYSTEM);

                    break;
                case STOPPING_COOLING:
                    if (!controller.isFanOn()) {
                        System.err.println("Can not stop FAN because it is already off");
                    } else {
                        System.out.println("Stopping FAN");
                        controller.stopFan();
                    }
                    fanStartTime = 0;
                    switchState(State.CHECKING_SYSTEM);

                    break;
                case TERMINATING:
                    controller.stopFan();

                    if (fanFailureAction.equals(Fan.FanFailureAction.SHUTDOWN)) {
                        System.out.println("RPiTemperatureService execution terminated, shutting down RPi");
                        controller.shutdown();
                    }
                    terminated = true;
                    break;

                default:
                    System.err.println("unknown state " + state.name());
                    switchState(State.TERMINATING);
                    break;
            }
            sleep(1_000);
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

    // TODO: find a better way to mock controller
    public void setController(Controller controller) {
        this.controller = controller;
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

    private boolean shouldStartFan() {
        if (controller.getCpuTemperature() > fanStartTempC)
            return true;

        if (shouldFanStartAtHighCpuUsage) {
            if (controller.getCpuUsage() >= cpuHighUsagePercent)
                return true;
        }

        return false;
    }

    //TODO: run in a separate thread, move in other class ???
//    public void addRecord() {
//        boolean fail = false;
//        while (!fail) {
//            try {
//                PiHealthStatistics.getInstance().
//                        addRecord(controller.getCpuTemperature(), controller.getCpuFrequency(), controller.isFanOn());
//            } catch (Exception ex) {
//                fail = true;
//                //TODO: log error
//            }
//
//            sleep(2_000);
//        }
//    }

    enum State {
        CHECKING_SYSTEM,
        STARTING_COOLING,
        CHECKING_COOLING,
        STOPPING_COOLING,
        TERMINATING
    }
}
