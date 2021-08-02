package khangtl.rantanplan.wifi;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import khangtl.rantanplan.activities.MainActivity;
import khangtl.rantanplan.activities.RegisterPlaceActivity;
import khangtl.rantanplan.activities.ShowPlaceActivity;
import khangtl.rantanplan.daos.PlaceDAO;
import khangtl.rantanplan.dtos.MatchPlaceDTO;
import khangtl.rantanplan.dtos.MinimizeSignalDTO;
import khangtl.rantanplan.dtos.PlaceDTO;
import khangtl.rantanplan.dtos.SignalDTO;
import khangtl.rantanplan.dtos.UnidentifiedPlaceDTO;
import khangtl.rantanplan.services.BackgroundService;
import khangtl.rantanplan.utils.APIUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WifiScanReceiver extends BroadcastReceiver {
    private BackgroundService backgroundService;

    public static UnidentifiedPlaceDTO dto;

    public static String placeId;
    public static String placeName;
    public static String placeAddress;

    private boolean isFirstResult;
    private int maximumCount;
    private int emptyCount;


    private Date endTime;

    private List<SignalDTO> results;

    public WifiScanReceiver(BackgroundService backgroundService) {
        this.backgroundService = backgroundService;
        this.isFirstResult = true;
        this.maximumCount = 0;
        this.emptyCount = 0;
        this.results = new ArrayList<>();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            showWifiList();
            if (backgroundService.isStopScan()) {
                backgroundService.setStopScan(true);
                endTime = new Date();
                backgroundService.sendMessage("Stop scan!!!");
                sendWifiData();
                if (WifiWaiting.isRegisteredWifi()) {
                    backgroundService.unregisterReceiver(backgroundService.getWifiScanReceiver());
                    backgroundService.setWifiScanReceiver(null);
                    WifiWaiting.setRegisteredWifi(false);
                }
                backgroundService.closeTimer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendWifiData() throws Exception {
        Date startTime = WifiWaiting.getStartTime();
        String start = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").format(startTime);
        String end = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").format(endTime);
        dto = new UnidentifiedPlaceDTO(start, end, maximumCount, results);

        PlaceDAO dao = new PlaceDAO();
        HashMap<String, PlaceDTO> mapPlace = new HashMap<>();
        HashMap<String, List<SignalDTO>> mapPlaceSignal = new HashMap<>();
        for (SignalDTO signal : dto.getSignals()) {
            dao.searchPlace(signal, mapPlace, mapPlaceSignal);
        }

        if (mapPlaceSignal.size() != 0) {
            List<MatchPlaceDTO> matchPlaceDTOList = new ArrayList<>();
            String id, name, address;
            double score;
            for (Map.Entry<String, List<SignalDTO>> entry : mapPlaceSignal.entrySet()) {
                id = entry.getKey();
                name = mapPlace.get(entry.getKey()).getName();
                address = mapPlace.get(entry.getKey()).getAddress();
                score = entry.getValue().size() / dto.getSignals().size();
                matchPlaceDTOList.add(new MatchPlaceDTO(id, name, address, score));
            }
            MatchPlaceDTO max = matchPlaceDTOList.get(0);
            for (MatchPlaceDTO match : matchPlaceDTOList) {
                if (match.getScore() > max.getScore()) {
                    max = match;
                }
            }
            placeId = max.getId();
            placeName = max.getName();
            placeAddress = max.getAddress();

            Intent intent = new Intent(backgroundService.getApplicationContext(), ShowPlaceActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(backgroundService.getApplicationContext(), 0, intent, 0);

            NotificationCompat.Builder builder = backgroundService.createNotificationBuilder("Match place", "Please confirm place name and place address for this signals", pendingIntent);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(backgroundService.getApplicationContext());
            notificationManager.notify(MainActivity.NOTIFY_ID, builder.build());
        } else {
            List<MinimizeSignalDTO> minimizeSignalDTOList = new ArrayList<>();
            for (SignalDTO signal : dto.getSignals()) {
                minimizeSignalDTOList.add(new MinimizeSignalDTO(signal.getBssid(), signal.getSignalLevel()));
            }
            APIUtils.getGeneralWifiAPI().postSearchPlace(minimizeSignalDTOList).enqueue(new Callback<List<MatchPlaceDTO>>() {
                @Override
                public void onResponse(Call<List<MatchPlaceDTO>> call, Response<List<MatchPlaceDTO>> response) {

                    List<MatchPlaceDTO> matchPlaceDTOList = response.body();
                    if (matchPlaceDTOList.size() == 0) {
                        Intent intent = new Intent(backgroundService.getApplicationContext(), RegisterPlaceActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(backgroundService.getApplicationContext(), 0, intent, 0);

                        NotificationCompat.Builder builder = backgroundService.createNotificationBuilder("Register place", "Please register place name and place address for this signals", pendingIntent);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(backgroundService.getApplicationContext());
                        notificationManager.notify(MainActivity.NOTIFY_ID, builder.build());
                    } else {
                        MatchPlaceDTO max = matchPlaceDTOList.get(0);
                        for (MatchPlaceDTO match : matchPlaceDTOList) {
                            if (match.getScore() > max.getScore()) {
                                max = match;
                            }
                        }
                        placeId = max.getId();
                        placeName = max.getName();
                        placeAddress = max.getAddress();

                        Intent intent = new Intent(backgroundService.getApplicationContext(), ShowPlaceActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(backgroundService.getApplicationContext(), 0, intent, 0);

                        NotificationCompat.Builder builder = backgroundService.createNotificationBuilder("Match place", "Please confirm place name and place address for this signals", pendingIntent);
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(backgroundService.getApplicationContext());
                        notificationManager.notify(MainActivity.NOTIFY_ID, builder.build());
                    }
                }

                @Override
                public void onFailure(Call<List<MatchPlaceDTO>> call, Throwable t) {
                    System.out.println(t.getMessage());
                    Toast.makeText(backgroundService.getApplicationContext(), "Send fail", Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    private String getStringOfWifiList() {
        SignalDTO dto;
        boolean isNew = false;
        List<ScanResult> wifiScanList = backgroundService.getWifiManager().getScanResults();
        if (wifiScanList.size() == 0) return null;
        if (isFirstResult) {
            isFirstResult = false;
            for (ScanResult r : wifiScanList) {
                dto = new SignalDTO(r.BSSID, r.SSID, r.frequency, r.level, 1, new ArrayList<Integer>(Collections.singletonList(r.level)));
                results.add(dto);
            }
            isNew = true;
        } else {
            for (ScanResult r : wifiScanList) {
                boolean isExist = false;
                for (SignalDTO wf : results) {
                    if (wf.getBssid().equals(r.BSSID)) {
                        wf.getSignalLevelList().add(r.level);
                        wf.setSignalLevel((int) getMedian(wf.getSignalLevelList()));
                        wf.setSampleCount(wf.getSampleCount() + 1);
                        isExist = true;
                    }
                }
                if (!isExist) {
                    dto = new SignalDTO(r.BSSID, r.SSID, r.frequency, r.level, 1, new ArrayList<Integer>(Collections.singletonList(r.level)));
                    results.add(dto);
                    isNew = true;
                }
            }
        }
        increaseMaximumScan();
        if (!isNew) increaseEmptyHandedScan();
        return getStringWifiResult(wifiScanList);
    }

    private void increaseMaximumScan() {
        maximumCount++;
        if (MaximumValueReceiver.getMaximumScanRound() == 10) return;
        if (maximumCount >= MaximumValueReceiver.getMaximumScanRound()) {
            backgroundService.setStopScan(true);
        }
    }

    private void increaseEmptyHandedScan() {
        emptyCount++;
        if (emptyCount >= EmptyValueReceiver.getEmptyHandedScanRound()) {
            backgroundService.setStopScan(true);
        }
    }

    private String getStringWifiResult(List<ScanResult> list) {
        StringBuilder wifiResult = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            ScanResult r = list.get(i);
            String qualityLevel = null;
            if (-57 <= r.level && r.level <= -45) {
                qualityLevel = "Excellent";
            } else if (-75 <= r.level && r.level <= -58) {
                qualityLevel = "Good";
            } else if (-85 <= r.level && r.level <= -76) {
                qualityLevel = "Fair";
            } else if (-95 <= r.level && r.level <= -86) {
                qualityLevel = "Poor";
            }
            wifiResult.append(i + 1)
                    .append(") SSID: ")
                    .append(r.SSID)
                    .append(", BSSID: ")
                    .append(r.BSSID)
                    .append(", Signal: ")
                    .append(r.level).append(", ")
                    .append(qualityLevel)
                    .append("\n");
        }
        return wifiResult.toString();
    }

    private void showWifiList() {
        String result = getStringOfWifiList();
        if (result == null) {
            backgroundService.sendMessage("No Wifi Access Points");
            backgroundService.closeTimer();
        } else {
            backgroundService.sendMessage("Wifi Access Points Found");
        }
    }

    private double getMedian(List<Integer> list) {
        int n = list.size();
        double median;
        if (n % 2 == 0)
            median = ((double) list.get(n / 2) + (double) list.get(n / 2 - 1)) / 2;
        else
            median = (double) list.get(n / 2);
        return median;
    }
}
