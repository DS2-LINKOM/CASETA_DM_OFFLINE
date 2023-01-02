package mx.linkom.caseta_dm_offline;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import java.util.HashMap;
import java.util.Map;

import mx.linkom.caseta_dm_offline.offline.Database.UrisContentProvider;
import mx.linkom.caseta_dm_offline.offline.Global_info;

public class EscaneoVisitaActivity extends mx.linkom.caseta_dm_offline.Menu {

    private CameraSource cameraSource;
    private SurfaceView cameraView;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private String token = "";
    private String tokenanterior = "";

    TextView tvRespusta;
    Configuracion Conf;
    JSONArray ja1,ja2,ja3;

    EditText qr;
    Button Buscar, Lector;
    EditText Placas;
    LinearLayout Qr,Qr2;
    Button Registro,Registro2;

    ImageView iconoInternet;
    boolean Offline = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escaneo_visita);


        Conf = new Configuracion(this);
        Conf.setQR(null);
        Conf.setST(null);

        Validacion();
        Qr = (LinearLayout) findViewById(R.id.qr);
        Qr2 = (LinearLayout) findViewById(R.id.qr2);
        Lector = (Button) findViewById(R.id.btnLector);
        Placas = (EditText) findViewById(R.id.editText1);
        Registro = (Button) findViewById(R.id.btnBuscar1);
        Registro2 = (Button) findViewById(R.id.btnBuscar2);
        iconoInternet = (ImageView) findViewById(R.id.iconoInternetEscaneoVisita);

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
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(EscaneoVisitaActivity.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("Aplicación funcionando en modo offline \n\nDatos actualizados hasta: \n\n"+Global_info.getULTIMA_ACTUALIZACION())
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();
                }else {
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(EscaneoVisitaActivity.this);
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
            }});

        Lector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Qr.setVisibility(View.VISIBLE);
                Qr2.setVisibility(View.VISIBLE);
            }});

        Registro2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Conf.setTipoReg("Peatonal");
                Conf.setPlacas("");
                Intent i = new Intent(getApplicationContext(), AccesoRegistroActivity.class);
                startActivity(i);
                finish();
            }});
        Placas.setFilters(new InputFilter[] { filter,new InputFilter.AllCaps() {
        } });
        

        qr = (EditText) findViewById(R.id.editText);
        Buscar = (Button) findViewById(R.id.btnBuscar);

        Buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Offline){
                    QR_codigoOffline();
                }else{
                    QR_codigo();
                }
            }});


        cameraView = (SurfaceView) findViewById(R.id.camera_view);
        tvRespusta = (TextView) findViewById(R.id.tvRespuesta);

        initQR();

       // qr.setFilters(new InputFilter[] { filter,new InputFilter.AllCaps() {
       // } });



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

    public void Validacion() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EscaneoVisitaActivity.this);
        alertDialogBuilder.setTitle("Alerta");
        alertDialogBuilder
                .setMessage("Confirmar si la visita tiene un QR.")
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                 }).create().show();
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
                if (ActivityCompat.checkSelfPermission(EscaneoVisitaActivity.this, Manifest.permission.CAMERA)
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
                            if (Offline){
                                QROffline();
                            }else{
                                QR();
                            }

                        } else {
                            Conf.setQR(token);
                            if (Offline){
                                QROffline();
                            }else{
                                QR();
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



    public void QROffline() {

        Cursor cursor = null;

        String qrs=Conf.getQR();
        String[] a=qrs.split("");
        String pl=a[0];
        String sl=a[1];

        if((pl+sl).equals("AR")){

            try {
                String qr =  Conf.getQR();
                String id_residencial = Conf.getResid().trim();

                String[] parametros = {qr, id_residencial};
                cursor = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_AUTO, null, null, parametros, null);

                if (cursor.moveToFirst()){
                    Conf.setTipoReg("Nada");
                    Conf.setST("Aceptado");
                    Log.e("EscaneoVisita", "Aceptado");
                    Intent i = new Intent(getApplicationContext(), AccesoAutosActivity.class);
                    startActivity(i);
                    finish();
                }else {
                    Conf.setST("Denegado");
                    Log.e("EscaneoVisita", "Denegado");
                    Intent i = new Intent(getApplicationContext(), AccesoAutosActivity.class);
                    startActivity(i);
                    finish();
                }
            }catch (Exception ex){
                Log.e("Exception", ex.toString());
            }finally {
                cursor.close();
            }

        }else {

            try {
                String qr =  Conf.getQR();
                String id_residencial = Conf.getResid().trim();

                String[] parametros = {qr, id_residencial};
                cursor = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_VISITA, null, "vst1", parametros, null);

                if (cursor.moveToFirst()){

                    if (Integer.parseInt(Conf.getPreQr()) == 1) {

                        try {

                            ja1 = new JSONArray();

                            do {
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

                            }while (cursor.moveToNext());

                            String sCadena = Conf.getQR().trim();
                            String palabra = sCadena.substring(0, 1);

                            if (ja1.getString(6).length() > 0) {
                                Conf.setEvento(ja1.getString(6));
                                Conf.setST("Aceptado");
                                Log.e("EscaneoVisita ", "ListaGrupalEntradaActivity1");
                                Intent i = new Intent(getApplicationContext(), ListaGrupalEntradaActivity.class);
                                startActivity(i);
                                finish();
                            } else if (ja1.getString(5).equals("2")) {
                                Conf.setST("Aceptado");
                                Conf.setTipoQr("Multiples");
                                Log.e("EscaneoVisita ", "EntradasQrActivity1");
                                Intent i = new Intent(getApplicationContext(), EntradasQrActivity.class);
                                startActivity(i);
                                finish();
                            } else if (palabra.equals("M")) {
                                Conf.setST("Aceptado");
                                Conf.setTipoQr("Multiples");
                                Log.e("EscaneoVisita ", "EntradasQrActivity1");
                                Intent i = new Intent(getApplicationContext(), EntradasQrActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Conf.setST("Aceptado");
                                Conf.setTipoQr("Normal");
                                Log.e("EscaneoVisita", "Normal 1");
                                Intent i = new Intent(getApplicationContext(), EntradasQrActivity.class);
                                startActivity(i);
                                finish();


                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {

                        try {

                            ja1 = new JSONArray();

                            do {
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

                            }while (cursor.moveToNext());

                            String sCadena = Conf.getQR().trim();
                            String palabra = sCadena.substring(0, 1);

                            if (ja1.getString(6).length() > 0) {
                                Conf.setEvento(ja1.getString(6));
                                Conf.setST("Aceptado");
                                Conf.setTipoReg("Auto");

                                Log.e("EscaneoVisita 2", "ListaGrupalEntradaActivity");
                                Intent i = new Intent(getApplicationContext(), ListaGrupalEntradaActivity.class);
                                startActivity(i);
                                finish();
                            } else if (ja1.getString(5).equals("2")) {
                                Conf.setST("Aceptado");
                                Conf.setTipoReg("Auto");

                                Log.e("EscaneoVisita 2", "AccesosMultiplesActivity");
                                Intent i = new Intent(getApplicationContext(), AccesosMultiplesActivity.class);
                                startActivity(i);
                                finish();
                            } else if (palabra.equals("M")) {
                                Conf.setST("Aceptado");
                                Conf.setTipoReg("Auto");

                                Log.e("EscaneoVisita 2", "AccesosMultiplesActivity");
                                Intent i = new Intent(getApplicationContext(), AccesosMultiplesActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Conf.setST("Aceptado");
                                Conf.setTipoReg("Auto");

                                Log.e("EscaneoVisita 2", "AccesosActivity");
                                Intent i = new Intent(getApplicationContext(), AccesosActivity.class);
                                startActivity(i);
                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }else {
                    Conf.setST("Denegado");

                    Log.e("EscaneoVisita", "Denegado sin resultados");
                    Intent i = new Intent(getApplicationContext(), AccesosActivity.class);
                    startActivity(i);
                    finish();
                }
            }catch (Exception ex){
                Log.e("Exception", ex.toString());
            }finally {
                cursor.close();
            }

        }
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

                        Intent i = new Intent(getApplicationContext(), AccesoAutosActivity.class);
                        startActivity(i);
                        finish();
                        //Log.e("Error ", "LINKOM ST: " + "Incorrecto");

                    }else {
                        Conf.setTipoReg("Nada");
                        Conf.setST("Aceptado");
                        Intent i = new Intent(getApplicationContext(), AccesoAutosActivity.class);
                        startActivity(i);
                        finish();
                        //Log.e("Error ", "LINKOM ST: " + "Correcto");


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

                        Intent i = new Intent(getApplicationContext(), AccesosActivity.class);
                        startActivity(i);
                        finish();

                    } else {

                        if (Integer.parseInt(Conf.getPreQr()) == 1) {

                            response = response.replace("][", ",");

                            try {

                                ja1 = new JSONArray(response);
                                String sCadena = Conf.getQR().trim();
                                String palabra = sCadena.substring(0, 1);

                                if (ja1.getString(6).length() > 0) {
                                    Conf.setEvento(ja1.getString(6));
                                    Conf.setST("Aceptado");
                                    Intent i = new Intent(getApplicationContext(), ListaGrupalEntradaActivity.class);
                                    startActivity(i);
                                    finish();
                                } else if (ja1.getString(5).equals("2")) {
                                    Conf.setST("Aceptado");
                                    Conf.setTipoQr("Multiples");
                                    Intent i = new Intent(getApplicationContext(), EntradasQrActivity.class);
                                    startActivity(i);
                                    finish();
                                } else if (palabra.equals("M")) {
                                    Conf.setST("Aceptado");
                                    Conf.setTipoQr("Multiples");
                                    Intent i = new Intent(getApplicationContext(), EntradasQrActivity.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Conf.setST("Aceptado");
                                    Conf.setTipoQr("Normal");
                                    Intent i = new Intent(getApplicationContext(), EntradasQrActivity.class);
                                    startActivity(i);
                                    finish();


                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            response = response.replace("][", ",");

                            try {

                                ja1 = new JSONArray(response);
                                String sCadena = Conf.getQR().trim();
                                String palabra = sCadena.substring(0, 1);

                                if (ja1.getString(6).length() > 0) {
                                    Conf.setEvento(ja1.getString(6));
                                    Conf.setST("Aceptado");
                                    Conf.setTipoReg("Auto");

                                    Intent i = new Intent(getApplicationContext(), ListaGrupalEntradaActivity.class);
                                    startActivity(i);
                                    finish();
                                } else if (ja1.getString(5).equals("2")) {
                                    Conf.setST("Aceptado");
                                    Conf.setTipoReg("Auto");

                                    Intent i = new Intent(getApplicationContext(), AccesosMultiplesActivity.class);
                                    startActivity(i);
                                    finish();
                                } else if (palabra.equals("M")) {
                                    Conf.setST("Aceptado");
                                    Conf.setTipoReg("Auto");

                                    Intent i = new Intent(getApplicationContext(), AccesosMultiplesActivity.class);
                                    startActivity(i);
                                    finish();
                                } else {
                                    Conf.setST("Aceptado");
                                    Conf.setTipoReg("Auto");
                                    Intent i = new Intent(getApplicationContext(), AccesosActivity.class);
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

    public void QR_codigoOffline() {

        String qrs = qr.getText().toString().trim();
        String[] a = qrs.split("");
        String pl = a[0];
        String sl = a[1];
        Log.e("Error ", "LKMST: " + pl + sl);
        Log.e("Error ", "LKMST: " + qr.getText().toString().trim());

        if ((pl + sl).equals("AR")) {

            try {
                String qr_auto =  qr.getText().toString().trim();
                String id_residencial = Conf.getResid().trim();

                String[] parametros = {qr_auto, id_residencial};
                Cursor cursor = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_AUTO, null, null, parametros, null);

                if (cursor.moveToFirst()){
                    Conf.setQR(qr.getText().toString().trim());
                    Conf.setST("Aceptado");
                    Intent i = new Intent(getApplicationContext(), AccesoAutosActivity.class);
                    startActivity(i);
                    finish();
                }else{
                    Conf.setQR(qr.getText().toString().trim());
                    Conf.setST("Denegado");
                    Intent i = new Intent(getApplicationContext(), AccesoAutosActivity.class);
                    startActivity(i);
                    finish();
                }

                cursor.close();
            }catch (Exception ex){
                Log.e("Exception ", ex.toString());
            }

        } else {

            try {
                String qr_visita = qr.getText().toString().trim();
                String id_residencial = Conf.getResid().trim();

                String[] parametros = {qr_visita, id_residencial};
                Cursor cursor = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_VISITA, null, "vst1", parametros,null);

                if (cursor.moveToFirst()){
                    if (Integer.parseInt(Conf.getPreQr()) == 1) {

                        try {
                            ja2 = new JSONArray();
                            ja2.put(cursor.getString(0));
                            ja2.put(cursor.getString(1));
                            ja2.put(cursor.getString(2));
                            ja2.put(cursor.getString(3));
                            ja2.put(cursor.getString(4));
                            ja2.put(cursor.getString(5));
                            ja2.put(cursor.getString(6));
                            ja2.put(cursor.getString(7));
                            ja2.put(cursor.getString(8));
                            ja2.put(cursor.getString(9));
                            ja2.put(cursor.getString(10));
                            ja2.put(cursor.getString(11));
                            ja2.put(cursor.getString(12));
                            ja2.put(cursor.getString(13));
                            ja2.put(cursor.getString(14));
                            ja2.put(cursor.getString(15));

                            String sCadena = qr.getText().toString().trim();
                            String palabra = sCadena.substring(0, 1);

                            if (ja2.getString(6).length() > 0) {
                                Conf.setEvento(ja2.getString(6));
                                Conf.setQR(qr.getText().toString().trim());
                                Conf.setST("Aceptado");
                                Intent i = new Intent(getApplicationContext(), ListaGrupalEntradaActivity.class);
                                startActivity(i);
                                finish();
                            } else if (ja2.getString(5).equals("2")) {
                                Conf.setST("Aceptado");
                                Conf.setQR(qr.getText().toString().trim());
                                Conf.setTipoQr("Multiples");

                                Intent i = new Intent(getApplicationContext(), EntradasQrActivity.class);
                                startActivity(i);
                                finish();
                            } else if (palabra.equals("M")) {
                                Conf.setST("Aceptado");
                                Conf.setQR(qr.getText().toString().trim());
                                Conf.setTipoQr("Multiples");

                                Intent i = new Intent(getApplicationContext(), EntradasQrActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Conf.setST("Aceptado");
                                Conf.setQR(qr.getText().toString().trim());
                                Conf.setTipoQr("Normal");

                                Intent i = new Intent(getApplicationContext(), EntradasQrActivity.class);
                                startActivity(i);
                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else {

                        try {
                            ja2 = new JSONArray();
                            ja2.put(cursor.getString(0));
                            ja2.put(cursor.getString(1));
                            ja2.put(cursor.getString(2));
                            ja2.put(cursor.getString(3));
                            ja2.put(cursor.getString(4));
                            ja2.put(cursor.getString(5));
                            ja2.put(cursor.getString(6));
                            ja2.put(cursor.getString(7));
                            ja2.put(cursor.getString(8));
                            ja2.put(cursor.getString(9));
                            ja2.put(cursor.getString(10));
                            ja2.put(cursor.getString(11));
                            ja2.put(cursor.getString(12));
                            ja2.put(cursor.getString(13));
                            ja2.put(cursor.getString(14));
                            ja2.put(cursor.getString(15));


                            String sCadena = qr.getText().toString().trim();
                            String palabra = sCadena.substring(0, 1);

                            if (ja2.getString(6).length() > 0) {
                                Conf.setEvento(ja2.getString(6));
                                Conf.setQR(qr.getText().toString().trim());
                                Conf.setST("Aceptado");
                                Intent i = new Intent(getApplicationContext(), ListaGrupalEntradaActivity.class);
                                startActivity(i);
                                finish();
                            } else if (ja2.getString(5).equals("2")) {
                                Conf.setST("Aceptado");
                                Conf.setQR(qr.getText().toString().trim());
                                Intent i = new Intent(getApplicationContext(), AccesosMultiplesActivity.class);
                                startActivity(i);
                                finish();
                            } else if (palabra.equals("M")) {
                                Conf.setST("Aceptado");
                                Conf.setQR(qr.getText().toString().trim());

                                Intent i = new Intent(getApplicationContext(), AccesosMultiplesActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Conf.setST("Aceptado");
                                Conf.setQR(qr.getText().toString().trim());

                                Intent i = new Intent(getApplicationContext(), AccesosActivity.class);
                                startActivity(i);
                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }else {
                    Conf.setST("Denegado");

                    Intent i = new Intent(getApplicationContext(), AccesosActivity.class);
                    startActivity(i);
                    finish();
                }
            }catch (Exception ex){

            }

        }
    }


    public void QR_codigo() {

        String qrs = qr.getText().toString().trim();
        String[] a = qrs.split("");
        String pl = a[0];
        String sl = a[1];
        Log.e("Error ", "LKMST: " + pl + sl);
        Log.e("Error ", "LKMST: " + qr.getText().toString().trim());

        if ((pl + sl).equals("AR")) {
            String url = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/auto1.php?bd_name=" + Conf.getBd() + "&bd_user=" + Conf.getBdUsu() + "&bd_pwd=" + Conf.getBdCon();

            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    if(response.equals("error")){
                        Conf.setQR(qr.getText().toString().trim());
                        Conf.setST("Denegado");
                        Intent i = new Intent(getApplicationContext(), AccesoAutosActivity.class);
                        startActivity(i);
                        finish();
                        //Log.e("Error ", "LINKOM ST: " + "Incorrecto");
                    }else {
                        Conf.setQR(qr.getText().toString().trim());
                        Conf.setST("Aceptado");
                        Intent i = new Intent(getApplicationContext(), AccesoAutosActivity.class);
                        startActivity(i);
                        finish();
                        //Log.e("Error ", "LINKOM ST: " + "Correcto");


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

                    Intent i = new Intent(getApplicationContext(), AccesosActivity.class);
                    startActivity(i);
                    finish();
                } else {

                    if (Integer.parseInt(Conf.getPreQr()) == 1) {

                        response = response.replace("][", ",");

                        try {
                            ja2 = new JSONArray(response);
                            String sCadena = qr.getText().toString().trim();
                            String palabra = sCadena.substring(0, 1);

                            if (ja2.getString(6).length() > 0) {
                                Conf.setEvento(ja2.getString(6));
                                Conf.setQR(qr.getText().toString().trim());
                                Conf.setST("Aceptado");
                                Intent i = new Intent(getApplicationContext(), ListaGrupalEntradaActivity.class);
                                startActivity(i);
                                finish();
                            } else if (ja2.getString(5).equals("2")) {
                                Conf.setST("Aceptado");
                                Conf.setQR(qr.getText().toString().trim());
                                Conf.setTipoQr("Multiples");

                                Intent i = new Intent(getApplicationContext(), EntradasQrActivity.class);
                                startActivity(i);
                                finish();
                            } else if (palabra.equals("M")) {
                                Conf.setST("Aceptado");
                                Conf.setQR(qr.getText().toString().trim());
                                Conf.setTipoQr("Multiples");

                                Intent i = new Intent(getApplicationContext(), EntradasQrActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Conf.setST("Aceptado");
                                Conf.setQR(qr.getText().toString().trim());
                                Conf.setTipoQr("Normal");

                                Intent i = new Intent(getApplicationContext(), EntradasQrActivity.class);
                                startActivity(i);
                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    } else {


                        response = response.replace("][", ",");

                        try {
                            ja2 = new JSONArray(response);
                            String sCadena = qr.getText().toString().trim();
                            String palabra = sCadena.substring(0, 1);

                            if (ja2.getString(6).length() > 0) {
                                Conf.setEvento(ja2.getString(6));
                                Conf.setQR(qr.getText().toString().trim());
                                Conf.setST("Aceptado");
                                Intent i = new Intent(getApplicationContext(), ListaGrupalEntradaActivity.class);
                                startActivity(i);
                                finish();
                            } else if (ja2.getString(5).equals("2")) {
                                Conf.setST("Aceptado");
                                Conf.setQR(qr.getText().toString().trim());
                                Intent i = new Intent(getApplicationContext(), AccesosMultiplesActivity.class);
                                startActivity(i);
                                finish();
                            } else if (palabra.equals("M")) {
                                Conf.setST("Aceptado");
                                Conf.setQR(qr.getText().toString().trim());

                                Intent i = new Intent(getApplicationContext(), AccesosMultiplesActivity.class);
                                startActivity(i);
                                finish();
                            } else {
                                Conf.setST("Aceptado");
                                Conf.setQR(qr.getText().toString().trim());

                                Intent i = new Intent(getApplicationContext(), AccesosActivity.class);
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






    public void placas() {

        if (Placas.getText().toString().equals("")) {

            Placas.setText("");

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EscaneoVisitaActivity.this);
            alertDialogBuilder.setTitle("Alerta");
            alertDialogBuilder
                    .setMessage("Placa Inexistente")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).create().show();
        } else if (Placas.getText().toString().equals(" ")) {

            Placas.setText("");

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EscaneoVisitaActivity.this);
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

                    if (response.equals("error")) {
                        Conf.setPlacas(Placas.getText().toString().trim());
                        Conf.setTipoReg("Auto");
                        Intent i = new Intent(getApplicationContext(), AccesoRegistroActivity.class);
                        startActivity(i);
                        finish();
                    } else {
                        response = response.replace("][", ",");
                        if (response.length() > 0) {
                            try {
                                ja3 = new JSONArray(response);
                                Conf.setPlacas(ja3.getString(9));
                                Conf.setQR(ja3.getString(2));
                                Conf.setTipoReg("Auto");

                                if(Integer.parseInt(Conf.getPreQr())==1) {
                                    Intent i = new Intent(getApplicationContext(), PreEntradasActivity.class);
                                    startActivity(i);
                                    finish();
                                }else{
                                    Intent i = new Intent(getApplicationContext(), AccesoRegistroActivity.class);
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

    public void placasOffline() {

        if (Placas.getText().toString().equals("")) {

            Placas.setText("");

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EscaneoVisitaActivity.this);
            alertDialogBuilder.setTitle("Alerta");
            alertDialogBuilder
                    .setMessage("Placa Inexistente")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).create().show();
        } else if (Placas.getText().toString().equals(" ")) {

            Placas.setText("");

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EscaneoVisitaActivity.this);
            alertDialogBuilder.setTitle("Alerta");
            alertDialogBuilder
                    .setMessage("Placa Inexistente")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).create().show();

        } else {

            try {
                String id_resid = Conf.getResid().trim();
                String placas = Placas.getText().toString().trim();
                String parametros[] = {id_resid, placas};

                Cursor cursor = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_DTL_ENTRADAS_SALIDAS, null, "consulta1", parametros, null);

                if (cursor.moveToFirst()){
                    ja3 = new JSONArray();
                    ja3.put(cursor.getString(0));
                    ja3.put(cursor.getString(1));
                    ja3.put(cursor.getString(2));
                    ja3.put(cursor.getString(3));
                    ja3.put(cursor.getString(4));
                    ja3.put(cursor.getString(5));
                    ja3.put(cursor.getString(6));
                    ja3.put(cursor.getString(7));
                    ja3.put(cursor.getString(8));
                    ja3.put(cursor.getString(9));
                    ja3.put(cursor.getString(10));
                    ja3.put(cursor.getString(11));
                    ja3.put(cursor.getString(12));
                    ja3.put(cursor.getString(13));
                    ja3.put(cursor.getString(14));
                    ja3.put(cursor.getString(15));

                    Conf.setPlacas(ja3.getString(9));
                    Conf.setQR(ja3.getString(2));
                    Conf.setTipoReg("Auto");

                    if(Integer.parseInt(Conf.getPreQr())==1) {
                        Log.e("EscaneoVisita", "PreEntradasActivity");
                        Intent i = new Intent(getApplicationContext(), PreEntradasActivity.class);
                        startActivity(i);
                        finish();
                    }else{
                        Log.e("EscaneoVisita", "AccesoRegistroActivity");
                        Intent i = new Intent(getApplicationContext(), AccesoRegistroActivity.class);
                        startActivity(i);
                        finish();
                    }
                }else {
                    Conf.setPlacas(Placas.getText().toString().trim());
                    Conf.setTipoReg("Auto");
                    Log.e("EscaneoVisita", "AccesoRegistroActivity2");
                    Intent i = new Intent(getApplicationContext(), AccesoRegistroActivity.class);
                    startActivity(i);
                    finish();
                }
                cursor.close();
            }catch (Exception ex){
                Log.e("Exception", ex.toString());
            }


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
