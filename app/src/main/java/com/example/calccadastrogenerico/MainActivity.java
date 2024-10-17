package com.example.calccadastrogenerico;



import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.calccadastrogenerico.fragment.EditProductFragment;
import com.example.calccadastrogenerico.fragment.HomeFragment;
import com.example.calccadastrogenerico.model.NomeProduto;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements EditProductFragment.OnNomesListener {

    ArrayList<NomeProduto> minhaLista = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Definir o BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Carregar o fragmento inicial (HomeFragment) quando a Activity for criada
        //antes era replace
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.nav_host_fragment, new EditProductFragment())
                    .commit();
        }

// Configurar o listener para troca de fragmentos
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;

                int id = item.getItemId();

                if (id == R.id.navigation_home) {
                    selectedFragment = new HomeFragment();

                    // Passar a lista para o HomeFragment
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("listaProdutos", minhaLista);
                    // Criar uma instância do HomeFragment e passar os argumentos
                    HomeFragment homeFragment = new HomeFragment();
                    homeFragment.setArguments(bundle);

                    // Realizar a transição para o HomeFragment
                    selectedFragment = homeFragment;

                } else if (id == R.id.navigation_edit_product) {
                    selectedFragment = new EditProductFragment();
                }

                // Troca o fragmento
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, selectedFragment)
                        .commit();

                return true;
            }
        });

    }


    // Implementação da interface que recebe a lista de nomes
    @Override
    public void onEnviarNomes(ArrayList<NomeProduto> listaNomes) {

        minhaLista = listaNomes;

    }

}