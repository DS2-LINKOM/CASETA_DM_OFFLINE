package mx.linkom.caseta_dm_offline;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mx.linkom.caseta_dm_offline.adaptadores.ListasClassGrid;
import mx.linkom.caseta_dm_offline.adaptadores.adaptador_Modulo;

public class RecepcionVisitasActivity extends  mx.linkom.caseta_dm_offline.Menu{

private GridView gridList,gridList2;
private Configuracion Conf;
        JSONArray ja1,ja2;
    Handler handler = new Handler();
    private final int TIEMPO = 6000;

        @Override
        protected void onCreate(Bundle savedInstanceState){
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_recepcionvisitas);

                Conf=new Configuracion(this);

            gridList=(GridView)findViewById(R.id.gridList);
            gridList2=(GridView)findViewById(R.id.gridList2);
            entradas();
            salidas();


        }

    @Override
    public void onStart() {
        super.onStart();

        onMapReady();
    }

    public void onMapReady() {
        handler.postDelayed(new Runnable() {
            public void run() {

                entradas();
                salidas();

                handler.postDelayed(this, TIEMPO);
            }

        }, TIEMPO);

    }

    public void entradas() {
        String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/visitas_recep_1.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {
                        ja1 = new JSONArray(response);
                        llenado1();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error ", "Id: " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                Map<String, String> params = new HashMap<>();
                params.put("id_residencial", Conf.getResid().trim());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void salidas() {
        String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/visitas_recep_2.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {
                        ja2 = new JSONArray(response);


                        llenado2();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error ", "Id: " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                Map<String, String> params = new HashMap<>();
                params.put("id_residencial", Conf.getResid().trim());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void llenado1(){
        ArrayList<ListasClassGrid> entradas = new ArrayList<ListasClassGrid>();

        for (int i = 0; i < ja1.length(); i += 3) {
            try {

                entradas.add(new ListasClassGrid(ja1.getString(i + 2), "ID:"+ja1.getString(i + 1)));
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        gridList.setAdapter(new adaptador_Modulo(this, R.layout.activity_listas, entradas){
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {

                    final TextView title = (TextView) view.findViewById(R.id.title);
                    if (title != null)
                        title.setText(((ListasClassGrid) entrada).getTitle());

                    final TextView subtitle = (TextView) view.findViewById(R.id.sub);
                    if (subtitle != null)
                        subtitle.setText(((ListasClassGrid) entrada).getSubtitle());

                    gridList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {


                            int posicion=position*3;
                            try {
                                Conf.setQR(ja1.getString(posicion));
                                Conf.setST("Aceptado");
                                //Conf.setQR("204");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                                Intent i = new Intent(getApplicationContext(), VisitasRecepEntradasDetalle.class);
                                startActivity(i);
                                finish();





                        }
                    });


                }
            }

        });
    }


    public void llenado2(){
        ArrayList<ListasClassGrid> salidas = new ArrayList<ListasClassGrid>();

        for (int i = 0; i < ja2.length(); i += 3) {
            try {

                salidas.add(new ListasClassGrid(ja2.getString(i + 2), "ID:"+ja2.getString(i + 1)));
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        gridList2.setAdapter(new adaptador_Modulo(this, R.layout.activity_listas, salidas){
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {

                    final TextView title = (TextView) view.findViewById(R.id.title);
                    if (title != null)
                        title.setText(((ListasClassGrid) entrada).getTitle());

                    final TextView subtitle = (TextView) view.findViewById(R.id.sub);
                    if (subtitle != null)
                        subtitle.setText(((ListasClassGrid) entrada).getSubtitle());

                    gridList2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {


                            int posicion=position*3;
                              try {
                             Conf.setQR(ja2.getString(posicion));
                                  Conf.setST("Aceptado");

                                  //Conf.setQR("204");
                             } catch (JSONException e) {
                                 e.printStackTrace();
                             }

                            Intent i = new Intent(getApplicationContext(), VisitasRecepSalidasDetalle.class);
                            startActivity(i);
                            finish();




                        }
                    });


                }
            }

        });
    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), EntradasSalidasActivity.class);
        startActivity(intent);
        finish();
    }


}

