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
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by hossein on 1/16/2017.
 */
public class CustomListViewAssistantsTiming extends BaseAdapter {
    private Context context;
    private OnItemClickListener onItemClickListener;
    private ArrayList<AssistantTiming> timings = new ArrayList<AssistantTiming>();

    public CustomListViewAssistantsTiming(Context context, ArrayList<AssistantTiming> timings) {
        this.context = context;
        this.timings = timings;
    }

    class Holder {
        public TextView day;
        public TextView time;
        public ImageButton btn_delete;

        public Holder(View row) {
            time = (TextView) row.findViewById(R.id.lvi_assistantTime);
            day = (TextView) row.findViewById(R.id.lvi_assistantDay);
            btn_delete = (ImageButton) row.findViewById(R.id.lvi_assistantDelTime);
        }
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        Holder holder;
        View rowView = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.lvi_assist_timing, null);
            holder = new Holder(rowView);
            rowView.setTag(holder);
        } else {
            holder = (Holder) rowView.getTag();
        }
        holder.time.setText(timings.get(position).getTime());
        holder.day.setText(timings.get(position).getPersianDate());
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeItem(position);
            }
        });
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(context, "click item", Toast.LENGTH_SHORT).show();
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
        MyAlertDialogFragment builder = MyAlertDialogFragment.newInstance("حذف وقت", android.R.drawable.ic_menu_delete
                , "شما در حال حذف این وقت می باشید .", "حذف وقت", "انصراف");

        builder.setOnClickListener(myDialogFrag).show(((Activity) context).getFragmentManager(), "");
        builder.setCancelable(false);
    }

    public void add(AssistantTiming timing) {
        timings.add(timing);
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<AssistantTiming> timeList) {
        timings.addAll(timeList);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        timings.remove(position);
        notifyDataSetChanged();
    }

    public void clear() {
        timings.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return timings.size();
    }

    @Override
    public Object getItem(int position) {
        return timings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
