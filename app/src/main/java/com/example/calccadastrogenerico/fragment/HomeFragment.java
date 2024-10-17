package com.example.calccadastrogenerico.fragment;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.calccadastrogenerico.R;
import com.example.calccadastrogenerico.adapter.ProdutoAdapter;

import com.example.calccadastrogenerico.databinding.FragmentHomeBinding;
import com.example.calccadastrogenerico.model.ListaComprasSupermecado;
import com.example.calccadastrogenerico.model.NomeProduto;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment    {

    private FragmentHomeBinding binding;

    private String nomeProduto;
    private double quantidadeProtudo = 0;
    private double valorUnidadeProduto = 0.0;
    private double valorQdtProduto = 0.0;
    private double valorQtdTotalPrudutos = 0.0;

    private int quantidadeProdutosLista = 0;

    private List<ListaComprasSupermecado> produtos = new ArrayList<>(); // Inicializando a lista de produtos
    private ProdutoAdapter produtoAdapter; // Adapter para o RecyclerView

    private ArrayList<NomeProduto> productList = new ArrayList<NomeProduto>();
    ArrayList<String> listaSpiner;

    private static final String PREFS_NAME = "my_preferences";

    private static final String KEY_PRODUTOS = "produtos";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflando o layout usando DataBinding
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        //removerListaItensAtual();

        // Recuperar lista de produtos do SharedPreferences
        recuperarProdutos();

        listaSpiner = new ArrayList<>();

        if (getArguments() != null) {
            productList = getArguments().getParcelableArrayList("listaProdutos");
            Log.d("BundleCheck", "Product list size: " + (productList != null ? productList.size() : "null"));
        }


////            // Adicionar itens à lista do Spinner
        if (productList != null && !productList.isEmpty()) {
            for (NomeProduto produto : productList) {
                String nomeProduto = produto.getNameProduct();
                if (nomeProduto != null) {
                    listaSpiner.add(nomeProduto);
                    Log.d("Spinner", "Produto adicionado: " + nomeProduto);
                }
            }
        }

        // Certifique-se de que o adapter está sendo atualizado corretamente.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.test_list_item, listaSpiner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerNomes.setAdapter(adapter);
        adapter.notifyDataSetChanged();  // Chame depois de vincular o adapter.


        binding.spinnerNomes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Recupera o item selecionado e o coloca na variável nomeProduto
                nomeProduto = parent.getItemAtPosition(position).toString();

                // Agora você pode usar a variável nomeProduto como desejar
                Log.d("SpinnerSelection", "Item selecionado: " + nomeProduto);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Ação quando nada é selecionado (opcional)

                Toast.makeText(getContext(), "Mensagem do Toast", Toast.LENGTH_SHORT).show();
                //  Log.d("SpinnerSelection", "Item selecionado: " + nomeProduto);
            }
        });


        binding.editTextQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                Log.d("beforeTextChanged", "Texto após a mudança: " + charSequence);

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                Log.d("onTextChanged", "Texto após a mudança: " + charSequence);

                String oquevem = charSequence.toString();
                // Este método é chamado enquanto o texto está sendo alterado
                if (!charSequence.toString().isEmpty()) {
                    try {
                        // Converta o texto para Double
                        quantidadeProtudo = Double.parseDouble(charSequence.toString());

                        // Exibir a quantidade capturada
                        Log.d("Quantidade", "Valor capturado: " + quantidadeProtudo);
                    } catch (NumberFormatException e) {
                        // Tratar erros de conversão
                        Log.e("Erro", "Erro ao converter para Integer: " + e.getMessage());
                    }
                } else {
                    // Se o campo estiver vazio, defina como null ou zero
                    quantidadeProtudo = 0; // ou 0 se preferir
                    Log.d("Quantidade", "Campo vazio");
                }

                updateTotalValue();

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d("afterTextChanged", "Texto após a mudança: " + editable);
            }
        });

        // Adicionar EditText para captura do valor unitário do produto
        binding.editTextUnitValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!charSequence.toString().isEmpty()) {
                    try {
                        // Converta o texto para Double
                        valorUnidadeProduto = Double.parseDouble(charSequence.toString());

                        // Exibir a quantidade capturada
                        Log.d("ValorUnidadeProduto", "Valor capturado: " + valorUnidadeProduto);
                    } catch (NumberFormatException e) {
                        // Tratar erros de conversão
                        Log.e("Erro", "Erro ao converter para Integer: " + e.getMessage());
                    }
                } else {
                    // Se o campo estiver vazio, defina como null ou zero
                    valorUnidadeProduto = 0.0; // ou 0 se preferir
                    Log.d("ValorUnidadeProduto", "Campo vazio");
                }

                updateTotalValue();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        // Configurar o botão de adicionar
        binding.imgAdd.setOnClickListener(v -> adicionarProduto());


//        // Configurar o RecyclerView
        RecyclerView recyclerView = binding.rvListaProdutos; // Certifique-se de que o RecyclerView está no seu layout
        // produtoAdapter = new ProdutoAdapter(produtos);

        produtoAdapter = new ProdutoAdapter(getContext(), produtos,
                this::showEditProductDialog, this::showDeleteConfirmationDialog);  // Passa o clique longo para deletar

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(produtoAdapter);


        binding.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Cria o AlertDialog
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Excluir Lista")
                        .setMessage("Você tem certeza que deseja excluir toda a lista?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Ação ao clicar em "Sim" - Excluir a lista
                                removerListaItensAtual();

                            }
                        })
                        .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Fechar o diálogo ao clicar em "Não"
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
            }
        });

        binding.imgCompartilhar.setOnClickListener(view -> {
            // Cria o PopupMenu
            PopupMenu popup = new PopupMenu(getActivity(), view);
            // Infla o menu usando o XML
            popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

            // Define ações para cada item do menu
            popup.setOnMenuItemClickListener(item -> {
                // Verifica qual item foi clicado usando if-else
                if (item.getItemId() == R.id.menu_pdf) {
                    // Ação para visualizar em PDF
                    salvarListaEmPDF(produtos);
                    return true;
                } else if (item.getItemId() == R.id.menu_compartilhar) {
                    // Ação para compartilhar
                    compartilhar();
                    return true;
                } else {
                    return false;
                }
            });
            // Mostra o PopupMenu
            popup.show();
        });


        // Retornando a raiz do layout
        return binding.getRoot();
    }//fim do método oncreate

    private void visualizarPDF() {
        // Implemente a lógica para visualizar o PDF
        Toast.makeText(getContext(), "Visualizar em PDF", Toast.LENGTH_SHORT).show();
    }

    private void compartilhar() {
        // Implemente a lógica para compartilhar
        Toast.makeText(getContext(), "Compartilhar", Toast.LENGTH_SHORT).show();
    }


    // Método para abrir o dialog de editar produto
    private void showEditProductDialog(int position) {
        ListaComprasSupermecado lista = produtos.get(position);

        // Inflar o layout do dialog
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.editar_campos_lista, null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setView(dialogView);

        // Referências ao EditText para editar o produto
        EditText quantidadeProduto = dialogView.findViewById(R.id.editTextQuantity);
        quantidadeProduto.setText(lista.getQtdUnidadeProduto().toString());

        EditText valorProduto = dialogView.findViewById(R.id.editTextUnitValue);
        valorProduto.setText(lista.getValorUnidadeProduto().toString());

        TextView nomeProducto = dialogView.findViewById(R.id.tv_nome_produto);
        nomeProducto.setText(lista.getNameProduct());

        TextView totalProduto = dialogView.findViewById(R.id.tv_total);
        totalProduto.setText(lista.getValorQdtxUndProduto().toString());

        // Configurar os botões do dialog para a quantidade do produto
        dialogBuilder.setTitle("Editar Produto");
        dialogBuilder.setPositiveButton("Salvar", (dialog, which) -> {
            Double qtdProduto = Double.valueOf(quantidadeProduto.getText().toString().trim());
            Double valorUnid = Double.valueOf(valorProduto.getText().toString().trim());

            if (!(qtdProduto == 0) || !(valorUnid == 0)) {
                String nomeIten = lista.getNameProduct();

                // Atualizar o produto
                lista.setQtdProduto(qtdProduto);
                lista.setValorUndProduto(valorUnid);

                // Atualizar o valor total do produto (quantidade * valor unitário)
//                double valorTotalProduto = qtdProduto * valorUnid;
//               lista.setQtdUnidadeProduto(valorTotalProduto);



                // Atualizar o valor total geral (somar o valorTotalProduto ao total)
                atualizarValorTotal();



                produtoAdapter.notifyItemChanged(position);


                // Salvar a lista de produtos atualizada
                salvarProdutos();

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

        // Identifica o produto que está sendo excluído
        String produtoExcluir = String.valueOf(produtos.get(position));

        // Crie uma SpannableString para formatar a mensagem
        String message = "Deseja excluir esta linha, referente ao item: " + produtoExcluir + " ?";
        SpannableString spannableString = new SpannableString(message);

// Encontre o índice onde o nome do produto começa e termina
        int start = message.indexOf(produtoExcluir);
        int end = start + produtoExcluir.length();

// Aplique o estilo negrito ao nome do produto
        spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);


        new AlertDialog.Builder(getContext())
                .setTitle("Excluir Produto")
                .setMessage(spannableString)
                .setPositiveButton("Excluir", (dialog, which) -> {
                    // Remover o produto da lista
                    produtos.remove(position);

                    // Atualizar a quantidade de produtos na lista
                    quantidadeProdutosLista = produtos.size();

                    // Atualizar a TextView com a nova quantidade de produtos
                    binding.tvQantidadeProdutos.setText("Qtd Produtos: " + quantidadeProdutosLista);

                    // Notificar o adapter sobre a remoção
                    produtoAdapter.notifyItemRemoved(position);

                    // Notificar o adapter que o restante dos itens mudou
                    produtoAdapter.notifyItemRangeChanged(position, produtos.size());

                    // Salvar a lista de produtos atualizada
                    atualizarValorTotal();

                    salvarProdutos();

                    Toast.makeText(getContext(), "Produto excluído", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }



    private void recuperarProdutos() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(KEY_PRODUTOS, null);
        Type type = new TypeToken<ArrayList<ListaComprasSupermecado>>() {}.getType();


        if (json != null) {
            produtos = gson.fromJson(json, type);
        } else {

            produtos = new ArrayList<>(); // Inicializar a lista vazia se não houver dados
        }

        // Recuperar a quantidade total de produtos
        int quantidadeTotalProdutos = sharedPreferences.getInt("quantidade_total_produtos", 0);

        quantidadeProdutosLista = quantidadeTotalProdutos;
        // Recuperar o valor total de todos os produtos
        float valorTotalProdutos = sharedPreferences.getFloat("valor_total_produtos", 0.0f);

        // Atualizar a interface com os valores recuperados
        binding.tvQantidadeProdutos.setText("Qtd Produtos: " + quantidadeProdutosLista);
        valorQtdTotalPrudutos = valorTotalProdutos;

        //valorQtdTotalPrudutos += valorQdtProduto;
        binding.textViewTotal.setText(String.format("Total: R$ %.2f", valorQtdTotalPrudutos));

        // Atualizar o RecyclerView com a lista recuperada
        produtoAdapter = new ProdutoAdapter(produtos);
        binding.rvListaProdutos.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvListaProdutos.setAdapter(produtoAdapter);
    }


    private void salvarProdutos() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(produtos);
        editor.putString(KEY_PRODUTOS, json);

        // Salvar a quantidade total de produtos
        editor.putInt("quantidade_total_produtos", produtos.size());
        // Salvar o valor total de todos os produtos
        editor.putFloat("valor_total_produtos", (float) valorQtdTotalPrudutos); // Converter para float se necessário



        editor.apply();

    }


    // Método para atualizar o valor total
    private void updateTotalValue() {
        if (quantidadeProtudo != 0 && valorUnidadeProduto > 0) {
            valorQdtProduto = quantidadeProtudo * valorUnidadeProduto;
            binding.textViewTotalValue.setText(String.format("R$ %.2f", valorQdtProduto));
        } else {
            binding.textViewTotalValue.setText("R$ 0,00");
        }
    }

    private void adicionarProduto() {

        // Verificar se a quantidade foi preenchida e é maior que 0
        if (quantidadeProtudo == 0 || quantidadeProtudo <= 0) {
            Toast.makeText(getContext(), "Por favor, informe uma quantidade válida", Toast.LENGTH_SHORT).show();
            binding.editTextQuantity.requestFocus(); // Define o foco no campo de quantidade
            return; // Interrompe a execução se a quantidade não for válida
        }

        // Verificar se o valor unitário foi preenchido e é maior que 0
        if (valorUnidadeProduto <= 0) {
            Toast.makeText(getContext(), "Por favor, informe o valor da unidade do produto ", Toast.LENGTH_SHORT).show();
            binding.editTextUnitValue.requestFocus(); // Define o foco no campo de valor unitário
            return; // Interrompe a execução se o valor unitário não for válido
        }


        // Adicionar um novo produto à lista
        ListaComprasSupermecado novoProduto = new ListaComprasSupermecado(nomeProduto, quantidadeProtudo, valorUnidadeProduto, valorQdtProduto);
        produtos.add(novoProduto);

        // Passar o novo produto para o adapter
        produtoAdapter.addProduto(novoProduto);



        //recupera o valor do total do sharedPreference e ao adicionar o item já soma com o valor total desse item.
        valorQtdTotalPrudutos += valorQdtProduto;
        quantidadeProdutosLista += 1;
        binding.textViewTotal.setText(String.format("Total: R$ %.2f", valorQtdTotalPrudutos));
        binding.tvQantidadeProdutos.setText("Qtd Produtos: " + quantidadeProdutosLista);



        // Limpar os campos de quantidade, valor da unidade e o total do produto
        binding.editTextQuantity.setText(""); // Limpa o campo de quantidade
        binding.editTextUnitValue.setText(""); // Limpa o campo de valor unitário
        binding.textViewTotalValue.setText("R$ 0,00"); // Reseta o valor total do produto

//        quantidadeProtudo = 0;
//        valorUnidadeProduto = 0.0;
//        valorQdtProduto = 0.0;

        // Salvar a lista de produtos no SharedPreferences
        salvarProdutos();

    }

    private void removerListaItensAtual(){
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Remover a lista de produtos
        editor.remove(KEY_PRODUTOS);

        // Remover a quantidade total de produtos
        editor.remove("quantidade_total_produtos");
        // Remover o valor total de todos os produtos
        editor.remove("valor_total_produtos");

        // Aplicar as mudanças
        editor.apply();

        recuperarProdutos();
    }

    private void atualizarValorTotal() {
        valorQtdTotalPrudutos = 0.0;

        // Somar o valor total de cada produto na lista
        for (ListaComprasSupermecado produto : produtos) {
            valorQtdTotalPrudutos += produto.getValorQdtxUndProduto();
        }

        // Salvar o valor total no SharedPreferences
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("PREFS_NAME", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("valorQtdTotalPrudutos", (float) valorQtdTotalPrudutos);
        editor.apply();

        binding.textViewTotal.setText(String.format("Total: R$ %.2f", valorQtdTotalPrudutos));

//        // Atualizar a UI, se necessário (exemplo: TextView que mostra o valor total)
//        TextView totalGeralView = getView().findViewById(R.id.tv_total_geral);
//        totalGeralView.setText(String.format(Locale.getDefault(), "%.2f", valorQtdTotalPrudutos));
    }

    private void salvarListaEmPDF(List<ListaComprasSupermecado> produtos) {
        // Cria um documento PDF
        PdfDocument documentoPDF = new PdfDocument();

        // Define as dimensões da página (A4)
        PdfDocument.PageInfo paginaInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page pagina = documentoPDF.startPage(paginaInfo);

        // Canvas para desenhar o conteúdo no PDF
        Canvas canvas = pagina.getCanvas();
        Paint paint = new Paint();
        paint.setTextSize(14); // Reduzi o tamanho da fonte para caber melhor

        // Margens e posições iniciais
        int xStart = 40; // Margem esquerda ajustada para menos afastamento
        int yStart = 80;
        int y = yStart;

        // Largura máxima da tabela, ajustada para não ultrapassar a borda
        int larguraMaxima = 510; // Ajustei ligeiramente para garantir mais espaço interno

        // Cabeçalho do documento
        paint.setFakeBoldText(true);  // Negrito para o título
        canvas.drawText("Lista de Compras", xStart, y, paint);
        y += 40;

        // Posições das colunas
        int colProduto = xStart + 10;        // Pequeno afastamento inicial do nome do produto
        int colQuantidade = colProduto + 170; // Posição para quantidade
        int colUnidade = colQuantidade + 120; // Posição para unidade
        int colTotal = colUnidade + 100;     // Reduzi o espaçamento do total para caber dentro da tabela

        // Cor e borda para o cabeçalho
        paint.setStyle(Paint.Style.STROKE);  // Modo de contorno para desenhar bordas
        paint.setStrokeWidth(2);

        // Cor de fundo cinza claro para o cabeçalho
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.LTGRAY);  // Cor de fundo cinza claro
        canvas.drawRect(xStart, y, larguraMaxima, y + 30, backgroundPaint);  // Fundo do cabeçalho

        // Desenhar a borda do cabeçalho
        canvas.drawRect(xStart, y, larguraMaxima, y + 30, paint);

        // Texto do cabeçalho
        paint.setStyle(Paint.Style.FILL);  // Modo de preenchimento para o texto
        paint.setFakeBoldText(true);       // Negrito para o cabeçalho
        paint.setTextSize(14);
        canvas.drawText("Produto", colProduto, y + 20, paint);
        canvas.drawText("Quantidade/Peso", colQuantidade, y + 20, paint); // Quantidade/Peso
        canvas.drawText("Unidade/Kg", colUnidade, y + 20, paint); // Unidade/Kg
        canvas.drawText("Total (R$)", colTotal, y + 20, paint); // Total (R$)
        y += 30; // Pular para a próxima linha

        // Adiciona borda inferior ao cabeçalho
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawLine(xStart, y, larguraMaxima, y, paint);
        y += 10; // Espaçamento entre o cabeçalho e os dados

        // Variáveis para cor de fundo alternada
        Paint alternateRowPaint = new Paint();
        boolean isRowGray = false; // Começa com a primeira linha branca

        // Variáveis para total geral e quantidade total de produtos
        double totalGeral = 0;
        int quantidadeTotalProdutos = produtos.size();  // Quantidade total de produtos

        // Adicionar produtos na tabela com fundo alternado
        for (ListaComprasSupermecado item : produtos) {
            // Alterna a cor do fundo (cinza e branco)
            if (isRowGray) {
                alternateRowPaint.setColor(Color.LTGRAY);  // Cor cinza claro
            } else {
                alternateRowPaint.setColor(Color.WHITE);  // Cor branca
            }
            isRowGray = !isRowGray;

            // Desenha o fundo da linha (baseado na cor alternada)
            canvas.drawRect(xStart, y, larguraMaxima, y + 30, alternateRowPaint);

            // Preenchimento do texto
            paint.setStyle(Paint.Style.FILL);
            canvas.drawText(item.getNameProduct(), colProduto, y + 20, paint); // Nome do produto
            canvas.drawText(String.valueOf(item.getQtdUnidadeProduto()), colQuantidade, y + 20, paint); // Quantidade/Peso
            canvas.drawText(item.getValorUnidadeProduto().toString(), colUnidade, y + 20, paint); // Unidade/Kg
            canvas.drawText(String.format("R$ %.2f", item.getValorQdtxUndProduto()), colTotal - 10, y + 20, paint); // Ajustado o afastamento do total
            y += 30;  // Ajustar espaçamento entre as linhas

            totalGeral += item.getValorQdtxUndProduto();  // Somar o total

            // Desenhar a borda para a linha
            paint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(xStart, y - 30, larguraMaxima, y, paint); // Borda da linha
        }

        // Adicionar uma linha de divisão após a tabela
        y += 10;
        canvas.drawLine(xStart, y, larguraMaxima, y, paint);
        y += 20;

        // Quantidade total de produtos (fora da tabela)
        paint.setFakeBoldText(true);  // Deixa o texto em negrito novamente
        paint.setStyle(Paint.Style.FILL);  // Preencher o texto
        canvas.drawText("Quantidade total de produtos: " + quantidadeTotalProdutos, xStart, y, paint);
        y += 30;

        // Valor total geral (fora da tabela)
        canvas.drawText("Valor total geral: R$ " + String.format("%.2f", totalGeral), xStart, y, paint);

        // Finalizar a página
        documentoPDF.finishPage(pagina);

        // Salvar o documento no arquivo
        File file = new File(requireContext().getExternalFilesDir(null), "lista_compras.pdf");

        try {
            documentoPDF.writeTo(new FileOutputStream(file));
            Toast.makeText(requireContext(), "PDF salvo com sucesso!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(requireContext(), "Erro ao salvar o PDF", Toast.LENGTH_SHORT).show();
        } finally {
            documentoPDF.close();
        }

        // Abrir o PDF após salvar
        abrirPDF(file);
    }






    private void abrirPDF(File file) {
        // Verifica se o arquivo existe
        if (file.exists()) {
            // Intent para abrir o PDF
            Intent intent = new Intent(Intent.ACTION_VIEW);

            // Obtém o URI do arquivo usando FileProvider para Android 7.0+
            Uri uri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", file);

            // Define o tipo de conteúdo como PDF
            intent.setDataAndType(uri, "application/pdf");

            // Concede permissão de leitura temporária ao PDF
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try {
                // Inicia o Intent para abrir o PDF
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(requireContext(), "Nenhum visualizador de PDF encontrado", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(requireContext(), "Arquivo PDF não encontrado", Toast.LENGTH_SHORT).show();
        }
    }

}
