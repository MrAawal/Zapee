package com.flinkmart.mahi.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.flinkmart.mahi.R;
import com.flinkmart.mahi.databinding.ActivityChatBinding;
import com.flinkmart.mahi.databinding.ActivityPrivacyBinding;
import com.flinkmart.mahi.utils.AndroidUtil;
import com.flinkmart.mahi.utils.Constants;

public class PrivacyActivity extends AppCompatActivity {
    ActivityPrivacyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        binding= ActivityPrivacyBinding.inflate (getLayoutInflater ());
        setContentView (binding.getRoot ());

        binding.view.setMixedContentAllowed(true);
        binding.view.loadUrl("https://www.flink-mart.com/privacypolicy.php");

        getSupportActionBar ().setDisplayHomeAsUpEnabled (true);

    }
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

}
