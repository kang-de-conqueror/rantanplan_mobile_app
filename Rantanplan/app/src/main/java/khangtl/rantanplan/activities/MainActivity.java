package khangtl.rantanplan.activities;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.List;

import khangtl.rantanplan.R;
import khangtl.rantanplan.services.BackgroundService;
import khangtl.rantanplan.utils.SQLiteDBHelper;

public class MainActivity extends AppCompatActivity {
    public static SQLiteDBHelper sqLiteDBHelper;

    public static final String CHANNEL_ID = "1";
    public static final int NOTIFY_ID = 1;

    private final int MY_REQUEST_CODE = 123;
    private final int MIN_MINIMUM = 1;
    public static final int DEFAULT_MINIMUM = 5;
    private final int MIN_EMPTY = 1;
    public static final int DEFAULT_EMPTY = 2;
    public static final List<Integer> MAXIMUM_LIST_VALUE = Arrays.asList(1, 2, 3, 5, 8, 10);
    private int holdEmptyValue = 10;

    private TextView txtMain;
    private TextView txtMinimum;
    private TextView txtMaximum;
    private TextView txtEmpty;

    private Intent serviceIntent;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String str = intent.getStringExtra("DATA_RECEIVE");
            txtMain.setText(str);
        }
    };

    public static boolean neverAskAgainSelected(final Activity activity, final String permission) {
        final boolean prevShouldShowStatus = getRationaleDisplayStatus(activity, permission);
        final boolean currShouldShowStatus = ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
        return prevShouldShowStatus != currShouldShowStatus;
    }

    public static boolean getRationaleDisplayStatus(final Context context, final String permission) {
        SharedPreferences genPrefs = context.getSharedPreferences("GENERIC_PREFERENCES", Context.MODE_PRIVATE);
        return genPrefs.getBoolean(permission, false);
    }

    private void sendValueToBroadcast(String extra, int value) {
        Intent intent = new Intent();
        switch (extra) {
            case "MINIMUM_VALUE":
                intent.setAction("MINIMUM_ACTION");
                break;
            case "MAXIMUM_VALUE":
                intent.setAction("MAXIMUM_ACTION");
                break;
            case "EMPTY_VALUE":
                intent.setAction("EMPTY_ACTION");
                break;
        }
        intent.putExtra(extra, value);
        sendBroadcast(intent);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void checkLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                if (neverAskAgainSelected(this, Manifest.permission.ACCESS_COARSE_LOCATION) || neverAskAgainSelected(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                    alertDialogBuilder.setMessage("Permission denied. Please open the setting to allow location permission");
                    alertDialogBuilder.setPositiveButton("Okay", null);
                    alertDialogBuilder.show();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.ACCESS_NETWORK_STATE
                    }, MY_REQUEST_CODE);
                }
            } else {
                if (serviceIntent == null) {
                    serviceIntent = new Intent(this, BackgroundService.class);
                }
                startService(serviceIntent);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (serviceIntent == null) {
                    serviceIntent = new Intent(this, BackgroundService.class);
                }
                startService(serviceIntent);
            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Permission denied. Please open the setting to allow location permission");
                alertDialogBuilder.setPositiveButton("Okay", null);
                alertDialogBuilder.show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConstraintLayout currentLayout = findViewById(R.id.main);
        currentLayout.setBackgroundColor(Color.RED);

        txtMain = findViewById(R.id.txtMain);
        createMinimumIdleComponent();
        createMaximumScanComponent();
        createEmptyHandedScanComponent();
        createNotificationChannel();
        checkLocationPermission();
        sqLiteDBHelper = new SQLiteDBHelper(this);
    }

    private void createMinimumIdleComponent() {
        txtMinimum = findViewById(R.id.txtMinimum);
        txtMinimum.setText(String.valueOf(DEFAULT_MINIMUM) + " min");
        SeekBar minimumSeekBar = (SeekBar) findViewById(R.id.seekBarMinimumIdle);
        int MAX_MINIMUM = 10;
        minimumSeekBar.setMax(MAX_MINIMUM);
        minimumSeekBar.setProgress(DEFAULT_MINIMUM);
        minimumSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= MIN_MINIMUM) {
                    seekBar.setProgress(MIN_MINIMUM);
                    sendValueToBroadcast("MINIMUM_VALUE", MIN_MINIMUM);
                    txtMinimum.setText(String.valueOf(MIN_MINIMUM) + " min");
                } else {
                    sendValueToBroadcast("MINIMUM_VALUE", progress);
                    txtMinimum.setText(String.valueOf(progress) + " min");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void createMaximumScanComponent() {
        txtMaximum = findViewById(R.id.txtMaximum);
        txtMaximum.setText(String.valueOf(holdEmptyValue));
        SeekBar maximumSeekBar = (SeekBar) findViewById(R.id.seekBarMaximumScan);
        maximumSeekBar.setMax(MAXIMUM_LIST_VALUE.get(MAXIMUM_LIST_VALUE.size() - 1));
        maximumSeekBar.setProgress(holdEmptyValue);
        if (holdEmptyValue == 10)
            txtMaximum.setText("infinite");
        maximumSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (MAXIMUM_LIST_VALUE.contains(progress)) {
                    holdEmptyValue = progress;
                    seekBar.setProgress(progress);
                    if (progress == MAXIMUM_LIST_VALUE.get(MAXIMUM_LIST_VALUE.size() - 1)) {
                        txtMaximum.setText("infinite");
                    } else {
                        txtMaximum.setText(String.valueOf(progress));
                    }
                } else {
                    seekBar.setProgress(holdEmptyValue);
                }
                sendValueToBroadcast("MAXIMUM_VALUE", holdEmptyValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void createEmptyHandedScanComponent() {
        txtEmpty = findViewById(R.id.txtEmpty);
        txtEmpty.setText(String.valueOf(DEFAULT_EMPTY));
        SeekBar emptySeekBar = (SeekBar) findViewById(R.id.seekBarEmptyHanded);
        int MAX_EMPTY = 5;
        emptySeekBar.setMax(MAX_EMPTY);
        emptySeekBar.setProgress(DEFAULT_EMPTY);
        emptySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= MIN_EMPTY) {
                    seekBar.setProgress(MIN_EMPTY);
                    sendValueToBroadcast("EMPTY_VALUE", MIN_EMPTY);
                    txtEmpty.setText(String.valueOf(MIN_EMPTY));
                } else {
                    sendValueToBroadcast("EMPTY_VALUE", progress);
                    txtEmpty.setText(String.valueOf(progress));
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(broadcastReceiver, new IntentFilter("WIFI_DETECTION"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(broadcastReceiver);
        if (serviceIntent != null) {
            stopService(serviceIntent);
            serviceIntent = null;
        }
    }
}
