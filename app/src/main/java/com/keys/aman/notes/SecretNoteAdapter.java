package com.keys.aman.notes;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
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

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.keys.aman.AES;
import com.keys.aman.data.MyPreference;
import com.keys.aman.R;
import com.keys.aman.SplashActivity;
import com.keys.aman.iAES;
import com.keys.aman.notes.addnote.AddNotesActivity;
import com.keys.aman.notes.addnote.NoteHelperClass;
import com.keys.aman.signin_login.LogInActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class SecretNoteAdapter extends RecyclerView.Adapter<SecretNoteAdapter.myviewholder> {
    final ArrayList<NoteHelperClass> dataholder;
    final ArrayList<NoteHelperClass> dataholderfilter;
    final Context context;
    Activity activity;
    iAES iAES;
    MyPreference myPreference;
    LogInActivity logInActivity = new LogInActivity();
    public static final String REQUEST_ID = "NoteAdapter";

    public SecretNoteAdapter(ArrayList<NoteHelperClass> dataholder, Context context, Activity activity) {
        this.dataholder = dataholder;
        this.context = context;
        this.activity = activity;
        this.dataholderfilter = new ArrayList<>(dataholder);
        myPreference = MyPreference.getInstance(context);
        iAES = AES.getInstance(myPreference.getAesKey(), myPreference.getAesIv());
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_layout_secret_note, parent, false);
        return new myviewholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        String noteDate, noteTitle, noteBody, decryptedNoteTitle, decryptedNoteBody;
        boolean isHideNote, isPinned;
        try {
            noteDate = dataholder.get(position).getDate();
            isHideNote = dataholder.get(position).isHideNote();
            isPinned = dataholder.get(position).isPinned();
            System.out.println(dataholder.get(position).isPinned());
            noteTitle = dataholder.get(position).getTitle();
            noteBody = dataholder.get(position).getNote();

            //Double Decryption
            decryptedNoteTitle = iAES.doubleDecryption(noteTitle);
            decryptedNoteBody = iAES.doubleDecryption(noteBody);


            DateFormat sdf = new SimpleDateFormat("dd-MMMM-yyyy / hh:mm", Locale.getDefault());
            DateFormat inputsdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String dateAndTime1 = sdf.format(Objects.requireNonNull(inputsdf.parse(noteDate)));
//            String ispin = String.valueOf(isPinned);
//            String ishide = String.valueOf(isHideNote);
            holder.tvDate.setText(dateAndTime1);
            holder.tvTitle.setText(decryptedNoteTitle);
            holder.tvNote.setText(decryptedNoteBody);

            holder.llCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SplashActivity.isForeground = true;
                    Intent intent = new Intent(context, AddNotesActivity.class);
                    intent.putExtra(logInActivity.REQUEST_CODE_NAME, REQUEST_ID);
                    intent.putExtra("date", noteDate);
                    intent.putExtra("hide note", isHideNote);
                    intent.putExtra("title", decryptedNoteTitle);
                    intent.putExtra("note", decryptedNoteBody);
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
                    intent.putExtra(logInActivity.REQUEST_CODE_NAME, REQUEST_ID);
                    intent.putExtra("date", noteDate);
                    intent.putExtra("hide note", isHideNote);
                    intent.putExtra("title", decryptedNoteTitle);
                    intent.putExtra("note", decryptedNoteBody);
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
                    intent.putExtra(logInActivity.REQUEST_CODE_NAME, REQUEST_ID);
                    intent.putExtra("date", noteDate);
                    intent.putExtra("hide note", isHideNote);
                    intent.putExtra("title", decryptedNoteTitle);
                    intent.putExtra("note", decryptedNoteBody);
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
                    intent.putExtra(logInActivity.REQUEST_CODE_NAME, REQUEST_ID);
                    intent.putExtra("date", noteDate);
                    intent.putExtra("hide note", isHideNote);
                    intent.putExtra("title", decryptedNoteTitle);
                    intent.putExtra("note", decryptedNoteBody);
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
                            ClipData clipData = ClipData.newPlainText("Copy_Password", decryptedNoteBody);
                            clipboardManager.setPrimaryClip(clipData);
                            Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show();
                            return true;
                        case R.id.img_delete:
                            new MaterialAlertDialogBuilder(context)
                                    .setTitle("Alert!")
                                    .setMessage("Are you sure, you want to delete the Note?")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            NotesFragment.reference.child(noteDate).removeValue();
                                            NotesFragment.adaptorUnpinned.notifyDataSetChanged();
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
                        case R.id.item_pin:
                            NotesFragment.reference.child(noteDate).child("pinned").setValue(true);
                            Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show();
                            holder.refreshRecViewCall();
                            return true;
                        case R.id.img_share_note:
                            NoteHelperClass noteData = new NoteHelperClass(dateAndTime1, decryptedNoteTitle, decryptedNoteBody, isHideNote, isPinned);
                            Log.e("shareNote", "Check1: NoteAdapterForUnpinned: " +noteData);
                            holder.shareNotesCall(noteData);
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

        public void refreshRecViewCall() {
            refreshRecView();
        }

        public void shareNotesCall(NoteHelperClass noteDate) {
            shareNotes(noteDate);
        }
    }

    //create new abstract method
    public void resetAdaptor() {
    }

    public void shareNotes(NoteHelperClass noteData){
    }

    public void refreshRecView() {
    }
}
