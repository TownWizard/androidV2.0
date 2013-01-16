#!/usr/bin/php

<?php

define("PARTNERS_FILE", 'build/partners/partners.json');
define("PARTNERS_INI_FILE", 'build/partners/partners.ini');
define("STRINGS_FILE", 'res/values/strings.xml');
define("SRC_DIR", 'src');
define("SRC_DIR_BKP", '_src');

$resources = array(
		'AndroidManifest.xml',
		'assets/params.txt',
		'res/values/strings.xml'
);

function loadPartners() {
	$partnerArray = json_decode(file_get_contents(PARTNERS_FILE));
	$partners = array();
	foreach($partnerArray as $p) {
		$partners[intval($p->id)] = $p;
	}
	return $partners;
}

function loadPartnersAdditionalData() {
	return parse_ini_file(PARTNERS_INI_FILE, true);
}

function backupSource() {
	print "Moving: ".SRC_DIR." -> ".SRC_DIR_BKP."\n";
	rename(SRC_DIR, SRC_DIR_BKP);
	mkdir(SRC_DIR);
}

function restoreSource() {
	if(is_dir(SRC_DIR_BKP)) {
		print "Moving: ".SRC_DIR_BKP." -> ".SRC_DIR."\n";
		system("rm -Rf " . SRC_DIR);
		rename(SRC_DIR_BKP, SRC_DIR);
	}
}

function backupResources() {
	global $resources;
	deleteBackups();
	foreach($resources as $r) {
		$fname = 'build/'.end(explode('/', $r)).'.bak';
		print "Copying: $r -> $fname\n";
		copy($r, $fname);
	}
}

function restoreResources() {
	global $resources;
	foreach($resources as $r) {
		$fname = 'build/'.end(explode('/', $r)).'.bak';
		if(file_exists($fname)) {
			print "Copying: $fname -> $r\n";
			copy($fname, $r);
		}
	}
	deleteBackups();
}

function deleteBackups() {
	global $resources;
	foreach($resources as $r) {
		$fname = 'build/'.end(explode('/', $r)).'.bak';
		if(file_exists($fname)) {
			print "Deleting: $fname\n";
			unlink($fname);
		}
	}	
}

function getPartnerId() {
	return intval(getenv('PARTNER_ID'));
}

function getPartnerData($key, $partnersAdditionalData) {
	$data = NULL;
	$partnerId = getPartnerId();
	$partnerAddData = $partnersAdditionalData[$partnerId];
	if(!empty($partnerAddData)) {
		$data = $partnerAddData[$key];
		if(!empty($data)) {
			print "Found partner $key = $data in ini file\n";
		}
	}
	return $data;
}

function getPartnerName($partners, $partnersAdditionalData) {
	$partnerName = getPartnerData('name', $partnersAdditionalData);
	if(empty($partnerName)) {
		$partnerName = $partners[$partnerId]->name;
	}
	return $partnerName;
}

?>