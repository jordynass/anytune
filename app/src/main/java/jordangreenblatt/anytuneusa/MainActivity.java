package jordangreenblatt.anytuneusa;


import android.app.ProgressDialog;
import android.content.Context;
import android.location.*;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;


public class MainActivity extends ActionBarActivity implements ConnectionCallbacks, OnConnectionFailedListener {
    EditText editText;// = (EditText) findViewById(R.id.city_name);
    TextView text;
    Button b;
    Context context;
    HashSet<String> simpleTitles = new HashSet<String>();
    GoogleApiClient mGoogleApiClient;
    Geocoder geocoder;
    String[] cities;
    //Place currentPlace;
    Location currentLocation = null;
    int maxLength;
    Integer playlistLength = 0;

    //Button b = (Button) findViewById(R.id.button);
    //Object o = findViewById(R.id.city_name);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = (EditText) findViewById(R.id.playlist_length);
        text = (TextView) findViewById(R.id.message);
        context = getApplicationContext();
        geocoder = new Geocoder(context);
        b = (Button) findViewById(R.id.button);
        b.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {////put this somewhere after connection
                try {
                    buildGoogleApiClient();
                    mGoogleApiClient.connect();
                    try {
                        playlistLength = Integer.parseInt(editText.getText().toString());
                    }
                    catch (NumberFormatException nfe) {
                        Toast.makeText(context, "Count with numbers!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    text.setText("Location unavailable");
                }
            }
        });

        //buildGoogleApiClient();

        /*mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();*/
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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

    @Override
    protected void onStart() {
        super.onStart();
        //mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Place currentPlace = new Place(currentLocation, context);
        cities = currentPlace.getCities();
        Log.d(playlistLength.toString(), "Before PBE, length should be " + playlistLength.toString());
        new PlaylistBuilderEnclosed().execute(playlistLength);//, roads = currentPlace.getRoads(),
                //neighborhoods = currentPlace.getNeighborhoods(), features = currentPlace.getFeatures();


        /*String playlist = "";
        for (int i = 0; i < cities.length; i++)
            playlist = playlist + cities[i] + "\n";


        text.setText(playlist);*/

    }

    @Override
    public void onConnectionSuspended(int i) { text.setText("Location unavailable"); }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) { text.setText("Location unavailable"); }


    class PlaylistBuilderEnclosed extends AsyncTask<Integer, Void, String[]> {

        //ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //pd = new ProgressDialog(MainActivity.this);
        }

        @Override
        protected String[] doInBackground(Integer... playlistLengths) {
            try {
                //Log.d("TESTAAAA", cities.length + "TESTAAAA " + playlistLengths[0].intValue());

                maxLength = playlistLengths[0].intValue();
                String[] urlFriendlyCities = new String[cities.length];

                //Log.d("TESTB", "TESTB");

                for (int i = 0; i < urlFriendlyCities.length; i++)
                    urlFriendlyCities[i] = cities[i].replace(' ', '+');

                //Log.d(cities[0], cities[0]);
                //Log.d(urlFriendlyCities[0], urlFriendlyCities[0]);

                URL chartLyricsTextSearchURL = new URL("http://api.chartlyrics.com/apiv1.asmx/SearchLyricText?lyricText=" + urlFriendlyCities[0]);
                Log.d("1", "1");
                URLConnection chartLyricsTextSearchURLConnection = chartLyricsTextSearchURL.openConnection();
                Log.d("1", "2");
                chartLyricsTextSearchURLConnection.connect();
                Log.d("1", "3");


                //InputStream inputStream = chartLyricsTextSearchURL.openStream();

                Log.d("1", "a");

                BufferedReader in;
                Log.d("1", "b");
                //InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                InputStreamReader inputStreamReader = new InputStreamReader(chartLyricsTextSearchURLConnection.getInputStream());
                Log.d("1", "c");
                in = new BufferedReader(inputStreamReader);
                Log.d("1", "d");

                Log.d("1", "4");

                Charset utf8 = StandardCharsets.UTF_8;

                String[] titles = new String[maxLength];
                String inputLine;
                int index = 0;

                Log.d("1", "5");

                while ((inputLine = in.readLine()) != null && index < maxLength) {
                    Log.d("1", "index: " + index);
                    if (inputLine.contains("<Song>")) {
                        String song = TagExtractor.extractTag(inputLine, "Song");

                        String simple = Song.simpleTitle(song);
                        if (!simpleTitles.contains(simple)) {
                            titles[index] = song;
                            simpleTitles.add(simple);
                            index++;
                        }
                    }
                }
                Log.d("1", "6");
                in.close();
                Log.d("" + titles.length, "" + titles.length);
                return titles;
            } catch (Exception e) {
                Log.d("exception", "exception");
                return new String[]{"Exception from PlaylistBuilderEnclosed: " + e};
            }
        }

        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            /*if (pd != null) {
                pd.dismiss();
            }*/
            String playlist = "";//use a StringBuilder instead
            for (int i = 0; i < maxLength && result[i] != null; i++)
                playlist = playlist + result[i] + "\n";
            text.setText(playlist);
        }
    }
}