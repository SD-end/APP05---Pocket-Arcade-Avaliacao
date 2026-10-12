package com.example.pocketarcade;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.pocketarcade.databinding.ActivityRandomizadorBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomizadorActivity extends AppCompatActivity {

    private static final String TAG = "CICLO_VIDA";
    private ActivityRandomizadorBinding binding;

    private List<ItemSorteio> listaItens;
    private RandomizadorAdapter adapter;

    private LinearLayoutManager layoutLinear;
    private GridLayoutManager layoutGrade;

    private MediaPlayer playerSomSorteio;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "RandomizadorActivity: onCreate()");

        binding = ActivityRandomizadorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime() | WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, imeInsets.top, 0, imeInsets.bottom);
            return insets;
        });

        random = new Random();

        playerSomSorteio = MediaPlayer.create(this, R.raw.som_vitoria1);

        configurarRecyclerView();
        configurarAcoes();
    }

    private void configurarRecyclerView() {
        listaItens = new ArrayList<>();

        listaItens.add(new ItemSorteio("Interestelar", "Filmes"));
        listaItens.add(new ItemSorteio("Pizza", "Comidas"));
        listaItens.add(new ItemSorteio("Paris", "Lugares"));
        listaItens.add(new ItemSorteio("10 flexões", "Desafios"));

        layoutLinear = new LinearLayoutManager(this);
        layoutGrade = new GridLayoutManager(this, 2);

        adapter = new RandomizadorAdapter(listaItens, posicao -> {
            if (posicao >= 0 && posicao < listaItens.size()) {
                listaItens.remove(posicao);
                adapter.notifyItemRemoved(posicao);
                Toast.makeText(this, getString(R.string.msg_excluir_item), Toast.LENGTH_SHORT).show();
            }
        });

        binding.itens.setLayoutManager(layoutLinear);
        binding.itens.setAdapter(adapter);
    }

    private void configurarAcoes() {
        binding.btnAdicionar.setOnClickListener(v -> adicionarItem());

        binding.btnSortear.setOnClickListener(v -> sortearPorCategoria());

        binding.alternarLayout.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.itens.setLayoutManager(layoutGrade);
                binding.lista.setTextColor(getColor(R.color.white));
                binding.grade.setTextColor(getColor(R.color.neon_laranja));
            } else {
                binding.itens.setLayoutManager(layoutLinear);
                binding.grade.setTextColor(getColor(R.color.white));
                binding.lista.setTextColor(getColor(R.color.neon_laranja));
            }
        });

        binding.btnVoltar.setOnClickListener(v -> finish());

        binding.btnSair.setOnClickListener(v -> {
            Intent intent = new Intent(RandomizadorActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra(MainActivity.EXTRA_ULTIMO_MODULO, "Randomizador de Palavras");
            startActivity(intent);
            finish();
        });
    }

    private void adicionarItem() {
        String palavra = binding.palavra.getText().toString().trim();
        String categoria = binding.spnCategorias.getSelectedItem().toString();

        if (palavra.isEmpty()) {
            binding.palavra.setError(getString(R.string.erro_campo_vazio));
            return;
        }

        ItemSorteio novoItem = new ItemSorteio(palavra, categoria);
        listaItens.add(novoItem);

        int novaPosicao = listaItens.size() - 1;
        adapter.notifyItemInserted(novaPosicao);
        binding.itens.smoothScrollToPosition(novaPosicao);

        binding.palavra.setText("");
    }

    private void sortearPorCategoria() {
        String categoriaSelecionada = binding.spnCategorias.getSelectedItem().toString();

        List<ItemSorteio> itensDaCategoria = new ArrayList<>();
        for (int i = 0; i < listaItens.size(); i++) {
            ItemSorteio item = listaItens.get(i);
            if (item.getCategoria().equalsIgnoreCase(categoriaSelecionada)) {
                itensDaCategoria.add(item);
            }
        }

        if (itensDaCategoria.isEmpty()) {
            Toast.makeText(this, getString(R.string.aviso_sem_itens_categoria), Toast.LENGTH_SHORT).show();
            binding.cardResultadoSorteio.setVisibility(View.GONE);
            return;
        }

        int indiceSorteado = random.nextInt(itensDaCategoria.size());
        ItemSorteio itemSorteado = itensDaCategoria.get(indiceSorteado);

        if (playerSomSorteio != null) {
            try {
                if (playerSomSorteio.isPlaying()) {
                    playerSomSorteio.seekTo(0);
                } else {
                    playerSomSorteio.start();
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro no áudio do sorteio: " + e.getMessage());
            }
        }

        binding.tvResultadoSorteio.setText(getString(R.string.resultado_sorteio) + " "  + itemSorteado.getPalavra());
        binding.cardResultadoSorteio.setAlpha(0f);
        binding.cardResultadoSorteio.setScaleX(0.8f);
        binding.cardResultadoSorteio.setVisibility(View.VISIBLE);
        binding.cardResultadoSorteio.animate()
                .alpha(1f)
                .scaleX(1f)
                .setDuration(400)
                .start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "RandomizadorActivity: onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "RandomizadorActivity: onPause()");
        if (playerSomSorteio != null && playerSomSorteio.isPlaying()) {
            playerSomSorteio.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "RandomizadorActivity: onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "RandomizadorActivity: onDestroy()");
        if (playerSomSorteio != null) {
            playerSomSorteio.release();
            playerSomSorteio = null;
        }
    }
}