package com.example.haven;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class ProfileEdit extends AppCompatActivity {
    private EditText oname,ophn,oephn,omed;
    private Button odone;
    private FirebaseAuth mauth;
    private DatabaseReference victimdatabase;

    String userid;
    String nname,nphn,nephn,nmed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        oname=(EditText)findViewById(R.id.name);
        ophn=(EditText)findViewById(R.id.phone);
        oephn=(EditText)findViewById(R.id.ephone);
        omed=(EditText)findViewById(R.id.med);
        odone=(Button)findViewById(R.id.done);
        mauth=FirebaseAuth.getInstance();
        userid=mauth.getCurrentUser().getUid();
        victimdatabase= FirebaseDatabase.getInstance().getReference().child("User").child("Profile").child(userid);
        getuserinfo();
        odone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveuserinfo();
                Intent intent=new Intent(ProfileEdit.this,Setting.class);
                startActivity(intent);

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
                        nname=map.get("name").toString();
                        oname.setText(nname);
                    }
                    if(map.get("phone")!=null){
                        nphn=map.get("phone").toString();
                        ophn.setText(nphn);
                    }
                    if(map.get("ephone")!=null){
                        nephn=map.get("ephone").toString();
                        oephn.setText(nephn);
                    }
                    if(map.get("medic")!=null){
                        nmed=map.get("medic").toString();
                        omed.setText(nmed);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void saveuserinfo(){
        nname=oname.getText().toString();
        nphn=ophn.getText().toString();
        nephn=oephn.getText().toString();

        Map userInfo=new HashMap();
        userInfo.put("name",nname);
        userInfo.put("phone",nphn);
        userInfo.put("ephone",nephn);
        userInfo.put("medic",nmed);

        victimdatabase.updateChildren(userInfo);
        finish();
    }
}
