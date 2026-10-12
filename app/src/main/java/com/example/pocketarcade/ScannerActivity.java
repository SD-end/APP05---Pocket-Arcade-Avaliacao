package com.example.pocketarcade;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.pocketarcade.databinding.ActivityScannerBinding;

public class ScannerActivity extends AppCompatActivity {

    private static final String TAG = "CICLO_VIDA";
    private ActivityScannerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ScannerActivity: onCreate()");

        binding = ActivityScannerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime() | WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, imeInsets.top, 0, imeInsets.bottom);
            return insets;
        });

        configurarAcoes();
    }

    private void configurarAcoes() {

        binding.btnAnalizar.setOnClickListener(v -> processarTexto());

        binding.btnLimpar.setOnClickListener(v -> {
            binding.mensagem.setText("");
            binding.mensagem.setError(null);
            binding.resultado.setVisibility(View.GONE);
        });

        binding.btnVoltar.setOnClickListener(v -> finish());

        binding.btnSair.setOnClickListener(v -> {
            Intent intent = new Intent(ScannerActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra(MainActivity.EXTRA_ULTIMO_MODULO, "Scanner de Mensagens");
            startActivity(intent);
            finish();
        });
    }

    private void processarTexto() {
        String texto = binding.mensagem.getText().toString();

        if (texto.trim().length() < 3) {
            binding.mensagem.setError(getString(R.string.erro_palavra_curta));
            return;
        }

        int charsSemEspaco = contarLetras(texto);

        int totalPalavras = contarPalavras(texto);

        int totalVogais = contarVogais(texto);

        String invertido = inverterTexto(texto);

        boolean palindromo = verificarPalindromo(texto);


        binding.caracteresSemEspacos.setText(getString(R.string.caracteres_sem_espacos) + " " + charsSemEspaco);
        binding.contadorPalavras.setText(getString(R.string.contador_palavras) + " " + totalPalavras);
        binding.contadorVogais.setText(getString(R.string.contador_vogais) + " " + totalVogais);
        binding.textoInvertido.setText(getString(R.string.texto_invertido) + " " + invertido);

        if (palindromo){
            binding.palindromo.setText(getString(R.string.palindromo) + " " + getString(R.string.palindromo_sim));
        }else {
            binding.palindromo.setText(getString(R.string.palindromo) + " " + getString(R.string.palindromo_nao));
        }

        binding.resultado.setAlpha(0f);
        binding.resultado.setScaleY(0.8f);
        binding.resultado.setVisibility(View.VISIBLE);
        binding.resultado.animate()
                .alpha(1f)
                .scaleY(1f)
                .setDuration(400)
                .start();
    }

    private int contarLetras(String frase){
        int letras = 0;

        for (int i = 0; i < frase.length(); i++) {
            if (frase.charAt(i) != ' ') {
                letras++;
            }
        }

        return letras;
    }

    private int contarPalavras(String frase){
        String contar = frase.trim();

        if (contar.isEmpty()){
            return 0;
        }

        int palavras = 0;
        boolean naPalavra = false;

        for (int i = 0; i < contar.length(); i++) {
            if (contar.charAt(i) != ' ') {
                if (!naPalavra) {
                    palavras++;
                    naPalavra = true;
                }
            } else {
                naPalavra = false;
            }
        }
        return palavras;
    }

    private int contarVogais(String frase) {
        String textoMinusculo = frase.toLowerCase();
        int total = 0;

        for (int i = 0; i < textoMinusculo.length(); i++) {
            char c = textoMinusculo.charAt(i);
            if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u' ||
                    c == 'á' || c == 'é' || c == 'í' || c == 'ó' || c == 'ú' ||
                    c == 'ã' || c == 'õ' || c == 'â' || c == 'ê' || c == 'ô' ||
                    c == 'à') {
                total++;
            }
        }
        return total;
    }

    private String inverterTexto(String frase) {
        String fraseInvertida = "";

        for (int i = frase.length() - 1; i >= 0; i--) {
            fraseInvertida += frase.charAt(i);
        }

        return fraseInvertida;
    }

    private boolean verificarPalindromo(String frase) {
        boolean palindromo;
        String textoCorreto = frase.toLowerCase().replace(" ", "").trim();
        String textoInvertido = inverterTexto(textoCorreto);

        if (textoCorreto.equals(textoInvertido)){
            palindromo = true;
        }else {
            palindromo = false;
        }

        return palindromo;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "ScannerActivity: onResume()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "ScannerActivity: onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "ScannerActivity: onDestroy()");
    }
}