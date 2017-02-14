package com.example.doctorsbuilding.nav;

import android.content.Context;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.doctorsbuilding.nav.Dr.Clinic.Office;

import java.util.ArrayList;

/**
 * Created by hossein on 1/29/2017.
 */
public class CustomListViewMyOffices extends BaseAdapter {
    private Context context;
    private ArrayList<Office> offices;
    private OnItemClickListener onItemClickListener;

    public CustomListViewMyOffices(Context context, ArrayList<Office> offices) {
        this.context = context;
        this.offices = offices;
    }

    @Override
    public int getCount() {
        return offices.size();
    }

    @Override
    public Object getItem(int position) {
        return offices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class Holder {
        public ImageView imageView;
        public TextView name;
        public TextView expert;
        public TextView address;
        public TextView phone;
        public TextView officeCode;
        public ImageView btnSetting;

        public Holder(View v) {
            imageView = (ImageView) v.findViewById(R.id.offices_item_image);
            name = (TextView) v.findViewById(R.id.offices_item_name);
            expert = (TextView) v.findViewById(R.id.offices_item_expert);
            address = (TextView) v.findViewById(R.id.offices_item_address);
            phone = (TextView) v.findViewById(R.id.offices_item_phone);
            btnSetting = (ImageView) v.findViewById(R.id.offices_btn_setting);
            officeCode = (TextView) v.findViewById(R.id.offices_item_officeCode);
        }
    }

    public void add(Office office) {
        offices.add(office);
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Office> officeha) {
        offices.clear();
        offices.addAll(officeha);
        notifyDataSetChanged();
    }

    public void remove(Office office) {
        offices.remove(office);
        notifyDataSetChanged();
    }

    public void removeAll(ArrayList<Office> officeha) {
        offices.removeAll(officeha);
        notifyDataSetChanged();
    }

    public void update(int position, Office office) {
        offices.set(position, office);
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        View rowView = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.offices_item, null);
            holder = new Holder(rowView);
            rowView.setTag(holder);
        } else {
            holder = (Holder) rowView.getTag();
        }

        holder.imageView.setImageBitmap(offices.get(position).getPhoto());
        holder.name.setText(offices.get(position).getFirstname().concat(" " + offices.get(position).getLastname()));
        holder.expert.setText(offices.get(position).getSubExpertName());
        holder.address.setText(offices.get(position).getAddress());
        holder.phone.setText("تلفن : ".concat(offices.get(position).getPhone()));
        holder.officeCode.setText(" " + offices.get(position).getId());
        holder.btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayMenu(position, holder.btnSetting);
            }
        });
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(position, LviActionType.select);
            }
        });
        if (offices.get(position).getRole() == UserType.User.ordinal() && G.officeInfo.getId() != offices.get(position).getId())
            holder.btnSetting.setVisibility(View.VISIBLE);
        else
            holder.btnSetting.setVisibility(View.GONE);


        return rowView;
    }

    private void displayMenu(final int position, View anchor) {
        PopupMenu popupMenu = new PopupMenu(context, anchor);
        popupMenu.getMenuInflater().inflate(R.menu.popup_office_delete, popupMenu.getMenu());
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.office_delete)
                    onItemClickListener.onItemClick(position, LviActionType.remove);
                return false;
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
