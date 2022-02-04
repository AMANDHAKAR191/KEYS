package com.example.keys;

import static android.accounts.AccountManager.KEY_PASSWORD;
import static android.content.Context.MODE_PRIVATE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class myadaptor extends RecyclerView.Adapter<myadaptor.myviewholder> {
    ArrayList<addDataHelperClass> dataholder;
    Context context;

    public myadaptor(ArrayList<addDataHelperClass> dataholder, Context context) {
        this.dataholder = dataholder;
        this.context = context;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_layout, parent, false);
        return new myviewholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        AES aes = new AES();
        aes.initFromStrings("CHuO1Fjd8YgJqTyapibFBQ==", "e3IYYJC2hxe24/EO");
        int p = holder.getAdapterPosition();
        final addDataHelperClass temp = dataholder.get(position);
        String dlogin, dpassword, dwebsite;
        try {
            dlogin = aes.decrypt(dataholder.get(position).getAddDataLogin());
            dpassword = aes.decrypt(dataholder.get(position).getAddDataPassword());
            holder.dlogin.setText(dlogin);
            holder.dpassword.setText(dpassword);
            dwebsite = aes.decrypt(temp.getAddWebsite());
            //String Title = website.substring(0, 1).toUpperCase() + website.substring(1, 2);
            String Title = dwebsite.substring(0,1).toUpperCase() + dwebsite.substring(1);
            holder.tv_img_title.setText(Title);

            holder.dlogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ShowCardviewDataActivity.class);
                    intent.putExtra("loginname", dlogin);
                    intent.putExtra("loginpassowrd", dpassword);
                    intent.putExtra("loginwebsite", dwebsite);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
            holder.dpassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ShowCardviewDataActivity.class);
                    intent.putExtra("loginname", dlogin);
                    intent.putExtra("loginpassowrd", dpassword);
                    intent.putExtra("loginwebsite", dwebsite);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
            holder.LLCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ShowCardviewDataActivity.class);
                    intent.putExtra("loginname", dlogin);
                    intent.putExtra("loginpassowrd", dpassword);
                    intent.putExtra("loginwebsite", dwebsite);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
            holder.img_copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences sharedPreferences;
                    sharedPreferences = context.getSharedPreferences(SignUpActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
                    String dp = dpassword;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(KEY_PASSWORD,dp );
                    editor.apply();

                    ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    dp = sharedPreferences.getString(KEY_PASSWORD, null);
                    ClipData clipData = ClipData.newPlainText("Copy_Password", dp);
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show();
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

    public static class myviewholder extends RecyclerView.ViewHolder {

        TextView dlogin, dpassword, tv_img_title;
        ImageView img_copy;
        LinearLayout LLCard;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            dlogin = (TextView) itemView.findViewById(R.id.displayname);
            dpassword = (TextView) itemView.findViewById(R.id.displaycontact);
            img_copy = (ImageView) itemView.findViewById(R.id.img_copy);
            LLCard = (LinearLayout) itemView.findViewById(R.id.lenear_layout_card);
            tv_img_title = (TextView) itemView.findViewById(R.id.tv_img_title);
        }

    }
}
