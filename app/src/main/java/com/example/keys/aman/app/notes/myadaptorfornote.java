package com.example.keys.aman.app.notes;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keys.aman.app.AES;
import com.example.keys.R;

import java.util.ArrayList;

public class myadaptorfornote extends RecyclerView.Adapter<myadaptorfornote.myviewholder> {
    final ArrayList<addDNoteHelperClass> dataholder;
    final Context context;

    public myadaptorfornote(ArrayList<addDNoteHelperClass> dataholder, Context context) {
        this.dataholder = dataholder;
        this.context = context;
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_cardview_layout, parent, false);
        return new myviewholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {
        AES aes = new AES();
        aes.initFromStrings("CHuO1Fjd8YgJqTyapibFBQ==", "e3IYYJC2hxe24/EO");
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
                }
            });
            holder.img_copy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("Copy_Password", tv_note_dc);
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show();
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

    public static class myviewholder extends RecyclerView.ViewHolder {

        final TextView tv_date;
        final TextView tv_title;
        final TextView tv_note;
        final ImageView img_copy;
        final LinearLayout LLCard;

        public myviewholder(@NonNull View itemView) {
            super(itemView);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_title = itemView.findViewById(R.id.tv_title);
            tv_note = itemView.findViewById(R.id.tv_note);
            img_copy = itemView.findViewById(R.id.img_copy);
            LLCard = itemView.findViewById(R.id.lenear_layout_card);
        }

    }
}
