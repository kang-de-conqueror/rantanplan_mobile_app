package khangtl.rantanplan.daos;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import khangtl.rantanplan.activities.MainActivity;
import khangtl.rantanplan.dtos.IdentifiedPlaceDTO;
import khangtl.rantanplan.dtos.PlaceDTO;
import khangtl.rantanplan.dtos.SignalDTO;
import khangtl.rantanplan.utils.SQLiteDBHelper;

public class PlaceDAO implements Serializable {

    private SQLiteDBHelper sqLiteDBHelper;
    private SQLiteDatabase db;
    private Cursor cursor;

    private void closeDatabase() {
        if (cursor != null) {
            cursor.close();
        }
        if (db != null) {
            db.close();
        }
        if (sqLiteDBHelper != null) {
            sqLiteDBHelper.close();
        }
    }

    public boolean addSignalIdentifiedPlace(IdentifiedPlaceDTO dto) throws Exception {
        boolean isAdded = false;
        try {
            sqLiteDBHelper = MainActivity.sqLiteDBHelper;
            db = sqLiteDBHelper.getWritableDatabase();
            String insertPlacesSQL = "INSERT INTO Places VALUES(?, ?, ?)";
            cursor = db.rawQuery(insertPlacesSQL, new String[]{dto.getId(), dto.getName(), dto.getAddress()});
            String insertImplementationsSQL = "INSERT INTO Places VALUES(?, ?, ?)";
            cursor = db.rawQuery(insertImplementationsSQL, new String[]{dto.getId(), dto.getName(), dto.getAddress()});
            String insertSignalsSQL = "INSERT INTO Places VALUES(?, ?, ?)";
            cursor = db.rawQuery(insertSignalsSQL, new String[]{dto.getId(), dto.getName(), dto.getAddress()});
        } finally {
            closeDatabase();
        }
        return isAdded;
    }

    public void searchPlace(SignalDTO coreSignal, HashMap<String, PlaceDTO> mapPlace, HashMap<String, List<SignalDTO>> mapPlaceSignal) throws Exception {
        SignalDTO rowSignal;
        PlaceDTO placeDTO;
        String placeId, placeName, placeAddress, bssid, ssid;
        int frequency, signalLevel, sampleCount;
        try {
            sqLiteDBHelper = MainActivity.sqLiteDBHelper;
            db = sqLiteDBHelper.getReadableDatabase();
            String sql = "SELECT p.ID as PlaceID, p.Name, p.Address, i.ID, i.StartTime, " +
                    "i.EndTime, i.RoundCount, si.ID, si.BSSID, si.SSID, si.Frequency, " +
                    "si.SignalLevel, si.SampleCount FROM Places p INNER JOIN Implementations i " +
                    "ON i.PlaceID = p.ID INNER JOIN Signals si ON si.ImplementationID = i.ID " +
                    "WHERE si.BSSID=?";
            cursor = db.rawQuery(sql, new String[]{String.valueOf(coreSignal.getBssid())});

            while (cursor.moveToNext()) {
                // Create place
                placeId = cursor.getString(cursor.getColumnIndex("PlaceId"));
                placeName = cursor.getString(cursor.getColumnIndex("PlaceName"));
                placeAddress = cursor.getString(cursor.getColumnIndex("PlaceAddress"));
                placeDTO = new PlaceDTO(placeId, placeName, placeAddress);

                // Create signal
                bssid = cursor.getString(cursor.getColumnIndex("BSSID"));
                ssid = cursor.getString(cursor.getColumnIndex("SSID"));
                frequency = cursor.getInt(cursor.getColumnIndex("Frequency"));
                signalLevel = cursor.getInt(cursor.getColumnIndex("SignalLevel"));
                sampleCount = cursor.getInt(cursor.getColumnIndex("SampleCount"));
                rowSignal = new SignalDTO(bssid, ssid, frequency, signalLevel, sampleCount);

                // Check place exist in map
                if (!mapPlace.containsKey(placeId)) {
                    mapPlace.put(placeId, placeDTO);
                }

                if (mapPlaceSignal.containsKey(placeId)) {
                    List<SignalDTO> currentSignalList = mapPlaceSignal.get(placeId);
                    boolean isExisted = false;
                    for (int i = 0; i < currentSignalList.size(); i++) {
                        SignalDTO currentSignal = currentSignalList.get(i);
                        if (currentSignal.getBssid().equals(rowSignal.getBssid())) {
                            int coreSignalLevel = coreSignal.getSignalLevel();
                            int rowSignalLevel = rowSignal.getSignalLevel();
                            int currentSignalLevel = currentSignal.getSignalLevel();
                            if (Math.abs(currentSignalLevel - coreSignalLevel) >= Math.abs(rowSignalLevel - coreSignalLevel)) {
                                currentSignalList.set(i, rowSignal);
                            }
                            isExisted = true;
                            break;
                        }
                    }
                    if (!isExisted) currentSignalList.add(rowSignal);
                } else {
                    mapPlaceSignal.put(placeId, new ArrayList<SignalDTO>(Arrays.asList(rowSignal)));
                }
            }
        } finally {
            closeDatabase();
        }
    }
}
