import Resources.ResourcesHelper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by fanta on 5/28/17.
 */
public class ResourcesHelperTest {
    private String resourcesPath;
    private ResourcesHelper resourcesHelper;

    @Test(expected = IllegalArgumentException.class)
    public void test_invalidArgumentPath_null() throws Exception {
        new ResourcesHelper(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalidArgumentPath_empty() throws Exception {
        new ResourcesHelper("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_checkPropsFileNameValid_nullFileName() throws Exception {
        resourcesPath = "/home/fanta";
        resourcesHelper = new ResourcesHelper(resourcesPath);

        resourcesHelper.checkPropsFileNameValid(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_checkPropsFileNameValid_emptyFileName() throws Exception {
        resourcesPath = "/home/fanta";
        resourcesHelper = new ResourcesHelper(resourcesPath);

        resourcesHelper.checkPropsFileNameValid("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_checkPropsFileNameValid_invalidExtension() throws Exception {
        resourcesPath = "/home/fanta";
        resourcesHelper = new ResourcesHelper(resourcesPath);

        resourcesHelper.checkPropsFileNameValid("file.propertiess");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_checkPropsFileNameValid_noExtension() throws Exception {
        resourcesPath = "/home/fanta";
        resourcesHelper = new ResourcesHelper(resourcesPath);

        resourcesHelper.checkPropsFileNameValid("file.");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_checkPropsFileNameValid_noExtension2() throws Exception {
        resourcesPath = "/home/fanta";
        resourcesHelper = new ResourcesHelper(resourcesPath);

        resourcesHelper.checkPropsFileNameValid("file");
    }

    @Test
    public void test_checkPropsFileNameValid() throws Exception {
        resourcesPath = "/home/fanta";
        resourcesHelper = new ResourcesHelper(resourcesPath);

        Assert.assertTrue("File extension not valid",
                resourcesHelper.checkPropsFileNameValid("file.properties"));
    }

    @Test
    public void test_normalizePath_1() throws Exception {
        resourcesPath = "/home/fanta";
        resourcesHelper = new ResourcesHelper(resourcesPath);

        String normalizedPath = resourcesHelper.normalizePath(resourcesPath);
        Assert.assertEquals("Path was not normalized", normalizedPath, resourcesPath + "/");
    }

    @Test
    public void test_normalizePath_2() throws Exception {
        resourcesPath = "/home/fanta/";
        resourcesHelper = new ResourcesHelper(resourcesPath);

        String normalizedPath = resourcesHelper.getResourcesPath();
        Assert.assertEquals("Path was not normalized", normalizedPath, resourcesPath);
    }

    @Test
    public void test_getFullPath_nullOrEmptyFileName() throws Exception {
        resourcesPath = "/home/fanta";
        ResourcesHelper resourcesHelper = new ResourcesHelper(resourcesPath);

        Assert.assertNull("Method should return null", resourcesHelper.getFullPath(null));
        Assert.assertNull("Method should return null", resourcesHelper.getFullPath(""));
    }

    @Test
    public void test_getFullPath_validFileName() throws Exception {
        resourcesPath = "/home/fanta";
        String fileName = "script.sh";
        ResourcesHelper resourcesHelper = new ResourcesHelper(resourcesPath);

        Assert.assertEquals("Full path not correct" , resourcesHelper.getFullPath(fileName), resourcesPath + "/" + fileName);
    }
}