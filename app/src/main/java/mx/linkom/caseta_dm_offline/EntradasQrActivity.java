package mx.linkom.caseta_dm_offline;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

public class EntradasQrActivity extends mx.linkom.caseta_dm_offline.Menu {

    JSONArray ja1;
    Configuracion Conf;
    EditText Placas;
    Button Registro,Registro2;

    ImageView iconoInternet;
    boolean Offline = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entradasqr);


        Conf = new Configuracion(this);

        Placas = (EditText) findViewById(R.id.editText1);
        Registro = (Button) findViewById(R.id.btnBuscar1);

        iconoInternet = (ImageView) findViewById(R.id.iconoInternetEntradasQr);

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
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(EntradasQrActivity.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("Aplicación funcionando en modo offline \n\nDatos actualizados hasta: \n\n"+Global_info.getULTIMA_ACTUALIZACION())
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();
                }else {
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(EntradasQrActivity.this);
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


        Registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Offline){
                    placasOffline();
                }else {
                    placas();
                }
            }
        });
        Placas.setFilters(new InputFilter[]{filter, new InputFilter.AllCaps() {
        }});

        Registro2 = (Button) findViewById(R.id.btnBuscar2);
        /*Registro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placas();
            }});*/

        Registro2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Conf.setTipoReg("Peatonal");
                Conf.setPlacas("");
                if (Conf.getTipoQr().equals("Normal")) {
                    Log.e("EntradasQr", "Normal");
                    Intent i = new Intent(getApplicationContext(), AccesosActivity.class);
                    startActivity(i);
                    finish();
                } else if (Conf.getTipoQr().equals("Multiples")) {
                    Log.e("EntradasQr", "Multiples");
                    Intent i = new Intent(getApplicationContext(), AccesosMultiplesActivity.class);
                    startActivity(i);
                    finish();
                } else if (Conf.getTipoQr().equals("Grupal")) {
                    Log.e("EntradasQr", "Grupal");
                    Intent i = new Intent(getApplicationContext(), AccesosGrupalActivity.class);
                    startActivity(i);
                    finish();
                }
            }});
    }

    InputFilter filter = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                if (Character.isSpaceChar(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        }
    };


    public void placasOffline() {

        if (Placas.getText().toString().equals("")) {

            Placas.setText("");

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EntradasQrActivity.this);
            alertDialogBuilder.setTitle("Alerta");
            alertDialogBuilder
                    .setMessage("Placa Inexistente")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).create().show();
        } else if (Placas.getText().toString().equals(" ")) {

            Placas.setText("");

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EntradasQrActivity.this);
            alertDialogBuilder.setTitle("Alerta");
            alertDialogBuilder
                    .setMessage("Placa Inexistente")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).create().show();

        } else {

            try {
                String placas = Placas.getText().toString().trim();
                String id_resid = Conf.getResid().trim();

                String parametros[] = {id_resid, placas};

                Cursor cursor = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_DTL_ENTRADAS_SALIDAS, null, "consulta1", parametros, null);

                Conf.setTipoReg("Auto");
                if (cursor.moveToFirst()){
                    try {
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

                        Conf.setPlacas(ja1.getString(9));
                        Conf.setIdPre(ja1.getString(2));


                        if (Conf.getTipoQr().equals("Normal")) {
                            Log.e("tipoPlaca", "Normal");
                            Intent i = new Intent(getApplicationContext(), PreEntradasQrActivity.class);
                            startActivity(i);
                            finish();
                        } else if (Conf.getTipoQr().equals("Multiples")) {
                            Log.e("tipoPlaca", "Multiples");
                            Intent i = new Intent(getApplicationContext(), PreEntradasMultiplesQrActivity.class);
                            startActivity(i);
                            finish();
                        } else if (Conf.getTipoQr().equals("Grupal")) {
                            Log.e("tipoPlaca", "Grupal");
                            Intent i = new Intent(getApplicationContext(), PreEntradasGrupalActivity.class);
                            startActivity(i);
                            finish();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Conf.setPlacas(Placas.getText().toString().trim());

                    if (Conf.getTipoQr().equals("Normal")) {
                        Log.e("tipoPlaca", "Normal no existe");
                        Intent i = new Intent(getApplicationContext(), AccesosActivity.class);
                        startActivity(i);
                        finish();
                    } else if (Conf.getTipoQr().equals("Multiples")) {
                        Log.e("tipoPlaca", "Multiples no existe");
                        Intent i = new Intent(getApplicationContext(), AccesosMultiplesActivity.class);
                        startActivity(i);
                        finish();
                    } else if (Conf.getTipoQr().equals("Grupal")) {
                        Log.e("tipoPlaca", "Grupal no existe");
                        Intent i = new Intent(getApplicationContext(), AccesosGrupalActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            }catch (Exception ex){

            }

        }

    }

    public void placas() {

        if (Placas.getText().toString().equals("")) {

            Placas.setText("");

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EntradasQrActivity.this);
            alertDialogBuilder.setTitle("Alerta");
            alertDialogBuilder
                    .setMessage("Placa Inexistente")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).create().show();
        } else if (Placas.getText().toString().equals(" ")) {

            Placas.setText("");

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EntradasQrActivity.this);
            alertDialogBuilder.setTitle("Alerta");
            alertDialogBuilder
                    .setMessage("Placa Inexistente")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).create().show();

        } else {

            String url = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/vst_reg_4.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Conf.setTipoReg("Auto");

                    if (response.equals("error")) {
                        Conf.setPlacas(Placas.getText().toString().trim());

                        if (Conf.getTipoQr().equals("Normal")) {
                            Intent i = new Intent(getApplicationContext(), AccesosActivity.class);
                            startActivity(i);
                            finish();
                        } else if (Conf.getTipoQr().equals("Multiples")) {
                            Intent i = new Intent(getApplicationContext(), AccesosMultiplesActivity.class);
                            startActivity(i);
                            finish();
                        } else if (Conf.getTipoQr().equals("Grupal")) {
                            Intent i = new Intent(getApplicationContext(), AccesosGrupalActivity.class);
                            startActivity(i);
                            finish();
                        }


                    } else {
                        response = response.replace("][", ",");
                        if (response.length() > 0) {
                            try {
                                ja1 = new JSONArray(response);

                                Conf.setPlacas(ja1.getString(9));
                                Conf.setIdPre(ja1.getString(2));


                                if (Conf.getTipoQr().equals("Normal")) {
                                    Intent i = new Intent(getApplicationContext(), PreEntradasQrActivity.class);
                                    startActivity(i);
                                    finish();
                                } else if (Conf.getTipoQr().equals("Multiples")) {
                                    Intent i = new Intent(getApplicationContext(), PreEntradasMultiplesQrActivity.class);
                                    startActivity(i);
                                    finish();
                                } else if (Conf.getTipoQr().equals("Grupal")) {
                                    Intent i = new Intent(getApplicationContext(), PreEntradasGrupalActivity.class);
                                    startActivity(i);
                                    finish();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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
                    params.put("Placas", Placas.getText().toString().trim());
                    params.put("id_residencial", Conf.getResid().trim());

                    return params;
                }
            };


        requestQueue.add(stringRequest);
    }

}



    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), EntradasSalidasActivity.class);
        startActivity(intent);
        finish();
    }


}
