package RaspberryPi;

import Resources.ResourcesHelper;

/*
 * Created by dev on 28.03.2017.
 */
public class Fan {
    private PiBash bash;
    private ResourcesHelper resourceHelper;
    private boolean fanIsOn;

    public Fan(PiBash bash, ResourcesHelper resourcesHelper) {
        this.resourceHelper = resourcesHelper;
        this.bash = bash;
        this.fanIsOn = false;
    }

    public boolean start() {
        try {
            bash.execute(resourceHelper.getFullPath("fan_start.sh"));
            fanIsOn = true;
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void stop() {
        bash.execute(resourceHelper.getFullPath("fan_stop.sh"));
        fanIsOn = false;
    }

    public boolean isFanOn() {
        return fanIsOn;
    }

    public enum FanFailureAction {
        LOG,
        SHUTDOWN
    }
}

