package mx.linkom.caseta_dm_offline;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;

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

public class EntregaFolio  extends mx.linkom.caseta_dm_offline.Menu {
    private Configuracion Conf;
    EditText folio;
    Button buscar;
    JSONArray ja1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entregafolio);
        Conf = new Configuracion(this);
        folio = (EditText) findViewById(R.id.setFolio);
        buscar = (Button) findViewById(R.id.btnBuscar);

        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }});

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
