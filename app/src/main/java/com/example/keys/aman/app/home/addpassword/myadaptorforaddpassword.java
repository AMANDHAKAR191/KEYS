package com.example.keys.aman.app.home.addpassword;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keys.R;

import java.util.ArrayList;

public abstract class myadaptorforaddpassword extends RecyclerView.Adapter<myadaptorforaddpassword.myviewholder> {
    private static final String REQUEST_CODE = "myadaptorforaddpassword";
    final ArrayList<websiteHelper> dataholder;
    final Context context;
    Activity activity;
    private SharedPreferences sharedPreferences;

    public myadaptorforaddpassword(ArrayList<websiteHelper> dataholder, Context context, Activity activity) {
        this.dataholder = dataholder;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.website_cardview_layout, parent, false);
        return new myviewholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {

        final websiteHelper temp = dataholder.get(position);
        String dwebsiteLink, dwebsitename;
        try {
            dwebsiteLink = dataholder.get(position).getWebsite_login_url();
            dwebsitename = temp.getWebsite_name();
            String Title = dwebsitename.substring(0,1).toUpperCase() + dwebsitename.substring(1);

//            holder.tv_img_title.setText(Title);
            holder.dname.setText(Title.replace("_","."));


            holder.onWebsiteClick(dwebsiteLink,dwebsitename);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return dataholder.size();
    }



    public class myviewholder extends RecyclerView.ViewHolder {

        final TextView dname;
//        final TextView tv_img_title;
        final LinearLayout LLCard;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            dname = itemView.findViewById(R.id.displayname);
            LLCard = itemView.findViewById(R.id.linear_layout_card);
//            tv_img_title = itemView.findViewById(R.id.tv_img_title);
        }
        public void onWebsiteClick(String dwebiteLink, String dwebsitename){
            LLCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPictureClick(dwebiteLink,dwebsitename);
                }
            });
        }

    }

    //create new abstract method
    public abstract void onPictureClick(String dname, String dwebsite);
}
