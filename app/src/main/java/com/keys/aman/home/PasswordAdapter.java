package com.keys.aman.home;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
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

import com.keys.aman.R;
import com.keys.aman.AES;
import com.keys.aman.home.addpassword.PasswordHelperClass;
import com.keys.aman.messages.ChatActivity;
import com.keys.aman.signin_login.LogInActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class PasswordAdapter extends RecyclerView.Adapter<PasswordAdapter.myViewHolder> implements Filterable {

    ArrayList<PasswordHelperClass> dataHolder;
    ArrayList<PasswordHelperClass> dataHolderFull;
    Context context;
    Activity activity;
    AES aes = new AES();
    public static Bitmap bmWebsiteLogo;
    private Bitmap emptyBitmap;
    LogInActivity logInActivity = new LogInActivity();
    ChatActivity chatActivity = new ChatActivity();
    public static final String REQUEST_ID = "passwordAdaptor";

    public PasswordAdapter(ArrayList<PasswordHelperClass> tempDataHolder, Context context, Activity activity) {
        this.dataHolder = tempDataHolder;
        this.dataHolderFull = tempDataHolder;
        this.context = context;
        this.activity = activity;
        System.out.println("in constructor... " + tempDataHolder);
//        dataHolderFull = new ArrayList<>(dataHolder);
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_layout_password, parent, false);

        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        System.out.println("in Adaptor...");
        System.err.println("dataHolderFull " + dataHolderFull + "\nlength = " + dataHolderFull.size());
        myAdaptorThreadRunnable threadRunnable = new myAdaptorThreadRunnable(position, holder);
        new Thread(threadRunnable).start();
    }

    @Override
    public int getItemCount() {
        return dataHolder.size();
    }

    private final Filter passwordFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<PasswordHelperClass> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0){
                System.out.println("Zero Length");
                System.out.println(dataHolderFull);
                filteredList.addAll(dataHolderFull);
            }else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                System.out.println("filterPattern " + filterPattern);
                System.out.println(dataHolderFull);
                for(PasswordHelperClass tempItem : dataHolderFull){
                    if (tempItem.getAddWebsite_name().toLowerCase().contains(filterPattern)){
                        System.out.println();
                        filteredList.add(tempItem);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredList;
            filterResults.count = filteredList.size();
            System.out.println(filteredList);
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            dataHolder.clear();
            dataHolder.addAll((ArrayList) filterResults.values);
            System.out.println("dataHolder + " + dataHolder + "\nlength = " + dataHolder.size());
            notifyDataSetChanged();
        }
    };

    @Override
    public Filter getFilter() {
        return passwordFilter;
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        final TextView tvLogin, tvWebsiteName, tvWebsiteTitle, tvImageTitle;
        final ImageView imgWebsiteLogo;
        final Toolbar tbcvMore;
        final LinearLayout LLCard;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLogin = itemView.findViewById(R.id.displayname);
            tvImageTitle = itemView.findViewById(R.id.tv_img_title);
            imgWebsiteLogo = itemView.findViewById(R.id.img_logo);

            tvWebsiteName = itemView.findViewById(R.id.displaywebsite);
            tbcvMore = itemView.findViewById(R.id.cardview_more);
            LLCard = itemView.findViewById(R.id.linear_layout_card);
            tvWebsiteTitle = itemView.findViewById(R.id.tv_img_title);

        }

        public void resetAdaptorCall() {
            resetAdaptor();
        }

        public void showCardViewFragmentCall(String currentDate, String tempLogin, String tempPassword,
                                             String dWebsiteName, String dWebsiteLink) {
            imgWebsiteLogo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCardViewFragment(currentDate, tempLogin, tempPassword, dWebsiteName, dWebsiteLink);
                }
            });
            tvLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCardViewFragment(currentDate, tempLogin, tempPassword, dWebsiteName, dWebsiteLink);
                }
            });
            tvWebsiteName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCardViewFragment(currentDate, tempLogin, tempPassword, dWebsiteName, dWebsiteLink);

                }
            });
            LLCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCardViewFragment(currentDate, tempLogin, tempPassword, dWebsiteName, dWebsiteLink);
                }
            });
        }
        public void sharePasswordCall(PasswordHelperClass passwordData){
            sharePassword(passwordData);
        }


    }

    //create new abstract method
    public void showCardViewFragment(String currentDate, String tempLogin, String tempPassword,
                                     String dWebsiteName, String dWebsiteLink) {}

    public void resetAdaptor() {}

    public void sharePassword(PasswordHelperClass passwordData){}


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
        myViewHolder holder;
        String dWebsiteLink, Title;
        String[] title1;
        String dWebsiteName;
        String currentDate, decryptedLogin, decryptedPassword, doubleDecryptedLogin, doubleDecryptedPassword, tempELogin, tempEPassword;


        public myAdaptorThreadRunnable(int position1, myViewHolder holder1) {
            this.position = position1;
            this.holder = holder1;
        }

        Handler handler = new Handler();

        @Override
        public void run() {
            SharedPreferences sharedPreferences = context.getSharedPreferences(logInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
            aes.initFromStrings(sharedPreferences.getString(logInActivity.getAES_KEY(), null), sharedPreferences.getString(logInActivity.getAES_IV(), null));
            int p = holder.getAdapterPosition();
            final PasswordHelperClass temp = dataHolder.get(position);

            try {
                currentDate = dataHolder.get(position).getDate();
                //Double Decryption
                tempELogin = dataHolder.get(position).getAddDataLogin();
                decryptedLogin = aes.decrypt(tempELogin);
                doubleDecryptedLogin = aes.decrypt(decryptedLogin);

                tempEPassword = dataHolder.get(position).getAddDataPassword();
                decryptedPassword = aes.decrypt(tempEPassword);
                doubleDecryptedPassword = aes.decrypt(decryptedPassword);


                dWebsiteName = temp.getAddWebsite_name();
                dWebsiteLink = temp.getAddWebsite_link();

                Title = dWebsiteName.substring(0, 1).toUpperCase() + dWebsiteName.substring(1);
                title1 = Title.split("_", 3);


            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                bmWebsiteLogo = fetchFavicon(Uri.parse(dWebsiteLink));
                emptyBitmap = Bitmap.createBitmap(bmWebsiteLogo.getWidth(), bmWebsiteLogo.getHeight(), bmWebsiteLogo.getConfig());

            } catch (Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        holder.tvWebsiteName.setText(dWebsiteName);
                    }
                });
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    //
                    holder.tvLogin.setText(doubleDecryptedLogin);
                    if (title1.length == 3) {
                        holder.tvWebsiteName.setText(title1[1]);
                    } else if (title1.length == 2) {
                        holder.tvWebsiteName.setText(title1[0]);
                    }

                    try {
                        if (bmWebsiteLogo.sameAs(emptyBitmap)) {

                        }
                    } catch (NullPointerException e) {
                        holder.imgWebsiteLogo.setVisibility(View.GONE);
                        holder.tvImageTitle.setVisibility(View.VISIBLE);
                        if (title1.length == 3) {
                            holder.tvImageTitle.setText(title1[1]);
                            holder.tvWebsiteName.setText(title1[1]);
                        } else if (title1.length == 2) {
                            holder.tvImageTitle.setText(title1[0]);
                            holder.tvWebsiteName.setText(title1[0]);
                        } else if (title1.length == 1) {
                            holder.tvImageTitle.setText(title1[0]);
                            holder.tvWebsiteName.setText(title1[0]);
                        }
                    }

                    holder.showCardViewFragmentCall(currentDate, doubleDecryptedLogin, doubleDecryptedPassword, dWebsiteName, dWebsiteLink);

                    holder.tbcvMore.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.img_copy_username:
                                    ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clipData = ClipData.newPlainText("Copy_Login", doubleDecryptedLogin);
                                    clipboardManager.setPrimaryClip(clipData);
                                    Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show();
                                    return true;

                                case R.id.img_copy_password:
                                    ClipboardManager clipboardManager1 = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                    ClipData clipData1 = ClipData.newPlainText("Copy_Password", doubleDecryptedPassword);
                                    clipboardManager1.setPrimaryClip(clipData1);
                                    Toast.makeText(context, "Copied! ", Toast.LENGTH_SHORT).show();
                                    return true;
                                case R.id.img_delete:
                                    new MaterialAlertDialogBuilder(context)
                                            .setTitle("Alert!")
                                            .setMessage("Are you sure, you want to delete the password?")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    HomeFragment.databaseReference.child(dWebsiteName).child(currentDate).removeValue();
                                                    HomeFragment.adaptor.notifyDataSetChanged();
                                                    holder.resetAdaptorCall();
                                                }
                                            })
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            }).show();
                                    return true;
                                case R.id.img_share_password:
                                    PasswordHelperClass passwordData = new PasswordHelperClass(currentDate, doubleDecryptedLogin, doubleDecryptedPassword, dWebsiteName, dWebsiteLink);
                                    holder.sharePasswordCall(passwordData);
                            }
                            return false;
                        }
                    });

                    holder.imgWebsiteLogo.setImageBitmap(bmWebsiteLogo);
                }
            });
        }
    }
}