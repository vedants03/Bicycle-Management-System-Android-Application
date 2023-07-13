package com.example.map;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    String[] locations = {"Sitabuldi","Lokmanya Nagar", "Airport","Automotive Square"};
    Double[] latitudes = {21.1417,21.1108,21.0868,21.185760577319027};
    Double[] longitudes = {79.0825,79.0017,79.0635,79.1195721771921};
    SupportMapFragment mapFragment;
    Spinner src;
    Spinner dest;
    String srcSelected,destSelected;
    GoogleMap map;
    Button proceed;
    MarkerOptions srcMarker;
    Marker destMarker;
    int srcIndex, destIndex;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String username;
    ArrayList<LatLng> poly = new ArrayList<LatLng>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        dest = (Spinner)findViewById(R.id.destination_button);
        src = (Spinner) findViewById(R.id.source_button);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,locations);
        src.setAdapter(adapter);
        dest.setAdapter(adapter);
        dest.setSelection(1);
        proceed = (Button)findViewById(R.id.proceed_button);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();


        dest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setMinZoomPreference(11.0f);
        LatLng initial = new LatLng(21.1458,79.0882);
        MarkerOptions initialMarker = new MarkerOptions().position(initial).title("Metro");
        src.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                srcIndex = adapterView.getSelectedItemPosition();
                srcSelected = locations[srcIndex];
                map.clear();
                LatLng latLng = new LatLng(latitudes[srcIndex],longitudes[srcIndex]);
                //Toast.makeText(getApplicationContext(),"Pick-up Location : "+locations[srcIndex],Toast.LENGTH_SHORT).show();
                srcMarker = new MarkerOptions().position(latLng).title("Pick-Up Point");
                map.addMarker(srcMarker);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, map.getMaxZoomLevel()));
                int indexWithMinDistance=2;
                double minDist = 999999.0;
                double dist;
                for (int j = 0; j< 4 && j != srcIndex;j++){
                     dist = (distance(latitudes[srcIndex],longitudes[srcIndex],latitudes[j],longitudes[j]));
                    if (dist < minDist){
                        minDist = dist;
                        indexWithMinDistance = j;
                    }
                }
                //Toast.makeText(getApplicationContext(),"Nearest Location: "+ locations[indexWithMinDistance],Toast.LENGTH_SHORT).show();
                dest.setSelection(indexWithMinDistance);
                destSelected = locations[indexWithMinDistance];
                LatLng latLng1 = new LatLng(latitudes[indexWithMinDistance],longitudes[indexWithMinDistance]);
                MarkerOptions tempMarker = new MarkerOptions().position(latLng1).title("Nearest Drop-Point");
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, map.getMinZoomLevel()));
                map.addMarker(tempMarker);
                //getPolyRoute(latitudes[srcIndex],longitudes[srcIndex],latitudes[indexWithMinDistance],longitudes[indexWithMinDistance]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });



        dest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (destMarker != null){
                    destMarker.remove();
                }
                destIndex = adapterView.getSelectedItemPosition();
                if (destIndex == srcIndex){
                    TextView errorText = (TextView)dest.getSelectedView();
                    errorText.setError("");
                    errorText.setTextColor(Color.RED);//just to highlight that this is an error
                    errorText.setText("Invalid Selection ");//changes the selected item text to this
                    proceed.setEnabled(false);
                }
                else{
                    LatLng latLng2 = new LatLng(latitudes[destIndex],longitudes[destIndex]);
                    Toast.makeText(getApplicationContext(),"Drop Location Selected : "+locations[destIndex],Toast.LENGTH_SHORT).show();
                    destMarker = map.addMarker(new MarkerOptions().position(latLng2).title("Drop Point"));
                    MarkerOptions tempMarker = new MarkerOptions().position(latLng2).title("Nearest Drop-Point");
                    destSelected = locations[destIndex];
                    proceed.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("source",srcSelected);
                bundle.putString("destination",destSelected);
                Intent intent = new Intent(getApplicationContext(),SelectBicycle.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

    }

    public void getRoute(final double lat1, final double lon1, final double lat2, final double lon2){
        final String[] parsedDistance = {""};
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient().newBuilder()
                        .build();
                MediaType mediaType = MediaType.parse("text/plain");
                RequestBody body = RequestBody.create(mediaType, "");
                Request request = new Request.Builder()
                        .url("https://maps.googleapis.com/maps/api/directions/json?destination=lat2,lon2&origin=lat1,lon1&key=AIzaSyBugat9Z4jZu3ctGmJdQsCbvn0lGhDMaRo")
                        .method("GET", null)
                        .addHeader("Accept", "application/json")
                        .build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                JSONObject steps = null;
                JSONObject polyArray;
                JSONArray routesArray;
                try {
                    String jsonData = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonData);
                    routesArray = jsonObject.getJSONArray("routes");
                    JSONObject route = routesArray.getJSONObject(0);
                    polyArray = route.getJSONObject("overview_polyline");
                    String polyPoints = polyArray.getString("points");

                    //Passing the Polyline points from the JSON file I get from the Google Directions URL into a decoder.
                    decodePoly(polyPoints);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private ArrayList<LatLng> decodePoly(String encoded) {

        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((int) (((double) lat / 1E5) * 1E6),
                    (int) (((double) lng / 1E5) * 1E6));
            poly.add(p);
        }

        return poly;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=  getMenuInflater();
        menuInflater.inflate(R.menu.options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout){
            Toast.makeText(getApplicationContext(),"Logging out...",Toast.LENGTH_SHORT).show();
            Intent intent3  =new Intent(getApplicationContext(),LoginActivity.class);
            firebaseAuth.signOut();
            startActivity(intent3);
        }
        return super.onOptionsItemSelected(item);
    }

}