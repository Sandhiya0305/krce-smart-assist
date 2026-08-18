package com.krce.mobile.adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.krce.mobile.R;
import com.krce.mobile.model.SitePage;

import java.util.ArrayList;
import java.util.List;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.PageViewHolder> {
    private final List<SitePage> items = new ArrayList<>();

    public void setItems(List<SitePage> pages) {
        items.clear();
        if (pages != null) items.addAll(pages);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_page, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        SitePage page = items.get(position);
        holder.title.setText(page.title == null || page.title.isBlank() ? "KRCE page" : page.title);
        holder.description.setText(page.description == null || page.description.isBlank()
                ? trim(page.content, 150) : trim(page.description, 150));

        if (page.imageUrl != null && !page.imageUrl.isBlank()) {
            holder.image.setVisibility(View.VISIBLE);
            Glide.with(holder.image).load(page.imageUrl).centerCrop().into(holder.image);
        } else {
            holder.image.setVisibility(View.GONE);
        }

        holder.open.setOnClickListener(v -> {
            String url = page.canonicalUrl == null || page.canonicalUrl.isBlank() ? page.url : page.canonicalUrl;
            if (url != null && !url.isBlank()) {
                v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
            }
        });
    }

    private String trim(String text, int max) {
        if (text == null) return "";
        String clean = text.replaceAll("\\s+", " ").trim();
        return clean.length() <= max ? clean : clean.substring(0, max).trim() + "…";
    }

    @Override public int getItemCount() { return items.size(); }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        ImageView image; TextView title; TextView description; Button open;
        PageViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.pageImage);
            title = itemView.findViewById(R.id.pageTitle);
            description = itemView.findViewById(R.id.pageDescription);
            open = itemView.findViewById(R.id.pageOpen);
        }
    }
}
