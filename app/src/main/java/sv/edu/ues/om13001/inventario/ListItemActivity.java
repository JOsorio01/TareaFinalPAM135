package sv.edu.ues.om13001.inventario;

import android.content.Intent;
import android.os.AsyncTask;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ListItemActivity extends AppCompatActivity {

    private String content_type;
    private String identificador;
    private String nombre;
    private String existencias;
    private String baseUrl;
    private String username;
    private String password;

    EditText edt_id;
    EditText edt_nombre;
    EditText edt_existencias;

    TextView tv_nombre;
    TextView tv_existencias;

    Button btn_actualizar;
    Button btn_eliminar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_item);
        Intent intent = getIntent();
        content_type = intent.getStringExtra("content_type");

        baseUrl = intent.getStringExtra("baseUrl");
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");
        content_type = intent.getStringExtra("content_type");
        identificador = "" + intent.getIntExtra("id", 0);
        nombre = intent.getStringExtra("nombre");
        existencias = "" + intent.getIntExtra("existencias", 0);

        tv_nombre = findViewById(R.id.li_nombre);
        tv_existencias = findViewById(R.id.li_existencias);

        edt_id = findViewById(R.id.li_identificador);
        edt_nombre = findViewById(R.id.li_edt_nombre);
        edt_existencias = findViewById(R.id.li_edt_existencias);

        edt_id.setText("" + identificador);
        edt_nombre.setText(nombre);

        if (content_type.equals("Inventario")) {
            tv_nombre.setText("Nombre del Producto");
            tv_existencias.setVisibility(View.VISIBLE);
            edt_existencias.setVisibility(View.VISIBLE);
            edt_existencias.setText("" + existencias);
        }

        btn_actualizar = findViewById(R.id.btn_actualizar);
        btn_actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nombre = edt_nombre.getText().toString();
                String data = "";
                if (content_type.equals("Inventario")) {
                    existencias = edt_existencias.getText().toString();
                    data = actualizarProducto();
                } else if (content_type.equals("Proveedores")) {
                    data = actualizarProveedor();
                }
                RestClient restClient;
                restClient = new RestClient(baseUrl + data, username, password);
                AsyncTask<Void, Void, String> execute = new ListItemActivity.ExecutableNetworkOperation(restClient);
                execute.execute();
                finish();
            }
        });

        btn_eliminar = findViewById(R.id.btn_eliminar);
        btn_eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = "";
                if (content_type.equals("Inventario")) {
                    data = eliminarProducto();
                } else if (content_type.equals("Proveedores")) {
                    data = eliminarProveedor();
                }
                RestClient restClient;
                restClient = new RestClient(baseUrl + data, username, password);
                AsyncTask<Void, Void, String> execute = new ListItemActivity.ExecutableNetworkOperation(restClient);
                execute.execute();
                finish();
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

            if (response.equals("exito")) {
                Toast.makeText(getApplicationContext(), "Realizado con éxito", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getApplicationContext(), "Falló al iniciar sesión", Toast.LENGTH_LONG).show();
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

    public String actualizarProducto() {
        return "iud_producto.php?id=" + identificador +
                "&nombre_producto=" + nombre +
                "&existencias=" + existencias +
                "&op=update";
    }

    public String actualizarProveedor() {
        return "iud_proveedores.php?id=" + identificador +
                "&nombre_proveedor=" + nombre +
                "&op=update";
    }

    public String eliminarProducto() {
        return "iud_producto.php?id=" + identificador +
                "&op=delete";
    }

    public String eliminarProveedor() {
        return "iud_proveedores.php?id=" + identificador +
                "&op=delete";
    }
}
