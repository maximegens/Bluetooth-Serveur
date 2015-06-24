<?php
		$contenu='';
		$compteur = 0;
		foreach($_POST as $key => $value) {
			if($key == 'date'){
				$contenu.='<h3>Données recues le '.utf8_encode($value).'</h3>';
			}else if($key == 'adressMac'){
				$contenu.='<h4>Depuis l\'appareil éméteur : '.utf8_encode($value).'</h4>';
			}else if(empty($value) && $key == 'nom'.$compteur){
				$contenu.="<span style='font-weight: bold;'>Inconnu</span><br/>";
			}else if(empty($value)){
				$contenu.='Inconnu<br/>';
			}else{
				if($key == 'nom'.$compteur){
					$contenu.="<span style='font-weight: bold;'>".utf8_encode($value).'</span><br/>';
				}else if($key == 'type'.$compteur){
					$contenu.=utf8_encode($value).'<br/></br>';
					$compteur++;
				}else{
					$contenu.=utf8_encode($value).'<br/>';
				}
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
