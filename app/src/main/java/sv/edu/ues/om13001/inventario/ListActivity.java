package sv.edu.ues.om13001.inventario;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private String content_type;
    private String baseUrl;
    private String username;
    private String password;
    public ArrayList<Producto> productos;
    public ArrayList<Proveedor> proveedores;

    FrameLayout frame_producto;
    FrameLayout frame_proveedor;
    FrameLayout background;

    Button btnProductos;
    Button btnProveedores;

    EditText edt_nombre_producto;
    EditText edt_existencias;
    EditText edt_nombre_proveedor;

    ListView lista;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Intent intent = getIntent();

        edt_nombre_producto = findViewById(R.id.nombre_producto);
        edt_existencias = findViewById(R.id.existencias);
        edt_nombre_proveedor = findViewById(R.id.nombre_proveedor);

        baseUrl = intent.getStringExtra("baseUrl");
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");
        content_type = intent.getStringExtra("content_type");

        btnProductos = findViewById(R.id.btn_producto);
        btnProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarProducto();
            }
        });

        btnProveedores = findViewById(R.id.btn_proveedor);
        btnProveedores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agregarProveedor();
            }
        });

        frame_producto = findViewById(R.id.frame_productos);
        frame_proveedor = findViewById(R.id.frame_proveedores);
        background = findViewById(R.id.background_frame);

        lista = findViewById(R.id.lista);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent inte = new Intent(getApplicationContext(), ListItemActivity.class);
                inte.putExtra("content_type", content_type);
                inte.putExtra("baseUrl", baseUrl);
                inte.putExtra("username", username);
                inte.putExtra("password", password);
                if (content_type.equals("Inventario")) {
                    inte.putExtra("id", productos.get(position).getIdProducto());
                    inte.putExtra("nombre", productos.get(position).getNombreProducto());
                    inte.putExtra("existencias", productos.get(position).getExistencias());
                }
                else if (content_type.equals("Proveedores")) {
                    inte.putExtra("id", proveedores.get(position).getIdProveedor());
                    inte.putExtra("nombre", proveedores.get(position).getNombreProveedor());
                }
                startActivity(inte);
            }
        });

        getSupportActionBar().setTitle(this.content_type);
        RestClient restClient;
        if (content_type.equals("Inventario")) {
            restClient = new RestClient(baseUrl + "producto.php", username, password);
            restClient.setSource("Inventario");
            AsyncTask<Void, Void, String> execute = new ListActivity.ExecutableNetworkOperation(restClient);
            execute.execute();
        } else if (content_type.equals("Proveedores")) {
            restClient = new RestClient(baseUrl + "proveedores.php", username, password);
            restClient.setSource("Proveedores");
            AsyncTask<Void, Void, String> execute = new ListActivity.ExecutableNetworkOperation(restClient);
            execute.execute();
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                background.setVisibility(View.VISIBLE);
                if (content_type.equals("Inventario")) {
                    animar(true, frame_producto);
                    frame_producto.setVisibility(View.VISIBLE);
                }
                else if (content_type.equals("Proveedores")){
                    animar(true, frame_proveedor);
                    frame_proveedor.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    public class ExecutableNetworkOperation extends AsyncTask<Void, Void, String> {

        private RestClient restClient;
        private String response = "";

        public ExecutableNetworkOperation(RestClient restClient) {
            this.restClient = restClient;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (!response.equals("") && !response.equals("exito")) {
                JSONArray jsonArray = restClient.getLastResponseAsJsonArray();
                ArrayList<String> datos = new ArrayList<String>();
                if (restClient.getSource().equals("Inventario")) {
                    productos = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject elem;
                        try {
                            elem = jsonArray.getJSONObject(i);
                            datos.add(elem.getString("nombre_producto") +
                                    "\nId: " + elem.getString("id_producto") +
                                    "\nExistencias: " + elem.getString("existencias"));
                            productos.add(new Producto(elem.getInt("id_producto"), elem.getString("nombre_producto"), elem.getInt("existencias")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (restClient.getSource().equals("Proveedores")) {
                    proveedores = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject elem;
                        try {
                            elem = jsonArray.getJSONObject(i);
                            datos.add(elem.getString("nombre_proveedor"));
                            proveedores.add(new Proveedor(elem.getInt("id_proveedor"), elem.getString("nombre_proveedor")));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, datos);
                setAdapter(adapter);

            }
            else if (response.equals("exito")) {
                Toast.makeText(getApplicationContext(), "La operación se completó con éxito", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "No se pudo realizar la transacción", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                response = restClient.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
    public void setAdapter(ArrayAdapter<String> adapter) {
        lista.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        background.setVisibility(View.GONE);
        if (frame_producto.getVisibility() == View.VISIBLE) {
            animar(false, frame_producto);
            frame_producto.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        else if (frame_proveedor.getVisibility() == View.VISIBLE) {
            animar(false, frame_proveedor);
            frame_proveedor.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        else {
            super.onBackPressed();
        }
    }

    private void animar(boolean mostrar, FrameLayout layout) {
        AnimationSet set = new AnimationSet(true);
        Animation animation = null;
        if (mostrar) {
            //desde la esquina inferior derecha a la superior izquierda
            animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        }
        else {    //desde la esquina superior izquierda a la esquina inferior derecha
            animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        }
        //duración en milisegundos
        animation.setDuration(500);
        set.addAnimation(animation);
        LayoutAnimationController controller = new LayoutAnimationController(set, 0.25f);

        layout.setLayoutAnimation(controller);
        layout.startAnimation(animation);
    }

    public void agregarProducto() {
        String data = "iud_producto.php?";
        data += "nombre_producto=" + edt_nombre_producto.getText().toString().replace(" ", "%20") +
                "&existencias=" + edt_existencias.getText().toString() +
                "&op=insert";
        RestClient restClient;
        restClient = new RestClient(baseUrl + data, username, password);
        AsyncTask<Void, Void, String> execute = new ListActivity.ExecutableNetworkOperation(restClient);
        execute.execute();
        background.setVisibility(View.GONE);
        animar(false, frame_producto);
        frame_producto.setVisibility(View.GONE);

        reload();
    }

    public void agregarProveedor() {
        String data = "iud_proveedores.php?";
        data += "nombre_proveedor=" + edt_nombre_proveedor.getText().toString().replace(" ", "%20") +
                "&op=insert";
        RestClient restClient;
        restClient = new RestClient(baseUrl + data, username, password);
        AsyncTask<Void, Void, String> execute = new ListActivity.ExecutableNetworkOperation(restClient);
        execute.execute();
        background.setVisibility(View.GONE);
        animar(false, frame_proveedor);
        frame_proveedor.setVisibility(View.GONE);

        reload();
    }

    public void reload() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        finish();
        startActivity(getIntent());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        reload();
    }
}
