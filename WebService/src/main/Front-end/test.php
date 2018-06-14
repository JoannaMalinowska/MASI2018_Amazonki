<?php

	$test_string = $_POST['scan'];

	//$test_string = intval($test_string);

	if(is_integer($test_string)==true){

		echo json_encode("to jest liczba");
	}else{
		echo json_encode("to jest string");
	}

?>