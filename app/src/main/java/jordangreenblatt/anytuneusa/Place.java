package jordangreenblatt.anytuneusa;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * Created by jordynass on 9/2/15.
 */
public class Place {
    protected double longitude, latitude;
    protected Geocoder geocoder;
    public String[] cities;//, roads, neighborhoods, features;

    public Place(Location location, Context context) {

        geocoder = new Geocoder(context);
        longitude = location.getLongitude();
        latitude = location.getLatitude();

        buildPlace();

    }

    public synchronized void buildPlace() {

        HashMap<String, Integer> citiesMap;//, roadsMap, neighborhoodsMap, featuresMap;
        citiesMap = new HashMap<String, Integer>();//roadsMap = neighborhoodsMap = featuresMap = new HashMap<String, Integer>();
        Random random = new Random();

        for (int i = 0; i < 20; i++) {
            double dx = (random.nextDouble() - .5) / 5, dy = (random.nextDouble() - .5) / 5;
            //this is the dx and dy for cities, divide to get other things.


            //small changes in longitude (dy) & latitude (dx)
            //the standard deviation is close to 2.5 km

            try {
                Address address = geocoder.getFromLocation(latitude + dx, longitude + dy, 1).get(0);
                String city = address.getLocality(), neighborhood = address.getSubLocality(),
                        feature = address.getFeatureName(), road = address.getThoroughfare();//extractFirstWord(address.getAddressLine(0));

                if (citiesMap.containsKey(city)) citiesMap.put(city, citiesMap.get(city) + 1);
                else citiesMap.put(city, 1);

                /*if (roadsMap.containsKey(road)) roadsMap.put(road, roadsMap.get(road) + 1);
                else roadsMap.put(road, 1);

                if (neighborhoodsMap.containsKey(neighborhood)) neighborhoodsMap.put(neighborhood, neighborhoodsMap.get(neighborhood) + 1);
                else neighborhoodsMap.put(neighborhood, 1);

                if (featuresMap.containsKey(feature)) featuresMap.put(feature, featuresMap.get(feature) + 1);
                else featuresMap.put(feature, 1);*/

                citiesMap.remove(null); citiesMap.remove("");
                //roadsMap.remove(null); roadsMap.remove("");
                //neighborhoodsMap.remove(null); neighborhoodsMap.remove("");
                //featuresMap.remove(null); featuresMap.remove("");
            }
            catch (Exception e) {}
        }


        Set<String> citySet = citiesMap.keySet();//, roadSet = roadsMap.keySet(),
                //neighborhoodSet = neighborhoodsMap.keySet(), featureSet = featuresMap.keySet();

        PopularPlace[] citiesPP = new PopularPlace[citySet.size()];//, roadsPP = new PopularPlace[roadSet.size()],
                //neighborhoodsPP = new PopularPlace[neighborhoodSet.size()], featuresPP = new PopularPlace[featureSet.size()];
        //DO I WANT ARRAYS OR LISTS?


        int index = 0;
        for (String s : citySet) {
            citiesPP[index] = new PopularPlace(s, citiesMap.get(s));
            index++;
        }

        /*index = 0;
        for (String s : roadSet) {
            roadsPP[index] = new PopularPlace(s, roadsMap.get(s));
            index++;
        }

        index = 0;
        for (String s : neighborhoodSet) {
            neighborhoodsPP[index] = new PopularPlace(s, neighborhoodsMap.get(s));
            index++;
        }

        index = 0;
        for (String s : featureSet) {
            featuresPP[index] = new PopularPlace(s, featuresMap.get(s));
            index++;
        }*/

        Arrays.sort(citiesPP);// Arrays.sort(roadsPP); Arrays.sort(neighborhoodsPP); Arrays.sort(featuresPP);
        cities = new String[citiesPP.length];// roads = new String[roadsPP.length];
        //neighborhoods = new String[neighborhoodsPP.length]; features = new String[featuresPP.length];

        for (int i = 0; i < cities.length; i++) cities[i] = citiesPP[i].name;
        //for (int i = 0; i < roads.length; i++) roads[i] = roadsPP[i].name;
        //for (int i = 0; i < neighborhoods.length; i++) neighborhoods[i] = neighborhoodsPP[i].name;
        //for (int i = 0; i < features.length; i++) features[i] = featuresPP[i].name;



    }


    public synchronized String[] getCities() { return cities; }
    //public synchronized String[] getRoads() { return roads; }
    //public synchronized String[] getNeighborhoods() { return neighborhoods; }
    //public synchronized String[] getFeatures() { return features; }

    public class PopularPlace implements Comparable<PopularPlace> {//popularity is quantified backwards to simplify sorting (so that the most popular places come first in the array)
        String name;
        Integer popularity;
        PopularPlace(String n, Integer p) { name = n; popularity = p; }

        @Override
        public int compareTo(PopularPlace pp) { return -popularity.compareTo(pp.popularity); }
    }

}
