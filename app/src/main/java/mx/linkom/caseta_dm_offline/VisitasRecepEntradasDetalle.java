package mx.linkom.caseta_dm_offline;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class VisitasRecepEntradasDetalle extends mx.linkom.caseta_dm_offline.Menu {
    Configuracion Conf;
    FirebaseStorage storage;
    StorageReference storageReference;

    LinearLayout rlPermitido, rlDenegado,rlVista,ContinuarBoton;
    TextView  tvMensaje;
    TextView Nombre,Dire,Visi,Pasajeros,Placas,Tipo,Tel;

    ArrayList<String> names;
    JSONArray ja1,ja2,ja3,ja4,ja5,ja6;
    Date FechaA;
    String estado;

    Button Registrar,Registrar2,Continuar;
    ImageView view1,view2,view3;
    TextView nombre_foto1,nombre_foto2,nombre_foto3;
    LinearLayout Foto1, Foto2,Foto3,Foto1View,Foto2View,Foto3View,espacio2,espacio3,espacio4,espacio5,espacio6,espacio8,espacio9,espacio10;
    LinearLayout PlacasL;
    EditText Comentarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitasrecepentradasdetalle);

        Conf = new Configuracion(this);
        storage= FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        names = new ArrayList<String>();

        Comentarios = (EditText)findViewById(R.id.setComentarios);
        PlacasL = (LinearLayout) findViewById(R.id.PlacasL);
        Foto1 = (LinearLayout) findViewById(R.id.Foto1);
        Foto2 = (LinearLayout) findViewById(R.id.Foto2);
        Foto3 = (LinearLayout) findViewById(R.id.Foto3);
        Foto1View = (LinearLayout) findViewById(R.id.Foto1View);
        Foto2View = (LinearLayout) findViewById(R.id.Foto2View);
        Foto3View = (LinearLayout) findViewById(R.id.Foto3View);
        espacio2 = (LinearLayout) findViewById(R.id.espacio2);
        espacio3 = (LinearLayout) findViewById(R.id.espacio3);
        espacio4 = (LinearLayout) findViewById(R.id.espacio4);
        espacio5 = (LinearLayout) findViewById(R.id.espacio5);
        espacio6 = (LinearLayout) findViewById(R.id.espacio6);
        espacio8 = (LinearLayout) findViewById(R.id.espacio8);
        espacio9 = (LinearLayout) findViewById(R.id.espacio9);
        espacio10 = (LinearLayout) findViewById(R.id.espacio10);
        rlVista = (LinearLayout) findViewById(R.id.rlVista);
        rlPermitido = (LinearLayout) findViewById(R.id.rlPermitido);
        rlDenegado = (LinearLayout) findViewById(R.id.rlDenegado);
        ContinuarBoton = (LinearLayout) findViewById(R.id.ContinuarBoton);
        tvMensaje = (TextView)findViewById(R.id.setMensaje);

        Registrar = (Button) findViewById(R.id.Registrar);
        Registrar2 = (Button) findViewById(R.id.Registrar2);

        nombre_foto1 = (TextView) findViewById(R.id.nombre_foto1);
        nombre_foto2 = (TextView) findViewById(R.id.nombre_foto2);
        nombre_foto3 = (TextView) findViewById(R.id.nombre_foto3);

        view1 = (ImageView) findViewById(R.id.view1);
        view2 = (ImageView) findViewById(R.id.view2);
        view3 = (ImageView) findViewById(R.id.view3);

        rlVista = (LinearLayout) findViewById(R.id.rlVista);
        rlPermitido = (LinearLayout) findViewById(R.id.rlPermitido);
        rlDenegado = (LinearLayout) findViewById(R.id.rlDenegado);
        tvMensaje = (TextView)findViewById(R.id.setMensaje);

        //SI ES ACEPTADO O DENEGAODO
        if(Conf.getST().equals("Aceptado")){
            rlVista.setVisibility(View.VISIBLE);
            rlPermitido.setVisibility(View.GONE);
            rlDenegado.setVisibility(View.GONE);
            menu();
        }else if(Conf.getST().equals("Denegado")){
            rlDenegado.setVisibility(View.VISIBLE);
            rlVista.setVisibility(View.GONE);
            rlPermitido.setVisibility(View.GONE);
            tvMensaje.setText("Placa Inexistente");
        }

        Nombre = (TextView)findViewById(R.id.setNombre);
        Dire = (TextView)findViewById(R.id.setDire);
        Tel = (TextView)findViewById(R.id.setTel);
        Visi = (TextView) findViewById(R.id.setVisi);
        Placas = (TextView) findViewById(R.id.setPlacas);
        Pasajeros = (TextView) findViewById(R.id.setPasajeros);
        Registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validacion();
            }
        });

        Registrar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validacion2();
            }
        });
        Tipo = (TextView)findViewById(R.id.setTipo);
        Continuar = (Button) findViewById(R.id.continuar);


        Continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), AccesosMorososActivity.class);
                startActivity(i);
                finish();
            }
        });
        Log.e("TAG", "LINKOM ST: " + Conf.getTicketR());

    }


    public void menu() {
        String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/menu.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {
                        ja5 = new JSONArray(response);
                        submenu(ja5.getString(0));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Error: " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_residencial", Conf.getResid());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void submenu(final String id_app) {
        String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/menu_2.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if(response.equals("error")){
                    int $arreglo[]={0};
                    try {
                        ja6 = new JSONArray($arreglo);
                        Visita();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else {
                    response = response.replace("][", ",");
                    if (response.length() > 0) {
                        try {
                            ja6 = new JSONArray(response);
                            Visita();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Error: " + error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_app", id_app.trim());
                params.put("id_residencial", Conf.getResid());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void Visita(){

        String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/vst_php1.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                response = response.replace("][",",");
                if (response.length()>0){
                    try {
                        ja1 = new JSONArray(response);

                        Usuario(ja1.getString(2));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG","Error: " + error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("QR", Conf.getQR().trim());
                params.put("id_residencial", Conf.getResid().trim());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void Usuario(final String IdUsu){ //DATOS USUARIO

        String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/vst_php2.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                response = response.replace("][",",");
                if (response.length()>0){
                    try {
                        ja2 = new JSONArray(response);
                        dtlLugar(ja2.getString(0));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG","Error: " + error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("IdUsu", IdUsu.trim());
                params.put("id_residencial", Conf.getResid().trim());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void dtlLugar(final String idUsuario){
        String URLResidencial = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/vst_php3.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLResidencial, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response.equals("error")) {
                    sincasa();
                } else {

                    response = response.replace("][", ",");
                    if (response.length() > 0) {
                        try {
                            ja3 = new JSONArray(response);
                            salidas(ja1.getString(0));


                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Error: " + error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_usuario", idUsuario.trim());
                params.put("id_residencial", Conf.getResid().trim());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }



    public void salidas (final String id_visitante){
        String URLResidencial = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/vst_php6.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLResidencial, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("TAG", "Tipo: " + response);

                try {
                    if (response.trim().equals("error")){

                        String $arreglo[]={"0","0","0"};
                        ja4 = new JSONArray($arreglo);
                        ValidarQR();
                    }else{
                        response = response.replace("][",",");
                        ja4 = new JSONArray(response);
                        ValidarQR();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", "Error: " + error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_visitante", id_visitante.trim());
                params.put("id_residencial", Conf.getResid().trim());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }




    public void sincasa(){

        rlVista.setVisibility(View.GONE);
        rlPermitido.setVisibility(View.GONE);
        rlDenegado.setVisibility(View.VISIBLE);
        tvMensaje.setText(" No tiene asignada una unidad privativa.");

    }



    public void ValidarQR(){
        try {
            if(ja1.getString(11).equals("0000-00-00 00:00:00")){
                rlVista.setVisibility(View.GONE);
                rlPermitido.setVisibility(View.VISIBLE);
                rlDenegado.setVisibility(View.GONE);

                Nombre.setText(ja2.getString(1) + " " + ja2.getString(2) + " " + ja2.getString(3));
                if(ja1.getString(4).equals("1") || ja1.getString(12).equals("0")){
                    Tipo.setText("Visita");
                }else if(ja1.getString(4).equals("2")){
                    Tipo.setText("Proveedor / Servicios");
                }else if(ja1.getString(4).equals("3")){
                    Tipo.setText("Taxista");
                }
                Dire.setText(ja3.getString(0));
                Visi.setText(ja1.getString(7));
                Pasajeros.setText(ja4.getString(6));
                Tel.setText(ja2.getString(4));



                if(ja4.getString(7).equals("")){
                    PlacasL.setVisibility(View.GONE);

                }else{
                    PlacasL.setVisibility(View.VISIBLE);
                    Placas.setText(ja4.getString(7));
                }
                //FOTO1
                if(ja4.getString(3).equals("")){
                    Foto1.setVisibility(View.GONE);
                    espacio2.setVisibility(View.GONE);
                    Foto1View.setVisibility(View.GONE);
                    espacio3.setVisibility(View.GONE);

                }else{
                    nombre_foto1.setText(ja6.getString(4)+":");

                    storageReference.child(Conf.getPin()+"/caseta/"+ja4.getString(3))
                            .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                        @Override

                        public void onSuccess(Uri uri) {
                            Glide.with(VisitasRecepEntradasDetalle.this)
                                    .load(uri)
                                    .error(R.drawable.log)
                                    .centerInside()
                                    .into(view1);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                }

                //FOTO2
                if(ja4.getString(4).equals("")){
                    Foto2.setVisibility(View.GONE);
                    espacio5.setVisibility(View.GONE);
                    Foto2View.setVisibility(View.GONE);
                    espacio6.setVisibility(View.GONE);
                }else{
                    nombre_foto2.setText(ja6.getString(6)+":");

                    storageReference.child(Conf.getPin()+"/caseta/"+ja4.getString(4))
                            .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                        @Override

                        public void onSuccess(Uri uri) {
                            Glide.with(VisitasRecepEntradasDetalle.this)
                                    .load(uri)
                                    .error(R.drawable.log)
                                    .centerInside()
                                    .into(view2);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                }
                //FOTO3
                if(ja4.getString(5).equals("")){
                    Foto3.setVisibility(View.GONE);
                    espacio8.setVisibility(View.GONE);
                    Foto3View.setVisibility(View.GONE);
                    espacio9.setVisibility(View.GONE);
                }else{
                    nombre_foto3.setText(ja6.getString(8)+":");

                    storageReference.child(Conf.getPin()+"/caseta/"+ja4.getString(5))
                            .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                        @Override

                        public void onSuccess(Uri uri) {
                            Glide.with(VisitasRecepEntradasDetalle.this)
                                    .load(uri)
                                    .error(R.drawable.log)
                                    .centerInside()
                                    .into(view3);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                        }
                    });
                }

            }else{

                Calendar c = Calendar.getInstance();
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                FechaA = Calendar.getInstance().getTime();
                Date dateentrada = (Date)formatter.parse(ja1.getString(10));
                Date datesalida = (Date)formatter.parse(ja1.getString(11));


                        rlVista.setVisibility(View.GONE);
                        rlPermitido.setVisibility(View.VISIBLE);
                        rlDenegado.setVisibility(View.GONE);

                        Nombre.setText(ja2.getString(1) + " " + ja2.getString(2) + " " + ja2.getString(3));
                        if(ja1.getString(4).equals("1") || ja1.getString(12).equals("0")){
                            Tipo.setText("Visita");
                        }else if(ja1.getString(4).equals("2")){
                            Tipo.setText("Proveedor / Servicios");
                        }else if(ja1.getString(4).equals("3")){
                            Tipo.setText("Taxista");
                        }
                        Dire.setText(ja3.getString(0));
                        Visi.setText(ja1.getString(7));
                        Pasajeros.setText(ja4.getString(6));
                        Comentarios.setText(ja1.getString(9));
                        Tel.setText(ja2.getString(4));

                        if(ja4.getString(7).equals("")){
                            PlacasL.setVisibility(View.GONE);

                        }else{
                            PlacasL.setVisibility(View.VISIBLE);
                            Placas.setText(ja4.getString(7));
                        }
                        //FOTO1
                        if(ja4.getString(3).equals("")){
                            Foto1.setVisibility(View.GONE);
                            espacio2.setVisibility(View.GONE);
                            Foto1View.setVisibility(View.GONE);
                            espacio3.setVisibility(View.GONE);

                        }else{
                            nombre_foto1.setText(ja6.getString(4)+":");

                            storageReference.child(Conf.getPin()+"/caseta/"+ja4.getString(3))
                                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                                @Override

                                public void onSuccess(Uri uri) {
                                    Glide.with(VisitasRecepEntradasDetalle.this)
                                            .load(uri)
                                            .error(R.drawable.log)
                                            .centerInside()
                                            .into(view1);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });
                        }

                        //FOTO2
                        if(ja4.getString(4).equals("")){
                            Foto2.setVisibility(View.GONE);
                            espacio5.setVisibility(View.GONE);
                            Foto2View.setVisibility(View.GONE);
                            espacio6.setVisibility(View.GONE);
                        }else{
                            nombre_foto2.setText(ja6.getString(6)+":");

                            storageReference.child(Conf.getPin()+"/caseta/"+ja4.getString(4))
                                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                                @Override

                                public void onSuccess(Uri uri) {
                                    Glide.with(VisitasRecepEntradasDetalle.this)
                                            .load(uri)
                                            .error(R.drawable.log)
                                            .centerInside()
                                            .into(view2);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });
                        }
                        //FOTO3
                        if(ja4.getString(5).equals("")){
                            Foto3.setVisibility(View.GONE);
                            espacio8.setVisibility(View.GONE);
                            Foto3View.setVisibility(View.GONE);
                            espacio9.setVisibility(View.GONE);
                        }else{
                            nombre_foto3.setText(ja6.getString(8)+":");

                            storageReference.child(Conf.getPin()+"/caseta/"+ja4.getString(5))
                                    .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                                @Override

                                public void onSuccess(Uri uri) {
                                    Glide.with(VisitasRecepEntradasDetalle.this)
                                            .load(uri)
                                            .error(R.drawable.log)
                                            .centerInside()
                                            .into(view3);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });
                        }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void Validacion() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VisitasRecepEntradasDetalle.this);
        alertDialogBuilder.setTitle("Alerta");
        alertDialogBuilder
                .setMessage("¿ Desea registrar la entrada ?")
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                       Registrar("Si");
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent i = new Intent(getApplicationContext(), EntradasSalidasActivity.class);
                        startActivity(i);
                        finish();
                    }
                }).create().show();
    }

    public void Validacion2() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VisitasRecepEntradasDetalle.this);
        alertDialogBuilder.setTitle("Alerta");
        alertDialogBuilder
                .setMessage("¿ Desea registrar el acceso denegado ?")
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Registrar("No");

                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        //Intent i = new Intent(getApplicationContext(), EntradasSalidasActivity.class);
                       // startActivity(i);
                        //finish();
                    }
                }).create().show();
    }

    public void Registrar (final String aprobar){


        String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/visitas_recep_3.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                if(response.equals("succes")){
                     if(aprobar.trim().equals("Si")){
                         estado="Entrada Exitosa";
                     }else if(aprobar.trim().equals("No")){
                         estado="Entrada Denegada Exitoso";
                     }
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VisitasRecepEntradasDetalle.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage(estado)
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                  if(aprobar.trim().equals("Si")){
                                      if(Integer.parseInt(Conf.getTicketR())==1){
                                          Imprimir();
                                      }else {
                                          Intent i = new Intent(getApplicationContext(), RecepcionVisitasActivity.class);
                                          startActivity(i);
                                          finish();
                                      }
                                  }else{
                                      Intent i = new Intent(getApplicationContext(), RecepcionVisitasActivity.class);
                                      startActivity(i);
                                      finish();
                                  }

                                }
                            }).create().show();


                }else {

                    if(aprobar.trim().equals("Si")){
                        estado="Entrada No Exitosa";
                    }else if(aprobar.trim().equals("No")){
                        estado="Entrada Denegada No Exitoso";

                    }
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VisitasRecepEntradasDetalle.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage(estado)
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {

                                        Intent i = new Intent(getApplicationContext(), RecepcionVisitasActivity.class);
                                        startActivity(i);
                                        finish();


                                }
                            }).create().show();


                }
            }
        }, new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG","Error: " + error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                try {

                    params.put("id_residencial", Conf.getResid().trim());
                    params.put("id", ja1.getString(0).trim());
                    params.put("id_vigilante", Conf.getUsu().trim());
                    params.put("aprobar",aprobar.trim());
                    params.put("usuario",ja2.getString(1).trim() + " " + ja2.getString(2).trim() + " " + ja2.getString(3).trim());
                    params.put("token", ja2.getString(5).trim());
                    params.put("correo",ja2.getString(6).trim());
                    params.put("visita",ja1.getString(7).trim());

                    params.put("comentarios",Comentarios.getText().toString().trim());


                } catch (JSONException e) {
                    Log.e("TAG","Error: " + e.toString());
                }
                return params;
            }
        };
        requestQueue.add(stringRequest);


    }

    public void Imprimir() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(VisitasRecepEntradasDetalle.this);
        alertDialogBuilder.setTitle("Alerta");
        alertDialogBuilder
                .setMessage("Desea imprimir ticket?")
                .setPositiveButton("Si ",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(getApplicationContext(), TicketImprimirActivity.class);
                        startActivity(i);
                        finish();

                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent i = new Intent(getApplicationContext(), EntradasSalidasActivity.class);
                        startActivity(i);
                        finish();

                    }
                }).create().show();

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), EntradasSalidasActivity.class);
        startActivity(intent);
        finish();
    }
}
