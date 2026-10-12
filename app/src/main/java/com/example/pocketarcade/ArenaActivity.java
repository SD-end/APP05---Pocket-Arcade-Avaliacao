package com.example.pocketarcade;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pocketarcade.databinding.ActivityArenaBinding;

import java.util.Random;

public class ArenaActivity extends AppCompatActivity {

    private static final String TAG = "CICLO_VIDA";
    private ActivityArenaBinding binding;

    private static final int PEDRA = 0;
    private static final int PAPEL = 1;
    private static final int TESOURA = 2;

    private int pontosJogador = 0;
    private int pontosCpu = 0;
    private boolean partidaFinalizada = false;

    private MediaPlayer playerSomVitoria;
    private MediaPlayer playerSomDerrota;

    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "ArenaActivity: onCreate()");

        binding = ActivityArenaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        random = new Random();

        playerSomDerrota = MediaPlayer.create(this, R.raw.som_derrota);
        playerSomVitoria = MediaPlayer.create(this, R.raw.som_vitoria1);

        configurarAcoes();
        atualizarPlacar();
    }

    private void configurarAcoes() {
        binding.btnPedra.setOnClickListener(v -> jogar(PEDRA));
        binding.btnPapel.setOnClickListener(v -> jogar(PAPEL));
        binding.btnTesoura.setOnClickListener(v -> jogar(TESOURA));

        binding.btnResetarJogo.setOnClickListener(v -> reiniciarPartida());

        binding.btnVoltar.setOnClickListener(v -> finish());

        binding.btnSair.setOnClickListener(v -> {
            Intent intent = new Intent(ArenaActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra(MainActivity.EXTRA_ULTIMO_MODULO, "Arena: Pedra, Papel e Tesoura");
            startActivity(intent);
            finish();
        });
    }

    private void jogar(int escolhaJogador) {
        if (partidaFinalizada) {
            return;
        }

        int escolhaCpu = random.nextInt(3);

        definirImagem(binding.escolhaJogador, escolhaJogador);
        definirImagem(binding.escolhaCpu, escolhaCpu);

        Animation animarJogadaPlayer = AnimationUtils.loadAnimation(this, R.anim.desloca_da_esquerda);
        Animation animarJogadaCpu = AnimationUtils.loadAnimation(this, R.anim.desloca_da_direita);

        animarDuelo(animarJogadaPlayer, animarJogadaCpu);

        if (escolhaJogador == escolhaCpu) {
            binding.resultadoRound.setText(getString(R.string.status_empate));
            binding.resultadoRound.setTextColor(getColor(R.color.termo_amarelo));
        } else if ((escolhaJogador == PEDRA && escolhaCpu == TESOURA) ||
                (escolhaJogador == PAPEL && escolhaCpu == PEDRA) ||
                (escolhaJogador == TESOURA && escolhaCpu == PAPEL)) {
            pontosJogador++;
            binding.resultadoRound.setText(getString(R.string.status_player_ganhar_round));
            binding.resultadoRound.setTextColor(getColor(R.color.termo_verde));
            tocarSom(playerSomVitoria);
        } else {
            pontosCpu++;
            binding.resultadoRound.setText(getString(R.string.status_rcpu_ganhar_round));
            binding.resultadoRound.setTextColor(getColor(R.color.neon_rosa));
            tocarSom(playerSomDerrota);
        }

        atualizarPlacar();
        verificarFimDeJogo();
    }

    private void definirImagem(android.widget.ImageView escolha, int jogada) {
        if (jogada == PEDRA) {
            escolha.setImageResource(R.drawable.pedra);
        } else if (jogada == PAPEL) {
            escolha.setImageResource(R.drawable.papel);
        } else {
            escolha.setImageResource(R.drawable.tesoura);
        }
    }

    private void animarDuelo(Animation animarJogadaPlayer, Animation animarJogadaCpu) {

        binding.escolhaJogador.startAnimation(animarJogadaPlayer);
        binding.escolhaCpu.startAnimation(animarJogadaCpu);

        binding.batalha.setScaleX(0.85f);
        binding.batalha.setScaleY(0.85f);
        binding.batalha.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(500)
                .start();
    }

    private void atualizarPlacar() {
        String textoPlacar = getString(R.string.marcador_jogador) + " " + pontosJogador +
                "  |  " +
                getString(R.string.marcador_dispositivo) + " " + pontosCpu;
        binding.pontuacao.setText(textoPlacar);
    }

    private void verificarFimDeJogo() {
        if (pontosJogador == 2 || pontosCpu == 2) {
            partidaFinalizada = true;
            binding.fimPartida.setText(R.string.fim_partida);
            binding.btnResetarJogo.setVisibility(View.VISIBLE);

            if (pontosJogador == 2) {
                binding.resultadoRound.setText(getString(R.string.status_final_vitoria));
                binding.resultadoRound.setTextColor(getColor(R.color.termo_verde));
            } else {
                binding.resultadoRound.setText(getString(R.string.status_final_perdeu));
                binding.resultadoRound.setTextColor(getColor(R.color.neon_rosa));
            }

            binding.btnPedra.setEnabled(false);
            binding.btnPapel.setEnabled(false);
            binding.btnTesoura.setEnabled(false);
        }
    }

    @SuppressLint("SetTextI18n")
    private void reiniciarPartida() {
        pontosJogador = 0;
        pontosCpu = 0;
        partidaFinalizada = false;

        atualizarPlacar();
        binding.resultadoRound.setText("Escolha uma opção acima para jogar");
        binding.resultadoRound.setTextColor(getColor(R.color.white));
        binding.btnResetarJogo.setVisibility(View.GONE);
        binding.fimPartida.setText("");

        binding.btnPedra.setEnabled(true);
        binding.btnPapel.setEnabled(true);
        binding.btnTesoura.setEnabled(true);
    }

    private void tocarSom(MediaPlayer mp) {
        if (mp != null) {
            try {
                if (mp.isPlaying()) {
                    mp.seekTo(0);
                } else {
                    mp.start();
                }
            } catch (Exception e) {
                Log.e(TAG, "Erro ao reproduzir som: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "ArenaActivity: onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "ArenaActivity: onPause()");
        if (playerSomVitoria != null && playerSomVitoria.isPlaying()) {
            playerSomVitoria.pause();
        }
        if (playerSomDerrota != null && playerSomDerrota.isPlaying()) {
            playerSomDerrota.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "ArenaActivity: onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "ArenaActivity: onDestroy()");
        if (playerSomVitoria != null) {
            playerSomVitoria.release();
            playerSomVitoria = null;
        }
        if (playerSomDerrota != null) {
            playerSomDerrota.release();
            playerSomDerrota = null;
        }
    }
}