package ruebenburrowsdavies.info.primefittracking;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class news_two extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MyRecyclerAdapterTwo adapter2;
    private List<FeedItem> feedItemList2 = new ArrayList<FeedItem>();
    public static RecyclerView mRecyclerView2;
    static List<String> IMGlINK = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_two);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress2);

        linlaHeaderProgress.setVisibility(View.VISIBLE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // CHECK FOR A ACTIVE INTERNET CONNECTION
        isInternetAvailable();
        new Title().execute();

        feedItemList2.clear();
        mRecyclerView2 = (RecyclerView) findViewById(R.id.newsTwo);
        mRecyclerView2.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    }

    public void isInternetAvailable() {
        // PING WEBSITE AND WAIT FOR A RESPONSE THE SEND A MESSAGE TO USER IF FALSE
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
        getMenuInflater().inflate(R.menu.news_two, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_home) {
            Intent intent2 = new Intent(news_two.this, MainActivity.class);
            startActivity(intent2);
        } else if (id == R.id.nav_med) {
            Intent intent2 = new Intent(news_two.this, Medication.class);
            startActivity(intent2);

        } else if (id == R.id.vent) {
            Intent intent = new Intent(news_two.this, news_two.class);
            startActivity(intent);
        } else if (id == R.id.nav_sett) {
            Intent intent = new Intent(news_two.this, Settings_new.class);
            startActivity(intent);
        }else if (id == R.id.teleg) {
            Intent intent = new Intent(news_two.this, news_one.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private static final String TAG = "MyActivity";

    private class Title extends AsyncTask<Void, Void, Void> { //method for background data

        @Override
        protected Void doInBackground(Void... params) {
            try {

                // Connect to the web site
                Document doc = Jsoup.connect("http://venturebeat.com/tag/health-tech/").timeout(10000).userAgent("Mozilla/5.0").get(); //get website data

                Elements sections = doc.select("article"); //get all data from search and put into elements then go through each one on page,
                //selcting and finding the title, value and html link  for that product then fill an areraylist for later use.

                for (Element section : sections) {
                    Elements mm = section.select("a[href]");
                    String item = mm.attr("href");

                    String title = section.select("h2").text();

                    Element featureImage = section.select("img").first();

                    String item2;
                    item2 = featureImage.attr("src");

                    String kk= String.valueOf(title);

                    FeedItem items = new FeedItem();
                    items.setTitle(kk);
                    items.setHtmlLink(item);
                    items.setPicLink(item2);


                    feedItemList2.add(items);//add title and price to array

                    // htmllink.add(item);//add product link to array
                    IMGlINK.add(item2);
                }



            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }



        @Override
        protected void onPostExecute(Void result) {

            LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress2);

            linlaHeaderProgress.setVisibility(View.GONE);

            adapter2 = new MyRecyclerAdapterTwo(news_two.this, feedItemList2);
            mRecyclerView2.setAdapter(adapter2);



        }
    }



}
