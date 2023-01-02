package mx.linkom.caseta_dm_offline;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import id.zelory.compressor.Compressor;
import mx.linkom.caseta_dm_offline.offline.Database.UrisContentProvider;
import mx.linkom.caseta_dm_offline.offline.Global_info;

public class RecepcionActivity extends mx.linkom.caseta_dm_offline.Menu{

    private Configuracion Conf;
    FirebaseStorage storage;
    StorageReference storageReference;
    JSONArray ja1,ja2,ja3,ja4,ja5,ja6;
    Spinner Calle,Numero;
    ArrayList<String> calles,numero;
    LinearLayout Numero_o;

    EditText comen;
    Button foto,Registrar;
    ImageView ViewFoto;
    LinearLayout View,BtnReg,espacio,espacio2;

    ProgressDialog pd;
    int fotos;
    Bitmap bitmap;
    String usuario,nombre,correo,token,notificacion;
    Uri uri_img;

    ImageView iconoInternet;
    boolean Offline = false;
    String rutaImagen1, rutaImagen2, rutaImagen3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recepcion);
        Conf = new Configuracion(this);

        storage= FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        calles = new ArrayList<String>();
        numero = new ArrayList<String>();

        Calle = (Spinner)findViewById(R.id.setCalle);
        Numero = (Spinner)findViewById(R.id.setNumero);
        Numero_o = (LinearLayout) findViewById(R.id.numero);
        Numero_o.setVisibility(View.GONE);

        comen = (EditText) findViewById(R.id.setComent);
        foto = (Button) findViewById(R.id.foto);
        Registrar = (Button) findViewById(R.id.btnRegistrar);
        View = (LinearLayout) findViewById(R.id.View);
        BtnReg = (LinearLayout) findViewById(R.id.BtnReg);
        espacio = (LinearLayout) findViewById(R.id.espacio);
        espacio2 = (LinearLayout) findViewById(R.id.espacio2);
        ViewFoto = (ImageView) findViewById(R.id.viewFoto);
        iconoInternet = (ImageView) findViewById(R.id.iconoInternetRecepcion);


        if (Global_info.getINTERNET().equals("Si")){
            iconoInternet.setImageResource(R.drawable.ic_online);
            Offline = false;
        }else {
            iconoInternet.setImageResource(R.drawable.ic_offline);
            Offline = true;
        }

        if (Offline){
            callesOffline();
        }else{
            calles();
        }

        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fotos=1;
                if (Offline){
                    imgFotoOffline();
                }else{
                    imgFoto();
                }
            }
        });


        iconoInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Offline){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecepcionActivity.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("Aplicación funcionando en modo offline \n\nDatos actualizados hasta: \n\n"+Global_info.getULTIMA_ACTUALIZACION())
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();
                }else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecepcionActivity.this);
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

        pd= new ProgressDialog(this);
        pd.setMessage("Subiendo Foto...");

        Registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validacion();
            }
        });

        Numero_o.setVisibility(View.VISIBLE);
        cargarSpinner3();


    }

    //ALETORIO
    Random primero = new Random();
    int prime= primero.nextInt(9);

    String [] segundo = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandonsegun = (int) Math.round(Math.random() * 25 ) ;

    Random tercero = new Random();
    int tercer= tercero.nextInt(9);

    String [] cuarto = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandoncuart = (int) Math.round(Math.random() * 25 ) ;

    String numero_aletorio=prime+segundo[numRandonsegun]+tercer+cuarto[numRandoncuart];

    Calendar fecha = Calendar.getInstance();
    int anio = fecha.get(Calendar.YEAR);
    int mes = fecha.get(Calendar.MONTH) + 1;
    int dia = fecha.get(Calendar.DAY_OF_MONTH);


    //IMAGEN FOTO

    public void imgFoto(){
        Intent intentCaptura = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCaptura.addFlags(intentCaptura.FLAG_GRANT_READ_URI_PERMISSION);

        if (intentCaptura.resolveActivity(getPackageManager()) != null) {

            File foto=null;
            try {
                foto= new File(getApplication().getExternalFilesDir(null),"recepcion.png");
            } catch (Exception ex) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecepcionActivity.this);
                alertDialogBuilder.setTitle("Alerta");
                alertDialogBuilder
                        .setMessage("Error al capturar la foto")
                        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        }).create().show();
            }
            if (foto != null) {

                uri_img= FileProvider.getUriForFile(getApplicationContext(),getApplicationContext().getPackageName()+".provider",foto);
                intentCaptura.putExtra(MediaStore.EXTRA_OUTPUT,uri_img);
                startActivityForResult(intentCaptura, 0);
            }
        }
    }


    public void imgFotoOffline(){
        Intent intentCaptura = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCaptura.addFlags(intentCaptura.FLAG_GRANT_READ_URI_PERMISSION);

        if (intentCaptura.resolveActivity(getPackageManager()) != null) {

            File foto=null;
            try {
                foto= new File(getApplication().getExternalFilesDir(null),"app"+dia+mes+anio+"-"+numero_aletorio+".png");
                rutaImagen1 = foto.getAbsolutePath();
            } catch (Exception ex) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecepcionActivity.this);
                alertDialogBuilder.setTitle("Alerta");
                alertDialogBuilder
                        .setMessage("Error al capturar la foto")
                        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        }).create().show();
            }
            if (foto != null) {

                uri_img= FileProvider.getUriForFile(getApplicationContext(),getApplicationContext().getPackageName()+".provider",foto);
                intentCaptura.putExtra(MediaStore.EXTRA_OUTPUT,uri_img);
                startActivityForResult(intentCaptura, 0);
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 0 && resultCode == RESULT_OK) {

            Bitmap bitmap;

            if (Offline){
                bitmap= BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(null)+"/app"+dia+mes+anio+"-"+numero_aletorio+".png");
            }else{
                bitmap= BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(null)+"/recepcion.png");
            }

            ViewFoto.setVisibility(View.VISIBLE);
            ViewFoto.setImageBitmap(bitmap);
            View.setVisibility(View.VISIBLE);
            espacio.setVisibility(View.VISIBLE);
            BtnReg.setVisibility(View.VISIBLE);
            espacio2.setVisibility(View.VISIBLE);


        }
    }


    public void Validacion() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecepcionActivity.this);
        alertDialogBuilder.setTitle("Alerta");
        alertDialogBuilder
                .setMessage("¿ Desea notificar la correspondencia ?")
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    public void onClick(DialogInterface dialog, int id) {

                        if (Offline){
                            DatosOffline();
                        }else{
                            Datos();
                        }
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent i = new Intent(getApplicationContext(), CorrespondenciaActivity.class);
                        startActivity(i);
                        finish();
                    }
                }).create().show();
    }

    public void callesOffline(){

        try {
            String id_residencial = Conf.getResid().trim();
            String parametros[] = {id_residencial};
            Cursor cursor = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_LUGAR, null, "calles", parametros, null);

            ja1 = new JSONArray();

            if (cursor.moveToFirst()){
                do{
                    ja1.put(cursor.getString(0));
                }while (cursor.moveToNext());

                cargarSpinner();

            }else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecepcionActivity.this);
                alertDialogBuilder.setTitle("Alerta");
                alertDialogBuilder
                        .setMessage("Error al obtener calles")
                        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(getApplicationContext(), CorrespondenciaActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }).create().show();
            }

            cursor.close();
        }catch (Exception ex){
            Log.e("error", ex.toString());
        }

    }

    public void calles(){

        String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/correspondencia_1.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                response = response.replace("][",",");
                if (response.length()>0){
                    try {
                        ja1 = new JSONArray(response);
                        cargarSpinner();
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
                params.put("id_residencial", Conf.getResid().trim());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void cargarSpinner(){


        try{
            calles.add("Seleccionar..");
            calles.add("Seleccionar...");

            for (int i=0;i<ja1.length();i+=1){
                calles.add(ja1.getString(i+0));
            }

            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,calles);
            Calle.setAdapter(adapter1);
            Calle.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    if(Calle.getSelectedItem().equals("Seleccionar..")){
                        calles.remove(0);
                    }else if(Calle.getSelectedItem().equals("Seleccionar...")){
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecepcionActivity.this);
                            alertDialogBuilder.setTitle("Alerta");
                            alertDialogBuilder
                                    .setMessage("No selecciono ninguna calle...")
                                    .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {

                                        }
                                    }).create().show();
                    }
                    else{
                        numero.clear();
                        if (Offline){
                            numerosOffline(Calle.getSelectedItem().toString());
                        }else{
                            numeros(Calle.getSelectedItem().toString());
                        }
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void numeros(final String IdUsu){

            String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/correspondencia_2.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {


                @Override
                public void onResponse(String response) {
                    response = response.replace("][",",");
                    if (response.length()>0){
                        try {
                            ja2 = new JSONArray(response);
                       //     Numero_o.setVisibility(View.VISIBLE);
                            cargarSpinner2();
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
                    params.put("id_residencial", Conf.getResid().trim());
                    params.put("calle", IdUsu);
                    return params;
                }
            };
            requestQueue.add(stringRequest);
    }


    public void numerosOffline(final String IdUsu){


        try {
            String calle = IdUsu;
            String id_residencial = Conf.getResid().trim();
            String parametros[] = {calle, id_residencial};
            Cursor cursor = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_LUGAR, null, "numeros", parametros, null);

            ja2 = new JSONArray();

            if (cursor.moveToFirst()){
                do{
                    ja2.put(cursor.getString(0));
                }while (cursor.moveToNext());

                cargarSpinner2();

            }else{
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecepcionActivity.this);
                alertDialogBuilder.setTitle("Alerta");
                alertDialogBuilder
                        .setMessage("Error al obtener numeros de calles")
                        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(getApplicationContext(), CorrespondenciaActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }).create().show();

            }
        }catch (Exception ex){
            Log.e("error", ex.toString());
        }

    }


    public void cargarSpinner2(){

        numero.add("Seleccionar...");

        try{
            for (int i=0;i<ja2.length();i+=1){
                numero.add(ja2.getString(i+0));
            }

            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,numero);
            Numero.setAdapter(adapter1);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void cargarSpinner3() {

        numero.add("Seleccionar...");
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,numero);
        Numero.setAdapter(adapter1);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void DatosOffline () {

        if(Calle.getSelectedItem().equals("Seleccionar..") || Calle.getSelectedItem().equals("Seleccionar...") || Numero.getSelectedItem().equals("Seleccionar...")){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecepcionActivity.this);
            alertDialogBuilder.setTitle("Alerta");
            alertDialogBuilder
                    .setMessage("No selecciono ninguna calle o número...")
                    .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).create().show();
        }else{


            try {
                String calle = Calle.getSelectedItem().toString();
                String numero = Numero.getSelectedItem().toString();
                String id_residencial = Conf.getResid().trim();

                String parametros[] = {calle, numero, id_residencial};

                Log.e("sql1", "SELECT usuario.id,usuario.nombre,usuario.a_paterno,usuario.a_materno,usuario.correo_electronico,usuario.token,usuario.notificacion  FROM usuario,lugar, dtl_lugar_usuario WHERE usuario.id_residencial="+"'"+id_residencial+"'"+" and lugar.numero="+"'"+numero+"'"+" and  lugar.calle="+"'"+calle+"'"+" and usuario.id=dtl_lugar_usuario.id_usuario and lugar.id=dtl_lugar_usuario.id_lugar and usuario.estatus=1");
                Cursor cursor1 = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_USUARIO, null, "usuarios", parametros, null);

                int cont= 0;

                if (cursor1.moveToFirst()){
                    do {
                        cont += 1;
                    }while (cursor1.moveToNext());
                }else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecepcionActivity.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("Está UP no esta habitada")
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(getApplicationContext(), CorrespondenciaActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            }).create().show();
                }

                Log.e("error", "Valor de contador: " + cont);
                switch (cont){
                    case 1:
                        ja3 = new JSONArray();
                        if (cursor1.moveToFirst()){
                            ja3.put(cursor1.getString(0));
                            ja3.put(cursor1.getString(1));
                            ja3.put(cursor1.getString(2));
                            ja3.put(cursor1.getString(3));
                            ja3.put(cursor1.getString(4));
                            ja3.put(cursor1.getString(5));
                            ja3.put(cursor1.getString(6));

                            RegistrarOffline();
                        }
                        break;
                    case 2:
                        Cursor cursor2 = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_USUARIO, null, "residente_o_inquilino", parametros, null);

                        ja3 = new JSONArray();
                        if (cursor1.moveToFirst()){
                            ja3.put(cursor1.getString(0));
                            ja3.put(cursor1.getString(1));
                            ja3.put(cursor1.getString(2));
                            ja3.put(cursor1.getString(3));
                            ja3.put(cursor1.getString(4));
                            ja3.put(cursor1.getString(5));
                            ja3.put(cursor1.getString(6));

                            RegistrarOffline();

                        }else {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecepcionActivity.this);
                            alertDialogBuilder.setTitle("Alerta");
                            alertDialogBuilder
                                    .setMessage("Está UP no esta habitada")
                                    .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent i = new Intent(getApplicationContext(), CorrespondenciaActivity.class);
                                            startActivity(i);
                                            finish();
                                        }
                                    }).create().show();
                        }
                        break;
                    default:
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecepcionActivity.this);
                        alertDialogBuilder.setTitle("Alerta");
                        alertDialogBuilder
                                .setMessage("Está UP no esta habitada")
                                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent i = new Intent(getApplicationContext(), CorrespondenciaActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                }).create().show();
                        break;
                }



            }catch (Exception ex){
                Log.e("error", ex.toString());
            }


        }

    }

    public void Datos () {

        if(Calle.getSelectedItem().equals("Seleccionar..") || Calle.getSelectedItem().equals("Seleccionar...") || Numero.getSelectedItem().equals("Seleccionar...")){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecepcionActivity.this);
            alertDialogBuilder.setTitle("Alerta");
            alertDialogBuilder
                    .setMessage("No selecciono ninguna calle o número...")
                    .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).create().show();
        }else{

            String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/correspondencia_3.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

                @Override
                public void onResponse(String response){

                    if(response.equals("error")){
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecepcionActivity.this);
                        alertDialogBuilder.setTitle("Alerta");
                        alertDialogBuilder
                                .setMessage("Está UP no esta habitada")
                                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent i = new Intent(getApplicationContext(), CorrespondenciaActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                }).create().show();
                    }else {
                        try {
                            ja3 = new JSONArray(response);
                            Registrar();
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
                    params.put("calle", Calle.getSelectedItem().toString());
                    params.put("numero",Numero.getSelectedItem().toString());
                    params.put("id_residencial", Conf.getResid().trim());



                    return params;
                }
            };
            requestQueue.add(stringRequest);

        }

    }


    public void Registrar (){
        String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/correspondencia_4.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response){

                System.out.println("RESPONSE DE REGISTRO EN LINEA:  " + response);

                if(response.equals("error")){

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecepcionActivity.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("Registro de Correspondencia No Exitoso")
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(getApplicationContext(), CorrespondenciaActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            }).create().show();
                }else {

                    upload1(response);
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
                try {
                    usuario = ja3.getString(0);
                    nombre=ja3.getString(1)+" "+ja3.getString(2)+" "+ja3.getString(3);
                    correo=ja3.getString(4);
                    token=ja3.getString(5);
                    notificacion=ja3.getString(6);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Map<String, String> params = new HashMap<>();
                params.put("id_usuario", usuario);
                params.put("guardia", Conf.getUsu().trim());
                params.put("comen", comen.getText().toString().trim());
                params.put("foto_recep", "app"+dia+mes+anio+"-"+numero_aletorio+".png");
                params.put("nombre", nombre);
                params.put("correo", correo);
                params.put("token", token);
                params.put("id_residencial", Conf.getResid().trim());
                params.put("nom_residencial",Conf.getNomResi().trim());
                params.put("notificacion",notificacion);

                return params;

            }
        };
        requestQueue.add(stringRequest);


    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void RegistrarOffline (){

        try {
            long id = 0;

            String titulo_Foto = "app"+dia+mes+anio+"-"+numero_aletorio+".png";

            //Registrar fotos en SQLite
            ContentValues val_img1 =  ValuesImagen(titulo_Foto, Conf.getPin()+"/correspondencia/"+titulo_Foto.trim(), rutaImagen1);
            Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_FOTOS_OFFLINE, val_img1);

            //Obtener fecha
            LocalDateTime hoy = LocalDateTime.now();

            int year = hoy.getYear();
            int month = hoy.getMonthValue();
            int day = hoy.getDayOfMonth();
            int hour = hoy.getHour();
            int minute = hoy.getMinute();
            int second =hoy.getSecond();

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
            String segundo = "0";

            if (second < 10){
                segundo = "0"+second;
            }else {
                segundo = ""+second;
            }

            if (hour < 10 || minute < 10){
                if (hour < 10 && minute >=10){
                    hora = "0"+hour+":"+minute+":"+segundo;
                }else if (hour >= 10 && minute < 10){
                    hora = hour+":0"+minute+":"+segundo;
                }else if (hour < 10 && minute < 10){
                    hora = "0"+hour+":0"+minute+":"+segundo;
                }
            }else {
                hora = hour+":"+minute+":"+segundo;
            }


            String fecha_registro = fecha + " " + hora;

            try {
                usuario = ja3.getString(0);
                nombre=ja3.getString(1)+" "+ja3.getString(2)+" "+ja3.getString(3);
                correo=ja3.getString(4);
                token=ja3.getString(5);
                notificacion=ja3.getString(6);

            } catch (JSONException e) {
                e.printStackTrace();
            }


            ContentValues values = new ContentValues();
            values.put("id_residencial", Conf.getResid().trim());
            values.put("id_usuario", usuario);
            values.put("id_tipo_paquete", 1);
            values.put("id_tipo_envio", 1);
            values.put("id_guardia", Conf.getUsu().trim());
            values.put("comentarios", comen.getText().toString().trim());
            values.put("foto_recep", titulo_Foto);
            values.put("foto", "");
            values.put("fecha_registro", fecha_registro);
            values.put("club", 0);
            values.put("fecha_entrega", "0000-00-00 00:00:00");
            values.put("estatus", 2);
            values.put("nombre", nombre);
            values.put("correo", correo);
            values.put("token", token);
            values.put("nombre_r", Conf.getNomResi().trim());
            values.put("notificacion", notificacion);
            values.put("sqliteEstatus", 1);

            //Insertar registro en correspondencia
            Uri uri2 = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_CORRESPONDENCIA, values);
            String idUri = uri2.getLastPathSegment();
            id = Integer.parseInt(idUri);

            //Si se inserto correctamente
            if (id != -1){

                //Obtener el id consecutivo del dispositivo
                /*ContentValues insert = new ContentValues();
                insert.put("fecha_registro", fecha);

                Uri uri_id_local = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_CORRESPONDENCIA_OFFLINE, insert);
                String id_uri_local = uri_id_local.getLastPathSegment();
                int id_local = Integer.parseInt(id_uri_local);*/

                String id_offline = Conf.getUsu().trim()+id;

                /*if (id_local != -1){
                    id_offline = Conf.getUsu().trim()+id_local;
                }*/

                ContentValues act = new ContentValues();
                act.put("id_offline", id_offline);

                int acttualizar = getContentResolver().update(UrisContentProvider.URI_CONTENIDO_CORRESPONDENCIA, act, "id="+id, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecepcionActivity.this);
                alertDialogBuilder.setTitle("Alerta");
                alertDialogBuilder
                        .setMessage("Registro de correspondencia  exitoso en offline FOLIO: "+id_offline)
                        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(getApplicationContext(), CorrespondenciaActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }).create().show();
            }else{
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecepcionActivity.this);
                alertDialogBuilder.setTitle("Alerta");
                alertDialogBuilder
                        .setMessage("Registro de correspondencia no exitoso en modo offline")
                        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(getApplicationContext(), CorrespondenciaActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }).create().show();
            }
        }catch (Exception ex){
            Log.e("Exception ", ex.toString());
        }

    }

    public ContentValues ValuesImagen(String nombre, String rutaFirebase, String rutaDispositivo){
        ContentValues values = new ContentValues();
        values.put("titulo", nombre);
        values.put("direccionFirebase", rutaFirebase);
        values.put("rutaDispositivo", rutaDispositivo);
        return values;
    }


    public void upload1(final String resp){

        StorageReference mountainImagesRef = null;
        mountainImagesRef = storageReference.child(Conf.getPin()+"/correspondencia/app"+dia+mes+anio+"-"+numero_aletorio+".png");

        UploadTask uploadTask = mountainImagesRef.putFile(uri_img);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                pd.show();

            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(AccesoActivity.this,"Pausado",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(RecepcionActivity.this,"Fallado", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RecepcionActivity.this);
                alertDialogBuilder.setTitle("Alerta");
                alertDialogBuilder
                        .setMessage("Registro de Correspondencia  Exitoso FOLIO:"+resp)
                        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(getApplicationContext(), CorrespondenciaActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }).create().show();



            }
        });
    }




    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), CorrespondenciaActivity.class);
        startActivity(intent);
        finish();

    }

}
