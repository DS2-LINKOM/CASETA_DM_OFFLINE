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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import id.zelory.compressor.Compressor;
import mx.linkom.caseta_dm_offline.offline.Database.UrisContentProvider;
import mx.linkom.caseta_dm_offline.offline.Global_info;

public class EntregaActivity extends mx.linkom.caseta_dm_offline.Menu {

    TextView setNumero,setComent,setPara;
    ImageView foto_recep,viewFoto;
    Button foto,btnRegistrar;
    LinearLayout View,espacio,espacio2,BtnReg,rlVista,rlPermitido;
    ProgressDialog pd;
    JSONArray ja1,ja2;
    private Configuracion Conf;
    FirebaseStorage storage;
    StorageReference storageReference;
    Bitmap bitmap;
    String fotos;
    Uri uri_img;

    ImageView iconoInternet;
    boolean Offline = false;
    TextView resp_foto;

    String rutaImagen1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrega);

        storage= FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        Conf = new Configuracion(this);

        setPara = (TextView) findViewById(R.id.setPara);
        setNumero = (TextView) findViewById(R.id.setNumero);
        setComent = (TextView) findViewById(R.id.setComent);
        foto_recep = (ImageView) findViewById(R.id.foto_recep);
        viewFoto = (ImageView) findViewById(R.id.viewFoto);
        foto = (Button) findViewById(R.id.foto);
        btnRegistrar = (Button) findViewById(R.id.btnRegistrar);
        rlVista = (LinearLayout) findViewById(R.id.rlVista);
        rlPermitido = (LinearLayout) findViewById(R.id.rlPermitido);
        View = (LinearLayout) findViewById(R.id.View);
        espacio = (LinearLayout) findViewById(R.id.espacio);
        espacio2 = (LinearLayout) findViewById(R.id.espacio2);
        BtnReg = (LinearLayout) findViewById(R.id.BtnReg);
        iconoInternet = (ImageView) findViewById(R.id.iconoInternetEntrega);
        resp_foto = (TextView) findViewById(R.id.resp_foto_entrega);

        if (Global_info.getINTERNET().equals("Si")){
            iconoInternet.setImageResource(R.drawable.ic_online);
            Offline = false;
        }else {
            iconoInternet.setImageResource(R.drawable.ic_offline);
            Offline = true;
        }

        iconoInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Offline){
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(EntregaActivity.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("Aplicación funcionando en modo offline \n\nDatos actualizados hasta: \n\n"+Global_info.getULTIMA_ACTUALIZACION())
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();
                }else {
                    android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(EntregaActivity.this);
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
        pd.setMessage("Subiendo Imagen ...");

        if (Offline){
            checkOffline();
        }else {
            check();
        }

        foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Offline){
                    imgFotoOffline();
                }else {
                    imgFoto();
                }
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validacion();
            }});

    }




    //ALETORIO
    Random primero = new Random();
    int prime= primero.nextInt(9);

    String[] segundo = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandonsegun = (int) Math.round(Math.random() * 25 ) ;

    Random tercero = new Random();
    int tercer= tercero.nextInt(9);

    String[] cuarto = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandoncuart = (int) Math.round(Math.random() * 25 ) ;

    String numero_aletorio=prime+segundo[numRandonsegun]+tercer+cuarto[numRandoncuart];


    public void checkOffline() {

        try {
            String id_residencial = Conf.getResid().trim();
            String fol = Conf.getPlacas();

            String parametros[] = {fol, id_residencial};

            Cursor cursor = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_CORRESPONDENCIA, null, "Online", parametros, null);

            if (cursor.moveToFirst()){
                ja1 = new JSONArray();
                ja1.put(cursor.getString(0));
                ja1.put(cursor.getString(1));
                ja1.put(cursor.getString(2));
                ja1.put(cursor.getString(3));
                ja1.put(cursor.getString(4));
                ja1.put(cursor.getString(5));
                ja1.put(cursor.getString(6));
                ja1.put(cursor.getString(7));
                ja1.put(cursor.getString(8));
                ja1.put(cursor.getString(9));
                ja1.put(cursor.getString(10));
                ja1.put(cursor.getString(11));
                ja1.put(cursor.getString(12));
                ja1.put(cursor.getString(13));
                ja1.put(cursor.getString(14));
                ja1.put(cursor.getString(15));
                ja1.put(cursor.getString(16));
                ja1.put(cursor.getString(17));
                ja1.put(cursor.getString(18));
                ja1.put(cursor.getString(19));

                check2Offline();
            }else{

            }
        }catch (Exception ex){
            Log.e("Exception", ex.toString());
        }
    }


    public void check() {
        String url = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/correspondencia_5.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                response = response.replace("][",",");
                if (response.length()>0){
                    try {
                        ja1 = new JSONArray(response);
                        check2();
                    } catch (JSONException e) {

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
                params.put("Folio", Conf.getPlacas());
                params.put("id_residencial", Conf.getResid().trim());

                return params;
            }
        };

        requestQueue.add(stringRequest);
    }


    public void check2Offline() {

        try {
            String id_usuario = ja1.getString(2);
            String id_residencial = Conf.getResid().trim();

            String parametros[] = {id_usuario, id_residencial};

            Cursor cursor = getContentResolver().query(UrisContentProvider.URI_CONTENIDO_DTL_LUGAR_USUARIO, null, null, parametros, null);

            if (cursor.moveToFirst()){
                ja2 = new JSONArray();
                ja2.put(cursor.getString(0));
                ja2.put(cursor.getString(1));

                ValidarQR();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    public void check2() {
        String url = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/correspondencia_6.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                response = response.replace("][",",");
                if (response.length()>0){
                    try {
                        ja2 = new JSONArray(response);
                        ValidarQR();
                    } catch (JSONException e) {

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
                try {
                    params.put("id_usuario", ja1.getString(2));
                    params.put("id_residencial", Conf.getResid().trim());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }


    public void ValidarQR(){
        try {
            rlVista.setVisibility(View.GONE);
            rlPermitido.setVisibility(View.VISIBLE);

            System.out.println("val: "+ja1.getString(13));
            if (!ja1.getString(13).isEmpty()){
                setNumero.setText(ja1.getString(0)+"-"+ja1.getString(13));
            }else{
                setNumero.setText(ja1.getString(0));
            }

            setPara.setText(ja2.getString(0));
            setComent.setText(ja1.getString(6));

            if (Offline){
                resp_foto.setText("Sin foto en modo offline");
                foto_recep.setVisibility(android.view.View.INVISIBLE);
            }else {
                storageReference.child(Conf.getPin()+"/correspondencia/"+ja1.getString(7))
                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                            @Override

                            public void onSuccess(Uri uri) {

                                Glide.with(EntregaActivity.this)
                                        .load(uri)
                                        .error(R.drawable.log)
                                        .centerInside()
                                        .into(foto_recep);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Log.e("TAG","Error123: " + exception);

                            }
                        });
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //IMAGEN FOTO

    public void imgFoto(){
        Intent intentCaptura = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCaptura.addFlags(intentCaptura.FLAG_GRANT_READ_URI_PERMISSION);

        if (intentCaptura.resolveActivity(getPackageManager()) != null) {

            File foto=null;
            try {
                foto= new File(getApplication().getExternalFilesDir(null),"entrega.png");
            } catch (Exception ex) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EntregaActivity.this);
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
                foto= new File(getApplication().getExternalFilesDir(null),"app"+ja1.getString(2)+"-"+numero_aletorio+".png");
                rutaImagen1 = foto.getAbsolutePath();
            } catch (Exception ex) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EntregaActivity.this);
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


        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {


                Bitmap bitmap = null;
                if (Offline){
                    try {
                        bitmap = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(null) + "/app"+ja1.getString(2)+"-"+numero_aletorio+".png");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    bitmap = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(null) + "/entrega.png");
                }



                View.setVisibility(android.view.View.VISIBLE);
                viewFoto.setVisibility(android.view.View.VISIBLE);
                viewFoto.setImageBitmap(bitmap);
                espacio.setVisibility(android.view.View.VISIBLE);
                espacio2.setVisibility(android.view.View.VISIBLE);
                BtnReg.setVisibility(android.view.View.VISIBLE);
                btnRegistrar.setVisibility(android.view.View.VISIBLE);

            }
        }
    }


    public void Validacion() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EntregaActivity.this);
        alertDialogBuilder.setTitle("Alerta");
        alertDialogBuilder
                .setMessage("¿ Desea Entregar Paquete ?")
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    public void onClick(DialogInterface dialog, int id) {

                        if (Offline){
                            RegistrarOffline();
                        }else {
                            Registrar();
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


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void RegistrarOffline(){

        try {
            int actualizar = 0;

            String titulo_Foto = "app"+ja1.getString(2)+"-"+numero_aletorio+".png";

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


            String fecha_entrega = fecha + " " + hora;

            String status = "";
            if (ja1.getString(19) != null){
                status = ja1.getString(19).trim();
            }

            System.out.println("Status sqlite: " + ja1.getString(19));

            System.out.println("ID : " + ja1.getString(0));

            ContentValues values = new ContentValues();
            values.put("foto", titulo_Foto);
            values.put("fecha_entrega", fecha_entrega);
            values.put("token",ja2.getString(1));
            values.put("nombre_r", Conf.getNomResi().trim());
            values.put("estatus", 1);
            if (status.equals("0")){
                values.put("sqliteEstatus", 2);
            }else{
                values.put("sqliteEstatus", 1);
            }

            actualizar = getContentResolver().update(UrisContentProvider.URI_CONTENIDO_CORRESPONDENCIA, values, "id = "+ ja1.getString(0), null);

            if (actualizar != -1){
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EntregaActivity.this);
                alertDialogBuilder.setTitle("Alerta");
                alertDialogBuilder
                        .setMessage("Entrega exitosa en modo offline")
                        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(getApplicationContext(), CorrespondenciaActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }).create().show();


            }else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EntregaActivity.this);
                alertDialogBuilder.setTitle("Alerta");
                alertDialogBuilder
                        .setMessage("Entrega no exitosa en modo offline")
                        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(getApplicationContext(), EntradasSalidasActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }).create().show();
            }

        }catch (Exception ex){
            Log.e("exception reg", ex.toString());
        }


    }

    public ContentValues ValuesImagen(String nombre, String rutaFirebase, String rutaDispositivo){
        ContentValues values = new ContentValues();
        values.put("titulo", nombre);
        values.put("direccionFirebase", rutaFirebase);
        values.put("rutaDispositivo", rutaDispositivo);
        return values;
    }

    public void Registrar(){

        String url = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/correspondencia_7.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response){


                if(response.equals("error")){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EntregaActivity.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("Entrega No Exitosa")
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(getApplicationContext(), EntradasSalidasActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            }).create().show();
                }else {

                        upload1();

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
                    fotos="app"+ja1.getString(2)+"-"+numero_aletorio+".png";
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                Map<String, String> params = new HashMap<>();
                params.put("Folio",Conf.getPlacas());
                params.put("Foto",fotos );
                params.put("id_residencial", Conf.getResid().trim());
                try {
                    params.put("token", ja2.getString(1));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("nom_residencial",Conf.getNomResi().trim());

                return params;
            }
        };
        requestQueue.add(stringRequest);


    }



    public void upload1(){

        StorageReference mountainImagesRef = null;
        try {
            mountainImagesRef = storageReference.child(Conf.getPin()+"/correspondencia/app"+ja1.getString(2)+"-"+numero_aletorio+".png");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        UploadTask uploadTask = mountainImagesRef.putFile(uri_img);


        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                pd.show(); // double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                //System.out.println("Upload is " + progress + "% done");
                // Toast.makeText(getApplicationContext(),"Cargando Imagen INE " + progress + "%", Toast.LENGTH_SHORT).show();

            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(AccesoActivity.this,"Pausado",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(EntregaActivity.this,"Fallado", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();
                terminar();
            }
        });
    }
    public void terminar() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(EntregaActivity.this);
        alertDialogBuilder.setTitle("Alerta");
        alertDialogBuilder
                .setMessage("Entrega Exitosa")
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(getApplicationContext(), CorrespondenciaActivity.class);
                        startActivity(i);
                        finish();
                    }
                }).create().show();
    }



    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), CorrespondenciaActivity.class);
        startActivity(intent);
        finish();

    }

}
