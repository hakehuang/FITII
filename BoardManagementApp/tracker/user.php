<?php
/**
 *      $Id: user.php 2014-12-25 Jingyi Gao $
 */
 
session_start();
include("header.html");
include 'db.php';
if($_REQUEST['CoreID']){
	openDatabase();
	$result = mysqli_query($db_conn, "SELECT * FROM userinfo WHERE `CoreID` = '".$_REQUEST['CoreID']."'");
	$row = mysqli_fetch_array($result);
?>
	<div class='page-header'>
		<h1><?echo $row['Name']?></h1>
	</div>
	<dl class="dl-horizontal">
		<dt>Core ID</dt>
		<dd><?echo $row['CoreID']?></dd>
	</dl>
	<dl class="dl-horizontal">
		<dt>Contact No.</dt>
		<dd><?echo $row['Phone']?></dd>
	</dl>
	<dl class="dl-horizontal">
		<dt>Department Code</dt>
		<dd><?echo $row['DeptCode']?></dd>
	</dl>
	<dl class="dl-horizontal">
		<dt>Location</dt>
		<dd><?echo $row['Location']?></dd>
	</dl>
<?
}
include("footer.html");
?>