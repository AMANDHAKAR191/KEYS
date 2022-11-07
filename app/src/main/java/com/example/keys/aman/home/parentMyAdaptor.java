package com.example.keys.aman.home;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keys.R;
import com.example.keys.aman.AES;
import com.example.keys.aman.home.addpassword.AddPasswordDataHelperClass;
import com.example.keys.aman.signin_login.LogInActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;

public class parentMyAdaptor extends RecyclerView.Adapter<parentMyAdaptor.myviewholder>{
    ArrayList<AddPasswordDataHelperClass> dataholder;
    final ArrayList<String> parentDataHolder;
    public static int passwordCounter = 0;
    final Context context;
    Activity activity;
    AES aes = new AES();
    public static Bitmap bmWebsiteLogo;
    private Bitmap emptyBitmap;
    public myadaptor myadaptor;
    boolean isPasswordHidden = false;
    LogInActivity logInActivity = new LogInActivity();

    public parentMyAdaptor(ArrayList<String> parentDataHolder, Context context, Activity activity) {
        this.parentDataHolder = parentDataHolder;
        this.context = context;
        this.activity = activity;
    }


    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parent_cardview_layout, parent, false);

        return new myviewholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        myAdaptorThreadRunnable threadRunnable = new myAdaptorThreadRunnable(position, holder);
        new Thread(threadRunnable).start();
    }

    public class myAdaptorThreadRunnable implements Runnable {

        private final int position;
        myviewholder holder;
        String dwebsite_link, Title;
        String[] title1;
        String dwebsite_name;

        public myAdaptorThreadRunnable(int position1, myviewholder holder1) {
            this.position = position1;
            this.holder = holder1;
        }

        Handler handler = new Handler();

        @Override
        public void run() {
            SharedPreferences sharedPreferences = context.getSharedPreferences(logInActivity.getSHARED_PREF_ALL_DATA(), MODE_PRIVATE);
            aes.initFromStrings(sharedPreferences.getString(logInActivity.getAES_KEY(), null), sharedPreferences.getString(logInActivity.getAES_IV(), null));
            int p = holder.getAdapterPosition();
            final String temp = parentDataHolder.get(position);

            try {
                dwebsite_name = temp;
                Title = dwebsite_name.substring(0, 1).toUpperCase() + dwebsite_name.substring(1);
                title1 = Title.split("_", 3);
            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.post(new Runnable() {
                @Override
                public void run() {
//                    Toast.makeText(context, "temp: " + temp, Toast.LENGTH_SHORT).show();

                    HomeFragment.databaseReference.child(temp).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
//                                System.out.println("dataSnapshot: " + dataSnapshot);
                                dataholder = new ArrayList<>();
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                                    System.out.println("dataSnapshot.getChildren(): " + ds);
                                    AddPasswordDataHelperClass data = ds.getValue(AddPasswordDataHelperClass.class);
//                                    System.out.println("data: " + data);
//                                    System.out.println("data value: " + data.getAddWebsite_name());
                                    dataholder.add(data);

                                }

                                holder.childRecView.setLayoutManager(new LinearLayoutManager(context));
                                myadaptor = new myadaptor(dataholder, context, activity){
                                    @Override
                                    public void resetAdaptor() {
                                        dataholder.clear();
                                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void showCardViewFragment(String currentDate, String tempLogin, String tempPassword,
                                                                     String dWebsiteName, String dWebsiteLink) {

                                        showCardViewFragment1(currentDate, tempLogin, tempPassword, dWebsiteName, dWebsiteLink);
                                    }
                                };
                                holder.childRecView.setAdapter(myadaptor);
                                Collections.sort(dataholder, AddPasswordDataHelperClass.addDataHelperClassComparator);
                                myadaptor.notifyDataSetChanged();

                            } else {
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
            handler.post(new Runnable() {
                @Override
                public void run() {

                    holder.tvImageTitle.setVisibility(View.VISIBLE);
                    if (title1.length == 3) {
                        holder.tvImageTitle.setText(title1[1].toUpperCase());
                    } else if (title1.length == 2) {
                        holder.tvImageTitle.setText(title1[0].toUpperCase());
                    } else if (title1.length == 1) {
                        holder.tvImageTitle.setText(title1[0].toUpperCase());
                    }

                    holder.tvImageTitle.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (isPasswordHidden){
                                holder.childRecView.setVisibility(View.VISIBLE);
                                isPasswordHidden = false;
                            }else {
                                holder.childRecView.setVisibility(View.GONE);
                                isPasswordHidden = true;
                            }
                        }
                    });
                }
            });
        }
    }

//    public static AddPasswordDataHelperClass dataHolderForAutofill(int index) {
//        return dataholder.get(index);
//    }

    private static Bitmap fetchFavicon(Uri uri) {
        final Uri iconUri = uri.buildUpon().path("favicon.ico").build();

        InputStream is = null;
        BufferedInputStream bis = null;
        try {
            URLConnection conn = new URL(iconUri.toString()).openConnection();
            conn.connect();
            is = conn.getInputStream();
            bis = new BufferedInputStream(is, 8192);
            return BitmapFactory.decodeStream(bis);
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public int getItemCount() {
        return parentDataHolder.size();
    }


    public class myviewholder extends RecyclerView.ViewHolder {

        final TextView tvImageTitle;
        final LinearLayout LLCard;
        RecyclerView childRecView;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            LLCard = itemView.findViewById(R.id.linear_layout_card);
            tvImageTitle = itemView.findViewById(R.id.tv_img_title);
            childRecView = itemView.findViewById(R.id.child_recview);
        }

        public void resetAdaptorCall() {
            resetAdaptor();
        }

        public void showCardViewFragmentCall() {

        }


    }

    //create new abstract method
    public void showCardViewFragment1(String currentDate, String tempLogin, String tempPassword,
                                     String dWebsiteName, String dWebsiteLink) {
    }

    public void resetAdaptor() {
    }
}

