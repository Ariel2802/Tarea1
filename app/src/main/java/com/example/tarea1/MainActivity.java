package com.example.tarea1;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import interfazRevista.InterfazRevista;
import modelo.Revista;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    EditText lbl;
    Button btnnRetroit;
    EditText txt;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lbl = findViewById(R.id.lblResultado);
        btnnRetroit = findViewById(R.id.btnRetrofit);
        txt = findViewById(R.id.txtID);

        requestQueue = Volley.newRequestQueue(this);

    }

    public void eventoButtonRetrofit(View v) {
        buscarRetrofit(txt.getText().toString(), "https://revistas.uteq.edu.ec/ws/");
    }

    public void eventoButtonVolley(View v) {
        buscarVolley(txt.getText().toString(), "https://revistas.uteq.edu.ec/ws/issues.php");
    }

    private void buscarRetrofit(String id, String url) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create()).build();

        InterfazRevista interfazRevista = retrofit.create(InterfazRevista.class);

        Call<List<Revista>> call = interfazRevista.buscar(id);
        call.enqueue(new Callback<List<Revista>>() {
            @Override
            public void onResponse(Call<List<Revista>> call, Response<List<Revista>> response) {
                try {
                    if (response.isSuccessful()) {
                        lbl.setText("Con Retrofit\n");
                        List<Revista> revistas = response.body();
                        for (int i = 0; i < revistas.size(); i++) {
                            lbl.append(revistas.get(i).toString());
                        }
                    }
                } catch (Exception ex) {
                    lbl.setText("Con Retrofit\nNo hay resultados");
                    Toast.makeText(MainActivity.this, "No hay resultados", Toast.LENGTH_SHORT);
                }
            }

            @Override
            public void onFailure(Call<List<Revista>> call, Throwable t) {
                lbl.setText("Con Retrofit\nNo hay resultados");
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT);
            }
        });
    }

    private void buscarVolley(String id, String url) {
        url += "?j_id=" + id;
        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new com.android.volley.Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        int tamanio = response.length();
                        lbl.setText("Con Volley\n");
                        for (int i = 0; i < tamanio; i++) {
                            try {
                                JSONObject json = new JSONObject(response.get(i).toString());
                                Revista revista = new Revista(json.getInt("issue_id"),
                                        json.getInt("volume"), json.getInt("number"),
                                        json.getInt("year"), json.getString("date_published"),
                                        json.getString("title"), json.getString("doi"),
                                        json.getString("cover"));
                                lbl.append(revista.toString());
                            } catch (JSONException ex) {
                                lbl.append("No hay resultados");
                                System.out.println(ex.toString());
                            }
                        }
                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError ex) {
                lbl.append("No hay resultados");
                System.out.println(ex.toString());
            }
        });
        requestQueue.add(jsonRequest);

        /* .baseUrl("https://revistas.uteq.edu.ec/ws/")*/
    }
}