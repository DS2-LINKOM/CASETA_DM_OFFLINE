package mx.linkom.caseta_dm_offline;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import id.zelory.compressor.Compressor;
import mx.linkom.caseta_dm_offline.offline.Database.Database;
import mx.linkom.caseta_dm_offline.offline.Database.UrisContentProvider;
import mx.linkom.caseta_dm_offline.offline.Global_info;


public class IncidenciaActivity  extends mx.linkom.caseta_dm_offline.Menu{

    Button foto1_boton,foto2_boton,foto3_boton;
    int foto;
    ImageView view_foto1,view_foto2,view_foto3;
    Bitmap bitmap,bitmap2,bitmap3;
    Button btnContinuar,btnContinuar2,btnContinuar3,btnContinuar4,btnContinuar5,btnContinuar6;
    LinearLayout Viewfoto1,Viewfoto2,Viewfoto3;
    LinearLayout registrar1,registrar2,registrar3,registrar4;
    LinearLayout foto2,foto3,espacio1,espacio2,espacio3,espacio4,espacio5,espacio6,espacio7;
    String ima1,ima2,ima3;
    ProgressDialog pd,pd2,pd3,pd4;
    FirebaseStorage storage;
    StorageReference storageReference;
    Configuracion Conf;
    EditText Comentarios,Accion;
    Uri uri_img,uri_img2,uri_img3;
    String rutaImagen1, rutaImagen2, rutaImagen3;
    boolean Offline = false;
    ImageView iconoInternet;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incidencia);

        storage= FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        Conf = new Configuracion(this);

        Comentarios = (EditText) findViewById(R.id.setComen);
        Accion = (EditText) findViewById(R.id.setAccion);


        foto1_boton = (Button) findViewById(R.id.foto1_boton);
        foto2_boton = (Button) findViewById(R.id.foto2_boton);
        foto3_boton = (Button) findViewById(R.id.foto3_boton);

        registrar1 = (LinearLayout) findViewById(R.id.registrar1);
        registrar2 = (LinearLayout) findViewById(R.id.registrar2);
        registrar3 = (LinearLayout) findViewById(R.id.registrar3);
        registrar4 = (LinearLayout) findViewById(R.id.registrar4);
        espacio1 = (LinearLayout) findViewById(R.id.espacio1);
        espacio2 = (LinearLayout) findViewById(R.id.espacio2);
        espacio3 = (LinearLayout) findViewById(R.id.espacio3);
        espacio4 = (LinearLayout) findViewById(R.id.espacio4);
        espacio5 = (LinearLayout) findViewById(R.id.espacio5);
        espacio6 = (LinearLayout) findViewById(R.id.espacio6);
        espacio7 = (LinearLayout) findViewById(R.id.espacio7);

        Viewfoto1 = (LinearLayout) findViewById(R.id.Viewfoto1);
        view_foto1 = (ImageView) findViewById(R.id.view_foto1);
        Viewfoto2 = (LinearLayout) findViewById(R.id.Viewfoto2);
        view_foto2 = (ImageView) findViewById(R.id.view_foto2);
        Viewfoto3 = (LinearLayout) findViewById(R.id.Viewfoto3);
        view_foto3 = (ImageView) findViewById(R.id.view_foto3);

        btnContinuar = (Button) findViewById(R.id.btnContinuar);
        btnContinuar2 = (Button) findViewById(R.id.btnContinuar2);
        btnContinuar3 = (Button) findViewById(R.id.btnContinuar3);
        btnContinuar4 = (Button) findViewById(R.id.btnContinuar4);
        btnContinuar5 = (Button) findViewById(R.id.btnContinuar5);
        btnContinuar6 = (Button) findViewById(R.id.btnContinuar6);

        foto2 = (LinearLayout) findViewById(R.id.foto2);
        foto3 = (LinearLayout) findViewById(R.id.foto3);

        iconoInternet = (ImageView)findViewById(R.id.iconoInternetIncidencia);

        if (Global_info.getINTERNET().equals("Si")){
            //Es online
            Offline = false;
            iconoInternet.setImageResource(R.drawable.ic_online);
        }else {
            //Es offline
            iconoInternet.setImageResource(R.drawable.ic_offline);
            Offline = true;
        }

        iconoInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Offline){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(IncidenciaActivity.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("Aplicación funcionando en modo offline \n\nDatos actualizados hasta: \n\n"+Global_info.getULTIMA_ACTUALIZACION())
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            }).create().show();
                }else {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(IncidenciaActivity.this);
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

        foto1_boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foto=1;
                if (Offline){
                    imgFotoOffline();

                }else {
                    imgFoto();
                }
            }
        });


        foto2_boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foto=2;
                if (Offline){
                    imgFoto2Offline();
                }else {
                    imgFoto2();
                }
            }
        });

        foto3_boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foto=3;
                if (Offline){
                    imgFoto3Offline();
                }else {
                    imgFoto3();
                }
            }
        });

        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validacion(1);
            }
        });

        btnContinuar2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foto=2;
                if (Offline){
                    imgFoto2Offline();
                }else {
                    imgFoto2();
                }
                registrar2.setVisibility(View.GONE);
                foto2.setVisibility(View.VISIBLE);
            }
        });

        btnContinuar3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validacion(2);
            }
        });

        btnContinuar4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foto=3;
                if (Offline){
                    imgFoto3Offline();
                }else {
                    imgFoto3();
                }
                registrar3.setVisibility(View.GONE);
                foto3.setVisibility(View.VISIBLE);
            }
        });

        btnContinuar5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validacion(3);
            }
        });

        btnContinuar6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validacion(4);
            }
        });

        pd= new ProgressDialog(this);
        pd.setMessage("Subiendo Foto 1...");

        pd2= new ProgressDialog(this);
        pd2.setMessage("Subiendo Foto 2...");

        pd3= new ProgressDialog(this);
        pd3.setMessage("Subiendo Foto 3...");

        pd4= new ProgressDialog(this);
        pd4.setMessage("Cargando...");

    }

    InputFilter filter = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                if (Character.isSpaceChar(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        }
    };

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

    //ALETORIO2

    Random primero2 = new Random();
    int prime2= primero2.nextInt(9);

    String[] segundo2 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandonsegun2 = (int) Math.round(Math.random() * 25 ) ;

    Random tercero2 = new Random();
    int tercer2= tercero2.nextInt(9);

    String[] cuarto2 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandoncuart2 = (int) Math.round(Math.random() * 25 ) ;

    String numero_aletorio2=prime2+segundo2[numRandonsegun2]+tercer2+cuarto2[numRandoncuart2];

    //ALETORIO3

    Random primero3 = new Random();
    int prime3= primero3.nextInt(9);

    String[] segundo3 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandonsegun3 = (int) Math.round(Math.random() * 25 ) ;

    Random tercero3 = new Random();
    int tercer3= tercero3.nextInt(9);

    String[] cuarto3 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandoncuart3 = (int) Math.round(Math.random() * 25 ) ;

    String numero_aletorio3=prime3+segundo3[numRandonsegun3]+tercer3+cuarto3[numRandoncuart3];


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
                foto= new File(getApplication().getExternalFilesDir(null),"incidencia1.png");
                rutaImagen1 = foto.getAbsolutePath();
            } catch (Exception ex) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(IncidenciaActivity.this);
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
                foto= new File(getApplication().getExternalFilesDir(null),"app"+numero_aletorio+numero_aletorio3+".png");
                rutaImagen1 = foto.getAbsolutePath();
            } catch (Exception ex) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(IncidenciaActivity.this);
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


    public void imgFoto2(){
        Intent intentCaptura = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCaptura.addFlags(intentCaptura.FLAG_GRANT_READ_URI_PERMISSION);

        if (intentCaptura.resolveActivity(getPackageManager()) != null) {
            File foto=null;
            try {
                foto = new File(getApplication().getExternalFilesDir(null),"incidencia2.png");
                rutaImagen2 = foto.getAbsolutePath();
            } catch (Exception ex) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(IncidenciaActivity.this);
                alertDialogBuilder.setTitle("Alerta");
                alertDialogBuilder
                        .setMessage("Error al capturar la foto")
                        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        }).create().show();
            }
            if (foto != null) {
                uri_img2= FileProvider.getUriForFile(getApplicationContext(),getApplicationContext().getPackageName()+".provider",foto);
                intentCaptura.putExtra(MediaStore.EXTRA_OUTPUT,uri_img2);
                startActivityForResult( intentCaptura, 1);
            }
        }
    }

    public void imgFoto2Offline(){
        Intent intentCaptura = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCaptura.addFlags(intentCaptura.FLAG_GRANT_READ_URI_PERMISSION);

        if (intentCaptura.resolveActivity(getPackageManager()) != null) {
            File foto=null;
            try {
                foto = new File(getApplication().getExternalFilesDir(null),"app"+numero_aletorio2+numero_aletorio+".png");
                rutaImagen2 = foto.getAbsolutePath();
            } catch (Exception ex) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(IncidenciaActivity.this);
                alertDialogBuilder.setTitle("Alerta");
                alertDialogBuilder
                        .setMessage("Error al capturar la foto")
                        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        }).create().show();
            }
            if (foto != null) {
                uri_img2= FileProvider.getUriForFile(getApplicationContext(),getApplicationContext().getPackageName()+".provider",foto);
                intentCaptura.putExtra(MediaStore.EXTRA_OUTPUT,uri_img2);
                startActivityForResult( intentCaptura, 1);
            }
        }
    }


    public void imgFoto3(){
        Intent intentCaptura = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCaptura.addFlags(intentCaptura.FLAG_GRANT_READ_URI_PERMISSION);

        if (intentCaptura.resolveActivity(getPackageManager()) != null) {

            File foto=null;
            try {
                foto = new File(getApplication().getExternalFilesDir(null),"incidencia3.png");
                rutaImagen3 = foto.getAbsolutePath();
            } catch (Exception ex) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(IncidenciaActivity.this);
                alertDialogBuilder.setTitle("Alerta");
                alertDialogBuilder
                        .setMessage("Error al capturar la foto")
                        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        }).create().show();
            }
            if (foto != null) {
                uri_img3= FileProvider.getUriForFile(getApplicationContext(),getApplicationContext().getPackageName()+".provider",foto);
                intentCaptura.putExtra(MediaStore.EXTRA_OUTPUT,uri_img3);
                startActivityForResult( intentCaptura, 2);
            }
        }
    }


    public void imgFoto3Offline(){
        Intent intentCaptura = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCaptura.addFlags(intentCaptura.FLAG_GRANT_READ_URI_PERMISSION);

        if (intentCaptura.resolveActivity(getPackageManager()) != null) {

            File foto=null;
            try {
                foto = new File(getApplication().getExternalFilesDir(null),"app"+numero_aletorio3+numero_aletorio2+".png");
                rutaImagen3 = foto.getAbsolutePath();
            } catch (Exception ex) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(IncidenciaActivity.this);
                alertDialogBuilder.setTitle("Alerta");
                alertDialogBuilder
                        .setMessage("Error al capturar la foto")
                        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        }).create().show();
            }
            if (foto != null) {
                uri_img3= FileProvider.getUriForFile(getApplicationContext(),getApplicationContext().getPackageName()+".provider",foto);
                intentCaptura.putExtra(MediaStore.EXTRA_OUTPUT,uri_img3);
                startActivityForResult( intentCaptura, 2);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {

                Bitmap bitmap;

                if (Offline){
                    bitmap = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(null) + "/app"+numero_aletorio+numero_aletorio3+".png");
                }else {
                    bitmap = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(null) + "/incidencia1.png");
                }


                registrar1.setVisibility(View.GONE);
                Viewfoto1.setVisibility(View.VISIBLE);
                view_foto1.setVisibility(View.VISIBLE);
                view_foto1.setImageBitmap(bitmap);
                registrar2.setVisibility(View.VISIBLE);
                espacio1.setVisibility(View.VISIBLE);
                espacio2.setVisibility(View.VISIBLE);


            }
            if (requestCode == 1) {
                Bitmap bitmap2;

                if (Offline){
                    bitmap2 = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(null) + "/app"+numero_aletorio2+numero_aletorio+".png");
                }else {
                    bitmap2 = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(null) + "/incidencia2.png");
                }


                Viewfoto2.setVisibility(View.VISIBLE);
                view_foto2.setVisibility(View.VISIBLE);
                view_foto2.setImageBitmap(bitmap2);
                registrar3.setVisibility(View.VISIBLE);
                espacio2.setVisibility(View.GONE);
                espacio3.setVisibility(View.VISIBLE);
                espacio4.setVisibility(View.VISIBLE);

            }

            if (requestCode == 2) {

                Bitmap bitmap3;

                if (Offline){
                    bitmap3 = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(null) + "/app"+numero_aletorio3+numero_aletorio2+".png");
                }else {
                    bitmap3 = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(null) + "/incidencia3.png");
                }


                Viewfoto3.setVisibility(View.VISIBLE);
                view_foto3.setVisibility(View.VISIBLE);
                view_foto3.setImageBitmap(bitmap3);
                registrar4.setVisibility(View.VISIBLE);

                espacio4.setVisibility(View.GONE);
                espacio5.setVisibility(View.VISIBLE);
                espacio6.setVisibility(View.VISIBLE);
                espacio7.setVisibility(View.VISIBLE);



            }
        }
    }



    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    public void Validacion(final int Ids) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(IncidenciaActivity.this);
        alertDialogBuilder.setTitle("Alerta");
        alertDialogBuilder
                .setMessage("¿ Desea registrar la incidencia ?")
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    public void onClick(DialogInterface dialog, int id) {
                        //Validar conexión a internet
                        if (Offline){
                            pd4.show();
                            RegistrarOffline(Ids);
                        }else{
                            Registrar(Ids);
                        }
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        Intent i = new Intent(getApplicationContext(), ReportesActivity.class);
                        startActivity(i);
                        finish();
                    }
                }).create().show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void RegistrarOffline(final int Id){

        try {

            long id = 0;

            String nombre1 = "app"+numero_aletorio+numero_aletorio3+".png";
            String nombre2 = "app"+numero_aletorio2+numero_aletorio+".png";
            String nombre3 = "app"+numero_aletorio3+numero_aletorio2+".png";

            //Registrar fotos en SQLite
            switch (Id){
                case 2:
                    ima1=nombre1;
                    ima2="";
                    ima3="";

                    //GuardarImagen(nombre1, rutaImagen1, uri_img);

                    ContentValues val_img1 =  ValuesImagen(ima1, Conf.getPin()+"/incidencias/"+ima1.trim(), rutaImagen1);
                    Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_FOTOS_OFFLINE, val_img1);


                    break;
                case 3:
                    ima1=nombre1;
                    ima2=nombre2;
                    ima3="";

                    /*GuardarImagen(nombre1, rutaImagen1, uri_img);
                    GuardarImagen(nombre2, rutaImagen2, uri_img2);*/

                    ContentValues val_img2 =  ValuesImagen(ima1, Conf.getPin()+"/incidencias/"+ima1.trim(), rutaImagen1);
                    Uri uri2 = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_FOTOS_OFFLINE, val_img2);

                    ContentValues val_img3 =  ValuesImagen(ima2, Conf.getPin()+"/incidencias/"+ima2.trim(), rutaImagen2);
                    Uri uri3 = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_FOTOS_OFFLINE, val_img3);

                    break;
                case 4:
                    ima1=nombre1;
                    ima2=nombre2;
                    ima3=nombre3;

                    /*GuardarImagen(nombre1, rutaImagen1, uri_img);
                    GuardarImagen(nombre2, rutaImagen2, uri_img2);
                    GuardarImagen(nombre3, rutaImagen3, uri_img3);*/

                    ContentValues val_img4 =  ValuesImagen(ima1, Conf.getPin()+"/incidencias/"+ima1.trim(), rutaImagen1);
                    Uri uri4 = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_FOTOS_OFFLINE, val_img4);

                    ContentValues val_img5 =  ValuesImagen(ima2, Conf.getPin()+"/incidencias/"+ima2.trim(), rutaImagen2);
                    Uri uri5 = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_FOTOS_OFFLINE, val_img5);

                    ContentValues val_img6 =  ValuesImagen(ima3, Conf.getPin()+"/incidencias/"+ima3.trim(), rutaImagen3);
                    Uri uri6 = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_FOTOS_OFFLINE, val_img6);
                    break;
                default:
                    ima1="";
                    ima2="";
                    ima3="";
                    break;
            }



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

            ContentValues values = new ContentValues();
            values.put("id_residencial", Conf.getResid().trim());
            values.put("id_usuario", Conf.getUsu().trim());
            values.put("id_tipo", 0);
            values.put("id_rondin", 0);
            values.put("dia", fecha);
            values.put("hora", hora);
            values.put("detalle", Comentarios.getText().toString().trim());
            values.put("accion", Accion.getText().toString().trim());
            values.put("foto1", ima1);
            values.put("foto2", ima2);
            values.put("foto3", ima3);
            values.put("club", 0);
            values.put("estatus", 1);
            values.put("sqliteEstatus", 1);

            Uri uri = getContentResolver().insert(UrisContentProvider.URI_CONTENIDO_INCIDENCIAS, values);

            String idUri = uri.getLastPathSegment();

            id = Integer.parseInt(idUri);


            if (id != -1){
                pd4.dismiss();
                //Toast.makeText(this, "Registro correcto en modo offline", Toast.LENGTH_SHORT).show();
                //Toast.makeText(this, Global_info.getULTIMA_ACTUALIZACION(), Toast.LENGTH_SHORT).show();

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(IncidenciaActivity.this);
                alertDialogBuilder.setTitle("Alerta");
                alertDialogBuilder
                        .setMessage("Registro de incidencia exitosa en modo offline ")
                        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(getApplicationContext(), ReportesActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }).create().show();


            }else{
                pd4.dismiss();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(IncidenciaActivity.this);
                alertDialogBuilder.setTitle("Alerta");
                alertDialogBuilder
                        .setMessage("Registro de incidencia no exitoso en modo offline")
                        .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(getApplicationContext(), ReportesActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }).create().show();
                //Toast.makeText(this, "Error al registrar en modo offline", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception ex){
            System.out.println(ex.toString());
        }

    }

    public ContentValues ValuesImagen(String nombre, String rutaFirebase, String rutaDispositivo){
        ContentValues values = new ContentValues();
        values.put("titulo", nombre);
        values.put("direccionFirebase", rutaFirebase);
        values.put("rutaDispositivo", rutaDispositivo);
        return values;
    }

    public void GuardarImagen(String nombreImagen, String direccionImagen, Uri uriImagen) {
        try {

            // uri de la imagen seleccionada
            Uri uri = uriImagen;
            //carpeta "imagenesguardadas"
            String rutacarpeta = getApplication().getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/";
            // nombre del nuevo png
            String nombre = nombreImagen;
            // Compruebas si existe la carpeta "PICTURES", sino, la crea
            File directorioImagenes = new File(rutacarpeta);
            if (!directorioImagenes.exists())
                directorioImagenes.mkdirs();

            // le pasas al bitmap la uri de la imagen seleccionada
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);


            // pones las medidas que quieras del nuevo .png
            int bitmapWidth = bitmap.getWidth(); // para utilizar width de la imagen original: bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight(); // para utilizar height de la imagen original: bitmap.getHeight();
            Bitmap bitmapout = Bitmap.createScaledBitmap(bitmap, bitmapWidth, bitmapHeight, false);
            //creas el nuevo png en la nueva ruta
            bitmapout.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(rutacarpeta + nombre));


            /*ExifInterface exif = new ExifInterface(rutacarpeta+nombreImagen);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            Matrix matrix = new Matrix();
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
                default:
                    break;
            }*/

        }catch (Exception ex){
            System.out.println(ex.toString());
        }
    }

    public void Registrar(final int Id){

        String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/incidencias.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response){


                if(response.equals("error")){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(IncidenciaActivity.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("Registro de Incidencia No Exitoso")
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(getApplicationContext(), ReportesActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            }).create().show();
                }else {

                    if(Id==1){

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(IncidenciaActivity.this);
                        alertDialogBuilder.setTitle("Alerta");
                        alertDialogBuilder
                                .setMessage("Registro de Incidencia Exitosa")
                                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent i = new Intent(getApplicationContext(), ReportesActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                }).create().show();

                    }else if(Id==2){
                        upload1();
                        terminar();
                    }else if(Id==3){
                        upload1();
                        upload2();
                        terminar();
                    }else if(Id==4){
                        upload1();
                        upload2();
                        upload3();
                        terminar();
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

                if(Id==1){
                    ima1="";
                    ima2="";
                    ima3="";
                }else if(Id==2){
                    ima1="app"+numero_aletorio+numero_aletorio3+".png";
                    ima2="";
                    ima3="";
                }else if(Id==3){
                    ima1="app"+numero_aletorio+numero_aletorio3+".png";
                    ima2="app"+numero_aletorio2+numero_aletorio+".png";
                    ima3="";
                }else if(Id==4){
                    ima1="app"+numero_aletorio+numero_aletorio3+".png";
                    ima2="app"+numero_aletorio2+numero_aletorio+".png";
                    ima3="app"+numero_aletorio3+numero_aletorio2+".png";
                }

                params.put("id_residencial", Conf.getResid().trim());
                params.put("id", Conf.getUsu().trim());
                params.put("Comentario", Comentarios.getText().toString().trim());
                params.put("Accion", Accion.getText().toString().trim());
                params.put("foto1", ima1);
                params.put("foto2", ima2);
                params.put("foto3", ima3);


                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void upload1() {
        StorageReference mountainImagesRef = null;
        mountainImagesRef = storageReference.child(Conf.getPin()+"/incidencias/app"+numero_aletorio+numero_aletorio3+".png");

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
                Toast.makeText(IncidenciaActivity.this,"Fallado", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();

            }
        });
    }
    public void upload2() {

        StorageReference mountainImagesRef2 = null;
        mountainImagesRef2 = storageReference.child(Conf.getPin()+"/incidencias/app"+numero_aletorio2+numero_aletorio+".png");

        UploadTask uploadTask = mountainImagesRef2.putFile(uri_img2);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                // double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                //System.out.println("Upload is " + progress + "% done");
                //Toast.makeText(getApplicationContext(),"Cargando Imagen PLACA " + progress + "%", Toast.LENGTH_SHORT).show();
                pd2.show();
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(AccesoActivity.this,"Pausado",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(IncidenciaActivity.this,"Fallado", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd2.dismiss();




            }
        });
    }
    public void upload3() {
        StorageReference mountainImagesRef3 = null;
        mountainImagesRef3 = storageReference.child(Conf.getPin()+"/incidencias/app"+numero_aletorio3+numero_aletorio2+".png");

        UploadTask uploadTask = mountainImagesRef3.putFile(uri_img3);

        // Listen for state changes, errors, and completion of the upload.
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                // double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                //System.out.println("Upload is " + progress + "% done");
                //Toast.makeText(getApplicationContext(),"Cargando Imagen PLACA " + progress + "%", Toast.LENGTH_SHORT).show();
                pd3.show();
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                //Toast.makeText(AccesoActivity.this,"Pausado",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(IncidenciaActivity.this,"Fallado", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd3.dismiss();

            }
        });
    }

    private void terminar() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(IncidenciaActivity.this);
        alertDialogBuilder.setTitle("Alerta");
        alertDialogBuilder
                .setMessage("Registro de Incidencia Exitosa")
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(getApplicationContext(), ReportesActivity.class);
                        startActivity(i);
                        finish();
                    }
                }).create().show();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), ReportesActivity.class);
        startActivity(intent);
        finish();
    }

}
