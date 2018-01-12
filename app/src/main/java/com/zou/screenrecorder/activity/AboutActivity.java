package com.zou.screenrecorder.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zou.screenrecorder.R;
import com.zou.screenrecorder.utils.Constant;

/**
 * Created by zou on 2018/1/10.
 */

public class AboutActivity extends AppCompatCommonActivity implements View.OnClickListener{
    //TODO 加点动画
    private LinearLayout ll_card_about_2_email,ll_card_about_2_git_hub,ll_card_about_2_location;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setContentView(R.layout.activity_about);
        ll_card_about_2_email = findViewById(R.id.ll_card_about_2_email);
        ll_card_about_2_email.setOnClickListener(this);
        ll_card_about_2_git_hub = findViewById(R.id.ll_card_about_2_git_hub);
        ll_card_about_2_git_hub.setOnClickListener(this);
        ll_card_about_2_location = findViewById(R.id.ll_card_about_2_location);
        ll_card_about_2_location.setOnClickListener(this);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                finish();
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.ll_card_about_2_email:
                intent.setAction(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse(Constant.EMAIL));
                intent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.about_email_intent));
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(AboutActivity.this, getString(R.string.about_not_found_email), Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.ll_card_about_2_git_hub:
                intent.setData(Uri.parse(Constant.GITHUB));
                intent.setAction(Intent.ACTION_VIEW);
                startActivity(intent);
                break;
            case R.id.ll_card_about_2_location:

                break;
        }
    }
}
