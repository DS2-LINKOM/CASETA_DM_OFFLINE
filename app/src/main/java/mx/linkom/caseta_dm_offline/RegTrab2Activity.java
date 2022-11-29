package mx.linkom.caseta_dm_offline;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import mx.linkom.caseta_dm_offline.adaptadores.ListasClassGrid;
import mx.linkom.caseta_dm_offline.adaptadores.ListasHoraFechaClassGrid;
import mx.linkom.caseta_dm_offline.adaptadores.adaptador_Modulo;

public class RegTrab2Activity extends mx.linkom.caseta_dm_offline.Menu {

    GridView gridList;
    Configuracion Conf;
    JSONArray ja1;
    Button Trabajador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regtrab2);

        Conf = new Configuracion(this);
        gridList = (GridView) findViewById(R.id.gridList);
        Trabajador = (Button) findViewById(R.id.btnTrabajador);

        Trabajador.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RegTrabActivity.class);
                startActivity(i);
                finish();
            }
        });
        trabajadores();


    }

    public void trabajadores() {
        String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/trabajador_1.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {
                        ja1 = new JSONArray(response);
                        lista();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

    public void lista(){
        ArrayList<ListasClassGrid> ubicacion = new ArrayList<ListasClassGrid>();


        for (int i = 0; i < ja1.length(); i += 24){
            try {

                ubicacion.add(new ListasClassGrid(ja1.getString(i+6), "ID:"+ja1.getString(i + 2)));
            }catch (JSONException e){
                e.printStackTrace();
            }
        }




        gridList.setAdapter(new adaptador_Modulo(this, R.layout.activity_listas, ubicacion){
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


                            int posicion=position*24;
                            try {
                                Conf.setTraba(ja1.getString(posicion));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Intent i = new Intent(getApplicationContext(), RegTrab3Activity.class);
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
        Intent intent = new Intent(getApplicationContext(), ReportesActivity.class);
        startActivity(intent);
        finish();
    }


}