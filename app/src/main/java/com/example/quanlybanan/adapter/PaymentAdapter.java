package com.example.quanlybanan.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlybanan.R;
import com.example.quanlybanan.model.Payment;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.PaymentViewHolder> {
    private Context context;
    private List<Payment> paymentList;
    public PaymentAdapter(Context context, List<Payment> paymentList) {
        this.context = context;
        this.paymentList = paymentList;
    }
    @Override
    public PaymentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_payment, parent, false);
        return new PaymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PaymentViewHolder holder, int position) {
        Payment payment = paymentList.get(position);
        holder.tableId.setText(payment.getTableId());
        holder.amount.setText(String.format("%.2f", payment.getAmount()));
        holder.timestamp.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(payment.getTimestamp())));
    }

    @Override
    public int getItemCount() {
        return paymentList.size();
    }

    public class PaymentViewHolder extends RecyclerView.ViewHolder {
        TextView tableId, amount, timestamp;

        public PaymentViewHolder(View itemView) {
            super(itemView);
            tableId = itemView.findViewById(R.id.txtTableId);
            amount = itemView.findViewById(R.id.txtAmount);
            timestamp = itemView.findViewById(R.id.txtTimestamp);
        }
    }
}

