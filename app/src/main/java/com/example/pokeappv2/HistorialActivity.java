package com.example.pokeappv2;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.HashSet;
import java.util.Set;

public class HistorialActivity extends AppCompatActivity {

    private LinearLayout containerHistory;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        containerHistory = findViewById(R.id.containerHistory);
        Button btnClearHistory = findViewById(R.id.btnClearHistory);
        sharedPreferences = getSharedPreferences("PokeHistory", MODE_PRIVATE);

        loadHistory();

        btnClearHistory.setOnClickListener(v -> {
            sharedPreferences.edit().clear().apply();
            loadHistory();
        });
    }

    private void loadHistory() {
        containerHistory.removeAllViews();
        Set<String> historySet = sharedPreferences.getStringSet("history_list", new HashSet<>());

        if (historySet.isEmpty()) {
            TextView emptyText = new TextView(this);
            emptyText.setText("No hay búsquedas recientes.");
            emptyText.setTextColor(Color.parseColor("#94A3B8"));
            emptyText.setTextSize(16);
            containerHistory.addView(emptyText);
            return;
        }

        for (String item : historySet) {
            TextView textView = new TextView(this);
            textView.setText("🔍 " + item.toUpperCase());
            textView.setTextColor(Color.WHITE);
            textView.setTextSize(16);
            textView.setPadding(24, 24, 24, 24);
            textView.setBackgroundColor(Color.parseColor("#1E293B"));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 16);
            textView.setLayoutParams(params);

            containerHistory.addView(textView);
        }
    }
}