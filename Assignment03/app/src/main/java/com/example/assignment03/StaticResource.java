package com.example.assignment03;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.assignment03.Fragments.FactDetailFragment;
import com.example.assignment03.Fragments.KeywordFactDetailFragment;
import com.example.assignment03.Fragments.MainFragment;
import com.example.assignment03.Model.KeywordResponse;
import com.example.assignment03.Model.Result;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ServiceConfigurationError;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// This class is use to record static resource such as variable and method
public class StaticResource {
    // Record category list
    public static ArrayList<String> categorySelection = new ArrayList<String>();

    // Record if the system is loading
    // True = loading
    // False = not loading
    public static boolean loading = false;


    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.chucknorris.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static ChuckNorrisService service = retrofit.create(ChuckNorrisService.class);

    // Method to send request and get category from chucknorris.io
    public static void getCategory(ChuckNorrisService service, Context context){

        Call<List<String>> categoryCall = service.getCategory();

        // Get response from call
        categoryCall.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if(response.isSuccessful()) {

                    Log.d("isSuccessful", "onResponse: DO RESPONSE CODE HERE");

                    // Split the list string by ","
                    if (response.body() != null) {
                        List<String> responseCategory = response.body();
                        for(String s: responseCategory){
                            categorySelection.add(s.substring(0, 1).toUpperCase() + s.substring(1));
                        }
                    }
                } else {
                    Log.d("!isSuccessful", "onResponse: ERROR IS: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.d("onFailure", "onFailure: ON FAILURE IS " + t.getLocalizedMessage());
                Toast.makeText(context, "The request failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    // Method to send request and get facts from chuck norris
    // input: category or keyword
    // inputType: R.id.optionKeyword / R.id.optionCategory
    public static void sendRequest(ChuckNorrisService service, final String input, final Context context, final FragmentManager fragmentManager, final int inputType){

        if(inputType == R.id.optionCategory){
            Call<Result> categoryCall = service.getRandom();
            if(!input.equals("Random")){
                categoryCall = service.getByCategory(input.toLowerCase());
            }

            categoryCall.enqueue(new Callback<Result>() {
                @Override
                public void onResponse(Call<Result> call, Response<Result> response) {
                    loading = false;
                    swapFragment(new FactDetailFragment(response.body(), input), fragmentManager);
                    // Response user when finish loading
                    Toast.makeText(context, "Finish Loading. ❤", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<Result> call, Throwable t) {
                    Toast.makeText(context, "The request failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    loading = false;
                }
            });
        } else if(inputType == R.id.optionKeyword){
            Call<KeywordResponse> textCall = service.getByText(input);

            textCall.enqueue(new Callback<KeywordResponse>() {
                @Override
                public void onResponse(Call<KeywordResponse> call, Response<KeywordResponse> response) {
                    loading = false;
                    swapFragment(new KeywordFactDetailFragment(response.body(), input), fragmentManager);
                    // Response user when finish loading
                    Toast.makeText(context, "Finish Loading. ❤", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<KeywordResponse> call, Throwable t) {
                    Toast.makeText(context, "The request failed: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    loading = false;
                }
            });
        }
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
