package mx.linkom.caseta_dm_offline.offline.Database;

import static solar.blaz.date.week.WeekDatePicker.TAG;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import mx.linkom.caseta_dm_offline.offline.Global_info;

public class ContentProvider extends android.content.ContentProvider {

    //Objeto UriMatcher para comprobar el content Uri
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Casos
    public static final int INCIDENCIAS = 100;
    public static final int FOTOS_OFFLINE = 200;
    public static final int APP_CASETA = 300;
    public static final int RONDINES = 400;
    public static final int RONDINES_QR = 500;
    public static final int RONDINES_DIA = 600;
    public static final int RONDINES_DIA_QR = 700;
    public static final int RONDINES_DTL = 800;
    public static final int RONDINES_DTL_QR = 900;
    public static final int RONDINES_INCIDENCIAS = 1000;
    public static final int RONDINES_UBICACIONES = 1100;
    public static final int RONDINES_UBICACIONES_QR = 1200;
    public static final int SESION_CASETA = 1300;
    public static final int UBICACIONES = 1400;
    public static final int UBICACIONES_QR = 1500;
    public static final int APP_CASETA_IMA = 1600;
    public static final int LUGAR = 1700;
    public static final int USUARIO = 1800;
    public static final int DTL_LUGAR_USUARIO = 1900;
    public static final int CORRESPONDENCIA = 2000;
    public static final int DTL_ENTRADAS_SALIDAS = 2100;
    public static final int VISITA = 2200;
    public static final int AUTO = 2300;
    public static final int CAJONES = 2400;
    public static final int DTL_ENTRADAS_SALIDAS_AUTOS = 2500;



    public static final String AUTORIDAD = "mx.linkom.caseta_dm_offline";

    //Static inicializer, se ejecuta la primera vez que algo es llamado desde la clase
    static {
        uriMatcher.addURI(AUTORIDAD, "app_caseta", APP_CASETA);
        uriMatcher.addURI(AUTORIDAD, "incidencias", INCIDENCIAS);
        uriMatcher.addURI(AUTORIDAD, "fotosOffline", FOTOS_OFFLINE);
        uriMatcher.addURI(AUTORIDAD, "app_caseta_ima", APP_CASETA_IMA);
        uriMatcher.addURI(AUTORIDAD, "rondines", RONDINES);
        uriMatcher.addURI(AUTORIDAD, "rondines_qr", RONDINES_QR);
        uriMatcher.addURI(AUTORIDAD, "rondines_dia", RONDINES_DIA);
        uriMatcher.addURI(AUTORIDAD, "rondines_dia_qr", RONDINES_DIA_QR);
        uriMatcher.addURI(AUTORIDAD, "rondines_dtl", RONDINES_DTL);
        uriMatcher.addURI(AUTORIDAD, "rondines_dtl_qr", RONDINES_DTL_QR);
        uriMatcher.addURI(AUTORIDAD, "rondines_incidencias", RONDINES_INCIDENCIAS);
        uriMatcher.addURI(AUTORIDAD, "rondines_ubicaciones", RONDINES_UBICACIONES);
        uriMatcher.addURI(AUTORIDAD, "rondines_ubicaciones_qr", RONDINES_UBICACIONES_QR);
        uriMatcher.addURI(AUTORIDAD, "sesion_caseta", SESION_CASETA);
        uriMatcher.addURI(AUTORIDAD, "ubicaciones", UBICACIONES);
        uriMatcher.addURI(AUTORIDAD, "ubicaciones_qr", UBICACIONES_QR);
        uriMatcher.addURI(AUTORIDAD, "lugar", LUGAR);
        uriMatcher.addURI(AUTORIDAD, "usuario", USUARIO);
        uriMatcher.addURI(AUTORIDAD, "dtl_lugar_usuario", DTL_LUGAR_USUARIO);
        uriMatcher.addURI(AUTORIDAD, "correspondencia", CORRESPONDENCIA);
        uriMatcher.addURI(AUTORIDAD, "dtl_entradas_salidas", DTL_ENTRADAS_SALIDAS);
        uriMatcher.addURI(AUTORIDAD, "visita", VISITA);
        uriMatcher.addURI(AUTORIDAD, "auto", AUTO);
        uriMatcher.addURI(AUTORIDAD, "cajones", CAJONES);
        uriMatcher.addURI(AUTORIDAD, "dtl_entradas_salidas_autos", DTL_ENTRADAS_SALIDAS_AUTOS);
    }

    //Inicializa el provider y el objetivo database Helper
    private Database database;
    private SQLiteDatabase bd;

    @Override
    public boolean onCreate() {
        database = new Database(getContext());
        bd = database.getWritableDatabase();
        return true;
    }

    //Realiza la solicitud para la Uri, Nececita projection, projection, selection, selection arguments, and sort order
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        //Log.d(TAG, "Query en " + uri);

        Cursor cursor = null;

        int match = uriMatcher.match(uri);
        switch (match){
            case APP_CASETA:
                cursor = bd.rawQuery("SELECT * FROM app_caseta" , null);
                break;
            case APP_CASETA_IMA:
                cursor = bd.rawQuery("SELECT * FROM app_caseta_ima" , null);
                break;
            case INCIDENCIAS:
                cursor = bd.rawQuery("SELECT * FROM incidencias WHERE sqliteEstatus = 1",null);
                break;
            case FOTOS_OFFLINE:
                cursor = bd.rawQuery("SELECT titulo, direccionFirebase, rutaDispositivo FROM fotosOffline WHERE rutaDispositivo != '' ",null);
                break;
            case RONDINES_UBICACIONES:
                String usuario = selectionArgs[0];
                String fecha = selectionArgs[1];
                String id_residencial = selectionArgs[2];
                String hora = selectionArgs[3];

                Log.e(TAG, "Usuario: "+usuario+" fecha: "+fecha+" id_res: "+id_residencial+" hora: " + hora);

                cursor = bd.rawQuery("SELECT ubi.id, ubi.hora, ubis.nombre, dia.id, dia.dia, rondin.id, rondin.nombre FROM rondines_ubicaciones as ubi, rondines_dia as dia, rondines as rondin, ubicaciones as ubis WHERE ubi.id_usuario="+"'"+usuario+"'"+" and ubi.id_rondin=dia.id_rondin and ubi.id_rondin=rondin.id and dia.dia="+"'"+fecha+"'"+" and ubi.id_residencial="+"'"+id_residencial+"'"+" and ubi.hora<="+"'"+hora+"'"+" and ubis.id=ubi.id_ubicacion and NOT EXISTS (SELECT * FROM rondines_dtl WHERE rondines_dtl.id_ubicaciones=ubi.id and rondines_dtl.id_dia=dia.id and rondines_dtl.id_rondin=rondin.id)", null);
                break;
            case RONDINES_DIA:
                String  id = selectionArgs[0];
                String usuario1 = selectionArgs[1];
                String dia = selectionArgs[2];
                String id_residencial1 = selectionArgs[3];
                String tiempo = selectionArgs[4];

                Log.e(TAG, "id: "+id+" Usuario: "+usuario1+" día: "+dia+" id_res: "+id_residencial1+" tiempo: " + tiempo);

                cursor = bd.rawQuery("SELECT ubi.id, ubi.hora, ubis.nombre, dia.id, dia.dia, rondin.id, rondin.nombre FROM rondines_ubicaciones as ubi, rondines_dia as dia, rondines as rondin, ubicaciones as ubis WHERE ubi.id="+"'"+id+"'"+" and ubi.id_usuario="+"'"+usuario1+"'"+" and ubi.id_rondin=dia.id_rondin and ubi.id_rondin=rondin.id and dia.dia="+"'"+dia+"'"+" and ubi.id_residencial="+"'"+id_residencial1+"'"+" and ubi.hora<="+"'"+tiempo+"'"+" and ubis.id=ubi.id_ubicacion and NOT EXISTS (SELECT * FROM rondines_dtl WHERE rondines_dtl.id_ubicaciones=ubi.id and rondines_dtl.id_dia=dia.id and rondines_dtl.id_rondin=rondin.id)", null);
                break;
            case UBICACIONES:
                String id_ub= selectionArgs[0];
                Log.e(TAG, "id: "+id_ub);

                cursor = bd.rawQuery("SELECT ubis.id, ubis.longitud, ubis.latitud FROM rondines_ubicaciones as ubi, ubicaciones as ubis WHERE  ubi.id="+"'"+id_ub+"'"+" AND ubi.id_ubicacion=ubis.id and ubis.estatus=1", null);
                break;
            case RONDINES_UBICACIONES_QR:
                String usuario_qr = selectionArgs[0];
                String dia_qr = selectionArgs[1];
                String id_residencial_qr = selectionArgs[2];
                String tiempo_qr = selectionArgs[3];
                Log.e(TAG, " Usuario: "+usuario_qr+" día: "+dia_qr+" id_res: "+id_residencial_qr+" tiempo: " + tiempo_qr);
                cursor = bd.rawQuery("SELECT ubi.id, ubi.hora, ubis.nombre, dia.id, dia.dia, rondin.id, rondin.nombre FROM rondines_ubicaciones_qr as ubi, rondines_dia_qr as dia, rondines_qr as rondin, ubicaciones_qr as ubis WHERE ubi.id_usuario="+"'"+usuario_qr+"'"+" and ubi.id_rondin=dia.id_rondin and ubi.id_rondin=rondin.id and dia.dia="+"'"+dia_qr+"'"+" and ubi.id_residencial="+"'"+id_residencial_qr+"'"+" and ubi.hora<="+"'"+tiempo_qr+"'"+" and ubis.id=ubi.id_ubicacion and NOT EXISTS (SELECT * FROM rondines_dtl_qr WHERE rondines_dtl_qr.id_ubicaciones=ubi.id and rondines_dtl_qr.id_dia=dia.id and rondines_dtl_qr.id_rondin=rondin.id)", null);
                break;
            case RONDINES_DIA_QR:
                String id_qr = selectionArgs[0];
                String usuario_qr2 = selectionArgs[1];
                String dia_qr2 = selectionArgs[2];
                String id_residencial_qr2 = selectionArgs[3];
                String tiempo_qr2 = selectionArgs[4];
                Log.e(TAG, "Id: "+id_qr+" Usuario: "+usuario_qr2+" día: "+dia_qr2+" id_res: "+id_residencial_qr2+" tiempo: " + tiempo_qr2);
                cursor =  bd.rawQuery("SELECT ubi.id, ubi.hora, ubis.nombre, dia.id, dia.dia, rondin.id, rondin.nombre, ubis.qr FROM rondines_ubicaciones_qr as ubi, rondines_dia_qr as dia, rondines_qr as rondin, ubicaciones_qr as ubis WHERE ubi.id="+"'"+id_qr+"'"+" and ubi.id_usuario="+"'"+usuario_qr2+"'"+" and ubi.id_rondin=dia.id_rondin and ubi.id_rondin=rondin.id and dia.dia="+"'"+dia_qr2+"'"+" and ubi.id_residencial="+"'"+id_residencial_qr2+"'"+" and ubi.hora<="+"'"+tiempo_qr2+"'"+" and ubis.id=ubi.id_ubicacion and NOT EXISTS (SELECT * FROM rondines_dtl_qr WHERE rondines_dtl_qr.id_ubicaciones=ubi.id and rondines_dtl_qr.id_dia=dia.id and rondines_dtl_qr.id_rondin=rondin.id)",null);
                break;
            case RONDINES_DTL:
                cursor = bd.rawQuery("SELECT * FROM rondines_dtl WHERE sqliteEstatus = 1",null);
                break;
            case RONDINES_DTL_QR:
                cursor = bd.rawQuery("SELECT * FROM rondines_dtl_qr WHERE sqliteEstatus = 1",null);
                break;
            case RONDINES_INCIDENCIAS:
                cursor = bd.rawQuery("SELECT * FROM rondines_incidencias WHERE sqliteEstatus = 1",null);
                break;
            case LUGAR:
                if (selection.equals("numeros")){
                    String calle = selectionArgs[0];
                    String id_res_lugar_numeros = selectionArgs[1];
                    cursor = bd.rawQuery("SELECT lugar.numero FROM lugar WHERE  lugar.calle="+"'"+calle+"'"+"  and id_residencial="+"'"+id_res_lugar_numeros+"'"+" and estatus=1",null);
                }else if (selection.equals("calles")){
                    String id_resi_lugar = selectionArgs[0];
                    cursor = bd.rawQuery("SELECT DISTINCT(calle) as nombre FROM lugar WHERE estatus=1 and id_residencial="+"'"+id_resi_lugar+"'"+" order by nombre asc",null);
                }else if (selection.equals("dtl_lugar_usuario")){
                    String id_resi_lugar = selectionArgs[0];
                    String id_usu = selectionArgs[1];
                    cursor = bd.rawQuery("SELECT lugar.nombre FROM lugar,dtl_lugar_usuario WHERE dtl_lugar_usuario.id_usuario="+"'"+id_usu+"'"+" and dtl_lugar_usuario.id_residencial="+"'"+id_resi_lugar+"'"+" and lugar.id=dtl_lugar_usuario.id_lugar and lugar.estatus=1 and dtl_lugar_usuario.estatus=1",null);
                }
                else if (selection.equals("verificaUP")){
                    String id_resi_lugar = selectionArgs[0];
                    String calle_usu = selectionArgs[1];
                    String numero_usu = selectionArgs[2];
                    Log.e("CONSULTA ", "SELECT usuario.id,usuario.nombre,usuario.a_paterno,usuario.a_materno,usuario.correo_electronico,usuario.token FROM lugar,dtl_lugar_usuario,usuario WHERE lugar.id = (SELECT id FROM lugar WHERE lugar.calle="+"'"+calle_usu+"'"+" and lugar.numero="+"'"+numero_usu+"'"+" and id_residencial="+"'"+id_resi_lugar+"'"+" and estatus=1) and lugar.id=dtl_lugar_usuario.id_lugar and usuario.id=dtl_lugar_usuario.id_usuario and usuario.id_residencial="+"'"+id_resi_lugar+"'"+"");
                    cursor = bd.rawQuery("SELECT usuario.id,usuario.nombre,usuario.a_paterno,usuario.a_materno,usuario.correo_electronico,usuario.token FROM lugar,dtl_lugar_usuario,usuario WHERE lugar.id = (SELECT id FROM lugar WHERE lugar.calle="+"'"+calle_usu+"'"+" and lugar.numero="+"'"+numero_usu+"'"+" and id_residencial="+"'"+id_resi_lugar+"'"+" and estatus=1) and lugar.id=dtl_lugar_usuario.id_lugar and usuario.id=dtl_lugar_usuario.id_usuario and usuario.id_residencial="+"'"+id_resi_lugar+"'"+"",null);
                }else if (selection.equals("verificaUP2")){
                    String id_resi_lugar = selectionArgs[0];
                    String calle_usu = selectionArgs[1];
                    String numero_usu = selectionArgs[2];
                    Log.e("CONSULTA ", "SELECT usuario.id,usuario.nombre,usuario.a_paterno,usuario.a_materno, usuario.correo_electronico,usuario.token  FROM lugar,dtl_lugar_usuario,usuario WHERE lugar.id = (SELECT id FROM lugar WHERE lugar.calle="+"'"+calle_usu+"'"+" and lugar.numero="+"'"+numero_usu+"'"+" and id_residencial="+"'"+id_resi_lugar+"'"+" and estatus=1) and lugar.id=dtl_lugar_usuario.id_lugar and usuario.id=dtl_lugar_usuario.id_usuario and usuario.id_residencial="+"'"+id_resi_lugar+"'"+" and usuario.tipo_usuario=2");
                    cursor = bd.rawQuery("SELECT usuario.id,usuario.nombre,usuario.a_paterno,usuario.a_materno, usuario.correo_electronico,usuario.token  FROM lugar,dtl_lugar_usuario,usuario WHERE lugar.id = (SELECT id FROM lugar WHERE lugar.calle="+"'"+calle_usu+"'"+" and lugar.numero="+"'"+numero_usu+"'"+" and id_residencial="+"'"+id_resi_lugar+"'"+" and estatus=1) and lugar.id=dtl_lugar_usuario.id_lugar and usuario.id=dtl_lugar_usuario.id_usuario and usuario.id_residencial="+"'"+id_resi_lugar+"'"+" and usuario.tipo_usuario=2",null);
                }


                break;
            case USUARIO:

                if (selection.equals("usuarios")){
                    String calle_usuario = selectionArgs[0];
                    String numero_usuario = selectionArgs[1];
                    String id_residencial_usuario = selectionArgs[2];

                    cursor = bd.rawQuery("SELECT usuario.id,usuario.nombre,usuario.a_paterno,usuario.a_materno,usuario.correo_electronico,usuario.token,usuario.notificacion  FROM usuario,lugar, dtl_lugar_usuario WHERE usuario.id_residencial="+"'"+id_residencial_usuario+"'"+" and lugar.numero="+"'"+numero_usuario+"'"+" and  lugar.calle="+"'"+calle_usuario+"'"+" and usuario.id=dtl_lugar_usuario.id_usuario and lugar.id=dtl_lugar_usuario.id_lugar and usuario.estatus=1",null);
                }else if (selection.equals("residente_o_inquilino")){
                    String calle_usuario = selectionArgs[0];
                    String numero_usuario = selectionArgs[1];
                    String id_residencial_usuario = selectionArgs[2];

                    cursor = bd.rawQuery("SELECT usuario.id,usuario.nombre,usuario.a_paterno,usuario.a_materno,usuario.correo_electronico,usuario.token,usuario.notificacion  FROM usuario,lugar, dtl_lugar_usuario WHERE usuario.id_residencial="+"'"+id_residencial_usuario+"'"+" and lugar.numero="+"'"+numero_usuario+"'"+" and lugar.calle="+"'"+calle_usuario+"'"+"  and usuario.id=dtl_lugar_usuario.id_usuario and lugar.id=dtl_lugar_usuario.id_lugar and usuario.estatus=1 and usuario.tipo_usuario=2",null);
                }else if (selection.equals("dts_accesso_autos")){

                    String id_usu = selectionArgs[0];
                    String id_residencial_usuario = selectionArgs[1];
                    cursor = bd.rawQuery("SELECT id,nombre,a_paterno,a_materno,telefono,token,correo_electronico FROM usuario WHERE id ="+"'"+id_usu+"'"+" and id_residencial="+"'"+id_residencial_usuario+"'"+" and estatus=1",null);
                }
                break;
            case CORRESPONDENCIA:
                if (selection.equals("Offline")){
                    String folio_corres = selectionArgs[0];
                    String id_res_corres = selectionArgs[1];
                    cursor = bd.rawQuery("SELECT * FROM correspondencia WHERE id_residencial="+"'"+id_res_corres+"'"+" and id_offline ="+"'"+folio_corres+"'"+"  and estatus=2 ORDER BY id DESC LIMIT 1",null);
                }else if (selection.equals("Online")){
                    String folio_corres = selectionArgs[0];
                    String id_res_corres = selectionArgs[1];
                    cursor = bd.rawQuery("SELECT * FROM correspondencia WHERE id_residencial="+"'"+id_res_corres+"'"+" and id ="+"'"+folio_corres+"'"+"  and estatus=2 ORDER BY id DESC LIMIT 1",null);
                }else if (selection.equals("insertados")){
                    cursor = bd.rawQuery("SELECT * FROM correspondencia WHERE sqliteEstatus = 1",null);
                }else if (selection.equals("actualizados")){
                    cursor = bd.rawQuery("SELECT * FROM correspondencia WHERE sqliteEstatus = 2",null);
                }
                break;
            case DTL_LUGAR_USUARIO:
                String id_dtl_lug = selectionArgs[0];
                String id_res_dtl_lug = selectionArgs[1];
                cursor = bd.rawQuery("SELECT lugar.nombre as persona,usuario.token FROM usuario,lugar, dtl_lugar_usuario WHERE usuario.id_residencial="+"'"+id_res_dtl_lug+"'"+" and usuario.id="+"'"+id_dtl_lug+"'"+" and usuario.id=dtl_lugar_usuario.id_usuario and lugar.id=dtl_lugar_usuario.id_lugar and usuario.estatus=1",null);
                break;
            case AUTO:
                String qr_auto = selectionArgs[0];
                String id_res_auto = selectionArgs[1];
                cursor = bd.rawQuery("SELECT * FROM auto WHERE id_residencial="+"'"+id_res_auto+"'"+" and qr="+"'"+qr_auto+"'"+" and estatus=1",null);
                break;
            case VISITA:
                if (selection.equals("vst1")){
                    String qr_visita = selectionArgs[0];
                    String id_res_visita = selectionArgs[1];
                    cursor = bd.rawQuery("SELECT * FROM visita WHERE codigo_qr = "+"'"+qr_visita+"'"+" and id_residencial = "+"'"+id_res_visita+"'"+" and estatus=1 LIMIT 1",null);
                }else if (selection.equals("vst_grupal1")){
                    String qr_visita = selectionArgs[0];
                    String id_res_visita = selectionArgs[1];
                    Log.e("vst_grupal1", "SELECT * FROM visita WHERE codigo_qr = "+"'"+qr_visita+"'"+" and id_residencial = "+"'"+id_res_visita+"'"+" and estatus=1 and NOT EXISTS (SELECT * FROM   dtl_entradas_salidas WHERE visita.id = dtl_entradas_salidas.id_visita) ORDER BY nombre_visita ASC");
                    cursor = bd.rawQuery("SELECT * FROM visita WHERE codigo_qr = "+"'"+qr_visita+"'"+" and id_residencial = "+"'"+id_res_visita+"'"+" and estatus=1 and NOT EXISTS (SELECT * FROM   dtl_entradas_salidas WHERE visita.id = dtl_entradas_salidas.id_visita) ORDER BY nombre_visita ASC",null);
                }else if (selection.equals("vst_gru_2")){
                    String id_visita = selectionArgs[0];
                    String id_res_visita = selectionArgs[1];
                    cursor = bd.rawQuery("SELECT * FROM visita WHERE id = "+"'"+id_visita+"'"+" and id_residencial =  "+"'"+id_res_visita+"'"+" and estatus=1 LIMIT 1",null);
                }else if (selection.equals("vst_reg_5")){
                    String id_visita = selectionArgs[0];
                    String id_res_visita = selectionArgs[1];
                    cursor = bd.rawQuery("SELECT * FROM visita WHERE id ="+"'"+id_visita+"'"+"  and id_residencial="+"'"+id_res_visita+"'"+" and  estatus=1",null);
                }else if (selection.equals("sincronizacion")){
                    cursor = bd.rawQuery("SELECT * FROM visita WHERE sqliteEstatus = 1", null);
                }else if (selection.equals("sincronizacionActualizados")){
                    cursor = bd.rawQuery("SELECT * FROM visita WHERE sqliteEstatus = 2", null);
                }
                break;
            case CAJONES:
                if (selection.equals("cajones")){
                    String id_resi_cajones = selectionArgs[0];
                    String id_cajones = selectionArgs[1];
                    cursor = bd.rawQuery("SELECT cajones.nombre FROM cajones WHERE  cajones.id_residencial="+"'"+id_resi_cajones+"'"+" and  cajones.usado=(SELECT id_lugar FROM dtl_lugar_usuario WHERE  dtl_lugar_usuario.id_usuario = "+"'"+id_cajones+"'"+" and dtl_lugar_usuario.id_residencial="+"'"+id_resi_cajones+"'"+" and dtl_lugar_usuario.estatus=1)  and cajones.estatus=1",null);
                }
                break;
            case DTL_ENTRADAS_SALIDAS:
                if (selection.equals("consulta1")){
                    String id_resi_entr = selectionArgs[0];
                    String placas_entr = selectionArgs[1];
                    cursor = bd.rawQuery("SELECT * FROM dtl_entradas_salidas WHERE placas ="+"'"+placas_entr+"'"+" and id_residencial="+"'"+id_resi_entr+"'"+" ORDER BY id DESC LIMIT 1",null);
                }else if (selection.equals("vst_php4")){
                    //falta la consulta
                    String id_resi_entr = selectionArgs[0];
                    String id_visita = selectionArgs[1];
                    Log.e("prueba", "SELECT estatus FROM dtl_entradas_salidas WHERE id_visita = "+"'"+id_visita+"'"+" and id_residencial="+"'"+id_resi_entr+"'"+" ORDER BY id DESC LIMIT 1");
                    cursor = bd.rawQuery("SELECT estatus FROM dtl_entradas_salidas WHERE id_visita = "+"'"+id_visita+"'"+" and id_residencial="+"'"+id_resi_entr+"'"+" ORDER BY id DESC LIMIT 1",null);
                }else if (selection.equals("vst_reg_8")){
                    String id_resi_entr = selectionArgs[0];
                    String id_visita = selectionArgs[1];
                    cursor = bd.rawQuery("SELECT * FROM dtl_entradas_salidas WHERE id_visita = "+"'"+id_visita+"'"+"  and id_residencial="+"'"+id_resi_entr+"'"+" ORDER BY id DESC LIMIT 1",null);
                }else if (selection.equals("sincronizacion")){
                    cursor = bd.rawQuery("SELECT * FROM dtl_entradas_salidas WHERE sqliteEstatus = 1",null);
                }
                break;
            case DTL_ENTRADAS_SALIDAS_AUTOS:
                if (selection.equals("auto2")){
                    String id_resid = selectionArgs[0];
                    String id_usuario = selectionArgs[1];
                    String id_auto = selectionArgs[2];
                    Log.e("Consulta ", "SELECT * FROM dtl_entradas_salidas_autos WHERE id_residencial="+"'"+id_resid+"'"+" and id_usuario="+"'"+id_usuario+"'"+" and  id_auto="+"'"+id_auto+"'"+" ORDER BY id DESC LIMIT 1");
                    cursor = bd.rawQuery("SELECT * FROM dtl_entradas_salidas_autos WHERE id_residencial="+"'"+id_resid+"'"+" and id_usuario="+"'"+id_usuario+"'"+" and  id_auto="+"'"+id_auto+"'"+" ORDER BY id DESC LIMIT 1",null);
                }
                if (selection.equals("sincronizacion")){
                    cursor = bd.rawQuery("SELECT * FROM dtl_entradas_salidas_autos WHERE sqliteEstatus = 1",null);
                }
                break;
            default:
                Log.e("error", "Error al ejecutar query:  " + uri.toString() );
                break;
        }

        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        //Log.d(TAG, "Inserción en " + uri + "( " + values.toString() + " )n");

        long insert = 0;

        String id = null;

        switch (uriMatcher.match(uri)){
            case APP_CASETA:
                insert = bd.insert("app_caseta", null, values);
                if (insert == -1){
                    Log.e("error", "Error al registrar en app_caseta");
                    return null;
                }
                break;
            case APP_CASETA_IMA:
                insert = bd.insert("app_caseta_ima", null, values);
                if (insert == -1){
                    Log.e("error", "Error al registrar en app_caseta_ima");
                    return null;
                }
                break;
            case INCIDENCIAS:
                insert = bd.insert("incidencias", null, values);
                if (insert == -1){
                    Log.e("error", "Error al registrar en incidencias");
                    return null;
                }
                break;
            case FOTOS_OFFLINE:
                insert = bd.insert("fotosOffline", null, values);
                if (insert == -1){
                    Log.e("error", "Error al registrar en fotos_offline");
                    return null;
                }
                break;
            case RONDINES:
                insert = bd.insert("rondines",null, values);
                if (insert == -1){
                    Log.e("error", "Error al registrar en rondines");
                    return null;
                }
                break;
            case RONDINES_QR:
                insert = bd.insert("rondines_qr",null, values);
                if (insert == -1){
                    Log.e("error", "Error al registrar en rondines_qr");
                    return null;
                }
                break;
            case RONDINES_DIA:
                insert = bd.insert("rondines_dia",null, values);
                if (insert == -1){
                    Log.e("error", "Error al registrar en rondines_dia");
                    return null;
                }
                break;
            case RONDINES_DIA_QR:
                insert = bd.insert("rondines_dia_qr",null, values);
                if (insert == -1){
                    Log.e("error", "Error al registrar en rondines_dia_qr");
                    return null;
                }
                break;
            case RONDINES_DTL:
                insert = bd.insert("rondines_dtl",null, values);
                if (insert == -1){
                    Log.e("error", "Error al registrar en rondines_dtl");
                    return null;
                }
                break;
            case RONDINES_DTL_QR:
                insert = bd.insert("rondines_dtl_qr",null, values);
                if (insert == -1){
                    Log.e("error", "Error al registrar en rondines_dtl_qr");
                    return null;
                }
                break;
            case RONDINES_INCIDENCIAS:
                insert = bd.insert("rondines_incidencias",null, values);
                if (insert == -1){
                    Log.e("error", "Error al registrar en rondines_incidencias");
                    return null;
                }
                break;
            case RONDINES_UBICACIONES:
                insert = bd.insert("rondines_ubicaciones",null, values);
                if (insert == -1){
                    Log.e("error", "Error al registrar en rondines_ubicaciones");
                    return null;
                }
                break;
            case RONDINES_UBICACIONES_QR:
                insert = bd.insert("rondines_ubicaciones_qr",null, values);
                if (insert == -1){
                    Log.e("error", "Error al registrar en rondines_ubicaciones_qr");
                    return null;
                }
                break;
            case SESION_CASETA:
                insert = bd.insert("sesion_caseta",null, values);
                if (insert == -1){
                    Log.e("error", "Error al registrar en sesion_caseta");
                    return null;
                }
                break;
            case UBICACIONES:
                insert = bd.insert("ubicaciones",null, values);
                if (insert == -1){
                    Log.e("error", "Error al registrar en ubicaciones");
                    return null;
                }
                break;
            case UBICACIONES_QR:
                insert = bd.insert("ubicaciones_qr",null, values);
                if (insert == -1){
                    Log.e("error", "Error al registrar en ubicaciones_qr");
                    return null;
                }
                break;
            case LUGAR:
                insert = bd.insert("lugar",null, values);
                if (insert == -1){
                    Log.e("error", "Error al registrar en lugar");
                    return null;
                }
                break;
            case USUARIO:
                insert = bd.insert("usuario",null, values);
                if (insert == -1){
                    Log.e("error", "Error al registrar en usuario");
                    return null;
                }
                break;
            case DTL_LUGAR_USUARIO:
                insert = bd.insert("dtl_lugar_usuario",null, values);
                if (insert == -1){
                    Log.e("error", "Error al registrar en dtl_lugar_usuario");
                    return null;
                }
                break;
            case CORRESPONDENCIA:
                insert = bd.insert("correspondencia",null, values);
                if (insert == -1){
                    Log.e("error", "Error al registrar en correspondencia");
                    return null;
                }
                break;
            case DTL_ENTRADAS_SALIDAS:
                insert = bd.insert("dtl_entradas_salidas",null, values);
                if (insert == -1){
                    Log.e("error", "Error al registrar en dtl_entradas_salidas");
                    return null;
                }
                break;
            case VISITA:
                insert = bd.insert("visita",null, values);
                if (insert == -1){
                    Log.e("error", "Error al registrar en visita");
                    return null;
                }
                break;
            case AUTO:
                insert = bd.insert("auto",null, values);
                if (insert == -1){
                    Log.e("error", "Error al registrar en auto");
                    return null;
                }
                break;
            case CAJONES:
                insert = bd.insert("cajones",null, values);
                if (insert == -1){
                    Log.e("error", "Error al registrar en cajones");
                    return null;
                }
                break;
            case DTL_ENTRADAS_SALIDAS_AUTOS:
                insert = bd.insert("dtl_entradas_salidas_autos",null, values);
                if (insert == -1){
                    Log.e("error", "Error al registrar en dtl_entradas_salidas_autos");
                    return null;
                }
                break;
            default:
                Log.e("error", "Error al insertar el registro:  " + uri.toString() );
                break;
        }
        return ContentUris.withAppendedId(uri,insert);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        //Log.d(TAG, "Delete registro : " + uri.toString());

        int delete = -1;

        switch (uriMatcher.match(uri)){
            case APP_CASETA:
                delete = bd.delete("app_caseta",null,null);
                break;
            case APP_CASETA_IMA:
                delete = bd.delete("app_caseta_ima",null,null);
                break;
            case INCIDENCIAS:
                delete = bd.delete("incidencias",null,null);
                break;
            case RONDINES:
                delete = bd.delete("rondines",null,null);
                break;
            case RONDINES_QR:
                delete = bd.delete("rondines_qr",null,null);
                break;
            case RONDINES_DIA:
                delete = bd.delete("rondines_dia",null,null);
                break;
            case RONDINES_DIA_QR:
                delete = bd.delete("rondines_dia_qr",null,null);
                break;
            case RONDINES_DTL:
                delete = bd.delete("rondines_dtl",null,null);
                break;
            case RONDINES_DTL_QR:
                delete = bd.delete("rondines_dtl_qr",null,null);
                break;
            case RONDINES_INCIDENCIAS:
                delete = bd.delete("rondines_incidencias",null,null);
                break;
            case RONDINES_UBICACIONES:
                delete = bd.delete("rondines_ubicaciones",null,null);
                break;
            case RONDINES_UBICACIONES_QR:
                delete = bd.delete("rondines_ubicaciones_qr",null,null);
                break;
            case SESION_CASETA:
                delete = bd.delete("sesion_caseta",null,null);
                break;
            case UBICACIONES:
                delete = bd.delete("ubicaciones",null,null);
                break;
            case UBICACIONES_QR:
                delete = bd.delete("ubicaciones_qr",null,null);
                break;
            case FOTOS_OFFLINE:
                delete = bd.delete("fotosOffline", selection, null);
                break;
            case LUGAR:
                delete = bd.delete("lugar", null, null);
                break;
            case USUARIO:
                delete = bd.delete("usuario", null, null);
                break;
            case DTL_LUGAR_USUARIO:
                delete = bd.delete("dtl_lugar_usuario", null, null);
                break;
            case CORRESPONDENCIA:
                delete = bd.delete("correspondencia", null, null);
                break;
            case DTL_ENTRADAS_SALIDAS:
                delete = bd.delete("dtl_entradas_salidas", null, null);
                break;
            case VISITA:
                delete = bd.delete("visita", null, null);
                break;
            case AUTO:
                delete = bd.delete("auto", null, null);
                break;
            case CAJONES:
                delete = bd.delete("cajones", null, null);
                break;
            case DTL_ENTRADAS_SALIDAS_AUTOS:
                delete = bd.delete("dtl_entradas_salidas_autos", null, null);
                break;
            default:
                Log.e("error", "Error al eliminar el registro:  " + uri.toString() );
                break;
        }

        return delete;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        int actualizar = -1;

        switch (uriMatcher.match(uri)){
            case CORRESPONDENCIA:
                actualizar = bd.update("correspondencia", values, selection, null);
                break;
            case VISITA:
                actualizar = bd.update("visita", values, selection, null);
                break;
            default:
                break;
        }
        return actualizar;
    }
}
