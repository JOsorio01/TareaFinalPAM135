package sv.edu.ues.om13001.inventario;

public class Producto {

    private int id_producto;
    private String nombre_producto;
    private int existencias;

    public Producto(int id_producto, String nombre_producto, int existencias) {
        this.id_producto = id_producto;
        this.nombre_producto = nombre_producto;
        this.existencias = existencias;
    }

    public int getIdProducto() {
        return this.id_producto;
    }

    public String getNombreProducto() {
        return this.nombre_producto;
    }

    public int getExistencias() {
        return this.existencias;
    }
}
