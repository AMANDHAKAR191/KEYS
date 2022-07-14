package com.example.keys.aman.app.notes;

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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keys.R;
import com.example.keys.aman.app.AES;
import com.example.keys.aman.app.SplashActivity;
import com.example.keys.aman.app.signin_login.LogInActivity;

import java.util.ArrayList;

public abstract class myadaptorfornote extends RecyclerView.Adapter<myadaptorfornote.myviewholder> implements Filterable {
    final ArrayList<addDNoteHelperClass> dataholder;
    final ArrayList<addDNoteHelperClass> dataholderfilter;
    final Context context;
    Activity activity;
    private SharedPreferences sharedPreferences;
    AES aes = new AES();

    public myadaptorfornote(ArrayList<addDNoteHelperClass> dataholder, Context context, Activity activity) {
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

        sharedPreferences = context.getSharedPreferences(LogInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
        aes.initFromStrings(sharedPreferences.getString(LogInActivity.AES_KEY, null), sharedPreferences.getString(LogInActivity.AES_IV, null));
        int p = holder.getAdapterPosition();
        String noteDate, noteTitle, noteBody, decryptedNoteTitle, decryptedNoteBody, doubleDecryptedNoteTitle, doubleDecryptedNoteBody;
        boolean isHideNote;
        try {
            noteDate = dataholder.get(position).getDate();
            isHideNote = dataholder.get(position).isHideNote();
            noteTitle = dataholder.get(position).getTitle();
            noteBody = dataholder.get(position).getNote();

            //Double Decryption
            decryptedNoteTitle = aes.decrypt(noteTitle);
            doubleDecryptedNoteTitle = aes.decrypt(decryptedNoteTitle);
            decryptedNoteBody = aes.decrypt(noteBody);
            doubleDecryptedNoteBody = aes.decrypt(decryptedNoteBody);

            holder.tvDate.setText(noteDate);
            holder.tvTitle.setText(doubleDecryptedNoteTitle);
            holder.tvNote.setText(doubleDecryptedNoteBody);

            holder.llCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SplashActivity.isForeground = true;
                    Intent intent = new Intent(context, addNotesActivity.class);
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
                    Intent intent = new Intent(context, addNotesActivity.class);
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
                    Intent intent = new Intent(context, addNotesActivity.class);
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
                    Intent intent = new Intent(context, addNotesActivity.class);
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
                            notesActivity.reference.child(noteDate).removeValue();
                            notesActivity.adaptor.notifyDataSetChanged();
                            holder.resetAdaptorCall();
                            return true;
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

    @Override
    public Filter getFilter() {
        return newFilter;
    }

    private final Filter newFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<addDNoteHelperClass> filteredDataList = new ArrayList<>();

            if (charSequence == null || charSequence.length() == 0) {
                filteredDataList.addAll(dataholder);
            } else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (addDNoteHelperClass addDNoteHelperClass : dataholder) {
                    try {
                        String a1 = aes.decrypt(addDNoteHelperClass.getTitle());
                        if (a1.toLowerCase().contains(filterPattern)) {
                            filteredDataList.add(addDNoteHelperClass);
                            try {
                                System.out.println("Filtered data: " + aes.decrypt(addDNoteHelperClass.getTitle()));
                                System.out.println("Filtered ArrayList : " + filteredDataList);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredDataList;
            filterResults.count = filteredDataList.size();
            System.out.println("filterResults: " + filterResults.values);
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {

            dataholderfilter.clear();
            dataholderfilter.addAll((ArrayList) filterResults.values);
            notifyDataSetChanged();
        }
    };


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
        public void resetAdaptorCall(){
            resetAdaptor();
        }

    }

    //create new abstract method
    public void resetAdaptor() {
    }
}
