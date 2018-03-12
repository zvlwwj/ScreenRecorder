package com.zou.screenrecorder.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.zou.screenrecorder.R;
import com.zou.screenrecorder.activity.GuideActivity;
import com.zou.screenrecorder.activity.MainActivity;
import com.zou.screenrecorder.utils.Constant;

/**
 * Created by zou on 2018/3/12.
 */

public class GuideThreeFragment extends Fragment {
    private Button btn_in_app;
    private SharedPreferences sp;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_guide_three,null);
        btn_in_app = view.findViewById(R.id.btn_in_app);
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        btn_in_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sp.edit().putBoolean(Constant.KEY_FIRST_IN_APP,false).apply();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        return view;
    }
}
