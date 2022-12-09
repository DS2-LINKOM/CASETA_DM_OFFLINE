package mx.linkom.caseta_dm_offline.Controller;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import mx.linkom.caseta_dm_offline.Configuracion;
import mx.linkom.caseta_dm_offline.R;
import mx.linkom.caseta_dm_offline.RondinInfoActivity;
import mx.linkom.caseta_dm_offline.RondinInfoQrActivity;
import mx.linkom.caseta_dm_offline.adaptadores.ListasClassGrid;
import mx.linkom.caseta_dm_offline.adaptadores.adaptador_Modulo;
import mx.linkom.caseta_dm_offline.offline.Database.UrisContentProvider;
import mx.linkom.caseta_dm_offline.offline.Global_info;

public class Qr extends Fragment {


    private GridView gridList;
    private Configuracion Conf;
    JSONArray ja1;

    boolean Offline = false;


    public Qr() {
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr, container, false);

        Conf = new Configuracion(getActivity());
        gridList = (GridView) view.findViewById(R.id.gridList);

        if (Global_info.getINTERNET().equals("Si")){
            Offline = false;
            horarios();
        }else {
            Offline = true;
            horariosOffline();
        }

        return view;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void horariosOffline() {

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

        String usuario = Conf.getUsu().trim();
        String dia = fecha;
        String id_residencial = Conf.getResid().trim();
        String tiempo = hora;

        String parametros[] = {usuario, dia, id_residencial, tiempo};

        Log.e("error", "SELECT ubi.id, ubi.hora, ubis.nombre, dia.id, dia.dia, rondin.id, rondin.nombre FROM rondines_ubicaciones_qr as ubi, rondines_dia_qr as dia, rondines_qr as rondin, ubicaciones_qr as ubis WHERE ubi.id_usuario="+"'"+usuario+"'"+" and ubi.id_rondin=dia.id_rondin and ubi.id_rondin=rondin.id and dia.dia="+"'"+dia+"'"+" and ubi.id_residencial="+"'"+id_residencial+"'"+" and ubi.hora<="+"'"+tiempo+"'"+" and ubis.id=ubi.id_ubicacion and NOT EXISTS (SELECT * FROM rondines_dtl_qr WHERE rondines_dtl_qr.id_ubicaciones=ubi.id and rondines_dtl_qr.id_dia=dia.id and rondines_dtl_qr.id_rondin=rondin.id)");

        Cursor cursorQr = getActivity().getContentResolver().query(UrisContentProvider.URI_CONTENIDO_RONDINESUBICACIONESQR, null, null, parametros, null);

        ja1 = new JSONArray();

        if (cursorQr.moveToFirst()){
            do {
                ja1.put(cursorQr.getString(0));
                ja1.put(cursorQr.getString(1));
                ja1.put(cursorQr.getString(2));
                ja1.put(cursorQr.getString(3));
                ja1.put(cursorQr.getString(4));
                ja1.put(cursorQr.getString(5));
                ja1.put(cursorQr.getString(6));
            }while (cursorQr.moveToNext());

            cursorQr.close();
            llenado();
        }

    }


    public void horarios() {
        String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/rondines_qr_1.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
       // String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/rondines_1.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {
                        ja1 = new JSONArray(response);
                        Log.e("Error ", "LINKOM ST: " + response);

                        llenado();

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
                params.put("guardia_de_entrada", Conf.getUsu().trim());
                params.put("id_residencial", Conf.getResid().trim());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }




    public void llenado(){
        ArrayList<ListasClassGrid> ubicacion = new ArrayList<ListasClassGrid>();


        for (int i = 0; i < ja1.length(); i += 7) {
            try {
               // String sCadena = ja1.getString(i + 1);
                //String hora = sCadena.substring(0,5);

                ubicacion.add(new ListasClassGrid(ja1.getString(i+1)+" - "+ja1.getString(i + 2), "ID:"+ja1.getString(i + 0)));
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        gridList.setAdapter(new adaptador_Modulo(getActivity(), R.layout.activity_listas, ubicacion){
            @Override
            public void onEntrada(Object entrada, View view) {
                if (entrada != null) {

                    final TextView title = (TextView) view.findViewById(R.id.title);
                    if (title != null)
                        title.setText(((ListasClassGrid) entrada).getTitle());

                    final TextView subtitle = (TextView) view.findViewById(R.id.sub);
                    if (subtitle != null)
                        subtitle.setText(((ListasClassGrid) entrada).getSubtitle());

                    gridList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {


                            int posicion=position*7;
                            try {
                                //RONDIN DIA
                                Conf.setRondin(ja1.getString(posicion));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Intent i = new Intent(getActivity(), RondinInfoQrActivity.class);
                            startActivity(i);
                        }
                    });


                }
            }

        });
    }

}