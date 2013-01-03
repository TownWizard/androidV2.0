#!/usr/bin/php

<?php

define("PARTNERS_SITE", 'http://www.townwizardcontainerapp.com/api/partner');
define("PARTNERS_FILE", 'partners/partners.json');
define("MAX_PARTNER_ID", 130);

function main() {
	$json = getPartnersJsonAsString();
	print "Writing json to partners file... ";
	file_put_contents(PARTNERS_FILE, $json);
	print "done\n";
}

function getPartnersJsonAsString() {
	$partnersJson = "[\n";
	for($i = 1; $i <= MAX_PARTNER_ID; $i++) {
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

main();

?>