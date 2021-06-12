package com.kelvin.dompet.Adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kelvin.dompet.Entity.detailEntity;
import com.kelvin.dompet.R;
import com.kelvin.dompet.databinding.DetailBinding;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class detailAdapter extends RecyclerView.Adapter<detailAdapter.detailViewHolder> {
    private ArrayList<detailEntity> details;
    private DetailItemListener listener;

    public detailAdapter(ArrayList<detailEntity> details) {
        this.details = details;
    }

    @NonNull
    @Override
    public detailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail, parent, false);
        return new detailViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(@NonNull detailAdapter.detailViewHolder holder, int position) {
        holder.setDetailData(details.get(position));
        holder.itemView.setOnClickListener(view -> listener.detailDataClicked(details.get(position)));
    }

    @Override
    public int getItemCount() {
        return details.size();
    }

    public void setOnItemClickListener(DetailItemListener listener2) {
        listener = listener2;
    }


    static class detailViewHolder extends RecyclerView.ViewHolder {
        private DetailBinding binding;

        public detailViewHolder(@NonNull View itemView, final DetailItemListener listener) {
            super(itemView);
            binding = DetailBinding.bind(itemView);

            binding.btedit.setOnClickListener(v -> {
                if(listener!=null){
                    int position = getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION){
                        listener.onEditClick(position);
                    }
                }
            });
            
            binding.btdelete.setOnClickListener(v -> {
                if(listener!=null){
                    int position = getAdapterPosition();
                    if(position!=RecyclerView.NO_POSITION){
                        listener.onDeleteClick(position);
                    }
                }
            });
        }
        

        public void setDetailData(detailEntity detail) {
            binding.tvdate.setText(detail.getDate());
            binding.tvcategory.setText(detail.getCategory());
            if(detail.getAmmount()<0){
                binding.tvcategory.setTextColor(Color.rgb(255,123,126));
            }
            binding.tvammount.setText(formater(detail.getAmmount()));
        }
    }

    public interface DetailItemListener {
        void detailDataClicked(detailEntity detail);

        void onEditClick(int position);

        void onDeleteClick(int position);
    }


    public static String formater(Integer amount) {
        String money;

        DecimalFormat idn = (DecimalFormat) DecimalFormat.getCurrencyInstance();
        DecimalFormatSymbols Rp = new DecimalFormatSymbols();
        Rp.setCurrencySymbol("Rp. ");
        Rp.setMonetaryDecimalSeparator(',');
        Rp.setGroupingSeparator('.');
        idn.setDecimalFormatSymbols(Rp);
        money = idn.format(amount);
        return money;
    }

}