package mx.linkom.caseta_dm_offline;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
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
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.io.File;
import java.lang.reflect.Modifier;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RegTrab3Activity extends mx.linkom.caseta_dm_offline.Menu {

    SearchView buscador;
    GridView gridList;
    Configuracion Conf;
    JSONArray ja1;
    Button Eliminar;
    TextView Tipo,Depa;
    EditText Nombre,Tel,Correo,Coment,Puesto,Direccion;
    FirebaseStorage storage;
    StorageReference storageReference;
    ImageView view1,view2,view3;
    LinearLayout Foto1View,espacio2;
    LinearLayout Foto2View,espacio4;
    LinearLayout Foto3View,espacio7;

    Button btn_foto1,btn_foto2,btn_foto3,Modificar;
    Uri uri_img,uri_img2,uri_img3;
    int foto1,foto2,foto3;
    String nfoto1,nfoto2,nfoto3;
    ProgressDialog pd,pd2,pd3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regtrab3);

        storage= FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        Conf = new Configuracion(this);
        Eliminar = (Button) findViewById(R.id.btn_eliminar);
        Tipo = (TextView) findViewById(R.id.setTipo);
        Depa = (TextView) findViewById(R.id.setDepa);
        Nombre = (EditText) findViewById(R.id.setNombre);
        Puesto = (EditText) findViewById(R.id.setPuesto);
        Direccion = (EditText) findViewById(R.id.setDire);
        Tel = (EditText) findViewById(R.id.setTel);
        Correo= (EditText) findViewById(R.id.setCorreo);
        Coment = (EditText) findViewById(R.id.setComen);
        view1 = (ImageView) findViewById(R.id.view1);
        view2 = (ImageView) findViewById(R.id.view2);
        view3= (ImageView) findViewById(R.id.view3);
        Foto1View = (LinearLayout) findViewById(R.id.Foto1View);
        espacio2 = (LinearLayout) findViewById(R.id.espacio2);
        Foto2View = (LinearLayout) findViewById(R.id.Foto2View);
        espacio4 = (LinearLayout) findViewById(R.id.espacio4);
        Foto3View = (LinearLayout) findViewById(R.id.Foto3View);
        espacio7 = (LinearLayout) findViewById(R.id.espacio7);
        Modificar = (Button) findViewById(R.id.modificar);
        btn_foto1 = (Button) findViewById(R.id.btn_foto1);
        btn_foto2 = (Button) findViewById(R.id.btn_foto2);
        btn_foto3 = (Button) findViewById(R.id.btn_foto3);
        Datos();
        Eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidacionE();
            }
        });

        Modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidacionM();
            }
        });


        btn_foto1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foto1=1;
                imgFoto1();

            }
        });

        btn_foto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foto2=2;
                imgFoto2();
            }
        });

        btn_foto3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foto3=3;
                imgFoto3();
            }
        });
        pd= new ProgressDialog(this);
        pd.setMessage("Subiendo Imagen 1...");

        pd2= new ProgressDialog(this);
        pd2.setMessage("Subiendo Imagen 2...");

        pd3= new ProgressDialog(this);
        pd3.setMessage("Subiendo Imagen 3...");
    }

    //ALETORIO
    Random primero = new Random();
    int prime= primero.nextInt(9);

    String [] segundo = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandonsegun = (int) Math.round(Math.random() * 26 ) ;

    Random tercero = new Random();
    int tercer= tercero.nextInt(9);

    String [] cuarto = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandoncuart = (int) Math.round(Math.random() * 26 ) ;

    String numero_aletorio=prime+segundo[numRandonsegun]+tercer+cuarto[numRandoncuart];

    //ALETORIO2

    Random primero2 = new Random();
    int prime2= primero2.nextInt(9);

    String [] segundo2 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandonsegun2 = (int) Math.round(Math.random() * 26 ) ;

    Random tercero2 = new Random();
    int tercer2= tercero2.nextInt(9);

    String [] cuarto2 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandoncuart2 = (int) Math.round(Math.random() * 26 ) ;

    String numero_aletorio2=prime2+segundo2[numRandonsegun2]+tercer2+cuarto2[numRandoncuart2];


    //ALETORIO3

    Random primero3 = new Random();
    int prime3= primero3.nextInt(9);

    String [] segundo3 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandonsegun3 = (int) Math.round(Math.random() * 26 ) ;

    Random tercero3 = new Random();
    int tercer3= tercero3.nextInt(9);

    String [] cuarto3 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandoncuart3 = (int) Math.round(Math.random() * 26 ) ;

    String numero_aletorio3=prime3+segundo3[numRandonsegun3]+tercer3+cuarto3[numRandoncuart3];


    Calendar fecha = Calendar.getInstance();
    int anio = fecha.get(Calendar.YEAR);
    int mes = fecha.get(Calendar.MONTH) + 1;
    int dia = fecha.get(Calendar.DAY_OF_MONTH);


    //FOTOS

    public void imgFoto1(){
        Intent intentCaptura = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCaptura.addFlags(intentCaptura.FLAG_GRANT_READ_URI_PERMISSION);

        if (intentCaptura.resolveActivity(getPackageManager()) != null) {

            File foto=null;
            try {
                foto= new File(getApplication().getExternalFilesDir(null),"ft1.png");
            } catch (Exception ex) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrab3Activity.this);
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
                foto = new File(getApplication().getExternalFilesDir(null),"ft2.png");
            } catch (Exception ex) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrab3Activity.this);
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
                foto = new File(getApplication().getExternalFilesDir(null),"ft3.png");
            } catch (Exception ex) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrab3Activity.this);
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


                Bitmap bitmap = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(null) + "/ft1.png");

                view1.setVisibility(View.VISIBLE);
                view1.setImageBitmap(bitmap);


            }
            if (requestCode == 1) {

                Bitmap bitmap2 = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(null) + "/ft2.png");
                Foto2View.setVisibility(View.VISIBLE);
                view2.setVisibility(View.VISIBLE);
                view2.setImageBitmap(bitmap2);
                espacio4.setVisibility(View.VISIBLE);

            }

            if (requestCode == 2) {

                Bitmap bitmap3 = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(null) + "/ft3.png");
                Foto3View.setVisibility(View.VISIBLE);
                view3.setVisibility(View.VISIBLE);
                view3.setImageBitmap(bitmap3);
                espacio7.setVisibility(View.VISIBLE);

            }


        }
    }



    public void ValidacionE() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrab3Activity.this);
        alertDialogBuilder.setTitle("Alerta");
        alertDialogBuilder
                .setMessage("¿ Desea registrar al trabajador ?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Eliminar();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        //Intent i = new Intent(getApplicationContext(), RegTrabActivity.class);
                        // startActivity(i);
                        // finish();

                    }
                }).create().show();
    }

    public void Eliminar() {
        String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/trabajador_8.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                if (response.equals("error")) {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrab3Activity.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("No se pudo eliminar registro")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(getApplicationContext(), RegTrab2Activity.class);
                                    startActivity(i);
                                    finish();
                                }
                            }).create().show();


                } else {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrab3Activity.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("Registro eliminado correctamente")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(getApplicationContext(), RegTrab2Activity.class);
                                    startActivity(i);
                                    finish();
                                }
                            }).create().show();
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
                params.put("id", Conf.getTraba());

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void Datos() {

        String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/trabajador_7.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.e("TAG", "Error: " + response);

                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {
                        ja1 = new JSONArray(response);
                        Informacion();
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
                params.put("id", Conf.getTraba());
                Log.e("TAG", "Error: " + params);

                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void Informacion() {
        try {
            Tipo.setText(ja1.getString(3));
            Depa.setText(ja1.getString(4));
            Nombre.setText(ja1.getString(6));
            Tel.setText(ja1.getString(9));
            Correo.setText(ja1.getString(11));
            Coment.setText(ja1.getString(16));
            Puesto.setText(ja1.getString(12));
            Direccion.setText(ja1.getString(10));

            if(ja1.getString(13).equals("")){
                Foto1View.setVisibility(View.GONE);
                espacio2.setVisibility(View.GONE);
            }else{
                storageReference.child(Conf.getPin()+"/trabajadores/"+ja1.getString(13))
                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                    @Override

                    public void onSuccess(Uri uri) {
                        Glide.with(RegTrab3Activity.this)
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

            if(ja1.getString(14).equals("")){
                Foto2View.setVisibility(View.GONE);
                espacio4.setVisibility(View.GONE);
            }else{
                storageReference.child(Conf.getPin()+"/trabajadores/"+ja1.getString(14))
                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                    @Override

                    public void onSuccess(Uri uri) {
                        Glide.with(RegTrab3Activity.this)
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

            if(ja1.getString(15).equals("")){
                Foto3View.setVisibility(View.GONE);
                espacio7.setVisibility(View.GONE);
            }else{
                storageReference.child(Conf.getPin()+"/trabajadores/"+ja1.getString(15))
                        .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                    @Override

                    public void onSuccess(Uri uri) {
                        Glide.with(RegTrab3Activity.this)
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
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void ValidacionM() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrab3Activity.this);
        alertDialogBuilder.setTitle("Alerta");
        alertDialogBuilder
                .setMessage("¿ Desea modificar al trabajador ?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Registro();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        //Intent i = new Intent(getApplicationContext(), RegTrabActivity.class);
                        // startActivity(i);
                        // finish();

                    }
                }).create().show();
    }

    public void Registro() {
        String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/trabajador_9.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                Log.e("TAG", "LINKOM ST: " + response);


                if(response.equals("error")){

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrab3Activity.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("Modificación No Exitosa")
                            .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent i = new Intent(getApplicationContext(), RegTrab2Activity.class);
                                    startActivity(i);
                                    finish();
                                }
                            }).create().show();


                }else {

                    if(foto1==1){
                        upload1();
                    }

                    if(foto2==2){
                        upload2();
                    }

                    if(foto3==3){
                        upload3();
                    }

                    Finalizar();
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
                if(foto1==1){
                    nfoto1="app"+anio+mes+dia+Nombre.getText().toString()+"-"+numero_aletorio+".png";
                }else{
                    try {
                        nfoto1=ja1.getString(13);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(foto2==2){
                    nfoto2="app"+anio+mes+dia+Nombre.getText().toString()+"-"+numero_aletorio2+".png";
                }else {
                    try {
                        nfoto2=ja1.getString(14);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                if(foto3==3){
                    nfoto3="app"+anio+mes+dia+Nombre.getText().toString()+"-"+numero_aletorio3+".png";
                }else {
                    try {
                        nfoto3=ja1.getString(15);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }


                params.put("id_residencial", Conf.getResid().trim());
                params.put("id", Conf.getTraba().trim());
                params.put("nombre", Nombre.getText().toString().trim());
                params.put("puesto", Puesto.getText().toString().trim());
                params.put("direccion", Direccion.getText().toString().trim());
                params.put("telefono", Tel.getText().toString().trim());
                params.put("correo", Correo.getText().toString().trim());
                params.put("comentarios", Coment.getText().toString().trim());
                params.put("foto1", nfoto1);
                params.put("foto2", nfoto2);
                params.put("foto3", nfoto3);


                return params;

            }
        };
        requestQueue.add(stringRequest);

    }

    public void upload1(){

        StorageReference mountainImagesRef = null;
        mountainImagesRef = storageReference.child(Conf.getPin()+"/trabajadores/app"+anio+mes+dia+Nombre.getText().toString()+"-"+numero_aletorio+".png");


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
                Toast.makeText(RegTrab3Activity.this,"Fallado",Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd.dismiss();

            }
        });
    }

    public void upload2(){


        StorageReference mountainImagesRef2 = null;
        mountainImagesRef2 = storageReference.child(Conf.getPin()+"/trabajadores/app"+anio+mes+dia+Nombre.getText().toString()+"-"+numero_aletorio2+".png");


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
                Toast.makeText(RegTrab3Activity.this,"Fallado",Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd2.dismiss();
            }
        });


    }


    public void upload3(){


        StorageReference mountainImagesRef3 = null;
        mountainImagesRef3 = storageReference.child(Conf.getPin()+"/trabajadores/app"+anio+mes+dia+Nombre.getText().toString()+"-"+numero_aletorio3+".png");


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
                Toast.makeText(RegTrab3Activity.this,"Fallado",Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd3.dismiss();
            }
        });


    }

    public void Finalizar(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrab3Activity.this);
        alertDialogBuilder.setTitle("Alerta");
        alertDialogBuilder
                .setMessage("Modificación Exitosa")
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(getApplicationContext(), RegTrab2Activity.class);
                        startActivity(i);
                        finish();
                    }
                }).create().show();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), RegTrab2Activity.class);
        startActivity(intent);
        finish();
    }
}
