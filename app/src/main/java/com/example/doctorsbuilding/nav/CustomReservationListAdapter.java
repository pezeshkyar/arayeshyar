package com.example.doctorsbuilding.nav;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.doctorsbuilding.nav.User.User;

import java.util.ArrayList;

/**
 * Created by hossein on 7/24/2016.
 */
public class CustomReservationListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<User> users = new ArrayList<User>();
    private OnItemClickListener onItemClickListener;

    public CustomReservationListAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    class Holder {
        public TextView name;
        public TextView mobile;

        public Holder(View row) {
            name = (TextView) row.findViewById(R.id.search_item_name);
            mobile = (TextView) row.findViewById(R.id.search_item_mobile);
        }
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        Holder holder;
        View rowView = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.search_item, null);
            holder = new Holder(rowView);
            rowView.setTag(holder);
        } else {
            holder = (Holder) rowView.getTag();
        }
        holder.name.setText(users.get(position).getFullName());
        holder.mobile.setText(users.get(position).getPhone());
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(position, LviActionType.select);
            }
        });
        return rowView;
    }

    public void add(User user) {
        users.add(user);
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<User> users1) {
        users.clear();
        users.addAll(users1);
        notifyDataSetChanged();
    }

    public void removeAll() {
        users.clear();
        notifyDataSetChanged();
    }

    public void remove(int position) {
        users.remove(position);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
