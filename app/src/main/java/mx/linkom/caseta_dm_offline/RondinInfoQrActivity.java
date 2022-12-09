package mx.linkom.caseta_dm_offline;

import android.Manifest;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import mx.linkom.caseta_dm_offline.offline.Database.UrisContentProvider;
import mx.linkom.caseta_dm_offline.offline.Global_info;

public class RondinInfoQrActivity extends mx.linkom.caseta_dm_offline.Menu {

    private CameraSource cameraSource;
    private SurfaceView cameraView;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private String token = "";
    private String tokenanterior = "";
    mx.linkom.caseta_dm_offline.Configuracion Conf;
    JSONArray ja1,ja2,ja3;
    LinearLayout camara;
    TextView Nombre,Hora,Ubicacion;
    Button btnLector;
    LinearLayout qr;
    Button Incidencia;
    boolean Offline = false;
    ImageView iconoInternet;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rondines_qr);


        Conf = new mx.linkom.caseta_dm_offline.Configuracion(this);
        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        camara = (LinearLayout) findViewById(R.id.camara);
        Nombre = (TextView) findViewById(R.id.nombre);
        Hora = (TextView) findViewById(R.id.hora);
        Ubicacion = (TextView) findViewById(R.id.ubicacion);
        btnLector = (Button) findViewById(R.id.btnLector);
        qr = (LinearLayout) findViewById(R.id.qr);

        Incidencia = (Button) findViewById(R.id.btnIncidencia);
        iconoInternet = (ImageView) findViewById(R.id.iconoInternetRondinInfoQr);

        if (Global_info.getINTERNET().equals("Si")){
            Offline = false;
            iconoInternet.setImageResource(R.drawable.ic_online);
        }else {
            Offline = true;
            iconoInternet.setImageResource(R.drawable.ic_offline);
        }

        iconoInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Offline){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RondinInfoQrActivity.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("Aplicación funcionando en modo offline \n\nDatos actualizados hasta: \n\n"+Global_info.getULTIMA_ACTUALIZACION())
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();
                }else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RondinInfoQrActivity.this);
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

        Incidencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RondinIncidenciasQr.class);
                startActivity(i);
                finish();
            }
        });

        btnLector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camara.setVisibility(View.VISIBLE);
            }});
        initQR();

        if (Offline){
            rondinOffline();
        }else {
            rondin();
        }
    }



    public void initQR() {


        // Creo el detector qr
        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(this)
                        .setBarcodeFormats(Barcode.ALL_FORMATS)
                        .build();

        // Creo la camara
        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1800, 1124)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        // Listener de ciclo de vida de la camara
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                // Verifico si el usuario dio los permisos para la camara
                if (ActivityCompat.checkSelfPermission(RondinInfoQrActivity.this, Manifest.permission.CAMERA)
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


            @RequiresApi(api = Build.VERSION_CODES.O)
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

                            Conf.setQRondines(token);
                            if (Offline){
                                RegistrarOffline();
                            }else{
                                Registrar();
                            }

                        } else {

                            Conf.setQRondines(token);
                            if (Offline){
                                RegistrarOffline();
                            }else{
                                Registrar();
                            }

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void rondinOffline() {

        LocalDateTime hoy = LocalDateTime.now();

        int year = hoy.getYear();
        int month = hoy.getMonthValue();
        int day = hoy.getDayOfMonth();

        String fecha = "";

        //Poner el cero cuando el mes o dia es menor a 10
        if (day < 10 || month < 10){
            if (month < 10 && day >= 10){
                fecha = year+"-0"+month+"-"+day;
            } else if (month >= 10 && day < 10){
                fecha = year+"-"+month+"-0"+day;
            }else if (month < 10 && day < 10){
                fecha = year+"-0"+month+"-0"+day;
            }
        }else {
            fecha = year+"-"+month+"-"+day;
        }

        LocalDateTime hoy2 = hoy.plusMinutes(30);


        int hour = hoy2.getHour();
        int minute = hoy2.getMinute();

        String hora = "";

        if (hour < 10 || minute < 10){
            if (hour < 10 && minute >=10){
                hora = "0"+hour+":"+minute;
            }else if (hour >= 10 && minute < 10){
                hora = hour+":0"+minute;
            }else if (hour < 10 && minute < 10){
                hora = "0"+hour+":0"+minute;
            }
        }else {
            hora = hour+":"+minute;
        }

        String id = Conf.getRondin().trim();
        String usuario = Conf.getUsu().trim();
        String dia = fecha;
        String id_residencial = Conf.getResid().trim();
        String tiempo = hora;

        String parametros[] = {id, usuario, dia, id_residencial, tiempo};

        Log.e("error", "SELECT ubi.id, ubi.hora, ubis.nombre, dia.id, dia.dia, rondin.id, rondin.nombre, ubis.qr FROM rondines_ubicaciones_qr as ubi, rondines_dia_qr as dia, rondines_qr as rondin, ubicaciones_qr as ubis WHERE ubi.id="+"'"+id+"'"+" and ubi.id_usuario="+"'"+usuario+"'"+" and ubi.id_rondin=dia.id_rondin and ubi.id_rondin=rondin.id and dia.dia="+"'"+dia+"'"+" and ubi.id_residencial="+"'"+id_residencial+"'"+" and ubi.hora<="+"'"+tiempo+"'"+" and ubis.id=ubi.id_ubicacion and NOT EXISTS (SELECT * FROM rondines_dtl_qr WHERE rondines_dtl_qr.id_ubicaciones=ubi.id and rondines_dtl_qr.id_dia=dia.id and rondines_dtl_qr.id_rondin=rondin.id)");

        Cursor cursor = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_RONDINESDIAQR, null, null, parametros, null);

        ja1 = new JSONArray();

        if (cursor.moveToFirst()){
            ja1.put(cursor.getString(0));
            ja1.put(cursor.getString(1));
            ja1.put(cursor.getString(2));
            ja1.put(cursor.getString(3));
            ja1.put(cursor.getString(4));
            ja1.put(cursor.getString(5));
            ja1.put(cursor.getString(6));
            ja1.put(cursor.getString(7));


            Nombre.setText(cursor.getString(6));
            Hora.setText(cursor.getString(1));
            Ubicacion.setText(cursor.getString(2));
        }

        cursor.close();

    }


    public void rondin() {
        String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/rondines_qr_2.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {
                        ja1 = new JSONArray(response);
                        Log.e("Error ", "LINKOM ST qr: " + response);

                        Nombre.setText(ja1.getString(6));
                        Hora.setText(ja1.getString(1));
                        Ubicacion.setText(ja1.getString(2));

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
                params.put("id", Conf.getRondin().trim());
                params.put("guardia_de_entrada", Conf.getUsu().trim());
                params.put("id_residencial", Conf.getResid().trim());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void RegistrarOffline(){



        try {

            if(ja1.getString(7).equals(Conf.getQRondines())){

                LocalDateTime hoy = LocalDateTime.now();

                int year = hoy.getYear();
                int month = hoy.getMonthValue();
                int day = hoy.getDayOfMonth();
                int hour = hoy.getHour();
                int minute = hoy.getMinute();

                String fecha = "";

                //Poner el cero cuando el mes o dia es menor a 10
                if (day < 10 || month < 10){
                    if (month < 10 && day >= 10){
                        fecha = year+"-0"+month+"-"+day;
                    } else if (month >= 10 && day < 10){
                        fecha = year+"-"+month+"-0"+day;
                    }else if (month < 10 && day < 10){
                        fecha = year+"-0"+month+"-0"+day;
                    }
                }else {
                    fecha = year+"-"+month+"-"+day;
                }

                String hora = "";

                if (hour < 10 || minute < 10){
                    if (hour < 10 && minute >=10){
                        hora = "0"+hour+":"+minute;
                    }else if (hour >= 10 && minute < 10){
                        hora = hour+":0"+minute;
                    }else if (hour < 10 && minute < 10){
                        hora = "0"+hour+":0"+minute;
                    }
                }else {
                    hora = hour+":"+minute;
                }


                ContentValues values = new ContentValues();
                values.put("id_residencial", Integer.parseInt(Conf.getResid().trim()));
                values.put("id_rondin", Integer.parseInt(ja1.getString(5)));
                values.put("id_dia", Integer.parseInt(ja1.getString(3)));
                values.put("id_ubicaciones", Integer.parseInt(ja1.getString(0)));
                values.put("dia", fecha);
                values.put("hora", hora);
                values.put("estatus", 1);
                values.put("sqliteEstatus", 1);


                Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_RONDINESDTLQR,values);
                String idUri = uri.getLastPathSegment();
                int insertar = Integer.parseInt(idUri);

                if (insertar != -1){
                    //Fue correcto
                    new Thread(){
                        @Override
                        public void run() {
                            RondinInfoQrActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RondinInfoQrActivity.this);
                                    alertDialogBuilder.setTitle("Alerta");
                                    alertDialogBuilder
                                            .setMessage("Registro de asistencia exitoso en modo offline")
                                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_dm_offline.Rondines.class);
                                                    startActivity(i);
                                                    finish();
                                                }
                                            }).create().show();
                                }
                            });
                        }
                    }.start();
                }else {
                    //Error al registrar
                    new Thread(){
                        @Override
                        public void run() {
                            RondinInfoQrActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RondinInfoQrActivity.this);
                                    alertDialogBuilder.setTitle("Alerta");
                                    alertDialogBuilder
                                            .setMessage("Registro de asistencia no exitoso en modo offline")
                                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_dm_offline.Rondines.class);
                                                    startActivity(i);
                                                    finish();
                                                }
                                            }).create().show();
                                }
                            });
                        }
                    }.start();
                }

            }else{

                new Thread(){
                    @Override
                    public void run() {
                        RondinInfoQrActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RondinInfoQrActivity.this);
                                alertDialogBuilder.setTitle("Alerta");
                                alertDialogBuilder
                                        .setMessage("El qr no corresponde a la ubicación")
                                        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                Intent i = new Intent(getApplicationContext(), Rondines.class);
                                                startActivity(i);
                                                finish();
                                            }
                                        }).create().show();
                            }
                        });
                    }
                }.start();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    public void Registrar(){



        try {

            if(ja1.getString(7).equals(Conf.getQRondines())){

                String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/rondines_qr_3.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
                RequestQueue requestQueue = Volley.newRequestQueue(this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){


                        if(response.equals("error")){
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RondinInfoQrActivity.this);
                            alertDialogBuilder.setTitle("Alerta");
                            alertDialogBuilder
                                    .setMessage("Registro de Asistencia No Exitoso")
                                    .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_dm_offline.Rondines.class);
                                            startActivity(i);
                                            finish();
                                        }
                                    }).create().show();
                        }else {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RondinInfoQrActivity.this);
                            alertDialogBuilder.setTitle("Alerta");
                            alertDialogBuilder
                                    .setMessage("Registro de Asistencia Exitoso")
                                    .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_dm_offline.Rondines.class);
                                            startActivity(i);
                                            finish();
                                        }
                                    }).create().show();

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
                        try {
                            params.put("id_rondin", ja1.getString(5));
                            params.put("id_dia", ja1.getString(3));
                            params.put("id_ubicaciones",  ja1.getString(0));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return params;
                    }
                };
                requestQueue.add(stringRequest);

            }else{

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RondinInfoQrActivity.this);
                alertDialogBuilder.setTitle("Alerta");
                alertDialogBuilder
                        .setMessage("El qr no corresponde a la ubicación")
                        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(getApplicationContext(), Rondines.class);
                                startActivity(i);
                                finish();
                            }
                        }).create().show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }





    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), mx.linkom.caseta_dm_offline.Rondines.class);
        startActivity(intent);
        finish();
    }

}
