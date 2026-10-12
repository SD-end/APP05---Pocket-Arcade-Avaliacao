package com.example.pocketarcade;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pocketarcade.databinding.ActivityCofreBinding;
import com.example.pocketarcade.databinding.ItemTentativaCofreBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CofreActivity extends AppCompatActivity {

    private static final String TAG = "CICLO_VIDA";
    private ActivityCofreBinding binding;

    private static final int MAX_TENTATIVAS = 8;
    private String codigoSecreto = "";
    private int tentativasRestantes = MAX_TENTATIVAS;
    private long tempoInicio = 0L;
    private boolean jogoFinalizado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "VaultActivity: onCreate()");

        binding = ActivityCofreBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime() | WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, imeInsets.top, 0, imeInsets.bottom);
            return insets;
        });

        iniciarNovaPartida();
        configurarAcoes();
    }

    private void iniciarNovaPartida() {
        codigoSecreto = gerarCodigoSecreto();
        tentativasRestantes = MAX_TENTATIVAS;
        jogoFinalizado = false;

        tempoInicio = System.currentTimeMillis();

        binding.historico.removeAllViews();
        binding.fimJogo.setVisibility(View.GONE);
        binding.tentativa.setEnabled(true);
        binding.btnTentar.setEnabled(true);
        binding.tentativa.setText("");
        atualizarContadorTentativas();
    }

    private String gerarCodigoSecreto() {
        List<Integer> numeros = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            numeros.add(i);
        }
        Collections.shuffle(numeros);

        String codigo = "";
        for (int i = 0; i < 4; i++){
            codigo += numeros.get(i);
        }

        return codigo;
    }

    private void configurarAcoes() {
        binding.btnTentar.setOnClickListener(v -> validarEProcessarTentativa());
        binding.btnNovoJogo.setOnClickListener(v -> iniciarNovaPartida());

        binding.btnVoltar.setOnClickListener(v -> finish());

        binding.btnSair.setOnClickListener(v -> {
            Intent intent = new Intent(CofreActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra(MainActivity.EXTRA_ULTIMO_MODULO, "Cofre: Código Secreto");
            startActivity(intent);
            finish();
        });
    }

    private void validarEProcessarTentativa() {
        if (jogoFinalizado) {
            return;
        }

        String chute = binding.tentativa.getText().toString().trim();

        if (chute.length() != 4) {
            binding.tentativa.setError(getString(R.string.erro_quatro_digitos));
            return;
        }

        if (possuiDigitosRepetidos(chute)) {
            binding.tentativa.setError(getString(R.string.erro_digitos_repetidos));
            return;
        }

        tentativasRestantes--;
        adicionarLinhaAoHistorico(chute);
        atualizarContadorTentativas();
        binding.tentativa.setText("");

        if (chute.equals(codigoSecreto)) {
            finalizarJogo(true);
        } else if (tentativasRestantes <= 0) {
            finalizarJogo(false);
        }
    }

    private boolean possuiDigitosRepetidos(String chute) {
        for (int i = 0; i < chute.length(); i++) {
            for (int j = i + 1; j < chute.length(); j++) {
                if (chute.charAt(i) == chute.charAt(j)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void adicionarLinhaAoHistorico(String chute) {
        ItemTentativaCofreBinding itemBinding = ItemTentativaCofreBinding.inflate(getLayoutInflater(), binding.historico, false);

        TextView[] blocos = new TextView[]{
                itemBinding.tvDigito1,
                itemBinding.tvDigito2,
                itemBinding.tvDigito3,
                itemBinding.tvDigito4
        };

        for (int i = 0; i < 4; i++) {
            char digitoChute = chute.charAt(i);
            int corFundo;

            if (digitoChute == codigoSecreto.charAt(i)) {
                corFundo = getColor(R.color.termo_verde);
            } else if (codigoSecreto.indexOf(digitoChute) != -1) {
                corFundo = getColor(R.color.termo_amarelo);
            } else {
                corFundo = getColor(R.color.termo_cinza);
            }

            blocos[i].setText(String.valueOf(digitoChute));
            blocos[i].setBackgroundColor(corFundo);
        }

        binding.historico.addView(itemBinding.getRoot());
    }

    private void finalizarJogo(boolean venceu) {
        jogoFinalizado = true;

        long tempoFim = System.currentTimeMillis();
        long tempoGastoSegundos = (tempoFim - tempoInicio) / 1000;
        int tentativasUsadas = MAX_TENTATIVAS - tentativasRestantes;

        binding.tentativa.setEnabled(false);
        binding.btnTentar.setEnabled(false);

        if (venceu) {
            binding.resultadoFinal.setText(getString(R.string.vitoria_cofre));
            binding.resultadoFinal.setTextColor(getColor(R.color.termo_verde));
        } else {
            binding.resultadoFinal.setText(getString(R.string.derrota_cofre) + codigoSecreto);
            binding.resultadoFinal.setTextColor(getColor(R.color.neon_rosa));
        }

        binding.tempoEstatisticas.setText(
                getString(R.string.tempo_gasto) + " " + tempoGastoSegundos + getString(R.string.segundos) +
                        "  |  Tentativas: " + tentativasUsadas + "/" + MAX_TENTATIVAS
        );

        binding.fimJogo.setAlpha(0f);
        binding.fimJogo.setScaleY(0.8f);
        binding.fimJogo.setVisibility(View.VISIBLE);
        binding.fimJogo.animate()
                .alpha(1f)
                .scaleY(1f)
                .setDuration(500)
                .start();
    }

    private void atualizarContadorTentativas() {
        binding.tentativasRestantes.setText(getString(R.string.label_tentativas_restantes) + " " + tentativasRestantes);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "CofreActivity: onResume()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "CofreActivity: onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "CofreActivity: onDestroy()");
    }
}