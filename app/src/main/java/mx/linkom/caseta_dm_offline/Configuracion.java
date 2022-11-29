package mx.linkom.caseta_dm_offline;

import android.content.Context;
import android.content.SharedPreferences;

public class Configuracion {

    private final String SHARED_PREFS_FILE = "HMPrefs";
    private final String KEY_EMAIL = "cor";
    private final String KEY_PASS = "con";
    private final String KEY_QR = "cqr";
    private final String KEY_QR_RONDINES = "cqrr";
    private final String KEY_USU = "cusu";
    private final String KEY_IDPRE = "cusupre";
    private final String KEY_PREQR = "cupreqr";
    private final String KEY_TICKET_E = "cutickete";
    private final String KEY_TICKET_R = "cuticketr";
    private final String KEY_TIPOQR = "cutipoqr";
    private final String KEY_ID_REG = "cuidreg";
    private final String KEY_ST = "est";
    private final String KEY_STPLACAS = "estplacas";
    private final String KEY_PLACAS = "cplacas";
    private final String KEY_RESI = "cresid";
    private final String KEY_LATITUD = "usu_latitud";
    private final String KEY_LONGITUD = "usu_longitud";
    private final String KEY_TRABAJADOR = "usu_trab";
    private final String KEY_REGTRABAJADOR = "usu_regtrab";

    private final String KEY_RONDIN = "usu_rondin";
    private final String KEY_RONDINOMBRE = "usu_rondinombre";
    private final String KEY_DIA = "usu_dia";
    private final String KEY_UBICACION= "usu_ubicacion";
    private final String KEY_LATITUD2 = "usu_latitud2";
    private final String KEY_LONGITUD2 = "usu_longitud2";
    private final String KEY_ID_VISITA = "usu_id_visita";
    private final String KEY_EVENTTO= "usu_evento";
    private final String KEY_NOMBRE= "usu_nombre";
    private final String KEY_NIP = "cnip";
    private final String KEY_NOMRE = "cnombrere";
    private final String KEY_BD = "cbd";
    private final String KEY_BDUSU = "cbdusu";
    private final String KEY_BDCON = "cbdcon";
    private Context mContext;

    public Configuracion(Context context){
        mContext = context;
    }

    private SharedPreferences getSettings(){
        return mContext.getSharedPreferences(SHARED_PREFS_FILE, 0);
    }

//    //REGTRABA
//    public String getRegTraba(){
//        return getSettings().getString(KEY_REGTRABAJADOR, null);
//    }
//
//    public void setRegTraba(String usu_regtrab){
//        SharedPreferences.Editor editor = getSettings().edit();
//        editor.putString(KEY_REGTRABAJADOR, usu_regtrab );
//        editor.commit();
//    }
    //TRABAJADOR
    public String getTraba(){
        return getSettings().getString(KEY_TRABAJADOR, null);
    }

    public void setTraba(String usu_trab){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_TRABAJADOR, usu_trab );
        editor.commit();
    }

    //PIN
    public String getPin(){
        return getSettings().getString(KEY_NIP, null);
    }

    public void setPin(String cnip){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_NIP, cnip );
        editor.commit();
    }

    //NOMBRE_RESIDENCIAL
    public String getNomResi(){
        return getSettings().getString(KEY_NOMRE, null);
    }

    public void setNomResi(String cnombrere){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_NOMRE, cnombrere );
        editor.commit();
    }

    //BD
    public String getBd(){
        return getSettings().getString(KEY_BD, null);
    }

    public void setBd(String cbd){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_BD, cbd );
        editor.commit();
    }

    //BD_USUARIO
    public String getBdUsu(){
        return getSettings().getString(KEY_BDUSU, null);
    }

    public void setBdUsu(String cbdusu){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_BDUSU, cbdusu );
        editor.commit();
    }


    //BD_CONTRASEÑA
    public String getBdCon(){
        return getSettings().getString(KEY_BDCON, null);
    }

    public void setBdCon(String cbdcon){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_BDCON, cbdcon );
        editor.commit();
    }



    //TICKET ENTRADA
    public String getTicketE(){
        return getSettings().getString(KEY_TICKET_E, null);
    }

    public void setTicketE(String cutickete){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_TICKET_E, cutickete );
        editor.commit();
    }
    //TIPO RECEPCION VISITAS
    public String getTicketR(){
        return getSettings().getString(KEY_TICKET_R, null);
    }

    public void setTicketR(String cuticketr){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_TICKET_R, cuticketr );
        editor.commit();
    }
    //TIPO REGISTRO
    public String getTipoReg(){
        return getSettings().getString(KEY_ID_REG, null);
    }

    public void setTipoReg(String cuidreg){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_ID_REG, cuidreg );
        editor.commit();
    }
    //TIPO QR
    public String getTipoQr(){
        return getSettings().getString(KEY_TIPOQR, null);
    }

    public void setTipoQr(String cutipoqr){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_TIPOQR, cutipoqr );
        editor.commit();
    }

    //PREQR
    public String getPreQr(){
        return getSettings().getString(KEY_PREQR, null);
    }

    public void setPreQr(String cupreqr){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PREQR, cupreqr );
        editor.commit();
    }
    //IDPRE
    public String getIdPre(){
        return getSettings().getString(KEY_IDPRE, null);
    }

    public void setIdPre(String cusupre){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_IDPRE, cusupre );
        editor.commit();
    }

    //Nombre
    public String getNombre(){
        return getSettings().getString(KEY_NOMBRE, null);
    }

    public void setNombre(String usu_nombre){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_NOMBRE, usu_nombre );
        editor.commit();
    }

    //EVENTO VISITA GRUPAL
    public String getEvento(){
        return getSettings().getString(KEY_EVENTTO, null);
    }

    public void setEvento(String usu_evento){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_EVENTTO, usu_evento );
        editor.commit();
    }
    //ID VISITA
    public String getIdvisita(){
        return getSettings().getString(KEY_ID_VISITA, null);
    }

    public void setIdvisita(String usu_id_visita){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_ID_VISITA, usu_id_visita );
        editor.commit();
    }


    //RONDIN NOMBRE
    public String getRondinNombre(){
        return getSettings().getString(KEY_RONDINOMBRE, null);
    }

    public void setRondinNombre(String usu_rondinombre){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_RONDINOMBRE, usu_rondinombre );
        editor.commit();
    }

 //RONDIN
    public String getRondin(){
        return getSettings().getString(KEY_RONDIN, null);
    }

    public void setRondin(String usu_rondin){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_RONDIN, usu_rondin );
        editor.commit();
    }
//DIA
public String getDia(){
    return getSettings().getString(KEY_DIA, null);
}

    public void setDia(String usu_dia){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_DIA, usu_dia );
        editor.commit();
    }
//UBICACION
    public String getUbicacion(){
        return getSettings().getString(KEY_UBICACION, null);
    }

    public void setUbicacion(String usu_ubicacion){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_UBICACION, usu_ubicacion );
        editor.commit();
    }
//LATITUD


    public String getUsuLatitud2(){
        return getSettings().getString(KEY_LATITUD2, null);
    }

    public void setUsuLatitud2(String usu_latitud2){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_LATITUD2, usu_latitud2 );
        editor.commit();
    }
//LONGITUD


    public String getUsuLongitud2(){
        return getSettings().getString(KEY_LONGITUD2, null);
    }

    public void setUsuLongitud2(String usu_longitud2){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_LONGITUD2, usu_longitud2 );
        editor.commit();
    }
//RESIDENCIAL
    public String getResid(){
        return getSettings().getString(KEY_RESI, null);
    }

    public void setResid(String cresid){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_RESI, cresid );
        editor.commit();
    }
//USUARIO
    public String getUsu(){
        return getSettings().getString(KEY_USU, null);
    }

    public void setUsu(String cusu){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_USU, cusu );
        editor.commit();
    }
//CORREO
    public String getUserEmail(){
        return getSettings().getString(KEY_EMAIL, null);
    }

    public void setUserEmail(String cor){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_EMAIL, cor );
        editor.commit();
    }
//PASWORD
    public String getUserPass(){
        return getSettings().getString(KEY_PASS, null);
    }

    public void setUserPass(String con){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PASS, con );
        editor.commit();
    }
    //QR_RONDINES
    public String getQRondines(){
        return getSettings().getString(KEY_QR_RONDINES, null);
    }

    public void setQRondines(String cqrr){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_QR_RONDINES, cqrr );
        editor.commit();
    }
//QR
    public String getQR(){
        return getSettings().getString(KEY_QR, null);
    }

    public void setQR(String cqr){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_QR, cqr );
        editor.commit();
    }
//ESTATUS
    public String getST(){
        return getSettings().getString(KEY_ST, null);
    }

    public void setST(String est){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_ST, est );
        editor.commit();
    }
//PLACAS
    public String getPlacas(){
        return getSettings().getString(KEY_PLACAS, null);
    }

    public void setPlacas(String cplacas){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_PLACAS, cplacas );
        editor.commit();
    }

    public String getSTPlacas(){
        return getSettings().getString(KEY_STPLACAS, null);
    }

    public void setSTPlacas(String estplacas){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_STPLACAS, estplacas );
        editor.commit();
    }



    //LATITUD


    public String getUsuLatitud(){
        return getSettings().getString(KEY_LATITUD, null);
    }

    public void setUsuLatitud(String usu_latitud){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_LATITUD, usu_latitud );
        editor.commit();
    }

    //LONGITUD


    public String getUsuLongitud(){
        return getSettings().getString(KEY_LONGITUD, null);
    }

    public void setUsuLongitud(String usu_longitud){
        SharedPreferences.Editor editor = getSettings().edit();
        editor.putString(KEY_LONGITUD, usu_longitud );
        editor.commit();
    }
}








