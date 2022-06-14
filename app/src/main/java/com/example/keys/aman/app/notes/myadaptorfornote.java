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
import com.example.keys.aman.app.signin_login.LogInActivity;

import java.util.ArrayList;

public class myadaptorfornote extends RecyclerView.Adapter<myadaptorfornote.myviewholder> implements Filterable {
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
        aes.initFromStrings(sharedPreferences.getString(LogInActivity.AES_KEY,null),sharedPreferences.getString(LogInActivity.AES_IV,null));
        int p = holder.getAdapterPosition();
        String tv_date, tv_title, tv_note,tv_title_dc, tv_note_dc;
        boolean cb_hide_note;
        try {
            tv_date = dataholder.get(position).getDate();
            cb_hide_note = dataholder.get(position).isHide_note();
            tv_title = dataholder.get(position).getTitle();
            tv_note = dataholder.get(position).getNote();
            tv_title_dc = aes.decrypt(tv_title);
            tv_note_dc = aes.decrypt(tv_note);
            holder.tv_date.setText(tv_date);
            holder.tv_title.setText(tv_title_dc);
            holder.tv_note.setText(tv_note_dc);

            holder.LLCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, addNotesActivity.class);
                    intent.putExtra("request_code","notesCardView");
                    intent.putExtra("date", tv_date);
                    intent.putExtra("hide note",cb_hide_note);
                    intent.putExtra("title", tv_title_dc);
                    intent.putExtra("note", tv_note_dc);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_down, 0);
                }
            });
            holder.cardview_more.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.img_copy_note:
                            ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData clipData = ClipData.newPlainText("Copy_Password", tv_note_dc);
                            clipboardManager.setPrimaryClip(clipData);
                            Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show();
                            return true;
                        case R.id.img_delete:
                            notesActivity.reference.child(tv_date).removeValue();
                            Toast.makeText(context,"Deleted !!", Toast.LENGTH_SHORT).show();
                            notesActivity.adaptor.notifyDataSetChanged();
                            activity.finish();
                            activity.overridePendingTransition(0, 0);
                            activity.startActivity(new Intent(context, notesActivity.class));
                            activity.overridePendingTransition(0, 0);
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

            if (charSequence == null || charSequence.length() == 0){
                filteredDataList.addAll(dataholder);
            }else {
                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (addDNoteHelperClass addDNoteHelperClass : dataholder){
                    try {
                        String a1 = aes.decrypt(addDNoteHelperClass.getTitle());
                        if (a1.toLowerCase().contains(filterPattern)){
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
            dataholderfilter.addAll((ArrayList)filterResults.values);
            notifyDataSetChanged();
        }
    };

    public static class myviewholder extends RecyclerView.ViewHolder {

        final TextView tv_date;
        final TextView tv_title;
        final TextView tv_note;
        final Toolbar cardview_more;
        final LinearLayout LLCard;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_note = itemView.findViewById(R.id.tv_note);
            cardview_more = itemView.findViewById(R.id.cardview_more);
            LLCard = itemView.findViewById(R.id.lenear_layout_card);
        }

    }
}
