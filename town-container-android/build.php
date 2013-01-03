#!/usr/bin/php

<?php

define("PARTNERS_FILE", 'build/partners/partners.json');

function main($argc, $argv) {
	$buildAll = false;
	$partnerId = 0;
	$install = false;
	
	if($argc > 1) $partnerId = intval($argv[1]);
	if($partnerId != 0) {
		if($argc > 2) $install = ($argv[2] == '-install');
	} else {
		if($argc > 1) $buildAll = ($argv[1] == '-all');
	}
	
	if(!$buildAll && $partnerId == 0) {
		print "Usage:\n\n./build.php <partner_id> [-install]\n\n./build.php -all\n\n";;
		return;
	}
	
	if($buildAll) print "Building all partners ...\n";
	
	$partners = loadPartners();
	
	if($partnerId != 0) {
		if(!array_key_exists($partnerId, $partners)) {
			printf("No partner id %d found in %s\n", $partnerId, PARTNERS_FILE);
			return;
		}
		$partner = $partners[$partnerId];
		$partners = array();
		$partners[$partnerId] = $partner;
	}
	
	if($install && $partnerId != 0) {
		$command = 'ant clean release install';
	} else if($buildAll) {
		$command = 'ant -q clean release';
	} else {
		$command = 'ant clean release';
	}
	
	foreach($partners as $partner) {
		print "Building partner $partner->id: '$partner->name' ...\n";
		putenv("PARTNER_ID=$partner->id");
		system($command);
	}
}

function loadPartners() {
	$partnerArray = json_decode(file_get_contents(PARTNERS_FILE));
	$partners = array();
	foreach($partnerArray as $p) {		
		$partners[intval($p->id)] = $p;
	}
	return $partners;
}

main($argc, $argv);

?>