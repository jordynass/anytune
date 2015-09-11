package jordangreenblatt.anytuneusa;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Exception;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


class PlaylistBuilder extends AsyncTask<String, Void, String[]> {

    @Override
    protected String[] doInBackground(String... url) {

        try {

            String urlFriendlyCity = url[0].replace(' ', '+');
            URL chartLyricsTextSearchURL = new URL("http://api.chartlyrics.com/apiv1.asmx/SearchLyricText?lyricText=" + urlFriendlyCity);
            URLConnection chartLyricsTextSearchURLConnection = chartLyricsTextSearchURL.openConnection();
            chartLyricsTextSearchURLConnection.connect();

            BufferedReader in = new BufferedReader(new InputStreamReader(chartLyricsTextSearchURLConnection.getInputStream()));

            Charset utf8 = StandardCharsets.UTF_8;

            /*ProgressDialog pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("loading");
            pd.show();*/

            int maxLength = 10;//To make things simple I'll control the max from here. I can adjust later.
            String[] titles = new String[maxLength];
            String inputLine;
            int index = 0;
            while ((inputLine = in.readLine()) != null && index < maxLength) {
                if (inputLine.contains("<Song>")) {
                    titles[index] = TagExtractor.extractTag(inputLine, "Song");
                    index++;
                }
            }
            in.close();
            return titles;
        }
        catch (Exception e) {
            return new String[]{"Exception from PlaylistBuilder: " + e};
        }
    }






    /*static String[] buildPlaylist(String city, int maxLength, Context context) throws UnknownHostException, MalformedURLException, IOException {



        String urlFriendlyCity = city.replace(' ', '+');
        URL chartLyricsTextSearchURL = new URL("http://api.chartlyrics.com/apiv1.asmx/SearchLyricText?lyricText=" + urlFriendlyCity);
        //URL chartLyricsTextSearchURL = new URL("http://developer.android.com/guide/topics/ui/notifiers/toasts.html");
        URLConnection chartLyricsTextSearchURLConnection = chartLyricsTextSearchURL.openConnection();

        chartLyricsTextSearchURLConnection.connect();     ////EXCEPTION IS HERE. It looks like it can't connect to the website.


        BufferedReader in = new BufferedReader(new InputStreamReader(chartLyricsTextSearchURLConnection.getInputStream()));

        Charset utf8 = StandardCharsets.UTF_8;

        String[] titles = new String[maxLength];
        String inputLine;
        int index = 0;
        while ((inputLine = in.readLine()) != null && index < maxLength)
        {
            if (inputLine.contains("<Song>"))
            {
                titles[index] = TagExtractor.extractTag(inputLine, "Song");
                index++;
            }
        }
        in.close();
        return titles;
    }*/
}