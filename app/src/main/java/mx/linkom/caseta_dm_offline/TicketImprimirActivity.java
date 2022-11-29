package mx.linkom.caseta_dm_offline;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import mx.linkom.caseta_dm_offline.Model.CasetaModel;
import mx.linkom.caseta_dm_offline.Utils.PDFUtils;
import mx.linkom.caseta_dm_offline.Utils.PdfDocumentAdapter;

public class TicketImprimirActivity extends mx.linkom.caseta_dm_offline.Menu {
    Configuracion Conf;

    private static final String FILE_PRINT="last_file_print.pdf";
    private AlertDialog dialog;
    List<CasetaModel> CasetaModelList= new ArrayList<CasetaModel>();
    JSONArray ja1,ja2,ja3,ja4;
    String tipo;
    Button regresar,imprimir;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_espera);
        Conf = new Configuracion(getApplicationContext());

        dialog = new AlertDialog.Builder(this).setCancelable(false).setMessage("Please Wait").create();
        regresar = (Button) findViewById(R.id.regresar);
        imprimir = (Button) findViewById(R.id.imprimir);

        Conf = new Configuracion(this);

        Visita();

        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), EntradasSalidasActivity.class);
                startActivity(i);
                finish();
            }
        });

        imprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), TicketImprimirActivity.class);
                startActivity(i);
                finish();
            }
        });

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


                    response = response.replace("][", ",");
                    if (response.length() > 0) {
                        try {
                            ja3 = new JSONArray(response);
                          Dtl();
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


    public void Dtl(){

        String URL = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/vst_reg_8.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                response = response.replace("][",",");
                if (response.length()>0){
                    try {
                        ja4 = new JSONArray(response);

                        Dexter.withActivity(TicketImprimirActivity.this)
                                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .withListener(new PermissionListener() {
                                    @Override
                                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                        createPDFFile(new StringBuilder(getAppPath()).append(FILE_PRINT).toString());
                                    }

                                    @Override
                                    public void onPermissionDenied(PermissionDeniedResponse response) {
                                        Toast.makeText(TicketImprimirActivity.this,""+response.getPermissionName()+"need enable", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                                    }
                                })
                                .check();

                        addDatos();
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
                try {
                    params.put("id", ja1.getString(0));
                    params.put("id_residencial", Conf.getResid().trim());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

    

    private void createPDFFile(String path) {
        if(new File(path).exists())

            new File(path).delete();

        try {
            Document document= new Document();
            //HAVE
            PdfWriter.getInstance(document,new FileOutputStream(path));
            //OPEN TO WRITE
            document.open();
            //SETTING
            document.setPageSize(PageSize.LETTER);
            document.addCreationDate();
            document.leftMargin();
            //FONT SETTING
            BaseColor colorAccent = new BaseColor(0,153,204,255);

            //CUSTOM FONT
            BaseFont fontName = BaseFont.createFont("res/font/negrita.otf","UTF-8",BaseFont.EMBEDDED);
            BaseFont fontNameNo = BaseFont.createFont("res/font/normal.otf","UTF-8",BaseFont.EMBEDDED);
            //CREATE TITLE
            Font titleFont = new Font(fontName,36.0f, Font.NORMAL,BaseColor.BLACK);
            PDFUtils.addNewItem(document,"DEMO PRIVADA ARBOLEDAS", Element.ALIGN_CENTER,titleFont);
            //s PDFUtils.addLineSeparator(document);
            Font titleFont2 = new Font(fontName,25f, Font.NORMAL,BaseColor.BLACK);
            Font titleFont3 = new Font(fontNameNo,25f, Font.NORMAL,BaseColor.BLACK);

            try {
                //ENTRADA
                String entrada=ja1.getString(10);
                String[] separacion1 = entrada.split(" ");
                String fechae = separacion1[0];
                String tiempoe = separacion1[1];

                String fecha1=fechae;
                String[] fechase = fecha1.split("-");
                String anioe = fechase[0];
                String mese = fechase[1];
                String diae = fechase[2];

                String hora1=tiempoe;
                String[] horase = hora1.split(":");
                String horae= horase[0];
                String minutose = horase[1];

                //SALIDA
                String salida=ja1.getString(11);
                String[] separacion2 = salida.split(" ");
                String fechas = separacion2[0];
                String tiempos = separacion2[1];

                String fecha2=fechas;
                String[] fechass = fecha2.split("-");
                String anios = fechass[0];
                String mess = fechass[1];
                String dias = fechass[2];

                String hora2=tiempos;
                String[] horass = hora2.split(":");
                String horas= horass[0];
                String minutoss = horass[1];

                if(ja1.getString(4).equals("1") || ja1.getString(4).equals("0")){
                    tipo="Visita";
                }else if(ja1.getString(4).equals("2")){
                    tipo="Proveedor / Servicio";

                }else if(ja1.getString(4).equals("3")){
                    tipo="Taxita";

                }
                PDFUtils.addNewItem(document,""+tipo+" "+ja1.getString(7), Element.ALIGN_LEFT,titleFont3);
                PDFUtils.addNewItem(document,"Colono: \n"+ja2.getString(1) +" "+ ja2.getString(2) + " " + ja2.getString(3), Element.ALIGN_LEFT,titleFont3);
                PDFUtils.addNewItem(document,""+ja3.getString(0), Element.ALIGN_LEFT,titleFont3);
                PDFUtils.addNewItem(document,"Del:"+diae+"/"+mese+"/"+anioe+" "+horae+":"+minutose, Element.ALIGN_LEFT,titleFont3);
                if(ja1.getString(11).equals("0000-00-00 00:00:00")) {
                }else{
                    PDFUtils.addNewItem(document,"Al:"+dias+"/"+mess+"/"+anios+" "+horas+":"+minutoss, Element.ALIGN_LEFT,titleFont3);
                }
                if(ja4.getString(9).equals("")) {
                }else{
                    PDFUtils.addNewItem(document,"Placas / Cono /Gafete / Credencial: "+ja4.getString(9), Element.ALIGN_LEFT,titleFont3);
                }
                PDFUtils.addNewItem(document,"QR:"+Conf.getQR(), Element.ALIGN_LEFT,titleFont3);
           } catch (JSONException e) {
                e.printStackTrace();
            }


            Observable.fromIterable(CasetaModelList)
                    .flatMap(model -> getBitmapFromURL(this,model,document))
                    .subscribeOn(Schedulers.computation())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(model ->{

                        PDFUtils.addNewItem(document,"www.linkom.mx \n\n\n", Element.ALIGN_CENTER,titleFont2);

                    },throwable -> {
                        dialog.dismiss();
                        Toast.makeText(this,throwable.getMessage(),Toast.LENGTH_SHORT).show();
                    },()->{
                        document.close();
                        dialog.dismiss();
                        Toast.makeText(this,"Generado Ticket....",Toast.LENGTH_SHORT).show();
                        printPDF();

                    });


        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {

            dialog.dismiss();
        }
    }

    private void printPDF() {
        PrintManager printManager= (PrintManager)getSystemService(Context.PRINT_SERVICE);
        try{
            PrintDocumentAdapter printDocumentAdapter= new PdfDocumentAdapter(this,new StringBuilder(getAppPath()).append(FILE_PRINT).toString(),FILE_PRINT);
            printManager.print("Document",printDocumentAdapter,new PrintAttributes.Builder().build());

        }catch (Exception ex){
            Toast.makeText(this,""+ex.getMessage(),Toast.LENGTH_SHORT).show();


        }
    }

    private Observable<CasetaModel> getBitmapFromURL(Context context, CasetaModel model, Document document) {
        return Observable.fromCallable(()->{
            Bitmap bitmap = Glide.with(context)
                    .asBitmap()
                    .load(model.getImage())
                    .submit().get();

            Image image = Image.getInstance(bitmapToByteArray(bitmap));
            image.scaleAbsolute(350 ,350);
            image.setAlignment(Element.ALIGN_CENTER);
            document.add(image);

            return model;
        });
    }



    private byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
        return stream.toByteArray();
    }


    private String getAppPath() {
        File dir = new File(getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), "Pictures");

        if (dir == null || !dir.mkdirs())
            dir.mkdir();
        return dir.getPath() + File.separator;
    }

    private void addDatos(){
        CasetaModel CasetaModel= null;
      
            CasetaModel = new CasetaModel("1","https://chart.googleapis.com/chart?chs=400x400&cht=qr&chl="+Conf.getQR()+"&.png","www.linkom.mx");
       

        CasetaModelList.add(CasetaModel);

    }



    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), EntradasSalidasActivity.class);
        startActivity(intent);
        finish();
    }
}
