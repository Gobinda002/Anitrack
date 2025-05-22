package com.example.anitrack;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

public class HomeActivity extends AppCompatActivity {

    Button btnHome, btnCompleted, btnTopAiring, btnProfile;
    OngoingFragment homeFragment = new OngoingFragment();
    CompletedFragment completedFragment = new CompletedFragment();  // Keep this instance
    TopairingFragment topairingFragment = new TopairingFragment();
    ProfileFragment profileFragment = new ProfileFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        btnHome = findViewById(R.id.btn_home);
        btnCompleted = findViewById(R.id.btn_completed);
        btnTopAiring = findViewById(R.id.btn_top_airing);
        btnProfile = findViewById(R.id.btn_profile);

        // Show default fragment (ongoing list)
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, homeFragment)
                .commit();

        btnHome.setOnClickListener(v -> switchFragment(homeFragment));
        btnCompleted.setOnClickListener(v -> switchFragment(completedFragment));
        btnTopAiring.setOnClickListener(v -> switchFragment(topairingFragment));
        btnProfile.setOnClickListener(v -> switchFragment(profileFragment));
    }

    private void switchFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    // This method is called by OngoingFragment to add anime to CompletedFragment
    public void addAnimeToCompleted(String animeName) {
        completedFragment.addAnimeExternally(animeName);
    }
}
