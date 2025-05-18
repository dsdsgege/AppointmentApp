package com.example.appointmentapp.adapter;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appointmentapp.R;
import com.example.appointmentapp.activity.BookAppActivity;
import com.example.appointmentapp.dao.HospitalDAO;
import com.example.appointmentapp.model.Hospital;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class HospitalAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private List<Hospital> hospitalList;
    private List<Hospital> hospitalListAll;
    private Context context;
    private HospitalDAO hospitalDAO = new HospitalDAO();
    private int lastPosition = -1;

    private Filter hospitalFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Hospital> filterList = new ArrayList<>();
            FilterResults result = new FilterResults();

            if(charSequence == null || charSequence.length() == 0) {
                result.count = hospitalListAll.size();
                result.values = hospitalListAll;
            } else {
                String filter = charSequence.toString().toLowerCase().trim();

                // filtering the hospitals by their name
                for(Hospital h : hospitalListAll) {
                    if(h.getName().toLowerCase().contains(filter)) {
                        filterList.add(h);
                    }
                }
                result.count = filterList.size();
                result.values = filterList;
            }

            return result;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            hospitalList = (ArrayList) filterResults.values;
            notifyDataSetChanged();

        }
    };

    public HospitalAdapter(Context context, List<Hospital> hospitalList) {
        this.context = context;
        this.hospitalList = hospitalList;
        this.hospitalListAll = hospitalList;
    }

    @Override
    public Filter getFilter() {
        return hospitalFilter;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_view_row, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Hospital currentHospital = hospitalList.get(position);

        ((HospitalAdapter.ViewHolder) holder).bindTo(currentHospital);
    }

    @Override
    public int getItemCount() { return hospitalList.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView hospitalName;
        private TextView nextAppointment;
        private ImageView goToAppointment;

        public ViewHolder(@NonNull View view) {
            super(view);
            hospitalName = view.findViewById(R.id.hospital_name);
            nextAppointment = view.findViewById(R.id.next_appointment);
            goToAppointment = view.findViewById(R.id.go_to_app);
            goToAppointment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Hospital currHospital = new Hospital();
                    currHospital.setName(hospitalName.toString());

                    hospitalDAO.getDoctors(currHospital, new HospitalDAO.HospitalCallback() {
                        @Override
                        public void onCallback(List<Hospital> hospitalList) {

                        }

                        @Override
                        public void onDoctorsCallback(String[] doctors) {
                            Intent intent = new Intent(context, BookAppActivity.class);
                            intent.putExtra("hospital_name", hospitalName.getText());
                            intent.putExtra("nextApp", nextAppointment.getText());
                            intent.putExtra("doctors", doctors);
                            context.startActivity(intent);
                        }
                    });
                }
            });
        }

        public void bindTo(Hospital currentHospital) {
            hospitalName.setText(currentHospital.getName());
            nextAppointment.setText(currentHospital.getNextApp());
        }
    }

    public void setHospitalList(List<Hospital> hospitals) {
        this.hospitalList.clear();
        this.hospitalList.addAll(hospitals);
    }


}


