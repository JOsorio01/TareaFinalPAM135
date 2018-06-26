<?php

include_once("db_controller.php");

class Proveedor {
    private $proveedores = array();

    public function getProveedores() {

        $query = "SELECT id_proveedor, nombre_proveedor FROM proveedores";
        $db = new DBController();
        $this->proveedores = $db->executableSelectQuery($query);

        return json_encode($this->proveedores);
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

$proveedores = new Proveedor();
if ($proveedores->authValidate($_SERVER['PHP_AUTH_USER'], $_SERVER['PHP_AUTH_PW'])) {
    echo $proveedores->getProveedores();
}
?>