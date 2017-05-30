package Service;

import Configuration.Configuration;
import Configuration.IConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Created by fanta on 5/30/17.
 */
public class RPiTemperatureServiceTest {
    private Configuration configuration;
    private IService service;

    @Before
    public void setUp() throws Exception {
        configuration = mock(Configuration.class);
        service = new RPiTemperatureService(configuration);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void start() throws Exception {
        

    }

}