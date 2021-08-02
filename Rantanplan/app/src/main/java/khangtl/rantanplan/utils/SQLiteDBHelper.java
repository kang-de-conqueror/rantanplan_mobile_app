package khangtl.rantanplan.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDBHelper extends SQLiteOpenHelper {
    public static final int DB_VERSION = 1;
    public static final String RANTANPLAN_DB = "Rantanplan.db";

    private static final String SQL_CREATE_PLACES =
            "CREATE TABLE IF NOT EXISTS " + SQLiteEntry.PLACE_TABLE + " ("
                    + SQLiteEntry.PLACE_ID + " TEXT PRIMARY KEY, "
                    + SQLiteEntry.PLACE_NAME + " TEXT NOT NULL, "
                    + SQLiteEntry.PLACE_ADDRESS + " TEXT NOT NULL)";

    private static final String SQL_CREATE_IMPLEMENTATIONS =
            "CREATE TABLE IF NOT EXISTS " + SQLiteEntry.IMPLEMENTATION_TABLE + " ("
                    + SQLiteEntry.IMPLEMENTATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + SQLiteEntry.IMPLEMENTATION_START_TIME + " TEXT NOT NULL, "
                    + SQLiteEntry.IMPLEMENTATION_END_TIME + " TEXT NOT NULL, "
                    + SQLiteEntry.IMPLEMENTATION_ROUND_COUNT + " INTEGER NOT NULL, "
                    + "PlaceID TEXT NOT NULL, "
                    + "FOREIGN KEY(PlaceID) REFERENCES Places(ID))";

    private static final String SQL_CREATE_SIGNALS =
            "CREATE TABLE IF NOT EXISTS " + SQLiteEntry.SIGNAL_TABLE + " ("
                    + SQLiteEntry.SIGNAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + SQLiteEntry.SIGNAL_BSSID + " TEXT NOT NULL, "
                    + SQLiteEntry.SIGNAL_SSID + " TEXT NOT NULL, "
                    + SQLiteEntry.SIGNAL_FREQUENCY + " INTEGER NOT NULL, "
                    + SQLiteEntry.SIGNAL_LEVEL + " INTEGER NOT NULL, "
                    + SQLiteEntry.SIGNAL_SAMPLE_COUNT + " INTEGER NOT NULL, "
                    + "ImplementationID INTEGER NOT NULL, "
                    + "FOREIGN KEY(ImplementationID) REFERENCES Implementations(ID))";

    private static final String SQL_DELETE_PLACES =
            "DROP TABLE IF EXISTS " + SQLiteEntry.PLACE_TABLE;

    private static final String SQL_DELETE_IMPLEMENTATIONS =
            "DROP TABLE IF EXISTS " + SQLiteEntry.IMPLEMENTATION_TABLE;

    private static final String SQL_DELETE_SIGNALS =
            "DROP TABLE IF EXISTS " + SQLiteEntry.SIGNAL_TABLE;

    public SQLiteDBHelper(Context context) {
        super(context, RANTANPLAN_DB, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_PLACES);
        db.execSQL(SQL_CREATE_IMPLEMENTATIONS);
        db.execSQL(SQL_CREATE_SIGNALS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_SIGNALS);
        db.execSQL(SQL_DELETE_IMPLEMENTATIONS);
        db.execSQL(SQL_DELETE_PLACES);
        onCreate(db);
    }
}
