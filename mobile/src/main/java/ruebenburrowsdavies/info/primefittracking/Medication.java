package ruebenburrowsdavies.info.primefittracking;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Random;

public class Medication extends AppCompatActivity
implements NavigationView.OnNavigationItemSelectedListener {
    private PendingIntent pendingIntent;
    public static final String TAG = "BasicHistoryApi";
    private TaskDBHelper helper;
    public TimePicker tp;
    private ListAdapter listAdapter;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meds2);

        updateUI(); //Refresh news_one List

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);


        //Onclick for the FAB button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialog.Builder builder = new AlertDialog.Builder(Medication.this); //start builder

                LayoutInflater inflater = Medication.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.alert_box, null);
                builder.setView(dialogView);
                builder.setTitle("Add news_one"); //Build dialogue

                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        final EditText edt = (EditText) dialogView.findViewById(R.id.edit1);
                        tp = (TimePicker) dialogView.findViewById(R.id.timePicker);
                        //Retrieve time and input values

                        String task = edt.getText().toString(); //retrieve vales from fields
                        String strDateTime = tp.getCurrentHour() + ":" + tp.getCurrentMinute();

                        TaskDBHelper helper = new TaskDBHelper(Medication.this);
                        SQLiteDatabase sqlDB = helper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.clear();

                        values.put(TaskContract.Columns.TASK, task);
                        //place values in DB
                        values.put(TaskContract.Columns.DATE, strDateTime);

                        Random generator = new Random();
                        int randomNum = generator.nextInt();

                        values.put(TaskContract.Columns.NOTIFICATION, randomNum);
                        //place values in db

                        sqlDB.insertWithOnConflict(TaskContract.TABLE, null, values,
                                SQLiteDatabase.CONFLICT_IGNORE); //Place values and write over conflicts
                        updateUI(); //refresh list view


                        Notif nn = new Notif(Medication.this);
                        nn.to_reminder(randomNum); //Run method in the Notif Class to start notifications
                    }
                });

                builder.setNegativeButton("Cancel", null);
                builder.create().show();
            }
        });

        //Database Stuff
        SQLiteDatabase sqlDB = new TaskDBHelper(this).getWritableDatabase();
        Cursor cursor = sqlDB.query(TaskContract.TABLE,
                new String[]{TaskContract.Columns.TASK},
                null,null,null,null,null);

        cursor.moveToFirst();
        while(cursor.moveToNext()) {
            Log.d("MainActivity cursor",
                    cursor.getString(
                            cursor.getColumnIndexOrThrow(
                                    TaskContract.Columns.TASK)));
        }

        SQLiteDatabase sqlDB2 = new TaskDBHelper(this).getWritableDatabase();
        Cursor cursor2 = sqlDB2.query(TaskContract.TABLE,
                new String[]{TaskContract.Columns.DATE},
                null,null,null,null,null);

        cursor2.moveToFirst();
        while(cursor2.moveToNext()) {
            Log.d("MainActivity cursor",
                    cursor2.getString(
                            cursor2.getColumnIndexOrThrow(
                                    TaskContract.Columns.DATE)));
        }

    }

    public void onDoneButtonClick(View view) {
        View v = (View) view.getParent();

        //Delete Records from DB once delete button is clicked
        TextView taskTextView = (TextView) v.findViewById(R.id.taskTextView);
        TextView taskTextView2 = (TextView) v.findViewById(R.id.time);

        String task2 = taskTextView2.getText().toString();
        String task = taskTextView.getText().toString();

        String sql = String.format("DELETE FROM %s WHERE %s = '%s'",
                TaskContract.TABLE,
                TaskContract.Columns.TASK,
                task);

        String sq2l = String.format("DELETE FROM %s WHERE %s = '%s'",
                TaskContract.TABLE,
                TaskContract.Columns.DATE,
                task2);

        helper = new TaskDBHelper(Medication.this);
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        sqlDB.execSQL(sql);
        sqlDB.execSQL(sq2l); //Run SQL commands

        updateUI(); //Refresh ListView
        DeleteNoti(); //Delete All notifications currently active
    }



    public void DeleteNoti(){

        // DELETE ALL NOTIFCATION THIS IS SO WE DONT GET REPEATS SINCE THE ID IS UNIQUE
        helper = new TaskDBHelper(Medication.this);
        SQLiteDatabase sqlDB = helper.getWritableDatabase(); //Set up Readable DB
        Cursor cursor = sqlDB.query(TaskContract.TABLE,
                new String[]{TaskContract.Columns._ID, TaskContract.Columns.TASK, TaskContract.Columns.DATE,TaskContract.Columns.NOTIFICATION},
                null, null, null, null, null);
        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            // Loop until all vales have been seen

            String notValue = cursor.getString(3);
            int numberNew = Integer.parseInt(notValue);
            //Gather unique ID number stored in DB

            //Delete Pending intent by passing Unique ID number, therefore delteing notifcation
            Intent intent=new Intent(Medication.this,BackgroundService.class);
            AlarmManager manager=(AlarmManager)Medication.this.getSystemService(Activity.ALARM_SERVICE);
             PendingIntent pendingIntent=PendingIntent.getService(Medication.this,
                     numberNew,intent, PendingIntent.FLAG_ONE_SHOT);
              try {
                  manager.cancel(pendingIntent);
               } catch (Exception e) {
                  Log.e(TAG, "AlarmManager update was not canceled. " + e.toString()); //Catch If error
             }

            cursor.moveToNext();
        }

            setValuies(); //Rerun the notifications with new values in DB

    }

    public void setValuies(){

        helper = new TaskDBHelper(Medication.this);
        SQLiteDatabase sqlDB = helper.getWritableDatabase();
        Cursor cursor = sqlDB.query(TaskContract.TABLE,
                new String[]{TaskContract.Columns._ID, TaskContract.Columns.TASK, TaskContract.Columns.DATE,TaskContract.Columns.NOTIFICATION},
                null, null, null, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) { // Loop until all vales have been seen

            String notValue = cursor.getString(3);
            int numberNew = Integer.parseInt(notValue);

            Notif nn = new Notif(Medication.this);
            nn.to_reminder(numberNew); //Pass new Unique ID number from DB into this method
            cursor.moveToNext();
        }
    }

    private void updateUI() {

        //Quick method to update the ListView by passing adapter into it
        helper = new TaskDBHelper(Medication.this);
        SQLiteDatabase sqlDB = helper.getReadableDatabase();

        Cursor cursor = sqlDB.query(TaskContract.TABLE,
                new String[]{TaskContract.Columns._ID, TaskContract.Columns.TASK, TaskContract.Columns.DATE},
                null, null, null, null, null);

        listAdapter = new SimpleCursorAdapter(
                this,
                R.layout.task_view,
                cursor,
                new String[]{TaskContract.Columns.TASK, TaskContract.Columns.DATE},
                new int[]{R.id.taskTextView, R.id.time},

                0
        );

        lv = (ListView)findViewById(R.id.list);
        lv.setAdapter(listAdapter); //fill with adapter
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.meds2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent2 = new Intent(Medication.this, MainActivity.class);
            startActivity(intent2);
        } else if (id == R.id.nav_med) {
            Intent intent2 = new Intent(Medication.this, Medication.class);
            startActivity(intent2);

        } else if (id == R.id.vent) {
            Intent intent = new Intent(Medication.this, news_two.class);
            startActivity(intent);
        } else if (id == R.id.nav_sett) {
            Intent intent = new Intent(Medication.this, Settings_new.class);
            startActivity(intent);
        }else if (id == R.id.teleg) {
            Intent intent = new Intent(Medication.this, news_one.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
