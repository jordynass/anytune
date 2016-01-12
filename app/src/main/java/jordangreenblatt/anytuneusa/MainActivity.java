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
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
    ArrayList<String> titleCatalog;


    int debugIndex = 0;


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
        for (String city: cities) Log.d("", "City: " + city);
        //Log.d("", "TOP 3 CITIES: " + cities[0] + " " + cities[1] + " " + cities[2]);


        //Log.d(playlistLength.toString(), "Before PBE, length should be " + playlistLength.toString());

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

    public synchronized void addInputLines(String urlString, int titleCount) {
        //List<String> allInputLines = new ArrayList<String>(100);
        //urlString = "http://papyrus.math.ucla.edu/seminars/show_quarter.php?t=1442817465&type=Analysis%20and%20PDE&id=&tba=";
        debugIndex++;
        Log.d("", "addInputLines called " + debugIndex);
        BufferedReader bufferedReader = null;
        HttpURLConnection httpURLConnection = null;
        try {
            Log.d("", "entered try statement " + debugIndex);

            //CookieHandler.setDefault(new CookieManager(null, CookiePolicy.ACCEPT_NONE));

            //urlString = "http://www.math.ucla.edu/~jsg66/songtesttext.txt";
            URL url = new URL(urlString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            Log.d("", "finished connecting methods " + debugIndex);

            InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());

            Log.d("", "got inputStream " + debugIndex);

            bufferedReader = new BufferedReader(inputStreamReader);

            Log.d("", "built bufferedReader " + debugIndex);

            String currentLine = bufferedReader.readLine();

            int currentTitleCount = 0;
            while (currentTitleCount < titleCount && currentLine != null) {
                if (currentLine.contains("<Song>")) {
                    String song = TagExtractor.extractTag(currentLine, "Song");
                    String simple = Song.simpleTitle(song);
                    if (!simpleTitles.contains(simple)) {
                        titleCatalog.add(song);
                        simpleTitles.add(simple);
                        currentTitleCount++;
                    }
                }
                currentLine = bufferedReader.readLine();


                //allInputLines.add(currentLine);
                //currentLine = bufferedReaders[i].readLine();
            }
        } catch (IOException ioe) {
            Log.d("", ioe.toString());
            text.setText("Database connection unavailable");
        } finally {
            try {
                bufferedReader.close();
            } catch (Exception e) {}
            try {
                httpURLConnection.disconnect();
            } catch (Exception e) {}
        }
    }

    class PlaylistBuilderEnclosed extends AsyncTask<Integer, Void, String[]> {

        //ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //pd = new ProgressDialog(MainActivity.this);
        }

        @Override
        protected String[] doInBackground(Integer... playlistLengths) {
            //try {
                //Log.d("TESTAAAA", cities.length + "TESTAAAA " + playlistLengths[0].intValue());

                int cityCount = cities.length;
                titleCatalog = new ArrayList<String>(cityCount * (cityCount + maxLength));

                maxLength = playlistLengths[0].intValue();
                String[] urlFriendlyCities = new String[cityCount];

                //Log.d("TESTB", "TESTB");

                for (int i = 0; i < cityCount; i++)
                    urlFriendlyCities[i] = cities[i].replace(' ', '+');

                //Log.d(cities[0], cities[0]);
                //Log.d(urlFriendlyCities[0], urlFriendlyCities[0]);
/*
                URL[] chartLyricsTextSearchURLs = new URL[cityCount];//if I change the length to 0, I should get back the "Just LA" case.
                HttpURLConnection[] chartLyricsTextSearchURLConnections = new HttpURLConnection[cityCount];
                InputStreamReader[] inputStreamReaders = new InputStreamReader[cityCount];
                BufferedReader[] bufferedReaders = new BufferedReader[cityCount];*/
                //String[] allInputLines = new String[cityCount * (cityCount + 1) / 2];//first index is line number, second index is city number

                //int index = 0;

                for (int i = 0; i < cityCount; i++) //{
                    addInputLines("http://api.chartlyrics.com/apiv1.asmx/SearchLyricText?lyricText=" + urlFriendlyCities[i], 4 * (cityCount - i) + maxLength);




                    /*Log.d("", "Start connection loop " + i);
                    chartLyricsTextSearchURLs[i] = new URL("http://api.chartlyrics.com/apiv1.asmx/SearchLyricText?lyricText=" + urlFriendlyCities[i]);
                    Log.d("","a");
                    chartLyricsTextSearchURLConnections[i] = (HttpURLConnection) chartLyricsTextSearchURLs[i].openConnection();
                    Log.d("", "b");
                    //chartLyricsTextSearchURLConnections[i].setRequestProperty("connection", "close");
                    //System.setProperty("http.keepAlive", "true");
                    chartLyricsTextSearchURLConnections[i].connect();
                    Log.d("", "c");
                    try { inputStreamReaders[i] = new InputStreamReader(chartLyricsTextSearchURLConnections[i].getInputStream()); }
                    catch (IOException ioe) { Log.d("", "HERE'S THE EXCEPTION: " + ioe.toString()); }
                    Log.d("","d");
                    bufferedReaders[i] = new BufferedReader(inputStreamReaders[i]);
                    Log.d("","e");
                    //List<String> lineList = new ArrayList<String>();
                    String currentLine = bufferedReaders[i].readLine();
                    Log.d("","f");
                    for (int j = 0; j < 4 * (cityCount - i) + maxLength && currentLine != null; j++) {
                        allInputLines.add(currentLine);
                        currentLine = bufferedReaders[i].readLine();
                    }
                    Log.d("","g");
                    bufferedReaders[i].close();
                    chartLyricsTextSearchURLConnections[i].disconnect();
                    Log.d("", "End connection loop " + i);
                    *//////////////////////////////////////////////////////////////////////////////////////////////////////////////

                    //Thread.sleep(10000);
                //}


                //InputStreamReader inputStreamReader = new InputStreamReader(inputStream);


                //        new InputStreamReader(chartLyricsTextSearchURLConnection.getInputStream());
                //Log.d("1", "c");
                //in = new BufferedReader(inputStreamReader);
                //Log.d("1", "d");

                //Log.d("1", "4");

                Charset utf8 = StandardCharsets.UTF_8;

                //titleCatalog = new ArrayList<String>(allInputLines.size() / 13); //this accounts for the format of Chartlyrics's xml documents
                //String[] titles = new String[maxLength];
                //String inputLine;
                //int index = 0;

                /*for (int i = 0; i < allInputLines.size(); i++) {
                    String currentLine = allInputLines.get(i);
                    if (currentLine.contains("<Song>")) {
                        String song = TagExtractor.extractTag(currentLine, "Song");
                        String simple = Song.simpleTitle(song);
                        if (!simpleTitles.contains(simple)) {
                            //titles[index] = song;
                            allTitles.add(song);
                            simpleTitles.add(simple);
                            //index++;
                        }
                    }
                }*/

                String[] titles = new String[maxLength];
                FastRemovalList titleFRL = new FastRemovalList(titleCatalog);

                for (int i = 0; i < maxLength && titleFRL.getSize() > 0; i++) titles[i] = titleFRL.remove();

                return titles;
                /*
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
                //in.close();
                Log.d("" + titles.length, "" + titles.length);*/
            //} catch (Exception e) {
              //  return new String[]{"Exception from PlaylistBuilderEnclosed: " + e};
            //}
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