package ruebenburrowsdavies.info.primefittracking;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import java.util.concurrent.atomic.AtomicInteger;

public class BackgroundService extends Service{
    private boolean isRunning;
    private Context context;
    private Thread backgroundThread;
    public static final String TAG = "BasicHistoryApi";
    static AtomicInteger c = new AtomicInteger(0);

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        this.context = this;
        this.isRunning = false;
        this.backgroundThread = new Thread(myTask);
    }

    private Runnable myTask = new Runnable() {
        public void run() {

            mainMeth();// run method
            stopSelf(); //stop

        }
    };

    public static int getID() {
        return c.incrementAndGet();
    }

    public void mainMeth(){

        //BUILD NOTIFCATION FOR USER
        Intent browserIntent = new Intent(this,Medication.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                browserIntent,
                0);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_action_meds_light)
                        .setContentTitle("Medication time! ")
                        .setContentIntent(pendingIntent)
                        .setContentText("Open Application" )
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setVibrate(new long[]{200, 200, 200, 200}) //set virbrate & color of led
                        .setLights(Color.RED, 1000 , 1000);

        NotificationManager nManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nManager.notify(getID(), builder.build());

    }

    @Override
    public void onDestroy() {
        this.isRunning = false;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!this.isRunning) {
            this.isRunning = true;
            this.backgroundThread.start();
        }
        return START_STICKY;
    }

}
