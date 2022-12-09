package mx.linkom.caseta_dm_offline.offline.Database;

import android.net.Uri;

public class UrisContentProvider {

    public static final String CONTENT_AUTHORITY = "mx.linkom.caseta_dm_offline";

    public static final Uri URI_BASE = Uri.parse("content://"+CONTENT_AUTHORITY);

    private static final String RUTA_INCIDENCIAS = "incidencias";
    private static final String RUTA_FOTOSOFFLINE = "fotosOffline";
    private static final String RUTA_APPCASETA = "app_caseta";
    private static final String RUTA_APPCASETAIMA = "app_caseta_ima";
    private static final String RUTA_RONDINES = "rondines";
    private static final String RUTA_RONDINESQR = "rondines_qr";
    private static final String RUTA_RONDINESDIA = "rondines_dia";
    private static final String RUTA_RONDINESDIAQR = "rondines_dia_qr";
    private static final String RUTA_RONDINESDTL = "rondines_dtl";
    private static final String RUTA_RONDINESDTLQR = "rondines_dtl_qr";
    private static final String RUTA_RONDINESINCIDENCIAS = "rondines_incidencias";
    private static final String RUTA_RONDINESUBICACIONES = "rondines_ubicaciones";
    private static final String RUTA_RONDINESUBICACIONESQR = "rondines_ubicaciones_qr";
    private static final String RUTA_SESIONCASETA = "sesion_caseta";
    private static final String RUTA_UBICACIONES = "ubicaciones";
    private static final String RUTA_UBICACIONESQR = "ubicaciones_qr";


    public static final Uri URI_CONTENIDO_APP_CASETA = Uri.withAppendedPath(URI_BASE, RUTA_APPCASETA);
    public static final Uri URI_CONTENIDO_INCIDENCIAS = Uri.withAppendedPath(URI_BASE, RUTA_INCIDENCIAS);
    public static final Uri URI_CONTENIDO_FOTOS_OFFLINE = Uri.withAppendedPath(URI_BASE, RUTA_FOTOSOFFLINE);
    public static final Uri URI_CONTENIDO_APPCASETAIMA  = Uri.withAppendedPath(URI_BASE, RUTA_APPCASETAIMA);
    public static final Uri URI_CONTENIDO_RONDINES = Uri.withAppendedPath(URI_BASE, RUTA_RONDINES);
    public static final Uri URI_CONTENIDO_RONDINESQR  = Uri.withAppendedPath(URI_BASE, RUTA_RONDINESQR);
    public static final Uri URI_CONTENIDO_RONDINESDIA = Uri.withAppendedPath(URI_BASE, RUTA_RONDINESDIA);
    public static final Uri URI_CONTENIDO_RONDINESDIAQR  = Uri.withAppendedPath(URI_BASE, RUTA_RONDINESDIAQR);
    public static final Uri URI_CONTENIDO_RONDINESDTL = Uri.withAppendedPath(URI_BASE, RUTA_RONDINESDTL);
    public static final Uri URI_CONTENIDO_RONDINESDTLQR = Uri.withAppendedPath(URI_BASE, RUTA_RONDINESDTLQR);
    public static final Uri URI_CONTENIDO_RONDINESINCIDENCIAS = Uri.withAppendedPath(URI_BASE, RUTA_RONDINESINCIDENCIAS);
    public static final Uri URI_CONTENIDO_RONDINESUBICACIONES = Uri.withAppendedPath(URI_BASE, RUTA_RONDINESUBICACIONES);
    public static final Uri URI_CONTENIDO_RONDINESUBICACIONESQR = Uri.withAppendedPath(URI_BASE, RUTA_RONDINESUBICACIONESQR);
    public static final Uri URI_CONTENIDO_SESIONCASETA = Uri.withAppendedPath(URI_BASE, RUTA_SESIONCASETA);
    public static final Uri URI_CONTENIDO_UBICACIONES = Uri.withAppendedPath(URI_BASE, RUTA_UBICACIONES);
    public static final Uri URI_CONTENIDO_UBICACIONESQR = Uri.withAppendedPath(URI_BASE, RUTA_UBICACIONESQR);


}



