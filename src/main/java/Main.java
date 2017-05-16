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

    public static void main(String[] args) {
        System.out.println("Starting RPi Temperature Service");

        if (!loadResourcesPath(args)) {
            System.err.println("failed to load resources path, closing service");
            return;
        }

        //DEBUG
//        {
//                System.out.println(PiBash.execute(RPi.getResource("example.bat")));
//        }

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
                    float cpuTemp = Cpu.getTemperature();

                    //DEBUG
                    System.out.println("CPU Temp:" + cpuTemp + "C");

                    if (cpuTemp > fanStartTempC) {
                        System.out.println("CPU Temp:" + cpuTemp + "C, trigger point:" + fanStartTempC + "C");
                        switchState(State.STARTING_FAN);
                        break;
                    }

                    if (fanShouldStartAtHighCpuUsage) {
                        int cpuUsage = Cpu.getUsage();

                        if (cpuUsage > cpuHighUsagePercent) {
                            System.out.println("CPU Usage:" + cpuUsage + "%, trigger point:" + cpuHighUsagePercent + "%");
                            switchState(State.STARTING_FAN);
                            break;
                        }
                    }

                    if (Cpu.isThrottling()) {
                        System.err.println("CPU Throttling detected, frequency:" + Cpu.getFrequency() + "MHz");
                        switchState(State.STARTING_FAN);
                        break;
                    }

                    if (fanIsOn()) {
                        System.out.println("System cooled down:" + cpuTemp + "C, " + Cpu.getFrequency() + "MHz");
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

                        if (Cpu.isThrottling()) {
                            System.err.println("CPU Throttling detected, frequency:" + Cpu.getFrequency() + "MHz");
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
                        System.out.println("Service execution terminated, shutting down RPi");
                        RPi.shutdown();
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
//                        RPi.shutdown();
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
//                            RPi.shutdown();
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

    private static void switchState(State newState) {
        //If multiple consecutive switches to the same state, then print only once
        if (!newState.equals(state))
            System.out.println("Switch state to " + newState);

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
        if (Cpu.getTemperature() > fanStartTempC)
            return true;

        if (fanShouldStartAtHighCpuUsage) {
            if (Cpu.getUsage() >= cpuHighUsagePercent)
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
}
