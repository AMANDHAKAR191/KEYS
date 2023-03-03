package com.keys.aman.home;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.keys.aman.AES;
import com.keys.aman.R;
import com.keys.aman.home.addpassword.PasswordHelperClass;
import com.keys.aman.messages.ChatActivity;
import com.keys.aman.signin_login.LogInActivity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class SharedPasswordAdapter extends RecyclerView.Adapter<SharedPasswordAdapter.myViewHolder>{

    ArrayList<PasswordHelperClass> dataHolder;
    Context context;
    Activity activity;
    AES aes = new AES();
    public static Bitmap bmWebsiteLogo;
    private Bitmap emptyBitmap;
    LogInActivity logInActivity = new LogInActivity();
    ChatActivity chatActivity = new ChatActivity();
    public static final String REQUEST_ID = "passwordAdaptor";

    public SharedPasswordAdapter(ArrayList<PasswordHelperClass> tempDataHolder, Context context, Activity activity, String commonEncryptionKey, String commonEncryptionIv) {
        this.dataHolder = tempDataHolder;
        this.context = context;
        this.activity = activity;
        aes.initFromStrings(commonEncryptionKey, commonEncryptionIv);
        System.out.println("in constructor... " + tempDataHolder);
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_receiver_password, parent, false);

        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        String[] title1;
        String tempELogin, dLogin, tempDLogin, tempEPassword, dPassword, tempDPassword, dWebsiteName, dWebsiteLink, Title, currentDate;
        final PasswordHelperClass temp = dataHolder.get(position);
        try {
            currentDate = temp.getDate();
            //Double Decryption
            tempELogin = temp.getAddDataLogin();
            dLogin = aes.decrypt(tempELogin);
            tempDLogin = aes.decrypt(dLogin);

            tempEPassword = temp.getAddDataPassword();
            dPassword = aes.decrypt(tempEPassword);
            tempDPassword = aes.decrypt(dPassword);


            dWebsiteName = temp.getAddWebsite_name();
            dWebsiteLink = temp.getAddWebsite_link();

            Title = dWebsiteName.substring(0, 1).toUpperCase() + dWebsiteName.substring(1);
            title1 = Title.split("_", 3);


            holder.tvLogin.setText(tempDLogin);

            myAdaptorThreadRunnable threadRunnable = new myAdaptorThreadRunnable(position, holder, dWebsiteLink);
            new Thread(threadRunnable).start();
            try {
                if (bmWebsiteLogo.sameAs(emptyBitmap)) {

                }
            } catch (NullPointerException e) {
                holder.imgWebsiteLogo.setVisibility(View.GONE);
                holder.tvImageTitle.setVisibility(View.VISIBLE);
                if (title1.length == 3) {
                    holder.tvImageTitle.setText(title1[1]);
                } else if (title1.length == 2) {
                    holder.tvImageTitle.setText(title1[0]);
                } else if (title1.length == 1) {
                    holder.tvImageTitle.setText(title1[0]);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return dataHolder.size();
    }


    public class myViewHolder extends RecyclerView.ViewHolder {

        final TextView tvLogin, tvImageTitle;
        final ImageView imgWebsiteLogo;
        final Toolbar tbcvMore;
        final LinearLayout LLCard;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLogin = itemView.findViewById(R.id.displayname);
            tvImageTitle = itemView.findViewById(R.id.tv_img_title);
            imgWebsiteLogo = itemView.findViewById(R.id.img_logo);

            tbcvMore = itemView.findViewById(R.id.cardview_more);
            LLCard = itemView.findViewById(R.id.linear_layout_card);


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

    public class myAdaptorThreadRunnable implements Runnable {

        private final int position;
        RecyclerView.ViewHolder holder;
        String dWebsiteLink;


        public myAdaptorThreadRunnable(int position1, RecyclerView.ViewHolder holder, String dWebsiteLink) {
            this.position = position1;
            this.holder = holder;
            this.dWebsiteLink = dWebsiteLink;
        }

        Handler handler = new Handler();

        @Override
        public void run() {
            try {
                //
                bmWebsiteLogo = fetchFavicon(Uri.parse(dWebsiteLink));
                System.out.println("website logo loading");
                emptyBitmap = Bitmap.createBitmap(bmWebsiteLogo.getWidth(), bmWebsiteLogo.getHeight(), bmWebsiteLogo.getConfig());

            } catch (Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ((myViewHolder)holder).imgWebsiteLogo.setImageBitmap(bmWebsiteLogo);
                    }
                });

            }
        }
    }
}