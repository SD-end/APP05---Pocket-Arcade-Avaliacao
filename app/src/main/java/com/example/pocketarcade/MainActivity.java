package com.example.pocketarcade;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pocketarcade.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_APELIDO = "extra_apelido";
    public static final String EXTRA_ULTIMO_MODULO = "extra_ultimo_modulo";
    private static final String TAG = "CICLO_VIDA";

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "MainActivity: onCreate()");

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime() | WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, imeInsets.top, 0, imeInsets.bottom);
            return insets;
        });

        configurarAcoes();
    }

    private void configurarAcoes() {
        binding.btnEntrar.setOnClickListener(v -> {
            String apelido = binding.edtApelido.getText().toString().trim();

            if (apelido.length() < 3) {
                binding.edtApelido.setError(getString(R.string.erro_apelido_curto));
                return;
            }

            binding.btnEntrar.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        binding.btnEntrar.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(100)
                                .withEndAction(() -> {
                                    Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                                    intent.putExtra(EXTRA_APELIDO, apelido);
                                    startActivity(intent);
                                })
                                .start();
                    })
                    .start();
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        if (intent != null && intent.hasExtra(EXTRA_ULTIMO_MODULO)) {
            String ultimoModulo = intent.getStringExtra(EXTRA_ULTIMO_MODULO);
            binding.moduloArena.setVisibility(View.VISIBLE);
            binding.moduloArena.setText("Último módulo acessado: " + ultimoModulo);
            Toast.makeText(this, "Sessão encerrada no módulo: " + ultimoModulo, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "MainActivity: onResume()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "MainActivity: onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MainActivity: onDestroy()");
    }
}