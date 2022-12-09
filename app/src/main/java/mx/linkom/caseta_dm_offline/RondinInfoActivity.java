package mx.linkom.caseta_dm_offline;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import mx.linkom.caseta_dm_offline.offline.Database.UrisContentProvider;
import mx.linkom.caseta_dm_offline.offline.Global_info;

public class RondinInfoActivity extends mx.linkom.caseta_dm_offline.Menu  implements OnMapReadyCallback {
    private mx.linkom.caseta_dm_offline.Configuracion Conf;
    private GoogleMap mMap;
    JSONArray ja1,ja2;
    TextView Nombre,Hora,Ubicacion;
    Button Registrar;
    LinearLayout registrar1;
    Button Incidencia;
    boolean Offline;
    ImageView iconoInternet;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rondines);
        Conf = new mx.linkom.caseta_dm_offline.Configuracion(this);
        registrar1 = (LinearLayout) findViewById(R.id.registrar1);
        Nombre = (TextView) findViewById(R.id.nombre);
        Hora = (TextView) findViewById(R.id.hora);
        Ubicacion = (TextView) findViewById(R.id.ubicacion);
        Registrar = (Button) findViewById(R.id.btnRegistrar);
        Incidencia = (Button) findViewById(R.id.btnIncidencia);
        iconoInternet = (ImageView) findViewById(R.id.iconoInternetRondinInfo);
       // dtl_rondines();

        if (Global_info.getINTERNET().equals("Si")){
            rondin();
            Offline = false;
            iconoInternet.setImageResource(R.drawable.ic_online);
        }else {
            rondinOffline();
            Offline = true;
            iconoInternet.setImageResource(R.drawable.ic_offline);
            View mapa = findViewById(R.id.map);
            mapa.setVisibility(View.INVISIBLE);
        }

        iconoInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Offline){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RondinInfoActivity.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("Aplicación funcionando en modo offline \n\nDatos actualizados hasta: \n\n"+Global_info.getULTIMA_ACTUALIZACION())
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();
                }else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RondinInfoActivity.this);
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
                Intent i = new Intent(getApplicationContext(), RondinIncidencias.class);
                startActivity(i);
                finish();
            }
        });
        Registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validacion();
            }
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            registrar1.setVisibility(View.VISIBLE);

            if (Offline){

            }else {
                locationStart();
            }
        }
        Log.e("Error ", "LINKOM ST: " +  Conf.getRondin());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void rondinOffline() {

        Log.e("error", "RondinOffline");

        try {
            Cursor cursorRondines2 = null;

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
            String id_residencial = Conf.getResid().trim();
            String dia = fecha;
            String tiempo = hora;

            Log.e("error", "SELECT ubi.id, ubi.hora, ubis.nombre, dia.id, dia.dia, rondin.id, rondin.nombre FROM rondines_ubicaciones as ubi, rondines_dia as dia, rondines as rondin, ubicaciones as ubis WHERE ubi.id="+"'"+id+"'"+" and ubi.id_usuario="+"'"+usuario+"'"+" and ubi.id_rondin=dia.id_rondin and ubi.id_rondin=rondin.id and dia.dia="+"'"+dia+"'"+" and ubi.id_residencial="+"'"+id_residencial+"'"+" and ubi.hora<="+"'"+tiempo+"'"+" and ubis.id=ubi.id_ubicacion and NOT EXISTS (SELECT * FROM rondines_dtl WHERE rondines_dtl.id_ubicaciones=ubi.id and rondines_dtl.id_dia=dia.id and rondines_dtl.id_rondin=rondin.id)");

            String parametros[] = {id, usuario, dia, id_residencial, tiempo};

            cursorRondines2 = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_RONDINESDIA, null, null, parametros, null);

            ja1 = new JSONArray();

            if (cursorRondines2.moveToFirst()){
                ja1.put(cursorRondines2.getString(0));
                ja1.put(cursorRondines2.getString(1));
                ja1.put(cursorRondines2.getString(2));
                ja1.put(cursorRondines2.getString(3));
                ja1.put(cursorRondines2.getString(4));
                ja1.put(cursorRondines2.getString(5));
                ja1.put(cursorRondines2.getString(6));

                Log.e("error", "RondinOffline ubo resp");
                ubicacionesOffline();
            }else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RondinInfoActivity.this);
                alertDialogBuilder.setTitle("Alerta");
                alertDialogBuilder
                        .setMessage("Error al obtener información de rondín")
                        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(getApplicationContext(), Rondines.class);
                                startActivity(i);
                                finish();
                            }
                        }).create().show();
            }

            cursorRondines2.close();

        }catch (Exception ex){
            System.out.println(ex.toString());
        }
    }

    public void rondin() {
        String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/rondines_2.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {
                        ja1 = new JSONArray(response);
                       ubicaciones();
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

    public void ubicacionesOffline() {

        Log.e("error", "UbicacionesOffline");

        try {
            Cursor cursorRondines3 = null;

            String id = ja1.getString(0);

            String parametros[] = {id};

            Log.e("error", "SELECT ubis.id, ubis.longitud, ubis.latitud FROM rondines_ubicaciones as ubi, ubicaciones as ubis WHERE  ubi.id="+"'"+id+"'"+" AND ubi.id_ubicacion=ubis.id and ubis.estatus=1");

            cursorRondines3 = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_UBICACIONES, null, null, parametros, null);

            ja2 = new JSONArray();

            if (cursorRondines3.moveToFirst()){
                ja2.put(cursorRondines3.getString(0));
                ja2.put(cursorRondines3.getString(1));
                ja2.put(cursorRondines3.getString(2));

                Nombre.setText(ja1.getString(6));
                Hora.setText(ja1.getString(1));
                Ubicacion.setText(ja1.getString(2));

                //Estas son las coordenadas que voy a registrar
                Conf.setUsuLatitud(ja2.getString(2));
                Conf.setUsuLongitud(ja2.getString(1));
            }

            cursorRondines3.close();


            /*SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(RondinInfoActivity.this);*/

        }catch (Exception ex){

        }
    }



    public void ubicaciones() {
        String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/rondines_3.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {
                        ja2 = new JSONArray(response);

                        Nombre.setText(ja1.getString(6));
                        Hora.setText(ja1.getString(1));
                        Ubicacion.setText(ja1.getString(2));
                        Conf.setUsuLatitud(ja2.getString(2));
                        Conf.setUsuLongitud(ja2.getString(1));
                        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                .findFragmentById(R.id.map);
                        mapFragment.getMapAsync(RondinInfoActivity.this);

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
                try {
                    params.put("id", ja1.getString(0));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = null;

        sydney = new LatLng( Double.parseDouble(Conf.getUsuLatitud()), Double.parseDouble( Conf.getUsuLongitud()));
        mMap.addMarker(new MarkerOptions().position(sydney));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,18 ));
    }

    private void locationStart() {

        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);

    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }
    public void setLocation(Location loc) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /* Aqui empieza la Clase Localizacion */
    public class Localizacion implements LocationListener {
        RondinInfoActivity mainActivity;
        public RondinInfoActivity getMainActivity() {
            return mainActivity;
        }
        public void setMainActivity(RondinInfoActivity mainActivity) {
            this.mainActivity = mainActivity;
        }
        @Override
        public void onLocationChanged(Location loc) {

            loc.getLatitude();
            loc.getLongitude();
            Conf.setUsuLatitud2(String.valueOf(loc.getLatitude()));
            Conf.setUsuLongitud2(String.valueOf(loc.getLongitude()));


            if (Offline){

            }else{
                this.mainActivity.setLocation(loc);
            }

        }
        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            //mensaje1.setText("GPS Desactivado");
            registrar1.setVisibility(View.GONE);

        }
        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            // mensaje1.setText("GPS Activado");
            registrar1.setVisibility(View.VISIBLE);

        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }

    public void Validacion() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RondinInfoActivity.this);
        alertDialogBuilder.setTitle("Alerta");
        alertDialogBuilder
                .setMessage("¿ Desea realizar el registro ?")
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    public void onClick(DialogInterface dialog, int id) {
                        Registrar.setEnabled(false);
                        if (Offline){
                            UbicacionOffline();
                        }else {
                            Ubicacion();
                        }
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent i = new Intent(getApplicationContext(), RondinInfoActivity.class);
                        startActivity(i);
                        finish();

                    }
                }).create().show();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void UbicacionOffline(){

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
        try {
            values.put("id_rondin", Integer.parseInt(ja1.getString(5)));
            values.put("id_dia", Integer.parseInt(ja1.getString(3)));
            values.put("id_ubicaciones", Integer.parseInt(ja1.getString(0)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        values.put("latitud", Conf.getUsuLatitud());
        values.put("longitud", Conf.getUsuLongitud());
        values.put("dia", fecha);
        values.put("hora", hora);
        values.put("estatus", 1);
        values.put("sqliteEstatus", 1);

        Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_RONDINESDTL,values);
        String idUri = uri.getLastPathSegment();
        int insertar = Integer.parseInt(idUri);

        if (insertar != -1){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RondinInfoActivity.this);
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
        }else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RondinInfoActivity.this);
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
    }



    public void Ubicacion(){
        String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/rondines_4.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response){


                if(response.equals("error")){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RondinInfoActivity.this);
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
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RondinInfoActivity.this);
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
                params.put("latitud", Conf.getUsuLatitud2());
                params.put("longitud", Conf.getUsuLongitud2());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), mx.linkom.caseta_dm_offline.Rondines.class);
        startActivity(intent);
        finish();
    }


}
