<?php
//running local cmd exemple
//C:\xampp\htdocs\test> C:\xampp\php\php.exe C:\xampp\htdocs\test\test.php export_WL_mars2017_3_structure.csv ebou_socle.Structure
header('Content-Type: text/html; charset=iso-8859-1');

$update_statement = "UPDATE";
$set_statement = "SET";
$where_statement = "WHERE";

$execution_date=date("Y-m-d");

$check_csv=1;
$check_parameters=1;

//we need some parameters for this script to work
//for the first parameter we can give file path to convert or -h for help
if (!isset($argv[1])){
	$check_parameters = 0;
}
elseif ($argv[1] == "-h"){
	echo "Ce script permet de generer des requetes de type SQL UPDATE pour un fichier CSV separateur point-virgule. \n";
	echo "Le script genere un fichier dans le répertoire courant, merci de verifier les permissions du dossier. \n";
	echo "L'id de la ressource a mettre a jour doit se trouver dans la dernier colonne du fichier et s'appeller id \n";
	echo "1er parametre : chemin du fichier csv. \n";
	echo "2e parametre : nom de la table a mettre a jour. \n";
	$check_parameters = 0;
}
else{
	$input_file_path = $argv[1];
}

//second parameter is the table name we wish to update
if (!isset($argv[2])){
	$check_parameters = 0;
}
else{
	$database_table = $argv[2];
}

//all the parameters are not fully set
if ($check_parameters == 0){
	echo "Merci de renseigner correctement les parametres. Taper -h pour plus d'informations";
}
//parameters OK, let's go with the file !
else {
	echo "Verification des parametres : OK. \n";
	
	//open the source file readonly
	if (($handle = fopen($input_file_path, "r")) !== FALSE) {
		$headers = fgetcsv($handle, 0, ';');
		$number_headers = count($headers);
		
		//check that the last header is id
		if($headers[$number_headers-1] !== "id"){
			echo "Le dernier header du fichier doit etre l'id !";
			$check_csv=0;
		}
		// csv check seems ok, let's go further beyond
		if ($check_csv == 1){
			echo "Verification du fichier CSV : OK. \n";
			
			//open the file we wish to write
			$ouput_file = fopen("update_".$database_table."_".$execution_date.".csv", "w");
			echo "Le fichier "."update_".$database_table."_".$execution_date.".csv"." a ete generer avec succes. \n";
			echo "Traitement du fichier CSV en cours. \n";
			
			//get the values line by line
			while (($data = fgetcsv($handle, 0, ";")) !== FALSE) {
				$num = count($data);
				
				//generate the sql query
				$final_string = $update_statement." ".$database_table." ".$set_statement." ";
				for ($c = 0; $c < $num; $c++) {
					
					//values to update BUT the last one
					if ($c < ($num -2)){
						if ($data[$c] !== "NULL"){
							$final_string .= $headers[$c]."='".addslashes($data[$c])."', ";
						}
						else{
							$final_string .= $headers[$c]."=".$data[$c].", ";
						}
					}
					
					//last value to update
					else if($c == ($num -2)){
						if ($data[$c] !== "NULL"){
							$final_string .= $headers[$c]."='".addslashes($data[$c])."' ";
						}
						else{
							$final_string .= $headers[$c]."=".$data[$c]." ";
						}
					}
					
					//this is the ID value, here we add a where clause
					else{
						if ($data[$c] !== "NULL"){
							$final_string .= $where_statement." ".$headers[$c]."='".addslashes($data[$c])."';";
						}
						else{
							$final_string .= $where_statement." ".$headers[$c]."=".$data[$c].";";
						}
					}
				}
				
				//add an end of line to a better visualisation
				$final_string .= PHP_EOL;
				
				//write the generated line
				fwrite($ouput_file, $final_string);
				//echo $final_string;
			}
		}
		//file treatment finished, we close both open files
		fclose($handle);
		fclose($ouput_file);
	}
	echo "Le script s'est execute avec succes !";
}
?>