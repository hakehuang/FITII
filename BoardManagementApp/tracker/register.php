<?php
/**
 *      $Id: register.php 2014-12-19 Jingyi Gao$
 */
 session_start();
include 'db.php';
if($_GET['action'] == "register"){
	if(!isset($_POST['submit'])){
		//exit('Access Denied!');
	}
	//conection database
	openDatabase();
		$username = htmlspecialchars($_POST['username']);
		$password = substr(MD5($_POST['password']),8,16);
		$name = htmlspecialchars($_POST['name']);
		$dept = htmlspecialchars($_POST['dept']);
		$location = htmlspecialchars($_POST['location']);
		$contact = htmlspecialchars($_POST['contact']);
		if($username&&$password){
			$check_query = mysqli_query($db_conn,"INSERT INTO userinfo (CoreID,Name,DeptCode,Location,Password,Phone) VALUES ('$username','$name','$dept','$location','$password','$contact')"); 
			$_SESSION['username'] = $username;
		}
	closeDatabase();
		?><script>location.href="./index.php";</script><?php

}
if(!$_SESSION['username']){
?>
	<div class='page-header'>
		<h1>Sign Up</h1>
	</div>
<div class="row">
	<div class="col-lg-10">
    <div class="well">
       <p>To make your password stronger, use upper and lower case letters, numbers and symbols like ! " ? $ % ^ & ).</p>
    </div>
	<form class="form-horizontal" id="RegForm" name="RegForm" method="post" action="register.php?action=register" onSubmit="return InputCheck(this)">
	  <div class="form-group">
		<label for="username" class="col-sm-2 control-label">Core ID</label>
		<div class="col-sm-4">
			<input type="text" class="form-control" id="username" name="username" placeholder="Core ID" check-type="required">
		</div>
	  </div>  
	  <div class="form-group">
		<label for="password" class="col-sm-2 control-label">Password</label>
		<div class="col-sm-4">
			<input type="password" class="form-control" id="password" name="password" placeholder="Password" check-type="required">
		</div>
	  </div>  
	  <div class="form-group">
		<label for="name" class="col-sm-2 control-label">Name</label>
		<div class="col-sm-4">
			<input type="text" class="form-control" id="name" name="name" placeholder="Your name" check-type="required">
		</div>
	  </div>  
	  <div class="form-group">
		<label for="dept" class="col-sm-2 control-label">Department Code</label>
		<div class="col-sm-4">
			<input type="text" class="form-control" id="dept" name="dept" placeholder="Department Code" check-type="required">
		</div>
	  </div>  
	  <div class="form-group">
		<label for="location" class="col-sm-2 control-label">Location</label>
		<div class="col-sm-4">
			<input type="text" class="form-control" id="location" name="location" placeholder="Location" check-type="required">
		</div>
	  </div>  
	  <div class="form-group">
		<label for="contact" class="col-sm-2 control-label">Contact No.</label>
		<div class="col-sm-4">
			<input type="text" class="form-control" id="contact" name="contact" placeholder="Contact No." check-type="required">
		</div>
	  </div>  
	    <div class="form-group">
    <div class="col-sm-offset-2 col-sm-10">
       <input type="submit" id="submit" name="submit" value="Confirm" class="btn btn btn-default"  tabindex="7"/>
	    </div>
  </div>
	  </form>
	</div>
</div>
<?}
?>