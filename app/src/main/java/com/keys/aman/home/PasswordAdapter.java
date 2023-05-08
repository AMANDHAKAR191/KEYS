package com.keys.aman.home;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.keys.aman.AES;
import com.keys.aman.data.MyPreference;
import com.keys.aman.R;
import com.keys.aman.data.Firebase;
import com.keys.aman.data.iFirebaseDAO;
import com.keys.aman.home.addpassword.PasswordHelperClass;
import com.keys.aman.iAES;
import com.keys.aman.messages.ChatActivity;
import com.keys.aman.signin_login.LogInActivity;

import java.util.ArrayList;

public class PasswordAdapter extends RecyclerView.Adapter<PasswordAdapter.myViewHolder> implements Filterable {

    public static final String REQUEST_ID = "passwordAdaptor";
    ArrayList<PasswordHelperClass> dataHolder;
    ArrayList<PasswordHelperClass> dataHolderFull;
    private final Filter passwordFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<PasswordHelperClass> filteredList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                System.out.println("Zero Length");
                System.out.println(dataHolderFull);
                filteredList.addAll(dataHolderFull);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();
                System.out.println("filterPattern " + filterPattern);
                System.out.println(dataHolderFull);
                for (PasswordHelperClass tempItem : dataHolderFull) {
                    if (tempItem.getAddWebsite_name().toLowerCase().contains(filterPattern)) {
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
    MyPreference myPreference;
    Context context;
    Activity activity;
    iAES iAES;
    LogInActivity logInActivity = new LogInActivity();
    ChatActivity chatActivity = new ChatActivity();
    //    public static Bitmap bmWebsiteLogo;
    private Bitmap emptyBitmap;

    public PasswordAdapter(ArrayList<PasswordHelperClass> tempDataHolder, Context context, Activity activity) {
        myPreference = MyPreference.getInstance(context);
        iAES = AES.getInstance(myPreference.getAesKey(), myPreference.getAesIv());
        this.dataHolder = tempDataHolder;
        this.dataHolderFull = tempDataHolder;
        this.context = context;
        this.activity = activity;

    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_layout_password, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {
        String date, userName, decryptedUserName, password, decryptedPassword, websiteName, websiteLink, title;
        final PasswordHelperClass temp = dataHolder.get(position);

        String[] title1;
        date = dataHolder.get(position).getDate();
        userName = dataHolder.get(position).getAddDataLogin();
        decryptedUserName = iAES.doubleDecryption(userName);
        password = dataHolder.get(position).getAddDataPassword();
        decryptedPassword = iAES.doubleDecryption(password);
        websiteName = temp.getAddWebsite_name();
        websiteLink = temp.getAddWebsite_link();

        title = websiteName.substring(0, 1).toUpperCase() + websiteName.substring(1);
        title1 = title.split("_", 3);
        holder.tvLogin.setText(decryptedUserName);
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


        holder.showCardViewFragmentCall(date, decryptedUserName, decryptedPassword, websiteName, websiteLink);
        holder.tbcvMore.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.img_copy_username:
                        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("Copy_Login", decryptedUserName);
                        clipboardManager.setPrimaryClip(clipData);
                        Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show();
                        return true;

                    case R.id.img_copy_password:
                        ClipboardManager clipboardManager1 = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clipData1 = ClipData.newPlainText("Copy_Password", decryptedPassword);
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
                                        iFirebaseDAO iFirebaseDAO = new Firebase(context);
                                        iFirebaseDAO.deleteSinglePassword(date, websiteName, new Firebase.iPasswordDeleteCallback() {
                                            @Override
                                            public void onPasswordDeleted() {
                                                HomeFragment.adaptor.notifyDataSetChanged();
                                                holder.resetAdaptorCall();
                                            }
                                        });
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
                        PasswordHelperClass passwordData = new PasswordHelperClass(date, decryptedUserName, decryptedPassword, websiteName, websiteLink);
                        holder.sharePasswordCall(passwordData);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataHolder.size();
    }

    @Override
    public Filter getFilter() {
        return passwordFilter;
    }

    //create new abstract method
    public void showCardViewFragment(String currentDate, String tempLogin, String tempPassword,
                                     String dWebsiteName, String dWebsiteLink) {
    }

    public void resetAdaptor() {
    }

    public void sharePassword(PasswordHelperClass passwordData) {
    }

    public class myViewHolder extends RecyclerView.ViewHolder {

        final TextView tvLogin, tvWebsiteName, tvWebsiteTitle, tvImageTitle;
        //        final ImageView imgWebsiteLogo;
        final Toolbar tbcvMore;
        final LinearLayout LLCard;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLogin = itemView.findViewById(R.id.displayname);
            tvImageTitle = itemView.findViewById(R.id.tv_img_title);
//            imgWebsiteLogo = itemView.findViewById(R.id.img_logo);

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
            tvImageTitle.setOnClickListener(new View.OnClickListener() {
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

        public void sharePasswordCall(PasswordHelperClass passwordData) {
            sharePassword(passwordData);
        }


    }
}