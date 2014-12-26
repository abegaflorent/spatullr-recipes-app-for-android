package com.pongodev.recipesapp.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.pongodev.recipesapp.R;
import com.pongodev.recipesapp.adapters.AdapterCategories;
import com.pongodev.recipesapp.utils.DBHelperRecipes;
import com.pongodev.recipesapp.utils.OnTapListener;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by taufanerfiyanto on 10/2/14.
 */

/**
 * A placeholder fragment containing a simple view.
 */
public class FragmentCategories extends Fragment {

    RecyclerView recyclerView;
    ProgressBar prgLoading;

    DBHelperRecipes dbhelper;

    AdapterCategories adapterCategories;
    OnCategorySelectedListener mCallback;

    ArrayList<ArrayList<Object>> data;

    private ArrayList<String> categoryIds = new ArrayList<String>();
    private ArrayList<String> categoryNames = new ArrayList<String>();

    public void setActivateOnItemClick(boolean activateOnItemClick) {
        // When setting CHOICE_MODE_SINGLE, ListView will automatically
        // give items the 'activated' state when touched.
        }


    // Create interface listener
    public interface OnCategorySelectedListener{
        public void onCategorySelected(String ID, String CategoryName);
    }

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static FragmentCategories newInstance() {
        FragmentCategories fragment = new FragmentCategories();

        return fragment;
    }

    public FragmentCategories() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_categories, container, false);

        setRetainInstance(true);

        // Connect view objects and view id on xml.
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        prgLoading = (ProgressBar) rootView.findViewById(R.id.prgLoading);


        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        dbhelper = new DBHelperRecipes(getActivity());

        try {
            dbhelper.createDataBase();
        }catch(IOException ioe){
            throw new Error("Unable to create database");
        }


        dbhelper.openDataBase();

        new syncGetData().execute();

        adapterCategories = new AdapterCategories(getActivity());


        adapterCategories.setOnTapListener(new OnTapListener() {
            @Override
            public void onTapView(String ID, String CategoryName) {
                mCallback.onCategorySelected(ID, CategoryName);
            }
        });

        return rootView;
    }

    public class syncGetData extends AsyncTask<Void, Void, Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            prgLoading.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            getDataFromDatabase();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            prgLoading.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapterCategories.updateList(categoryIds, categoryNames);

            recyclerView.setAdapter(adapterCategories);

        }
    }

    public void getDataFromDatabase(){
        data = dbhelper.getAllCategoriesData();


        for(int i = 0; i< data.size(); i++){
            ArrayList<Object> row = data.get(i);

            categoryIds.add(row.get(0).toString());
            categoryNames.add(row.get(1).toString());
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        try {
            mCallback = (OnCategorySelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCategorySelectedListener");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dbhelper.close();
    }
}