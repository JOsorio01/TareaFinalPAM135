<?php

include_once("db_controller.php");

class Producto {
    private $producto = array();

    public function getProductos() {

        $query = "SELECT id_producto, nombre_producto, existencias FROM producto";
        $db = new DBController();
        $this->producto = $db->executableSelectQuery($query);

        return json_encode($this->producto);
    }

    public function authValidate($username, $password) {
        $password = md5($password);
        $query = "SELECT * FROM inventario_user WHERE username = '{$username}' AND password = '{$password}'";
        $db = new DBController();
        if (json_encode($db->executableSelectQuery($query)) != "null") {
            return true;
        } else {
            return false;
        }
    }
}

$producto = new Producto();
if ($producto->authValidate($_SERVER['PHP_AUTH_USER'], $_SERVER['PHP_AUTH_PW'])) {
    echo $producto->getProductos();
}
?>