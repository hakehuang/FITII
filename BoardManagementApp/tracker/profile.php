<?php
 session_start();
 include'db.php';
 global $db_conn;

if($_REQUEST['action']=='change'){
	openDatabase();
	$sql = "UPDATE userinfo SET Name='".$_REQUEST['name']."', DeptCode='".$_REQUEST['dept']."', Location='".$_REQUEST['location']."', Phone='".$_REQUEST['contact']."' WHERE CoreID='".$_SESSION['username']."'";
	$result=mysqli_query($db_conn,$sql);
	closeDatabase();
	if($result){
	?><script>location.href="./index.php?p=pinfo";</script><?php
	}
}else{
	openDatabase();
	$sql="select * from userinfo where CoreID = '".$_SESSION['username']."'";
	$result=mysqli_query($db_conn,$sql);
	if( $result)
		$row=mysqli_fetch_array($result);
	closeDatabase();}
?>
<div class='page-header'>
		<h1>Your Profile</h1>
	</div>
<div class="row">
	<div class="col-lg-10">
	<form class="form-horizontal" name="LoginForm" method="post" action="profile.php?action=change" onSubmit="return InputCheck(this)">
	  <div class="form-group">
		<label for="name" class="col-sm-2 control-label">Name</label>
		<div class="col-sm-4">
			<input type="text" class="form-control" id="name" name="name" placeholder="Your name" value="<?echo $row['Name']?>">
		</div>
	  </div>  
	  <div class="form-group">
		<label for="dept" class="col-sm-2 control-label">Department Code</label>
		<div class="col-sm-4">
			<input type="text" class="form-control" id="dept" name="dept" placeholder="Department Code" value="<?echo $row['DeptCode']?>">
		</div>
	  </div>  
	  <div class="form-group">
		<label for="location" class="col-sm-2 control-label">Location</label>
		<div class="col-sm-4">
			<input type="text" class="form-control" id="location" name="location" placeholder="Location" value="<?echo $row['Location']?>">
		</div>
	  </div>  
	  <div class="form-group">
		<label for="contact" class="col-sm-2 control-label">Contact No.</label>
		<div class="col-sm-4">
			<input type="text" class="form-control" id="contact" name="contact" placeholder="Contact No." value="<?echo $row['Phone']?>">
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
