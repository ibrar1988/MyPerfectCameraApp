package com.techv.camera1example;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class CameraSettingDialog extends DialogFragment implements View.OnClickListener {

    private MainActivity mActivity;
    private View mFragment;
    private int window_width = 0;
    private int window_heigth = 0;

    public CameraSettingDialog(){}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getActivity();
        Bundle bundle = getArguments();
        if(bundle!=null) {
            String w = bundle.getString("window_width");
            String h = bundle.getString("window_heigth");
            if(w!=null && h!=null) {
                window_width = Integer.parseInt(w);
                window_heigth = Integer.parseInt(h);
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragment = inflater.inflate(R.layout.fragment_camera_setting,container,false);
        mFragment.findViewById(R.id.btn_close_dialog).setOnClickListener(this);
        handleWhiteBalance();
        handleFocusMode();
        handleColorEffect();
        handleSceneMode();
        return mFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if(dialog!=null) {
            dialog.setCancelable(true);
            Window window = dialog.getWindow();
            if(window!=null) {
                window.setLayout(window_width, window_heigth);
                window.setBackgroundDrawableResource(android.R.color.transparent);
            } else {

            }
        } else {

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mActivity = (MainActivity) getActivity();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mActivity.showControlUI();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close_dialog:
                dismiss();
                break;
        }
    }

    private void handleWhiteBalance(){
        final String item = LocalStorage.getStringPreference(mActivity,Constants.kDefault_White_Balance, "");
        int position;
        Spinner spinner_white_balance =  ((Spinner)mFragment.findViewById(R.id.spinner_white_balance));
        spinner_white_balance.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)parent.getChildAt(0)).setTextColor(Color.WHITE);
                String selected_item = (String) parent.getSelectedItem();
                if(!selected_item.equalsIgnoreCase(item)) {
                    mActivity.setWhiteBalance(selected_item);
                    LocalStorage.saveStringPreference(mActivity, Constants.kDefault_White_Balance, selected_item);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter arrayAdapter = (ArrayAdapter) spinner_white_balance.getAdapter();
        if(item.equals("")) {
            position = arrayAdapter.getPosition(Constants.kFluorescent_White_Balance);
            spinner_white_balance.setSelection(position);
        } else {
            position = arrayAdapter.getPosition(item);
            spinner_white_balance.setSelection(position);
        }
    }

    private void handleFocusMode(){
        final String item = LocalStorage.getStringPreference(mActivity,Constants.kDefault_Focus_Mode, "");
        int position;
        Spinner spinner_focus_mode =  ((Spinner)mFragment.findViewById(R.id.spinner_focus_mode));
        spinner_focus_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)parent.getChildAt(0)).setTextColor(Color.WHITE);
                String selected_item = (String) parent.getSelectedItem();
                if(!selected_item.equalsIgnoreCase(item)) {
                    mActivity.setFocusMode(selected_item);
                    LocalStorage.saveStringPreference(mActivity, Constants.kDefault_Focus_Mode, selected_item);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter arrayAdapter = (ArrayAdapter) spinner_focus_mode.getAdapter();
        if(item.equals("")) {
            position = arrayAdapter.getPosition(Constants.kDefault_Focus_Mode);
            spinner_focus_mode.setSelection(position);
        } else {
            position = arrayAdapter.getPosition(item);
            spinner_focus_mode.setSelection(position);
        }
    }

    private void handleColorEffect() {
        final String item = LocalStorage.getStringPreference(mActivity,Constants.kDefault_Color, "");
        int position;
        Spinner spinner_color_effect =  ((Spinner)mFragment.findViewById(R.id.spinner_color_effect));
        spinner_color_effect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)parent.getChildAt(0)).setTextColor(Color.WHITE);
                String selected_item = (String) parent.getSelectedItem();
                if(!selected_item.equalsIgnoreCase(item)) {
                    mActivity.setColorEffect(selected_item);
                    LocalStorage.saveStringPreference(mActivity, Constants.kDefault_Color, selected_item);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter arrayAdapter = (ArrayAdapter) spinner_color_effect.getAdapter();
        if(item.equals("")) {
            position = arrayAdapter.getPosition(Constants.kDefault_Color);
            spinner_color_effect.setSelection(position);
        } else {
            position = arrayAdapter.getPosition(item);
            spinner_color_effect.setSelection(position);
        }
    }

    private void handleSceneMode(){
        final String item = LocalStorage.getStringPreference(mActivity,Constants.kDefault_Scene, "");
        int position;
        Spinner spinner_scene_mode =  ((Spinner)mFragment.findViewById(R.id.spinner_scene_mode));
        spinner_scene_mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)parent.getChildAt(0)).setTextColor(Color.WHITE);
                String selected_item = (String) parent.getSelectedItem();
                if(!selected_item.equalsIgnoreCase(item)) {
                    mActivity.setSceneMode(selected_item);
                    LocalStorage.saveStringPreference(mActivity, Constants.kDefault_Scene, selected_item);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter arrayAdapter = (ArrayAdapter) spinner_scene_mode.getAdapter();
        if(item.equals("")) {
            position = arrayAdapter.getPosition(Constants.kDefault_Color);
            spinner_scene_mode.setSelection(position);
        } else {
            position = arrayAdapter.getPosition(item);
            spinner_scene_mode.setSelection(position);
        }

    }
}
