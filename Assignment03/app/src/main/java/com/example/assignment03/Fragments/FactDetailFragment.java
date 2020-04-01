package com.example.assignment03.Fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.assignment03.Model.Result;
import com.example.assignment03.R;
import com.example.assignment03.StaticResource;

public class FactDetailFragment extends Fragment {

    // Declare components
    private ImageView icon;
    private TextView value;
    private TextView category;
    private Button btnNextFact;
    private TextView currentCategorty;
    private TextView resetCategorty;
    private TextView url;
    private TextView updateTime;
    private TextView notice;

    public FactDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_fact_detail, container, false);

        // Get the current instance found
        final Result result = StaticResource.currentCategoryFact;

        // Get the components by id
        icon = view.findViewById(R.id.icon);
        value = view.findViewById(R.id.value);
        category = view.findViewById(R.id.category);
        btnNextFact = view.findViewById(R.id.btnNextFact);
        notice = view.findViewById(R.id.notice);
        currentCategorty = view.findViewById(R.id.currentCategory);
        resetCategorty = view.findViewById(R.id.resetCategory);
        url = view.findViewById(R.id.url);
        updateTime = view.findViewById(R.id.updateTime);

        // Setup and format category
        category.setText("\"" + StaticResource.formatCategory(result.getCategories()) + "\"");

        // Setup Icon
        Glide.with(this).load(result.getIconUrl()).into(icon);
        value.setText(result.getValue());

        // Setup current category choose
        currentCategorty.setText("Chosen Category: " + StaticResource.currentCategory);

        // Setup last updated time
        int index = result.getUpdatedAt().indexOf(".");
        updateTime.setText(result.getUpdatedAt().substring(0, index));

        // Click text to back to main fragment to reset category
        resetCategorty.setText("Click Here to Reset Category");
        resetCategorty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticResource.swapFragment(new MainFragment(), getFragmentManager());
            }
        });

        // Setup url link implicit intent
        url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(result.getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                view.getContext().startActivity(intent);
            }
        });

        // Setup button to get next fact in same category
        btnNextFact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Set the loading to true
                StaticResource.loading = true;

                // Disable other button or input text in the fragment
                btnNextFact.setClickable(false);

                // Show user the app is loading
                notice.setVisibility(View.VISIBLE);
                notice.setText("Loading.");
                Thread thread = new Thread(){
                    @Override
                    public void run(){
                        try {
                            while (StaticResource.loading) {
                                Thread.sleep(500);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(notice.getText().equals("Loading.")){
                                            notice.setText("Loading..");
                                        } else if(notice.getText().equals("Loading..")){
                                            notice.setText("Loading...");
                                        } else if(notice.getText().equals("Loading...")){
                                            notice.setText("Loading.");
                                        }
                                    }
                                });
                            }
                            // Enable the button again after loading
                            btnNextFact.setClickable(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();

                // Run the send request method again
                StaticResource.sendRequest(StaticResource.currentCategory, getContext(), getFragmentManager(), R.id.optionCategory);
            }
        });

        return view;
    }
}
