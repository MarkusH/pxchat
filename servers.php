<?php
    // show errors
    error_reporting(E_ALL | E_STRICT);

    // MySQL daten
    define("MYSQL_HOST",     "localhost");
    define("MYSQL_USER",     "pxchat");
    define("MYSQL_PASS",     "pxchat");
    define("MYSQL_DATABASE", "pxchat");
    
    $connection = mysql_connect(MYSQL_HOST, MYSQL_USER, MYSQL_PASS) or die("Could not connect to database: " . mysql_error());
    mysql_select_db(MYSQL_DATABASE, $connection) or die("Could not select database: " . mysql_error($connection));
    
    // read GET
    $action = isset($_GET["action"]) ? $_GET["action"] : null;
    $name   = isset($_GET["name"])   ? mysql_real_escape_string($_GET["name"], $connection)   : null;
    $port   = isset($_GET["port"])   ? mysql_real_escape_string($_GET["port"], $connection)   : null;
    // server address
    $address = isset($_SERVER["REMOTE_ADDR"]) ? mysql_real_escape_string($_SERVER["REMOTE_ADDR"], $connection) : "127.0.0.1";
    
    function cron($interval) {
        global $connection;
        $sql = "DELETE FROM servers WHERE time < '" . (time() - $interval * 60) . "'";
        mysql_query($sql, $connection);
    }
    
    function add($addr, $port, $name) {
        global $connection;
        $time = time();
        $sql = "INSERT INTO servers (address, port, time, name) VALUES
                    ('" . $addr . "', '" . $port . "', '" . $time . "', '" . $name . "')
                    ON DUPLICATE KEY UPDATE time = '" . $time . "', name = '" . $name . "'";
        mysql_query($sql, $connection);
        echo "Added " . $addr . " " . $port . " " . $name . " -";
    }
    
    function del($addr) {
        global $connection;
        $sql = "DELETE FROM servers WHERE address = '" . $addr . "' AND port = '" . $port . "'";
        mysql_query($sql);
    }
    
    function printList() {
        global $connection;
        $sql = "SELECT address, port, name FROM servers";
        $result = mysql_query($sql);
        while ($row = mysql_fetch_assoc($result)) {
            echo $row['address'] . " " . $row['port'] . " " . $row['name'] . "\n";
        }
    }
    // action query
    switch ($action) {
        case "add":
            cron(5);
            add($address, $port, $name);
            break;
        case "del":
            cron(5);
            del($address);
            break;
        default:
            cron(5);
            printList();
            break;
    }
?>
