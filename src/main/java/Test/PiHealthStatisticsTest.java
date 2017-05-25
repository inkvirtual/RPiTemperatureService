package Test;

import Statistics.PiHealthRecord;
import Statistics.PiHealthStatistics;
import org.junit.*;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Created by fanta on 5/23/17.
 */
public class PiHealthStatisticsTest extends TestBaseClass {
//    @BeforeClass
//    public static void setUp() throws Exception {
//    }
//
//    @AfterClass
//    public static void cleanUp() throws Exception {
//    }

    @Test
    public void test_getInstance() throws Exception {
        assertNotNull("Instance should not be null", PiHealthStatistics.getInstance());
    }

//    @Test
//    public void addRecord() throws Exception {
//        PiHealthRecord record = Mockito.mock(PiHealthRecord.class);
//
//        Mockito.when(record.getCpuFrequency()).thenReturn(12000);
//
//        Assert.assertEquals(record.getCpuFrequency(), 12000);
//    }

    @Test
    public void test_addRecord_invalidFrequency() throws Exception {
        PiHealthRecord record = new PiHealthRecord(20.00, -1, true);
        PiHealthStatistics.getInstance().addRecord(record.getCpuTemperature(), record.getCpuFrequency(), record.isFanOn());
        Assert.assertTrue("Invalid record should not be stored", PiHealthStatistics.getInstance().getRecords().size() == 0);
    }

    @Test
    public void test_addRecord_invalidTemperature() throws Exception {
        PiHealthRecord record = new PiHealthRecord(0.00, 600, true);
        PiHealthStatistics.getInstance().addRecord(record.getCpuTemperature(), record.getCpuFrequency(), record.isFanOn());
        Assert.assertTrue("Invalid record should not be stored", PiHealthStatistics.getInstance().getRecords().size() == 0);
    }

    @Test
    public void test_addRecord_validRecord() throws Exception {
        PiHealthRecord record = new PiHealthRecord(40.00, 600, true);
        PiHealthStatistics.getInstance().addRecord(record.getCpuTemperature(), record.getCpuFrequency(), record.isFanOn());
        Assert.assertTrue("Valid record should be stored", PiHealthStatistics.getInstance().getRecords().size() == 1);

        PiHealthRecord storedRecord = PiHealthStatistics.getInstance().getRecords().entrySet().iterator().next().getValue();
        Assert.assertEquals(record.getCpuFrequency(), storedRecord.getCpuFrequency());
        Assert.assertEquals(record.getCpuTemperature(), storedRecord.getCpuTemperature(), 0.01);
        Assert.assertEquals(record.isFanOn(), storedRecord.isFanOn());
    }

    @Test
    public void test_getRecordsMapSince() throws Exception {
        fail("Not yet implemented");
    }

    @Test
    public void test_getRecordsListSince() throws Exception {
//        PiHealthRecord record; // = new PiHealthRecord(40.00, 1200, true);
//        List<PiHealthRecord> recordList = new ArrayList<>();
//        long testStartTime = System.currentTimeMillis();
//
//        double temperature = 0.00;
//        int frequency = 0;
//        boolean fanIsOn = true;
//
//
//
//        for (int i = 0; i < 999; i++) {
//            record = new PiHealthRecord(temperature, frequency, fanIsOn);
//            recordList.add(record);
//
//            addRecord(record);
//
//            temperature++;
//            frequency++;
//
//            sleep(5);
//        }

        fail("Not yet implemented");
    }

    private void addRecord(PiHealthRecord record) throws Exception {
        PiHealthStatistics.getInstance().addRecord(record);
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}