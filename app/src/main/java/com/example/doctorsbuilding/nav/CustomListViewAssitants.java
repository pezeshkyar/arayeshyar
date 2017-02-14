package com.example.doctorsbuilding.nav;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by hossein on 1/16/2017.
 */
public class CustomListViewAssitants extends BaseAdapter {
    private Context context;
    private OnItemClickListener onItemClickListener;
    private ArrayList<Assistant> assistants = new ArrayList<Assistant>();

    public CustomListViewAssitants(Context context, ArrayList<Assistant> assistants) {
        this.context = context;
        this.assistants = assistants;
    }

    class Holder {
        public TextView name;
        public ImageButton btn_delete;

        public Holder(View row) {
            name = (TextView) row.findViewById(R.id.lvi_assistantName);
            btn_delete = (ImageButton) row.findViewById(R.id.lvi_assistantDelete);
        }
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        Holder holder;
        View rowView = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.assistants_listview_item, null);
            holder = new Holder(rowView);
            rowView.setTag(holder);
        } else {
            holder = (Holder) rowView.getTag();
        }
        holder.name.setText(assistants.get(position).getFullName());
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItem(position);
            }
        });
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(position, LviActionType.select);
            }
        });
        return rowView;
    }

    private void removeItem(final int position) {
        MyAlertDialogFragment.OnClickListener myDialogFrag = new MyAlertDialogFragment.OnClickListener() {
            @Override
            public void onClick(int whichButton) {
                switch (whichButton) {
                    case DialogInterface.BUTTON_POSITIVE:
                        onItemClickListener.onItemClick(position, LviActionType.remove);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        MyAlertDialogFragment builder = MyAlertDialogFragment.newInstance("حذف همکار آرایشی", android.R.drawable.ic_menu_delete
                , "شما در حال حذف این  همکار آرایشی می باشید .", "حذف همکار آرایشی", "انصراف");

        builder.setOnClickListener(myDialogFrag).show(((Activity) context).getFragmentManager(), "");
        builder.setCancelable(false);
    }

    public void add(Assistant assistant) {
        assistants.add(assistant);
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Assistant> assistantList) {
        assistants.addAll(assistantList);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        assistants.remove(position);
        notifyDataSetChanged();
    }

    public void removeAll(ArrayList<Assistant> assistantList) {
        assistants.removeAll(assistantList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return assistants.size();
    }

    @Override
    public Object getItem(int position) {
        return assistants.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public CustomListViewAssitants setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        return this;
    }

}
