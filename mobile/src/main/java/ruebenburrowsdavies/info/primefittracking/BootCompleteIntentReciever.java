package ruebenburrowsdavies.info.primefittracking;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BootCompleteIntentReciever extends BroadcastReceiver {
    public static final String TAG = "BasicHistoryApi";
    private TaskDBHelper helper;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.e(TAG, "Background Service has RUN for BOOT COMPLETE ");
        //RECEIVE ONCE A DEVICE REBOOT HAS OCCURED
        setValuies(context);

    }


    public void setValuies(Context context){

        helper = new TaskDBHelper(context);
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        Cursor cursor = sqlDB.query(TaskContract.TABLE,
                new String[]{TaskContract.Columns._ID, TaskContract.Columns.TASK, TaskContract.Columns.DATE,TaskContract.Columns.NOTIFICATION},
                null, null, null, null, null);
        //RECREATE THE ALARMS BASED ON DB TIMES

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) { // Loop until all vales have been seen

            String notValue = cursor.getString(3);
            int numberNew = Integer.parseInt(notValue);

            //PASS VALUES OF TIME INTO THE Notif CLASS TO RECREATE ALARMS
            Notif nn = new Notif(context);
            nn.to_reminder(numberNew);
            cursor.moveToNext();
        }




    }
}
