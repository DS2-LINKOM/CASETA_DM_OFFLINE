package mx.linkom.caseta_dm_offline.offline;

import android.app.Application;

public class Global_info extends Application {
    private static boolean INTERNET_DISPOSITIVO = false;
    private static int SEGUNDOS = 0;
    private static String ULTIMA_ACTUALIZACION = "No se ha registrado ninguna actualización";
    private static String INTERNET = "Si";
    private static String URL = "http://192.168.7.106/android/demoCaseta/";

    public static String getINTERNET() {
        return INTERNET;
    }

    public static void setINTERNET(String INTERNET) {
        Global_info.INTERNET = INTERNET;
    }

    public boolean getINTERNET_DISPOSITIVO() {
        return INTERNET_DISPOSITIVO;
    }

    public void setINTERNET_DISPOSITIVO(boolean INTERNET_DISPOSITIVO) {
        this.INTERNET_DISPOSITIVO = INTERNET_DISPOSITIVO;
    }

    public static String getULTIMA_ACTUALIZACION() {
        return ULTIMA_ACTUALIZACION;
    }

    public static void setULTIMA_ACTUALIZACION(String ultimaActualizacion) {
        ULTIMA_ACTUALIZACION = ultimaActualizacion;
    }

    public int getSEGUNDOS() {
        return SEGUNDOS;
    }

    public void setSEGUNDOS(int SEGUNDOS) {
        this.SEGUNDOS = SEGUNDOS;
    }

    public static String getURL() {
        return URL;
    }

    public static void setURL(String URL) {
        Global_info.URL = URL;
    }
}
