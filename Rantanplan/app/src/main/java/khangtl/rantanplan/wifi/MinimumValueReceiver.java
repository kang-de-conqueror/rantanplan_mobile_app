package khangtl.rantanplan.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import khangtl.rantanplan.activities.MainActivity;
import khangtl.rantanplan.services.BackgroundService;

public class MinimumValueReceiver extends BroadcastReceiver {
    private static int MINIMUM_IDLE_MOTION_POSITION_DURATION = MainActivity.DEFAULT_MINIMUM;
    private BackgroundService backgroundService;

    public MinimumValueReceiver(BackgroundService backgroundService) {
        this.backgroundService = backgroundService;
    }

    public static int getMinimumIdleMotionPositionDuration() {
        return MINIMUM_IDLE_MOTION_POSITION_DURATION;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("MINIMUM_ACTION")) {
            MINIMUM_IDLE_MOTION_POSITION_DURATION = intent.getIntExtra("MINIMUM_VALUE", 5);
            backgroundService.closeTimer();
            backgroundService.scheduleTimer();
        }

    }
}
