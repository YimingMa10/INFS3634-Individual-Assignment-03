package com.example.assignment03;

import android.content.Context;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.assignment03.Fragments.FactDetailFragment;
import com.example.assignment03.Fragments.KeywordFactDetailFragment;
import com.example.assignment03.Fragments.MainFragment;
import com.example.assignment03.Model.KeywordResponse;
import com.example.assignment03.Model.Result;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

// This class is use to record static resource such as variable and method
public class StaticResource {
    // Record category list
    public static ArrayList<String> categorySelection = new ArrayList<String>();

    // Record current search's category and fact found
    public static String currentCategory;
    public static Result currentCategoryFact;

    // Record current search's keyword and facts found
    public static String currentKeyword;
    public static KeywordResponse currentKeywordFacts;

    // Record if the system is loading
    // True = loading
    // False = not loading
    public static boolean loading = false;

    // Method to send request and get category from chucknorris.io
    public static void getCategory(final Context context){
        final RequestQueue requestQueue =  Volley.newRequestQueue(context);
        String categoryUrl = "https://api.chucknorris.io/jokes/categories";

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String[] categoryResponse = response.split(",",0);

                if(categoryResponse.length >0){
                    for(String s: categoryResponse){
                        s = s.replaceAll("[^a-zA-Z]", "");
                        s = s.substring(0,1).toUpperCase() + s.substring(1);
                        StaticResource.categorySelection.add(s);
                    }
                }

                MainFragment.setupSpinnerAdapter(context);
                requestQueue.stop();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "The request failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
                requestQueue.stop();
            }
        };

        StringRequest stringRequest = new StringRequest(Request.Method.GET, categoryUrl, responseListener,
                errorListener);
        requestQueue.add(stringRequest);
    }


    // Method to send request and get facts from chuck norris
    // input: category or keyword
    // inputType: R.id.optionKeyword / R.id.optionCategory
    public static void sendRequest(final String input, final Context context, final FragmentManager fragmentManager, final int inputType){
        final RequestQueue requestQueue =  Volley.newRequestQueue(context);

        // Setup url depend on input and inputType
        String requestUrl = "https://api.chucknorris.io/jokes/random";
        if(inputType == R.id.optionCategory){
            if(!input.equals("Random")){
                requestUrl = requestUrl + "?category=" + input.toLowerCase();
            }
        } else if(inputType == R.id.optionKeyword){
            requestUrl = "https://api.chucknorris.io/jokes/search?query=" + input;
        }

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                // Use different class model to handle the response depend on inputType
                // Setup correct fragment
                Gson gson = new Gson();
                Fragment fragment = new Fragment();
                if(inputType == R.id.optionCategory){
                    StaticResource.currentCategoryFact = gson.<Result>fromJson(response, Result.class);
                    fragment = new FactDetailFragment();
                } else if(inputType == R.id.optionKeyword){
                    StaticResource.currentKeywordFacts = gson.fromJson(response, KeywordResponse.class);
                    fragment = new KeywordFactDetailFragment();
                }

                requestQueue.stop();

                // set loading to false mean the app finish loading
                loading = false;

                // Swap fragment to correct fragment layout
                swapFragment(fragment, fragmentManager);

                // Response user when finish loading
                Toast.makeText(context, "Finish Loading. ❤", Toast.LENGTH_SHORT).show();
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "The request failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
                loading = false;
                requestQueue.stop();
            }
        };

        StringRequest stringRequest = new StringRequest(Request.Method.GET, requestUrl, responseListener,
                errorListener);
        requestQueue.add(stringRequest);
    }

    // Method to swap fragment
    public static void swapFragment(Fragment fragment, FragmentManager fragmentManager) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_slot, fragment);
        // If app is loading, lock the fragment
        if(!loading) {
            fragmentTransaction.commit();
        }
    }

    // Method to format the category text
    public static String formatCategory(List<String> categories){

        String catString = "";

        if(categories.size() < 1) {
            catString = "Random";
        } else {
            for (String cat : categories) {
                catString += cat.substring(0, 1).toUpperCase() + cat.substring(1);
            }
        }

        return catString;
    }
}
