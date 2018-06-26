<?php

include_once("db_controller.php");

class Proveedor {

    public function insertProveedor($id, $nombre_proveedor) {
        $query = "INSERT INTO proveedores VALUES ({$id}, '{$nombre_proveedor}')";
        $db = new DBController();
        $rows = $db->affectedRows($query);
        if ($rows > 0) {
            return "exito";
        }
    }

    public function setIdProveedor() {
        $query = "SELECT MAX(id_proveedor) FROM proveedores";
        $db = new DBController();
        return pg_fetch_row($db->resultQuery($query))[0] + 1;
    }

    public function updateProveedor($id, $nombre_proveedor) {
        $query = "UPDATE proveedores SET nombre_proveedor = '{$nombre_proveedor}' WHERE id_proveedor = {$id}";
        $db = new DBController();
        $rows = $db->affectedRows($query);
        if ($rows > 0) {
            return "exito";
        }
    }

    public function deleteProveedor($id) {
        $query = "DELETE FROM proveedores WHERE id_proveedor = {$id}";
        $db = new DBController();
        $rows = $db->affectedRows($query);
        if ($rows > 0) {
            return "exito";
        }
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

$proveedor = new Proveedor();
$op = $_REQUEST['op'];
if ($proveedor->authValidate($_SERVER['PHP_AUTH_USER'], $_SERVER['PHP_AUTH_PW'])) {
    if ($op == 'insert/') {
        $id = $proveedor->setIdProveedor();
        echo $proveedor->insertProveedor($id, $_REQUEST['nombre_proveedor']);
    }
    elseif ($op == 'update/') {
        echo $proveedor->updateProveedor($_REQUEST['id'], $_REQUEST['nombre_proveedor']);
    }
    elseif ($op == 'delete/') {
        echo $proveedor->deleteProveedor($_REQUEST['id']);
    }
}