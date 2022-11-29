package mx.linkom.caseta_dm_offline;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EscaneoVisitaSalidaActivity extends mx.linkom.caseta_dm_offline.Menu {
    private CameraSource cameraSource;
    private SurfaceView cameraView;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private String token = "";
    private String tokenanterior = "";
    FloatingActionButton fabReiniciar;
    TextView tvRespusta;
    Configuracion Conf;
    EditText qr,placas;
    Button Buscar,Buscar1;
    JSONArray ja1,ja2,ja3,ja4;
    LinearLayout rlOtro;

    Button Lector;
    LinearLayout Qr,Qr2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escaneo_visita_salidas);
        Conf = new Configuracion(this);
        placas = (EditText) findViewById(R.id.editText1);
        Buscar1 = (Button) findViewById(R.id.btnBuscar1);
        placas.setFilters(new InputFilter[] { filter,new InputFilter.AllCaps() {
        } });

        Qr = (LinearLayout) findViewById(R.id.qr);
        Qr2 = (LinearLayout) findViewById(R.id.qr2);
        Lector = (Button) findViewById(R.id.btnLector);

        qr = (EditText) findViewById(R.id.editText);
        Buscar = (Button) findViewById(R.id.btnBuscar);
        rlOtro = (LinearLayout) findViewById(R.id.rlOtro);
       // qr.setFilters(new InputFilter[] { filter,new InputFilter.AllCaps() {
       // } });

        Lector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Qr.setVisibility(View.VISIBLE);
                Qr2.setVisibility(View.VISIBLE);
            }});


        Buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QR_codigo();
            }});
        Buscar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placas();
            }});

        Conf = new Configuracion(this);
        Conf.setQR(null);
        Conf.setST(null);

        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        tvRespusta = (TextView) findViewById(R.id.tvRespuesta);

        initQR();
        rlOtro.setVisibility(View.VISIBLE);




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




    public void initQR() {

        // Creo el detector qr
        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(this)
                        .setBarcodeFormats(Barcode.ALL_FORMATS)
                        .build();

        // Creo la camara
        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1600, 1024)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        // Listener de ciclo de vida de la camara
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                // Verifico si el usuario dio los permisos para la camara
                if (ActivityCompat.checkSelfPermission(EscaneoVisitaSalidaActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // Verificamos la version de ANdroid que sea al menos la M para mostrar
                        // El dialog de la solicitud de la camara
                        if (shouldShowRequestPermissionRationale(
                                Manifest.permission.CAMERA)) ;
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);
                    }
                    return;
                } else {
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException ie) {
                        Log.e("CAMERA SOURCE", ie.getMessage());
                    }
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        // Preparo el detector de QR
        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }


            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() > 0) {

                    // Obtenemos el token
                    token = barcodes.valueAt(0).displayValue.toString();

                    // Verificamos que el token anterior no se igual al actual
                    // Esto es util para evitar multiples llamadas empleando el mismo token
                    if (!token.equals(tokenanterior)) {

                        // Guardamos el ultimo token proceado
                        tokenanterior = token;
                        Log.i("Token", token);

                        if (URLUtil.isValidUrl(token)) {

                            Conf.setQR(token);
                            QR();
                        } else {
                            Conf.setQR(token);
                            QR();
                        }

                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    synchronized (this) {
                                        wait(5000);
                                        // Limpiamos el token
                                        tokenanterior = "";
                                    }
                                } catch (InterruptedException e) {
                                    // TODO Auto-generated catch block
                                    Log.e("Error", "Waiting didnt work!!");
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                }
            }
        });
    }




    public void placas() {
        if (placas.getText().toString().equals("")) {

            placas.setText("");
            rlOtro.setVisibility(View.VISIBLE);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EscaneoVisitaSalidaActivity.this);
            alertDialogBuilder.setTitle("Alerta");
            alertDialogBuilder
                    .setMessage("Placa Inexistente")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).create().show();
        }else if (placas.getText().toString().equals(" ")) {

            placas.setText("");

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EscaneoVisitaSalidaActivity.this);
            alertDialogBuilder.setTitle("Alerta");
            alertDialogBuilder
                    .setMessage("Placa Inexistente")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).create().show();

        } else{
            String url = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/vst_php8.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {

                    if (response.equals("error")) {
                        rlOtro.setVisibility(View.VISIBLE);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EscaneoVisitaSalidaActivity.this);
                        alertDialogBuilder.setTitle("Alerta");
                        alertDialogBuilder
                                .setMessage("Placa Inexistente")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                }).create().show();

                    } else {

                        response = response.replace("][", ",");
                        if (response.length() > 0) {

                            try {
                                ja1 = new JSONArray(response);
                                placas2(ja1.getString(2));
                            } catch (JSONException e) {
                               // placas.setText("");
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
                    params.put("Placas", placas.getText().toString().trim());
                    params.put("id_residencial", Conf.getResid().trim());

                    return params;
                }
            };

            requestQueue.add(stringRequest);
        }
    }

    public void placas2(final String id_visita) {
        String url = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/vst_php9.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                response = response.replace("][",",");
                if (response.length()>0){
                    try {
                        ja2 = new JSONArray(response);
                        String sCadena = ja2.getString(12);
                        String palabra=sCadena.substring(0, 1);

                         if(ja2.getString(5).equals("2") ){
                            Conf.setST("Aceptado");
                            Conf.setQR(ja2.getString(12));
                            Intent i = new Intent(getApplicationContext(), AccesosMultiplesSalidasActivity.class);
                            startActivity(i);
                            finish();
                        }else if(palabra.equals("M")){
                            Conf.setST("Aceptado");
                            Conf.setQR(ja2.getString(12));
                            Intent i = new Intent(getApplicationContext(), AccesosMultiplesSalidasActivity.class);
                            startActivity(i);
                            finish();
                        }else{
                            Conf.setST("Aceptado");
                            Conf.setQR(ja2.getString(12));
                            Intent i = new Intent(getApplicationContext(), AccesosSalidasActivity.class);
                            startActivity(i);
                            finish();
                        }

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
                params.put("id_visita", id_visita.trim());
                params.put("id_residencial", Conf.getResid().trim());

                return params;
            }
        };

        requestQueue.add(stringRequest);

    }

    public void QR() {
        String qrs=Conf.getQR();
        String[] a=qrs.split("");
        String pl=a[0];
        String sl=a[1];

        if((pl+sl).equals("AR")){
            String url = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/auto1.php?bd_name=" + Conf.getBd() + "&bd_user=" + Conf.getBdUsu() + "&bd_pwd=" + Conf.getBdCon();

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    if(response.equals("error")){
                        Conf.setST("Denegado");

                        Intent i = new Intent(getApplicationContext(), AccesosAutosSalidasActivity.class);
                        startActivity(i);
                        finish();

                    }else {
                        Conf.setTipoReg("Nada");
                        Conf.setST("Aceptado");
                        Intent i = new Intent(getApplicationContext(), AccesosAutosSalidasActivity.class);
                        startActivity(i);
                        finish();


                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Error ", "Id: " + error.toString());
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<>();
                    params.put("QR", Conf.getQR());
                    params.put("id_residencial", Conf.getResid().trim());

                    return params;
                }
            };
            requestQueue.add(stringRequest);
        }else {
        String url = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/vst_php1.php?bd_name=" + Conf.getBd() + "&bd_user=" + Conf.getBdUsu() + "&bd_pwd=" + Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if (response.equals("error")) {
                    Conf.setST("Denegado");

                    Intent i = new Intent(getApplicationContext(), AccesosSalidasActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    response = response.replace("][", ",");

                    try {
                        ja3 = new JSONArray(response);
                        String sCadena = Conf.getQR().trim();
                        String palabra = sCadena.substring(0, 1);

                        if (ja3.getString(6).length() > 0) {
                            Conf.setEvento(ja3.getString(6));
                            Conf.setST("Aceptado");
                            Intent i = new Intent(getApplicationContext(), ListaGrupalSalidaActivity.class);
                            startActivity(i);
                            finish();
                        } else if (ja3.getString(5).equals("2")) {
                            Conf.setST("Aceptado");
                            Intent i = new Intent(getApplicationContext(), AccesosMultiplesSalidasActivity.class);
                            startActivity(i);
                            finish();
                        } else if (palabra.equals("M")) {
                            Conf.setST("Aceptado");
                            Intent i = new Intent(getApplicationContext(), AccesosMultiplesSalidasActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Conf.setST("Aceptado");
                            Intent i = new Intent(getApplicationContext(), AccesosSalidasActivity.class);
                            startActivity(i);
                            finish();
                        }

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
                params.put("QR", Conf.getQR().trim());
                params.put("id_residencial", Conf.getResid().trim());

                return params;
            }
        };
        requestQueue.add(stringRequest);
        }
    }

    public void QR_codigo() {

        String qrs = qr.getText().toString().trim();
        String[] a = qrs.split("");
        String pl = a[0];
        String sl = a[1];

        if ((pl + sl).equals("AR")) {
            String url = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/auto1.php?bd_name=" + Conf.getBd() + "&bd_user=" + Conf.getBdUsu() + "&bd_pwd=" + Conf.getBdCon();

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    if(response.equals("error")){
                        Conf.setQR(qr.getText().toString().trim());
                        Conf.setST("Denegado");
                        Intent i = new Intent(getApplicationContext(), AccesosAutosSalidasActivity.class);
                        startActivity(i);
                        finish();
                    }else {
                        Conf.setQR(qr.getText().toString().trim());
                        Conf.setST("Aceptado");
                        Intent i = new Intent(getApplicationContext(), AccesosAutosSalidasActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Error ", "Id: " + error.toString());
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {

                    Map<String, String> params = new HashMap<>();
                    params.put("QR", qr.getText().toString().trim());
                    params.put("id_residencial", Conf.getResid().trim());

                    return params;
                }
            };
            requestQueue.add(stringRequest);
        } else {

        String url = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/vst_php1.php?bd_name=" + Conf.getBd() + "&bd_user=" + Conf.getBdUsu() + "&bd_pwd=" + Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if (response.equals("error")) {
                    Conf.setST("Denegado");

                    Intent i = new Intent(getApplicationContext(), AccesosSalidasActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    response = response.replace("][", ",");

                    try {
                        ja4 = new JSONArray(response);
                        String sCadena = qr.getText().toString().trim();
                        String palabra = sCadena.substring(0, 1);

                        if (ja4.getString(6).length() > 0) {
                            Conf.setEvento(ja4.getString(6));
                            Conf.setQR(qr.getText().toString().trim());
                            Conf.setST("Aceptado");
                            Intent i = new Intent(getApplicationContext(), ListaGrupalSalidaActivity.class);
                            startActivity(i);
                            finish();
                        } else if (ja4.getString(5).equals("2")) {

                            Conf.setST("Aceptado");
                            Conf.setQR(qr.getText().toString().trim());
                            Intent i = new Intent(getApplicationContext(), AccesosMultiplesSalidasActivity.class);
                            startActivity(i);
                            finish();
                        } else if (palabra.equals("M")) {
                            Conf.setST("Aceptado");
                            Conf.setQR(qr.getText().toString().trim());

                            Intent i = new Intent(getApplicationContext(), AccesosMultiplesSalidasActivity.class);
                            startActivity(i);
                            finish();
                        } else {
                            Conf.setST("Aceptado");
                            Conf.setQR(qr.getText().toString().trim());

                            Intent i = new Intent(getApplicationContext(), AccesosSalidasActivity.class);
                            startActivity(i);
                            finish();
                        }

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
                params.put("QR", qr.getText().toString().trim());
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
