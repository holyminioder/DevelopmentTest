package com.example.lederui.developmenttest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.lederui.developmenttest.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by holyminier on 2017/4/20.
 */

public class ListViewAdapter extends BaseAdapter {
    private List<String> mList = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context mContext;

    public ListViewAdapter(Context context, List<String> list) {
        mList = list;
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listview_item, null);
            viewHolder = new ViewHolder();
            ButterKnife.bind(viewHolder, convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mListItem.setText(mList.get(position));
        return convertView;
    }

    class ViewHolder {
        @BindView(R.id.list_item)
        TextView mListItem;
    }
}
