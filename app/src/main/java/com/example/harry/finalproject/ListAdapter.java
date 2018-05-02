package com.example.harry.finalproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lib.Product;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter {
    private Context myContext;
    private LayoutInflater myInflator;
    private ArrayList<Product> myList;

    public ListAdapter(Context context, ArrayList<Product> list) {
        myContext = context;
        myList = list;
        myInflator = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public Object getItem(int index) {
        return myList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int index, View convertView, ViewGroup parent) {
        View rowView = myInflator.inflate(R.layout.list_layout, parent, false);

        TextView subtitleTextView =
                rowView.findViewById(R.id.recipe_list_subtitle);

        TextView detailTextView =
                rowView.findViewById(R.id.recipe_list_detail);

        Product item = (Product) getItem(index);

        subtitleTextView.setText(item.getName());
        detailTextView.setText(String.format("%.0f kCal", item.getCalories()
                * item.getAmount() / 100));
//        Bitmap picture = BitmapFactory.decodeByteArray(item.getPicutre(),
//                0, item.getPicutre().length);
//        thumbnailImageView.setImageBitmap(picture);
//        thumbnailImageView.setVisibility(View.VISIBLE);

        return rowView;
    }
}
