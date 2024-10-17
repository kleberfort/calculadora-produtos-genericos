package com.example.calccadastrogenerico.model;

import android.os.Parcel;
import android.os.Parcelable;

public class NomeProduto implements Parcelable {

    private String nameProduct;

    // Construtor
    public NomeProduto(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    // Getters e Setters
    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProduct) {
        this.nameProduct = nameProduct;
    }

    // Métodos para implementar Parcelable
    protected NomeProduto(Parcel in) {
        nameProduct = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nameProduct);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<NomeProduto> CREATOR = new Creator<NomeProduto>() {
        @Override
        public NomeProduto createFromParcel(Parcel in) {
            return new NomeProduto(in);
        }

        @Override
        public NomeProduto[] newArray(int size) {
            return new NomeProduto[size];
        }
    };

    @Override
    public String toString() {
        return nameProduct; // Retorna o nome do produto
    }
}
