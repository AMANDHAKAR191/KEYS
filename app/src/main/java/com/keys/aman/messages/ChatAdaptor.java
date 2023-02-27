package com.keys.aman.messages;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.keys.aman.AES;
import com.keys.aman.R;
import com.keys.aman.home.addpassword.PasswordHelperClass;
import com.keys.aman.signin_login.LogInActivity;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class ChatAdaptor extends RecyclerView.Adapter {

    private SharedPreferences sharedPreferences;
    ArrayList<ChatModelClass> dataHolder;
    String commonEncryptionKey, commonEncryptionIv;
    Context context;
    Activity activity;
    LogInActivity logInActivity = new LogInActivity();

    final int SENDER_VIEW_TYPE = 1;
    final int RECEIVER_VIEW_TYPE = -1;
    final int SENDER_NOTE_VIEW_TYPE = 2;
    final int RECEIVER_NOTE_VIEW_TYPE = -2;
    final int SENDER_PASSWORD_VIEW_TYPE = 3;
    final int RECEIVER_PASSWORD_VIEW_TYPE = -3;

    String chatType = "text";
    private final AES aes = new AES();
    private Bitmap bmWebsiteLogo;
    private Bitmap emptyBitmap;

    public ChatAdaptor() {
    }

    public ChatAdaptor(ArrayList<ChatModelClass> dataHolder, String commonEncryptionKey, String commonEncryptionIv, Context context, Activity activity) {
        this.dataHolder = dataHolder;
        this.context = context;
        this.activity = activity;
        sharedPreferences = activity.getSharedPreferences(logInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
        aes.initFromStrings(commonEncryptionKey, commonEncryptionIv);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case SENDER_VIEW_TYPE:
                view = LayoutInflater.from(context).inflate(R.layout.layout_sender_message, parent, false);
                return new SenderViewHolder(view);
            case RECEIVER_VIEW_TYPE:
                view = LayoutInflater.from(context).inflate(R.layout.layout_receiver_message, parent, false);
                return new ReceiverViewHolder(view);
            case SENDER_NOTE_VIEW_TYPE:
                view = LayoutInflater.from(context).inflate(R.layout.layout_sender_note, parent, false);
                return new SenderNoteViewHolder(view);
            case RECEIVER_NOTE_VIEW_TYPE:
                view = LayoutInflater.from(context).inflate(R.layout.layout_receiver_note, parent, false);
                return new ReceiverNoteViewHolder(view);
            case SENDER_PASSWORD_VIEW_TYPE:
                view = LayoutInflater.from(context).inflate(R.layout.layout_sender_password, parent, false);
                return new SenderPasswordViewHolder(view);
            default:
                view = LayoutInflater.from(context).inflate(R.layout.layout_receiver_password, parent, false);
                return new ReceiverPasswordViewHolder(view);
        }

    }

    @Override
    public int getItemViewType(int position) {
        // sender type
        if (dataHolder.get(position).getPublicUid().equals(sharedPreferences.getString(logInActivity.PUBLIC_UID, null))) {
            if (dataHolder.get(position).getType().equals("text")) {
                return SENDER_VIEW_TYPE;
            } else if (dataHolder.get(position).getType().equals("note")) {
                return SENDER_NOTE_VIEW_TYPE;
            } else {
                return SENDER_PASSWORD_VIEW_TYPE;
            }
        } else { // receiver type
            if (dataHolder.get(position).getType().equals("text")) {
                return RECEIVER_VIEW_TYPE;
            } else if (dataHolder.get(position).getType().equals("note")) {
                return RECEIVER_NOTE_VIEW_TYPE;
            } else {
                return RECEIVER_PASSWORD_VIEW_TYPE;
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String noteTitle, noteBody, decryptedNoteTitle, decryptedNoteBody, doubleDecryptedNoteTitle, doubleDecryptedNoteBody;
        String[] title1;
        String tempELogin, dLogin, tempDLogin, tempEPassword, dPassword, tempDPassword, dWebsiteName, dWebsiteLink, Title, currentDate;
        try {
            if (holder.getClass() == SenderViewHolder.class) {
                ((SenderViewHolder) holder).tvSenderMessage.setText(dataHolder.get(position).getMessage());
                DateFormat sdf = new SimpleDateFormat("hh:mm", Locale.getDefault());
                DateFormat inputsdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                String dateAndTime1 = sdf.format(Objects.requireNonNull(inputsdf.parse(dataHolder.get(position).getDateAndTime())));
                ((SenderViewHolder) holder).tvSenderTimeStamp.setText(dateAndTime1);
                ((SenderViewHolder) holder).tvSenderMessageStatus.setText(dataHolder.get(position).getStatus());

            } else if (holder.getClass() == ReceiverViewHolder.class) {
                ((ReceiverViewHolder) holder).tvReceiverMessage.setText(dataHolder.get(position).getMessage());
                DateFormat sdf = new SimpleDateFormat("hh:mm", Locale.getDefault());
                DateFormat inputsdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                String dateAndTime1 = sdf.format(Objects.requireNonNull(inputsdf.parse(dataHolder.get(position).getDateAndTime())));
                ((ReceiverViewHolder) holder).tvReceiverTimeStamp.setText(dateAndTime1);

            } else if (holder.getClass() == SenderNoteViewHolder.class) {
                if (dataHolder.get(position).getType().equals("note")) {
                    noteBody = dataHolder.get(position).getNoteModelClass().getNote();
                    noteTitle = dataHolder.get(position).getNoteModelClass().getTitle();
                    //Double Decryption
                    decryptedNoteTitle = aes.decrypt(noteTitle);
                    doubleDecryptedNoteTitle = aes.decrypt(decryptedNoteTitle);
                    decryptedNoteBody = aes.decrypt(noteBody);
                    doubleDecryptedNoteBody = aes.decrypt(decryptedNoteBody);

                    System.out.println(doubleDecryptedNoteTitle);
                    System.out.println(doubleDecryptedNoteBody);

                    ((SenderNoteViewHolder) holder).tvNote.setText(doubleDecryptedNoteBody);
                    ((SenderNoteViewHolder) holder).tvTitle.setText(doubleDecryptedNoteTitle);
                    System.out.println(dataHolder.get(position).getNoteModelClass());
                }

            } else if (holder.getClass() == ReceiverNoteViewHolder.class) {
                if (dataHolder.get(position).getType().equals("note")) {
                    noteBody = dataHolder.get(position).getNoteModelClass().getNote();
                    noteTitle = dataHolder.get(position).getNoteModelClass().getTitle();
                    //Double Decryption
                    decryptedNoteTitle = aes.decrypt(noteTitle);
                    doubleDecryptedNoteTitle = aes.decrypt(decryptedNoteTitle);
                    decryptedNoteBody = aes.decrypt(noteBody);
                    doubleDecryptedNoteBody = aes.decrypt(decryptedNoteBody);

                    System.out.println(doubleDecryptedNoteTitle);
                    System.out.println(doubleDecryptedNoteBody);

                    ((ReceiverNoteViewHolder) holder).tvNote.setText(doubleDecryptedNoteBody);
                    ((ReceiverNoteViewHolder) holder).tvTitle.setText(doubleDecryptedNoteTitle);
                    System.out.println(dataHolder.get(position).getNoteModelClass());
                }

            } else if (holder.getClass() == SenderPasswordViewHolder.class) {
                final PasswordHelperClass temp = dataHolder.get(position).getPasswordModelClass();
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


                    ((SenderPasswordViewHolder) holder).tvLogin.setText(tempDLogin);
                    myAdaptorThreadRunnable threadRunnable = new myAdaptorThreadRunnable(position, holder, dWebsiteLink);
                    new Thread(threadRunnable).start();
                    try {
                        if (bmWebsiteLogo.sameAs(emptyBitmap)) {

                        }
                    } catch (NullPointerException e) {
                        ((SenderPasswordViewHolder) holder).imgWebsiteLogo.setVisibility(View.GONE);
                        ((SenderPasswordViewHolder) holder).tvImageTitle.setVisibility(View.VISIBLE);
                        if (title1.length == 3) {
                            ((SenderPasswordViewHolder) holder).tvImageTitle.setText(title1[1]);
                        } else if (title1.length == 2) {
                            ((SenderPasswordViewHolder) holder).tvImageTitle.setText(title1[0]);
                        } else if (title1.length == 1) {
                            ((SenderPasswordViewHolder) holder).tvImageTitle.setText(title1[0]);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                final PasswordHelperClass temp = dataHolder.get(position).getPasswordModelClass();
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


                    ((ReceiverPasswordViewHolder) holder).tvLogin.setText(tempDLogin);

                    myAdaptorThreadRunnable threadRunnable = new myAdaptorThreadRunnable(position, holder, dWebsiteLink);
                    new Thread(threadRunnable).start();
                    try {
                        if (bmWebsiteLogo.sameAs(emptyBitmap)) {

                        }
                    } catch (NullPointerException e) {
                        ((ReceiverPasswordViewHolder) holder).imgWebsiteLogo.setVisibility(View.GONE);
                        ((ReceiverPasswordViewHolder) holder).tvImageTitle.setVisibility(View.VISIBLE);
                        if (title1.length == 3) {
                            ((ReceiverPasswordViewHolder) holder).tvImageTitle.setText(title1[1]);
                        } else if (title1.length == 2) {
                            ((ReceiverPasswordViewHolder) holder).tvImageTitle.setText(title1[0]);
                        } else if (title1.length == 1) {
                            ((ReceiverPasswordViewHolder) holder).tvImageTitle.setText(title1[0]);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {

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
        return dataHolder.size();
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {

        TextView tvReceiverMessage, tvReceiverTimeStamp;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReceiverMessage = itemView.findViewById(R.id.tv_receiver_message);
            tvReceiverTimeStamp = itemView.findViewById(R.id.tv_receiver_time_stamp);
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {

        TextView tvSenderMessage, tvSenderTimeStamp, tvSenderMessageStatus;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSenderMessage = itemView.findViewById(R.id.tv_sender_message);
            tvSenderTimeStamp = itemView.findViewById(R.id.tv_sender_time_stamp);
            tvSenderMessageStatus = itemView.findViewById(R.id.tv_sender_message_status);
        }
    }

    public class SenderNoteViewHolder extends RecyclerView.ViewHolder {

        final TextView tvDate;
        final TextView tvTitle;
        final TextView tvNote;
        final Toolbar tbcvMore;
        final LinearLayout llCard;

        public SenderNoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvNote = itemView.findViewById(R.id.tv_note);
            tbcvMore = itemView.findViewById(R.id.cardview_more);
            llCard = itemView.findViewById(R.id.lenear_layout_card);
        }
    }

    public class ReceiverNoteViewHolder extends RecyclerView.ViewHolder {

        final TextView tvDate;
        final TextView tvTitle;
        final TextView tvNote;
        final Toolbar tbcvMore;
        final LinearLayout llCard;

        public ReceiverNoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvNote = itemView.findViewById(R.id.tv_note);
            tbcvMore = itemView.findViewById(R.id.cardview_more);
            llCard = itemView.findViewById(R.id.lenear_layout_card);
        }
    }

    public class SenderPasswordViewHolder extends RecyclerView.ViewHolder {

        final TextView tvLogin, tvImageTitle;
        final ImageView imgWebsiteLogo;

        public SenderPasswordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLogin = itemView.findViewById(R.id.displayname);
            tvImageTitle = itemView.findViewById(R.id.tv_img_title);
            imgWebsiteLogo = itemView.findViewById(R.id.img_logo);
        }

    }

    public class ReceiverPasswordViewHolder extends RecyclerView.ViewHolder {

        final TextView tvLogin, tvImageTitle;
        final ImageView imgWebsiteLogo;
        final Toolbar tbcvMore;
        final LinearLayout LLCard;

        public ReceiverPasswordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLogin = itemView.findViewById(R.id.displayname);
            tvImageTitle = itemView.findViewById(R.id.tv_img_title);
            imgWebsiteLogo = itemView.findViewById(R.id.img_logo);

            tbcvMore = itemView.findViewById(R.id.cardview_more);
            LLCard = itemView.findViewById(R.id.linear_layout_card);


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
                        ((SenderPasswordViewHolder) holder).imgWebsiteLogo.setImageBitmap(bmWebsiteLogo);
                    }
                });

            }
        }
    }


}
