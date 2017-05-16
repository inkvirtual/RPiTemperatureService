package Statistics;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.Time;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dev on 16.05.2017.
 */
public class PiHealthStatistics {
    // map <timestamp in ms, PiHealthRecord>
    private Map<long, PiHealthRecord> records;
    private int maxRecordsNumber;

    private static PiHealthStatistics instance;

    private PiHealthStatistics() {
        records = new HashMap();
        //TODO: verify this
        maxRecordsNumber = Integer.parseInt(Configuration.getInstance().getProperties().getOrDefault("statistics.records.number", "100"));
    }

    public static PiHealthStatistics getInstance() {
        if (instance == null)
            instance = new PiHealthStatistics();
        return instance;
    }

    public Result addRecord(int cpuTemp, int cpuFreq, boolean fanIsOn) {
        if (cpuTemp < 1 || cpuFrequency < 1)
            return Result.INVALID_ARGUMENT_PROVIDED;
        
        // delete oldest record
        if (this.records.size() > maxRecordsNumber) {
         // TODO: implement deleting oldest element in the map, or perform resize(sublist)  
        }
        
        this.records.put(System.currentTimeMillis(), new PiHealthRecord(cpuTemp, cpuFreq, fanIsOn));
        return Result.OK;
    }
    
    public Map<long, PiHealthRecord> getRecords() {
        return records;
    }
    
    public Map<long, PiHealthRecord> getRecordsMapSince(long timestamp) {
        if (timeStamp < 1 || timeStamp > System.currentTimeMillis() {
            // TODO: print a proper error message ?!
            return null;
        }
        
        Map<long, PiHealthRecord> recordsSince = new HashMap();
        
        // TODO: implement
        return recordsSince;
    }
    
    public List<PiHealthRecord> getRecordsListSince(long timeStamp) {
        if (timeStamp < 1 || timeStamp > System.currentTimeMillis() {
            // TODO: print a proper error message ?!
            return null;
        }
        
        List<PiHealthRecord> recordsSince = new ArrayList();
        
        // TODO: implement
        return recordsSince;
    }

    private void clear() {
        records.clear();
    }
}

enum Result {
    OK,
    INVALID_ARGUMENT_PROVIDED,
    UNKNOWN_ERROR
}
