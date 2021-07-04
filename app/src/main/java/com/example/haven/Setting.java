package com.example.haven;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Setting extends Activity {
    private TextView oname,edi;
    private Button btnlogout,backbtn;

    private FirebaseAuth mauth;
    private DatabaseReference victimdatabase;

    String userid;
    String nName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        edi=(TextView)findViewById(R.id.edt);
        oname=(TextView)findViewById(R.id.name);
        backbtn=(Button)findViewById(R.id.bckbtn);
        btnlogout=(Button)findViewById(R.id.button);
        mauth=FirebaseAuth.getInstance();
        userid=mauth.getCurrentUser().getUid();
        victimdatabase= FirebaseDatabase.getInstance().getReference().child("User").child("Profile").child(userid);
        getuserinfo();
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Setting.this,DashBoard.class);
                startActivity(intent);
                finish();
                return;
            }
        });
        edi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Setting.this,ProfileEdit.class);
                startActivity(intent);
                finish();
                return;
            }
        });
        btnlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(Setting.this, MainActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });

    }
    private void getuserinfo(){
        victimdatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()&&dataSnapshot.getChildrenCount()>0){
                    Map<String,Object> map=(Map<String, Object>)dataSnapshot.getValue();
                    if(map.get("name")!=null){
                        nName=map.get("name").toString();
                        oname.setText(nName);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
