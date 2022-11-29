package mx.linkom.caseta_dm_offline;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mx.linkom.caseta_dm_offline.adaptadores.ModuloClassGrid;
import mx.linkom.caseta_dm_offline.adaptadores.adaptador_Modulo;

public class EntradasSalidasActivity extends  mx.linkom.caseta_dm_offline.Menu {
    private FirebaseAuth fAuth;
    private Configuracion Conf;
    JSONArray ja1;
    public GridView gridList,gridList2,gridList3,gridList4,gridList5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entradassalidas);
        fAuth = FirebaseAuth.getInstance();
        Conf = new Configuracion(this);
        gridList = (GridView)findViewById(R.id.gridList);
        gridList2 = (GridView)findViewById(R.id.gridList2);
        gridList3 = (GridView)findViewById(R.id.gridList3);
        gridList4 = (GridView)findViewById(R.id.gridList4);
        gridList5 = (GridView)findViewById(R.id.gridList5);


    }
    @Override
    public void onStart() {
        super.onStart();
        menu();
    }



    public void menu() {

        String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/menu.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {

                        ja1 = new JSONArray(response);

                        if(ja1.getString(2).equals("1")) {
                            Conf.setTicketE(ja1.getString(11));
                        }else{
                            Conf.setTicketE("0");
                        }

                        if(ja1.getString(3).equals("1")) {
                            Conf.setTicketR(ja1.getString(12));
                        }else{
                            Conf.setTicketR("0");
                        }


                        llenado();
                        llenado2();
                        llenado3();

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), "Usuario y/o Contraseña Incorrectos", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "NO HAY CONEXIÓN A INTERNET", Toast.LENGTH_LONG).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Error: " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_residencial", Conf.getResid());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }




    public void llenado(){
        ArrayList<ModuloClassGrid> lista = new ArrayList<ModuloClassGrid>();

        try {
            if(ja1.getString(2).equals("1")  ){
                Conf.setPreQr(ja1.getString(4));

                lista.add(new ModuloClassGrid(R.drawable.entradas,"Entradas","#FF4081"));
                lista.add(new ModuloClassGrid(R.drawable.entradas,"Salidas","#4cd2c7"));
            }else{

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


        gridList.setAdapter(new adaptador_Modulo(this, R.layout.activity_modulo_lista, lista){
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {
                    ImageView add = (ImageView) view.findViewById(R.id.imageView);
                    if (add != null)
                        add.setImageResource(((ModuloClassGrid) entrada).getImagen());

                    final TextView title = (TextView) view.findViewById(R.id.title);
                    if (title != null)
                        title.setText(((ModuloClassGrid) entrada).getTitle());

                    final LinearLayout line = (LinearLayout) view.findViewById(R.id.line);
                    if (line != null)
                        line.setBackgroundColor(Color.parseColor(((ModuloClassGrid) entrada).getColorCode()));

                    gridList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                            if(position==0) {
                                Intent docugen = new Intent(getApplication(), EscaneoVisitaActivity.class);
                                startActivity(docugen);
                                finish();
                            }else if(position==1){
                                Intent docugen = new Intent(getApplication(), EscaneoVisitaSalidaActivity.class);
                                startActivity(docugen);
                                finish();
                            }

                        }
                    });

                }
            }

        });
    }

    public void llenado2(){
        ArrayList<ModuloClassGrid> lista4 = new ArrayList<ModuloClassGrid>();

        try {


            if(ja1.getString(5).equals("1")  ){
                lista4.add(new ModuloClassGrid(R.drawable.entradas,"Entradas Trabaja.","#FF4081"));
                lista4.add(new ModuloClassGrid(R.drawable.entradas,"Salidas Trabaja.","#4cd2c7"));
            }else{

            }




        } catch (JSONException e) {
            e.printStackTrace();
        }


        gridList4.setAdapter(new adaptador_Modulo(this, R.layout.activity_modulo_lista, lista4){
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {
                    ImageView add = (ImageView) view.findViewById(R.id.imageView);
                    if (add != null)
                        add.setImageResource(((ModuloClassGrid) entrada).getImagen());

                    final TextView title = (TextView) view.findViewById(R.id.title);
                    if (title != null)
                        title.setText(((ModuloClassGrid) entrada).getTitle());

                    final LinearLayout line = (LinearLayout) view.findViewById(R.id.line);
                    if (line != null)
                        line.setBackgroundColor(Color.parseColor(((ModuloClassGrid) entrada).getColorCode()));

                    gridList4.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                           if(position==0){
                                Intent docugen = new Intent(getApplication(), EscaneoTrabajadorEntradaActivity.class);
                                startActivity(docugen);
                                finish();
                            }else if(position==1){
                                Intent docugen = new Intent(getApplication(), EscaneoTrabajadorSalidaActivity.class);
                                startActivity(docugen);
                                finish();
                            }

                        }
                    });

                }
            }

        });
    }


    public void llenado3(){
        ArrayList<ModuloClassGrid> lista5 = new ArrayList<ModuloClassGrid>();


        try {
            if(ja1.getString(3).equals("1") ){
                lista5.add(new ModuloClassGrid(R.drawable.reportes,"Recepción Visitas","#4cd2c7"));
            }else{
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        gridList5.setAdapter(new adaptador_Modulo(this, R.layout.activity_modulo_lista, lista5){
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {
                    ImageView add = (ImageView) view.findViewById(R.id.imageView);
                    if (add != null)
                        add.setImageResource(((ModuloClassGrid) entrada).getImagen());

                    final TextView title = (TextView) view.findViewById(R.id.title);
                    if (title != null)
                        title.setText(((ModuloClassGrid) entrada).getTitle());
                    final LinearLayout line = (LinearLayout) view.findViewById(R.id.line);
                    if (line != null)
                        line.setBackgroundColor(Color.parseColor(((ModuloClassGrid) entrada).getColorCode()));

                    gridList5.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                            Intent docugen = new Intent(getApplication(), RecepcionVisitasActivity.class);
                            startActivity(docugen);
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
        Intent intent = new Intent(getApplicationContext(), DashboardActivity.class);
        startActivity(intent);
        finish();
    }



}