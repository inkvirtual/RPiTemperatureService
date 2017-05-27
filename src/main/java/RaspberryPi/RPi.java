package RaspberryPi;

import Resources.ResourcesHelper;

/**
 * Created by dev on 28.03.2017.
 */
public class RPi {
    private PiBash bash;
//    private Configuration configuration;
    private ResourcesHelper resourcesHelper;

    public RPi(ResourcesHelper resourcesHelper) {
        this.resourcesHelper = resourcesHelper;
        this.bash = new PiBash();
    }

    public void shutdown() {
        bash.execute(resourcesHelper.getFullPath("shutdown.sh"));
    }
}
