<?php

include_once("db_controller.php");

class Producto {

    public function insertProducto($id, $nombre_producto, $existencias) {
        $query = "INSERT INTO producto VALUES ({$id}, '{$nombre_producto}', {$existencias})";
        $db = new DBController();
        $rows = $db->affectedRows($query);
        if ($rows > 0) {
            return "exito";
        }
    }

    public function setIdProducto() {
        $query = "SELECT MAX(id_producto) FROM producto";
        $db = new DBController();
        return pg_fetch_row($db->resultQuery($query))[0] + 1;
    }

    public function updateProducto($id, $nombre_producto, $existencias) {
        $query = "UPDATE producto SET nombre_producto = '{$nombre_producto}', existencias = {$existencias} WHERE id_producto = {$id}";
        $db = new DBController();
        $rows = $db->affectedRows($query);
        if ($rows > 0) {
            return "exito";
        }
    }

    public function deleteProducto($id) {
        $query = "DELETE FROM producto WHERE id_producto = {$id}";
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

$producto = new Producto();
$op = $_REQUEST['op'];
if ($producto->authValidate($_SERVER['PHP_AUTH_USER'], $_SERVER['PHP_AUTH_PW'])) {
    if ($op == 'insert/') {
        $id = $producto->setIdProducto();
        echo $producto->insertProducto($id, $_REQUEST['nombre_producto'], $_REQUEST['existencias']);
    }
    elseif ($op == 'update/') {
        echo $producto->updateProducto($_REQUEST['id'], $_REQUEST['nombre_producto'], $_REQUEST['existencias']);
    }
    elseif ($op == 'delete/') {
        echo $producto->deleteProducto($_REQUEST['id']);
    }
}