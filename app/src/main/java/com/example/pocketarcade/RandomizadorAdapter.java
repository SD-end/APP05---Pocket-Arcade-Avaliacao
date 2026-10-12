package com.example.pocketarcade;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pocketarcade.databinding.ItemRandomizadorBinding;

import java.util.List;

public class RandomizadorAdapter extends RecyclerView.Adapter<RandomizadorAdapter.ViewHolder> {

    public interface OnItemLongClickListener {
        void onItemLongClick(int posicao);
    }

    private final List<ItemSorteio> listaItens;
    private final OnItemLongClickListener longClickListener;

    public RandomizadorAdapter(List<ItemSorteio> listaItens, OnItemLongClickListener longClickListener) {
        this.listaItens = listaItens;
        this.longClickListener = longClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRandomizadorBinding binding = ItemRandomizadorBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemSorteio item = listaItens.get(position);
        holder.binding.palavra.setText(item.getPalavra());
        holder.binding.categoria.setText(item.getCategoria());

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(holder.getAdapterPosition());
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listaItens.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemRandomizadorBinding binding;

        public ViewHolder(@NonNull ItemRandomizadorBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}