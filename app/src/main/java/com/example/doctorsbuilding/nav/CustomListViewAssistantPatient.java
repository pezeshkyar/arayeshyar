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
 * Created by hossein on 1/18/2017.
 */
public class CustomListViewAssistantPatient extends BaseAdapter {
    private Context context;
    private ArrayList<PatientAssistant> patients = new ArrayList<PatientAssistant>();

    public CustomListViewAssistantPatient(Context context, ArrayList<PatientAssistant> assistants) {
        this.context = context;
        this.patients = assistants;
    }

    class Holder {
        public TextView name;
        public TextView time;

        public Holder(View row) {
            name = (TextView) row.findViewById(R.id.search_item_name);
            time = (TextView) row.findViewById(R.id.search_item_mobile);
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
        holder.name.setText(patients.get(position).getFullName());
        holder.time.setText(patients.get(position).getTime());
        return rowView;
    }

    public void add(PatientAssistant patientAssistant) {
        patients.add(patientAssistant);
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<PatientAssistant> patientList) {
        patients.addAll(patientList);
        notifyDataSetChanged();
    }

    public void clear() {
        patients.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return patients.size();
    }

    @Override
    public Object getItem(int position) {
        return patients.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
