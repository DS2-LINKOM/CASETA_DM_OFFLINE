package mx.linkom.caseta_dm_offline;

import android.app.Activity;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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
import java.util.Random;

import id.zelory.compressor.Compressor;

public class AccesosGrupalActivity extends mx.linkom.caseta_dm_offline.Menu {
    Configuracion Conf;
    FirebaseStorage storage;
    StorageReference storageReference;

    LinearLayout rlPermitido, rlDenegado,rlVista;
    TextView  tvMensaje;
    TextView Nombre,Dire,Visi,Tipo;

    EditText Placas;
    Spinner Pasajeros;
    LinearLayout Pasajeros1;

    ArrayList<String> names;
    JSONArray ja1,ja2,ja3,ja4,ja5,ja6;
    Bitmap bitmap,bitmap2,bitmap3;
    ProgressDialog pd,pd2,pd3;
    int foto;
    String f1,f2,f3;

    LinearLayout espacio1,espacio2,espacio3,espacio4,espacio5,espacio6,espacio7,espacio8,espacio9,espacio10;
    LinearLayout registrar1,registrar2,registrar3,registrar4;
    Button reg1,reg2,reg3,reg4,btn_foto1,btn_foto2,btn_foto3;
    LinearLayout Foto1View,Foto2View,Foto3View;
    LinearLayout Foto1,Foto2,Foto3;
    ImageView view1,view2,view3;
    TextView nombre_foto1,nombre_foto2,nombre_foto3;
    Uri uri_img,uri_img2,uri_img3;
    RadioGroup rdgGrupo2;
    RadioButton si,no;
    TextView dato;
    LinearLayout CPlacasTexto;
    EditText Comentarios;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accesosgrupales);

        Conf = new Configuracion(this);
        storage= FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        names = new ArrayList<String>();

        reg1 = (Button) findViewById(R.id.reg1);
        reg2 = (Button) findViewById(R.id.reg2);
        reg3 = (Button) findViewById(R.id.reg3);
        reg4 = (Button) findViewById(R.id.reg4);
        btn_foto1 = (Button) findViewById(R.id.btn_foto1);
        btn_foto2 = (Button) findViewById(R.id.btn_foto2);
        btn_foto3 = (Button) findViewById(R.id.btn_foto3);

        nombre_foto1 = (TextView) findViewById(R.id.nombre_foto1);
        nombre_foto2 = (TextView) findViewById(R.id.nombre_foto2);
        nombre_foto3 = (TextView) findViewById(R.id.nombre_foto3);

        view1 = (ImageView) findViewById(R.id.view1);
        view2 = (ImageView) findViewById(R.id.view2);
        view3 = (ImageView) findViewById(R.id.view3);

        espacio1 = (LinearLayout) findViewById(R.id.espacio1);
        espacio2 = (LinearLayout) findViewById(R.id.espacio2);
        espacio3 = (LinearLayout) findViewById(R.id.espacio3);
        espacio4 = (LinearLayout) findViewById(R.id.espacio4);
        espacio5 = (LinearLayout) findViewById(R.id.espacio5);
        espacio6 = (LinearLayout) findViewById(R.id.espacio6);
        espacio7 = (LinearLayout) findViewById(R.id.espacio7);
        espacio8 = (LinearLayout) findViewById(R.id.espacio8);
        espacio9 = (LinearLayout) findViewById(R.id.espacio9);
        espacio10 = (LinearLayout) findViewById(R.id.espacio10);
        registrar1 = (LinearLayout) findViewById(R.id.registrar1);
        registrar2 = (LinearLayout) findViewById(R.id.registrar2);
        registrar3 = (LinearLayout) findViewById(R.id.registrar3);
        registrar4 = (LinearLayout) findViewById(R.id.registrar4);
        Foto1View = (LinearLayout) findViewById(R.id.Foto1View);
        Foto2View = (LinearLayout) findViewById(R.id.Foto2View);
        Foto3View = (LinearLayout) findViewById(R.id.Foto3View);
        Foto1 = (LinearLayout) findViewById(R.id.Foto1);
        Foto2 = (LinearLayout) findViewById(R.id.Foto2);
        Foto3 = (LinearLayout) findViewById(R.id.Foto3);

        Comentarios = (EditText)findViewById(R.id.setComentarios);
        Nombre = (TextView)findViewById(R.id.setNombre);
        Tipo = (TextView)findViewById(R.id.setTipo);
        Dire = (TextView)findViewById(R.id.setDire);
        Visi = (TextView) findViewById(R.id.setVisi);
        Pasajeros1 = (LinearLayout) findViewById(R.id.setPasajeros1);
        Pasajeros = (Spinner) findViewById(R.id.setPasajeros);
        Placas = (EditText) findViewById(R.id.setPlacas);
        tvMensaje = (TextView) findViewById(R.id.setMensaje);

        rlVista = (LinearLayout) findViewById(R.id.rlVista);
        rlPermitido = (LinearLayout) findViewById(R.id.rlPermitido);
        rlDenegado = (LinearLayout) findViewById(R.id.rlDenegado);
        CPlacasTexto = (LinearLayout) findViewById(R.id.CPlacasTexto);

        rdgGrupo2 = (RadioGroup)findViewById(R.id.rdgGrupo2);
        si = (RadioButton)findViewById(R.id.Si);
        no = (RadioButton)findViewById(R.id.No);
        dato = (TextView) findViewById(R.id.placas_texto);

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
            tvMensaje.setText("QR Inexistente");
        }

        pd= new ProgressDialog(this);
        pd.setMessage("Subiendo Imagen 1...");

        pd2= new ProgressDialog(this);
        pd2.setMessage("Subiendo Imagen 2...");

        pd3= new ProgressDialog(this);
        pd3.setMessage("Subiendo Imagen 3...");



        reg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validacion();
            }
        });

        reg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validacion();
            }
        });

        reg3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validacion();
            }
        });

        reg4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Validacion();
            }
        });

        btn_foto1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foto=1;
                imgFoto();
            }
        });

        btn_foto2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foto=2;
                imgFoto2();
            }
        });

        btn_foto3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foto=3;
                imgFoto3();
            }
        });
        Placas.setFilters(new InputFilter[] { filter,new InputFilter.AllCaps() {
        } });
        cargarSpinner();
        CPlacasTexto.setVisibility(View.VISIBLE);

        if(Conf.getTipoReg().equals("Nada")){
            dato.setText("Placas / Cono / Gafete / Credencial:");
            si.setChecked(true);
        }else if(Conf.getTipoReg().equals("Auto")) {
            dato.setText("Placas / Cono:");
            si.setVisibility(View.GONE);
            no.setVisibility(View.GONE);
            si.setChecked(true);
        }else if(Conf.getTipoReg().equals("Peatonal")){
            dato.setText("Gafete / Credencial:");
        }
    }
    public void onRadioButtonClicked(View view) {
        boolean marcado = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.Si:
                if (marcado) {
                    CPlacasTexto.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.No:
                if (marcado) {
                    CPlacasTexto.setVisibility(View.GONE);

                }
                break;
        }
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
    public void cargarSpinner(){

        names.add("Selecciona...");
        names.add("1");
        names.add("2");
        names.add("3");
        names.add("4");
        names.add("5");
        names.add("6");
        names.add("7");
        names.add("8");
        names.add("9");
        names.add("10");
        names.add("11");
        names.add("12");
        names.add("13");
        names.add("14");
        names.add("15");
        names.add("16");
        names.add("17");
        names.add("18");

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,names);
        Pasajeros.setAdapter(adapter1);


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

    //ALETORIO2

    Random primero2 = new Random();
    int prime2= primero2.nextInt(9);

    String [] segundo2 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandonsegun2 = (int) Math.round(Math.random() * 25 ) ;

    Random tercero2 = new Random();
    int tercer2= tercero2.nextInt(9);

    String [] cuarto2 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandoncuart2 = (int) Math.round(Math.random() * 25 ) ;

    String numero_aletorio2=prime2+segundo2[numRandonsegun2]+tercer2+cuarto2[numRandoncuart2];

//ALETORIO3

    Random primero3 = new Random();
    int prime3= primero3.nextInt(9);

    String [] segundo3= {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandonsegun3 = (int) Math.round(Math.random() * 25 ) ;

    Random tercero3 = new Random();
    int tercer3= tercero3.nextInt(9);

    String [] cuarto3 = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m","n","o","p","q","r","s","t","u","v","w", "x","y","z" };
    int numRandoncuart3 = (int) Math.round(Math.random() * 25 ) ;

    String numero_aletorio3=prime3+segundo3[numRandonsegun3]+tercer3+cuarto3[numRandoncuart3];


    Calendar fecha = Calendar.getInstance();
    int anio = fecha.get(Calendar.YEAR);
    int mes = fecha.get(Calendar.MONTH) + 1;
    int dia = fecha.get(Calendar.DAY_OF_MONTH);


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
                        imagenes();
                        Visita();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else {
                    response = response.replace("][", ",");
                    if (response.length() > 0) {
                        try {
                            ja6 = new JSONArray(response);
                            imagenes();
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

    public void imagenes(){
        try {

            if(ja6.getString(0).equals("0") || ja6.getString(3).equals("0")) {

                registrar1.setVisibility(View.VISIBLE);
                espacio1.setVisibility(View.VISIBLE);

                Foto1.setVisibility(View.GONE);
                espacio2.setVisibility(View.GONE);
                Foto1View.setVisibility(View.GONE);
                espacio3.setVisibility(View.GONE);
                registrar2.setVisibility(View.GONE);
                espacio4.setVisibility(View.GONE);
                Foto2.setVisibility(View.GONE);
                espacio5.setVisibility(View.GONE);
                Foto2View.setVisibility(View.GONE);
                espacio6.setVisibility(View.GONE);
                registrar3.setVisibility(View.GONE);
                espacio7.setVisibility(View.GONE);
                Foto3.setVisibility(View.GONE);
                espacio8.setVisibility(View.GONE);
                Foto3View.setVisibility(View.GONE);
                espacio9.setVisibility(View.GONE);
                registrar4.setVisibility(View.GONE);
                espacio10.setVisibility(View.GONE);


            }else if(ja6.getString(3).equals("1")){

                registrar1.setVisibility(View.GONE);
                espacio1.setVisibility(View.GONE);

                Foto1.setVisibility(View.VISIBLE);
                espacio2.setVisibility(View.VISIBLE);
                nombre_foto1.setVisibility(View.VISIBLE);
                nombre_foto1.setText(ja6.getString(4)+":");

                Foto1View.setVisibility(View.GONE);
                espacio3.setVisibility(View.GONE);
                registrar2.setVisibility(View.GONE);
                espacio4.setVisibility(View.GONE);
                Foto2.setVisibility(View.GONE);
                espacio5.setVisibility(View.GONE);
                Foto2View.setVisibility(View.GONE);
                espacio6.setVisibility(View.GONE);
                registrar3.setVisibility(View.GONE);
                espacio7.setVisibility(View.GONE);
                Foto3.setVisibility(View.GONE);
                espacio8.setVisibility(View.GONE);
                Foto3View.setVisibility(View.GONE);
                espacio9.setVisibility(View.GONE);
                registrar4.setVisibility(View.GONE);
                espacio10.setVisibility(View.GONE);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //FOTOS


    public void imgFoto(){
        Intent intentCaptura = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCaptura.addFlags(intentCaptura.FLAG_GRANT_READ_URI_PERMISSION);

        if (intentCaptura.resolveActivity(getPackageManager()) != null) {

            File foto=null;
            try {
                foto= new File(getApplication().getExternalFilesDir(null),"accesosGrupal1.png");
            } catch (Exception ex) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AccesosGrupalActivity.this);
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
                foto = new File(getApplication().getExternalFilesDir(null),"accesosGrupal2.png");
            } catch (Exception ex) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AccesosGrupalActivity.this);
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
                foto = new File(getApplication().getExternalFilesDir(null),"accesosGrupal3.png");
            } catch (Exception ex) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AccesosGrupalActivity.this);
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


                Bitmap bitmap = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(null) + "/accesosGrupal1.png");


                Foto1View.setVisibility(View.VISIBLE);
                view1.setVisibility(View.VISIBLE);
                view1.setImageBitmap(bitmap);
                espacio3.setVisibility(View.VISIBLE);


                try {
                    if(ja6.getString(3).equals("1") && ja6.getString(5).equals("0") && ja6.getString(7).equals("0")) {
                        registrar2.setVisibility(View.VISIBLE);
                        reg2.setVisibility(View.VISIBLE);
                        espacio4.setVisibility(View.VISIBLE);
                        espacio5.setVisibility(View.VISIBLE);
                    }else if(ja6.getString(3).equals("1") && ja6.getString(5).equals("1") && ja6.getString(7).equals("0")){
                        registrar2.setVisibility(View.GONE);
                        Foto2.setVisibility(View.VISIBLE);
                        espacio5.setVisibility(View.VISIBLE);
                        espacio6.setVisibility(View.VISIBLE);
                        nombre_foto2.setVisibility(View.VISIBLE);
                        nombre_foto2.setText(ja6.getString(6)+":");
                    }else if(ja6.getString(3).equals("1") && ja6.getString(5).equals("1") && ja6.getString(7).equals("1")) {
                        registrar2.setVisibility(View.GONE);
                        Foto2.setVisibility(View.VISIBLE);
                        espacio5.setVisibility(View.VISIBLE);
                        espacio6.setVisibility(View.VISIBLE);
                        nombre_foto2.setVisibility(View.VISIBLE);
                        nombre_foto2.setText(ja6.getString(6)+":");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
            if (requestCode == 1) {


                Bitmap bitmap2 = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(null) + "/accesosGrupal2.png");

                Foto2View.setVisibility(View.VISIBLE);
                view2.setVisibility(View.VISIBLE);
                view2.setImageBitmap(bitmap2);
                espacio6.setVisibility(View.VISIBLE);

                try {
                    if(ja6.getString(3).equals("1") && ja6.getString(5).equals("1") && ja6.getString(7).equals("0")) {
                        registrar3.setVisibility(View.VISIBLE);
                        reg3.setVisibility(View.VISIBLE);
                        espacio7.setVisibility(View.VISIBLE);
                    }else if(ja6.getString(3).equals("1") && ja6.getString(5).equals("1") && ja6.getString(7).equals("1")){
                        registrar3.setVisibility(View.GONE);
                        espacio7.setVisibility(View.VISIBLE);
                        espacio8.setVisibility(View.VISIBLE);
                        Foto3.setVisibility(View.VISIBLE);
                        espacio9.setVisibility(View.VISIBLE);
                        nombre_foto3.setVisibility(View.VISIBLE);
                        nombre_foto3.setText(ja6.getString(8)+":");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            if (requestCode == 2) {


                Bitmap bitmap3 = BitmapFactory.decodeFile(getApplicationContext().getExternalFilesDir(null) + "/accesosGrupal3.png");


                Foto3View.setVisibility(View.VISIBLE);
                view3.setVisibility(View.VISIBLE);
                view3.setImageBitmap(bitmap3);
                espacio10.setVisibility(View.VISIBLE);


                try {
                    if(ja6.getString(3).equals("1") && ja6.getString(5).equals("1") && ja6.getString(7).equals("1")){
                        registrar4.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }
    }





    public void Visita(){

        String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/vst_gru_2.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
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
                params.put("id", Conf.getIdvisita());
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
        String URLResidencial = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/vst_php4.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLResidencial, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    if (response.trim().equals("error")){

                        int $arreglo[]={0};
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
            Calendar c = Calendar.getInstance();
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date dateentrada = (Date)formatter.parse(ja1.getString(10));
            Date datesalida = (Date)formatter.parse(ja1.getString(11));

            //ANTES DE LA ENTRADA
            if(c.getTime().before(dateentrada) && ja4.getString(0).equals("0")) {//NUEVO
                rlVista.setVisibility(View.GONE);
                rlPermitido.setVisibility(View.GONE);
                rlDenegado.setVisibility(View.VISIBLE);
                tvMensaje.setText("Aún no es hora de entrada");
                //EN MEDIO
            } else if( c.getTime().equals(dateentrada) || c.getTime().before(datesalida) ) {

                if (ja4.getString(0).equals("0")) { //NUEVO
                    rlVista.setVisibility(View.GONE);
                    rlDenegado.setVisibility(View.GONE);
                    rlPermitido.setVisibility(View.VISIBLE);

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

                    if(Integer.parseInt(Conf.getPreQr())==1){
                        Placas.setText(Conf.getPlacas());
                    }

                    Comentarios.setText(ja1.getString(9));


                } else if (ja4.getString(0).equals("1")) { //Entro y quiere volver a entrar
                    rlVista.setVisibility(View.GONE);
                    rlPermitido.setVisibility(View.GONE);
                    rlDenegado.setVisibility(View.VISIBLE);
                    tvMensaje.setText("Esté auto se encuentra dentro del complejo");

                } else if (ja4.getString(0).equals("2")) { //Entro y salio ; y quiere volver a entrar
                    rlVista.setVisibility(View.GONE);
                    rlPermitido.setVisibility(View.GONE);
                    rlDenegado.setVisibility(View.VISIBLE);
                    tvMensaje.setText("Esté código QR ya fue utilizado");
                }

            }else if(c.getTime().after(datesalida)  && ja4.getString(0).equals("0") ) { //NUEVO
                rlVista.setVisibility(View.GONE);
                rlPermitido.setVisibility(View.GONE);
                rlDenegado.setVisibility(View.VISIBLE);
                tvMensaje.setText("Esté código QR ha caducado");

            }else    if(c.getTime().after(datesalida)  && ja4.getString(0).equals("2") ){ //ENTRO Y SALIO
                rlVista.setVisibility(View.GONE);
                rlPermitido.setVisibility(View.GONE);
                rlDenegado.setVisibility(View.VISIBLE);
                tvMensaje.setText("Esté código QR ha caducado");

            } else if(c.getTime().after(datesalida)  && ja4.getString(0).equals("1") ){//ESTA ADENTRO
                rlVista.setVisibility(View.GONE);
                rlPermitido.setVisibility(View.GONE);
                rlDenegado.setVisibility(View.VISIBLE);
                tvMensaje.setText("Esté código QR ha caducado, Leer Salida");
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public void Validacion() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AccesosGrupalActivity.this);
        alertDialogBuilder.setTitle("Alerta");
        alertDialogBuilder
                .setMessage("¿ Desea realizar la entrada ?")
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Registrar();
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


    public void Registrar(){

        if(Placas.getText().toString().equals("") && si.isChecked()){
            Toast.makeText(getApplicationContext(),"Campo de placas", Toast.LENGTH_SHORT).show();
        }else if(Placas.getText().toString().equals(" ") && si.isChecked()){
            Toast.makeText(getApplicationContext(),"Campo de placas ", Toast.LENGTH_SHORT).show();
        }else if( Placas.getText().toString().equals("N/A") && si.isChecked()){
            Toast.makeText(getApplicationContext(),"Campo de placas", Toast.LENGTH_SHORT).show();
        }else{

            String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/vst_php5.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

                @Override
                public void onResponse(String response){


                    if(response.equals("error")){

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AccesosGrupalActivity.this);
                        alertDialogBuilder.setTitle("Alerta");
                        alertDialogBuilder
                                .setMessage("Visita No Exitosa")
                                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Toast.makeText(getApplicationContext(),"Visita No Registrada", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(getApplicationContext(), EscaneoVisitaActivity.class);
                                        startActivity(i);
                                        finish();
                                    }
                                }).create().show();


                    }else {

                        try {
                            if(ja6.getString(0).equals("0") || ja6.getString(3).equals("0")) {
                                Terminar();
                            }else if(ja6.getString(3).equals("1") && ja6.getString(5).equals("0") && ja6.getString(7).equals("0")){
                                upload1();
                                Terminar();
                            }else if(ja6.getString(3).equals("1") && ja6.getString(5).equals("1") && ja6.getString(7).equals("0")){
                                upload1();
                                upload2();
                                Terminar();
                            }else if(ja6.getString(3).equals("1") && ja6.getString(5).equals("1") && ja6.getString(7).equals("1")){
                                upload1();
                                upload2();
                                upload3();
                                Terminar();
                            }
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

                    try {
                        if(ja6.getString(0).equals("0") || ja6.getString(3).equals("0")) {
                            f1="";
                            f2="";
                            f3="";
                        }else if(ja6.getString(3).equals("1") && ja6.getString(5).equals("0") && ja6.getString(7).equals("0")){
                            f1="app"+anio+mes+dia+Placas.getText().toString()+"-"+numero_aletorio+".png";
                            f2="";
                            f3="";
                        }else if(ja6.getString(3).equals("1") && ja6.getString(5).equals("1") && ja6.getString(7).equals("0")){
                            f1="app"+anio+mes+dia+Placas.getText().toString()+"-"+numero_aletorio+".png";
                            f2="app"+anio+mes+dia+Placas.getText().toString()+"-"+numero_aletorio2+".png";
                            f3="";
                        }else if(ja6.getString(3).equals("1") && ja6.getString(5).equals("1") && ja6.getString(7).equals("1")){
                            f1="app"+anio+mes+dia+Placas.getText().toString()+"-"+numero_aletorio+".png";
                            f2="app"+anio+mes+dia+Placas.getText().toString()+"-"+numero_aletorio2+".png";
                            f3="app"+anio+mes+dia+Placas.getText().toString()+"-"+numero_aletorio3+".png";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    Map<String, String> params = new HashMap<>();
                    try {

                        params.put("id_residencial", Conf.getResid().trim());
                        params.put("id_visita", ja1.getString(0).trim());
                        params.put("guardia_de_entrada", Conf.getUsu().trim());
                        params.put("pasajeros",Pasajeros.getSelectedItem().toString());
                        params.put("placas", Placas.getText().toString().trim());
                        params.put("foto1", f1);
                        params.put("foto2", f2);
                        params.put("foto3", f3);
                        params.put("usuario",ja2.getString(1).trim() + " " + ja2.getString(2).trim() + " " + ja2.getString(3).trim());
                        params.put("token", ja2.getString(5).trim());
                        params.put("correo",ja2.getString(6).trim());
                        params.put("visita",ja1.getString(7).trim());
                        params.put("nom_residencial",Conf.getNomResi().trim());

                        params.put("comentarios",Comentarios.getText().toString().trim());

                    } catch (JSONException e) {
                        Log.e("TAG","Error: " + e.toString());
                    }
                    return params;
                }
            };
            requestQueue.add(stringRequest);
        }
    }



    public void upload1(){

        StorageReference mountainImagesRef = null;
        mountainImagesRef = storageReference.child(Conf.getPin()+"/caseta/app"+anio+mes+dia+Placas.getText().toString()+"-"+numero_aletorio+".png");

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
                Toast.makeText(AccesosGrupalActivity.this,"Fallado",Toast.LENGTH_SHORT).show();
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
        mountainImagesRef2 = storageReference.child(Conf.getPin()+"/caseta/app"+anio+mes+dia+Placas.getText().toString()+"-"+numero_aletorio2+".png");

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
                Toast.makeText(AccesosGrupalActivity.this,"Fallado",Toast.LENGTH_SHORT).show();
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
        mountainImagesRef3 = storageReference.child(Conf.getPin()+"/caseta/app"+anio+mes+dia+Placas.getText().toString()+"-"+numero_aletorio3+".png");

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
                Toast.makeText(AccesosGrupalActivity.this,"Fallado",Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                pd3.dismiss();

            }
        });


    }



    public void Terminar() {


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AccesosGrupalActivity.this);
        alertDialogBuilder.setTitle("Alerta");
        alertDialogBuilder
                .setMessage("Entrada de Visita Exitosa")
                .setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

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
