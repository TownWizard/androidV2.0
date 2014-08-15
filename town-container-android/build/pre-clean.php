#!/usr/bin/php

<?php

include 'resources.php';

function main() {
	print "========== Start pre-clean.php =============\n";
	$isError = false;
	try {
		restoreSource();
		restoreResources();
	} catch (Exception $e) {
		print $e->getMessage();
		$isError = true;
	}

	print "========== Done  pre-clean.php =============\n";
	if($isError) return 1;
}

main();

?>