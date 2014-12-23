<?php
/**
 *      $Id: index.php 2014-12-19 Jingyi Gao $
 */
	session_start();
	$appVersion = "1.0";
	if(!$_SESSION['username'] && $_REQUEST["p"] !='register'&& $_REQUEST["p"] !='login' && $_REQUEST["p"] !='about'&& $_REQUEST["p"] !='shopping'){
		header("Location: index.php?p=login");exit;
	}
include("header.html");
// BEGIN Main page switch (pf = 'page function')
$pf = $_REQUEST["p"]; 
if($pf == 'login'){
	include 'login.php';
}elseif($pf=='register'){
	include 'register.php';
}elseif($pf=='list_board'||$pf==''){
	include 'board.php';
	?>
			<div class='page-header'>
		<h1>Board List</h1>
		</div>
		<div class="row">
		<div class="col-lg-10">
			<div class="well">
			<p>You can use board number, name of master chip and description of board as search words.</p>
			</div>
			<form name="SearchForm" method="GET" action="index.php" onSubmit="return InputCheck(this)" class="form-inline" style="float:left;margin-right:10px">
            <div class="form-group">
                <label for="item_name" class="col-sm-4 control-label">Search for</label>
				<div class="col-sm-8">
					<input type="hidden" name="p" value="list_board"/>
					<input type="text" class="form-control" name = "item_name"id="item_name" tabindex="1" >
				</div>
			</div>
			<input type="submit" value="Search" class="btn btn btn-default"  tabindex="2"/>
			</form>
			<form name="Displayall" method="GET" action="index.php" onSubmit="return InputCheck(this)">
			<input type="hidden" name="p" value="list_board"/>
			<input type="submit" value="Display All" class="btn btn btn-default"  />
			<?	if($_REQUEST['f']){
			echo "<a href='index.php?p=add_board&bn=".$_REQUEST['f']."'>Not find result? Add a board</a>";}?>
			</form>

			<?display_list()?>
		</div>

	</div>
	<?	
}else if($pf == 'update_board'||$pf == 'add_board'){
	include 'modify.php';
}else if($pf == 'pinfo'){
	include 'profile.php';
}else if($pf == 'password')	{
?>	<div class='page-header'>
		<h1>Change Password</h1>
	</div>
    <div class="well">
       <p>To update your password enter a new one below. To make it stronger, use upper and lower case letters, numbers and symbols like ! " ? $ % ^ & ).</p>
    </div>
	<form class="form-horizontal" name="LoginForm" method="post" action="login.php?action=password" onSubmit="return InputCheck(this)">
		<div class="form-group">
			<label for="password" class="col-sm-2 control-label">New Password</label>
			<div class="col-sm-4">
				<input type="password" class="form-control" id="password" name="password" placeholder="Enter New Password">
			</div>
		</div>	
		<div class="form-group">
			<label for="repeat" class="col-sm-2 control-label">Re-enter Password</label>
			<div class="col-sm-4">
				<input type="password" class="form-control" id="repeat" name="repeat" placeholder="Re-enter New Password">
			</div>
		</div>	
	    <div class="form-group">
			<div class="col-sm-offset-2 col-sm-10">
				<input type="submit" id="submit" name="submit" value="Confirm" class="btn btn btn-default"/>
			</div>
		</div>
	  </form>
<?
} else if ($pf == 'about') {
?>
      <div class="theme-showcase">
      <div class="my-jumbo">
        <div class='page-header'>
			<h1>About</h1>
		</div>
        
        <div class="well">
			<p>Test Version 2014-12-22</p>
        </div>
       </div>
       </div>

<?
} 
include("footer.html");
// END Main page switch
?>
