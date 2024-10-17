package com.example.calccadastrogenerico.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ListaComprasSupermecado extends NomeProduto implements Parcelable {

    private Double qtdProduto;
    private Double valorUndProduto;
    private Double valorQdtxUndProduto;
    private Double valorQtdTotalProdutos;

    // Construtor
    public ListaComprasSupermecado(String nameProduct, Double qtdProduto, Double valorUndProduto, Double valorQdtxUndProduto ) {
        super(nameProduct); // Chama o construtor da superclasse
        this.qtdProduto = qtdProduto;
        this.valorUndProduto = valorUndProduto;
        this.valorQdtxUndProduto = calcularValorQdtUnidadeProduto(); // Supondo que você tenha um método para calcular
        this.valorQtdTotalProdutos = calcularValorQtdTotalProdutos(); // Supondo que você tenha um método para calcular
    }

    public ListaComprasSupermecado(String nameProduct, Double qtdProduto, Double valorUndProduto, Double valorQdtxUndProduto, Double valorQtdTotalProdutos) {
        super(nameProduct);
        this.qtdProduto = qtdProduto;
        this.valorUndProduto = valorUndProduto;
        this.valorQdtxUndProduto = valorQdtxUndProduto;
        this.valorQtdTotalProdutos = valorQtdTotalProdutos;
    }

    // Métodos de cálculo (caso você queira implementar)
    private Double calcularValorQdtUnidadeProduto() {
        return qtdProduto * valorUndProduto;
    }

    private Double calcularValorQtdTotalProdutos() {
        return valorQdtxUndProduto; // Ajuste conforme necessário
    }

    // Getters e Setters
    public Double getQtdUnidadeProduto() {
        return qtdProduto;
    }

    public void setQtdProduto(Double qtdUnidadeProduto) {
        this.qtdProduto = qtdUnidadeProduto;
    }

    public Double getValorUnidadeProduto() {
        return valorUndProduto;
    }

    public void setValorUndProduto(Double valorUnidadeProduto) {
        this.valorUndProduto = valorUnidadeProduto;
        this.valorQdtxUndProduto = calcularValorQdtUnidadeProduto(); // Atualiza ao modificar
    }

    public Double getValorQdtxUndProduto() {
        return valorQdtxUndProduto;
    }

    public Double getValorQtdTotalProdutos() {
        return valorQtdTotalProdutos;
    }

    // Métodos para implementar Parcelable
    protected ListaComprasSupermecado(Parcel in) {
        super(in.readString()); // Chama o construtor da superclasse
        qtdProduto = (Double) in.readValue(Double.class.getClassLoader());
        valorUndProduto = in.readDouble();
        valorQdtxUndProduto = in.readDouble();
        valorQtdTotalProdutos = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getNameProduct()); // Escreve o nome do produto
        dest.writeValue(qtdProduto);
        dest.writeDouble(valorUndProduto);
        dest.writeDouble(valorQdtxUndProduto);
        dest.writeDouble(valorQtdTotalProdutos);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ListaComprasSupermecado> CREATOR = new Creator<ListaComprasSupermecado>() {
        @Override
        public ListaComprasSupermecado createFromParcel(Parcel in) {
            return new ListaComprasSupermecado(in);
        }

        @Override
        public ListaComprasSupermecado[] newArray(int size) {
            return new ListaComprasSupermecado[size];
        }
    };
}