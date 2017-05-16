package Statistics;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dev on 16.05.2017.
 */
public class PiHealthStatistics {
    private Map<Time, PiHealthRecord> records;

    private static PiHealthStatistics instance;

    private PiHealthStatistics() {
        records = new HashMap();
    }

    public static PiHealthStatistics getInstance() {
        if (instance == null)
            instance = new PiHealthStatistics();
        return instance;
    }

    public Result addRecord(int cpuTemp, int cpuFreq, boolean fanIsOn) {
        throw new NotImplementedException();
    }

    private void clear() {
        records.clear();
    }
}

enum Result {
    OK,
    UNKNOWN_ERROR
}
