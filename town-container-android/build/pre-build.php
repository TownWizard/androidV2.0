#!/usr/bin/php

<?php

include 'resources.php';

function main() {
	print "========== Start pre-build.php =============\n";
	$isError = false;
	try {
		$partners = loadPartners();
		backupResources();
		replaceParams();
		replaceStrings($partners);
	} catch (Exception $e) {
		print $e->getMessage();
		$isError = true;
	}
	
	print "========== Done  pre-build.php =============\n";
	if($isError) return 1;
}

function replaceParams() {	
	$file = 'assets/params.txt';
	if(!file_exists($file)) throw new Exception('params.txt file not present');
	
	$partnerId = getPartnerId();
	$str = "ID=$partnerId";
	
	print "Writing $str to params.txt\n";
	file_put_contents($file, $str);
	$strFromFile = file_get_contents($file);
	if($str != $strFromFile) throw new Exception('Problem writing to params.txt');
}

function replaceStrings($partners) {
	$doc = DOMDocument::load(STRINGS_FILE);
	$strings = $doc->getElementsByTagName('string');
	$partnerId = getPartnerId();
	foreach($strings as $s) {
		if($s->getAttribute('name') == 'app_name') {
			$appName = $partners[$partnerId]->name;
			print "Replacing app_name with $appName\n";
			$s->nodeValue = $appName;
		}
	}
	$doc->save(STRINGS_FILE);
	$newXml = file_get_contents(STRINGS_FILE);
	print "Modified strings.xml is\n $newXml";
}

main();

?>