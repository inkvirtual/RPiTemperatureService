package Statistics;

import Configuration.Configuration;

import java.util.*;

/**
 * Created by dev on 16.05.2017.
 */
public class PiHealthStatistics {
    // map <timestamp in ms, PiHealthRecord>
    private Map<Long, PiHealthRecord> records = Collections.synchronizedMap(new HashMap<>());
    private int minRecordsNumber;
    private int maxRecordsNumber;

    private static PiHealthStatistics instance;

    private PiHealthStatistics() throws InstantiationException {
        //TODO: verify this
        minRecordsNumber = Integer.parseInt(Configuration.getInstance().getProperties().getOrDefault("statistics.records.number.min", "50"));
        maxRecordsNumber = Integer.parseInt(Configuration.getInstance().getProperties().getOrDefault("statistics.records.number.max", "100"));

        if (minRecordsNumber < 10)
            throw new InstantiationException("Invalid \"statistics.records.number.min\" argument value:" + minRecordsNumber);

        if (maxRecordsNumber < minRecordsNumber || maxRecordsNumber < 20)
            throw new InstantiationException("Invalid \"statistics.records.number.max\" argument value:" + maxRecordsNumber);

    }

    public static PiHealthStatistics getInstance() throws InstantiationException {
        if (instance == null)
            instance = new PiHealthStatistics();
        return instance;
    }

    public Result addRecord(PiHealthRecord record) {
        return addRecord(record.getCpuTemperature(), record.getCpuFrequency(), record.isFanOn());
    }

    public Result addRecord(double cpuTemp, int cpuFreq, boolean fanIsOn) {
        if (cpuTemp < 1 || cpuFreq < 1)
            return Result.INVALID_ARGUMENT_PROVIDED;

        //TODO: find a better method to resize records map to minRecordsNumber size
        //If records size is bigger than maxRecordsNumber then resize records to minRecordsNumber
        if (records.size() > maxRecordsNumber) {
            Map<Long, PiHealthRecord> temp = records;
            records.clear();

            int index = 0;

            for (Map.Entry<Long, PiHealthRecord> entry : records.entrySet()) {
                records.put(entry.getKey(), entry.getValue());
                index++;

                if (index > minRecordsNumber)
                    break;
            }
        }
        
        this.records.put(getCurrentTimeMillis(), new PiHealthRecord(cpuTemp, cpuFreq, fanIsOn));
        return Result.OK;
    }

    protected long getCurrentTimeMillis() {
        return System.currentTimeMillis();
    }

    public Map<Long, PiHealthRecord> getRecords() {
        return records;
    }
    
    public Map<Long, PiHealthRecord> getRecordsMapSince(long timestamp) {
        if (timestamp < 1 || timestamp > getCurrentTimeMillis()) {
            // TODO: print a proper error message ?!
            return null;
        }
        
        Map<Long, PiHealthRecord> recordsSince = new HashMap();
        for (Map.Entry<Long, PiHealthRecord> entry : records.entrySet()) {
            if (entry.getKey() > timestamp)
                recordsSince.put(entry.getKey(), entry.getValue());
        }
        
        return recordsSince;
    }
    
    public List<PiHealthRecord> getRecordsListSince(long timestamp) {
        if (timestamp < 1 || timestamp > getCurrentTimeMillis()) {
            // TODO: print a proper error message ?!
            return null;
        }
        
        List<PiHealthRecord> recordsSince = new ArrayList();
        for (Map.Entry<Long, PiHealthRecord> entry : records.entrySet()) {
            if (entry.getKey() > timestamp)
                recordsSince.add(entry.getValue());
        }
        
        return recordsSince;
    }

    public void clearRecords() {
        records.clear();
    }
}

enum Result {
    OK,
    INVALID_ARGUMENT_PROVIDED,
    UNKNOWN_ERROR
}
