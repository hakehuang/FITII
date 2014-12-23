<?php
/**
 *      $Id: login.php 2014-12-19 Jingyi Gao$
 */
session_start();
include 'db.php';

if($_GET['action'] == "logout"){
    unset($_SESSION['userid']);
    unset($_SESSION['username']);
	
	?>
	<div class="alert alert-info">
        <strong>Sign out...</strong> 
     </div>
	<script>
	location.href="./index.php?p=login";
	</script>
	<?
    exit;
}else{
	if(!isset($_POST['submit'])){
		//exit('Access Denied!');
	}
	//conection database
	openDatabase();
	if($_GET['action'] == "login"){
		$username = htmlspecialchars($_POST['username']);
		$password = substr(MD5($_POST['password']),8,16);
		//$password = $_POST['password'];
		//check password
		$check_query = mysqli_query($db_conn,"SELECT * FROM userinfo where CoreID='$username' and password='$password' 
		limit 1");
		echo $check_query=="";
		if($result = mysqli_fetch_array($check_query)){
			//login succeed
			$_SESSION['username'] = $username;
			closeDatabase();
		?>
			<div class="alert alert-success">
				<strong>Logging, please be patient</strong> 
			</div>
			<script>location.href="./index.php";</script><?
		} else {
			exit('<h3>Login Failed <a href="javascript:history.back(-1);">Return</a><h3>');
		}
	}else if($_GET['action'] == "password"){
		if($_POST['password']==$_POST['repeat']){
			//$password = MD5($_POST['password']);
			$password = substr(MD5($_POST['password']),8,16);
			$coreid = $_SESSION['username'];
			$check_query = mysqli_query($db_conn,"UPDATE userinfo SET Password='$password' where CoreID='$coreid'"); 
		}
		closeDatabase();
		?><script>location.href="./index.php";</script><?
	}

}
if(!$_SESSION['username']){
?>
<div class="login">
<table width="600" border="0" align="center" cellpadding="0" cellspacing="0">
  <tr>
    <td width="148" align="center"><table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="297" height="74" align="right"><img src="/image/logo.png" /></td>
      </tr>
      <tr>
        <td height="33" align="center" valign="top"><span id="tip"></span></td>
      </tr>
    </table></td>
    <td width="18" align="center"><img src="/image/bg_login.gif" /></td>
    <td colspan="2" align="center" class="title"><table width="100%" border="0" cellspacing="0" cellpadding="0">
	<form class="form-horizontal" name="LoginForm" method="post" action="login.php?action=login" onSubmit="return InputCheck(this)">
		<div class="form-group">
			<label for="username" class="col-sm-2 control-label">CoreID</label>
			<div class="col-sm-10">
				<input type="text" class="form-control" name = "username" id="username" placeholder="Enter CoreID">
			</div>
		</div>
		<div class="form-group">
			<label for="password" class="col-sm-2 control-label">Password</label>
			<div class="col-sm-10">
				<input type="password" class="form-control" name = "password" id="password" placeholder="Enter password">
			</div>
		</div>
      <tr>
        <td height="33" align="right">&nbsp;</td>
        <td align="left"><input type="submit" id="submit" name="submit" value="Login" class="btn btn-default"  tabindex="4"/>
		<a href = "index.php?p=register" class='btn btn-default'>Sign Up</a></td>
		
	  </tr>
	  </form>
    </table></td>
  </tr>
</table>
</div>
<?}
?>