package com.example.keys.aman.app.home.addpassword;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keys.R;
import com.example.keys.aman.app.home.ShowCardviewDataActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class websiteListAdaptor extends RecyclerView.Adapter<websiteListAdaptor.myviewholder> {
    final ArrayList<websiteHelper> dataholder;
    final Context context;

    public websiteListAdaptor(ArrayList<websiteHelper> dataholder, Context context) {
        this.dataholder = dataholder;
        this.context = context;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.website_card, parent, false);
        return new myviewholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {

        try {
            holder.web_img.setImageResource(R.drawable.add);
            String website_name = dataholder.get(position).getWebsite_image_name();
            holder.web_name.setText(website_name);
//            holder.web_img.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(context, addPasswordData.class);
//                    String REQUEST_CODE = "from_website_adaptor";
//                    intent.putExtra("request_code",REQUEST_CODE);
//                    intent.putExtra("loginwebsite",website_name);
//                    context.startActivity(intent);
//                }
//            });
//            holder.LLCard.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    @Override
    public int getItemCount() {
        return dataholder.size();
    }

    public static class myviewholder extends RecyclerView.ViewHolder {

        final ImageView web_img;
        TextView web_name;
        final LinearLayout LLCard;
        TextInputEditText tiet_website;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            web_img = itemView.findViewById(R.id.img_web);
            web_name = itemView.findViewById(R.id.web_name);
            tiet_website = itemView.findViewById(R.id.tiet_addwebsitedata);
            LLCard = itemView.findViewById(R.id.lenear_layout_card);
        }

    }
}
