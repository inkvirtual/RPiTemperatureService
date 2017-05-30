package RaspberryPi;

import Resources.ResourcesHelper;

/**
 * Created by fanta on 5/30/17.
 */
public class RPi {
    private PiBash bash;
    private ResourcesHelper resourcesHelper;

    public RPi(PiBash bash, ResourcesHelper resourcesHelper) {
        this.bash = bash;
        this.resourcesHelper = resourcesHelper;
    }

    public void shutdown() {
        bash.execute(resourcesHelper.getFullPath("shutdown.sh"));
    }
}
