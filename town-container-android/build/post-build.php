#!/usr/bin/php

<?php

include 'resources.php';

function main() {
	print "========== Start post-build.php =============\n";
	$isError = false;
	try {		
		restoreResources();
	} catch (Exception $e) {
		print $e->getMessage();
		$isError = true;
	}

	print "========== Done  post-build.php =============\n";
	if($isError) return 1;
}

main();

?>