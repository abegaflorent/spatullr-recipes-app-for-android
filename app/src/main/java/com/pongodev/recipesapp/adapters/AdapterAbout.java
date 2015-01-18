package com.pongodev.recipesapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pongodev.recipesapp.R;
import com.pongodev.recipesapp.listeners.OnTapAboutListener;

import java.util.ArrayList;


public class AdapterAbout extends RecyclerView.Adapter<AdapterAbout.ViewHolder>
{
    private final ArrayList<String> titles;
    private final ArrayList<String> summaries;
    private OnTapAboutListener onTapAboutListener;

    private Context mContext;

    public AdapterAbout(Context context)
    {

        this.titles = new ArrayList<String>();
        this.summaries = new ArrayList<String>();

        mContext = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {

        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_about, null);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position)
    {
        viewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (onTapAboutListener != null)
                    onTapAboutListener.onTapView(position);
            }
        });

        // set data to textview and imageview
        viewHolder.txtTitle.setText(titles.get(position));
        viewHolder.txtSummary.setText(summaries.get(position));

    }

    @Override
    public int getItemCount()
    {
        return titles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView txtTitle, txtSummary;

        public ViewHolder(View v)
        {
            super(v);
            // connect views object and views id on xml
            txtTitle = (TextView) v.findViewById(R.id.txtTitle);
            txtSummary = (TextView) v.findViewById(R.id.txtSubTitle);
        }
    }

    public void updateList(
            ArrayList<String> titles,
            ArrayList<String> summaries)
    {
        this.titles.clear();
        this.titles.addAll(titles);

        this.summaries.clear();
        this.summaries.addAll(summaries);

        this.notifyDataSetChanged();
    }

    public void setOnTapAboutListener(OnTapAboutListener onTapAboutListener)
    {
        this.onTapAboutListener = onTapAboutListener;
    }
}
