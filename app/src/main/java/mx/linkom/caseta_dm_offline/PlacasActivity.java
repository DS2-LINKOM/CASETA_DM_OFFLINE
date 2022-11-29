package mx.linkom.caseta_dm_offline;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class PlacasActivity extends mx.linkom.caseta_dm_offline.Menu {

    private Configuracion Conf;
    EditText placas;
    Button Buscar;
    JSONArray ja1,ja2;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_placas);

        Conf = new Configuracion(this);

        placas = (EditText) findViewById(R.id.setPlacas);
        Buscar = (Button) findViewById(R.id.btnBuscar);

        placas.setFilters(new InputFilter[] { filter,new InputFilter.AllCaps() {
        } });

        Buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check();
            }});

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


    public void check() {

        String url = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/placas_1.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                response = response.replace("][",",");
                if (response.length()>0){
                    try {
                        ja1 = new JSONArray(response);
                        Conf.setPlacas(ja1.getString(4));
                        Conf.setSTPlacas("Registrado");
                        Intent i = new Intent(getApplicationContext(), PlacasInfoActivity.class);
                        startActivity(i);
                        finish();
                    } catch (JSONException e) {
                        check2();
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
                params.put("Placas", placas.getText().toString().trim());
                params.put("id_residencial", Conf.getResid().trim());

                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void check2() {

        String url = "https://demoarboledas.privadaarboledas.net/plataforma/casetaV2/controlador/CC/placas_2.php?bd_name="+Conf.getBd()+"&bd_user="+Conf.getBdUsu()+"&bd_pwd="+Conf.getBdCon();

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                response = response.replace("][",",");
                if (response.length()>0){
                    try {
                        ja2 = new JSONArray(response);

                        Conf.setPlacas(ja2.getString(9));
                        Conf.setSTPlacas("RegistradoVisita");
                        Intent i = new Intent(getApplicationContext(), PlacasInfoActivity.class);
                        startActivity(i);
                        finish();
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(),"Usario no registrado", Toast.LENGTH_LONG).show();
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
                params.put("Placas", placas.getText().toString().trim());
                params.put("id_residencial", Conf.getResid().trim());

                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), ReportesActivity.class);
        startActivity(intent);
        finish();
    }

}
