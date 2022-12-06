package com.example.keys.aman.messages;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keys.R;
import com.example.keys.aman.signin_login.LogInActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class ChatAdaptor extends RecyclerView.Adapter{

    private SharedPreferences sharedPreferences;
    ArrayList<ChatModelClass> dataHolder;
    Context context;
    Activity activity;
    LogInActivity logInActivity = new LogInActivity();

    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;

    public ChatAdaptor() {
    }

    public ChatAdaptor(ArrayList<ChatModelClass> dataHolder, Context context, Activity activity) {
        this.dataHolder = dataHolder;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE){
            View view = LayoutInflater.from(context).inflate(R.layout.layout_sender_message,parent,false);
            return new SenderViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_receiver_message,parent,false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        sharedPreferences = activity.getSharedPreferences(logInActivity.getSHARED_PREF_ALL_DATA(), MODE_PRIVATE);
        if (dataHolder.get(position).getPublicUid().equals(sharedPreferences.getString(logInActivity.PUBLIC_UID,null))){
            return SENDER_VIEW_TYPE;
        }
        return RECEIVER_VIEW_TYPE;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder.getClass() == SenderViewHolder.class) {
                ((SenderViewHolder) holder).tvSenderMessage.setText(dataHolder.get(position).getMessage());
                DateFormat sdf = new SimpleDateFormat("hh:mm", Locale.getDefault());
                DateFormat inputsdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                String dateAndTime1 = sdf.format(Objects.requireNonNull(inputsdf.parse(dataHolder.get(position).getDateAndTime())));
                ((SenderViewHolder) holder).tvSenderTimeStamp.setText(dateAndTime1);
            } else {
                ((ReceiverViewHolder) holder).tvReceiverMessage.setText(dataHolder.get(position).getMessage());
                DateFormat sdf = new SimpleDateFormat("hh:mm", Locale.getDefault());
                DateFormat inputsdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                String dateAndTime1 = sdf.format(Objects.requireNonNull(inputsdf.parse(dataHolder.get(position).getDateAndTime())));
                ((ReceiverViewHolder) holder).tvReceiverTimeStamp.setText(dateAndTime1);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return dataHolder.size();
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder{

        TextView tvReceiverMessage, tvReceiverTimeStamp;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReceiverMessage = itemView.findViewById(R.id.tv_receiver_message);
            tvReceiverTimeStamp = itemView.findViewById(R.id.tv_receiver_time_stamp);
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder{

        TextView tvSenderMessage, tvSenderTimeStamp, tvSenderMessageStatus;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSenderMessage = itemView.findViewById(R.id.tv_sender_message);
            tvSenderTimeStamp = itemView.findViewById(R.id.tv_sender_time_stamp);
            tvSenderMessageStatus = itemView.findViewById(R.id.tv_sender_message_status);
        }
    }

}
