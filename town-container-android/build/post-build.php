#!/usr/bin/php

<?php

include 'resources.php';

define("RELEASE_FILE", 'bin/town-container-android-release.apk');

function main() {
	print "========== Start post-build.php =============\n";
	$isError = false;
	try {		
		restoreResources();
		saveReleaseFile();
	} catch (Exception $e) {
		print $e->getMessage();
		$isError = true;
	}

	print "========== Done  post-build.php =============\n";
	if($isError) return 1;
}

function saveReleaseFile() {
	$partnerId = getPartnerId();
	mkdir("deploy");
	mkdir("deploy/$partnerId");
	
	$src = RELEASE_FILE;
	$dest = "deploy/$partnerId/".end(explode('/', $src));	
	
	print "Copying: $src -> $dest\n";
	copy($src, $dest);
}

main();

?>