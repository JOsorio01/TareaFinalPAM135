package sv.edu.ues.om13001.inventario;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener {

    private String baseUrl;
    private String username;
    private String password;
    private String content_type;

    Button btn_inventario;
    Button btn_proveedores;
    ListView lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        baseUrl = intent.getStringExtra("baseUrl");
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");

        btn_inventario = findViewById(R.id.inventario);
        btn_inventario.setOnClickListener(this);
        btn_proveedores = findViewById(R.id.proveedores);
        btn_proveedores.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.inventario) {
            content_type = "Inventario";
        }
        else if (v.getId() == R.id.proveedores) {
            content_type = "Proveedores";
        }
        startListActivity();
    }


    public void startListActivity() {
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("password", password);
        bundle.putString("baseUrl", baseUrl);
        bundle.putString("content_type", content_type);
        Intent intent = new Intent(this, ListActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
