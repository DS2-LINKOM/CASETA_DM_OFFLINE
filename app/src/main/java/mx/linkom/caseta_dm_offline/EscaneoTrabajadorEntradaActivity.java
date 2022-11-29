package mx.linkom.caseta_dm_offline;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class EscaneoTrabajadorEntradaActivity extends mx.linkom.caseta_dm_offline.Menu{

    private CameraSource cameraSource;
    private SurfaceView cameraView;
    private final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private String token = "";
    private String tokenanterior = "";
    mx.linkom.caseta_dm_offline.Configuracion Conf;
    EditText Codigo;
    Button Buscar,Lector;
    LinearLayout Qr,Qr2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escaneo_trabajador_entrada);
        Conf = new mx.linkom.caseta_dm_offline.Configuracion(this);
        Conf.setQR(null);
        Conf.setST(null);

        Lector = (Button) findViewById(R.id.btnLector);
        Codigo = (EditText) findViewById(R.id.editText);
        Buscar = (Button) findViewById(R.id.btnBuscar);
        Qr = (LinearLayout) findViewById(R.id.qr);
        Qr2 = (LinearLayout) findViewById(R.id.qr2);
        Buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CodigoTr();
            }});
        cameraView = (SurfaceView) findViewById(R.id.camera_view);

        Lector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Qr.setVisibility(View.VISIBLE);
                Qr2.setVisibility(View.VISIBLE);
            }});


        initQR();


    }



    public void initQR() {

        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(this)
                        .setBarcodeFormats(Barcode.ALL_FORMATS)
                        .build();

        cameraSource = new CameraSource
                .Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1600, 1024)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                if (ActivityCompat.checkSelfPermission(EscaneoTrabajadorEntradaActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

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

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() > 0) {

                    token = barcodes.valueAt(0).displayValue.toString();

                    if (!token.equals(tokenanterior)) {

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
                                        tokenanterior = "";
                                    }
                                } catch (InterruptedException e) {
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

    public void QR() {
        String url = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/tbj_php1.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if(response.equals("error")){
                    Conf.setST("Denegado");
                    Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_dm_offline.AccesosTrabajadorActivity.class);
                    startActivity(i);
                    finish();
                }else {
                    response = response.replace("][",",");

                    Conf.setST("Aceptado");
                    Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_dm_offline.AccesosTrabajadorActivity.class);
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
                params.put("Codigo", Conf.getQR().trim());
                params.put("id_residencial", Conf.getResid().trim());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void CodigoTr() {
        String url = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/tbj_php1.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Conf.setQR(Codigo.getText().toString().trim());

                if(response.equals("error")){
                    Conf.setST("Denegado");
                    Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_dm_offline.AccesosTrabajadorActivity.class);
                    startActivity(i);
                    finish();
                }else {
                    response = response.replace("][",",");

                    Conf.setST("Aceptado");
                    Intent i = new Intent(getApplicationContext(), mx.linkom.caseta_dm_offline.AccesosTrabajadorActivity.class);
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
                params.put("Codigo", Codigo.getText().toString().trim());
                params.put("id_residencial", Conf.getResid().trim());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }



    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), mx.linkom.caseta_dm_offline.EntradasSalidasActivity.class);
        startActivity(intent);
        finish();
    }
}
