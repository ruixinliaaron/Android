package com.example.bohan.flyingpig.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.bohan.flyingpig.R;
import com.example.bohan.flyingpig.entity.Order;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

/**
 * Created by beiwen on 2017/12/5.
 */

public class ExpandableMyOrderListAdapter extends BaseExpandableListAdapter implements ExpandableListView.OnChildClickListener {
    private Context context;
    private List<Order> listHeader;
    private HashMap<Order,List<String>> listHashMap;

    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public ExpandableMyOrderListAdapter(Context context, List<Order> listHeader, HashMap<Order, List<String>> listHashMap) {
        this.context = context;
        this.listHeader = listHeader;
        this.listHashMap = listHashMap;
    }

    @Override
    public int getGroupCount() {
        return listHeader.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return listHashMap.get(listHeader.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return listHeader.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return listHashMap.get(listHeader.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        Order order = (Order) getGroup(i);
        if(view == null){
            LayoutInflater inflater =(LayoutInflater) this.context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.myorderlist_group,null);
        }
        TextView creationDate = (TextView)view.findViewById(R.id.creationDate1);
        TextView status = (TextView)view.findViewById(R.id.status1);

        creationDate.setText(String.valueOf(dateFormat.format(order.getCreationDate()))+"   ");
        status.setText(order.getStatus());
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        final String childText = (String)getChild(i,i1);
        if(view == null){
            LayoutInflater inflater =(LayoutInflater) this.context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.order_item,null);
        }
        TextView txtTextChild = (TextView)view.findViewById(R.id.orderitem);
        txtTextChild.setText(childText);
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        return false;
    }
}
