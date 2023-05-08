package com.keys.aman.notes;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.keys.aman.R;
import com.keys.aman.SplashActivity;
import com.keys.aman.data.MyPreference;
import com.keys.aman.iAES;
import com.keys.aman.notes.addnote.AddNotesActivity;
import com.keys.aman.notes.addnote.NoteHelperClass;
import com.keys.aman.signin_login.LogInActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class NoteAdapter extends RecyclerView.Adapter {
    public static final String REQUEST_ID = "NoteAdapter";
    final ArrayList<NoteHelperClass> dataholder;
    final ArrayList<NoteHelperClass> dataholderfilter;
    public static final int PINNED_VIEW_TYPE = 1;
    public static final int UNPINNED_VIEW_TYPE = -1;
    final Context context;
    Activity activity;
    iAES iAES;
    MyPreference myPreference;
    LogInActivity logInActivity = new LogInActivity();

    public NoteAdapter(ArrayList<NoteHelperClass> dataholder, Context context, Activity activity) {
        this.dataholder = dataholder;
        this.context = context;
        this.activity = activity;
        this.dataholderfilter = new ArrayList<>(dataholder);
        myPreference = MyPreference.getInstance(context);
        iAES = AES.getInstance(myPreference.getAesKey(), myPreference.getAesIv());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == PINNED_VIEW_TYPE){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_layout_note_pinned, parent, false);
            return new myViewHolderPinned(view);

        }else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_layout_note_unpinned, parent, false);
            return new myViewHolderUnpinned(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
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
            if (holder.getClass() == myViewHolderPinned.class){
                ((myViewHolderPinned) holder).tvDate.setText(dateAndTime1);
                ((myViewHolderPinned) holder).tvTitle.setText(decryptedNoteTitle);
                ((myViewHolderPinned) holder).tvNote.setText(decryptedNoteBody);
                if (dataholder.get(position).isPinned()){
                    ((myViewHolderPinned)holder).tbcvMore.getMenu().findItem(R.id.item_pin).setIcon(R.drawable.ic_pin_icon);
                }
            }else {
                ((myViewHolderUnpinned)holder).tvDate.setText(dateAndTime1);
                ((myViewHolderUnpinned)holder).tvTitle.setText(decryptedNoteTitle);
                ((myViewHolderUnpinned)holder).tvNote.setText(decryptedNoteBody);
                if (dataholder.get(position).isPinned()){
                    ((myViewHolderUnpinned)holder).tbcvMore.getMenu().findItem(R.id.item_pin).setIcon(R.drawable.ic_pin_icon);
                }
            }

            //write the code here to change menu item's icon in option menu if isPinned == true

            holder.itemView.setOnClickListener(new View.OnClickListener() {
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

            if (holder instanceof myViewHolderPinned) {
                ((myViewHolderPinned)holder).tbcvMore.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
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
                                        .setPositiveButton("Yes,Delete", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                NotesFragment.reference.child(noteDate).removeValue();
                                                NotesFragment.adaptorUnpinned.notifyDataSetChanged();
                                                ((myViewHolderPinned) holder).resetAdaptorCall();
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
                                NotesFragment.reference.child(noteDate).child("pinned").setValue(false);
                                Toast.makeText(context, "Pinned!", Toast.LENGTH_SHORT).show();
                                ((myViewHolderPinned) holder).refreshRecViewCall();
                                return true;
                            case R.id.img_share_note:
                                NoteHelperClass noteData = new NoteHelperClass(dateAndTime1, decryptedNoteTitle, decryptedNoteBody, isHideNote, isPinned);
                                ((myViewHolderPinned) holder).shareNotesCall(noteData);
                        }
                        return false;
                    }
                });
            }
            if (holder instanceof myViewHolderUnpinned) {
                ((myViewHolderUnpinned)holder).tbcvMore.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
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
                                        .setPositiveButton("Yes,Delete", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                NotesFragment.reference.child(noteDate).removeValue();
                                                NotesFragment.adaptorUnpinned.notifyDataSetChanged();
                                                ((myViewHolderUnpinned) holder).resetAdaptorCall();
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
                                Toast.makeText(context, "Pinned!", Toast.LENGTH_SHORT).show();
                                ((myViewHolderUnpinned) holder).refreshRecViewCall();
                                return true;
                            case R.id.img_share_note:
                                NoteHelperClass noteData = new NoteHelperClass(dateAndTime1, decryptedNoteTitle, decryptedNoteBody, isHideNote, isPinned);
                                ((myViewHolderUnpinned) holder).shareNotesCall(noteData);
                        }
                        return false;
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return dataholder.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (dataholder.get(position).isPinned()) {
            return PINNED_VIEW_TYPE;
        }
        return UNPINNED_VIEW_TYPE;
    }

    //create new abstract method
    public void resetAdaptor() {
    }

    public void shareNotes(NoteHelperClass noteData) {
    }

    public void refreshRecView() {
    }

    public class myViewHolderPinned extends RecyclerView.ViewHolder {
        final TextView tvDate;
        final TextView tvTitle;
        final TextView tvNote;
        final Toolbar tbcvMore;
        final LinearLayout llCard;
        public myViewHolderPinned(@NonNull View itemView) {
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
    public class myViewHolderUnpinned extends RecyclerView.ViewHolder {
        final TextView tvDate;
        final TextView tvTitle;
        final TextView tvNote;
        final Toolbar tbcvMore;
        final LinearLayout llCard;
        public myViewHolderUnpinned(@NonNull View itemView) {
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
}
