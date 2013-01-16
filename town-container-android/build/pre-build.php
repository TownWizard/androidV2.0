#!/usr/bin/php

<?php

include 'resources.php';

define("ORIGINAL_PKG_NAME", 'com.townwizard.android');

function main() {
	print "========== Start pre-build.php =============\n";
	$isError = false;
	try {
		$partners = loadPartners();
		$partnersAdditionalData = loadPartnersAdditionalData();		
		$partnerName = getPartnerName($partners, $partnersAdditionalData);
		$pkgName = getPartnerPackageName($partners, $partnersAdditionalData);
		
		backupSource();
		backupResources();
		replaceParams();
		replaceStrings($partnerName);
		replaceManifestPackageNames($pkgName);
		replaceSource($pkgName);
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

function replaceStrings($partnerName) {
	$doc = DOMDocument::load(STRINGS_FILE);
	$strings = $doc->getElementsByTagName('string');
	foreach($strings as $s) {
		if($s->getAttribute('name') == 'app_name') {			
			print "Replacing app_name with $partnerName\n";
			$s->nodeValue = $partnerName;
		}
	}
	$doc->save(STRINGS_FILE);
	$newXml = file_get_contents(STRINGS_FILE);
	print "Modified strings.xml is\n $newXml";
}

function replaceManifestPackageNames($pkgName) {
	replacePackageNames("AndroidManifest.xml", "AndroidManifest.xml", $pkgName);	
}

function replaceSource($pkgName) {
	$it = new RecursiveDirectoryIterator(SRC_DIR_BKP);
	foreach(new RecursiveIteratorIterator($it) as $file) {
		if($file->getFilename() != '.' && $file->getFilename() != '..') {
			copyWithPackagesReplaced($pkgName, $file);
		}
	}
}

function copyWithPackagesReplaced($newPkgName, $file) {	
	$oldPathBase = SRC_DIR_BKP.'/'.str_replace('.', '/', ORIGINAL_PKG_NAME);	 
	$newPathBase = SRC_DIR.'/'.str_replace('.', '/', $newPkgName);
	$newPath = str_replace($oldPathBase, $newPathBase, $file->getPath());

	$dir = '';
	foreach(explode('/', $newPath) as $nextDir) {
		if(empty($dir)) {
			$dir = $nextDir;
			//no need to create: src directory is already there
		} else {
			$dir = $dir.'/'.$nextDir;
			if(!is_dir($dir)) {
				print "Creating directory $dir\n";
				mkdir($dir);
			}
		}
	}	
	
	$newFile = $newPath.'/'.$file->getFilename();	
	replacePackageNames($file->getPathname(), $newFile, $newPkgName);
}

function replacePackageNames($oldFile, $newFile, $newPkgName) {
	$origContent = file_get_contents($oldFile);
	$modifiedContent = str_replace(ORIGINAL_PKG_NAME, $newPkgName, $origContent);
	file_put_contents($newFile, $modifiedContent);	
}

function getPartnerPackageName($partners, $partnersAdditionalData) {	
	$pkgName = getPartnerData('package', $partnersAdditionalData);
	if(!empty($pkgName)) return $pkgName;
	
	$partnerId = getPartnerId();
	$url = $partners[$partnerId]->website_url;
	$original_url = $url;
	if(empty($url)) {
		throw new Exception("Cannot figure out partner package name. There is no package-name.txt available and now web site url known.");
	}
	
	//remove http://
	if(startsWith($url, 'http://')) $url = str_replace('http://', '', $url);
	else if(startsWith($url, 'https://')) $url = str_replace('https://', '', $url);
	
	//remove www.
	if(startsWith($url, 'www.')) $url = str_replace('www.', '', $url);
	
	//remove dashes
	$url = str_replace('-', '', $url);
	
	//remove trailing slash
	if($url[strlen($url)-1] == '/') $url = substr_replace($url, "", -1);	
	
	//inverse parts of package name
	$pkgName = implode('.', array_reverse(explode('.', $url)));
	print "Generated package name $pkgName for partner $partnerId from url $original_url\n";
	return $pkgName;
}

function startsWith($haystack, $needle) {
	return !strncmp($haystack, $needle, strlen($needle));
}

main();

?>