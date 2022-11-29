package mx.linkom.caseta_dm_offline;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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

import java.util.HashMap;
import java.util.Map;

public class PlacasInfoActivity extends mx.linkom.caseta_dm_offline.Menu{
    Configuracion Conf;
    JSONArray ja1,ja2,ja3,ja4,ja5;
    LinearLayout rlVisita;
    TextView Tipos,Nombre,Placas,Direccion,Visitante;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placasinfo);
        Conf = new Configuracion(this);

        rlVisita = (LinearLayout) findViewById(R.id.rlVisita);
        Tipos = (TextView)findViewById(R.id.setTipos);
        Nombre = (TextView)findViewById(R.id.setNombre);
        Placas = (TextView) findViewById(R.id.setPlacas);
        Direccion = (TextView) findViewById(R.id.setDir);
        Visitante = (TextView) findViewById(R.id.setVisita);

        if(Conf.getSTPlacas().equals("Registrado")){
            RegPlacas(); //AUTO
        }else  if(Conf.getSTPlacas().equals("RegistradoVisita")){
            RegPlacas2(); //VISITA
        }

    }

    public void RegPlacas(){

        String url = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/placas_1.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                response = response.replace("][",",");
                if (response.length()>0){
                    try {
                        ja1 = new JSONArray(response);
                        Usuario(ja1.getString(2));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG","Error: " + error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Placas", Conf.getPlacas().trim());
                params.put("id_residencial", Conf.getResid().trim());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void RegPlacas2(){

        String url = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/placas_2.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                response = response.replace("][",",");
                if (response.length()>0){
                    try {
                        ja2 = new JSONArray(response);
                        Visita(ja2.getString(2));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG","Error: " + error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Placas", Conf.getPlacas().trim());
                params.put("id_residencial", Conf.getResid().trim());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void Visita(final String Id){

        String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/vst_reg_5.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                response = response.replace("][",",");
                if (response.length()>0){
                    try {
                        ja3= new JSONArray(response);

                        Usuario(ja3.getString(2));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG","Error: " + error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id",  Id.trim());
                params.put("id_residencial", Conf.getResid().trim());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }


    public void Usuario(final String IdUsu){

        String url = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/placas_3.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                response = response.replace("][",",");
                if (response.length()>0){
                    try {
                        ja4 = new JSONArray(response);
                        dtlLugar(ja4.getString(0));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG","Error: " + error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("IdUsu", IdUsu.trim());
                params.put("id_residencial", Conf.getResid().trim());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void dtlLugar(final String idUsuario){
        String url = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/placas_4.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {
                        ja5 = new JSONArray(response);
                        ValidarPlacas();

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
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_usuario", idUsuario.trim());
                params.put("id_residencial", Conf.getResid().trim());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }


    public void ValidarPlacas(){

        try {

            if(Conf.getSTPlacas().equals("Registrado")){
                Tipos.setText("Residente");
                Nombre.setText(ja4.getString(1)+" "+ja4.getString(2)+" "+ja4.getString(3));
                Placas.setText(ja1.getString(4));
                Direccion.setText(ja5.getString(0));
                Visitante.setText("");
            }else  if(Conf.getSTPlacas().equals("RegistradoVisita")){
                rlVisita.setVisibility(View.VISIBLE);

                Tipos.setText("Visita");
                Nombre.setText(ja4.getString(1)+" "+ja4.getString(2)+" "+ja4.getString(3));
                Visitante.setText(ja3.getString(7));
                Placas.setText(ja2.getString(9));
                Direccion.setText(ja5.getString(0));


            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), ReportesActivity.class);
        startActivity(intent);
        finish();
    }

}
