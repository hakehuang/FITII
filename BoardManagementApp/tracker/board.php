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

	echo "<table class='table table-hover table-striped'><thead>";
	echo "<tr>
     <TH>Description</TH>
	 <TH>Board Number</TH>
     <TH>Master Chip</TH>
     <TH>Owner</TH>
     <TH>Last Transfer</TH>
	 <TH>History</TH>
	 <TH>Updated</TH><TH></TH><TH></TH><TH></TH></tr></thead><TBody>";


	while ($row = mysqli_fetch_array($result)) {
                		echo "<TR id='board_id_".$row[ID]."' onmouseover='showDivLocal(this.id)' onmouseout='hideDivLocal(this.id)'>
        <TD>".$row[description]."</TD>
        <TD>".$row[Board_Number]."</TD>
		<TD>".$row[master_chip_on_board]."</TD>
        <TD>".$row[Owner_ID]."</TD>
        <TD>".$row[Owner_Register_Date]."</TD>
        <TD>".$row[Last_Owner]."</TD>
        <TD>".$row[Last_Update]."</TD>

		<TD><a href=index.php?p=list_board&action=delete&id=".$row[ID].">delete</a></TD>
		<TD><a href=index.php?p=update_board&id=".$row[ID].">edit</a></TD><TD>";
		if($row[Owner_ID]!= $_SESSION['username']){
			echo "<a href='index.php?p=list_board&action=take_over&id=".$row[ID]."' class='btn btn-sm btn-default'>Take Over</a>";
		}
		echo"
		<TD><TD><div class='img_popupdiv' id='board_id_".$row[ID]."_menu' style='position: absolute; margin-left: -500px; margin-top: -80px; opacity: 0.8; display: none;'>
                        <img src='".$row[Pic]."' height='300' class='img-rounded'></div>
						
                </div>
		</TR>";
  }
  echo "</tbody></TABLE>";
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