package Test;

import Configuration.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by fanta on 5/23/17.
 */
public class ConfigurationTest extends TestBaseClass {
//    @Before
//    public void setUp() throws Exception {
//    }
//
//    @After
//    public void tearDown() throws Exception {
//    }

    @Test
    public void getInstanceTest() throws Exception {
        assertNotNull("Instance should not be null", Configuration.getInstance());
    }

    @Test
    public void getPropertiesTest() throws Exception {
        fail("Not yet implemented");
    }

}