package mx.linkom.caseta_dm_offline.offline.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {

    public static int VERSION = 10;
    public Database(@Nullable Context context) {
        super(context, "caseta.db", null, VERSION);
    }

    //Se ejcuta cuando la base de datos se  va a crear
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createbd(sqLiteDatabase);
    }

    //Se ejecuta cuando se va a hacer una a ctualización en la versión
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS incidencias");
        db.execSQL("DROP TABLE IF EXISTS fotosOffline");
        db.execSQL("DROP TABLE IF EXISTS app_caseta");
        db.execSQL("DROP TABLE IF EXISTS app_caseta_ima");
        db.execSQL("DROP TABLE IF EXISTS rondines");
        db.execSQL("DROP TABLE IF EXISTS rondines_qr");
        db.execSQL("DROP TABLE IF EXISTS rondines_dia");
        db.execSQL("DROP TABLE IF EXISTS rondines_dia_qr");
        db.execSQL("DROP TABLE IF EXISTS rondines_dtl");
        db.execSQL("DROP TABLE IF EXISTS rondines_dtl_qr");
        db.execSQL("DROP TABLE IF EXISTS rondines_incidencias");
        db.execSQL("DROP TABLE IF EXISTS rondines_ubicaciones");
        db.execSQL("DROP TABLE IF EXISTS rondines_ubicaciones_qr");
        db.execSQL("DROP TABLE IF EXISTS sesion_caseta");
        db.execSQL("DROP TABLE IF EXISTS ubicaciones");
        db.execSQL("DROP TABLE IF EXISTS ubicaciones_qr");
        db.execSQL("DROP TABLE IF EXISTS lugar");
        db.execSQL("DROP TABLE IF EXISTS usuario");
        db.execSQL("DROP TABLE IF EXISTS dtl_lugar_usuario");
        db.execSQL("DROP TABLE IF EXISTS correspondencia");
        db.execSQL("DROP TABLE IF EXISTS auto");
        db.execSQL("DROP TABLE IF EXISTS dtl_entradas_salidas");
        db.execSQL("DROP TABLE IF EXISTS visita");
        db.execSQL("DROP TABLE IF EXISTS cajones");
        db.execSQL("DROP TABLE IF EXISTS dtl_entradas_salidas_autos");

        onCreate(db);
    }

    public void createbd(SQLiteDatabase db){
        //Status 0 = No se ha modificado, 1=  Insertado desde SQLite, 2 = Editado desde SQLite

        String incidencias = "CREATE TABLE incidencias" +
                "(id INTEGER PRIMARY KEY, " +
                "id_residencial INTEGER," +
                "id_usuario INTEGER," +
                "id_tipo INTEGER," +
                "id_rondin INTEGER," +
                "dia TEXT," +
                "hora TEXT," +
                "detalle TEXT," +
                "accion TEXT," +
                "foto1 TEXT," +
                "foto2 TEXT, " +
                "foto3 TEXT," +
                "club INTEGER," +
                "estatus INTEGER," +
                "sqliteEstatus INTEGER);";

        String fotosOffline = "CREATE TABLE fotosOffline" +
                "(id INTEGER PRIMARY KEY, " +
                "titulo TEXT, " +
                "direccionFirebase TEXT, " +
                "rutaDispositivo TEXT)";

        String app_caseta = "CREATE TABLE app_caseta" +
                "(id INTEGER PRIMARY KEY, " +
                "id_residencial INTEGER," +
                "qr INTEGER," +
                "registro INTEGER," +
                "pre_entradas INTEGER," +
                "trabajadores INTEGER," +
                "consulta_placas INTEGER," +
                "consulta_trabajadores INTEGER," +
                "incidencias INTEGER," +
                "correspondencia INTEGER," +
                "rondin INTEGER, " +
                "tickete INTEGER," +
                "ticketr INTEGER," +
                "estatus INTEGER," +
                "sqliteEstatus INTEGER);";

        String app_caseta_ima = "CREATE TABLE app_caseta_ima" +
                "(id INTEGER PRIMARY KEY, " +
                "id_residencial INTEGER," +
                "id_app INTEGER," +
                "foto1 INTEGER," +
                "nombre_foto1 TEXT," +
                "foto2 INTEGER," +
                "nombre_foto2 TEXT," +
                "foto3 INTEGER," +
                "nombre_foto3 TEXT," +
                "estatus INTEGER," +
                "sqliteEstatus INTEGER);";

        String rondines = "CREATE TABLE rondines" +
                "(id INTEGER PRIMARY KEY, " +
                "id_residencial INTEGER," +
                "nombre TEXT," +
                "club INTEGER," +
                "estatus INTEGER," +
                "sqliteEstatus INTEGER);";

        String rondines_qr = "CREATE TABLE rondines_qr" +
                "(id INTEGER PRIMARY KEY, " +
                "id_residencial INTEGER," +
                "nombre TEXT," +
                "club INTEGER," +
                "estatus INTEGER," +
                "sqliteEstatus INTEGER);";

        String rondines_dia = "CREATE TABLE rondines_dia" +
                "(id INTEGER PRIMARY KEY, " +
                "id_residencial INTEGER," +
                "id_rondin INTEGER," +
                "dia TEXT," +
                "club INTEGER," +
                "estatus INTEGER," +
                "sqliteEstatus INTEGER);";

        String rondines_dia_qr = "CREATE TABLE rondines_dia_qr" +
                "(id INTEGER PRIMARY KEY, " +
                "id_residencial INTEGER," +
                "id_rondin INTEGER," +
                "dia TEXT," +
                "club INTEGER," +
                "estatus INTEGER," +
                "sqliteEstatus INTEGER);";

        String rondines_dtl = "CREATE TABLE rondines_dtl" +
                "(id INTEGER PRIMARY KEY, " +
                "id_residencial INTEGER," +
                "id_rondin INTEGER," +
                "id_dia INTEGER," +
                "id_ubicaciones INTEGER," +
                "latitud TEXT," +
                "longitud TEXT," +
                "dia TEXT," +
                "hora TEXT," +
                "estatus INTEGER," +
                "sqliteEstatus INTEGER);";

        String rondines_dtl_qr = "CREATE TABLE rondines_dtl_qr" +
                "(id INTEGER PRIMARY KEY, " +
                "id_residencial INTEGER," +
                "id_rondin INTEGER," +
                "id_dia INTEGER," +
                "id_ubicaciones INTEGER," +
                "dia TEXT," +
                "hora TEXT," +
                "estatus INTEGER," +
                "sqliteEstatus INTEGER);";

        String rondines_incidencias = "CREATE TABLE rondines_incidencias" +
                "(id INTEGER PRIMARY KEY, " +
                "id_residencial INTEGER," +
                "id_rondin INTEGER," +
                "id_usuario INTEGER," +
                "id_tipo INTEGER," +
                "id_ubicacion INTEGER," +
                "dia TEXT," +
                "hora TEXT," +
                "detalle TEXT," +
                "accion TEXT," +
                "foto1 TEXT, " +
                "foto2 TEXT," +
                "foto3 TEXT," +
                "club INTEGER," +
                "estatus INTEGER," +
                "sqliteEstatus INTEGER);";

        String rondines_ubicaciones = "CREATE TABLE rondines_ubicaciones" +
                "(id INTEGER PRIMARY KEY, " +
                "id_residencial INTEGER," +
                "id_rondin INTEGER," +
                "hora TEXT," +
                "id_ubicacion INTEGER," +
                "id_usuario INTEGER," +
                "club INTEGER," +
                "estatus INTEGER," +
                "sqliteEstatus INTEGER);";

        String rondines_ubicaciones_qr = "CREATE TABLE rondines_ubicaciones_qr" +
                "(id INTEGER PRIMARY KEY, " +
                "id_residencial INTEGER," +
                "id_rondin INTEGER," +
                "hora TEXT," +
                "id_ubicacion INTEGER," +
                "id_usuario INTEGER," +
                "club INTEGER," +
                "estatus INTEGER," +
                "sqliteEstatus INTEGER);";

        String sesion_caseta = "CREATE TABLE sesion_caseta" +
                "(id INTEGER PRIMARY KEY, " +
                "id_residencial INTEGER," +
                "nombre_completo TEXT," +
                "usuario TEXT," +
                "contrasenia TEXT," +
                "correo_electronico TEXT," +
                "caseta INTEGER," +
                "sesion INTEGER," +
                "hora_inicio TEXT," +
                "hora_fin TEXT," +
                "comentarios TEXT," +
                "token TEXT," +
                "club INTEGER," +
                "estatus INTEGER," +
                "sqliteEstatus INTEGER);";

        String ubicaciones = "CREATE TABLE ubicaciones" +
                "(id INTEGER PRIMARY KEY, " +
                "id_residencial INTEGER," +
                "nombre TEXT," +
                "longitud TEXT," +
                "latitud TEXT," +
                "club INTEGER," +
                "estatus INTEGER," +
                "sqliteEstatus INTEGER);";

        String ubicaciones_qr = "CREATE TABLE ubicaciones_qr" +
                "(id INTEGER PRIMARY KEY, " +
                "id_residencial INTEGER," +
                "nombre TEXT," +
                "qr TEXT," +
                "club INTEGER," +
                "estatus INTEGER," +
                "sqliteEstatus INTEGER);";

        String lugar = "CREATE TABLE lugar" +
                "(id INTEGER PRIMARY KEY, " +
                "id_residencial INTEGER," +
                "nombre TEXT," +
                "estado TEXT," +
                "municipio TEXT," +
                "colonia TEXT," +
                "calle TEXT," +
                "numero INTEGER," +
                "codigo_postal TEXT," +
                "descripcion TEXT," +
                "estatus INTEGER," +
                "sqliteEstatus INTEGER);";

        String usuario = "CREATE TABLE usuario" +
                "(id INTEGER PRIMARY KEY, " +
                "id_residencial INTEGER," +
                "tipo_usuario INTEGER," +
                "nombre TEXT," +
                "a_paterno TEXT," +
                "a_materno TEXT," +
                "telefono TEXT," +
                "correo_electronico TEXT," +
                "usuario TEXT," +
                "contrasenia TEXT," +
                "foto TEXT," +
                "miembro_club INTEGER," +
                "fecha_registro TEXT," +
                "notificacion INTEGER," +
                "token TEXT," +
                "fecha_nacimiento TEXT," +
                "usu_master INTEGER," +
                "estatus INTEGER," +
                "sqliteEstatus INTEGER);";

        String dtl_lugar_usuario = "CREATE TABLE dtl_lugar_usuario" +
                "(id INTEGER PRIMARY KEY, " +
                "id_residencial INTEGER," +
                "id_lugar INTEGER," +
                "id_usuario INTEGER," +
                "estatus INTEGER," +
                "sqliteEstatus INTEGER);";

        String correspondencia = "CREATE TABLE correspondencia" +
                "(id INTEGER PRIMARY KEY, " +
                "id_residencial INTEGER," +
                "id_usuario INTEGER," +
                "id_tipo_paquete INTEGER," +
                "id_tipo_envio INTEGER," +
                "id_guardia INTEGER," +
                "comentarios TEXT," +
                "foto_recep TEXT," +
                "foto TEXT," +
                "fecha_registro TEXT," +
                "club INTEGER," +
                "fecha_entrega TEXT," +
                "estatus INTEGER," +
                "id_offline TEXT," +
                "nombre TEXT," +
                "correo TEXT," +
                "token TEXT," +
                "nombre_r TEXT," +
                "notificacion TEXT," +
                "sqliteEstatus INTEGER);";


        String visita = "CREATE TABLE visita" +
                "(id INTEGER PRIMARY KEY, " +
                "id_residencial INTEGER," +
                "id_usuario INTEGER," +
                "id_tipo_visita INTEGER," +
                "id_tipo INTEGER," +
                "ilimitada TEXT," +
                "evento TEXT," +
                "nombre_visita TEXT," +
                "correo_electronico TEXT," +
                "comentarios TEXT," +
                "fecha_entrada TEXT," +
                "fecha_salida TEXT," +
                "codigo_qr TEXT," +
                "fecha_registro TEXT," +
                "club INTEGER," +
                "estatus INTEGER," +
                "sqliteEstatus INTEGER);";

        String dtl_entradas_salidas = "CREATE TABLE dtl_entradas_salidas" +
                "(id INTEGER PRIMARY KEY, " +
                "id_residencial INTEGER," +
                "id_visita INTEGER," +
                "entrada_real TEXT," +
                "salida_real TEXT," +
                "guardia_de_entrada INTEGER," +
                "guardia_de_salida INTEGER," +
                "cajon TEXT," +
                "personas INTEGER," +
                "placas TEXT," +
                "descripcion_transporte TEXT," +
                "foto1 TEXT," +
                "foto2 TEXT," +
                "foto3 TEXT," +
                "comentarios_salida_tardia TEXT," +
                "estatus INTEGER," +
                "sqliteEstatus INTEGER);";

        String auto = "CREATE TABLE auto" +
                "(id INTEGER PRIMARY KEY, " +
                "id_residencial INTEGER," +
                "id_usuario INTEGER," +
                "marca TEXT," +
                "placas TEXT," +
                "modelo TEXT," +
                "color TEXT," +
                "tarjeta TEXT," +
                "fotografia TEXT," +
                "fecha_registro TEXT," +
                "detalle TEXT," +
                "qr TEXT," +
                "estatus INTEGER," +
                "sqliteEstatus INTEGER);";

        String cajones = "CREATE TABLE cajones" +
                "(id INTEGER PRIMARY KEY, " +
                "id_residencial INTEGER," +
                "nombre TEXT," +
                "descripcion TEXT," +
                "usado INTEGER," +
                "club INTEGER," +
                "estatus INTEGER," +
                "sqliteEstatus INTEGER);";

        String dtl_entradas_salidas_autos = "CREATE TABLE dtl_entradas_salidas_autos" +
                "(id INTEGER PRIMARY KEY, " +
                "id_residencial INTEGER," +
                "id_usuario INTEGER," +
                "id_auto INTEGER," +
                "entrada_real TEXT," +
                "guardia_de_entrada INTEGER," +
                "salida_real TEXT," +
                "guardia_de_salida INTEGER," +
                "foto1 TEXT," +
                "foto2 TEXT," +
                "foto3 TEXT," +
                "estatus INTEGER," +
                "sqliteEstatus INTEGER);";

        db.execSQL(incidencias);
        db.execSQL(fotosOffline);
        db.execSQL(app_caseta);
        db.execSQL(app_caseta_ima);
        db.execSQL(rondines);
        db.execSQL(rondines_qr);
        db.execSQL(rondines_dia);
        db.execSQL(rondines_dia_qr);
        db.execSQL(rondines_dtl);
        db.execSQL(rondines_dtl_qr);
        db.execSQL(rondines_incidencias);
        db.execSQL(rondines_ubicaciones);
        db.execSQL(rondines_ubicaciones_qr);
        db.execSQL(sesion_caseta);
        db.execSQL(ubicaciones);
        db.execSQL(ubicaciones_qr);
        db.execSQL(lugar);
        db.execSQL(usuario);
        db.execSQL(dtl_lugar_usuario);
        db.execSQL(correspondencia);
        db.execSQL(visita);
        db.execSQL(dtl_entradas_salidas);
        db.execSQL(auto);
        db.execSQL(cajones);
        db.execSQL(dtl_entradas_salidas_autos);

    }
}
