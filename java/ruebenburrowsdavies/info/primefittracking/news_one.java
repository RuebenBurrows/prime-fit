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

public class news_one extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MyRecyclerAdapter adapter;
    private List<FeedItem> feedItemList = new ArrayList<FeedItem>();
    public static RecyclerView mRecyclerView;
    static List<String> IMGlINK = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);

        linlaHeaderProgress.setVisibility(View.VISIBLE);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        // CHECK FOR INTERNET CONNECTION THEN DISPLAY MESSAGE
        isInternetAvailable();
        new Title().execute();

        feedItemList.clear();
        mRecyclerView = (RecyclerView) findViewById(R.id.medList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

    }

    public void isInternetAvailable() {

        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            // PING A LINK TO SEE IF YOU GET A RESPONSE
            int returnVal = p1.waitFor();
            boolean reachable = (returnVal==0);


            if (reachable){

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

    private class Title extends AsyncTask<Void, Void, Void> {
        //method for background data
        @Override
        protected Void doInBackground(Void... params) {
            try {

                // Connect to the web site
                Document doc = Jsoup.connect("http://www.telegraph.co.uk/news/health/news/").timeout(10000).userAgent("Mozilla/5.0").get(); //get website data

                Elements sections = doc.select("div.summary.headlineImageCentre"); //get all data from search and put into elements then go through each one on page,
                //selecting and finding the title, value and html link  for that product then fill an arraylist for later use.

                for (Element section : sections) {
                    Elements mm = section.select("a[href]");
                    String item = mm.attr("href");
                    String title = section.select("h3").text();

                    Element featureImage = section.select("img").first();

                    String item2;
                    item2 = featureImage.attr("src");

                    String kk= String.valueOf(title);

                    FeedItem items = new FeedItem();
                    items.setTitle(kk);
                    items.setHtmlLink(item);
                    items.setPicLink(item2);

                    feedItemList.add(items);
                    //add title and price to array
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
            LinearLayout linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);

            linlaHeaderProgress.setVisibility(View.GONE);

            adapter = new MyRecyclerAdapter(news_one.this, feedItemList);
            // FILL THE LIST WITH CONTENT JUST GATHERED
            mRecyclerView.setAdapter(adapter);
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
        getMenuInflater().inflate(R.menu.medication, menu);
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
            Intent intent2 = new Intent(news_one.this, MainActivity.class);
            startActivity(intent2);
        } else if (id == R.id.nav_med) {
            Intent intent2 = new Intent(news_one.this, Medication.class);
            startActivity(intent2);

        } else if (id == R.id.vent) {
            Intent intent = new Intent(news_one.this, news_two.class);
            startActivity(intent);
        } else if (id == R.id.nav_sett) {
            Intent intent = new Intent(news_one.this, Settings_new.class);
            startActivity(intent);
        }else if (id == R.id.teleg) {
            Intent intent = new Intent(news_one.this, news_one.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
