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
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import mx.linkom.caseta_dm_offline.offline.Global_info;

public class testInternet extends Service {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        boolean internet;
                        Global_info infoG = new Global_info();

                        while (true) {
                            //Saber si el dispositivo esta conectado auna red
                            internet = isOnline(testInternet.this);
                            try {
                                if (internet){
                                    //Hacer ping a google para comprobar conexión a internet
                                    new testInternet.Ping(testInternet.this).execute();
                                }else {
                                    System.out.println("No esta conectado a una red");
                                    //infoG.setINTERNET_DISPOSITIVO(false);
                                    Global_info.setINTERNET("No");
                                }
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
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

        final String HOST = "8.8.8.8";
        boolean alcanzable;
        Context context;
        Global_info gInfo = new Global_info();

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

            final Database database = new Database(context);
            final SQLiteDatabase db = database.getWritableDatabase();

            try {

                InetAddress direccion = InetAddress.getByName(HOST);
                alcanzable = direccion.isReachable(2000);

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
                    String hora = "Ultima actualización: " + day + "/" + month + "/" +year+ ", hora: " + hour + ":" + minute;

                    if (tiempo+1 == 10){
                        System.out.println("Paso un minuto de conexión aqui actualizo bd");

                        //Sincronizar SQLite a MySQL
                        enviarIncidencias(context);


                        //Solo ejecutar si el servicio no se esta ejecutando
                        if (!servicioFotos()){
                            Cursor cursoFotos = null;

                            cursoFotos = db.rawQuery("SELECT titulo, direccionFirebase, rutaDispositivo FROM fotosOffline WHERE rutaDispositivo != '' ", null);


                            ArrayList<String> titulos = new ArrayList<String>();
                            ArrayList<String> direccionesFirebase = new ArrayList<String>();
                            ArrayList<String> rutasDispositivo = new ArrayList<String>();

                            if (cursoFotos.moveToFirst()){
                                do {
                                    titulos.add(cursoFotos.getString(0));
                                    direccionesFirebase.add(cursoFotos.getString(1));
                                    rutasDispositivo.add(cursoFotos.getString(2));

                                } while (cursoFotos.moveToNext());
                                cursoFotos.close();
                            }



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


                        db.execSQL("DELETE FROM app_caseta");
                        db.execSQL("DELETE FROM rondines");
                        db.execSQL("DELETE FROM rondines_qr");
                        db.execSQL("DELETE FROM rondines_dia");
                        db.execSQL("DELETE FROM rondines_dia_qr");
                        db.execSQL("DELETE FROM rondines_dtl");
                        db.execSQL("DELETE FROM rondines_dtl_qr");
                        db.execSQL("DELETE FROM rondines_incidencias");
                        db.execSQL("DELETE FROM rondines_ubicaciones");
                        db.execSQL("DELETE FROM rondines_ubicaciones_qr");
                        db.execSQL("DELETE FROM sesion_caseta");
                        db.execSQL("DELETE FROM ubicaciones");
                        db.execSQL("DELETE FROM ubicaciones_qr");

                        //Sincronizar MySQL a SQLite
                        recibirApp_caseta(context);
                        recibirRondines(context);
                        recibirRondines_qr(context);
                        recibirRondines_dia(context);
                        recibirRondines_dia_qr(context);
                        recibirRondines_dtl(context);
                        recibirRondines_dtl_qr(context);
                        recibirRondines_incidencias(context);
                        recibirRondines_ubicaciones(context);
                        recibirRondines_ubicaciones_qr(context);
                        recibirSesion_caseta(context);
                        recibirUbicaciones(context);
                        recibirUbicaciones_qr(context);

                        Global_info.setULTIMA_ACTUALIZACION(hora);
                        gInfo.setSEGUNDOS(0);


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
            }finally {
                db.close();
            }
            return null;
        }
    }


    /*public static void sendMySql(Context context, String data, String url){

        String finalDatosConsulta = data;
        System.out.println(finalDatosConsulta);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            //Se ejcuta cuando se obtiene una respuesta
            @Override
            public void onResponse(String response) {
                System.out.println("Respuesta: " + response);
                Toast.makeText(context, "Información enviada", Toast.LENGTH_SHORT).show();
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
                params.put("datos", finalDatosConsulta);
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQue(stringRequest);

    }*/

    public void enviarIncidencias(Context context){

        final Database database = new Database(context);
        final SQLiteDatabase db = database.getWritableDatabase();

        try{

            final String urlInsertIncidencias = Global_info.getURL()+"insertarIncidencias.php";

            Cursor insertIncidencias = null;

            insertIncidencias = db.rawQuery("SELECT * FROM incidencias WHERE sqliteEstatus = 1 ", null);

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

            String finalDatosinsertarIncidencias = datosinsertarIncidencias;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, urlInsertIncidencias, new Response.Listener<String>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onResponse(String response) {

                    //System.out.println("Response " + response);

                    if (response.equals("1")){
                        System.out.println("Incidencias enviadas correctamente");
                        final Database database = new Database(context);
                        final SQLiteDatabase db2 = database.getWritableDatabase();
                        try {
                            db2.execSQL("DELETE FROM incidencias");
                        }catch (Exception ex){
                            ex.toString();
                        }finally {
                            db2.close();
                        }

                        recibirIncidencias(context);
                    }else if (response.equals("0")){
                        System.out.println("Error al enviar incidencias");
                        CrearNotificacion("Error al sincronizar incidencias tipo 1", "Espere a la próxima actualización o reporte este problema al administrador");
                    }

                }
            }, new Response.ErrorListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onErrorResponse(VolleyError error) {
                    CrearNotificacion("Error al sincronizar incidencias tipo 2", "Espere a la próxima actualización o reporte este problema al administrador");
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


        }catch (Exception ex){

        }finally {
            db.close();
        }

    }

    public void recibirIncidencias(Context context){

        Configuracion Conf = new Configuracion(context.getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerIncidencias.php", new Response.Listener<String>() {
            //Se ejcuta cuando se obtiene una respuesta
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                final Database database = new Database(context);
                final SQLiteDatabase db = database.getWritableDatabase();

                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i<array.length(); i++){
                        JSONObject object = array.getJSONObject(i);
                        //database.saveAutos(object.getInt("id"),object.getString("marca"),object.getString("modelo"),object.getString("placas"),db);
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

                        db.insert("incidencias", null, values);

                        if (i == array.length()) db.close();
                    }
                    System.out.println("incidencias importadas");
                }catch (Exception ex){
                    System.out.println(ex.toString());
                    CrearNotificacion("Error al sincronizar incidencias tipo 3", "Espere a la próxima actualización o reporte este problema al administrador");
                }finally {
                    db.close();
                }
            }
        }, new Response.ErrorListener() {
            //Método para manejar errores de la petición
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", ""+error);
                CrearNotificacion("Error al sincronizar incidencias tipo 4", "Espere a la próxima actualización o reporte este problema al administrador");
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


    public static void recibirApp_caseta(Context context){
        Configuracion Conf = new Configuracion(context.getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerMenu.php", new Response.Listener<String>() {
            //Se ejcuta cuando se obtiene una respuesta
            @Override
            public void onResponse(String response) {

                final Database database = new Database(context);
                final SQLiteDatabase db = database.getWritableDatabase();

                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i<array.length(); i++){
                        JSONObject object = array.getJSONObject(i);
                        //database.saveAutos(object.getInt("id"),object.getString("marca"),object.getString("modelo"),object.getString("placas"),db);
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

                        db.insert("app_caseta", null, values);

                        if (i == array.length()) db.close();
                    }
                    System.out.println("app_caseta importadas");
                }catch (Exception ex){
                    System.out.println(ex.toString());
                }finally {
                    db.close();
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

    public void recibirRondines(Context context){

        Configuracion Conf = new Configuracion(context.getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerRondines.php", new Response.Listener<String>() {
            //Se ejcuta cuando se obtiene una respuesta
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                final Database database = new Database(context);
                final SQLiteDatabase db = database.getWritableDatabase();

                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i<array.length(); i++){
                        JSONObject object = array.getJSONObject(i);
                        //database.saveAutos(object.getInt("id"),object.getString("marca"),object.getString("modelo"),object.getString("placas"),db);
                        ContentValues values = new ContentValues();
                        values.put("id", object.getInt("id"));
                        values.put("id_residencial", object.getInt("id_residencial"));
                        values.put("nombre", object.getString("nombre"));
                        values.put("club", object.getInt("club"));
                        values.put("estatus", object.getInt("estatus"));
                        values.put("sqliteEstatus", 0);

                        db.insert("rondines", null, values);

                        if (i == array.length()) db.close();
                    }
                    System.out.println("rondines importados");
                }catch (Exception ex){
                    System.out.println(ex.toString());
                    CrearNotificacion("Error al sincronizar rondines tipo 3", "Espere a la próxima actualización o reporte este problema al administrador");
                }finally {
                    db.close();
                }
            }
        }, new Response.ErrorListener() {
            //Método para manejar errores de la petición
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", ""+error);
                CrearNotificacion("Error al sincronizar rondines tipo 4", "Espere a la próxima actualización o reporte este problema al administrador");
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

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerRondines_qr.php", new Response.Listener<String>() {
            //Se ejcuta cuando se obtiene una respuesta
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                final Database database = new Database(context);
                final SQLiteDatabase db = database.getWritableDatabase();

                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i<array.length(); i++){
                        JSONObject object = array.getJSONObject(i);
                        //database.saveAutos(object.getInt("id"),object.getString("marca"),object.getString("modelo"),object.getString("placas"),db);
                        ContentValues values = new ContentValues();
                        values.put("id", object.getInt("id"));
                        values.put("id_residencial", object.getInt("id_residencial"));
                        values.put("nombre", object.getString("nombre"));
                        values.put("club", object.getInt("club"));
                        values.put("estatus", object.getInt("estatus"));
                        values.put("sqliteEstatus", 0);

                        db.insert("rondines_qr", null, values);

                        if (i == array.length()) db.close();
                    }
                    System.out.println("rondines_qr importados");
                }catch (Exception ex){
                    System.out.println(ex.toString());
                    CrearNotificacion("Error al sincronizar rondines_qr tipo 3", "Espere a la próxima actualización o reporte este problema al administrador");
                }finally {
                    db.close();
                }
            }
        }, new Response.ErrorListener() {
            //Método para manejar errores de la petición
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", ""+error);
                CrearNotificacion("Error al sincronizar rondines_qr tipo 4", "Espere a la próxima actualización o reporte este problema al administrador");
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

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerRondines_dia.php", new Response.Listener<String>() {
            //Se ejcuta cuando se obtiene una respuesta
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                final Database database = new Database(context);
                final SQLiteDatabase db = database.getWritableDatabase();

                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i<array.length(); i++){
                        JSONObject object = array.getJSONObject(i);
                        //database.saveAutos(object.getInt("id"),object.getString("marca"),object.getString("modelo"),object.getString("placas"),db);
                        ContentValues values = new ContentValues();
                        values.put("id", object.getInt("id"));
                        values.put("id_residencial", object.getInt("id_residencial"));
                        values.put("id_rondin", object.getInt("id_rondin"));
                        values.put("dia", object.getString("dia"));
                        values.put("club", object.getInt("club"));
                        values.put("estatus", object.getInt("estatus"));
                        values.put("sqliteEstatus", 0);

                        db.insert("rondines_dia", null, values);

                        if (i == array.length()) db.close();
                    }
                    System.out.println("rondines_dia importados");
                }catch (Exception ex){
                    System.out.println(ex.toString());
                    CrearNotificacion("Error al sincronizar rondines_qr tipo 3", "Espere a la próxima actualización o reporte este problema al administrador");
                }finally {
                    db.close();
                }
            }
        }, new Response.ErrorListener() {
            //Método para manejar errores de la petición
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", ""+error);
                CrearNotificacion("Error al sincronizar rondines_qr tipo 4", "Espere a la próxima actualización o reporte este problema al administrador");
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

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerRondines_dia_qr.php", new Response.Listener<String>() {
            //Se ejcuta cuando se obtiene una respuesta
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                final Database database = new Database(context);
                final SQLiteDatabase db = database.getWritableDatabase();

                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i<array.length(); i++){
                        JSONObject object = array.getJSONObject(i);
                        //database.saveAutos(object.getInt("id"),object.getString("marca"),object.getString("modelo"),object.getString("placas"),db);
                        ContentValues values = new ContentValues();
                        values.put("id", object.getInt("id"));
                        values.put("id_residencial", object.getInt("id_residencial"));
                        values.put("id_rondin", object.getInt("id_rondin"));
                        values.put("dia", object.getString("dia"));
                        values.put("club", object.getInt("club"));
                        values.put("estatus", object.getInt("estatus"));
                        values.put("sqliteEstatus", 0);

                        db.insert("rondines_dia_qr", null, values);

                        if (i == array.length()) db.close();
                    }
                    System.out.println("rondines_dia_qr importados");
                }catch (Exception ex){
                    System.out.println(ex.toString());
                    CrearNotificacion("Error al sincronizar rondines_qr tipo 3", "Espere a la próxima actualización o reporte este problema al administrador");
                }finally {
                    db.close();
                }
            }
        }, new Response.ErrorListener() {
            //Método para manejar errores de la petición
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", ""+error);
                CrearNotificacion("Error al sincronizar rondines_qr tipo 4", "Espere a la próxima actualización o reporte este problema al administrador");
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

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerRondines_dtl.php", new Response.Listener<String>() {
            //Se ejcuta cuando se obtiene una respuesta
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                final Database database = new Database(context);
                final SQLiteDatabase db = database.getWritableDatabase();

                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i<array.length(); i++){
                        JSONObject object = array.getJSONObject(i);
                        //database.saveAutos(object.getInt("id"),object.getString("marca"),object.getString("modelo"),object.getString("placas"),db);
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

                        db.insert("rondines_dtl", null, values);

                        if (i == array.length()) db.close();
                    }
                    System.out.println("rondines_dtl importados");
                }catch (Exception ex){
                    System.out.println(ex.toString());
                }finally {
                    db.close();
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

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerRondines_dtl_qr.php", new Response.Listener<String>() {
            //Se ejcuta cuando se obtiene una respuesta
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                final Database database = new Database(context);
                final SQLiteDatabase db = database.getWritableDatabase();

                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i<array.length(); i++){
                        JSONObject object = array.getJSONObject(i);
                        //database.saveAutos(object.getInt("id"),object.getString("marca"),object.getString("modelo"),object.getString("placas"),db);
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
                        db.insert("rondines_dtl_qr", null, values);

                        if (i == array.length()) db.close();
                    }
                    System.out.println("rondines_dtl_qr importados");
                }catch (Exception ex){
                    System.out.println(ex.toString());
                }finally {
                    db.close();
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

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerRondines_incidencias.php", new Response.Listener<String>() {
            //Se ejcuta cuando se obtiene una respuesta
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                final Database database = new Database(context);
                final SQLiteDatabase db = database.getWritableDatabase();

                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i<array.length(); i++){
                        JSONObject object = array.getJSONObject(i);
                        //database.saveAutos(object.getInt("id"),object.getString("marca"),object.getString("modelo"),object.getString("placas"),db);
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
                        db.insert("rondines_incidencias", null, values);

                        if (i == array.length()) db.close();
                    }
                    System.out.println("rondines_incidencias importados");
                }catch (Exception ex){
                    System.out.println(ex.toString());
                }finally {
                    db.close();
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

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerRondines_ubicaciones.php", new Response.Listener<String>() {
            //Se ejcuta cuando se obtiene una respuesta
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                final Database database = new Database(context);
                final SQLiteDatabase db = database.getWritableDatabase();

                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i<array.length(); i++){
                        JSONObject object = array.getJSONObject(i);
                        //database.saveAutos(object.getInt("id"),object.getString("marca"),object.getString("modelo"),object.getString("placas"),db);
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
                        db.insert("rondines_ubicaciones", null, values);

                        if (i == array.length()) db.close();
                    }
                    System.out.println("rondines_ubicaciones importados");
                }catch (Exception ex){
                    System.out.println(ex.toString());
                }finally {
                    db.close();
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

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerRondines_ubicaciones_qr.php", new Response.Listener<String>() {
            //Se ejcuta cuando se obtiene una respuesta
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                final Database database = new Database(context);
                final SQLiteDatabase db = database.getWritableDatabase();

                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i<array.length(); i++){
                        JSONObject object = array.getJSONObject(i);
                        //database.saveAutos(object.getInt("id"),object.getString("marca"),object.getString("modelo"),object.getString("placas"),db);
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
                        db.insert("rondines_ubicaciones_qr", null, values);

                        if (i == array.length()) db.close();
                    }
                    System.out.println("rondines_ubicaciones_qr importados");
                }catch (Exception ex){
                    System.out.println(ex.toString());
                }finally {
                    db.close();
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

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerSesion_caseta.php", new Response.Listener<String>() {
            //Se ejcuta cuando se obtiene una respuesta
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                final Database database = new Database(context);
                final SQLiteDatabase db = database.getWritableDatabase();

                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i<array.length(); i++){
                        JSONObject object = array.getJSONObject(i);
                        //database.saveAutos(object.getInt("id"),object.getString("marca"),object.getString("modelo"),object.getString("placas"),db);
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
                        db.insert("sesion_caseta", null, values);

                        if (i == array.length()) db.close();
                    }
                    System.out.println("sesion_caseta importados");
                }catch (Exception ex){
                    System.out.println(ex.toString());
                }finally {
                    db.close();
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

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerUbicaciones.php", new Response.Listener<String>() {
            //Se ejcuta cuando se obtiene una respuesta
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                final Database database = new Database(context);
                final SQLiteDatabase db = database.getWritableDatabase();

                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i<array.length(); i++){
                        JSONObject object = array.getJSONObject(i);
                        //database.saveAutos(object.getInt("id"),object.getString("marca"),object.getString("modelo"),object.getString("placas"),db);
                        ContentValues values = new ContentValues();
                        values.put("id", object.getInt("id"));
                        values.put("id_residencial", object.getInt("id_residencial"));
                        values.put("nombre", object.getString("nombre"));
                        values.put("longitud", object.getString("longitud"));
                        values.put("latitud", object.getString("latitud"));
                        values.put("club", object.getInt("club"));
                        values.put("estatus", object.getInt("estatus"));
                        values.put("sqliteEstatus", 0);
                        db.insert("ubicaciones", null, values);

                        if (i == array.length()) db.close();
                    }
                    System.out.println("ubicaciones importados");
                }catch (Exception ex){
                    System.out.println(ex.toString());
                }finally {
                    db.close();
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

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Global_info.getURL()+"obtenerUbicaciones_qr.php", new Response.Listener<String>() {
            //Se ejcuta cuando se obtiene una respuesta
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {

                final Database database = new Database(context);
                final SQLiteDatabase db = database.getWritableDatabase();

                try {
                    JSONArray array = new JSONArray(response);
                    for (int i = 0; i<array.length(); i++){
                        JSONObject object = array.getJSONObject(i);
                        //database.saveAutos(object.getInt("id"),object.getString("marca"),object.getString("modelo"),object.getString("placas"),db);
                        ContentValues values = new ContentValues();
                        values.put("id", object.getInt("id"));
                        values.put("id_residencial", object.getInt("id_residencial"));
                        values.put("nombre", object.getString("nombre"));
                        values.put("qr", object.getString("qr"));
                        values.put("club", object.getInt("club"));
                        values.put("estatus", object.getInt("estatus"));
                        values.put("sqliteEstatus", 0);
                        db.insert("ubicaciones_qr", null, values);

                        if (i == array.length()) db.close();
                    }
                    System.out.println("ubicaciones_qr importados");
                }catch (Exception ex){
                    System.out.println(ex.toString());
                }finally {
                    db.close();
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


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void CrearNotificacion(String titulo, String contenido){

        //System.out.println("Crear notifi");
        final String CHANNELID = "Sincronizacion";
        NotificationChannel channel = new NotificationChannel(
                CHANNELID,
                CHANNELID,
                NotificationManager.IMPORTANCE_LOW
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNELID)
                .setSmallIcon(R.drawable.ic_info)
                .setContentTitle(titulo)
                .setContentText(contenido)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(111111, builder.build());
    }

}
