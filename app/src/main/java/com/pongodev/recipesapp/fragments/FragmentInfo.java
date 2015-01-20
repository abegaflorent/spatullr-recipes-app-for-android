package com.pongodev.recipesapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.pongodev.recipesapp.R;
import com.pongodev.recipesapp.utils.Utils;


public class FragmentInfo extends Fragment {

    // Create objects of views.
    WebView webInfo;

    private String info;

    String htmlFormat = "<body bgcolor=\"#FBFBFB\">" +
            "<font color=\"#212121\">"+ Utils.ARG_TAG_CONTENT+ "</font>" +
            "</body>";



    public static FragmentInfo newInstance(String info) {
        FragmentInfo fragment = new FragmentInfo();
        Bundle bundle = new Bundle();
        bundle.putString(Utils.ARG_INFO, info);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        info = getArguments().getString(Utils.ARG_INFO);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_info,container,false);
        webInfo = (WebView) rootView.findViewById(R.id.webInfo);

        // Load value to webview.
        webInfo.loadData(htmlFormat.replace(Utils.ARG_TAG_CONTENT, info), "text/html", "UTF-8");

        return rootView;
    }

}
