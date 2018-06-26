package sv.edu.ues.om13001.inventario;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private String baseUrl;
    private String username;
    private String password;

    Button button_login;
    Boolean logged = false;
    EditText edt_username;
    EditText edt_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        baseUrl = "http://192.168.1.11/pam135_ws/";

        button_login = findViewById(R.id.login);
        button_login.setOnClickListener(this);

        edt_username = findViewById(R.id.username);
        edt_password = findViewById(R.id.password);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (logged) {
            setContentView(R.layout.activity_home);
        }
    }



    @Override
    public void onClick(View v) {
        username = edt_username.getText().toString();
        password = edt_password.getText().toString();

        RestClient restClient = new RestClient(baseUrl + "user.php/", username, password);

        AsyncTask<Void, Void, String> execute = new ExecutableNetworkOperation(restClient);
        execute.execute();
    }

    public class ExecutableNetworkOperation extends AsyncTask<Void, Void, String> {

        private RestClient restClient;
        private String isValidCredentials = "";

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

            if (isValidCredentials.equals("true")) {
                startHomeActivity();
            }
            else {
                Toast.makeText(getApplicationContext(), "Falló al iniciar sesión", Toast.LENGTH_LONG).show();
            }

        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                isValidCredentials = restClient.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public void startHomeActivity() {
        logged = true;

        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        bundle.putString("password", password);
        bundle.putString("baseUrl", baseUrl);

        Intent intent = new Intent(this, HomeActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
