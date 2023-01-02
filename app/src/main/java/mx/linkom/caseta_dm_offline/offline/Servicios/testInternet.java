package mx.linkom.caseta_dm_offline.offline.Servicios;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mx.linkom.caseta_dm_offline.Configuracion;
import mx.linkom.caseta_dm_offline.R;
import mx.linkom.caseta_dm_offline.offline.Database.Database;
import mx.linkom.caseta_dm_offline.offline.Database.UrisContentProvider;
import mx.linkom.caseta_dm_offline.offline.Global_info;

public class testInternet extends Service {

    private Configuracion Conf;
    Global_info gInfo = new Global_info();
    boolean esperar = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        boolean internet;
                        Conf = new Configuracion(getApplicationContext());

                        while (true) {
                            //Saber si el dispositivo esta conectado auna red
                            internet = isOnline(testInternet.this);
                            try {
                                if (internet){
                                    //Hacer ping a google para comprobar conexión a internet
                                    if (esperar){
                                        Thread.sleep(10000);
                                        esperar = false;
                                    }
                                    new testInternet.Ping(testInternet.this).execute();
                                }else {
                                    System.out.println("No esta conectado a una red");
                                    Global_info.setINTERNET("No");
                                    gInfo.setSEGUNDOS(0);
                                }
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }finally {
                                //Asegurar que el contador no pase a mas de un minuto
                                int cant = gInfo.getSEGUNDOS();
                                if (cant > 20){
                                    gInfo.setSEGUNDOS(0);
                                }
                            }
                        }
                    }
                }
        ).start();

        //Se crea la notificación para informar que se va a ejcutar el servicio en primer plano
        final String CHANNELID = "Foreground Service ID";
        NotificationChannel channel = new NotificationChannel(
                CHANNELID,
                CHANNELID,
                NotificationManager.IMPORTANCE_LOW
        );

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNELID)
                .setContentText("Servicio necesario para funcionalidad en offline.")
                .setContentTitle("Demo caseta")
                .setSmallIcon(R.drawable.caseta_logo);

        //Llama el inicio del servicio en primer plano
        startForeground(1001, notification.build());


        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    public class Ping extends AsyncTask<Void,Void,Void> {

        //Dirección publica de google
        final String HOST = "8.8.8.8";
        boolean alcanzable;
        Context context;
        //Global_info gInfo = new Global_info();

        public Ping(Context context){
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void unused) {
            super.onPostExecute(unused);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Void doInBackground(Void... voids) {

            try {

                InetAddress direccion = InetAddress.getByName(HOST);
                alcanzable = direccion.isReachable(4000);

                if (alcanzable){ //Si el ping respondio

                    int tiempo = gInfo.getSEGUNDOS();
                    gInfo.setSEGUNDOS(tiempo+1);
                    gInfo.setINTERNET_DISPOSITIVO(true);
                    Global_info.setINTERNET("Si");

                    System.out.println("Segundo: " + tiempo);
                    System.out.println("Si hay internet: " + alcanzable);

                    LocalDateTime hoy = LocalDateTime.now();

                    int year = hoy.getYear();
                    int month = hoy.getMonthValue();
                    int day = hoy.getDayOfMonth();
                    int hour = hoy.getHour();
                    int minute = hoy.getMinute();
                    String hora = day + "/" + month + "/" +year+ ", hora: " + hour + ":" + minute;

                    if (tiempo+1 == 20){
                        esperar = true;
                        System.out.println("Paso un minuto de conexión aqui actualizo bd");

                        //Sincronizar SQLite a MySQL
                        enviarIncidencias(context);
                        enviarRondines_Dtl(context);
                        enviarRondines_Dtl_Qr(context);
                        enviarRondinesIncidencias(context);
                        enviarCorrespondencia(context);
                        enviarDtl_entradas_salidas_autos(context);
                        enviarDtl_entradas_salidas(context);
                        enviarVisita(context);


                        //Solo ejecutar si el servicio no se esta ejecutando
                        if (!servicioFotos()){
                            Cursor cursoFotos = null;

                            cursoFotos = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_FOTOS_OFFLINE,null,null,null);

                            ArrayList<String> titulos = new ArrayList<String>();
                            ArrayList<String> direccionesFirebase = new ArrayList<String>();
                            ArrayList<String> rutasDispositivo = new ArrayList<String>();

                            if (cursoFotos.moveToFirst()){
                                do {
                                    titulos.add(cursoFotos.getString(0));
                                    direccionesFirebase.add(cursoFotos.getString(1));
                                    rutasDispositivo.add(cursoFotos.getString(2));

                                } while (cursoFotos.moveToNext());
                            }

                            cursoFotos.close();


                            //Si hay fotos sin subir iniciar servicio para subir fotos a firebase

                            if (titulos.size() > 0 && direccionesFirebase.size() > 0){
                                System.out.println("Si hay fotos para subir");
                                Intent cargarFotos = new Intent(testInternet.this, subirFotos.class);
                                cargarFotos.putExtra("nombres", titulos);
                                cargarFotos.putExtra("direccionesFirebase", direccionesFirebase);
                                cargarFotos.putExtra("rutasDispositivo", rutasDispositivo);
                                startService(cargarFotos);
                            }
                        }


                        //Sincronizar MySQL a SQLite
                        recibirApp_caseta(context);
                        recibirRondines(context);
                        recibirRondines_qr(context);
                        recibirRondines_dia(context);
                        recibirRondines_dia_qr(context);
                        recibirRondines_ubicaciones(context);
                        recibirRondines_ubicaciones_qr(context);
                        recibirSesion_caseta(context);
                        recibirUbicaciones(context);
                        recibirUbicaciones_qr(context);
                        recibirLugar(context);
                        recibirUsuario(context);
                        recibirDtl_lugar_usuario(context);
                        //recibirCorrespondencia(context);
                        //recibirDtl_entradas_salidas(context);
                        //recibirVisita(context);
                        recibirAuto(context);
                        recibirCajones(context);
                        //recibirDtl_entradas_salidas_autos(context);

                        Global_info.setULTIMA_ACTUALIZACION(hora);
                        gInfo.setSEGUNDOS(0);

                        //Thread.sleep(15000);

                    }

                }else{
                    gInfo.setSEGUNDOS(0);
                    System.out.println("No hay internet: " + alcanzable);
                    System.out.println(gInfo.getULTIMA_ACTUALIZACION());
                    gInfo.setINTERNET_DISPOSITIVO(false);
                    Global_info.setINTERNET("No");
                }
            }catch (Exception ex){
                ex.toString();
            }
            return null;
        }
    }

    public void enviarVisita(Context context){

        System.out.println("Enviar Visita");

        try{

            final String url = Global_info.getURL()+"insertarVisita.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();

            Cursor cursor = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_VISITA, null, "sincronizacion", null, null);


            String datosinsertar = "";

            if (cursor.moveToFirst()){
                do {

                    datosinsertar += cursor.getString(1) + "sIgCaM"
                            + cursor.getString(2) + "sIgCaM"
                            + cursor.getString(3) + "sIgCaM"
                            + cursor.getString(4) + "sIgCaM"
                            + cursor.getString(5) + "sIgCaM"
                            + cursor.getString(6) + "sIgCaM"
                            + cursor.getString(7) + "sIgCaM"
                            + cursor.getString(8) + "sIgCaM"
                            + cursor.getString(9) + "sIgCaM"
                            + cursor.getString(10) + "sIgCaM"
                            + cursor.getString(11) + "sIgCaM"
                            + cursor.getString(12) + "sIgCaM"
                            + cursor.getString(13) + "sIgCaM"
                            + cursor.getString(14) + "sIgCaM"
                            + cursor.getString(15) + "sIgCaM" + "sIgObJ";

                } while (cursor.moveToNext());
            }
            cursor.close();

            //Si la cadena es vacia, solo actualizar tabla, si no es vacia enviar datos al servidor
            if (datosinsertar.isEmpty()){
                enviarVisitaActualizadas(getApplicationContext());
            }else {
                String finalDatosinsertar = datosinsertar;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {

                        if (response.equals("1")){

                            enviarVisitaActualizadas(getApplicationContext());

                        }else if (response.equals("0")){
                            Log.e("error", "Error al enviar Visita");
                        }

                    }
                }, new Response.ErrorListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("datos", finalDatosinsertar);
                        System.out.println("Envia esto Visita" + finalDatosinsertar);
                        return params;
                    }
                };

                MySingleton.getInstance(context).addToRequestQue(stringRequest);
            }


        }catch (Exception ex){
            Log.e("error", ex.toString());
        }

    }

    public void enviarVisitaActualizadas(Context context){

        System.out.println("Enviar Visita Actualizadas");

        try{

            final String url = Global_info.getURL()+"actualizarVisitas.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();

            Cursor cursor = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_VISITA, null, "sincronizacionActualizados", null, null);


            String datosinsertar = "";

            if (cursor.moveToFirst()){
                do {

                    datosinsertar += cursor.getString(0) + "sIgCaM"
                            + cursor.getString(1) + "sIgCaM"
                            + cursor.getString(9) + "sIgCaM" + "sIgObJ";

                } while (cursor.moveToNext());
            }
            cursor.close();

            //Si la cadena es vacia, solo actualizar tabla, si no es vacia enviar datos al servidor
            if (datosinsertar.isEmpty()){
                recibirVisita(getApplicationContext());
            }else {
                String finalDatosinsertar = datosinsertar;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {

                        if (response.equals("1")){

                            recibirVisita(getApplicationContext());

                        }else if (response.equals("0")){
                            Log.e("error", "Error al enviar Visita Actualizadas");
                        }

                    }
                }, new Response.ErrorListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("datos", finalDatosinsertar);
                        System.out.println("Envia esto Visita Actualizadas" + finalDatosinsertar);
                        return params;
                    }
                };

                MySingleton.getInstance(context).addToRequestQue(stringRequest);
            }


        }catch (Exception ex){
            Log.e("error", ex.toString());
        }

    }

    public void enviarDtl_entradas_salidas(Context context){

        System.out.println("Enviar dtl_entradas_salidas");

        try{

            final String urlInsertIncidencias = Global_info.getURL()+"insertarDtl_entradas_salidas.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();

            Cursor cursor = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_DTL_ENTRADAS_SALIDAS, null, "sincronizacion", null, null);


            String datosinsertarDtl_entradas_salidas = "";

            if (cursor.moveToFirst()){
                do {

                    datosinsertarDtl_entradas_salidas += cursor.getString(1) + "sIgCaM"
                            + cursor.getString(2) + "sIgCaM"
                            + cursor.getString(3) + "sIgCaM"
                            + cursor.getString(4) + "sIgCaM"
                            + cursor.getString(5) + "sIgCaM"
                            + cursor.getString(6) + "sIgCaM"
                            + cursor.getString(7) + "sIgCaM"
                            + cursor.getString(8) + "sIgCaM"
                            + cursor.getString(9) + "sIgCaM"
                            + cursor.getString(10) + "sIgCaM"
                            + cursor.getString(11) + "sIgCaM"
                            + cursor.getString(12) + "sIgCaM"
                            + cursor.getString(13) + "sIgCaM"
                            + cursor.getString(14) + "sIgCaM"
                            + cursor.getString(15) + "sIgCaM" + "sIgObJ";

                } while (cursor.moveToNext());
            }
            cursor.close();

            //Si la cadena es vacia, solo actualizar tabla, si no es vacia enviar datos al servidor
            if (datosinsertarDtl_entradas_salidas.isEmpty()){
                recibirDtl_entradas_salidas(getApplicationContext());
            }else {
                String finalDatosinsertar = datosinsertarDtl_entradas_salidas;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, urlInsertIncidencias, new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {

                        if (response.equals("1")){

                            recibirDtl_entradas_salidas(getApplicationContext());

                        }else if (response.equals("0")){
                            Log.e("error", "Error al enviar dtl_entradas_salidas");
                        }

                    }
                }, new Response.ErrorListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("datos", finalDatosinsertar);
                        System.out.println("Envia esto Dtl_entradas_salidas" + finalDatosinsertar);
                        return params;
                    }
                };

                MySingleton.getInstance(context).addToRequestQue(stringRequest);
            }


        }catch (Exception ex){
            Log.e("error", ex.toString());
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void enviarDtl_entradas_salidas_autos(Context context){

        System.out.println("Enviar dtl_entradas_salidas_autos");

        try{

            final String urlInsertIncidencias = Global_info.getURL()+"insertarDtl_entradas_salidas_autos.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();

            Cursor cursor = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_DTL_ENTRADAS_SALIDAS_AUTOS, null, "sincronizacion", null, null);


            String datosinsertarDtl_entradas_salidas_autos = "";

            if (cursor.moveToFirst()){
                do {

                    datosinsertarDtl_entradas_salidas_autos += cursor.getString(1) + "sIgCaM"
                            + cursor.getString(2) + "sIgCaM"
                            + cursor.getString(3) + "sIgCaM"
                            + cursor.getString(4) + "sIgCaM"
                            + cursor.getString(5) + "sIgCaM"
                            + cursor.getString(6) + "sIgCaM"
                            + cursor.getString(7) + "sIgCaM"
                            + cursor.getString(8) + "sIgCaM"
                            + cursor.getString(9) + "sIgCaM"
                            + cursor.getString(10) + "sIgCaM"
                            + cursor.getString(11) + "sIgCaM" + "sIgObJ";

                } while (cursor.moveToNext());
            }
            cursor.close();

            //Si la cadena es vacia, solo actualizar tabla, si no es vacia enviar datos al servidor
            if (datosinsertarDtl_entradas_salidas_autos.isEmpty()){
                recibirDtl_entradas_salidas_autos(getApplicationContext());
            }else {
                String finalDatosinsertar = datosinsertarDtl_entradas_salidas_autos;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, urlInsertIncidencias, new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {

                        if (response.equals("1")){

                            recibirDtl_entradas_salidas_autos(getApplicationContext());

                        }else if (response.equals("0")){
                            Log.e("error", "Error al enviar dtl_entradas_salidas_autos");
                        }

                    }
                }, new Response.ErrorListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("datos", finalDatosinsertar);
                        System.out.println("Envia esto Dtl_entradas_salidas_autos" + finalDatosinsertar);
                        return params;
                    }
                };

                MySingleton.getInstance(context).addToRequestQue(stringRequest);
            }


        }catch (Exception ex){
            Log.e("error", ex.toString());
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void enviarCorrespondenciasActualizadas(Context context){

        System.out.println("Enviar Correspondencias Actualizadas");

        try{
            Cursor actualizarCorrespondencia = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_CORRESPONDENCIA, null, "actualizados", null, null);

            if (actualizarCorrespondencia.moveToFirst()){
                new testInternet.Correspondencias_actualizadas_sync(actualizarCorrespondencia).execute();
            }else {
                actualizarCorrespondencia.close();
                recibirCorrespondencia(context);
            }

        }catch (Exception ex){
            Log.e("error", ex.toString());
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void enviarCorrespondencia(Context context){

        System.out.println("Enviar Correspondencia");

        try{

            Cursor insertICorrespondencia = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_CORRESPONDENCIA, null, "insertados", null, null);

            if (insertICorrespondencia.moveToFirst()){
                Log.e("info", "Si hay resp");
                new testInternet.Correspondencias_insertadas_sync(insertICorrespondencia).execute();
            }else{
                insertICorrespondencia.close();
                enviarCorrespondenciasActualizadas(getApplicationContext());
                System.out.println("No hay registros de correspondencia");
            }

        }catch (Exception ex){
            Log.e("error1", ex.toString());
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void enviarRondinesIncidencias(Context context){

        //System.out.println("Enviar RondinesIncidencias");

        try{

            final String urlInsertIncidencias = Global_info.getURL()+"insertarRondindesIncidencias.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();

            Cursor insertRondines_dtl_qr = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_RONDINESINCIDENCIAS, null, null, null);


            String datosinsertarIncidencias = "";

            if (insertRondines_dtl_qr.moveToFirst()){
                do {

                    datosinsertarIncidencias += insertRondines_dtl_qr.getString(1) + "sIgCaM"
                            + insertRondines_dtl_qr.getString(2) + "sIgCaM"
                            + insertRondines_dtl_qr.getString(3) + "sIgCaM"
                            + insertRondines_dtl_qr.getString(4) + "sIgCaM"
                            + insertRondines_dtl_qr.getString(5) + "sIgCaM"
                            + insertRondines_dtl_qr.getString(6) + "sIgCaM"
                            + insertRondines_dtl_qr.getString(7) + "sIgCaM"
                            + insertRondines_dtl_qr.getString(8) + "sIgCaM"
                            + insertRondines_dtl_qr.getString(9) + "sIgCaM"
                            + insertRondines_dtl_qr.getString(10) + "sIgCaM"
                            + insertRondines_dtl_qr.getString(11) + "sIgCaM"
                            + insertRondines_dtl_qr.getString(12) + "sIgCaM"
                            + insertRondines_dtl_qr.getString(13) + "sIgCaM"
                            + insertRondines_dtl_qr.getString(14) + "sIgCaM" + "sIgObJ";

                } while (insertRondines_dtl_qr.moveToNext());
            }
            insertRondines_dtl_qr.close();

            //Si la cadena es vacia, solo actualizar tabla, si no es vacia enviar datos al servidor
            if (datosinsertarIncidencias.isEmpty()){
                recibirRondines_incidencias(getApplicationContext());
            }else {
                String finalDatosinsertarIncidencias = datosinsertarIncidencias;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, urlInsertIncidencias, new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {

                        if (response.equals("1")){

                            recibirRondines_incidencias(getApplicationContext());

                        }else if (response.equals("0")){
                            Log.e("error", "Error al enviar Rondines incidencias");
                        }

                    }
                }, new Response.ErrorListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("datos", finalDatosinsertarIncidencias);
                        System.out.println("Envia esto rondines incidencias" + finalDatosinsertarIncidencias);
                        return params;
                    }
                };

                MySingleton.getInstance(context).addToRequestQue(stringRequest);
            }


        }catch (Exception ex){
            Log.e("error", ex.toString());
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void enviarRondines_Dtl_Qr(Context context){

        //System.out.println("Enviar Rondines_Dtl_Qr");

        try{

            final String urlInsertIncidencias = Global_info.getURL()+"insertarRondines_dtl_qr.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();

            Cursor insertRondines_dtl_qr = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_RONDINESDTLQR, null, null, null);

            String datosinsertarIncidencias = "";

            if (insertRondines_dtl_qr.moveToFirst()){
                do {

                    datosinsertarIncidencias += insertRondines_dtl_qr.getString(1) + "sIgCaM"
                            + insertRondines_dtl_qr.getString(2) + "sIgCaM"
                            + insertRondines_dtl_qr.getString(3) + "sIgCaM"
                            + insertRondines_dtl_qr.getString(4) + "sIgCaM"
                            + insertRondines_dtl_qr.getString(5) + "sIgCaM"
                            + insertRondines_dtl_qr.getString(6) + "sIgCaM"
                            + insertRondines_dtl_qr.getString(7) + "sIgCaM"
                            + insertRondines_dtl_qr.getString(8) + "sIgCaM" + "sIgObJ";

                } while (insertRondines_dtl_qr.moveToNext());
            }
            insertRondines_dtl_qr.close();

            if (datosinsertarIncidencias.isEmpty()){
                recibirRondines_dtl_qr(getApplicationContext());
            }else {
                String finalDatosinsertarIncidencias = datosinsertarIncidencias;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, urlInsertIncidencias, new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {

                        if (response.equals("1")){

                            recibirRondines_dtl_qr(getApplicationContext());

                        }else if (response.equals("0")){
                            Log.e("error", "Error al enviar Rondines dtl qr");
                        }

                    }
                }, new Response.ErrorListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("datos", finalDatosinsertarIncidencias);
                        System.out.println("Envia esto rondines dtl qr" + finalDatosinsertarIncidencias);
                        return params;
                    }
                };

                MySingleton.getInstance(context).addToRequestQue(stringRequest);
            }


        }catch (Exception ex){
            Log.e("error", ex.toString());
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void enviarRondines_Dtl(Context context){

        try{

            final String urlInsertIncidencias = Global_info.getURL()+"insertarRondines_dtl.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();

            Cursor insertRondines_dtl = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_RONDINESDTL, null, null, null);

            String datosinsertarIncidencias = "";

            if (insertRondines_dtl.moveToFirst()){
                do {

                    datosinsertarIncidencias += insertRondines_dtl.getString(1) + "sIgCaM"
                            + insertRondines_dtl.getString(2) + "sIgCaM"
                            + insertRondines_dtl.getString(3) + "sIgCaM"
                            + insertRondines_dtl.getString(4) + "sIgCaM"
                            + insertRondines_dtl.getString(5) + "sIgCaM"
                            + insertRondines_dtl.getString(6) + "sIgCaM"
                            + insertRondines_dtl.getString(7) + "sIgCaM"
                            + insertRondines_dtl.getString(8) + "sIgCaM"
                            + insertRondines_dtl.getString(9) + "sIgCaM"
                            + insertRondines_dtl.getString(10) + "sIgCaM" + "sIgObJ";

                } while (insertRondines_dtl.moveToNext());
            }
            insertRondines_dtl.close();

            if (datosinsertarIncidencias.isEmpty()){
                recibirRondines_dtl(getApplicationContext());
            }else {
                String finalDatosinsertarIncidencias = datosinsertarIncidencias;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, urlInsertIncidencias, new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {

                        if (response.equals("1")){

                            recibirRondines_dtl(getApplicationContext());

                        }else if (response.equals("0")){
                            Log.e("error", "Error al enviar rondines dtl");
                        }

                    }
                }, new Response.ErrorListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("datos", finalDatosinsertarIncidencias);
                        System.out.println("Envia esto rondines dtl" + finalDatosinsertarIncidencias);
                        return params;
                    }
                };

                MySingleton.getInstance(context).addToRequestQue(stringRequest);
            }


        }catch (Exception ex){
            Log.e("error", ex.toString());
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void enviarIncidencias(Context context){

        System.out.println("Enviar incidencias");

        try{

            final String urlInsertIncidencias = Global_info.getURL()+"insertarIncidencias.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();

            Cursor insertIncidencias = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_INCIDENCIAS, null, null, null);

            String datosinsertarIncidencias = "";

            if (insertIncidencias.moveToFirst()){
                do {

                    datosinsertarIncidencias += insertIncidencias.getString(1) + "sIgCaM"
                            + insertIncidencias.getString(2) + "sIgCaM"
                            + insertIncidencias.getString(3) + "sIgCaM"
                            + insertIncidencias.getString(4) + "sIgCaM"
                            + insertIncidencias.getString(5) + "sIgCaM"
                            + insertIncidencias.getString(6) + "sIgCaM"
                            + insertIncidencias.getString(7) + "sIgCaM"
                            + insertIncidencias.getString(8) + "sIgCaM"
                            + insertIncidencias.getString(9) + "sIgCaM"
                            + insertIncidencias.getString(10) + "sIgCaM"
                            + insertIncidencias.getString(11) + "sIgCaM"
                            + insertIncidencias.getString(12) + "sIgCaM"
                            + insertIncidencias.getString(13) + "sIgCaM" + "sIgObJ";

                } while (insertIncidencias.moveToNext());
            }
            insertIncidencias.close();

            if (datosinsertarIncidencias.isEmpty()){
                recibirIncidencias(getApplicationContext());
            }else {
                String finalDatosinsertarIncidencias = datosinsertarIncidencias;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, urlInsertIncidencias, new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {

                        if (response.equals("1")){

                            recibirIncidencias(getApplicationContext());

                        }else if (response.equals("0")){
                            Log.e("error", "Error al enviar incidencias");
                        }

                    }
                }, new Response.ErrorListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("datos", finalDatosinsertarIncidencias);
                        //System.out.println("Envia esto " + finalDatosinsertarIncidencias);
                        return params;
                    }
                };

                MySingleton.getInstance(context).addToRequestQue(stringRequest);
            }


        }catch (Exception ex){
            Log.e("error", ex.toString());
        }

    }



    public void recibirIncidencias(Context context){

        Configuracion Conf = new Configuracion(context.getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerIncidencias.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon(), new Response.Listener<String>() {
            //Se ejcuta cuando se obtiene una respuesta
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                int eliminar = getContentResolver().delete(UrisContentProvider.URI_CONTENIDO_INCIDENCIAS, null, null);

                //System.out.println("Valor de eliminar en recibir app incidencias: " + eliminar);

                if (eliminar >= 0){
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i<array.length(); i++){
                            JSONObject object = array.getJSONObject(i);
                            ContentValues values = new ContentValues();
                            values.put("id", object.getInt("id"));
                            values.put("id_residencial", object.getInt("id_residencial"));
                            values.put("id_usuario", object.getInt("id_usuario"));
                            values.put("id_tipo", object.getInt("id_tipo"));
                            values.put("id_rondin", object.getInt("id_rondin"));
                            values.put("dia", object.getString("dia"));
                            values.put("hora", object.getString("hora"));
                            values.put("detalle", object.getString("detalle"));
                            values.put("accion", object.getString("accion"));
                            values.put("foto1", object.getString("foto1"));
                            values.put("foto2", object.getString("foto2"));
                            values.put("foto3", object.getString("foto3"));
                            values.put("club", object.getInt("club"));
                            values.put("estatus", object.getInt("estatus"));
                            values.put("sqliteEstatus", 0);

                            Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_INCIDENCIAS, values);
                            if (uri == null) Log.e("error", "Error al registrar el registro: " + values.toString());

                        }
                        System.out.println("incidencias importadas");
                    }catch (Exception ex){
                        Log.e("error", ex.toString());
                    }
                }
            }
        }, new Response.ErrorListener() {
            //Método para manejar errores de la petición
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", ""+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_residencial", Conf.getResid());
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQue(stringRequest);

    }


    public void recibirApp_caseta(Context context){
        Configuracion Conf = new Configuracion(context.getApplicationContext());

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerMenu.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon(), new Response.Listener<String>() {
                //Se ejcuta cuando se obtiene una respuesta
                @Override
                public void onResponse(String response) {
                    int eliminar = getContentResolver().delete(UrisContentProvider.URI_CONTENIDO_APP_CASETA, null, null);

                    //System.out.println("Valor de eliminar en recibir app caseta: " + eliminar);

                    if (eliminar >= 0){
                        try {
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i<array.length(); i++){
                                JSONObject object = array.getJSONObject(i);
                                ContentValues values = new ContentValues();
                                values.put("id ", object.getInt("id"));
                                values.put("id_residencial", object.getInt("id_residencial"));
                                values.put("qr", object.getInt("qr"));
                                values.put("registro", object.getInt("registro"));
                                values.put("pre_entradas", object.getInt("pre_entradas"));
                                values.put("trabajadores", object.getString("trabajadores"));
                                values.put("consulta_placas", object.getString("consulta_placas"));
                                values.put("consulta_trabajadores", object.getString("consulta_trabajadores"));
                                values.put("incidencias", object.getString("incidencias"));
                                values.put("correspondencia", object.getString("correspondencia"));
                                values.put("rondin", object.getString("rondin"));
                                values.put("tickete", object.getString("tickete"));
                                values.put("ticketr", object.getInt("ticketr"));
                                values.put("estatus", object.getInt("estatus"));
                                values.put("sqliteEstatus", 0);

                                Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_APP_CASETA, values);
                                if (uri == null) Log.e("error", "Error al registrar el registro: " + values.toString());

                            }

                            recibirApp_caseta_ima(context);
                            System.out.println("app_caseta importadas");
                        }catch (Exception ex){
                            System.out.println(ex.toString());
                        }
                    }
                }
            }, new Response.ErrorListener() {
                //Método para manejar errores de la petición
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error", ""+error);
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("id_residencial", Conf.getResid());
                    return params;
                }
            };
            MySingleton.getInstance(context).addToRequestQue(stringRequest);


    }


    public void recibirApp_caseta_ima(Context context){
        Configuracion Conf = new Configuracion(context.getApplicationContext());

        Cursor cursor = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_APP_CASETA, null, null, null, null);
        String id = "0";
        if (cursor.moveToFirst()){
            id = cursor.getString(0);
        }
        String finalId = id;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerAppCasetaIma.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon(), new Response.Listener<String>() {
            //Se ejcuta cuando se obtiene una respuesta
            @Override
            public void onResponse(String response) {
                int eliminar = getContentResolver().delete(UrisContentProvider.URI_CONTENIDO_APPCASETAIMA, null, null);

                //System.out.println("Valor de eliminar en recibir app caseta: " + eliminar);

                if (eliminar >= 0){
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i<array.length(); i++){
                            JSONObject object = array.getJSONObject(i);
                            ContentValues values = new ContentValues();
                            values.put("id ", object.getInt("id"));
                            values.put("id_residencial", object.getInt("id_residencial"));
                            values.put("id_app", object.getInt("id_app"));
                            values.put("foto1", object.getInt("foto1"));
                            values.put("nombre_foto1", object.getString("nombre_foto1"));
                            values.put("foto2", object.getInt("foto2"));
                            values.put("nombre_foto2", object.getString("nombre_foto2"));
                            values.put("foto3", object.getInt("foto3"));
                            values.put("nombre_foto3", object.getString("nombre_foto3"));
                            values.put("estatus", object.getInt("estatus"));
                            values.put("sqliteEstatus", 0);

                            Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_APPCASETAIMA, values);
                            if (uri == null) Log.e("error", "Error al registrar el registro: " + values.toString());

                        }
                        System.out.println("app_caseta_ima importadas");
                    }catch (Exception ex){
                        System.out.println(ex.toString());
                    }
                }
            }
        }, new Response.ErrorListener() {
            //Método para manejar errores de la petición
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", ""+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_residencial", Conf.getResid());
                params.put("id_app", finalId);
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQue(stringRequest);


    }

    public void recibirRondines(Context context){

        Configuracion Conf = new Configuracion(context.getApplicationContext());

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerRondines.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon(), new Response.Listener<String>() {
                //Se ejcuta cuando se obtiene una respuesta
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(String response) {

                    int eliminar = getContentResolver().delete(UrisContentProvider.URI_CONTENIDO_RONDINES, null, null);

                    //System.out.println("Valor de eliminar en recibir rondines: " + eliminar);

                    if (eliminar >= 0){
                        try {
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i<array.length(); i++){
                                JSONObject object = array.getJSONObject(i);
                                ContentValues values = new ContentValues();
                                values.put("id", object.getInt("id"));
                                values.put("id_residencial", object.getInt("id_residencial"));
                                values.put("nombre", object.getString("nombre"));
                                values.put("club", object.getInt("club"));
                                values.put("estatus", object.getInt("estatus"));
                                values.put("sqliteEstatus", 0);

                                Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_RONDINES, values);
                                if (uri == null) Log.e("error", "Error al registrar el registro: " + values.toString());
                            }
                            System.out.println("rondines importados");
                        }catch (Exception ex){
                            Log.e("error", ex.toString());
                        }
                    }

                }
            }, new Response.ErrorListener() {
                //Método para manejar errores de la petición
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error", ""+error);
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("id_residencial", Conf.getResid());
                    return params;
                }
            };
            MySingleton.getInstance(context).addToRequestQue(stringRequest);


    }

    public void recibirRondines_qr(Context context){

        Configuracion Conf = new Configuracion(context.getApplicationContext());

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerRondines_qr.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon(), new Response.Listener<String>() {
                //Se ejcuta cuando se obtiene una respuesta
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(String response) {

                    int eliminar = getContentResolver().delete(UrisContentProvider.URI_CONTENIDO_RONDINESQR, null, null);

                    //System.out.println("Valor de eliminar en recibir rondines_qr: " + eliminar);

                    if (eliminar >= 0){
                        try {
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i<array.length(); i++){
                                JSONObject object = array.getJSONObject(i);
                                ContentValues values = new ContentValues();
                                values.put("id", object.getInt("id"));
                                values.put("id_residencial", object.getInt("id_residencial"));
                                values.put("nombre", object.getString("nombre"));
                                values.put("club", object.getInt("club"));
                                values.put("estatus", object.getInt("estatus"));
                                values.put("sqliteEstatus", 0);

                                Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_RONDINESQR, values);
                                if (uri == null) Log.e("error", "Error al registrar el registro: " + values.toString());
                            }
                            System.out.println("rondines_qr importados");
                        }catch (Exception ex){
                            Log.e("error", ex.toString());
                        }
                    }

                }
            }, new Response.ErrorListener() {
                //Método para manejar errores de la petición
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error", ""+error);
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("id_residencial", Conf.getResid());
                    return params;
                }
            };
            MySingleton.getInstance(context).addToRequestQue(stringRequest);

    }

    public void recibirRondines_dia(Context context){

        Configuracion Conf = new Configuracion(context.getApplicationContext());


            StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerRondines_dia.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon(), new Response.Listener<String>() {
                //Se ejcuta cuando se obtiene una respuesta
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(String response) {

                    int eliminar = getContentResolver().delete(UrisContentProvider.URI_CONTENIDO_RONDINESDIA, null, null);

                    //System.out.println("Valor de eliminar en recibir rondines_dia: " + eliminar);

                    if (eliminar >= 0){
                        try {
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i<array.length(); i++){
                                JSONObject object = array.getJSONObject(i);
                                ContentValues values = new ContentValues();
                                values.put("id", object.getInt("id"));
                                values.put("id_residencial", object.getInt("id_residencial"));
                                values.put("id_rondin", object.getInt("id_rondin"));
                                values.put("dia", object.getString("dia"));
                                values.put("club", object.getInt("club"));
                                values.put("estatus", object.getInt("estatus"));
                                values.put("sqliteEstatus", 0);

                                Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_RONDINESDIA, values);
                                if (uri == null) Log.e("error", "Error al registrar el registro: " + values.toString());
                            }
                            System.out.println("rondines_dia importados");
                        }catch (Exception ex){
                            Log.e("error", ex.toString());
                        }
                    }

                }
            }, new Response.ErrorListener() {
                //Método para manejar errores de la petición
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error", ""+error);
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("id_residencial", Conf.getResid());
                    return params;
                }
            };
            MySingleton.getInstance(context).addToRequestQue(stringRequest);


    }

    public void recibirRondines_dia_qr(Context context){

        Configuracion Conf = new Configuracion(context.getApplicationContext());

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerRondines_dia_qr.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon(), new Response.Listener<String>() {
                //Se ejcuta cuando se obtiene una respuesta
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(String response) {

                    int eliminar = getContentResolver().delete(UrisContentProvider.URI_CONTENIDO_RONDINESDIAQR, null, null);

                    //System.out.println("Valor de eliminar en recibir rondines_dia_qr: " + eliminar);

                    if (eliminar >= 0){
                        try {
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i<array.length(); i++){
                                JSONObject object = array.getJSONObject(i);
                                ContentValues values = new ContentValues();
                                values.put("id", object.getInt("id"));
                                values.put("id_residencial", object.getInt("id_residencial"));
                                values.put("id_rondin", object.getInt("id_rondin"));
                                values.put("dia", object.getString("dia"));
                                values.put("club", object.getInt("club"));
                                values.put("estatus", object.getInt("estatus"));
                                values.put("sqliteEstatus", 0);

                                Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_RONDINESDIAQR, values);
                                if (uri == null) Log.e("error", "Error al registrar el registro: " + values.toString());

                            }
                            System.out.println("rondines_dia_qr importados");
                        }catch (Exception ex){
                            Log.e("error", ex.toString());
                        }
                    }
                }
            }, new Response.ErrorListener() {
                //Método para manejar errores de la petición
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error", ""+error.toString());
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("id_residencial", Conf.getResid());
                    return params;
                }
            };
            MySingleton.getInstance(context).addToRequestQue(stringRequest);
    }

    public void recibirRondines_dtl(Context context){

        Configuracion Conf = new Configuracion(context.getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerRondines_dtl.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon(), new Response.Listener<String>() {
            //Se ejcuta cuando se obtiene una respuesta
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                int eliminar = getContentResolver().delete(UrisContentProvider.URI_CONTENIDO_RONDINESDTL, null, null);
                //System.out.println("Valor de eliminar en recibir rondines_dtl: " + eliminar);

                if (eliminar >= 0){
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i<array.length(); i++){
                            JSONObject object = array.getJSONObject(i);
                            ContentValues values = new ContentValues();
                            values.put("id", object.getInt("id"));
                            values.put("id_residencial", object.getInt("id_residencial"));
                            values.put("id_rondin", object.getInt("id_rondin"));
                            values.put("id_dia", object.getString("id_dia"));
                            values.put("id_ubicaciones", object.getInt("id_ubicaciones"));
                            values.put("latitud", object.getString("latitud"));
                            values.put("longitud", object.getString("longitud"));
                            values.put("dia", object.getString("dia"));
                            values.put("hora", object.getString("hora"));
                            values.put("estatus", object.getInt("estatus"));
                            values.put("sqliteEstatus", 0);

                            Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_RONDINESDTL, values);
                            if (uri == null) Log.e("error", "Error al registrar el registro: " + values.toString());

                        }
                        System.out.println("rondines_dtl importados");
                    }catch (Exception ex){
                        System.out.println(ex.toString());
                    }
                }
            }
        }, new Response.ErrorListener() {
            //Método para manejar errores de la petición
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", ""+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_residencial", Conf.getResid());
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQue(stringRequest);

    }

    public void recibirRondines_dtl_qr(Context context){

        Configuracion Conf = new Configuracion(context.getApplicationContext());

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerRondines_dtl_qr.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon(), new Response.Listener<String>() {
                //Se ejcuta cuando se obtiene una respuesta
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(String response) {

                    int eliminar = getContentResolver().delete(UrisContentProvider.URI_CONTENIDO_RONDINESDTLQR, null, null);

                    //System.out.println("Valor de eliminar en recibir rondines_dtl_qr: " + eliminar);
                    if (eliminar >= 0){
                        try {
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i<array.length(); i++){
                                JSONObject object = array.getJSONObject(i);
                                ContentValues values = new ContentValues();
                                values.put("id", object.getInt("id"));
                                values.put("id_residencial", object.getInt("id_residencial"));
                                values.put("id_rondin", object.getInt("id_rondin"));
                                values.put("id_dia", object.getInt("id_dia"));
                                values.put("id_ubicaciones", object.getInt("id_ubicaciones"));
                                values.put("dia", object.getString("dia"));
                                values.put("hora", object.getString("hora"));
                                values.put("estatus", object.getInt("estatus"));
                                values.put("sqliteEstatus", 0);

                                Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_RONDINESDTLQR, values);
                                if (uri == null) Log.e("error", "Error al registrar el registro: " + values.toString());
                            }
                            System.out.println("rondines_dtl_qr importados");
                        }catch (Exception ex){
                            System.out.println(ex.toString());
                        }
                    }

                }
            }, new Response.ErrorListener() {
                //Método para manejar errores de la petición
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error", ""+error);
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("id_residencial", Conf.getResid());
                    return params;
                }
            };
            MySingleton.getInstance(context).addToRequestQue(stringRequest);
    }


    public void recibirRondines_incidencias(Context context){

        Configuracion Conf = new Configuracion(context.getApplicationContext());

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerRondines_incidencias.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon(), new Response.Listener<String>() {
                //Se ejcuta cuando se obtiene una respuesta
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(String response) {

                    int eliminar = getContentResolver().delete(UrisContentProvider.URI_CONTENIDO_RONDINESINCIDENCIAS, null, null);

                    //System.out.println("Valor de eliminar en recibir recibirRondines_incidencias: " + eliminar);

                    if (eliminar >= 0){
                        try {
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i<array.length(); i++){
                                JSONObject object = array.getJSONObject(i);
                                ContentValues values = new ContentValues();
                                values.put("id", object.getInt("id"));
                                values.put("id_residencial", object.getInt("id_residencial"));
                                values.put("id_rondin", object.getInt("id_rondin"));
                                values.put("id_usuario", object.getInt("id_usuario"));
                                values.put("id_tipo", object.getInt("id_tipo"));
                                values.put("id_ubicacion", object.getInt("id_ubicacion"));
                                values.put("dia", object.getString("dia"));
                                values.put("hora", object.getString("hora"));
                                values.put("detalle", object.getString("detalle"));
                                values.put("accion", object.getString("accion"));
                                values.put("foto1", object.getString("foto1"));
                                values.put("foto2", object.getString("foto2"));
                                values.put("foto3", object.getString("foto3"));
                                values.put("club", object.getInt("club"));
                                values.put("estatus", object.getInt("estatus"));
                                values.put("sqliteEstatus", 0);

                                Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_RONDINESINCIDENCIAS, values);
                                if (uri == null) Log.e("error", "Error al registrar el registro: " + values.toString());

                            }
                            System.out.println("rondines_incidencias importados");
                        }catch (Exception ex){
                            System.out.println(ex.toString());
                        }
                    }
                }
            }, new Response.ErrorListener() {
                //Método para manejar errores de la petición
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error", ""+error);
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("id_residencial", Conf.getResid());
                    return params;
                }
            };
            MySingleton.getInstance(context).addToRequestQue(stringRequest);

    }


    public void recibirRondines_ubicaciones(Context context){

        Configuracion Conf = new Configuracion(context.getApplicationContext());

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerRondines_ubicaciones.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon(), new Response.Listener<String>() {
                //Se ejcuta cuando se obtiene una respuesta
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(String response) {

                    int eliminar = getContentResolver().delete(UrisContentProvider.URI_CONTENIDO_RONDINESUBICACIONES, null, null);

                    //System.out.println("Valor de eliminar en recibir recibirRondines_ubicaciones: " + eliminar);

                    if (eliminar >= 0){
                        try {
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i<array.length(); i++){
                                JSONObject object = array.getJSONObject(i);
                                ContentValues values = new ContentValues();
                                values.put("id", object.getInt("id"));
                                values.put("id_residencial", object.getInt("id_residencial"));
                                values.put("id_rondin", object.getInt("id_rondin"));
                                values.put("hora", object.getString("hora"));
                                values.put("id_ubicacion", object.getInt("id_ubicacion"));
                                values.put("id_usuario", object.getInt("id_usuario"));
                                values.put("club", object.getInt("club"));
                                values.put("estatus", object.getInt("estatus"));
                                values.put("sqliteEstatus", 0);

                                Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_RONDINESUBICACIONES, values);
                                if (uri == null) Log.e("error", "Error al registrar el registro: " + values.toString());

                            }
                            System.out.println("rondines_ubicaciones importados");
                        }catch (Exception ex){
                            System.out.println(ex.toString());
                        }
                    }
                }
            }, new Response.ErrorListener() {
                //Método para manejar errores de la petición
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error", ""+error);
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("id_residencial", Conf.getResid());
                    return params;
                }
            };
            MySingleton.getInstance(context).addToRequestQue(stringRequest);

    }


    public void recibirRondines_ubicaciones_qr(Context context){

        Configuracion Conf = new Configuracion(context.getApplicationContext());

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerRondines_ubicaciones_qr.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon(), new Response.Listener<String>() {
                //Se ejcuta cuando se obtiene una respuesta
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(String response) {

                    int eliminar = getContentResolver().delete(UrisContentProvider.URI_CONTENIDO_RONDINESUBICACIONESQR, null, null);

                    //System.out.println("Valor de eliminar en recibir recibirRondines_ubicaciones_qr: " + eliminar);

                    if (eliminar >= 0){
                        try {
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i<array.length(); i++){
                                JSONObject object = array.getJSONObject(i);
                                ContentValues values = new ContentValues();
                                values.put("id", object.getInt("id"));
                                values.put("id_residencial", object.getInt("id_residencial"));
                                values.put("id_rondin", object.getInt("id_rondin"));
                                values.put("hora", object.getString("hora"));
                                values.put("id_ubicacion", object.getInt("id_ubicacion"));
                                values.put("id_usuario", object.getInt("id_usuario"));
                                values.put("club", object.getInt("club"));
                                values.put("estatus", object.getInt("estatus"));
                                values.put("sqliteEstatus", 0);

                                Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_RONDINESUBICACIONESQR, values);
                                if (uri == null) Log.e("error", "Error al registrar el registro: " + values.toString());

                            }
                            System.out.println("rondines_ubicaciones_qr importados");
                        }catch (Exception ex){
                            System.out.println(ex.toString());
                        }
                    }
                }
            }, new Response.ErrorListener() {
                //Método para manejar errores de la petición
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error", ""+error);
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("id_residencial", Conf.getResid());
                    return params;
                }
            };
            MySingleton.getInstance(context).addToRequestQue(stringRequest);

    }

    public void recibirSesion_caseta(Context context){

        Configuracion Conf = new Configuracion(context.getApplicationContext());

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerSesion_caseta.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon(), new Response.Listener<String>() {
                //Se ejcuta cuando se obtiene una respuesta
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(String response) {

                    int eliminar = getContentResolver().delete(UrisContentProvider.URI_CONTENIDO_SESIONCASETA, null, null);

                    //System.out.println("Valor de eliminar en recibir recibirSesion_caseta: " + eliminar);

                    if (eliminar >= 0 ){
                        try {
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i<array.length(); i++){
                                JSONObject object = array.getJSONObject(i);
                                ContentValues values = new ContentValues();
                                values.put("id", object.getInt("id"));
                                values.put("id_residencial", object.getInt("id_residencial"));
                                values.put("nombre_completo", object.getString("nombre_completo"));
                                values.put("usuario", object.getString("usuario"));
                                values.put("contrasenia", object.getString("contrasenia"));
                                values.put("correo_electronico", object.getString("correo_electronico"));
                                values.put("caseta", object.getInt("caseta"));
                                values.put("sesion", object.getInt("sesion"));
                                values.put("hora_inicio", object.getString("hora_inicio"));
                                values.put("hora_fin", object.getString("hora_fin"));
                                values.put("comentarios", object.getString("comentarios"));
                                values.put("token", object.getString("token"));
                                values.put("club", object.getInt("club"));
                                values.put("estatus", object.getInt("estatus"));
                                values.put("sqliteEstatus", 0);

                                Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_SESIONCASETA, values);
                                if (uri == null) Log.e("error", "Error al registrar el registro: " + values.toString());

                            }
                            System.out.println("sesion_caseta importados");
                        }catch (Exception ex){
                            System.out.println(ex.toString());
                        }
                    }
                }
            }, new Response.ErrorListener() {
                //Método para manejar errores de la petición
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error", ""+error);
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("id_residencial", Conf.getResid());
                    return params;
                }
            };
            MySingleton.getInstance(context).addToRequestQue(stringRequest);

    }


    public void recibirUbicaciones(Context context){

        Configuracion Conf = new Configuracion(context.getApplicationContext());

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerUbicaciones.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon(), new Response.Listener<String>() {
                //Se ejcuta cuando se obtiene una respuesta
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(String response) {

                    int eliminar = getContentResolver().delete(UrisContentProvider.URI_CONTENIDO_UBICACIONES, null, null);

                    //System.out.println("Valor de eliminar en recibir recibirUbicaciones: " + eliminar);

                    if (eliminar >= 0){
                        try {
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i<array.length(); i++){
                                JSONObject object = array.getJSONObject(i);
                                ContentValues values = new ContentValues();
                                values.put("id", object.getInt("id"));
                                values.put("id_residencial", object.getInt("id_residencial"));
                                values.put("nombre", object.getString("nombre"));
                                values.put("longitud", object.getString("longitud"));
                                values.put("latitud", object.getString("latitud"));
                                values.put("club", object.getInt("club"));
                                values.put("estatus", object.getInt("estatus"));
                                values.put("sqliteEstatus", 0);

                                Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_UBICACIONES, values);
                                if (uri == null) Log.e("error", "Error al registrar el registro: " + values.toString());

                            }
                            System.out.println("ubicaciones importados");
                        }catch (Exception ex){
                            System.out.println(ex.toString());
                        }
                    }
                }
            }, new Response.ErrorListener() {
                //Método para manejar errores de la petición
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error", ""+error);
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("id_residencial", Conf.getResid());
                    return params;
                }
            };
            MySingleton.getInstance(context).addToRequestQue(stringRequest);

    }


    public void recibirLugar(Context context){

        Configuracion Conf = new Configuracion(context.getApplicationContext());

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerLugar.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon(), new Response.Listener<String>() {
                //Se ejcuta cuando se obtiene una respuesta
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(String response) {

                    int eliminar = getContentResolver().delete(UrisContentProvider.URI_CONTENIDO_LUGAR, null, null);

                    //System.out.println("Valor de eliminar en recibir recibirUbicaciones_qr: " + eliminar);

                    if (eliminar >= 0){
                        try {
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i<array.length(); i++){
                                JSONObject object = array.getJSONObject(i);
                                ContentValues values = new ContentValues();
                                values.put("id", object.getInt("id"));
                                values.put("id_residencial", object.getInt("id_residencial"));
                                values.put("nombre", object.getString("nombre"));
                                values.put("estado", object.getString("estado"));
                                values.put("municipio", object.getString("municipio"));
                                values.put("colonia", object.getString("colonia"));
                                values.put("calle", object.getString("calle"));
                                values.put("numero", object.getInt("numero"));
                                values.put("codigo_postal", object.getString("codigo_postal"));
                                values.put("descripcion", object.getString("descripcion"));
                                values.put("estatus", object.getInt("estatus"));
                                values.put("sqliteEstatus", 0);

                                Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_LUGAR, values);
                                if (uri == null) Log.e("error", "Error al registrar el registro: " + values.toString());
                            }
                            System.out.println("lugar importados");
                        }catch (Exception ex){
                            System.out.println(ex.toString());
                        }
                    }

                }
            }, new Response.ErrorListener() {
                //Método para manejar errores de la petición
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("error", ""+error);
                }
            }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("id_residencial", Conf.getResid());
                    return params;
                }
            };
            MySingleton.getInstance(context).addToRequestQue(stringRequest);

    }

    public void recibirUbicaciones_qr(Context context){

        Configuracion Conf = new Configuracion(context.getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerUbicaciones_qr.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon(), new Response.Listener<String>() {
            //Se ejcuta cuando se obtiene una respuesta
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                int eliminar = getContentResolver().delete(UrisContentProvider.URI_CONTENIDO_UBICACIONESQR, null, null);

                //System.out.println("Valor de eliminar en recibir recibirUbicaciones_qr: " + eliminar);

                if (eliminar >= 0){
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i<array.length(); i++){
                            JSONObject object = array.getJSONObject(i);
                            ContentValues values = new ContentValues();
                            values.put("id", object.getInt("id"));
                            values.put("id_residencial", object.getInt("id_residencial"));
                            values.put("nombre", object.getString("nombre"));
                            values.put("qr", object.getString("qr"));
                            values.put("club", object.getInt("club"));
                            values.put("estatus", object.getInt("estatus"));
                            values.put("sqliteEstatus", 0);

                            Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_UBICACIONESQR, values);
                            if (uri == null) Log.e("error", "Error al registrar el registro: " + values.toString());
                        }
                        System.out.println("ubicaciones_qr importados");
                    }catch (Exception ex){
                        System.out.println(ex.toString());
                    }
                }

            }
        }, new Response.ErrorListener() {
            //Método para manejar errores de la petición
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", ""+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_residencial", Conf.getResid());
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQue(stringRequest);

    }


    public void recibirUsuario(Context context){

        Configuracion Conf = new Configuracion(context.getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerUsuario.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon(), new Response.Listener<String>() {
            //Se ejcuta cuando se obtiene una respuesta
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                int eliminar = getContentResolver().delete(UrisContentProvider.URI_CONTENIDO_USUARIO, null, null);

                //System.out.println("Valor de eliminar en recibir recibirUbicaciones_qr: " + eliminar);

                if (eliminar >= 0){
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i<array.length(); i++){
                            JSONObject object = array.getJSONObject(i);
                            ContentValues values = new ContentValues();
                            values.put("id", object.getInt("id"));
                            values.put("id_residencial", object.getInt("id_residencial"));
                            values.put("tipo_usuario", object.getInt("tipo_usuario"));
                            values.put("nombre", object.getString("nombre"));
                            values.put("a_paterno", object.getString("a_paterno"));
                            values.put("a_materno", object.getString("a_materno"));
                            values.put("telefono", object.getString("telefono"));
                            values.put("correo_electronico", object.getString("correo_electronico"));
                            values.put("usuario", object.getString("usuario"));
                            values.put("contrasenia", object.getString("contrasenia"));
                            values.put("foto", object.getString("foto"));
                            values.put("miembro_club", object.getInt("miembro_club"));
                            values.put("fecha_registro", object.getString("fecha_registro"));
                            values.put("notificacion", object.getInt("notificacion"));
                            values.put("token", object.getString("token"));
                            values.put("fecha_nacimiento", object.getString("fecha_nacimiento"));
                            values.put("usu_master", object.getInt("usu_master"));
                            values.put("estatus", object.getInt("estatus"));
                            values.put("sqliteEstatus", 0);

                            Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_USUARIO, values);
                            if (uri == null) Log.e("error", "Error al registrar el registro: " + values.toString());
                        }
                        System.out.println("usuario importados");
                    }catch (Exception ex){
                        System.out.println(ex.toString());
                    }
                }

            }
        }, new Response.ErrorListener() {
            //Método para manejar errores de la petición
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", ""+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_residencial", Conf.getResid());
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQue(stringRequest);

    }


    public void recibirDtl_lugar_usuario(Context context){

        Configuracion Conf = new Configuracion(context.getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerDtl_lugar_usuario.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon(), new Response.Listener<String>() {
            //Se ejcuta cuando se obtiene una respuesta
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                int eliminar = getContentResolver().delete(UrisContentProvider.URI_CONTENIDO_DTL_LUGAR_USUARIO, null, null);

                //System.out.println("Valor de eliminar en recibir recibirUbicaciones_qr: " + eliminar);

                if (eliminar >= 0){
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i<array.length(); i++){
                            JSONObject object = array.getJSONObject(i);
                            ContentValues values = new ContentValues();
                            values.put("id", object.getInt("id"));
                            values.put("id_residencial", object.getInt("id_residencial"));
                            values.put("id_lugar", object.getInt("id_lugar"));
                            values.put("id_usuario", object.getString("id_usuario"));
                            values.put("estatus", object.getString("estatus"));
                            values.put("sqliteEstatus", 0);

                            Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_DTL_LUGAR_USUARIO, values);
                            if (uri == null) Log.e("error", "Error al registrar el registro: " + values.toString());
                        }
                        System.out.println("dtl_lugar_usuario importados");
                    }catch (Exception ex){
                        System.out.println(ex.toString());
                    }
                }

            }
        }, new Response.ErrorListener() {
            //Método para manejar errores de la petición
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", ""+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_residencial", Conf.getResid());
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQue(stringRequest);

    }

    public void recibirCorrespondencia(Context context){

        Configuracion Conf = new Configuracion(context.getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerCorrespondencia.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon(), new Response.Listener<String>() {
            //Se ejcuta cuando se obtiene una respuesta
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                int eliminar = getContentResolver().delete(UrisContentProvider.URI_CONTENIDO_CORRESPONDENCIA, null, null);

                //System.out.println("Valor de eliminar en recibir recibirUbicaciones_qr: " + eliminar);

                if (eliminar >= 0){
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i<array.length(); i++){
                            JSONObject object = array.getJSONObject(i);
                            ContentValues values = new ContentValues();
                            values.put("id", object.getInt("id"));
                            values.put("id_residencial", object.getInt("id_residencial"));
                            values.put("id_usuario", object.getInt("id_usuario"));
                            values.put("id_tipo_paquete", object.getInt("id_tipo_paquete"));
                            values.put("id_tipo_envio", object.getInt("id_tipo_envio"));
                            values.put("id_guardia", object.getInt("id_guardia"));
                            values.put("comentarios", object.getString("comentarios"));
                            values.put("foto_recep", object.getString("foto_recep"));
                            values.put("foto", object.getString("foto"));
                            values.put("fecha_registro", object.getString("fecha_registro"));
                            values.put("club", object.getInt("club"));
                            values.put("fecha_entrega", object.getString("fecha_entrega"));
                            values.put("estatus", object.getInt("estatus"));
                            values.put("id_offline", object.getString("id_offline"));
                            values.put("sqliteEstatus", 0);

                            Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_CORRESPONDENCIA, values);
                            if (uri == null) Log.e("error", "Error al registrar el registro: " + values.toString());
                        }
                        System.out.println("correspondencia importados");
                    }catch (Exception ex){
                        System.out.println(ex.toString());
                    }
                }

            }
        }, new Response.ErrorListener() {
            //Método para manejar errores de la petición
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", ""+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_residencial", Conf.getResid());
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQue(stringRequest);

    }


    //Método para saber si es que el servicio ya se esta ejecutando
    public boolean servicioFotos(){
        //Obtiene los servicios que se estan ejecutando
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        //Se recorren todos los servicios obtnidos para saber si el servicio creado ya se esta ejecutando
        for(ActivityManager.RunningServiceInfo service: activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if(subirFotos.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public class Correspondencias_actualizadas_sync extends AsyncTask<Void,Void,Void>{

        Cursor registros;

        public Correspondencias_actualizadas_sync(Cursor cursor) {
            registros = cursor;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void unused) {
            registros.close();
            recibirCorrespondencia(getApplicationContext());
            super.onPostExecute(unused);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (registros.moveToFirst()){
                do{

                    String datosinsertar = "";

                    datosinsertar += registros.getString(0) + "sIgCaM"
                            + registros.getString(1) + "sIgCaM"
                            + registros.getString(8) + "sIgCaM"
                            + registros.getString(11) + "sIgCaM"
                            + registros.getString(16) + "sIgCaM"
                            + registros.getString(17) + "sIgCaM" + "sIgObJ";

                    insertarCorrespondenciasActualizadas(datosinsertar);

                }while (registros.moveToNext());
            }
            return null;
        }
    }


    public class Correspondencias_insertadas_sync extends AsyncTask<Void,Void,Void>{

        Cursor registros;

        public Correspondencias_insertadas_sync(Cursor cursor) {
            registros = cursor;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(Void unused) {
            registros.close();
            System.out.println("Se registraron todas las correspondencias");
            enviarCorrespondenciasActualizadas(getApplicationContext());
            super.onPostExecute(unused);

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Void doInBackground(Void... voids) {
            if (registros.moveToFirst()){
                do{

                    String datosinsertar = "";

                    datosinsertar += registros.getString(1) + "sIgCaM"
                            + registros.getString(2) + "sIgCaM"
                            + registros.getString(3) + "sIgCaM"
                            + registros.getString(4) + "sIgCaM"
                            + registros.getString(5) + "sIgCaM"
                            + registros.getString(6) + "sIgCaM"
                            + registros.getString(7) + "sIgCaM"
                            + registros.getString(8) + "sIgCaM"
                            + registros.getString(9) + "sIgCaM"
                            + registros.getString(10) + "sIgCaM"
                            + registros.getString(11) + "sIgCaM"
                            + registros.getString(12) + "sIgCaM"
                            + registros.getString(13) + "sIgCaM"
                            + registros.getString(14) + "sIgCaM"
                            + registros.getString(15) + "sIgCaM"
                            + registros.getString(16) + "sIgCaM"
                            + registros.getString(17) + "sIgCaM"
                            + registros.getString(18) + "sIgCaM" + "sIgObJ";

                    insertarCorrespondencia(datosinsertar);

                }while (registros.moveToNext());
            }
            return null;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void insertarCorrespondencia(String datos){

        System.out.println("Enviar registro Correspondencia");

        try{

            final String urlInsertCorrespondencia = Global_info.getURL()+"insertarCorrespondencias.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();


                String finalDatosinsertarIncidencias = datos;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, urlInsertCorrespondencia, new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {

                        System.out.println("RESPUESTA DE ENVIAR CORRESPONDENCIA: "+response);

                        if (response.equals("1")){

                            System.out.println("-------------REGISTRO INSERTADO CORRECTAMENTE-------------");

                        }else if (response.equals("0")){
                            Log.e("error", "Error al enviar Correspondencias");
                        }

                    }
                }, new Response.ErrorListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("datos", finalDatosinsertarIncidencias);
                        System.out.println("Envia esto en correspondencia " + finalDatosinsertarIncidencias);
                        return params;
                    }
                };

                MySingleton.getInstance(getApplicationContext()).addToRequestQue(stringRequest);



        }catch (Exception ex){
            Log.e("error1", ex.toString());
        }

    }


    public void insertarCorrespondenciasActualizadas(String datos){

        System.out.println("Insertar Correspondencias Actualizadas");

        try{

            final String urlActualizarCorrespondencia = Global_info.getURL()+"actualizarCorrespondencias.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();

                String finalDatosinsertarIncidencias = datos;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, urlActualizarCorrespondencia, new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onResponse(String response) {

                        System.out.println("RESPUESTA DE ENVIAR CORRESPONDENCIA ACTUALIZADAS: "+response);

                        if (response.equals("1")){

                            System.out.println("-------------REGISTRO ACTUALIZADO CORRECTAMENTE-------------");

                        }else if (response.equals("0")){
                            Log.e("error", "Error al enviar actualizar correspondencia");
                        }

                    }
                }, new Response.ErrorListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("datos", finalDatosinsertarIncidencias);
                        System.out.println("Envia esto en correspondencia actualizadas " + finalDatosinsertarIncidencias);
                        return params;
                    }
                };

                MySingleton.getInstance(getApplicationContext()).addToRequestQue(stringRequest);


        }catch (Exception ex){
            Log.e("error", ex.toString());
        }

    }


    public void recibirDtl_entradas_salidas(Context context){

        Configuracion Conf = new Configuracion(context.getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerDtl_entradas_salidas.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon(), new Response.Listener<String>() {
            //Se ejcuta cuando se obtiene una respuesta
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                int eliminar = getContentResolver().delete(UrisContentProvider.URI_CONTENIDO_DTL_ENTRADAS_SALIDAS, null, null);

                //System.out.println("Valor de eliminar en recibir recibirUbicaciones_qr: " + eliminar);

                if (eliminar >= 0){
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i<array.length(); i++){
                            JSONObject object = array.getJSONObject(i);
                            ContentValues values = new ContentValues();
                            values.put("id", object.getInt("id"));
                            values.put("id_residencial", object.getInt("id_residencial"));
                            values.put("id_visita", object.getInt("id_visita"));
                            values.put("entrada_real", object.getString("entrada_real"));
                            values.put("salida_real", object.getString("salida_real"));
                            values.put("guardia_de_entrada", object.getInt("guardia_de_entrada"));
                            values.put("guardia_de_salida", object.getInt("guardia_de_salida"));
                            values.put("cajon", object.getString("cajon"));
                            values.put("personas", object.getInt("personas"));
                            values.put("placas", object.getString("placas"));
                            values.put("descripcion_transporte", object.getString("descripcion_transporte"));
                            values.put("foto1", object.getString("foto1"));
                            values.put("foto2", object.getString("foto2"));
                            values.put("foto3", object.getString("foto3"));
                            values.put("comentarios_salida_tardia", object.getString("comentarios_salida_tardia"));
                            values.put("estatus", object.getInt("estatus"));
                            values.put("sqliteEstatus", 0);

                            Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_DTL_ENTRADAS_SALIDAS, values);
                            if (uri == null) Log.e("error", "Error al registrar el dtl entradas salidas: " + values.toString());
                        }
                        System.out.println("dtl entradas salidas importados");
                    }catch (Exception ex){
                        System.out.println(ex.toString());
                    }
                }

            }
        }, new Response.ErrorListener() {
            //Método para manejar errores de la petición
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", ""+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_residencial", Conf.getResid());
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQue(stringRequest);

    }

    public void recibirVisita(Context context){

        Configuracion Conf = new Configuracion(context.getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerVisita.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon(), new Response.Listener<String>() {
            //Se ejcuta cuando se obtiene una respuesta
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                int eliminar = getContentResolver().delete(UrisContentProvider.URI_CONTENIDO_VISITA, null, null);

                //System.out.println("Valor de eliminar en recibir recibirUbicaciones_qr: " + eliminar);

                if (eliminar >= 0){
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i<array.length(); i++){
                            JSONObject object = array.getJSONObject(i);
                            ContentValues values = new ContentValues();
                            values.put("id", object.getInt("id"));
                            values.put("id_residencial", object.getInt("id_residencial"));
                            values.put("id_usuario", object.getInt("id_usuario"));
                            values.put("id_tipo_visita", object.getInt("id_tipo_visita"));
                            values.put("id_tipo", object.getInt("id_tipo"));
                            values.put("ilimitada", object.getInt("ilimitada"));
                            values.put("evento", object.getString("evento"));
                            values.put("nombre_visita", object.getString("nombre_visita"));
                            values.put("correo_electronico", object.getString("correo_electronico"));
                            values.put("comentarios", object.getString("comentarios"));
                            values.put("fecha_entrada", object.getString("fecha_entrada"));
                            values.put("fecha_salida", object.getString("fecha_salida"));
                            values.put("codigo_qr", object.getString("codigo_qr"));
                            values.put("fecha_registro", object.getString("fecha_registro"));
                            values.put("club", object.getInt("club"));
                            values.put("estatus", object.getInt("estatus"));
                            values.put("sqliteEstatus", 0);

                            Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_VISITA, values);
                            if (uri == null) Log.e("error", "Error al registrar el visita: " + values.toString());
                        }
                        System.out.println("visita importados");
                    }catch (Exception ex){
                        System.out.println(ex.toString());
                    }
                }

            }
        }, new Response.ErrorListener() {
            //Método para manejar errores de la petición
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", ""+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_residencial", Conf.getResid());
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQue(stringRequest);

    }


    public void recibirAuto(Context context){

        Configuracion Conf = new Configuracion(context.getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerAuto.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon(), new Response.Listener<String>() {
            //Se ejcuta cuando se obtiene una respuesta
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                int eliminar = getContentResolver().delete(UrisContentProvider.URI_CONTENIDO_AUTO, null, null);

                //System.out.println("Valor de eliminar en recibir recibirUbicaciones_qr: " + eliminar);

                if (eliminar >= 0){
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i<array.length(); i++){
                            JSONObject object = array.getJSONObject(i);
                            ContentValues values = new ContentValues();
                            values.put("id", object.getInt("id"));
                            values.put("id_residencial", object.getInt("id_residencial"));
                            values.put("id_usuario", object.getInt("id_usuario"));
                            values.put("marca", object.getString("marca"));
                            values.put("placas", object.getString("placas"));
                            values.put("modelo", object.getString("modelo"));
                            values.put("color", object.getString("color"));
                            values.put("tarjeta", object.getString("tarjeta"));
                            values.put("fotografia", object.getString("fotografia"));
                            values.put("fecha_registro", object.getString("fecha_registro"));
                            values.put("detalle", object.getString("detalle"));
                            values.put("qr", object.getString("qr"));
                            values.put("estatus", object.getInt("estatus"));
                            values.put("sqliteEstatus", 0);

                            Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_AUTO, values);
                            if (uri == null) Log.e("error", "Error al registrar el auto: " + values.toString());
                        }
                        System.out.println("auto importados");
                    }catch (Exception ex){
                        System.out.println(ex.toString());
                    }
                }

            }
        }, new Response.ErrorListener() {
            //Método para manejar errores de la petición
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", ""+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_residencial", Conf.getResid());
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQue(stringRequest);

    }

    public void recibirCajones(Context context){

        Configuracion Conf = new Configuracion(context.getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerCajones.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon(), new Response.Listener<String>() {
            //Se ejcuta cuando se obtiene una respuesta
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                int eliminar = getContentResolver().delete(UrisContentProvider.URI_CONTENIDO_CAJONES, null, null);

                //System.out.println("Valor de eliminar en recibir recibirUbicaciones_qr: " + eliminar);

                if (eliminar >= 0){
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i<array.length(); i++){
                            JSONObject object = array.getJSONObject(i);
                            ContentValues values = new ContentValues();
                            values.put("id", object.getInt("id"));
                            values.put("id_residencial", object.getInt("id_residencial"));
                            values.put("nombre", object.getString("nombre"));
                            values.put("descripcion", object.getString("descripcion"));
                            values.put("usado", object.getInt("usado"));
                            values.put("club", object.getInt("club"));
                            values.put("estatus", object.getInt("estatus"));
                            values.put("sqliteEstatus", 0);

                            Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_CAJONES, values);
                            if (uri == null) Log.e("error", "Error al registrar el cajon: " + values.toString());
                        }
                        System.out.println("cajones importados");
                    }catch (Exception ex){
                        System.out.println(ex.toString());
                    }
                }

            }
        }, new Response.ErrorListener() {
            //Método para manejar errores de la petición
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", ""+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_residencial", Conf.getResid());
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQue(stringRequest);

    }


    public void recibirDtl_entradas_salidas_autos(Context context){

        Configuracion Conf = new Configuracion(context.getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerDtl_entradas_salidas_autos.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon(), new Response.Listener<String>() {
            //Se ejcuta cuando se obtiene una respuesta
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                int eliminar = getContentResolver().delete(UrisContentProvider.URI_CONTENIDO_DTL_ENTRADAS_SALIDAS_AUTOS, null, null);

                //System.out.println("Valor de eliminar en recibir recibirUbicaciones_qr: " + eliminar);

                if (eliminar >= 0){
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i<array.length(); i++){
                            JSONObject object = array.getJSONObject(i);
                            ContentValues values = new ContentValues();
                            values.put("id", object.getInt("id"));
                            values.put("id_residencial", object.getInt("id_residencial"));
                            values.put("id_usuario", object.getInt("id_usuario"));
                            values.put("id_auto", object.getInt("id_auto"));
                            values.put("entrada_real", object.getString("entrada_real"));
                            values.put("guardia_de_entrada", object.getInt("guardia_de_entrada"));
                            values.put("salida_real", object.getString("salida_real"));
                            values.put("guardia_de_salida", object.getInt("guardia_de_salida"));
                            values.put("foto1", object.getString("foto1"));
                            values.put("foto2", object.getString("foto2"));
                            values.put("foto3", object.getString("foto3"));
                            values.put("estatus", object.getInt("estatus"));
                            values.put("sqliteEstatus", 0);

                            Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_DTL_ENTRADAS_SALIDAS_AUTOS, values);
                            if (uri == null) Log.e("error", "Error al registrar dtl_entradas_salidas_autos: " + values.toString());
                        }
                        System.out.println("dtl_entradas_salidas_autos importados");
                    }catch (Exception ex){
                        System.out.println(ex.toString());
                    }
                }

            }
        }, new Response.ErrorListener() {
            //Método para manejar errores de la petición
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", ""+error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_residencial", Conf.getResid());
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQue(stringRequest);

    }


}
