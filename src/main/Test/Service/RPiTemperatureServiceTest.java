package Service;

import Configuration.Configuration;
import RaspberryPi.Fan;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Created by fanta on 5/30/17.
 */
public class RPiTemperatureServiceTest {
    private Configuration mockConfiguration;
    private Controller mockController;
    // TODO: switch to interface
    private RPiTemperatureService service;

    @Before
    public void setUp() throws Exception {
        mockConfiguration = mock(Configuration.class);
        mockController = mock(Controller.class);

//        doReturn(setConfig).when(mockConfiguration).getProperties();
//
//        service = new RPiTemperatureService(mockConfiguration);
//        service.setController(mockController);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test_AssertCheckConfiguration() throws Exception {
        Map<String, String> setConfig = setupConfig(
                true,
                65d,
                false,
                75,
                20,
                Fan.FanFailureAction.LOG,
                2_000
        );

        doReturn(setConfig).when(mockConfiguration).getProperties();

        service = new RPiTemperatureService(mockConfiguration);
        service.setController(mockController);

        Assert.assertEquals("Test configuration not set properly", setConfig, mockConfiguration.getProperties());
    }

    @Test
    public void test_start_1() throws Exception {
        Map<String, String> setConfig = setupConfig(
                false,
                65d,
                false,
                0,
                20,
                Fan.FanFailureAction.LOG,
                500
        );

        doReturn(setConfig).when(mockConfiguration).getProperties();

        service = new RPiTemperatureService(mockConfiguration);
        service.setController(mockController);

        Assert.assertEquals("Test configuration not set properly", setConfig, mockConfiguration.getProperties());

        doReturn(45.5d).when(mockController).getCpuTemperature();

        service.start();

    }

    private Map<String, String> setupConfig(
            boolean fanAlwaysOn,
            double fanStartTempC,
            boolean shouldFanStartAtHighCpuUsage,
            int cpuHighUsagePercent,
            int fanCoolingMinTimeS,
            Fan.FanFailureAction fanFailureAction,
            int sleepTimeMs
    ) {
        Map<String, String> config = new HashMap<>();
        config.put("fan.always.on", String.valueOf(fanAlwaysOn));
        config.put("fan.start.temperature.celsius", String.valueOf(fanStartTempC));
        config.put("fan.start.cpu.usage", String.valueOf(shouldFanStartAtHighCpuUsage));
        config.put("fan.start.cpu.usage.percent", String.valueOf(cpuHighUsagePercent));
        config.put("fan.cooling.min.time.seconds", String.valueOf(fanCoolingMinTimeS));
        config.put("fan.failure.action", String.valueOf(fanFailureAction.name()));
        config.put("sleep.time.ms", String.valueOf(sleepTimeMs));

        return config;
    }
}