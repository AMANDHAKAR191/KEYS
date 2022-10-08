package com.example.keys.aman.import_data;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.keys.aman.home.addpassword.AddPasswordDataHelperClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ImportData {

    public void importPasswordData(Context context, Activity activity) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            try {
                File file = new File("/sdcard/keys/");
                file.mkdirs();
//                File file = new File(path,"passwordData.csv");
//                Log.d("ImportData","mkdir: " + file.mkdirs());
////                file.mkdir();

                String path = "/sdcard/keys/passwordData.csv";
                CSVWriter csvWriter = new CSVWriter(new FileWriter(path, true));

                //
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("addpassworddata")
                        .child(uid);

                ArrayList<AddPasswordDataHelperClass> passwordDataHolder = new ArrayList<>();

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot ds1 : dataSnapshot.getChildren()) {
                                for (DataSnapshot ds : ds1.getChildren()) {
                                    AddPasswordDataHelperClass data = ds.getValue(AddPasswordDataHelperClass.class);
                                    String[] data1 = new String[]{data.getDate(), data.getAddDataLogin(), data.getAddDataPassword()
                                            , data.getAddWebsite_name(), data.getAddWebsite_link()};
                                    csvWriter.writeNext(data1);
                                    passwordDataHolder.add(data);
                                }
                            }

                        } else {
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                //


            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
