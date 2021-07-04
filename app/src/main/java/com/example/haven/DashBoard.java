package com.example.haven;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DashBoard extends Activity implements LocationListener {
    private Button settingbtn;
    private ImageButton helpbtn;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0 ;

    protected LocationManager locationManager;
    private FirebaseAuth mauth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private DatabaseReference request;
    public String latitude ="16.003056",longitude="73.691733";
    private TextView t1,t2,ad;
    public String address;
    String userid;
    String nName,nphone,nage;
    String phoneNo;
    String message;
    private DatabaseReference victimdatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        mauth=FirebaseAuth.getInstance();
        helpbtn=(ImageButton)findViewById(R.id.hlpbtn);
        settingbtn=(Button)findViewById(R.id.setting);
        mauth=FirebaseAuth.getInstance();
        userid=mauth.getCurrentUser().getUid();
        victimdatabase= FirebaseDatabase.getInstance().getReference().child("User").child("Profile").child(userid);
        getd();

        String user_id = mauth.getCurrentUser().getUid();
        CheckPermission();
        settingbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(DashBoard.this,Setting.class);
                startActivity(intent);

            }
        });

            //Replace text
    }
    @Override
    public void onResume() {
        super.onResume();
        getLocation();
    }
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }
    public void getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    public void CheckPermission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        t1 = (TextView) findViewById(R.id.longit);
        t2 = (TextView) findViewById(R.id.lattitude);
        longitude = String.valueOf(location.getLongitude());
        latitude = String.valueOf(location.getLatitude());

        t1.setText("Longitude:"+ longitude);
        t2.setText("Latitude:"+ latitude);
        ad=(TextView)findViewById(R.id.address);
        double latitudeq=Double.parseDouble(latitude);
        double longitudeq=Double.parseDouble(longitude);
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latitudeq,longitudeq,1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            ad.setText("Address is :-"+address);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("Latitude","status");
    }
    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider!" + provider,
                Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onProviderDisabled(String provider) {
    }
    private void raiserequest(){
        victimdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()&&dataSnapshot.getChildrenCount()>0){
                    Map<String,Object> map=(Map<String, Object>)dataSnapshot.getValue();
                    if(map.get("name")!=null){
                        nName=map.get("name").toString();

                    }
                    if(map.get("phone")!=null){
                        nphone=map.get("phone").toString();

                    }
                    if(map.get("ephone")!=null){
                        nage=map.get("ephone").toString();

                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
       String name1=nName;
        String phoneNo = nage;
        String sms = ""+name1+" needs help at http://maps.google.com/maps?saddr="+latitude+","+longitude+" Pls reach as fast as posssible her address  ";

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, sms, null, null);
            Toast.makeText(getApplicationContext(), "SMS Sent!",
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    "SMS faild, please try again later!",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        Map userInfo=new HashMap();
        userInfo.put("Latitude",latitude);
        userInfo.put("longitude",longitude);
        userInfo.put("Address",address);
        request.updateChildren(userInfo);
        finish();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }
    private void getd()
    {
        victimdatabase.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()&&dataSnapshot.getChildrenCount()>0){
                Map<String,Object> map=(Map<String, Object>)dataSnapshot.getValue();
                if(map.get("name")!=null){
                    nName=map.get("name").toString();

                }
                if(map.get("phone")!=null){
                    nphone=map.get("phone").toString();

                }
                if(map.get("ephone")!=null){
                    nage=map.get("ephone").toString();

                }

            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
    }
}
