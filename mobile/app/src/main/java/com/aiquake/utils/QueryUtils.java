package com.aiquake.utils;

import android.text.TextUtils;
import android.util.Log;
import com.aiquake.models.EarthquakeEvent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class QueryUtils {
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Private constructor to prevent instantiation.
     */
    private QueryUtils() {
    }

    /**
     * Query the USGS dataset and return a list of {@link EarthquakeEvent} objects.
     */
    public static List<EarthquakeEvent> fetchEarthquakeData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link EarthquakeEvent}s
        List<EarthquakeEvent> earthquakes = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link EarthquakeEvent}s
        return earthquakes;
    }

    /**
     * Returns new URL object from the given URL string.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200), read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why the makeHttpRequest(URL url) method signature specifies than an IOException could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link EarthquakeEvent} objects that has been built up from
     * parsing the JSON response.
     */
    private static List<EarthquakeEvent> extractFeatureFromJson(String earthquakeJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(earthquakeJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding earthquakes to
        List<EarthquakeEvent> earthquakes = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON is formatted, a JSONException exception object will be thrown.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);

            // Extract the "features" JSONArray
            JSONArray earthquakeFeatureArray = baseJsonResponse.getJSONArray("features");

            // For each earthquake in the earthquakeFeatureArray, create an {@link EarthquakeEvent} object
            for (int i = 0; i < earthquakeFeatureArray.length(); i++) {

                // Get a single earthquake at position i
                JSONObject currentEarthquake = earthquakeFeatureArray.getJSONObject(i);

                // For a given earthquake, extract the JSONObject associated with the key called "properties".
                JSONObject properties = currentEarthquake.getJSONObject("properties");

                // Extract the value for the key called "mag"
                double magnitude = properties.getDouble("mag");

                // Extract the value for the key called "place"
                String location = properties.getString("place");

                // Extract the value for the key called "time"
                long time = properties.getLong("time");

                 // Extract the JSONObject associated with the key called "geometry".
                JSONObject geometry = currentEarthquake.getJSONObject("geometry");
                // Extract the JSONArray associated with the key called "coordinates"
                JSONArray coordinates = geometry.getJSONArray("coordinates");
                // Extract longitude, latitude, and depth from the JSONArray
                double longitude = coordinates.getDouble(0);
                double latitude = coordinates.getDouble(1);
                double depth = coordinates.getDouble(2);

                // Create a new {@link EarthquakeEvent} object with the magnitude, location, time, and URL from the JSON response.
                EarthquakeEvent earthquake = new EarthquakeEvent(
                    magnitude,
                    latitude,
                    longitude,
                    depth,
                    location,
                    new Date(time),
                    0.0  // Default confidence level
                );

                // Add the new {@link EarthquakeEvent} to the list of earthquakes.
                earthquakes.add(earthquake);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block, catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of earthquakes
        return earthquakes;
    }
} 