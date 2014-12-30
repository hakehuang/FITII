<?php
/**
 *      $Id: index.php 2014-12-19 Jingyi Gao $
 */
	session_start();
	$appVersion = "2.1";
	if(!$_SESSION['username'] && $_REQUEST["p"]&&$_REQUEST["p"] !='register'&& $_REQUEST["p"] !='login' && $_REQUEST["p"] !='about'&& $_REQUEST["p"] !='shopping'){
		header("Location: index.php?p=login");exit;
	}
include("header.html");
// BEGIN Main page switch (pf = 'page function')
$pf = $_REQUEST["p"]; 
if($pf == 'login'){
	include 'login.php';
}elseif($pf=='register'){
	include 'register.php';
}elseif($pf=='list_board'||$_REQUEST['f']){
	include 'board.php';
	?>
			<div class='page-header'>
		<h1>Board List</h1>
		</div>
		<div class="row">
		<div class="col-lg-10">
			<p>Tags: <?tags()?></p>
			<div class="well">
			<p>You can use board number, name of master chip and description of board as search words.</p>
			</div>
			<form name="SearchForm" method="GET" action="index.php" onSubmit="return InputCheck(this)" class="form-inline" >
            <div class="form-group">
                <label for="item_name" class="col-sm-4 control-label">Search for</label>
				<div class="col-sm-8">
					<input type="hidden" name="p" value="list_board"/>
					<?if($_REQUEST['tag']){?>
					<input type="hidden" name="tag" value="<?echo $_REQUEST['tag'];?>"/>
					<?}?>
					<input type="text" class="form-control" name = "item_name"id="item_name" tabindex="1" >
					
				</div>
			</div>
			<div class="form-group">
				<div class="col-sm-offset-2 col-sm-10">
				<input type="submit" value="Search" class="btn btn btn-default"  tabindex="2"/>
				</div>
			</div>
			<div class="form-group">
				<div class="col-sm-offset-2 col-sm-10">
					<a href = "index.php?p=list_board" class='btn btn-default'>Display All</a></td>
				</div>
			</div>
			</form>
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
			<p>Test Version 2014-12-30</p>
			<p>Author: B53505 Jingyi Gao</p>
			<p><a href="https://prezi.com/insvxswnsdsk/online-tracking-system/?utm_campaign=share&utm_medium=copy">View Tutorial</p>
        </div>
       </div>
       </div>

<?
} else if($pf==''){
?>
			
			    <div  id="top" class="callbacks_container">  
			          <div class="caption text-center">
			          	<div class="slide-text-info">
			          		<h1>For Android <span>Avaliable Now</span></h1>
			          		<h2>Made to modify and use anywhere</h2>
			          		<div class="slide-text">
			          			<ul>
			          				<li><span> </span>Scan QR/Barcode </li>
			          				<li><span> </span>Modify/Add boards </li>
			          				<li><span> </span>Make phone call to owner</li>
			          			</ul>
			          		</div>
			          		<div class="clearfix"> </div>
			          		<div class="big-btns">
			          			<a class="download" href="download/the_tracker_installation.apk"><label> </label>Download</a>
			          			<a class="view" href="https://prezi.com/xb2hpaei69jp/copy-of-user-guide/"><label> </label>Vew Guide</a>
			          		</div>
			          	</div>
			          </div>
			    </div>
			    <div class="clearfix"> </div>
			    <!-----divice----->
			    	<div class="divice-demo">
			    		<img src="image/divice-in-hand.png" title="demo" />
			    	</div>
			    <!---//divice----->
			<!----- //End-slider---->
			
<?
}


include("footer.html");
// END Main page switch
?>
