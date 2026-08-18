package com.krce.mobile.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.krce.mobile.R;
import com.krce.mobile.api.ApiClient;
import com.krce.mobile.api.KrceApi;
import com.krce.mobile.model.SitePage;

import java.util.List;

import android.widget.ImageView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    public HomeFragment() { super(R.layout.fragment_home); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView hero = view.findViewById(R.id.heroImage);
        TextView status = view.findViewById(R.id.syncStatus);
        Button sync = view.findViewById(R.id.syncButton);
        Button official = view.findViewById(R.id.officialButton);

        // The scraper will replace this with the current page image when the backend is synced.
        Glide.with(this)
                .load("https://www.krce.ac.in/")
                .placeholder(R.drawable.bg_hero)
                .error(R.drawable.bg_hero)
                .into(hero);

        official.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.krce.ac.in/"))));

        sync.setOnClickListener(v -> {
            sync.setEnabled(false);
            status.setText("Syncing official KRCE website…");
            ApiClient.service().sync().enqueue(new Callback<KrceApi.SyncResponse>() {
                @Override public void onResponse(Call<KrceApi.SyncResponse> call, Response<KrceApi.SyncResponse> response) {
                    sync.setEnabled(true);
                    status.setText(response.isSuccessful() && response.body() != null
                            ? "Live sync complete • " + response.body().saved + " pages cached"
                            : "Sync failed. Check that the backend is running.");
                    loadHero(hero);
                }
                @Override public void onFailure(Call<KrceApi.SyncResponse> call, Throwable t) {
                    sync.setEnabled(true);
                    status.setText("Backend unavailable • run Spring Boot first");
                }
            });
        });

        loadHero(hero);
    }

    private void loadHero(ImageView hero) {
        ApiClient.service().getPages().enqueue(new Callback<List<SitePage>>() {
            @Override public void onResponse(Call<List<SitePage>> call, Response<List<SitePage>> response) {
                if (!response.isSuccessful() || response.body() == null) return;
                for (SitePage page : response.body()) {
                    if (page.imageUrl != null && !page.imageUrl.isBlank()) {
                        Glide.with(HomeFragment.this).load(page.imageUrl).centerCrop().into(hero);
                        break;
                    }
                }
            }
            @Override public void onFailure(Call<List<SitePage>> call, Throwable t) { }
        });
    }
}
