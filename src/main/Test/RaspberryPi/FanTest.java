package RaspberryPi;

import Resources.ResourcesHelper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by fanta on 5/28/17.
 */
public class FanTest {
    private ResourcesHelper resourcesHelper;
    private PiBash bash;
    private Fan fan;

    @Before
    public void setUp() throws Exception {
        this.resourcesHelper = mock(ResourcesHelper.class);
        this.bash = mock(PiBash.class);
        this.fan = new Fan(bash, resourcesHelper);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test_isFanOn() throws Exception {
        Assert.assertFalse("Fan should be off", fan.isFanOn());
    }

    @Test
    public void test_start_pass() throws Exception {
        doReturn("").when(bash).execute(any());

        Assert.assertTrue("Fan should be on", fan.start());
        Assert.assertTrue("Fan should be on", fan.isFanOn());

        verify(bash, times(1)).execute(any());
    }

    @Test
    public void test_start_fail() throws Exception {
        doThrow(Exception.class).when(bash).execute(any());

        Assert.assertFalse("Fan should be off", fan.start());
        Assert.assertFalse("Fan should be off", fan.isFanOn());

        verify(bash, times(1)).execute(any());
    }

    @Test
    public void test_stop() throws Exception {
        doReturn("").when(bash).execute(any());

        fan.stop();

        Assert.assertFalse("Fan should be off", fan.isFanOn());

        verify(bash, times(1)).execute(any());
    }
}