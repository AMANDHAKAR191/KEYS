package com.example.keys.aman.messages;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keys.R;
import com.example.keys.aman.AES;
import com.example.keys.aman.signin_login.LogInActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class ChatAdaptor extends RecyclerView.Adapter {

    private SharedPreferences sharedPreferences;
    ArrayList<ChatModelClass> dataHolder;
    Context context;
    Activity activity;
    LogInActivity logInActivity = new LogInActivity();

    final int SENDER_VIEW_TYPE = 1;
    final int RECEIVER_VIEW_TYPE = 2;
    final int NOTE_VIEW_TYPE = 3;
    final int PASSWORD_VIEW_TYPE = 4;

    String chatType = "text";
    private final AES aes = new AES();

    public ChatAdaptor() {
    }

    public ChatAdaptor(ArrayList<ChatModelClass> dataHolder, Context context, Activity activity) {
        this.dataHolder = dataHolder;
        this.context = context;
        this.activity = activity;
        sharedPreferences = activity.getSharedPreferences(logInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
        aes.initFromStrings(sharedPreferences.getString(logInActivity.getAES_KEY(), null), sharedPreferences.getString(logInActivity.getAES_IV(), null));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case SENDER_VIEW_TYPE:
                view = LayoutInflater.from(context).inflate(R.layout.layout_sender_message, parent, false);
                return new SenderViewHolder(view);
            case RECEIVER_VIEW_TYPE:
                view = LayoutInflater.from(context).inflate(R.layout.layout_receiver_message, parent, false);
                return new ReceiverViewHolder(view);
            case NOTE_VIEW_TYPE:
                view = LayoutInflater.from(context).inflate(R.layout.note_share_cardview_layout, parent, false);
                return new NoteViewHolder(view);
            default:
                view = LayoutInflater.from(context).inflate(R.layout.cardview_layout, parent, false);
                return new PasswordViewHolder(view);
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (dataHolder.get(position).getType().equals("text")) {
            if (dataHolder.get(position).getPublicUid().equals(sharedPreferences.getString(logInActivity.PUBLIC_UID, null))) {
                return SENDER_VIEW_TYPE;
            } else {
                return RECEIVER_VIEW_TYPE;
            }
        } else if (dataHolder.get(position).getType().equals("note")) {
            return NOTE_VIEW_TYPE;
        } else {
            return PASSWORD_VIEW_TYPE;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String noteTitle, noteBody, decryptedNoteTitle, decryptedNoteBody, doubleDecryptedNoteTitle, doubleDecryptedNoteBody;

        try {
            if (holder.getClass() == SenderViewHolder.class) {
                Log.e("ChatActivity", "SenderViewHolder");
                ((SenderViewHolder) holder).tvSenderMessage.setText(dataHolder.get(position).getMessage());
                DateFormat sdf = new SimpleDateFormat("hh:mm", Locale.getDefault());
                DateFormat inputsdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                String dateAndTime1 = sdf.format(Objects.requireNonNull(inputsdf.parse(dataHolder.get(position).getDateAndTime())));
                ((SenderViewHolder) holder).tvSenderTimeStamp.setText(dateAndTime1);
            }else if (holder.getClass() == NoteViewHolder.class){
                Log.e("ChatActivity", "NoteViewHolder => text");
                Log.e("ChatActivity", "NoteViewHolder => " + dataHolder.get(position).getType());
                if (dataHolder.get(position).getType().equals("note")){
                    Log.e("ChatActivity", "NoteViewHolder: note");
                    noteBody = dataHolder.get(position).getNoteModelClass().getNote();
                    noteTitle = dataHolder.get(position).getNoteModelClass().getTitle();
                    //Double Decryption
                    decryptedNoteTitle = aes.decrypt(noteTitle);
                    doubleDecryptedNoteTitle = aes.decrypt(decryptedNoteTitle);
                    decryptedNoteBody = aes.decrypt(noteBody);
                    doubleDecryptedNoteBody = aes.decrypt(decryptedNoteBody);

                    System.out.println(doubleDecryptedNoteTitle);
                    System.out.println(doubleDecryptedNoteBody);

                    ((NoteViewHolder) holder).tvNote.setText(doubleDecryptedNoteBody);
                    ((NoteViewHolder) holder).tvTitle.setText(doubleDecryptedNoteTitle);
                    System.out.println(dataHolder.get(position).getNoteModelClass());
                }
            } else {
                Log.e("ChatActivity", "ReceiverViewHolder");
                ((ReceiverViewHolder) holder).tvReceiverMessage.setText(dataHolder.get(position).getMessage());
                DateFormat sdf = new SimpleDateFormat("hh:mm", Locale.getDefault());
                DateFormat inputsdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                String dateAndTime1 = sdf.format(Objects.requireNonNull(inputsdf.parse(dataHolder.get(position).getDateAndTime())));
                ((ReceiverViewHolder) holder).tvReceiverTimeStamp.setText(dateAndTime1);
            }

        } catch (Exception e) {
            e.printStackTrace();
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
    public class NoteViewHolder extends RecyclerView.ViewHolder {

        final TextView tvDate;
        final TextView tvTitle;
        final TextView tvNote;
        final Toolbar tbcvMore;
        final LinearLayout llCard;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvNote = itemView.findViewById(R.id.tv_note);
            tbcvMore = itemView.findViewById(R.id.cardview_more);
            llCard = itemView.findViewById(R.id.lenear_layout_card);
        }
    }
    public class PasswordViewHolder extends RecyclerView.ViewHolder {

        final TextView tvLogin, tvWebsiteName, tvWebsiteTitle, tvImageTitle;
        final ImageView imgWebsiteLogo;
        final Toolbar tbcvMore;
        final LinearLayout LLCard;

        public PasswordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLogin = itemView.findViewById(R.id.displayname);
            tvWebsiteName = itemView.findViewById(R.id.displaywebsite);
            tbcvMore = itemView.findViewById(R.id.cardview_more);
            LLCard = itemView.findViewById(R.id.linear_layout_card);
            tvWebsiteTitle = itemView.findViewById(R.id.tv_img_title);
            tvImageTitle = itemView.findViewById(R.id.tv_img_title);
            imgWebsiteLogo = itemView.findViewById(R.id.img_logo);
        }

    }

}
