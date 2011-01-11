<?php
	// show errors
	error_reporting(E_ALL);

	// MySQL daten
	define('MYSQL_HOST',     'localhost');
	define('MYSQL_USER',     'root');
	define('MYSQL_PASS',     '!!!_PASSWORD_!!!'); //define pw here
	define('MYSQL_DATABASE', 'pxchat');
	
	$action = "undef";
	$name = "undef";
	$address = "192.168.2.1";
	$port = "12345";

	// read GET
	if (isset($_GET['action'])) {
		$action = $_GET['action'];
	}
	
	if (isset($_GET['name'])) {
		$name = $_GET['name'];
	}

	if (isset($_GET['port'])) {
		$port = $_GET['port'];
	}
	
	// save address
	$address = $_SERVER['REMOTE_ADDR'];
	
	
	@mysql_connect(MYSQL_HOST, MYSQL_USER, MYSQL_PASS) OR
        	die("Could not connect to database: " . mysql_error());

    	mysql_select_db(MYSQL_DATABASE) OR
        	die("Could not select database: " . mysql_error());
	
	function cron($interval)
	{
		$sql= "SELECT id, time FROM servers";
			
		$result = mysql_query($sql) OR
			die(mysql_error());
				
		while ($row = mysql_fetch_assoc($result)) {
        		if (time() - $row['time'] >= ($interval * 60)) {
				$sql= "DELETE FROM servers WHERE id='".$row['id']."';";
				$res = mysql_query($sql) OR
					die(mysql_error());
			}
		}
	}
	
	function add($addr, $port, $name)
	{
		$time = time();
		$sql= "INSERT INTO servers (address, port, time, name) VALUES ('".$addr."','".$port."','".$time."','".$name."')";
		$result = mysql_query($sql) OR die(mysql_error());
		echo "Added ".$addr." ".$port." ".$name." -";
	}
	
	function del($addr)
	{
		$sql= "DELETE FROM servers WHERE address='".$addr."';";
		$result = mysql_query($sql) OR 
			die(mysql_error());
	}
	
	
	function printList()
	{
		$sql="SELECT address, port, name FROM servers;";
		$result = mysql_query($sql) OR
			die(mysql_error());
		
		while ($row = mysql_fetch_assoc($result)) {
			echo $row['address']." ".$row['port']." ".$row['name']."\n";
		}
	}
	
	// action query
	
	switch ($action) {
	 
	 case "undef":
	 	cron(5);
		printList();
	 	break;
	 case "add":
	 	cron(5);
	 	add($address, $port, $name);
		break;
	 case "del":
	 	cron(5);
	 	del($address);
	 	break;
	 case "list":
	 	cron(5);
	 	printList();
		break;
	}
	
	
	

?>
