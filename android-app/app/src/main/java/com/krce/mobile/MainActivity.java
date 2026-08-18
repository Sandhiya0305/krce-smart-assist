package com.krce.mobile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.krce.mobile.ui.ChatbotFragment;
import com.krce.mobile.ui.ContactFragment;
import com.krce.mobile.ui.ExploreFragment;
import com.krce.mobile.ui.HomeFragment;

public class MainActivity extends AppCompatActivity {
    private FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container = findViewById(R.id.fragmentContainer);
        setupNavigation();
        if (savedInstanceState == null) show(new HomeFragment());
    }

    private void setupNavigation() {
        Button home = findViewById(R.id.navHome);
        Button explore = findViewById(R.id.navExplore);
        Button bot = findViewById(R.id.navBot);
        Button contact = findViewById(R.id.navContact);

        home.setOnClickListener(v -> show(new HomeFragment()));
        explore.setOnClickListener(v -> show(new ExploreFragment()));
        bot.setOnClickListener(v -> show(new ChatbotFragment()));
        contact.setOnClickListener(v -> show(new ContactFragment()));
    }

    private void show(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
