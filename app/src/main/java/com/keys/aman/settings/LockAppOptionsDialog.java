package com.keys.aman.settings;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.keys.R;

public class LockAppOptionsDialog extends DialogFragment  {

    RadioGroup rgLockAppOptions;
    RadioButton rbImmediately, rbAfterOneMinute, rbNever;
//    private OnInputListener onInputListner;
    Activity activity;
    Context context;
    SettingFragment settingFragment = new SettingFragment();

    public LockAppOptionsDialog(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

//    public interface OnInputListener{
//        public void onSendResult(RadioGroup radioGroup ,int iResult);
//    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_lock_app_options,container,false);

        rgLockAppOptions =  view.findViewById(R.id.rg_lock_app_options);
        rbImmediately = view.findViewById(R.id.rb_immediate);
        rbAfterOneMinute = view.findViewById(R.id.rb_after_one_minute);
        rbNever = view.findViewById(R.id.rb_never);

//        try {
//            onInputListner = (OnInputListener) getParentFragment();
//        }catch (ClassCastException e){
//            throw new ClassCastException("Calling Fragment must implement OnInputListener");
//        }



        return view;
    }


}
