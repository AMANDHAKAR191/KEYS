package com.example.keys.aman.app.home;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keys.R;
import com.example.keys.aman.app.AES;
import com.example.keys.aman.app.home.addpassword.addDataHelperClass;
import com.example.keys.aman.app.signin_login.LogInActivity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class myadaptor extends RecyclerView.Adapter<myadaptor.myviewholder> implements Filterable {
    final ArrayList<addDataHelperClass> dataholder;
    final ArrayList<addDataHelperClass> dataholderfilter;
    final Context context;
    Activity activity;
    AES aes = new AES();
    private SharedPreferences sharedPreferences;
    public static Bitmap website_logo1;

    public myadaptor(ArrayList<addDataHelperClass> dataholder, Context context, Activity activity) {
        this.dataholder = dataholder;
        this.context = context;
        this.activity = activity;
        this.dataholderfilter = new ArrayList<>(dataholder);
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_layout, parent, false);

        return new myviewholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        myadaptorThreadRunnable threadRunnable = new myadaptorThreadRunnable(position,holder);
        new Thread(threadRunnable).start();
    }

    public class myadaptorThreadRunnable implements Runnable {

        private int position;
        myviewholder holder;
        String dwebsite_link, Title;
        String[] title1;
        String current_date, dlogin, dpassword, dwebsite_name, temp_dlogin, temp_dpassword;

        public myadaptorThreadRunnable (int position1, myviewholder holder1){
            this.position = position1;
            this.holder = holder1;
        }

        Handler handler = new Handler();
        @Override
        public void run() {
            sharedPreferences = context.getSharedPreferences(LogInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
            aes.initFromStrings(sharedPreferences.getString(LogInActivity.AES_KEY, null), sharedPreferences.getString(LogInActivity.AES_IV, null));
            int p = holder.getAdapterPosition();
            final addDataHelperClass temp = dataholder.get(position);

            try {
                current_date = dataholder.get(position).getDate();
                //Double Decryption
                dlogin = aes.decrypt(dataholder.get(position).getAddDataLogin());
                temp_dlogin = aes.decrypt(dlogin);
                dpassword = aes.decrypt(dataholder.get(position).getAddDataPassword());
                temp_dpassword = aes.decrypt(dpassword);


                dwebsite_name = temp.getAddWebsite_name();
                dwebsite_link = temp.getAddWebsite_link();

                Title = dwebsite_name.substring(0, 1).toUpperCase() + dwebsite_name.substring(1);
                title1 = Title.split("_",3);



            } catch (Exception e) {
                e.printStackTrace();
            }

            website_logo1 = myadaptor.fetchFavicon(Uri.parse(dwebsite_link));
            handler.post(new Runnable() {
                @Override
                public void run() {
                    holder.tv_dlogin.setText(temp_dlogin);
                    if (title1.length == 3){
                        holder.tv_dwebsitename.setText(title1[1]);
                    }else if (title1.length == 2){
                        holder.tv_dwebsitename.setText(title1[0]);
                    }

                    holder.img_logo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, ShowCardviewDataActivity.class);
                            intent.putExtra(LogInActivity.REQUEST_CODE_NAME, "HomeActivity.myadaptor");
                            intent.putExtra("date", current_date);
                            intent.putExtra("loginname", temp_dlogin);
                            intent.putExtra("loginpassowrd", temp_dpassword);
                            intent.putExtra("loginwebsite_name", dwebsite_name);
                            intent.putExtra("loginwebsite_link", dwebsite_link);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            activity.overridePendingTransition(R.anim.slide_in_down, 0);

                        }
                    });
                    holder.tv_dlogin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, ShowCardviewDataActivity.class);
                            intent.putExtra(LogInActivity.REQUEST_CODE_NAME, "HomeActivity.myadaptor");
                            intent.putExtra("date", current_date);
                            intent.putExtra("loginname", temp_dlogin);
                            intent.putExtra("loginpassowrd", temp_dpassword);
                            intent.putExtra("loginwebsite_name", dwebsite_name);
                            intent.putExtra("loginwebsite_link", dwebsite_link);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            activity.overridePendingTransition(R.anim.slide_in_down, 0);

                        }
                    });
                    holder.tv_dwebsitename.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, ShowCardviewDataActivity.class);
                            intent.putExtra(LogInActivity.REQUEST_CODE_NAME, "HomeActivity.myadaptor");
                            intent.putExtra("date", current_date);
                            intent.putExtra("loginname", temp_dlogin);
                            intent.putExtra("loginpassowrd", temp_dpassword);
                            intent.putExtra("loginwebsite_name", dwebsite_name);
                            intent.putExtra("loginwebsite_link", dwebsite_link);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            activity.overridePendingTransition(R.anim.slide_in_down, 0);

                        }
                    });
                    holder.LLCard.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(context, ShowCardviewDataActivity.class);
                            intent.putExtra(LogInActivity.REQUEST_CODE_NAME, "HomeActivity.myadaptor");
                            intent.putExtra("date", current_date);
                            intent.putExtra("loginname", temp_dlogin);
                            intent.putExtra("loginpassowrd", temp_dpassword);
                            intent.putExtra("loginwebsite_name", dwebsite_name);
                            intent.putExtra("loginwebsite_link", dwebsite_link);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            activity.overridePendingTransition(R.anim.slide_in_down, 0);
                        }
                    });
                    holder.tb_cardview_more.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.img_copy_username:

                                    ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clipData = ClipData.newPlainText("Copy_Login", temp_dlogin);
                                    clipboardManager.setPrimaryClip(clipData);
                                    Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show();
                                    return true;

                                case R.id.img_copy_password:
                                    ClipboardManager clipboardManager1 = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clipData1 = ClipData.newPlainText("Copy_Password", temp_dpassword);
                                    clipboardManager1.setPrimaryClip(clipData1);
                                    Toast.makeText(context, "Copied! <> ", Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.img_delete:
                                    HomeActivity.databaseReference.child(dwebsite_name).child(current_date).removeValue();
                                    Toast.makeText(context, "Deleted !!", Toast.LENGTH_SHORT).show();
                                    HomeActivity.adaptor.notifyDataSetChanged();
                                    Intent intent = new Intent(context, HomeActivity.class);
                                    intent.putExtra(LogInActivity.REQUEST_CODE_NAME, "HomeActivity.myadaptor");
                                    activity.startActivity(intent);
                                    activity.finish();
                                    activity.overridePendingTransition(0, 0);
                                    return true;
                            }
                            return false;
                        }
                    });

                    holder.img_logo.setImageBitmap(website_logo1);
                }
            });
        }
    }


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
        return dataholder.size();
    }

    @Override
    public Filter getFilter() {
        return newFilter;
    }

    private final Filter newFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<addDataHelperClass> filteredDataList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredDataList.addAll(dataholder);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (addDataHelperClass addDataHelperClass : dataholder) {
                    try {
                        String a1 = addDataHelperClass.getAddWebsite_name();
                        if (a1.toLowerCase().contains(filterPattern)) {
                            filteredDataList.add(addDataHelperClass);
                            try {
                                System.out.println("Filtered data: " + addDataHelperClass.getAddWebsite_name());
                                System.out.println("Filtered ArrayList : " + filteredDataList);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredDataList;
            filterResults.count = filteredDataList.size();
            System.out.println("filterResults: " + filterResults.values);
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            dataholderfilter.clear();
            dataholderfilter.addAll((ArrayList) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public static class myviewholder extends RecyclerView.ViewHolder {

        final TextView tv_dlogin, tv_dwebsitename;
        final TextView tv_img_title;
        final ImageView img_logo;
        final Toolbar tb_cardview_more;
        final LinearLayout LLCard;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            tv_dlogin = itemView.findViewById(R.id.displayname);
            tv_dwebsitename = itemView.findViewById(R.id.displaywebsite);
            tb_cardview_more = itemView.findViewById(R.id.cardview_more);
            LLCard = itemView.findViewById(R.id.linear_layout_card);
            tv_img_title = itemView.findViewById(R.id.tv_img_title);
            img_logo = itemView.findViewById(R.id.img_logo);
        }

    }
}
