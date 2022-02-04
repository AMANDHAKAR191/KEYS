package com.example.keys.baseactivitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keys.AES;
import com.example.keys.PassGenActivity;
import com.example.keys.R;
import com.example.keys.SignUpActivity;
import com.example.keys.addDataHelperClass;
import com.example.keys.addPasswordData;
import com.example.keys.myadaptor;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.util.ArrayList;


public class HomeActivity extends AppCompatActivity {

    ExtendedFloatingActionButton exFABtn;
    FloatingActionButton AddPasswordFab, PasswordGenratorFab, ShowpersonalInfofab;
    TextView welcomename, textView_addpassword, textView_passgen, textView_ShowpersonalInfo;
    Boolean isAllFabsVisible;

    //Shared Preference
    SharedPreferences sharedPreferences;
    SignUpActivity SA = new SignUpActivity();

    private Instant AppRate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sharedPreferences = getSharedPreferences(SignUpActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);

        //Link to id
        welcomename = findViewById(R.id.welcome_name);
        exFABtn = findViewById(R.id.ExtendedFloatingActionButton);
        AddPasswordFab = findViewById(R.id.add_alarm_fab);
        textView_addpassword = findViewById(R.id.tv_add_password);
        PasswordGenratorFab = findViewById(R.id.add_person_fab);
        textView_passgen = findViewById(R.id.tv_pass_gen);
        ShowpersonalInfofab = findViewById(R.id.show_personal_info_fab);
        textView_ShowpersonalInfo = findViewById(R.id.tv_personal_info);
        setfabVisblity();

        //set Welcome name on top of the Home Screen
        AES aes = new AES();
        aes.initFromStrings("CHuO1Fjd8YgJqTyapibFBQ==", "e3IYYJC2hxe24/EO");
        String name = sharedPreferences.getString(SignUpActivity.KEY_USER_NAME, null);
        try {
            welcomename.setText("Hello " + aes.decrypt(name));
        } catch (Exception e) {
            e.printStackTrace();
        }
        home_bottom_nav();

        AddPasswordFab();
        PasswordGenratorFab();
        ShowpersonalInfofab();
        recyclerviewsetdata();

    }

    private void AddPasswordFab() {
        AddPasswordFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(HomeActivity.this, "AddDataActivity", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(HomeActivity.this, addPasswordData.class));
            }
        });
    }

    private void PasswordGenratorFab() {
        PasswordGenratorFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String REQUEST_CODE = "HomeActivity";
                Toast.makeText(HomeActivity.this, "PassGenActivity", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(HomeActivity.this, PassGenActivity.class);
                intent.putExtra("requestCode",REQUEST_CODE);
                startActivity(intent);
            }
        });
    }

    private void ShowpersonalInfofab() {
        ShowpersonalInfofab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HomeActivity.this, "ShowPersonalInfoActivity", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(HomeActivity.this, ShowPersonalInfoActivity.class));
            }
        });
    }

    private void setfabVisblity() {
        isAllFabsVisible = false;

        AddPasswordFab.setVisibility(View.GONE);
        textView_addpassword.setVisibility(View.GONE);
        PasswordGenratorFab.setVisibility(View.GONE);
        textView_passgen.setVisibility(View.GONE);
        ShowpersonalInfofab.setVisibility(View.GONE);
        textView_ShowpersonalInfo.setVisibility(View.GONE);


        exFABtn.extend();
        exFABtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isAllFabsVisible) {
                            AddPasswordFab.show();
                            PasswordGenratorFab.show();
                            ShowpersonalInfofab.show();
                            textView_addpassword.setVisibility(View.VISIBLE);
                            textView_passgen.setVisibility(View.VISIBLE);
                            textView_ShowpersonalInfo.setVisibility(View.VISIBLE);
                            exFABtn.shrink();
                            exFABtn.setIconResource(R.drawable.close);
                            isAllFabsVisible = true;
                        } else {
                            AddPasswordFab.hide();
                            PasswordGenratorFab.hide();
                            ShowpersonalInfofab.hide();
                            textView_addpassword.setVisibility(View.GONE);
                            textView_passgen.setVisibility(View.GONE);
                            textView_ShowpersonalInfo.setVisibility(View.GONE);
                            exFABtn.setIconResource(R.drawable.add);
                            exFABtn.extend();
                            isAllFabsVisible = false;
                        }

                    }
                });
    }

    public void home_bottom_nav() {
        // initialize And Assign Variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        //set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.menu_home);
        //Perform ItemSelectorListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        return true;
                    case R.id.menu_setting:
                        Intent intent1 = new Intent(HomeActivity.this, SettingActivity.class);
                        startActivity(intent1);
                        return true;
                    case R.id.menu_profile:
                        Intent intent2 = new Intent(HomeActivity.this, ProfileActivity.class);
                        startActivity(intent2);
                        return true;
                }
                return false;
            }
        });
    }

    public void recyclerviewsetdata() {
        RecyclerView recyclerView;
        DatabaseReference databaseReference;
        ArrayList<addDataHelperClass> dataholder = new ArrayList<>();


        recyclerView = findViewById(R.id.recview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        String mobile = sharedPreferences.getString(SignUpActivity.KEY_USER_MOBILE, null);
        databaseReference = FirebaseDatabase.getInstance().getReference("addpassworddata")
                .child(mobile);

        myadaptor adaptor = new myadaptor(dataholder,getApplicationContext());
        recyclerView.setAdapter(adaptor);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //Toast.makeText(HomeActivity.this, "Data exists!", Toast.LENGTH_SHORT).show();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                        System.out.println("Parent: " + ds.getKey());
                        for (DataSnapshot ds1 : ds.getChildren()) {
//                            System.out.println(ds.getKey() + " Child: " + ds1.getKey());
                            addDataHelperClass data = ds1.getValue(addDataHelperClass.class);
                            assert data != null;
//                            System.out.println("Data login: " + data.getAddDataLogin());
//                            System.out.println("Data password: " + data.getAddDataPassword());
//                            System.out.println("Data website: " + data.getAddWebsite());
                            dataholder.add(data);
                            //Toast.makeText(HomeActivity.this, "dataholder: " + dataholder.getAddWebsite(), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    //Toast.makeText(HomeActivity.this, "Data does not exists!", Toast.LENGTH_SHORT).show();
                }
            }
            //adaptor.notifyDataSetChanged();
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
