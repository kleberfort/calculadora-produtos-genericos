package com.example.calccadastrogenerico.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calccadastrogenerico.R;
import com.example.calccadastrogenerico.model.NomeProduto;

import java.util.List;

public class ListaCadastroAdapter extends RecyclerView.Adapter<ListaCadastroAdapter.ViewHolder> {

    private Context context;
    private List<NomeProduto> productList;
    private OnItemClickListener onItemClickListener;
    private OnItemLongClickListener onItemLongClickListener;

    // Interface para cliques
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Interface para clique longo
    public interface OnItemLongClickListener {
        void onItemLongClick(int position);
    }

    public ListaCadastroAdapter(Context context, List<NomeProduto> productList,
                                OnItemClickListener onItemClickListener,
                                OnItemLongClickListener onItemLongClickListener) {
        this.context = context;
        this.productList = productList;
        this.onItemClickListener = onItemClickListener;
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar o layout do item para o RecyclerView
        View view = LayoutInflater.from(context).inflate(R.layout.cadastro_produto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NomeProduto product = productList.get(position);

        holder.bind(product, position, onItemClickListener, onItemLongClickListener);
    }

    @Override
    public int getItemCount() {
        // Retorna o tamanho da lista de produtos
        return productList.size();
    }

    // Classe ViewHolder que referencia as Views do layout do item
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomeProdutoTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Referenciar o TextView do layout item_cadastro_produto.xml
            nomeProdutoTextView = itemView.findViewById(R.id.tv_nome_produto);
        }

        public void bind(NomeProduto product, int position, OnItemClickListener clickListener,
                         OnItemLongClickListener longClickListener) {
            nomeProdutoTextView.setText(product.getNameProduct());

            itemView.setOnClickListener(v -> clickListener.onItemClick(position));

            // Detectar clique longo
            itemView.setOnLongClickListener(v -> {
                longClickListener.onItemLongClick(position);
                return true;
            });
        }
    }


}


