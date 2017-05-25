package Test;

/**
 * Created by fanta on 5/23/17.
 */
public class TestBaseClass {
    public TestBaseClass() {
        init();
    }

    protected void init() {
        staticInit();
    }

    protected void terminate() {
        System.clearProperty("rpi_temp_service_resources_path");;
    }

    public static void staticInit() {
        System.setProperty("rpi_temp_service_resources_path", "/home/fanta/IdeaProjects/RPiTemperatureService/src/main/resources/");
    }
}
