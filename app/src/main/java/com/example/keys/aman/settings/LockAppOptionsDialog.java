package com.example.keys.aman.settings;

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

public class LockAppOptionsDialog extends DialogFragment {

    RadioGroup rgLockAppOptions;
    RadioButton rbImmediately, rbAfterOneMinute, rbNever;
    private String input;

    public interface onInputListener{
        void sendInput(String input);
    }
    public onInputListener mOnInputListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_lock_app_options,container,false);

        rgLockAppOptions =  view.findViewById(R.id.rg_lock_app_options);
        rbImmediately = view.findViewById(R.id.rb_immediate);
        rbAfterOneMinute = view.findViewById(R.id.rb_after_one_minute);
        rbNever = view.findViewById(R.id.rb_never);

        rgLockAppOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                input = Integer.toString(i);
                mOnInputListener.sendInput(input);
                getDialog().dismiss();
            }
        });

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mOnInputListener = (onInputListener) getContext();
        }catch (ClassCastException e){
            e.printStackTrace();
        }
    }
}
