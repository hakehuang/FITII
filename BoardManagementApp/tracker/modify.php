<?php
/**
 *      $Id: modify.php 2014-12-19 Jingyi Gao$
 */
 include'db.php';
 global $db_conn;
 
 openDatabase();
if($_GET['id']){
	$id =  intval($_GET['id']);
	$sql="select * from BoardInfo where ID = $id";
	$result=mysqli_query($db_conn,$sql);
	$row=mysqli_fetch_array($result);
}
?>
<div class='page-header'>
	<h1><?if($_GET['p']=='update_board'){echo"Edit";}else{echo"Add a new board";}?></h1>
</div>
<div class="row">
	<div class="col-lg-10">
		<form id="ModifyForm" name="ModifyForm" enctype="multipart/form-data" class="form-horizontal" method="POST" action="update.php" onSubmit="return InputCheck(this)">
			<? if($_GET['id']){
			echo "<input type='hidden' name='id' value='$_GET[id]'/>";
			?><div class="form-group">
				<label class="col-sm-2 control-label">ID</label>
				<div class="col-sm-9">
					<p class="form-control-static"><?echo $row[ID];?></p>
				</div>
			</div>
			<?}?>
		<div class="form-group">
			<label for="boardnumber" class="col-sm-2 control-label">Board Number</label>
			<div class="col-sm-9">
				<input type="text" class="form-control" id="boardnumber" name="boardnumber" placeholder="Board Number" check-type="required" value="<?php if($_REQUEST['bn']){echo $_REQUEST['bn'];}else{echo $row['Board_Number'];}?>">
			</div>
		</div>	
		<div class="form-group">
			<label for="description" class="col-sm-2 control-label">Description</label>
			<div class="col-sm-9">
				<input type="text" class="form-control" id="description" name="description" placeholder="description" check-type="required" value="<?php echo $row['description'];?>">
			</div>
		</div>	
		<div class="form-group">
			<label for="mcob" class="col-sm-2 control-label">Master Chip</label>
			<div class="col-sm-9">
				<input type="text" class="form-control" id="mcob" name="mcob" placeholder="Master Chip" check-type="required" value="<?php echo $row['master_chip_on_board'];?>">
			</div>
		</div>	
		<div class="form-group">
			<label for="boardrev" class="col-sm-2 control-label">Board Rev</label>
			<div class="col-sm-9">
				<input type="text" class="form-control" id="boardrev" name="boardrev" placeholder="Board Rev" value="<?php echo $row['Board_Rev'];?>">
			</div>
		</div>	
		<div class="form-group">
			<label for="schematicrev" class="col-sm-2 control-label">Schematic Rev</label>
			<div class="col-sm-9">
				<input type="text" class="form-control" id="schematicrev" name="schematicrev" placeholder="Board Number" value="<?php echo $row['Schematic_Rev'];?>">
			</div>
		</div>	
		<div class="form-group">
			<label for="upfile" class="col-sm-2 control-label">Image</label>
			<div class="col-sm-9">
						<?php if($row['Pic'])
			echo "<img src='".$row['Pic']."' height='300' class='img-rounded'>"?>
				<input type="file" name="upfile" id="upfile">
			</div>
		</div>	
		<div class="form-group">
			<div class="col-sm-offset-2 col-sm-10">
				<button type="submit" class="btn btn-default">Confirm</button>
			</div>
		</div>
	  </form>

<?
closeDatabase();
?>
