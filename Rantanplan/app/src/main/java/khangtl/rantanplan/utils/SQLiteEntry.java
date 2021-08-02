package khangtl.rantanplan.utils;


public class SQLiteEntry {
    // Info for table Places
    public static final String PLACE_TABLE = "Places";
    public static final String PLACE_ID = "ID";
    public static final String PLACE_NAME = "Name";
    public static final String PLACE_ADDRESS = "Address";

    // Info for table Scans
    public static final String IMPLEMENTATION_TABLE = "Implementations";
    public static final String IMPLEMENTATION_ID = "ID";
    public static final String IMPLEMENTATION_START_TIME = "StartTime";
    public static final String IMPLEMENTATION_END_TIME = "EndTime";
    public static final String IMPLEMENTATION_ROUND_COUNT = "RoundCount";

    // Info for table Signals
    public static final String SIGNAL_TABLE = "Signals";
    public static final String SIGNAL_ID = "ID";
    public static final String SIGNAL_BSSID = "BSSID";
    public static final String SIGNAL_SSID = "SSID";
    public static final String SIGNAL_FREQUENCY = "Frequency";
    public static final String SIGNAL_LEVEL = "SignalLevel";
    public static final String SIGNAL_SAMPLE_COUNT = "SampleCount";
}