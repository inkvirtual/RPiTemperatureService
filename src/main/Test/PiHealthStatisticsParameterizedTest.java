//package Test;
//
//import Configuration.Configuration;
//import Statistics.PiHealthRecord;
//import Statistics.PiHealthStatistics;
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.Parameterized;
//
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.Map;
//
//@RunWith(Parameterized.class)
//public class PiHealthStatisticsParameterizedTest extends TestBaseClass {
//    private double cpuTemperature;
//    private int cpuFrequency;
//    private boolean fanIsOn;
//    private boolean recordExpectedToBeStored;
//
//    public PiHealthStatisticsParameterizedTest(double cpuTemperature, int cpuFrequency, boolean fanIsOn, boolean recordExpectedToBeStored) {
//        this.cpuTemperature = cpuTemperature;
//        this.cpuFrequency = cpuFrequency;
//        this.fanIsOn = fanIsOn;
//        this.recordExpectedToBeStored = recordExpectedToBeStored;
//    }
//
//    @Parameterized.Parameters
//    public static Collection<Object[]> theMissingMethod() {
//        TestBaseClass.staticInit();
//
//        return Arrays.asList(new Object[][]{
//                {0.0, 0, true, false},
//                {0.0, 500, true, false},
//                {0.0, 600, true, false},
//                {0.0, 700, true, false},
//                {0.0, 1190, true, false},
//                {0.0, 1200, true, false},
//                {0.0, 1210, true, false},
//                {getTriggerTemp() - 1, 600, true, true},
//                {getTriggerTemp(), 600, true, true},
//                {getTriggerTemp() + 1, 600, true, true},
//                {getTriggerTemp() - 1, 1200, true, true},
//                {getTriggerTemp(), 1200, true, true},
//                {getTriggerTemp() + 1, 1200, true, true},
//        });
//    }
//
//
//    @Test
//    public void test_addRecord() throws Exception {
//        PiHealthRecord record = new PiHealthRecord(cpuTemperature, cpuFrequency, fanIsOn);
//        PiHealthStatistics.getInstance().addRecord(record.getCpuTemperature(), record.getCpuFrequency(), record.isFanOn());
//
//
//        Assert.assertEquals(
//                "Record expected to be stored [temp:" + record.getCpuTemperature() +
//                        ", frequency:" + record.getCpuFrequency() + ", fanIsOn:" + record.isFanOn() + "]: " +
//                        recordExpectedToBeStored, recordExpectedToBeStored,
//                PiHealthStatistics.getInstance().getRecords().size() == 1);
//
//        if (recordExpectedToBeStored) {
//            Assert.assertEquals(cpuTemperature, record.getCpuTemperature(), .01);
//            Assert.assertEquals(cpuFrequency, record.getCpuFrequency());
//            Assert.assertEquals(fanIsOn, record.isFanOn());
//        }
//
//        // Because PiHealthStatistics its a singleton, we may want to clear its records after each test iteration
//        PiHealthStatistics.getInstance().clearRecords();
//    }
//
//    private static Map<String, String> getConfiguration() {
//        return Configuration.getInstance().getProperties();
//    }
//
//    private static double getTriggerTemp() {
//        return Double.parseDouble(getConfiguration().get("fan.start.temperature.celsius"));
//    }
//}
