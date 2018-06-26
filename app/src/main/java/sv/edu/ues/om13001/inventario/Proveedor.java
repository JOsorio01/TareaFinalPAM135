package sv.edu.ues.om13001.inventario;

public class Proveedor {

    private int id_proveedor;
    private String nombre_proveedor;

    public Proveedor(int id_proveedor, String nombre_proveedor) {
        this.id_proveedor = id_proveedor;
        this.nombre_proveedor = nombre_proveedor;
    }

    public int getIdProveedor() {
        return id_proveedor;
    }

    public String getNombreProveedor() {
        return nombre_proveedor;
    }
}
