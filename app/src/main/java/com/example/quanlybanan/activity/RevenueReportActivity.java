package com.example.quanlybanan.activity;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlybanan.R;
import com.example.quanlybanan.adapter.RevenueReportAdapter;
import com.example.quanlybanan.database.DBHelper;
import com.example.quanlybanan.model.RevenueReportItem;

import java.util.List;

public class RevenueReportActivity extends AppCompatActivity {
    DBHelper db;
    RecyclerView recyclerReports;
    RevenueReportAdapter reportAdapter;
    TextView txtGrandTotalRevenue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revenue_report);

        db = new DBHelper(this);

        txtGrandTotalRevenue = findViewById(R.id.txtGrandTotalRevenue);
        recyclerReports = findViewById(R.id.recyclerReports);
        recyclerReports.setLayoutManager(new LinearLayoutManager(this));

        loadRevenueReport();
    }

    private void loadRevenueReport() {
        List<RevenueReportItem> reportList = db.getRevenueReport();
        reportAdapter = new RevenueReportAdapter(this, reportList);
        recyclerReports.setAdapter(reportAdapter);

        int grandTotal = 0;
        for (RevenueReportItem item : reportList) {
            grandTotal += item.getTotalRevenue();
        }
        txtGrandTotalRevenue.setText("Tổng doanh thu: " + grandTotal + " đ");
    }
}