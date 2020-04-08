package com.example.assignment03.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignment03.Adapter.KeywordFactAdapter;
import com.example.assignment03.Model.KeywordResponse;
import com.example.assignment03.R;
import com.example.assignment03.StaticResource;


public class KeywordFactDetailFragment extends Fragment {

    // Declare components
    private TextView total;
    private Button btnSubmit;
    private EditText keyword;
    private RecyclerView recyclerView;
    private final KeywordFactAdapter keywordFactAdapter = new KeywordFactAdapter();
    private TextView keywordNotice;
    private TextView resetSearch;

    private KeywordResponse keywordResponse;
    private String currentKeyword;

    public KeywordFactDetailFragment(KeywordResponse keywordResponse, String currentKeyword) {
        this.keywordResponse = keywordResponse;
        this.currentKeyword = currentKeyword;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_keyword_fact_detail, container, false);

        // Get the components by id
        total = view.findViewById(R.id.total);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        keyword = view.findViewById(R.id.keyword);
        recyclerView = view.findViewById(R.id.rv_list);
        keywordNotice = view.findViewById(R.id.notice);
        resetSearch = view.findViewById(R.id.resetSearch);

        // Setup recycler view to display the list of facts get by keyword
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        keywordFactAdapter.setData(keywordResponse.getResult());
        recyclerView.setAdapter(keywordFactAdapter);

        // Setup the keyword inserted in EditText component
        keyword.setText(currentKeyword);

        // Show the facts found
        total.setText("Number of Facts Found: " + keywordResponse.getTotal());

        // Click text to back to main fragment
        resetSearch.setText("Search by Category?");
        resetSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StaticResource.swapFragment(new MainFragment(), getFragmentManager());
            }
        });

        // Setup the button to research the keyword input
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the keyword from EditText component
                // remove all space at the back of input keyword
                String newKeyword = String.valueOf(keyword.getText()).replaceAll("\\s+$", "");

                // Check the length of keyword (Minimum 3 character
                // If not valid, show notice to user
                if(newKeyword.length() < 3){
                    keywordNotice.setVisibility(View.VISIBLE);
                    keywordNotice.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            keywordNotice.setVisibility(View.GONE);
                        }
                    }, 3000);
                    // else if valid
                } else {
                    // Set Loading to true
                    StaticResource.loading = true;

                    // Disable all button and function in the fragments
                    btnSubmit.setClickable(false);
                    keyword.setEnabled(false);
                    keyword.setFocusable(false);
                    keyword.setActivated(false);

                    // Show user the app is loading
                    keywordNotice.setVisibility(View.VISIBLE);
                    keywordNotice.setText("Loading.");
                    Thread thread = new Thread(){
                        @Override
                        public void run(){
                            try {
                                while (StaticResource.loading) {
                                    Thread.sleep(500);
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(keywordNotice.getText().equals("Loading.")){
                                                keywordNotice.setText("Loading..");
                                            } else if(keywordNotice.getText().equals("Loading..")){
                                                keywordNotice.setText("Loading...");
                                            } else if(keywordNotice.getText().equals("Loading...")){
                                                keywordNotice.setText("Loading.");
                                            }
                                        }
                                    });
                                }
                                // Enable the button and function in the fragment after loading
                                btnSubmit.setClickable(true);
                                keyword.setEnabled(true);
                                keyword.setFocusable(true);
                                keyword.setActivated(true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    thread.start();

                    // Send the request again to get new facts for the keyword
                    StaticResource.sendRequest(StaticResource.service, newKeyword, getContext(), getFragmentManager(), R.id.optionKeyword);

                    // Toast some text to give user some response
                    Toast.makeText(getContext(),"Submitted. Be Patient~ ❤", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}
