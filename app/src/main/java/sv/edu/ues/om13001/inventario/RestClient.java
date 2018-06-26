package sv.edu.ues.om13001.inventario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RestClient {

    private String baseUrl;
    private String username;
    private String password;
    private String urlResource;
    private String httpMethod;
    private String urlPath;
    private String lastResponse;
    private String payload;
    private String source;
    private HashMap<String, String> parameters;
    private Map<String, List<String>> headerFields;

    /**
     *  @param baseUrl String
     *  @param username String
     *  @param password String
     */
    public RestClient(String baseUrl, String username, String password) {
        setBaseUrl(baseUrl);
        this.username = username;
        this.password = password;
        this.urlResource = "";
        this.urlPath = "";
        this.httpMethod = "GET";
        parameters = new HashMap<>();
        lastResponse = "";
        payload = "";
        headerFields = new HashMap<>();

        System.setProperty("jsse.enableSNIExtension", "false");
    }

    /**
     * --&gt;http://BASE_URL.COM&lt;--/resource/path
     * @param baseUrl la raiz del URL
     * @return this
     */
    public RestClient setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        if (!baseUrl.substring(baseUrl.length() - 1).equals("/")) {
            this.baseUrl += "/";
        }
        return this;
    }

    /**
     * Asigna el nombre del recurso usado para llamar la API Rest
     * @param urlResource http://base_url.com/--&gt;URL_RESOURCE&lt;--url_path
     * @return this
     */
    public RestClient setUrlResource(String urlResource) {
        this.urlResource = urlResource;
        return this;
    }

    public void setSource(String source) {
        this.source = source;
    }
    public String getSource() {
        return this.source;
    }

    /**
     * Asigna el path usado para llamar la API Rest
     * Usualmente es un numero ID para funciones Get single record, PUT y DELETE.
     * @param urlPath http://base_url.com/resource/--&gt;URL_PATH&lt;--url_path
     * @return this
     */
    public final RestClient setUrlPath(String urlPath) {
        this.urlPath = urlPath;
        return this;
    }

    /**
     * Asigna el metodo HTTP usado en la API Rest
     * GET, PUT, POST o DELETE
     * @return this
     */
    public RestClient setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    /**
     * Obtiene la salida de la ultima llamada a la API Rest
     * @return String
     */
    public String getLastResponse() {
        return lastResponse;
    }

    /**
     * Obtiene una lista de cabeceras retornadas por la ultima llamada a la API Rest
     * @return Map&lt;String, List&lt;String&gt;&gt;
     */
    public Map<String, List<String>> getHeaderFields() {
        return headerFields;
    }

    /**
     * Asigna un parametro usado en la llamada de la API Rest
     * @param key el nombre del parametro
     * @param value el valor del parametro
     * @return this
     */
    public RestClient setParameters(String key, String value) {
        this.parameters.put(key, value);
        return this;
    }

    /**
     * Remueve un parametro especifico
     * @param key nombre del parametro a remover
     * @return
     */
    public RestClient removeParameter(String key) {
        this.parameters.remove(key);
        return this;
    }

    /**
     * Borra todos los valores usados para hacer las llamadas con la API Rest
     * @return
     */
    public RestClient clearAll() {
        parameters.clear();
        baseUrl = "";
        this.username = "";
        this.password = "";
        this.urlResource = "";
        this.urlPath = "";
        this.httpMethod = "";
        lastResponse = "";
        payload = "";
        headerFields.clear();
        return this;
    }

    /**
     * Obtiene la ultima respuesta de la API Rest como un JSONObject
     * @return
     */
    public JSONObject getLastResponseAsJsonObject() {
        try {
            return new JSONObject(String.valueOf(lastResponse));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Obtiene la ultima respuesta de la API Rest como un JSONArray
     * @return
     */
    public JSONArray getLastResponseAsJsonArray() {
        try {
            return new JSONArray(String.valueOf(lastResponse));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Obtiene el payload como un string desde parametros existentes
     * @return String
     */
    private String getPayloadAsString() {
        StringBuilder stringBuffer = new StringBuilder();
        Iterator it = parameters.entrySet().iterator();
        int count = 0;
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (count > 0) stringBuffer.append('&');

            stringBuffer.append(pair.getKey()).append('=').append(pair.getValue());

            it.remove();
            count++;
        }
        return stringBuffer.toString();
    }

    public String execute() {
        String line;
        StringBuilder outputStringBuilder = new StringBuilder();

        try {
            StringBuilder urlString = new StringBuilder(baseUrl + urlResource);

            if (!urlPath.equals("")) {
                urlString.append("/" + urlPath);
            }

            if (parameters.size() > 0 && httpMethod.equals("GET")) {
                this.payload = getPayloadAsString();
                urlString.append("?" + this.payload);
            }
            URL url = new URL(urlString.toString());

            String encoding = Base64Encoder.encode(username + ":" + password);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(httpMethod);
            connection.setRequestProperty("Authorization", "Basic " + encoding);
            connection.setRequestProperty("Accept", "aplication/json");
            connection.setRequestProperty("Connect-Type", "text/plain");

            if (httpMethod.equals("POST") || httpMethod.equals("PUT")) {

                payload = getPayloadAsString();

                connection.setDoInput(true);
                connection.setDoOutput(true);

                try {
                    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
                    writer.write(this.payload);

                    headerFields = connection.getHeaderFields();

                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        outputStringBuilder.append(line);
                    }
                } catch (Exception e) { }
                connection.disconnect();
            }
            else {
                InputStream content = (InputStream) connection.getInputStream();
                headerFields = connection.getHeaderFields();
                BufferedReader in = new BufferedReader(new InputStreamReader(content));

                while ((line = in.readLine()) != null) {
                    outputStringBuilder.append(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!outputStringBuilder.toString().equals("")) {
            lastResponse = outputStringBuilder.toString();
        }

        return outputStringBuilder.toString();
    }
}
