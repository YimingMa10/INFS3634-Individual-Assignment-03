package com.example.assignment03.Fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.assignment03.R;
import com.example.assignment03.StaticResource;

public class MainFragment extends Fragment {

    // Declare components
    private static Spinner category;
    private Button btnSubmit;
    private RadioGroup radioGroup;
    private ConstraintLayout layoutCategory;
    private ConstraintLayout layoutKeyword;
    private EditText inputKeyword;
    private TextView notice;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // Get the components by id
        category = view.findViewById(R.id.spinner);
        radioGroup = view.findViewById(R.id.radioGroup);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        notice = view.findViewById(R.id.notice);

        layoutCategory = view.findViewById(R.id.layoutCategory);
        layoutKeyword = view.findViewById(R.id.layoutKeyword);

        inputKeyword = view.findViewById(R.id.inputKeyword);
        layoutKeyword.setVisibility(View.GONE);

        // Setup Spinner Category list
        if(StaticResource.categorySelection.size() < 1){
            StaticResource.categorySelection.add("Random");
            StaticResource.getCategory(getContext());

            // Notice for category loading delay
            notice.setVisibility(View.VISIBLE);
            notice.setText("Categories might take few second to loaded.\nThanks for patient~ ❤");
            notice.postDelayed(new Runnable() {
                @Override
                public void run() {
                    notice.setVisibility(View.GONE);
                }
            }, 3000);
        }
        setupSpinnerAdapter(getContext());

        // Setup radio group function to choose between option: Category / Keyword
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                if(checkedId == R.id.optionCategory){
                    layoutCategory.setVisibility(View.VISIBLE);
                    layoutKeyword.setVisibility(View.GONE);
                } else if(checkedId == R.id.optionKeyword){
                    layoutCategory.setVisibility(View.GONE);
                    layoutKeyword.setVisibility(View.VISIBLE);
                }
            }
        });

        // Setup submit button function
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Input Check: true = valid; false = invalid
                boolean inputCheck = true;

                // Get the type of option user choose by radio group id
                int selectedId = radioGroup.getCheckedRadioButtonId();

                // If category chosen
                if(selectedId == R.id.optionCategory){
                    // Get and update current category
                    String categorySelected = category.getSelectedItem().toString();
                    StaticResource.currentCategory = categorySelected;

                    // If category selected equal to empty, set input valid check to false
                    if(categorySelected.equals("")){
                        inputCheck = false;

                        // Show user notice
                        notice.setVisibility(View.VISIBLE);
                        notice.setText("Invalid Category! Please reselect category~ ❤");
                        notice.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                notice.setVisibility(View.GONE);
                            }
                        }, 3000);
                    }

                    // Send request to get fact by category
                    StaticResource.sendRequest(categorySelected, getContext(), getFragmentManager(), selectedId);

                // If keyword chosen
                } else if(selectedId == R.id.optionKeyword){

                    // Get the keyword from EditText component
                    // remove all space at the back of input keyword
                    final String keyword = String.valueOf(inputKeyword.getText()).replaceAll("\\s+$", "");

                    // Check the length of keyword (Minimum 3 character
                    // If not valid, show notice to user
                    if(keyword.length() < 3){
                        // set input false
                        inputCheck = false;

                        // Show user notice
                        notice.setVisibility(View.VISIBLE);
                        notice.setText(getString(R.string.keywordNotice));
                        notice.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                notice.setVisibility(View.GONE);
                            }
                        }, 3000);

                    // If input keyword valid
                    } else {
                        // Update current keyword
                        StaticResource.currentKeyword = keyword;

                        // Send request to get facts by keyword
                        StaticResource.sendRequest(keyword, getContext(), getFragmentManager(), selectedId);
                    }
                }

                // If everything is valid and request sent
                if(inputCheck){
                    // Update loading to true
                    StaticResource.loading = true;

                    // Disable all button and function in the fragment
                    btnSubmit.setClickable(false);
                    inputKeyword.setEnabled(false);
                    inputKeyword.setFocusable(false);
                    inputKeyword.setActivated(false);
                    category.setEnabled(false);

                    for (int i = 0; i < radioGroup.getChildCount(); i++) {
                        if(radioGroup.getChildAt(i).getId() != radioGroup.getCheckedRadioButtonId()){
                            radioGroup.getChildAt(i).setEnabled(false);
                        }
                    }

                    // Show loading notice to user
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

                                // Enable the button and function again
                                btnSubmit.setClickable(true);
                                inputKeyword.setEnabled(true);
                                inputKeyword.setFocusable(true);
                                inputKeyword.setActivated(true);
                                category.setEnabled(true);

                                for (int i = 0; i < radioGroup.getChildCount(); i++) {
                                    radioGroup.getChildAt(i).setEnabled(true);
                                }

                                notice.setVisibility(View.GONE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    thread.start();

                    // Show response to user
                    Toast.makeText(getContext(),"Submitted. Be Patient~ ❤", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    // Setup spinner adapter method
    public static void setupSpinnerAdapter(Context context){
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, StaticResource.categorySelection);
        category.setAdapter(categoryAdapter);
    }
}
