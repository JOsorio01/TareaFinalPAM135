<?php

class DBController {
    private $conn = "";
    private $host = "localhost";
    private $user = "postgres";
    private $password = "postgres";
    private $database = "inventario_pam135";
    private $port = 5432;

    function __construct() {
        $conn = $this->connectDB();
        if (!empty($conn)) {
            $this->conn = $conn;
        }
    }

    function connectDB() {
        $conn = pg_connect("host={$this->host} dbname={$this->database} port={$this->port} user={$this->user} password={$this->password}");
        return $conn;
    }

    function executableSelectQuery($query) {
        $result = pg_query($this->conn, $query);
        while ($row = pg_fetch_assoc($result)) {
            $resultset[] = $row;
        }
        if (!empty($resultset)) {
            return $resultset;
        }

        pg_free_result($result);

        pg_close($this->conn);
    }

    function affectedRows($query) {
        $result = pg_query($this->conn, $query);
        return pg_affected_rows($result);
    }

    function resultQuery($query) {
        $result = pg_query($this->conn, $query);
        return $result;
    }
}
?>