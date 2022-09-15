package com.example.keys.aman.notes;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keys.R;
import com.example.keys.aman.AES;
import com.example.keys.aman.SplashActivity;
import com.example.keys.aman.signin_login.LogInActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class myadaptorfornote extends RecyclerView.Adapter<myadaptorfornote.myviewholder> {
    final ArrayList<AddNoteDataHelperClass> dataholder;
    final ArrayList<AddNoteDataHelperClass> dataholderfilter;
    final Context context;
    Activity activity;
    AES aes = new AES();
    LogInActivity logInActivity = new LogInActivity();

    public myadaptorfornote(ArrayList<AddNoteDataHelperClass> dataholder, Context context, Activity activity) {
        this.dataholder = dataholder;
        this.context = context;
        this.activity = activity;
        this.dataholderfilter = new ArrayList<>(dataholder);
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_cardview_layout, parent, false);
        return new myviewholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(logInActivity.getSHARED_PREF_ALL_DATA(), MODE_PRIVATE);
        aes.initFromStrings(sharedPreferences.getString(logInActivity.getAES_KEY(), null), sharedPreferences.getString(logInActivity.getAES_IV(), null));
        int p = position;
        String noteDate, noteTitle, noteBody, decryptedNoteTitle, decryptedNoteBody, doubleDecryptedNoteTitle, doubleDecryptedNoteBody;
        boolean isHideNote, isPinned;
        try {
            noteDate = dataholder.get(position).getDate();
            isHideNote = dataholder.get(position).isHideNote();
            isPinned = dataholder.get(position).isPinned();
            System.out.println(dataholder.get(position).isPinned());
            noteTitle = dataholder.get(position).getTitle();
            noteBody = dataholder.get(position).getNote();

            //Double Decryption
            decryptedNoteTitle = aes.decrypt(noteTitle);
            doubleDecryptedNoteTitle = aes.decrypt(decryptedNoteTitle);
            decryptedNoteBody = aes.decrypt(noteBody);
            doubleDecryptedNoteBody = aes.decrypt(decryptedNoteBody);


            DateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy / hh:mm", Locale.getDefault());
            DateFormat inputsdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String dateAndTime1 = sdf.format(Objects.requireNonNull(inputsdf.parse(noteDate)));
//            String ispin = String.valueOf(isPinned);
//            String ishide = String.valueOf(isHideNote);
            holder.tvDate.setText(dateAndTime1);
            holder.tvTitle.setText(doubleDecryptedNoteTitle);
            holder.tvNote.setText(doubleDecryptedNoteBody);

            holder.llCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SplashActivity.isForeground = true;
                    Intent intent = new Intent(context, AddNotesActivity.class);
                    intent.putExtra("request_code", "notesCardView");
                    intent.putExtra("date", noteDate);
                    intent.putExtra("hide note", isHideNote);
                    intent.putExtra("title", doubleDecryptedNoteTitle);
                    intent.putExtra("note", doubleDecryptedNoteBody);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_down, 0);
                }
            });
            holder.tvDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SplashActivity.isForeground = true;
                    Intent intent = new Intent(context, AddNotesActivity.class);
                    intent.putExtra("request_code", "notesCardView");
                    intent.putExtra("date", noteDate);
                    intent.putExtra("hide note", isHideNote);
                    intent.putExtra("title", doubleDecryptedNoteTitle);
                    intent.putExtra("note", doubleDecryptedNoteBody);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_down, 0);
                }
            });
            holder.tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SplashActivity.isForeground = true;
                    Intent intent = new Intent(context, AddNotesActivity.class);
                    intent.putExtra("request_code", "notesCardView");
                    intent.putExtra("date", noteDate);
                    intent.putExtra("hide note", isHideNote);
                    intent.putExtra("title", doubleDecryptedNoteTitle);
                    intent.putExtra("note", doubleDecryptedNoteBody);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_down, 0);
                }
            });
            holder.tvNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SplashActivity.isForeground = true;
                    Intent intent = new Intent(context, AddNotesActivity.class);
                    intent.putExtra("request_code", "notesCardView");
                    intent.putExtra("date", noteDate);
                    intent.putExtra("hide note", isHideNote);
                    intent.putExtra("title", doubleDecryptedNoteTitle);
                    intent.putExtra("note", doubleDecryptedNoteBody);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_down, 0);
                }
            });
            holder.tbcvMore.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.img_copy_note:
                            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clipData = ClipData.newPlainText("Copy_Password", doubleDecryptedNoteBody);
                            clipboardManager.setPrimaryClip(clipData);
                            Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show();
                            return true;
                        case R.id.img_delete:
                            NotesFragment.reference.child(noteDate).removeValue();
                            NotesFragment.adaptorUnpinned.notifyDataSetChanged();
                            holder.resetAdaptorCall();
                            return true;
                        case R.id.item_pin:
                            NotesFragment.reference.child(noteDate).child("pinned").setValue(true);
                            holder.refreshRecViewCall();

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


    public class myviewholder extends RecyclerView.ViewHolder {

        final TextView tvDate;
        final TextView tvTitle;
        final TextView tvNote;
        final Toolbar tbcvMore;
        final LinearLayout llCard;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvNote = itemView.findViewById(R.id.tv_note);
            tbcvMore = itemView.findViewById(R.id.cardview_more);
            llCard = itemView.findViewById(R.id.lenear_layout_card);
        }

        public void resetAdaptorCall() {
            resetAdaptor();
        }
        public void refreshRecViewCall(){
            refreshRecView();
        }

    }

    //create new abstract method
    public void resetAdaptor() {
    }
    public void refreshRecView(){
    }
}
