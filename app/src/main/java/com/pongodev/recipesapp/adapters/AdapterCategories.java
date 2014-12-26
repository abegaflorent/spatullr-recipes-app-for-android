package com.pongodev.recipesapp.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pongodev.recipesapp.R;
import com.pongodev.recipesapp.utils.OnTapListener;

import java.util.ArrayList;


public class AdapterCategories extends RecyclerView.Adapter<AdapterCategories.ViewHolder>
{
    private final ArrayList<String> categoryIds;
    private final ArrayList<String> categoryNames;
    private OnTapListener onTapListener;

    private Context mContext;

    public AdapterCategories(Context context)
    {

        this.categoryIds = new ArrayList<String>();
        this.categoryNames = new ArrayList<String>();

        mContext = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.row_categories, null);

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
                if (onTapListener != null)
                    onTapListener.onTapView(categoryIds.get(position), categoryNames.get(position));
            }
        });
        //       viewHolder.textView.setText(textArrayList.get(i));
        // set data to textview and imageview
        viewHolder.txtCategory.setText(categoryNames.get(position));

    }

    @Override
    public int getItemCount()
    {
        return categoryIds.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView txtCategory;

        public ViewHolder(View v)
        {
            super(v);
            // connect views object and views id on xml
            txtCategory = (TextView) v.findViewById(R.id.txtCategory);
        }
    }

    public void updateList(
            ArrayList<String> categoryIds,
            ArrayList<String> categoryNames)
    {
        this.categoryIds.clear();
        this.categoryIds.addAll(categoryIds);

        this.categoryNames.clear();
        this.categoryNames.addAll(categoryNames);

        this.notifyDataSetChanged();
    }

    public void setOnTapListener(OnTapListener onTapListener)
    {
        this.onTapListener = onTapListener;
    }
}