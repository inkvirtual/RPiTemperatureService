package RaspberryPi;

import Resources.ResourcesHelper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ResourceBundle;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by fanta on 5/28/17.
 */
public class CpuTest {
    private PiBash bash;
    private ResourcesHelper resourcesHelper;
    private Cpu cpu;

    @Before
    public void setUp() throws Exception {
        bash = mock(PiBash.class);
        resourcesHelper = mock(ResourcesHelper.class);

        cpu = new Cpu(bash, resourcesHelper);

        doReturn("").when(resourcesHelper).getFullPath(anyString());
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test_isCpuThrottling_lowThrottling() throws Exception {
        doReturn(String.valueOf(Cpu.LOW_FREQUENCY - 11)).when(bash).execute(
                anyString());

        Assert.assertTrue("CPU should throttle", cpu.isCpuThrottling());
        verify(bash, times(1)).execute(anyString());
    }

    @Test
    public void test_isCpuThrottling_highThrottling() throws Exception {
        doReturn(String.valueOf(Cpu.LOW_FREQUENCY + 50)).when(bash).execute(
                anyString());

        Assert.assertTrue("CPU should throttle", cpu.isCpuThrottling());
        verify(bash, times(1)).execute(anyString());
    }

    @Test
    public void test_isCpuThrottling_low() throws Exception {
        doReturn(String.valueOf(Cpu.LOW_FREQUENCY - 1)).when(bash).execute(
                anyString());

        Assert.assertFalse("CPU should not throttle", cpu.isCpuThrottling());
        verify(bash, times(1)).execute(anyString());
    }

    @Test
    public void test_isCpuThrottling_high() throws Exception {
        doReturn(String.valueOf(Cpu.HIGH_FREQUENCY - 1)).when(bash).execute(
                anyString());

        Assert.assertFalse("CPU should not throttle", cpu.isCpuThrottling());
        verify(bash, times(1)).execute(anyString());
    }
}