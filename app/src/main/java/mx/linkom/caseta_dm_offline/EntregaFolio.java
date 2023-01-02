package mx.linkom.caseta_dm_offline;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

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

import mx.linkom.caseta_dm_offline.offline.Database.UrisContentProvider;
import mx.linkom.caseta_dm_offline.offline.Global_info;

public class EntregaFolio  extends mx.linkom.caseta_dm_offline.Menu {
    private Configuracion Conf;
    EditText folio;
    Button buscar;
    JSONArray ja1;

    ImageView iconoInternet;
    boolean Offline = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entregafolio);
        Conf = new Configuracion(this);
        folio = (EditText) findViewById(R.id.setFolio);
        buscar = (Button) findViewById(R.id.btnBuscar);
        iconoInternet = (ImageView) findViewById(R.id.iconoInternetEntregaFolio);

        if (Global_info.getINTERNET().equals("Si")){
            iconoInternet.setImageResource(R.drawable.ic_online);
            Offline = false;
        }else {
            iconoInternet.setImageResource(R.drawable.ic_offline);
            Offline = true;
        }

        iconoInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Offline){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EntregaFolio.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("Aplicación funcionando en modo offline \n\nDatos actualizados hasta: \n\n"+Global_info.getULTIMA_ACTUALIZACION())
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();
                }else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EntregaFolio.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("Aplicación funcionando en modo online \n\nDatos actualizados para funcionamiento en modo offline hasta: \n\n"+Global_info.getULTIMA_ACTUALIZACION())
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();
                }
            }
        });

        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Offline){
                    checkOffline();
                }else {
                    check();
                }
            }});

    }


    public void checkOffline() {

        try {
            String id_residencial = Conf.getResid().trim();
            String fol = folio.getText().toString().trim();

            String parametros[] = {fol, id_residencial};

            Cursor cursor = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_CORRESPONDENCIA, null, "Online", parametros, null);


            if (cursor.moveToFirst()){
                ja1 = new JSONArray();
                ja1.put(cursor.getString(0));
                ja1.put(cursor.getString(1));
                ja1.put(cursor.getString(2));
                ja1.put(cursor.getString(3));
                ja1.put(cursor.getString(4));
                ja1.put(cursor.getString(5));
                ja1.put(cursor.getString(6));
                ja1.put(cursor.getString(7));
                ja1.put(cursor.getString(8));
                ja1.put(cursor.getString(9));
                ja1.put(cursor.getString(10));
                ja1.put(cursor.getString(11));
                ja1.put(cursor.getString(12));
                ja1.put(cursor.getString(13));
                ja1.put(cursor.getString(14));
                ja1.put(cursor.getString(15));
                ja1.put(cursor.getString(16));
                ja1.put(cursor.getString(17));
                ja1.put(cursor.getString(18));
                ja1.put(cursor.getString(19));


                Conf.setPlacas(ja1.getString(0));
                Intent i = new Intent(getApplicationContext(), EntregaActivity.class);
                startActivity(i);
                finish();
            }else {
                //Intentar con el folio de offline

                Cursor cursor2 = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_CORRESPONDENCIA, null, "Offline", parametros, null); ;

                if (cursor2.moveToFirst()){
                    ja1 = new JSONArray();
                    ja1.put(cursor2.getString(0));
                    ja1.put(cursor2.getString(1));
                    ja1.put(cursor2.getString(2));
                    ja1.put(cursor2.getString(3));
                    ja1.put(cursor2.getString(4));
                    ja1.put(cursor2.getString(5));
                    ja1.put(cursor2.getString(6));
                    ja1.put(cursor2.getString(7));
                    ja1.put(cursor2.getString(8));
                    ja1.put(cursor2.getString(9));
                    ja1.put(cursor2.getString(10));
                    ja1.put(cursor2.getString(11));
                    ja1.put(cursor2.getString(12));
                    ja1.put(cursor2.getString(13));
                    ja1.put(cursor2.getString(14));
                    ja1.put(cursor2.getString(15));
                    ja1.put(cursor2.getString(16));
                    ja1.put(cursor2.getString(17));
                    ja1.put(cursor2.getString(18));
                    ja1.put(cursor2.getString(19));


                    Conf.setPlacas(ja1.getString(0));
                    Intent i = new Intent(getApplicationContext(), EntregaActivity.class);
                    startActivity(i);
                    finish();
                }else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EntregaFolio.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("No existe folio en modo offline")
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(getApplicationContext(),CorrespondenciaActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            }).create().show();
                }

            }
        }catch (Exception ex){
            Log.e("error", ex.toString());
        }

    }


    public void check() {
        String url = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/correspondencia_5.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                response = response.replace("][",",");
                if (response.length()>0){
                    try {
                        ja1 = new JSONArray(response);

                        Conf.setPlacas(ja1.getString(0));
                        Intent i = new Intent(getApplicationContext(), EntregaActivity.class);
                        startActivity(i);
                        finish();
                    } catch (JSONException e) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EntregaFolio.this);
                        alertDialogBuilder.setTitle("Alerta");
                        alertDialogBuilder
                                .setMessage("No existe folio")
                                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent i = new Intent(getApplicationContext(),CorrespondenciaActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                }).create().show();


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
                params.put("Folio", folio.getText().toString().trim());
                params.put("id_residencial", Conf.getResid().trim());

                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), CorrespondenciaActivity.class);
        startActivity(intent);
        finish();
    }


}
