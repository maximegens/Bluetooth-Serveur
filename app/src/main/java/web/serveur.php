<?php
		$contenu='';
		foreach($_POST as $key => $value) {
			if($key == 'date'){
				$contenu.='<h3>Données recues le '.$value.'</h3><br/>';
			}
			if(empty($value)){
				$contenu.="Inconnu<br/>";
			}else{
				$contenu.=$value.'<br/>';
			}
		}

		// specify the file where we will save the contents of the variable message
		$filename="androidmessages.html";
		// write (append) the data to the file
		file_put_contents($filename,$contenu,FILE_APPEND | LOCK_EX);
		// load the contents of the file to a variable
		$androidmessages=file_get_contents($filename);
		// display the contents of the variable (which has the contents of the file)
		echo $androidmessages;
	
?>
