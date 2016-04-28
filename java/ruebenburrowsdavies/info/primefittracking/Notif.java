package ruebenburrowsdavies.info.primefittracking;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Calendar;


public class Notif {

    Context context;
    private TaskDBHelper helper;

    public Notif(Context context){
      this.context = context;
    }

    public  void to_reminder(int randomOne)
    {
        //Set up DB and move cursor along values
        helper = new TaskDBHelper(context);
        SQLiteDatabase sqlDB = helper.getReadableDatabase();
        Cursor cursor = sqlDB.query(TaskContract.TABLE,
                new String[]{TaskContract.Columns._ID, TaskContract.Columns.TASK, TaskContract.Columns.DATE},
                null, null, null, null, null);

        cursor.moveToFirst();


        while (!cursor.isAfterLast()) {
            // Loop until all vales have been seen
            String time = cursor.getString(2);
            String[] parts = time.split(":"); //Split  String Value stored in db
            String part1 = parts[0]; // hour
            String part2 = parts[1]; // minute
            int hr = Integer.parseInt(part1);
            int min = Integer.parseInt(part2);

            int random = randomOne;

            setUpAlarm(hr,min,random);
            //Pass values into method below to set up notification with the Background service class
            cursor.moveToNext();
        }
    }

    public void setUpAlarm(int hr, int min, int random){

        //Pass in Hours and Minute and Random Unique ID number and set up the alarm based of this.
        Intent intent=new Intent(context,BackgroundService.class);
        AlarmManager manager=(AlarmManager)context.getSystemService(Activity.ALARM_SERVICE);
        PendingIntent pendingIntent=PendingIntent.getService(context,
                random,intent, PendingIntent.FLAG_ONE_SHOT);

        long _alarm = 0;
        Calendar now = Calendar.getInstance();
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hr);
        cal.set(Calendar.MINUTE,min);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        if(cal.getTimeInMillis() <= now.getTimeInMillis())
            _alarm = cal.getTimeInMillis() + (AlarmManager.INTERVAL_DAY+1); //If time is before current time add 24 hours onto it, do not engade it
        else
            _alarm = cal.getTimeInMillis();

        manager.setRepeating(AlarmManager.RTC_WAKEUP,_alarm,AlarmManager.INTERVAL_DAY,pendingIntent); //Set alarm for time and 24 hours after firing

    }

}
