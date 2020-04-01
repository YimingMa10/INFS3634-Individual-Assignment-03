package com.example.assignment03.Adapter;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.assignment03.Model.Result;
import com.example.assignment03.R;
import com.example.assignment03.StaticResource;

import java.util.List;

// Recycler view adapter for search by keyword fact
public class KeywordFactAdapter extends RecyclerView.Adapter<KeywordFactAdapter.KeywordFactViewHolder> {

    private List<Result> keywordFacts;

    // Set up recycler view data
    public void setData(List<Result> keywordFacts) {
        this.keywordFacts = keywordFacts;
    }

    // Declare components in the view
    public class KeywordFactViewHolder extends RecyclerView.ViewHolder {
        public View v;
        public TextView value;
        public ImageView iconSmall;
        public TextView updateTime;
        public TextView url;
        public TextView category;

        // Find components by Id
        public KeywordFactViewHolder(@NonNull View itemView) {
            super(itemView);
            v = itemView;
            value = v.findViewById(R.id.value);
            iconSmall = v.findViewById(R.id.iconSmall);
            updateTime = v.findViewById(R.id.updateTime);
            url = v.findViewById(R.id.url);
            category = v.findViewById(R.id.category);
        }
    }

    // Setup layout inflate
    @NonNull
    @Override
    public KeywordFactAdapter.KeywordFactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.content_recycle_view, parent, false);

        KeywordFactViewHolder keywordFactViewHolder = new KeywordFactViewHolder(view);
        return keywordFactViewHolder;
    }

    // Setup data for all components
    @Override
    public void onBindViewHolder(@NonNull final KeywordFactAdapter.KeywordFactViewHolder holder, int position) {
        // Get the instance for current position
        final Result result = keywordFacts.get(position);

        // Setup and format Category String
        holder.category.setText("\"" + StaticResource.formatCategory(result.getCategories()) + "\"");

        // Setup content text
        holder.value.setText(result.getValue());

        // Setup icon
        holder.iconSmall.setVisibility(View.VISIBLE);
        Glide.with(holder.v.getContext()).load(result.getIconUrl()).into(holder.iconSmall);

        // Setup last updated time
        int index = result.getUpdatedAt().indexOf(".");
        holder.updateTime.setText(result.getUpdatedAt().substring(0, index));

        // Setup url link implicit intent
        holder.url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(result.getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                holder.v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return keywordFacts.size();
    }
}
