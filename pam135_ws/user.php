<?php
include_once("db_controller.php");

Class User {
    private $user = array();
    private $username;
    private $password;

    public function getUser() {
        $query = "SELECT * FROM inventario_user WHERE username = '{$this->username}' AND password = '{$this->password}'";
        $db = new DBController();
        $this->user = $db->executableSelectQuery($query);
        return $this->user;
    }

    public function setData($user, $password) {
        $this->username = $user;
        $this->password = md5($password);
    }
}

$user = new User();
$user->setData($_SERVER['PHP_AUTH_USER'], $_SERVER['PHP_AUTH_PW']);
if (json_encode($user->getUser()) != "null") {
    echo "true";
}
?>
