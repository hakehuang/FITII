<?php
/**
 *      $Id: board.php 2014-12-19 Jingyi Gao$
 */
include 'db.php';

 function display_list(){
   global $db_conn; 
	if($_REQUEST["action"]){
		$id = intval($_GET['id']);
		if($_REQUEST["action"]=="delete")  delete($id);
		if($_REQUEST["action"]=="take_over") take_over($id);
	}
	openDatabase();
	$page=isset($_GET['page'])?intval($_GET['page']):1;    
	$num=10;//lines in every page
	if($_REQUEST["item_name"]){
		$search = $_REQUEST["item_name"];
		$result = mysqli_query($db_conn, "SELECT * FROM BoardInfo WHERE `description` LIKE '%".$search."%' OR `master_chip_on_board` LIKE '%".$search."%' OR `board_number` LIKE '%".$search."%'");
	}else if($_REQUEST["f"]){
		$result = mysqli_query($db_conn, "SELECT * FROM BoardInfo WHERE `board_number` = '".$_REQUEST["f"]."'");	
	}else{
		$result = mysqli_query($db_conn, "SELECT * FROM BoardInfo WHERE `owner_id` = '".$_SESSION['username']."' OR `last_owner` LIKE '%".$_SESSION['username']."%'");
	}
	if(!$result){
		printf("Error: %s\n", mysqli_error($db_conn));
	}
	
	$total=mysqli_num_rows($result);
	$pagenum=ceil($total/$num);    
	If($page>$pagenum || $page == 0){
       Echo "Error : Can Not Found The page .";
       Exit;
	}
	$offset=($page-1)*$num;   
	echo "<table class='table table-hover table-striped'><thead>";
	echo "<tr>
     <TH>Description</TH>
	 <TH>Board Number</TH>
     <TH>Master Chip</TH>
     <TH>Last Transfer</TH>
	 <TH>History</TH>
	 <TH>Updated</TH>
     <TH>Owner</TH><TH></TH><TH></TH><TH></TH></tr></thead><TBody>";
	if($_REQUEST["item_name"]){
		$search = $_REQUEST["item_name"];
		$info = mysqli_query($db_conn, "SELECT * FROM BoardInfo WHERE `description` LIKE '%".$search."%' OR `master_chip_on_board` LIKE '%".$search."%' OR `board_number` LIKE '%".$search."%' limit $offset,$num");
	}else if($_REQUEST["f"]){
		$info = mysqli_query($db_conn, "SELECT * FROM BoardInfo WHERE `board_number` = '".$_REQUEST["f"]."' limit $offset,$num");	
	}else{
		$info = mysqli_query($db_conn, "SELECT * FROM BoardInfo WHERE `owner_id` = '".$_SESSION['username']."' OR `last_owner` LIKE '%".$_SESSION['username']."%' limit $offset,$num");
	}

	while ($row = mysqli_fetch_array($info)) {
                		echo "<TR>
        <TD>".$row[description]."</TD>
        <TD id='board_id_".$row[ID]."' onmouseover='showDivLocal(this.id)' onmouseout='hideDivLocal(this.id)'>".$row[Board_Number]."</TD>
		<TD>".$row[master_chip_on_board]."</TD>
        <TD>".$row[Owner_Register_Date]."</TD>
        <TD>".$row[Last_Owner]."</TD>
        <TD>".$row[Last_Update]."</TD>
        <TD>";
		$user_result = mysqli_fetch_array(mysqli_query($db_conn, "SELECT * FROM userinfo WHERE `CoreID` = '".$row[Owner_ID]."'"));
		if($user_result['CoreID']){
			echo "<a href=user.php?CoreID=".$row[Owner_ID].">".$row[Owner_ID]."</a>";
		}else{ 
			echo $row[Owner_ID];
		}
		echo "<TD><a href=index.php?p=update_board&id=".$row[ID]." class='btn btn-sm btn-primary'>edit</a></TD><TD>";
		if(strtolower($row[Owner_ID])!= strtolower($_SESSION['username'])){
			echo "<a href='index.php?p=list_board&action=take_over&id=".$row[ID]."' class='btn btn-sm btn-default'>Take Over</a>";
		}
		echo"
		</TD><TD><div class='img_popupdiv' id='board_id_".$row[ID]."_menu' style='position: absolute; margin-left: -600px; margin-top: -80px; opacity: 0.8; display: none;'>
                        <img src='".$row[Pic]."' height='300' class='img-rounded'></div>
						
                </div></TD>
		</TR>";
  }
  echo "</tbody></TABLE>";
  echo "<nav>
  <ul class='pagination'>";
	$begin = $page - 2;
	$end = $page + 2;
	if($begin<1)$begin=1;
	if($end>$pagenum) $end = $pagenum;
	$url = $_SERVER["REQUEST_URI"];
	
	$p = strpos($url, "&page");
	if($p>0)
		$url = substr($url,0,$p);
	if($page!=1)
		echo "<li><a href='".$url."&page=".($page - 1)."'>&laquo;</a></li>";
	for($i=$begin;$i<=$end;$i++){
		if($i == $page){
			echo" <li class='active'><a href='".$url."&page=".$i."'>";
			echo $i;
			echo "<span class='sr-only'>(current)</span></a></li>";
		}else{
			echo" <li><a href='".$url."&page=".$i."'>".$i."</a></li>";
		}
	}
	if($page!=$pagenum)
		echo "<li><a href='".$url."&page=".($page + 1)."'>&raquo;</a></li>";
  echo "</ul>
</nav>";
  closeDatabase();
 }
 function delete($id) {
    global $db_conn; 
	openDatabase();
	$sql = "DELETE FROM BoardInfo WHERE ID=$id";
	$result=mysqli_query($db_conn,$sql);
	if($result){
    ?>
		<div class="alert alert-success">
		<strong>Deleted!</strong> 
		</div>
		<script>location.href="./index.php?p=list_board";</script>
	<?
	}else{
    ?>
		<div class="alert alert-danger">
		<strong>Failed!</strong> 
		</div>
		<script>location.href="./index.php?p=list_board";</script>
	<?
	}
	  closeDatabase();

}
function take_over($id){
    global $db_conn; 
	openDatabase();
	$sql="select * from BoardInfo where ID = $id";
	$result=mysqli_query($db_conn,$sql);
	$row=mysqli_fetch_array($result);
	$Owners = $row[Last_Owner];
	$position = strpos($Owners, $row[Owner_ID]);
		if ($position != 0){
			$Owners = str_replace(";".$row[Owner_ID], "",$Owners);
		}
		$Owners = $row[Owner_ID].";".$Owners;
		$time = date("Y-m-d H:i:s", time());
	$sql = "Update BoardInfo SET `Owner_ID` ='".$_SESSION['username']."', `Last_Owner` = '".$Owners."', `Owner_Register_Date` = '".$time."' WHERE ( `ID` ='".$id."')";
	$result=mysqli_query($db_conn,$sql);
	closeDatabase();
	if($result){
    ?>
		<div class="alert alert-success alert-dismissible">
		<button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
		<strong>Succeed!</strong> 
		</div>
		<script>location.href="./index.php?p=list_board";</script>
	<?
	}else{
    ?>
		<div class="alert alert-danger alert-dismissible">
		<button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
		<strong>Failed!</strong> 
		</div>
		<script>location.href="./index.php?p=list_board";</script>		
	<?
	}
}
 ?>