package com.example.aqipredictor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;
import com.google.maps.android.heatmaps.HeatmapTileProvider.Builder;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.heatmaps.Gradient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Result extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap mMap;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    private Dialog instruction,chart,weatherdetail;
    private FloatingActionButton floatingActionButton;
    private BottomAppBar bottomAppBar;
    private LocationManager locationManager;
    private static final int REQUEST_LOCATION = 1;
    private ScrollView mScrollView;
    private ArrayList<LatLng> helplist;
    private Intent intent;
    private ImageView userimage;
    Double lat,longi;
    private String feelsv,pressurev,sunrisev,sunsetv,temperaturev,humidv,main,mintemp,maxtemp,temp,icon;
    private String result,cityName="";
    private TextView userresult,weather,weather2,range;
    private Gradient redgradient,greengradient,bluegradient;
    private DatabaseReference myRef;
    private ImageView recommendation1;
    public TextView recommendation11;
    public TextView recommendation12;
    private ImageView recommendation2;
    private ImageView recommendation3;
    private ImageView recommendation4,weatherimage;
    private LinearLayout recommendationtext1;
    private LinearLayout recommendationtext2;
    private LinearLayout health1;
    private LinearLayout health2;
    public DecimalFormat df;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        intent = getIntent();
        result = intent.getStringExtra("result");

//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);

//        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
//        ((com.example.aqipredictor.WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).setListener(new com.example.aqipredictor.WorkaroundMapFragment.OnTouchListener() {
//            public void onTouch() {
//                Result.this.mScrollView.requestDisallowInterceptTouchEvent(true);
//            }
//        });

        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).setListener(new WorkaroundMapFragment.OnTouchListener() {
            public void onTouch() {
                Result.this.mScrollView.requestDisallowInterceptTouchEvent(true);
            }
        });

        userimage = (ImageView) findViewById(R.id.userimage);
        userresult = (TextView) findViewById(R.id.resulttext);
        weather = (TextView) findViewById(R.id.weather);
        weather2 = (TextView)findViewById(R.id.weather2);
        userimage.setImageBitmap(Image.bitmap);
        userimage = (ImageView) findViewById(R.id.userimage);
        userresult = (TextView) findViewById(R.id.resulttext);
        weatherimage = (ImageView) findViewById(R.id.weatherimage);
        mScrollView = (ScrollView) findViewById(R.id.scrollview);
        recommendation11 = (TextView) findViewById(R.id.recommendationtext11);
        recommendation12 = (TextView) findViewById(R.id.recommendationtext12);
        range = (TextView) findViewById(R.id.range);
        userimage.setImageBitmap(Image.bitmap);
        health1 = (LinearLayout) findViewById(R.id.health_1);
        health2 = (LinearLayout) findViewById(R.id.health_2);
        recommendation1 = (ImageView) findViewById(R.id.recommendation1);
        recommendation2 = (ImageView) findViewById(R.id.recommendation2);
        recommendation3 = (ImageView) findViewById(R.id.recommendation3);
        recommendation4 = (ImageView) findViewById(R.id.recommendation4);
        recommendationtext1 = (LinearLayout) findViewById(R.id.recommendationtext1);
        recommendationtext2 = (LinearLayout) findViewById(R.id.recommendationtext2);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        helplist = new ArrayList<>();
        myRef = FirebaseDatabase.getInstance().getReference().child("wedapp-266618");
        df = new DecimalFormat("###.#");



        instruction = new Dialog(this);
        chart = new Dialog(this);
        weatherdetail = new Dialog(this);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        toolbar =  (Toolbar) findViewById(R.id.bottomAppBar);


        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

        result = "[1]";
        if (result.equals("[0]")) {
            average();
            this.recommendation1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    recommendation11.setText(" Sensitive Group Wear Masks");
                }
            });
            this.recommendation2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    recommendation11.setText(" Close your window to avoid dirty outdoor air");
                }
            });
        } else if (result.equals("[1]")) {
            bad();
            this.recommendation1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    recommendation11.setText(" Sensitive and General Group Both Wear Masks");
                }
            });
            this.recommendation2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    recommendation11.setText(" Sensitive and General Group Both Close Window");
                }
            });
            this.recommendation3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    recommendation12.setText(" Sensitive and General Group Both Avoid Unnecessary Outdoor Activities");
                }
            });
            this.recommendation4.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Result.this.recommendation12.setText(" Run Air Purifier ");
                }
            });
        } else if (result.equals("[2]")) {
            good();
            this.recommendation1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Result.this.recommendation11.setText(" Open your windows and enjoy fresh air ");
                }
            });
            this.recommendation2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    recommendation11.setText(" Enjoy outdoor activities ");
                }
            });
        }else if (result.equals("NO")) {
            Toast.makeText(this,"Please Read Instructions Carefully and Upload Pic again:(",Toast.LENGTH_LONG).show();
            intent = new Intent(this,MainActivity.class);
            MainActivity.ins = 0;
            startActivity(intent);
        } else {
            Toast.makeText(this, "Some error occurs and Upload Pic again :(", Toast.LENGTH_SHORT).show();
            intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(Double.valueOf(Image.latitude),Double.valueOf(Image.longitude) , 1);
            cityName = addresses.get(0).getLocality();

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("exception",e.toString());
        }
        if (!cityName.equals("") )
         Log.i("country4",cityName);
        else
            cityName = "Lahore";
        DownloadTask task = new DownloadTask();
        String key = "e9a5439372509a73181c4b6e44ad3fd1";
        task.execute("https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid="+ key);

        range.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showrange();
            }
        });

        final Intent intentimage = new Intent(this,Image.class);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(intentimage);
            }
        });
    }

    public void showWeather(View view){
        TextView close,humidity,pressure,feel,sunrise,sunset ;

        weatherdetail.setCancelable(false);
        weatherdetail.setContentView(R.layout.weatherdetail);
        close = weatherdetail.findViewById(R.id.textview1);
        humidity = weatherdetail.findViewById(R.id.textView12);
        pressure = weatherdetail.findViewById(R.id.textView6);
        feel = weatherdetail.findViewById(R.id.textView58);
        sunrise = weatherdetail.findViewById(R.id.textView9);
        sunset = weatherdetail.findViewById(R.id.textView11);
        String fee = df.format(Double.valueOf(feelsv));

        humidity.setText(humidv+" %");
        pressure.setText(pressurev + "MB");
        feel.setText(fee +" ℃");
        sunrise.setText(sunrisev + " AM");
        sunset.setText(sunsetv + " PM");
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                weatherdetail.dismiss();
            }
        });
        weatherdetail.show();
    }

    public void showrange(){
        TextView close ;
        chart.setCancelable(false);
        chart.setContentView(R.layout.aqichart);
        close = chart.findViewById(R.id.textview30);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chart.dismiss();
            }
        });
        chart.show();
    }

    public void showinstruction(){
        Button close;
        instruction.setCancelable(false);
        instruction.setContentView(R.layout.instructionspopup);
        close = instruction.findViewById(R.id.button);
        instruction.show();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instruction.dismiss();
            }
        });
    }

    public void good() {
        this.userresult.setText("GOOD");
        ViewGroup.LayoutParams params = this.health2.getLayoutParams();
        params.height = 0;
        this.health2.setLayoutParams(params);
        ViewGroup.LayoutParams params2 = this.recommendationtext2.getLayoutParams();
        params2.height = 0;
        this.recommendationtext2.setLayoutParams(params2);
        this.recommendation1.setBackgroundResource(R.drawable.window);
        this.recommendation2.setBackgroundResource(R.drawable.cycle);
        this.recommendation11.setText(" Open your windows and enjoy fresh air ");
    }

    public void average() {
        this.userresult.setText("average");
        ViewGroup.LayoutParams params = this.health2.getLayoutParams();
        params.height = 0;
        this.health2.setLayoutParams(params);
        ViewGroup.LayoutParams params2 = this.recommendationtext2.getLayoutParams();
        params2.height = 0;
        this.recommendationtext2.setLayoutParams(params2);
        this.recommendation1.setBackgroundResource(R.drawable.mask);
        this.recommendation2.setBackgroundResource(R.drawable.nowindow);
        this.recommendation11.setText(" Sensitive Group Wear Masks ");
    }

    public void bad() {
        this.userresult.setText("BAD");
        this.recommendation1.setBackgroundResource(R.drawable.mask);
        this.recommendation2.setBackgroundResource(R.drawable.nowindow);
        this.recommendation3.setBackgroundResource(R.drawable.nocycle);
        this.recommendation4.setBackgroundResource(R.drawable.wind);
        this.recommendation11.setText(" Sensitive and General Group Both Wear Masks ");
        this.recommendation12.setText(" Sensitive and General Group Both Avoid Unnecessary Outdoor Activities ");
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        /*this.mMap = googleMap;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        this.mMap.getUiSettings().setAllGesturesEnabled(true);
        this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(31.5204d, 74.3587d), 12.0f));
        int[] green = {Color.rgb(0, 255, 0)};
        float[] greenstartPoints = {1.0f};
        int[] yellow = {Color.rgb(0, 0, 255)};
        float[] yellowstartPoints = {1.0f};
        this.redgradient = new Gradient(new int[]{Color.rgb(255, 0, 0)}, new float[]{1.0f});
        this.greengradient = new Gradient(green, greenstartPoints);
        this.bluegradient = new Gradient(yellow, yellowstartPoints);
        this.myRef.addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String latitude = (String) dataSnapshot.child("latitude").getValue(String.class);
                String longtitude = (String) dataSnapshot.child("longitude").getValue(String.class);
                StringBuilder sb = new StringBuilder();
                sb.append(latitude);
                sb.append(longtitude);
                Log.i("values", sb.toString());
                Result.this.helplist.add(new LatLng(Double.valueOf(latitude).doubleValue(), Double.valueOf(longtitude).doubleValue()));
                String str = "result";
                if (((String) dataSnapshot.child(str).getValue(String.class)).equals("0")) {
                    mMap.addTileOverlay(new TileOverlayOptions().tileProvider(new Builder()
                            .data(Result.this.helplist)
                            .radius(34)
                            .gradient(redgradient)
                            .build()));
                } else if (((String) dataSnapshot.child(str).getValue(String.class)).equals("1")) {
                    Result.this.mMap.addTileOverlay(new TileOverlayOptions().tileProvider(new Builder()
                            .data(helplist)
                            .radius(34)
                            .gradient(greengradient)
                            .build()));
                } else if (((String) dataSnapshot.child(str).getValue(String.class)).equals("2")) {
                    Result.this.mMap.addTileOverlay(new TileOverlayOptions().tileProvider(new Builder()
                            .data(helplist)
                            .radius(34)
                            .gradient(bluegradient)
                            .build()));
                }
                String str2 = "waqas";
                Log.i(str2, String.valueOf(Result.this.helplist));
                Result.this.helplist.clear();
                Log.i(str2, String.valueOf(Result.this.helplist));
            }

            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            public void onCancelled(DatabaseError databaseError) {
                Log.i("waqas", String.valueOf(databaseError));
            }
        });
        */
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId() ){
            case R.id.nav_home:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.nav_instruction:
                showinstruction();
                break;
            case R.id.guide:
                showrange();
                break;
            case R.id.rate:
                break;
            case R.id.exit:
                Intent intentend = new Intent(Intent.ACTION_MAIN);
                intentend.addCategory(Intent.CATEGORY_HOME);
                //intentend.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intentend);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else {
            super.onBackPressed();
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }

    }

    public class DownloadTask extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... urls) {
            String result="";
            URL url;
            HttpURLConnection urlConnection=null;
            try{
                url=new URL(urls[0]);
                urlConnection=(HttpURLConnection) url.openConnection();
                InputStream in=urlConnection.getInputStream();
                InputStreamReader reader=new InputStreamReader(in);
                int data=reader.read();

                while(data!=-1){
                    char current=(char) data;
                    result+=current;
                    data=reader.read();

                }

                return result;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                JSONObject parentObject = new JSONObject(s);
                Log.i("Weather content2", String.valueOf(parentObject));
                JSONObject userDetails = parentObject.getJSONObject("main");
                JSONObject sysDetails = parentObject.getJSONObject("sys");
                JSONArray weatherDetails = parentObject.getJSONArray("weather");

                for (int i=0 ; i<weatherDetails.length() ; i++){
                    JSONObject maina = weatherDetails.getJSONObject(i);
                    main = maina.getString("main");
                    icon = maina.getString("icon");
                }

                mintemp = userDetails.getString("temp_min");
                maxtemp = userDetails.getString("temp_max");
                temp = userDetails.getString("temp");

                double tempva,mintempv,maxtempv;
                tempva = Double.valueOf(temp) - 275.15 ;
                mintempv = Double.valueOf(mintemp) - 275.15 ;
                maxtempv = Double.valueOf(maxtemp) - 275.15 ;

                String tempv = df.format(tempva);
                String mint = df.format(mintempv);
                String maxt = df.format(maxtempv);

                weather.setText( tempv +"℃");
                weather2.setText(main + "\n"  +mint + "/" + maxt +"℃");
                temperaturev = userDetails.getString("temp");
                humidv = userDetails.getString("humidity") ;
                feelsv = userDetails.getString("feels_like");
                pressurev = userDetails.getString("pressure");
                sunrisev =  sysDetails.getString("sunrise");
                sunsetv = sysDetails.getString("sunset");

                String iconurl = "http://openweathermap.org/img/w/" + icon + ".png";

                loadImage(weatherimage,iconurl);
                //Picasso.get().load(iconurl).into(weatherimage);

            } catch (Exception e) {
                e.printStackTrace();
                Log.i("weather2",e.toString());
                Toast.makeText(Result.this, "Some Exception occured ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadImage(final ImageView imageView, final String imageUrl){
        Picasso.get()
                .load(imageUrl)
                .into(imageView , new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError(Exception e) {
                        String updatedImageUrl;
                        if (imageUrl.contains("https")){
                            updatedImageUrl = imageUrl.replace("https", "http");
                        }else{
                            updatedImageUrl = imageUrl.replace("http", "https");
                        }
                        loadImage(imageView, updatedImageUrl);
                    }
                });
    }
}
