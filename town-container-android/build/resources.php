#!/usr/bin/php

<?php

define("PARTNERS_FILE", 'build/partners/partners.json');
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

?>