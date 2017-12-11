package com.example.bohan.flyingpig.adapter;

/**
 * Created by 18910931590163.com on 12/4/17.
 */

import android.widget.BaseExpandableListAdapter;

import com.example.bohan.flyingpig.R;
import com.example.bohan.flyingpig.entity.GoodsInfo;
import com.example.bohan.flyingpig.entity.StoreInfo;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 18910931590163.com on 11/21/17.
 */

public class cartAdapter extends BaseExpandableListAdapter {
    private List<StoreInfo> groups;
    private Map<String, List<GoodsInfo>> children;
    private Context context;
    private CheckInterface checkInterface;
    public cartAdapter(List<StoreInfo> groups, Map<String, List<GoodsInfo>> children, Context context) {
        this.groups = groups;
        this.children = children;
        this.context = context;
    }
    public void setCheckInterface(CheckInterface checkInterface) {
        this.checkInterface = checkInterface;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        final GroupViewHolder gholder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.cart_group, null);
            gholder = new GroupViewHolder(convertView);
            convertView.setTag(gholder);
        } else {
            gholder = (GroupViewHolder) convertView.getTag();
        }
        final StoreInfo group = (StoreInfo) getGroup(groupPosition);

        gholder.groupbar.setText(group.getName());
        gholder.groupcheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                group.setChoosed(((CheckBox) v).isChecked());
                checkInterface.checkGroup(groupPosition, ((CheckBox) v).isChecked());
            }
        });
        gholder.groupcheckbox.setChecked(group.isChoosed());
        notifyDataSetChanged();
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, final boolean isLastChild, View convertView, final ViewGroup parent) {

        final ChildViewHolder cholder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.cart_item, null);
            cholder = new ChildViewHolder(convertView);
            convertView.setTag(cholder);
        } else {
            cholder = (ChildViewHolder) convertView.getTag();
        }
        final GoodsInfo goodsInfo = (GoodsInfo) getChild(groupPosition, childPosition);

        if (goodsInfo != null) {
            cholder.itemname.setText(goodsInfo.getName());
            cholder.itemprice.setText("$" + goodsInfo.getPrice() + "");
            cholder.childimage.setImageResource(goodsInfo.getGoodsImg());
            cholder.itemQty.setText("x" + goodsInfo.getCount());
            cholder.checkBox.setChecked(goodsInfo.isChoosed());
            cholder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goodsInfo.setChoosed(((CheckBox) v).isChecked());
                    cholder.checkBox.setChecked(((CheckBox) v).isChecked());
                    checkInterface.checkChild(groupPosition, childPosition, ((CheckBox) v).isChecked());
                }
            });
            notifyDataSetChanged();
        }
        return convertView;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String groupId = groups.get(groupPosition).getId();

        return children.get(groupId).size();
    }
    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<GoodsInfo> childs = children.get(groups.get(groupPosition).getId());
        return childs.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;

    }

    static class GroupViewHolder {//binding group elements

        @BindView(R.id.groupcheckbox)
        CheckBox groupcheckbox;
        @BindView(R.id.groupbar)
        TextView groupbar;
        GroupViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
    static class ChildViewHolder {//binding child elements
        @BindView(R.id.checkbox)
        CheckBox checkBox;
        @BindView(R.id.childimage)
        ImageView childimage;
        @BindView(R.id.itemname)
        TextView itemname;
        @BindView(R.id.itemprice)
        TextView itemprice;
        @BindView(R.id.itemQty)
        TextView itemQty;
        ChildViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
    public interface CheckInterface {
        void checkGroup(int groupPosition, boolean isChecked);
        void checkChild(int groupPosition, int childPosition, boolean isChecked);
    }
}

