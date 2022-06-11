package com.example.keys.aman.app.home;

import static android.accounts.AccountManager.KEY_PASSWORD;
import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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

import java.util.ArrayList;

public class myadaptor extends RecyclerView.Adapter<myadaptor.myviewholder> implements Filterable {
    final ArrayList<addDataHelperClass> dataholder;
    final ArrayList<addDataHelperClass> dataholderfilter;
    final Context context;
    Activity activity;
    AES aes = new AES();
    private SharedPreferences sharedPreferences;

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

        sharedPreferences = context.getSharedPreferences(LogInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
        aes.initFromStrings(sharedPreferences.getString(LogInActivity.AES_KEY, null), sharedPreferences.getString(LogInActivity.AES_IV, null));
        int p = holder.getAdapterPosition();
        final addDataHelperClass temp = dataholder.get(position);
        String current_date, dlogin, dpassword, dwebsite;
        try {
            current_date = dataholder.get(position).getDate();
            dlogin = aes.decrypt(dataholder.get(position).getAddDataLogin());
            dpassword = aes.decrypt(dataholder.get(position).getAddDataPassword());
            holder.dlogin.setText(dlogin);
//            dwebsite = aes.decrypt(temp.getAddWebsite());
            dwebsite = temp.getAddWebsite();
            //String Title = website.substring(0, 1).toUpperCase() + website.substring(1, 2);
            String Title = dwebsite.substring(0, 1).toUpperCase() + dwebsite.substring(1);
            holder.tv_img_title.setText(Title);

            holder.dlogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ShowCardviewDataActivity.class);
                    intent.putExtra("date", current_date);
                    intent.putExtra("loginname", dlogin);
                    intent.putExtra("loginpassowrd", dpassword);
                    intent.putExtra("loginwebsite", dwebsite);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_down, 0);

                }
            });
            holder.LLCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ShowCardviewDataActivity.class);
                    intent.putExtra("date", current_date);
                    intent.putExtra("loginname", dlogin);
                    intent.putExtra("loginpassowrd", dpassword);
                    intent.putExtra("loginwebsite", dwebsite);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_down, 0);
                }
            });
            holder.cardview_more.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.img_copy:
                            SharedPreferences sharedPreferences;
                            sharedPreferences = context.getSharedPreferences(LogInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
                            String dp = dpassword;
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(KEY_PASSWORD, dp);
                            editor.apply();

                            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            dp = sharedPreferences.getString(KEY_PASSWORD, null);
                            ClipData clipData = ClipData.newPlainText("Copy_Password", dp);
                            clipboardManager.setPrimaryClip(clipData);
                            Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show();
                            return true;
                        case R.id.img_delete:
                            HomeActivity.databaseReference.child(dwebsite).child(current_date).removeValue();
                            Toast.makeText(context, "Deleted !!", Toast.LENGTH_SHORT).show();
                            HomeActivity.adaptor.notifyDataSetChanged();
                            activity.finish();
                            activity.overridePendingTransition(0, 0);
                            activity.startActivity(new Intent(context, HomeActivity.class));
                            activity.overridePendingTransition(0, 0);
                            return true;
                    }
                    return false;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
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
                        String a1 = addDataHelperClass.getAddWebsite();
                        if (a1.toLowerCase().contains(filterPattern)) {
                            filteredDataList.add(addDataHelperClass);
                            try {
                                System.out.println("Filtered data: " + addDataHelperClass.getAddWebsite());
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

        final TextView dlogin;
        final TextView tv_img_title;
        final Toolbar cardview_more;
        final LinearLayout LLCard;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            dlogin = itemView.findViewById(R.id.displayname);
            cardview_more = itemView.findViewById(R.id.cardview_more);
            LLCard = itemView.findViewById(R.id.linear_layout_card);
            tv_img_title = itemView.findViewById(R.id.tv_img_title);
        }

    }
}
