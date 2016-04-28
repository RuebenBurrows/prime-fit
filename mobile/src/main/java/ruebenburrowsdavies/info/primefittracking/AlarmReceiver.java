package ruebenburrowsdavies.info.primefittracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
            Intent background = new Intent(context, BackgroundService.class);
            context.startService(background);
            //START BACKGROUND SERVICE TO BUILDNOTIFCATIONS
    }

}
