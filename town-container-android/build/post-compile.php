#!/usr/bin/php

<?php

include 'resources.php';

function main() {
	print "========== Start post-compile.php =============\n";
	$isError = false;
	try {		
		replaceImages();
	} catch (Exception $e) {
		print $e->getMessage();
		$isError = true;
	}

	print "========== Done  post-compile.php =============\n";
	if($isError) return 1;
}

function replaceImages() {	
	$partnerId = getPartnerId();
	$partnerDir = "build/partners/$partnerId/";
	if(file_exists($partnerDir)) {
		$destDir = "bin/res";
		system("cp -rv $partnerDir $destDir");
	}
}

main();

?>