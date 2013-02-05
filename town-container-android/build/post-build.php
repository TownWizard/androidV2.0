#!/usr/bin/php

<?php

include 'resources.php';

define("RELEASE_FILE", 'bin/town-container-android-release.apk');

function main() {
	print "========== Start post-build.php =============\n";
	$isError = false;
	try {
		$partners = loadPartners();
		$partnersAdditionalData = loadPartnersAdditionalData();
		$partnerName = getPartnerName($partners, $partnersAdditionalData);
		
		restoreSource();
		restoreResources();
		saveReleaseFile($partnerName);
	} catch (Exception $e) {
		print $e->getMessage();
		$isError = true;
	}

	print "========== Done  post-build.php =============\n";
	if($isError) return 1;
}

function saveReleaseFile($partnerName) {
	$partnerId = getPartnerId();
	if($partnerId == 0) {
		$apkFileName = 'TownWizard.apk';
	} else {
		$partners = loadPartners();
		$apkFileName = str_replace(' ', '', $partnerName) . '.apk';
	}

	mkdir("deploy");	
	
	$src = RELEASE_FILE;
	//$dest = "deploy/$partnerId/".end(explode('/', $src));
	$dest = "deploy/$apkFileName";	
	
	print "Copying: $src -> $dest\n";
	copy($src, $dest);
}

main();

?>