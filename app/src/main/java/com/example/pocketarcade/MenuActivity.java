package com.example.pocketarcade;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pocketarcade.databinding.ActivityMenuBinding;

public class MenuActivity extends AppCompatActivity {

    private static final String TAG = "CICLO_VIDA";
    private ActivityMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "MenuActivity: onCreate()");

        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String apelido = getIntent().getStringExtra(MainActivity.EXTRA_APELIDO);
        if (apelido != null && !apelido.isEmpty()) {
            binding.boasVindas.setText("Olá, " + apelido + "!");
        }

        Animation animEntrada = AnimationUtils.loadAnimation(this, R.anim.fade_in_translate);
        binding.layoutContainer.startAnimation(animEntrada);

        configurarNavegacao();
    }

    private void configurarNavegacao() {
        binding.btnOpenScanner.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, ScannerActivity.class);
            startActivity(intent);
        });

        binding.btnOpenArena.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, ArenaActivity.class);
            startActivity(intent);
        });

        binding.btnOpenVault.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, CofreActivity.class);
            startActivity(intent);
        });

        binding.btnOpenRandomizer.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, RandomizadorActivity.class);
            startActivity(intent);
        });

        binding.btnExitSession.setOnClickListener(v -> {
            Intent intent = new Intent(MenuActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra(MainActivity.EXTRA_ULTIMO_MODULO, "Central de Desafios");
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MenuActivity: onResume()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "MenuActivity: onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MenuActivity: onDestroy()");
    }
}