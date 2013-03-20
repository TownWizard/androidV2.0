#!/usr/bin/php

<?php

define("PARTNERS_SITE", 'http://www.townwizardcontainerapp.com/apiv30/partner');
define("PARTNERS_FILE", 'partners/partners.json');
define("MAX_PARTNER_ID", 200);

function main($argc, $argv) {
	if($argc > 1) $max_partner_id = intval($argv[1]);
	else $max_partner_id = MAX_PARTNER_ID;
	
	print "Updating up to $max_partner_id partners\n";
	
	$json = getPartnersJsonAsString($max_partner_id);
	print "Writing json to partners file... ";
	file_put_contents(PARTNERS_FILE, $json);
	print "done\n";
}

function getPartnersJsonAsString($max_partner_id) {
	$partnersJson = "[\n";
	for($i = 1; $i <= $max_partner_id; $i++) {
		$partnerJson = getJson($i);		
		if(!empty($partnerJson)) {
			$partnersJson = $partnersJson . $partnerJson . ",\n";
		}
	}	
	$partnersJson = substr($partnersJson, 0, -2); //remove last comma and \n
	$partnersJson = $partnersJson . "\n]";
	return $partnersJson;
}

function getJson($id) {
	print "Getting partner $id ... ";
	$url = PARTNERS_SITE."/$id";
	$ch = curl_init($url);
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
	$responseMessage = curl_exec($ch);
	$status = curl_getinfo($ch);	
	$statusCode = $status["http_code"];
	curl_close($ch);
	
	if($statusCode != 200) {
		throw new Exception("Received status code $statusCode");
	}
	
	$partnerJson = "";
	$responseData = json_decode($responseMessage);
	$error = $responseData->error;
	if(empty($error)) {
	  $partner = $responseData->data;
	  if(!empty($partner)) {
	  	$partnerJson = json_encode($partner);
	  	print "done\n";
	  } else {
	  	print "empty data\n";
	  }
	} else {
		print "$error\n";
	}
	
	return $partnerJson;
}

main($argc, $argv);

?>