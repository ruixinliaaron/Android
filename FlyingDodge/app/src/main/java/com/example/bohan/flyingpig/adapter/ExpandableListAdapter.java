package com.example.bohan.flyingpig.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.bohan.flyingpig.R;
import com.example.bohan.flyingpig.entity.Order;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;


/**
 * Created by beiwen on 2017/11/28.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter implements ExpandableListView.OnChildClickListener {
    private Context context;
    private List<Order> listHeader;
    private HashMap<Order,List<String>> listHashMap;
    private static HashMap<Integer, Boolean> isSelected;
    DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public ExpandableListAdapter(Context context, List<Order> listHeader, HashMap<Order, List<String>> listHashMap, HashMap<Integer,Boolean> isSelected) {
        this.context = context;
        this.listHeader = listHeader;
        this.listHashMap = listHashMap;
        this.isSelected = isSelected;
        initDate();
    }

    private void initDate() {
        for (int i = 0; i < listHeader.size(); i++) {
            getIsSelected().put(i, false);
        }
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        ExpandableListAdapter.isSelected = isSelected;
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
    public View getGroupView(final int i, boolean b, View view, ViewGroup viewGroup) {
        Order order = (Order) getGroup(i);//obtain current order object
        if(view == null){
            LayoutInflater inflater =(LayoutInflater) this.context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.list_group,null);
        }
        TextView orderId = (TextView)view.findViewById(R.id.orderId);
        TextView customerName = (TextView)view.findViewById(R.id.customerName);
        TextView createDate = (TextView)view.findViewById(R.id.creationDate);
        TextView status = (TextView)view.findViewById(R.id.status);
        CheckBox checkbox = (CheckBox) view.findViewById(R.id.checkBox);
        orderId.setText(String.valueOf(order.getOrderID())+ " ");
        customerName.setText(order.getCustomer().getName()+ " ");
        createDate.setText(String.valueOf(dateFormat.format(order.getCreationDate()))+" ");
        status.setText(order.getStatus());

        checkbox.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (isSelected.get(i)) {
                    isSelected.put(i, false);
                    setIsSelected(isSelected);
                } else {
                    isSelected.put(i, true);
                    setIsSelected(isSelected);
                }
                notifyDataSetChanged();
            }
        });

        checkbox.setChecked(getIsSelected().get(i));

        if(b){
            view.setBackgroundResource(R.color.colorAccent);
        }else{
            view.setBackgroundResource(android.R.color.white);
        }
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
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
        return false;
    }
}
