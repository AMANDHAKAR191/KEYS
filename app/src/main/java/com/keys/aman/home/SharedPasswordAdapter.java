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

public class SharedPasswordAdapter extends RecyclerView.Adapter<SharedPasswordAdapter.myViewHolder> {

    public static final String REQUEST_ID = "passwordAdaptor";
    ArrayList<PasswordHelperClass> dataHolder;
    Context context;
    Activity activity;
    AES aes;
    LogInActivity logInActivity = new LogInActivity();
    ChatActivity chatActivity = new ChatActivity();
    private Bitmap emptyBitmap;

    public SharedPasswordAdapter(ArrayList<PasswordHelperClass> tempDataHolder, Context context, Activity activity, String commonEncryptionKey, String commonEncryptionIv) {
        this.dataHolder = tempDataHolder;
        this.context = context;
        this.activity = activity;
        aes = AES.getInstance(commonEncryptionKey, commonEncryptionIv);
        System.out.println("in constructor... " + tempDataHolder);
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

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_receiver_password, parent, false);

        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        String[] title1;
        String dLogin, dPassword, dWebsiteName, dWebsiteLink, Title, currentDate;
        final PasswordHelperClass temp = dataHolder.get(position);
        currentDate = temp.getDate();
        dLogin = aes.doubleDecryption(temp.getAddDataLogin());
        dPassword = aes.doubleDecryption(temp.getAddDataPassword());

        dWebsiteName = temp.getAddWebsite_name();
        dWebsiteLink = temp.getAddWebsite_link();

        Title = dWebsiteName.substring(0, 1).toUpperCase() + dWebsiteName.substring(1);
        title1 = Title.split("_", 3);

        holder.tvLogin.setText(dLogin);

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
}