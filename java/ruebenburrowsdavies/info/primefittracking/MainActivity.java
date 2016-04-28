package ruebenburrowsdavies.info.primefittracking;

import android.Manifest;
import android.app.UiModeManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataReadResult;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    //SETTING UP VARIABLES FOR PROGRAMME
    public static final String TAG = "BasicHistoryApi";
    String s, PREF_SHOW_ABOUT_ON_APP_START,channel;
    private RecycleAdapterSteps adapter;
    private List<FeedItem> feedItemList = new ArrayList<FeedItem>();
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    public TextView myAwesomeTextView;
    private GoogleApiClient mClient = null;
    private RecyclerView mRecyclerView;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private ShareActionProvider mShareActionProvider;
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    public Calendar cal;
    int i=8;


    static {
        AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_AUTO);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        ProgressBar progressBar2 = (ProgressBar) findViewById(R.id.progressBar3);
        //Display spinning progress bar
        progressBar.setVisibility(View.VISIBLE);
        progressBar2.setVisibility(View.VISIBLE);

        //Set night time dark mode automatically
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);

        // add this code for 6.0
        UiModeManager uiManager = (UiModeManager) getSystemService(Context.UI_MODE_SERVICE);
        uiManager.setNightMode(UiModeManager.MODE_NIGHT_AUTO);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        myAwesomeTextView   = (TextView)findViewById(R.id.stepCount);
        navigationView.setItemIconTintList(null);

        //Open SharedPref File
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);

        //SET VALUE TO TRUE, START THAT CLASS FIRST TIME START UP
        if(prefs.getBoolean(PREF_SHOW_ABOUT_ON_APP_START, true)){
            Intent intent = new Intent(this, start_fullscreen_partone.class);
            startActivity(intent);

            prefs.edit().putBoolean(PREF_SHOW_ABOUT_ON_APP_START, false).apply();
            // This can be done in line within the SetupActivity to ensure they have actually done what you wanted

            finish();

        } else {
            // Check for authenitcation
            if (savedInstanceState != null) {
                authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
            }
            // When permissions are revoked the app is restarted so onCreate is sufficient to check for
            // permissions core to the Activity's functionality.
            if (!checkPermissions()) {
                requestPermissions();
            }
            //Clear set count
            feedItemList.clear();

            //SET DEFUALT STEP VALUE IF USER HAS NOT INSERTED ANYTHING
            prefs = getSharedPreferences(MY_PREFS_NAME, MODE_APPEND);

            channel = prefs.getString("step", "6000");


            TextView tt = (TextView) findViewById(R.id.value_steps);
            String nGoal = "Goal is " + channel;

            tt.setText(nGoal);

        }



        isInternetAvailable();



        //SET UP RECYCLER VIEW FOR PAST STEPS
        mRecyclerView = (RecyclerView) findViewById(R.id.cardList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    public void isInternetAvailable() {

        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal==0);


            if (reachable == true){

            }else{

                Snackbar snackbar = Snackbar
                        .make(findViewById(android.R.id.content),"No Internet Connection. Some Features wont work without it.", Snackbar.LENGTH_LONG)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                            }
                        });
                snackbar.show();
            }



        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Error", Toast.LENGTH_LONG).show();


        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        // This ensures that if the user denies the permissions then uses Settings to re-enable
        // them, the app will start working.
        buildFitnessClient();
        mClient.connect();
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
        getMenuInflater().inflate(R.menu.main, menu);
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.action_share);

        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

        //SHARING TODAYS SET COUNT WITH APPS
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString("steps", null);
        if (restoredText != null) {
            String name = prefs.getString("steps", "No steps defined");//"No name defined" is the default value.
        }

        Log.i(TAG, "Value of " + restoredText + " Has been retrived and shared");

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,"I have done a amazing " +restoredText+" steps today!!");

        mShareActionProvider.setShareIntent(shareIntent);

        return true;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.INTERNET);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.main_activity_view),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.INTERNET},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.INTERNET},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent2 = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent2);
        } else if (id == R.id.nav_med) {
            Intent intent2 = new Intent(MainActivity.this, Medication.class);
            startActivity(intent2);

        } else if (id == R.id.vent) {
            Intent intent = new Intent(MainActivity.this, news_two.class);
            startActivity(intent);
        } else if (id == R.id.nav_sett) {
            Intent intent = new Intent(MainActivity.this, Settings_new.class);
            startActivity(intent);
        }else if (id == R.id.teleg) {
            Intent intent = new Intent(MainActivity.this, news_one.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void buildFitnessClient() {

        // THIS WILL CHECK TO SEE IF USER HAS GRANTED PERMISSION, IF THEY HAVE RUN METHOD IF NOT
        // REQUEST AUTH
        if (mClient == null && checkPermissions() ) {
            mClient = new GoogleApiClient.Builder(this)
                    .addApi(Fitness.HISTORY_API) //CONNECT TO THE HISTORY API
                    .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                    .addConnectionCallbacks(
                            new GoogleApiClient.ConnectionCallbacks() {
                                @Override
                                public void onConnected(Bundle bundle) {
                                    Log.i(TAG, "Connected!!!");
                                    // Now you can make calls to the Fitness APIs.
                                    new Title().execute(); //CALL ASYNC METHOD TO RUN TASKS
                                }

                                @Override
                                public void onConnectionSuspended(int i) {
                                    // If your connection to the sensor gets lost at some point,
                                    // you'll be able to determine the reason and react to it here.
                                    if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                        Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                        Toast.makeText(getBaseContext(), "Connection lost.  Cause: Network Lost.", Toast.LENGTH_LONG).show();

                                    } else if (i
                                            == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                        Log.i(TAG,
                                                "Connection lost.  Reason: Service Disconnected");
                                    }
                                }
                            }
                    )
                    .enableAutoManage(this, 0, new GoogleApiClient.OnConnectionFailedListener() {
                        @Override
                        public void onConnectionFailed(ConnectionResult result) {
                            Log.i(TAG, "Google Play services connection failed. Cause: " +
                                    result.toString());
                            Toast.makeText(getBaseContext(), "Google Play services connection failed. Cause: " +
                                    result.toString(), Toast.LENGTH_LONG).show();

                        }
                    })
                    .build();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted.
                buildFitnessClient();
            } else {

            }
        }
    }

    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private class Title extends AsyncTask<Void, Void, Void> { //method for background data only if  AUTH has been given

        @Override
        protected Void doInBackground(Void... params) {

            Calendar cal = Calendar.getInstance();
            Date now = new Date();
            cal.setTime(now);
            long endTime = cal.getTimeInMillis();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 00);
            long startTime = cal.getTimeInMillis();


            int steps = 0;
            DataSource ESTIMATED_STEP_DELTAS = new DataSource.Builder()
                    .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                    .setType(DataSource.TYPE_DERIVED)
                    .setStreamName("estimated_steps")
                    .setAppPackageName("com.google.android.gms").build();

            // fill result with just the steps from the start and end time of the present day
            PendingResult<DailyTotalResult> result = Fitness.HistoryApi.readDailyTotal(mClient, DataType.AGGREGATE_STEP_COUNT_DELTA);
            DailyTotalResult totalResult = result.await(60, TimeUnit.SECONDS);
            if (totalResult.getStatus().isSuccess()) {
                DataSet totalSet = totalResult.getTotal();
                steps = totalSet.isEmpty() ? -1 : totalSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
            }

            // SINGLE VALUE FOR TODAYS STEPS
            s = String.valueOf(steps);


            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString("steps", s);
            Log.e(TAG, "Value of " + s+ " has been stored.");
            editor.commit();

            DataReadRequest readRequest = queryFitnessData();

            DataReadResult dataReadResult =
                    Fitness.HistoryApi.readData(mClient, readRequest).await(1, TimeUnit.MINUTES);
            feedItemList.clear();

            //SET UP WEEKLY STEPS
            printData(dataReadResult);

            return null;
        }



        @Override
        protected void onPostExecute(Void result) {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar2);
            ProgressBar progressBar2 = (ProgressBar) findViewById(R.id.progressBar3);
            progressBar.setVisibility(View.GONE); //SET CIRCLE PROGRESS BAR TO DISAPEAR
            progressBar2.setVisibility(View.GONE);
            String nSteps = s+ " steps";

            myAwesomeTextView.setText(nSteps); //SET SINGLE STEP TEXTFIELD

            adapter = new RecycleAdapterSteps(MainActivity.this, feedItemList);
            mRecyclerView.setAdapter(adapter);//FILL VIEW WITH PAST STEPS
        }

    }

    private DataReadRequest queryFitnessData() {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 week before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();

        cal.setTime(now);
        cal.add(Calendar.DAY_OF_YEAR, -1);
        cal.set(Calendar.HOUR_OF_DAY, 24);
        cal.set(Calendar.MINUTE, 00);

        long endTime = cal.getTimeInMillis();
        cal.set(Calendar.HOUR_OF_DAY, 00);
        cal.set(Calendar.MINUTE, 00);
        cal.add(Calendar.DAY_OF_YEAR, -6);
        long startTime = cal.getTimeInMillis();

        DataSource ESTIMATED_STEP_DELTAS = new DataSource.Builder()
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_DERIVED)
                .setStreamName("estimated_steps")
                .setAppPackageName("com.google.android.gms")
                .build();


        DataReadRequest readRequest = new DataReadRequest.Builder()
                // The data request can specify multiple data types to return, effectively
                // combining multiple data queries into one call.
                // In this example, it's very unlikely that the request is for several hundred
                // datapoints each consisting of a few steps and a timestamp.  The more likely
                // scenario is wanting to see how many steps were walked per day, for 7 days.
                .aggregate(ESTIMATED_STEP_DELTAS, DataType.AGGREGATE_STEP_COUNT_DELTA)
                        // Analogous to a "Group By" in SQL, defines how data should be aggregated.
                        // bucketByTime allows for a time span, whereas bucketBySession would allow
                        // bucketing by "sessions", which would need to be defined in code.
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();
        // [END build_read_data_request]
        return readRequest;
    }

    private void printData(DataReadResult dataReadResult) {
        // [START parse_read_data_result]
        // If the DataReadRequest object specified aggregated data, dataReadResult will be returned
        // as buckets containing DataSets, instead of just DataSets.
        if (dataReadResult.getBuckets().size() > 0) {
            // Log.i(TAG, "Number of returned buckets of DataSets is: "
            //    + dataReadResult.getBuckets().size());
            for (Bucket bucket : dataReadResult.getBuckets()) {
                i = i-1;
                cal = Calendar.getInstance(); // Initialized to today/now
                cal.add(Calendar.DAY_OF_MONTH, -i); //TAKE AWAY ONE DAY ON EACH LOOP, SO TAKE AWAY 7 TIMES, BUT PRINT DAY EACH TIME
                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd");
                    Log.i(TAG, "Cal VALUE IS:  " + sdf.format(cal.getTime()));

                    dumpDataSet(dataSet);
                }
            }
        } else if (dataReadResult.getDataSets().size() > 0) {

            for (DataSet dataSet : dataReadResult.getDataSets()) {

            }
        }
        // [END parse_read_data_result]
    }




    private void dumpDataSet(DataSet dataSet) {
        Log.i(TAG, "Data returned for Data type: " + dataSet.getDataType().getName());
        for (DataPoint dp : dataSet.getDataPoints()) {
            for(Field field : dp.getDataType().getFields()) { //loop 7 times

                    int test = dp.getValue(field).asInt();

                    String weekSteps = String.valueOf(test); //get weekday steps one at a time

                    SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd");

                    String weekday = sdf.format(cal.getTime());

                    String weekStepsFinal = weekSteps + " steps on " + weekday; //set Textfield to steps and the day

                    FeedItem item = new FeedItem();
                    item.setTitle(weekStepsFinal);

                    feedItemList.add(item);

            }
        }

    }

    //FINISH MAIN ACTIVITY



}
