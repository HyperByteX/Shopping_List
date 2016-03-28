package com.shoppinglist.jordan.shoppinglist;


import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;


public class IdListAdapter extends BaseAdapter implements ListAdapter {

    private Context context;
    private ArrayList<String> list = new ArrayList<>();



    public IdListAdapter(Context context, ArrayList<String> list) {
        this.list = list;
        this.context = context;
    }




    @Override
    public int getCount() {
        return list.size();
    }




    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }




    @Override
    public long getItemId(int pos) {
        return 0;
    }




    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.id_list_row, null);
        }

        //Handle TextView and display string from your list
        TextView listItemText = (TextView)view.findViewById(R.id.list_id);
        listItemText.setText(list.get(position));


        return view;
    }
}