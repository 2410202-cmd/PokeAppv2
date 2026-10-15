package com.example.pokeappv2;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class HistorialActivity extends AppCompatActivity {

    private RecyclerView rvHistory;
    private Button btnClearHistory;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        rvHistory = findViewById(R.id.rvHistory);
        btnClearHistory = findViewById(R.id.btnClearHistory);

        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        loadHistoryData();

        btnClearHistory.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("PokeAppHistory", Context.MODE_PRIVATE);
            prefs.edit().remove("battle_list").apply();
            loadHistoryData();
            Toast.makeText(this, "Historial limpiado", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadHistoryData() {
        SharedPreferences prefs = getSharedPreferences("PokeAppHistory", Context.MODE_PRIVATE);
        String historyRaw = prefs.getString("battle_list", "");

        List<BattleRecord> battleList = new ArrayList<>();

        if (!historyRaw.isEmpty()) {
            String[] entries = historyRaw.split("\\|\\|");
            for (String entry : entries) {
                if (!entry.trim().isEmpty()) {
                    String[] parts = entry.split(":::");
                    if (parts.length >= 6) {
                        battleList.add(new BattleRecord(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]));
                    } else if (parts.length >= 4) {
                        battleList.add(new BattleRecord(parts[0], parts[1], parts[2], parts[3], "", ""));
                    }
                }
            }
        }

        adapter = new HistoryAdapter(this, battleList);
        rvHistory.setAdapter(adapter);
    }

    // Modelo para el registro
    private static class BattleRecord {
        String date, player, rival, winner, playerGif, rivalGif;

        BattleRecord(String date, String player, String rival, String winner, String playerGif, String rivalGif) {
            this.date = date;
            this.player = player;
            this.rival = rival;
            this.winner = winner;
            this.playerGif = playerGif;
            this.rivalGif = rivalGif;
        }
    }

    // Adaptador RecyclerView
    private static class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

        private final Context context;
        private final List<BattleRecord> records;

        HistoryAdapter(Context context, List<BattleRecord> records) {
            this.context = context;
            this.records = records;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_history_battle, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            BattleRecord record = records.get(position);

            holder.tvDate.setText(record.date);
            holder.tvPlayerName.setText(record.player);
            holder.tvRivalName.setText(record.rival);
            holder.tvWinner.setText("👑 GANADOR: " + record.winner);

            if (!record.playerGif.isEmpty()) {
                Glide.with(context).load(record.playerGif).into(holder.ivPlayerGif);
            }
            if (!record.rivalGif.isEmpty()) {
                Glide.with(context).load(record.rivalGif).into(holder.ivRivalGif);
            }
        }

        @Override
        public int getItemCount() {
            return records.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvDate, tvPlayerName, tvRivalName, tvWinner;
            ImageView ivPlayerGif, ivRivalGif;

            ViewHolder(View itemView) {
                super(itemView);
                tvDate = itemView.findViewById(R.id.tvDate);
                tvPlayerName = itemView.findViewById(R.id.tvPlayerName);
                tvRivalName = itemView.findViewById(R.id.tvRivalName);
                tvWinner = itemView.findViewById(R.id.tvWinner);
                ivPlayerGif = itemView.findViewById(R.id.ivPlayerGif);
                ivRivalGif = itemView.findViewById(R.id.ivRivalGif);
            }
        }
    }
}