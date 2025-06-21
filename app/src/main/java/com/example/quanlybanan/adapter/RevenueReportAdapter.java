package com.example.quanlybanan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlybanan.R;
import com.example.quanlybanan.model.RevenueReportItem;

import java.util.List;

public class RevenueReportAdapter extends RecyclerView.Adapter<RevenueReportAdapter.ViewHolder> {
    private List<RevenueReportItem> reportList;
    private Context context;

    public RevenueReportAdapter(Context context, List<RevenueReportItem> reportList) {
        this.context = context;
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_revenue_report, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RevenueReportItem item = reportList.get(position);
        holder.txtItemName.setText(item.getItemName());
        holder.txtTotalQuantity.setText("Số lượng bán: " + item.getTotalQuantity());
        holder.txtTotalRevenue.setText("Doanh thu: " + item.getTotalRevenue() + " đ");
    }

    @Override
    public int getItemCount() {
        return reportList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtItemName, txtTotalQuantity, txtTotalRevenue;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtItemName = itemView.findViewById(R.id.txtItemName);
            txtTotalQuantity = itemView.findViewById(R.id.txtTotalQuantity);
            txtTotalRevenue = itemView.findViewById(R.id.txtTotalRevenue);
        }
    }
}