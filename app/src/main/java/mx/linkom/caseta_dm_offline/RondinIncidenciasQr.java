package mx.linkom.caseta_dm_offline;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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


import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import mx.linkom.caseta_dm_offline.Menu;


public class RondinIncidenciasQr extends Menu {

    Button foto1_boton,foto2_boton,foto3_boton;
    int foto;
    ImageView view_foto1,view_foto2,view_foto3;
    Bitmap bitmap,bitmap2,bitmap3;
    Button btnContinuar,btnContinuar2,btnContinuar3,btnContinuar4,btnContinuar5,btnContinuar6;
    LinearLayout Viewfoto1,Viewfoto2,Viewfoto3;
    LinearLayout registrar1,registrar2,registrar3,registrar4;
    LinearLayout foto2,foto3,espacio1,espacio2,espacio3,espacio4,espacio5,espacio6,espacio7;
    String ima1,ima2,ima3;
    ProgressDialog pd,pd2,pd3;
    FirebaseStorage storage;
    StorageReference storageReference;
    Configuracion Conf;
    EditText Comentarios,Accion;
    Uri uri_img,uri_img2,uri_img3;
    JSONArray ja1;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rondinincidencias_qr);

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


        foto1_boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foto=1;
                imgFoto();
            }
        });


        foto2_boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foto=2;
                imgFoto2();
            }
        });

        foto3_boton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foto=3;
                imgFoto3();
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
                imgFoto2();
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
                imgFoto3();
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
        rondin();
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
    int numRandonsegun = (int) Math.round(Math.random() * 26 ) ;

    Random tercero = new Random();
    int tercer= tercero.nextInt(9);

    String[] cuarto = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandoncuart = (int) Math.round(Math.random() * 26 ) ;

    String numero_aletorio=prime+segundo[numRandonsegun]+tercer+cuarto[numRandoncuart];

    //ALETORIO2

    Random primero2 = new Random();
    int prime2= primero2.nextInt(9);

    String[] segundo2 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandonsegun2 = (int) Math.round(Math.random() * 26 ) ;

    Random tercero2 = new Random();
    int tercer2= tercero2.nextInt(9);

    String[] cuarto2 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandoncuart2 = (int) Math.round(Math.random() * 26 ) ;

    String numero_aletorio2=prime2+segundo2[numRandonsegun2]+tercer2+cuarto2[numRandoncuart2];

    //ALETORIO3

    Random primero3 = new Random();
    int prime3= primero3.nextInt(9);

    String[] segundo3 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandonsegun3 = (int) Math.round(Math.random() * 26 ) ;

    Random tercero3 = new Random();
    int tercer3= tercero3.nextInt(9);

    String[] cuarto3 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandoncuart3 = (int) Math.round(Math.random() * 26 ) ;

    String numero_aletorio3=prime3+segundo3[numRandonsegun3]+tercer3+cuarto3[numRandoncuart3];


    Calendar fecha = Calendar.getInstance();
    int anio = fecha.get(Calendar.YEAR);
    int mes = fecha.get(Calendar.MONTH) + 1;
    int dia = fecha.get(Calendar.DAY_OF_MONTH);

    public void rondin() {
        String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/rondines_qr_2.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {
                        ja1 = new JSONArray(response);
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
                params.put("id", Conf.getRondin().trim());
                params.put("guardia_de_entrada", Conf.getUsu().trim());
                params.put("id_residencial", Conf.getResid().trim());
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }


    //IMAGEN FOTO

    //FOTOS

    public void imgFoto(){
        Intent intentCaptura = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCaptura.addFlags(intentCaptura.FLAG_GRANT_READ_URI_PERMISSION);

        if (intentCaptura.resolveActivity(getPackageManager()) != null) {

            File foto=null;
            try {
                foto= new File(getApplication().getExternalFilesDir(null),"rondines1.png");
            } catch (Exception ex) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RondinIncidenciasQr.this);
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
                foto = new File(getApplication().getExternalFilesDir(null),"rondines2.png");
            } catch (Exception ex) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RondinIncidenciasQr.this);
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
                foto = new File(getApplication().getExternalFilesDir(null),"rondines3.png");
            } catch (Exception ex) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RondinIncidenciasQr.this);
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


                Bitmap bitmap = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(null) + "/rondines1.png");

                registrar1.setVisibility(View.GONE);
                Viewfoto1.setVisibility(View.VISIBLE);
                view_foto1.setVisibility(View.VISIBLE);
                view_foto1.setImageBitmap(bitmap);
                registrar2.setVisibility(View.VISIBLE);
                espacio1.setVisibility(View.VISIBLE);
                espacio2.setVisibility(View.VISIBLE);

            }
            if (requestCode == 1) {


                Bitmap bitmap2 = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(null) + "/rondines2.png");

                Viewfoto2.setVisibility(View.VISIBLE);
                view_foto2.setVisibility(View.VISIBLE);
                view_foto2.setImageBitmap(bitmap2);
                registrar3.setVisibility(View.VISIBLE);
                espacio2.setVisibility(View.GONE);
                espacio3.setVisibility(View.VISIBLE);
                espacio4.setVisibility(View.VISIBLE);

            }

            if (requestCode == 2) {


                Bitmap bitmap3 = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(null) + "/rondines3.png");

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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RondinIncidenciasQr.this);
        alertDialogBuilder.setTitle("Alerta");
        alertDialogBuilder
                .setMessage("¿ Desea registrar la incidencia ?")
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Registrar(Ids);
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

    public void Registrar(final int Id){

        String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/rondines_incidencias.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response){


                if(response.equals("error")){
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RondinIncidenciasQr.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("Registro de Incidencia No Exitoso")
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(getApplicationContext(), Rondines.class);
                                    startActivity(i);
                                    finish();
                                }
                            }).create().show();
                }else {

                    if(Id==1){

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RondinIncidenciasQr.this);
                        alertDialogBuilder.setTitle("Alerta");
                        alertDialogBuilder
                                .setMessage("Registro de Incidencia Exitosa")
                                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent i = new Intent(getApplicationContext(), Rondines.class);
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
                params.put("id_usuario", Conf.getUsu().trim());
                params.put("Comentario", Comentarios.getText().toString().trim());
                params.put("Accion", Accion.getText().toString().trim());
                params.put("foto1", ima1);
                params.put("foto2", ima2);
                params.put("foto3", ima3);
                params.put("foto3", ima3);
                try {
                    params.put("id_rondin", ja1.getString(5));
                    params.put("id_ubicacion", ja1.getString(0));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("id_tipo","1");



                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void upload1() {
        StorageReference mountainImagesRef = null;
        mountainImagesRef = storageReference.child(Conf.getPin()+"/incidencias/app"+numero_aletorio+numero_aletorio3+".png");

        final UploadTask uploadTask = mountainImagesRef.putFile(uri_img);


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
                Toast.makeText(RondinIncidenciasQr.this,"Fallado", Toast.LENGTH_SHORT).show();
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

        final UploadTask uploadTask = mountainImagesRef2.putFile(uri_img2);

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
                Toast.makeText(RondinIncidenciasQr.this,"Fallado", Toast.LENGTH_SHORT).show();
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

        final UploadTask uploadTask = mountainImagesRef3.putFile(uri_img3);

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
                Toast.makeText(RondinIncidenciasQr.this,"Fallado", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd3.dismiss();

            }
        });
    }

    private void terminar() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RondinIncidenciasQr.this);
        alertDialogBuilder.setTitle("Alerta");
        alertDialogBuilder
                .setMessage("Registro de Incidencia Exitosa")
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(getApplicationContext(), Rondines.class);
                        startActivity(i);
                        finish();
                    }
                }).create().show();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), Rondines.class);
        startActivity(intent);
        finish();
    }

}
