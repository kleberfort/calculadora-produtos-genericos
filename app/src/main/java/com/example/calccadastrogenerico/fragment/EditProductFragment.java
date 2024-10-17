package com.example.calccadastrogenerico.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.calccadastrogenerico.R;
import com.example.calccadastrogenerico.adapter.ListaCadastroAdapter;
import com.example.calccadastrogenerico.databinding.FragmentEditProductBinding;
import com.example.calccadastrogenerico.model.NomeProduto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class EditProductFragment extends Fragment {

    private FragmentEditProductBinding binding;


    private RecyclerView recyclerView;
    private ListaCadastroAdapter adapter;



    // Nome do SharedPreferences e chave para salvar a lista
    private static final String PREFS_NAME = "my_prefs";
    private static final String LIST_KEY = "produtos_key";

    private ArrayList<NomeProduto> productList;

    private OnNomesListener listener;

    public interface OnNomesListener {
        void onEnviarNomes(ArrayList<NomeProduto> listaNomes);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verifica se a Activity implementa a interface
        if (context instanceof OnNomesListener) {
            listener = (OnNomesListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " deve implementar OnNomesListener");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar o layout do fragmento
        View view = inflater.inflate(R.layout.fragment_edit_product, container, false);

        // Inicializar a lista de produtos
        productList = new ArrayList<>();

        // Carregar a lista de produtos do SharedPreferences
        carregarListaProdutos();

        // Aqui você pode chamar o método da interface para passar a lista
        if (listener != null) {
            listener.onEnviarNomes(productList);
        }





        // Configurar o RecyclerView e o Adapter
        recyclerView = view.findViewById(R.id.recycler_view_edit_product);
        adapter = new ListaCadastroAdapter(getContext(), productList,
                this::showEditProductDialog, this::showDeleteConfirmationDialog);  // Passa o clique longo para deletar
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));




        // Configurar o FloatingActionButton
        FloatingActionButton fab = view.findViewById(R.id.fab_edit_product);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddProductDialog();

            }
        });

        return view;
    }

    // Método para abrir o dialog de editar produto
    private void showEditProductDialog(int position) {
        NomeProduto product = productList.get(position);

        // Inflar o layout do dialog
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_product, null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setView(dialogView);

        // Referências ao EditText para editar o produto
        EditText productNameInput = dialogView.findViewById(R.id.edit_text_product_name);
        productNameInput.setText(product.getNameProduct());

        // Configurar os botões do dialog
        dialogBuilder.setTitle("Editar Produto");
        dialogBuilder.setPositiveButton("Salvar", (dialog, which) -> {
            String novoNome = productNameInput.getText().toString().trim();

            if (!novoNome.isEmpty()) {
                novoNome = novoNome.substring(0, 1).toUpperCase() + novoNome.substring(1).toLowerCase();

                // Atualizar o produto
                product.setNameProduct(novoNome);
                adapter.notifyItemChanged(position);

                // Salvar a lista de produtos atualizada
                salvarListaProdutos();

                Toast.makeText(getContext(), "Produto atualizado com sucesso!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "O nome do produto não pode estar vazio!", Toast.LENGTH_SHORT).show();
            }
        });

        dialogBuilder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        // Mostrar o dialog
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }


    // Método para mostrar o diálogo de confirmação de exclusão
    private void showDeleteConfirmationDialog(int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Excluir Produto")
                .setMessage("Tem certeza de que deseja excluir este produto?")
                .setPositiveButton("Excluir", (dialog, which) -> {
                    // Remover o produto da lista
                    productList.remove(position);

                    // Notificar o adapter sobre a remoção
                    adapter.notifyItemRemoved(position);

                    // Salvar a lista de produtos atualizada
                    salvarListaProdutos();

                    Toast.makeText(getContext(), "Produto excluído", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    // Método para abrir o dialog de adicionar produto
    private void showAddProductDialog() {
        // Inflar o layout do dialog
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_add_product, null);

        // Criar o AlertDialog
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setView(dialogView);

        // Capturar a referência do EditText do nome do produto
        EditText productNameInput = dialogView.findViewById(R.id.edit_text_product_name);

        // Configurar botões do dialog
        dialogBuilder.setTitle("Adicionar Produto");
        dialogBuilder.setPositiveButton("Adicionar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String nomeProduto = productNameInput.getText().toString().trim();

                if (!nomeProduto.isEmpty()) {
                    // Transformar a primeira letra em maiúscula e o restante em minúsculo
                    nomeProduto = nomeProduto.substring(0, 1).toUpperCase() + nomeProduto.substring(1).toLowerCase();

                    // Criar um novo objeto NomeProduto com o nome capturado
                    NomeProduto novoProduto = new NomeProduto(nomeProduto);
                    // Adicionar o novo produto à lista
                    productList.add(novoProduto);

                    // Notificar o adapter que a lista mudou
                    adapter.notifyItemInserted(productList.size() - 1);

                    // Salvar a lista no SharedPreferences
                    salvarListaProdutos();


                    // Fechar o dialog e exibir uma mensagem de sucesso (opcional)
                    Toast.makeText(getContext(), "Produto adicionado com sucesso!", Toast.LENGTH_SHORT).show();

                } else {
                    // Mostrar uma mensagem de erro caso o campo esteja vazio
                    Toast.makeText(getContext(), "O nome do produto não pode estar vazio!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        dialogBuilder.setNegativeButton("Cancelar", null);

        // Mostrar o dialog
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }



    // Método para salvar a lista de produtos no SharedPreferences
    private void salvarListaProdutos() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String json = gson.toJson(productList);  // Converter a lista para JSON

        Log.d("MinhaLista", "Lista: " + json);

        editor.putString(LIST_KEY, json);
        editor.apply();  // Salva a mudança de forma assíncrona
    }

    // Método para carregar a lista de produtos do SharedPreferences
    private void carregarListaProdutos() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();

        String json = sharedPreferences.getString(LIST_KEY, null);

        Type type = new TypeToken<List<NomeProduto>>() {}.getType();
        productList = gson.fromJson(json, type);

        if (productList == null) {
            productList = new ArrayList<>();  // Se não houver dados salvos, inicializa uma lista vazia
        }
    }



}
