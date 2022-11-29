package mx.linkom.caseta_dm_offline;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RegTrabActivity extends mx.linkom.caseta_dm_offline.Menu {

    Button registrar;
    Spinner Tipo, Departamento;
    ArrayList<String> tipo, departamento;
    FirebaseStorage storage;
    StorageReference storageReference;
    JSONArray ja1, ja2, ja3, ja4, ja5, ja6;
    Configuracion Conf;
    EditText nombre,telefono,correo,comentarios,clave_e,puesto,direccion;
    LinearLayout Foto1View,espacio2,Foto2,espacio3,Foto2View,espacio4,Foto3View,espacio7,Foto3,espacio6,registrar1,espacio5,registro,clave;
    Button btn_foto1,btn_foto2,btn_foto3,buscar;
    ImageView view1,view2,view3;
    Uri uri_img,uri_img2,uri_img3;
    int foto1,foto2,foto3,n_t;
    String nfoto1,nfoto2,nfoto3;
    ProgressDialog pd,pd2,pd3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regtrab);

        n_t=0;
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        Conf = new Configuracion(this);


        btn_foto1 = (Button) findViewById(R.id.btn_foto1);
        btn_foto2 = (Button) findViewById(R.id.btn_foto2);
        btn_foto3 = (Button) findViewById(R.id.btn_foto3);
        buscar = (Button) findViewById(R.id.buscar);
        view1 = (ImageView) findViewById(R.id.view1);
        view2 = (ImageView) findViewById(R.id.view2);
        view3 = (ImageView) findViewById(R.id.view3);
        puesto = (EditText) findViewById(R.id.setPuesto);
        direccion = (EditText) findViewById(R.id.setDire);
        clave = (LinearLayout) findViewById(R.id.clave);
        registro = (LinearLayout) findViewById(R.id.registro);
        Foto1View = (LinearLayout) findViewById(R.id.Foto1View);
        espacio2 = (LinearLayout) findViewById(R.id.espacio2);
        Foto2 = (LinearLayout) findViewById(R.id.Foto2);
        espacio3 = (LinearLayout) findViewById(R.id.espacio3);
        Foto3 = (LinearLayout) findViewById(R.id.Foto3);
        espacio6 = (LinearLayout) findViewById(R.id.espacio6);
        Foto2View = (LinearLayout) findViewById(R.id.Foto2View);
        espacio4 = (LinearLayout) findViewById(R.id.espacio4);
        Foto3View = (LinearLayout) findViewById(R.id.Foto3View);
        espacio7 = (LinearLayout) findViewById(R.id.espacio7);
        registrar1 = (LinearLayout) findViewById(R.id.registrar1);
        espacio5 = (LinearLayout) findViewById(R.id.espacio5);

        tipo = new ArrayList<String>();
        departamento = new ArrayList<String>();
        registrar = (Button) findViewById(R.id.registrar);
        Tipo = (Spinner) findViewById(R.id.setTipo);
        Departamento = (Spinner) findViewById(R.id.setDepa);
        clave_e = (EditText) findViewById(R.id.setColono);
        nombre = (EditText) findViewById(R.id.setNombre);
        telefono = (EditText) findViewById(R.id.setTel);
        correo = (EditText) findViewById(R.id.setCorreo);
        comentarios = (EditText) findViewById(R.id.setComen);

        Tipo();
        cargarDepartamento2();

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validacion();
            }
        });

        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                busqueda();
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
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrabActivity.this);
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
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrabActivity.this);
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
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrabActivity.this);
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


                Matrix matrix = new Matrix();
                matrix.postRotate(90.0F);
                Bitmap bitmap = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(null) + "/ft1.png");



                Foto1View.setVisibility(View.VISIBLE);
                view1.setVisibility(View.VISIBLE);
                view1.setImageBitmap(bitmap);
                espacio2.setVisibility(View.VISIBLE);

                Foto2.setVisibility(View.VISIBLE);
                espacio3.setVisibility(View.VISIBLE);

                Foto3.setVisibility(View.VISIBLE);
                espacio6.setVisibility(View.VISIBLE);
                registrar1.setVisibility(View.VISIBLE);

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


    public void busqueda() {
        if(clave_e.getText().toString().equals("") ){
            Toast.makeText(getApplicationContext(),"Campo Vació", Toast.LENGTH_SHORT).show();
        }else if(clave_e.getText().toString().equals(" ") ){
            Toast.makeText(getApplicationContext(),"Campo Vació", Toast.LENGTH_SHORT).show();
        }else{

            String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/trabajador_2.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {

                    if (response.equals("error")) {

                        id();

                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrabActivity.this);
                        alertDialogBuilder.setTitle("Alerta");
                        alertDialogBuilder
                                .setMessage("Ya existe un trabajo registrado")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        registro.setVisibility(View.GONE);

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
                    params.put("id_residencial", Conf.getResid().trim());
                    params.put("clave_elector", clave_e.getText().toString().trim());

                    return params;
                }
            };
            requestQueue.add(stringRequest);
        }
    }


    public void id() {
        String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/trabajador_3.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {

                    response = response.replace("][", ",");
                    if (response.length() > 0) {
                        try {
                            registro.setVisibility(View.VISIBLE);
                            ja3 = new JSONArray(response);
                            n_t=Integer.parseInt(ja3.getString(0))+1;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                   // Log.e("TAG", "LINKOM ST response response:"+response);
                   // Log.e("TAG", "LINKOM ST response total:"+ n_t);


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
                    params.put("id_residencial", Conf.getResid().trim());

                    return params;
                }
            };
            requestQueue.add(stringRequest);
    }



    public void Tipo() {
        tipo.add("Seleccionar..");
        tipo.add("Seleccionar...");
        tipo.add("Inmueble");
        tipo.add("Colono");

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, tipo);
        Tipo.setAdapter(adapter1);
        Tipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (Tipo.getSelectedItem().equals("Seleccionar..")) {
                    tipo.remove(0);
                } else if (Tipo.getSelectedItem().equals("Seleccionar...")) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrabActivity.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("No selecciono ningún tipo...")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {

                                }
                            }).create().show();
                } else {
                    departamento.clear();
                    traeDepartamento();


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void traeDepartamento() {

        String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/trabajador_4.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {

                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {
                        ja1 = new JSONArray(response);
                        cargarDepartamento();
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
                params.put("id_residencial", Conf.getResid().trim());
                params.put("departamento", Tipo.getSelectedItem().toString());

                return params;

            }
        };
        requestQueue.add(stringRequest);
    }


    public void cargarDepartamento() {


        try {
            departamento.add("Seleccionar..");
            departamento.add("Seleccionar...");

            for (int i = 0; i < ja1.length(); i += 7) {
                departamento.add(ja1.getString(i + 2));
            }

            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, departamento);
            Departamento.setAdapter(adapter1);
            Departamento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (Departamento.getSelectedItem().equals("Seleccionar..")) {
                        departamento.remove(0);
                    } else if (Departamento.getSelectedItem().equals("Seleccionar...")) {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrabActivity.this);
                        alertDialogBuilder.setTitle("Alerta");
                        alertDialogBuilder
                                .setMessage("No selecciono ningún departamento...")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                    }
                                }).create().show();
                    } else {
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cargarDepartamento2() {

        departamento.add("Seleccionar...");
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, departamento);
        Departamento.setAdapter(adapter1);
    }

    public void Validacion() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrabActivity.this);
        alertDialogBuilder.setTitle("Alerta");
        alertDialogBuilder
                .setMessage("¿ Desea registrar al trabajador ?")
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        traeDepartamento2();
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

    public void traeDepartamento2() {

        if (Tipo.getSelectedItem().toString().equals("Seleccionar...") || Departamento.getSelectedItem().toString().equals("Seleccionar...")) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrabActivity.this);
            alertDialogBuilder.setTitle("Alerta");
            alertDialogBuilder
                    .setMessage("No selecciono ningún tipo / departamento...")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).create().show();
        } else if(nombre.getText().toString().equals("") || nombre.getText().toString().equals(" ")){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrabActivity.this);
            alertDialogBuilder.setTitle("Alerta");
            alertDialogBuilder
                    .setMessage("Rellenar campo de nombre...")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    }).create().show();
        } else {
            String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/trabajador_5.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {


                @Override
                public void onResponse(String response) {

                    response = response.replace("][", ",");
                    if (response.length() > 0) {
                        try {
                            ja2 = new JSONArray(response);
                            Registro();
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
                    params.put("id_residencial", Conf.getResid().trim());
                    params.put("tipo", Tipo.getSelectedItem().toString());
                    params.put("departamento", Departamento.getSelectedItem().toString());

                    return params;

                }
            };
            requestQueue.add(stringRequest);
        }
    }

    public void Registro() {

        String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/trabajador_6.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {


                if(response.equals("error")){

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrabActivity.this);
                    alertDialogBuilder.setTitle("Alerta");
                    alertDialogBuilder
                            .setMessage("Registro No Exitoso")
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
                    nfoto1="app"+anio+mes+dia+nombre.getText().toString()+"-"+numero_aletorio+".png";
                }else{
                    nfoto1="";
                }


                if(foto2==2){
                    nfoto2="app"+anio+mes+dia+nombre.getText().toString()+"-"+numero_aletorio2+".png";
                }else{
                    nfoto2="";
                }

                if(foto3==3){
                    nfoto3="app"+anio+mes+dia+nombre.getText().toString()+"-"+numero_aletorio3+".png";
                }else{
                    nfoto3="";
                }




                params.put("id_residencial", Conf.getResid().trim());
                try {
                    params.put("tipo", Tipo.getSelectedItem().toString());
                    params.put("departamento", ja2.getString(0));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                params.put("nombre", nombre.getText().toString().trim());
                params.put("puesto", puesto.getText().toString().trim());
                params.put("direccion", direccion.getText().toString().trim());
                params.put("telefono", telefono.getText().toString().trim());
                params.put("correo", correo.getText().toString().trim());
                params.put("comentarios", comentarios.getText().toString().trim());
                params.put("foto1", nfoto1);
                params.put("foto2", nfoto2);
                params.put("foto3", nfoto3);
                params.put("id_trabajador", String.valueOf(n_t));

                params.put("clave_elector", clave_e.getText().toString().trim());


                return params;

            }
        };
        requestQueue.add(stringRequest);

    }

    public void upload1(){

        StorageReference mountainImagesRef = null;
        mountainImagesRef = storageReference.child(Conf.getPin()+"/trabajadores/app"+anio+mes+dia+nombre.getText().toString()+"-"+numero_aletorio+".png");


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
                Toast.makeText(RegTrabActivity.this,"Fallado",Toast.LENGTH_SHORT).show();
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
        mountainImagesRef2 = storageReference.child(Conf.getPin()+"/trabajadores/app"+anio+mes+dia+nombre.getText().toString()+"-"+numero_aletorio2+".png");


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
                Toast.makeText(RegTrabActivity.this,"Fallado",Toast.LENGTH_SHORT).show();
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
        mountainImagesRef3 = storageReference.child(Conf.getPin()+"/trabajadores/app"+anio+mes+dia+nombre.getText().toString()+"-"+numero_aletorio3+".png");


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
                Toast.makeText(RegTrabActivity.this,"Fallado",Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd3.dismiss();
            }
        });


    }

    public void Finalizar(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(RegTrabActivity.this);
        alertDialogBuilder.setTitle("Alerta");
        alertDialogBuilder
                .setMessage("Registro Exitoso")
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

