package com.krce.mobile.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krce.mobile.R;
import com.krce.mobile.adapter.PageAdapter;
import com.krce.mobile.api.ApiClient;
import com.krce.mobile.model.SitePage;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExploreFragment extends Fragment {
    private PageAdapter adapter;
    private ProgressBar progress;
    private TextView empty;

    public ExploreFragment() { super(R.layout.fragment_explore); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView list = view.findViewById(R.id.pagesList);
        EditText search = view.findViewById(R.id.searchInput);
        Button searchButton = view.findViewById(R.id.searchButton);
        progress = view.findViewById(R.id.exploreProgress);
        empty = view.findViewById(R.id.emptyText);

        adapter = new PageAdapter();
        list.setLayoutManager(new LinearLayoutManager(requireContext()));
        list.setAdapter(adapter);

        loadPages();
        searchButton.setOnClickListener(v -> {
            String q = search.getText().toString().trim();
            if (q.isBlank()) loadPages(); else searchPages(q);
        });
    }

    private void loadPages() {
        progress.setVisibility(View.VISIBLE);
        empty.setVisibility(View.GONE);
        ApiClient.service().getPages().enqueue(new Callback<List<SitePage>>() {
            @Override public void onResponse(Call<List<SitePage>> call, Response<List<SitePage>> response) {
                progress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setItems(response.body());
                    empty.setVisibility(response.body().isEmpty() ? View.VISIBLE : View.GONE);
                } else showError();
            }
            @Override public void onFailure(Call<List<SitePage>> call, Throwable t) { progress.setVisibility(View.GONE); showError(); }
        });
    }

    private void searchPages(String query) {
        progress.setVisibility(View.VISIBLE);
        ApiClient.service().search(query).enqueue(new Callback<List<SitePage>>() {
            @Override public void onResponse(Call<List<SitePage>> call, Response<List<SitePage>> response) {
                progress.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) adapter.setItems(response.body());
                else Toast.makeText(requireContext(), "Search failed", Toast.LENGTH_SHORT).show();
            }
            @Override public void onFailure(Call<List<SitePage>> call, Throwable t) { progress.setVisibility(View.GONE); Toast.makeText(requireContext(), "Backend unavailable", Toast.LENGTH_SHORT).show(); }
        });
    }

    private void showError() {
        empty.setText("No live pages yet. Open Home and tap Sync Website.");
        empty.setVisibility(View.VISIBLE);
    }
}
