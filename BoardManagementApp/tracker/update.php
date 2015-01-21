<?php
/**
 *      $Id: modify.php 2014-12-19 Jingyi Gao$
 */
error_reporting(0);
session_start();
include("header.html");
include 'db.php';
$uploadimg = false;
echo $_REQUEST['upfile'];
$uptypes=array(
    'image/jpg',
    'image/jpeg',
    'image/png',
    'image/pjpeg',
    'image/gif',
    'image/bmp',
    'image/x-png'
);
$max_file_size=2000000;     //上传文件大小限制, 单位BYTE
$destination_folder="/usr/local/tomcat/webapps/Image/"; //上传文件路径
if ($_SERVER['REQUEST_METHOD'] == 'POST')
{
    if (is_uploaded_file($_FILES["upfile"][tmp_name]))
    {
	$uploadimg = true;
    $file = $_FILES["upfile"];
    if(2000000 < $file["size"])
    //检查文件大小
    {
        echo "The file is too large!";
        exit;
    }

    if(!in_array($file["type"], $uptypes))
    //检查文件类型
    {
        echo "文件类型不符!".$file["type"];
        exit;
    }

    if(!file_exists($destination_folder))
    {
        mkdir($destination_folder);
    }

    $filename=$file["tmp_name"];
    $image_size = getimagesize($filename);
    $pinfo=pathinfo($file["name"]);
    $ftype=$pinfo['extension'];
    $destination = $destination_folder.time().".".$ftype;
    if (file_exists($destination) && $overwrite != true)
    {
        echo "同名文件已经存在了";
        exit;
    }

    if(!move_uploaded_file ($filename, $destination))
    {
        echo "移动文件出错";
        exit;
    }

    $pinfo=pathinfo($destination);
    $fname=$pinfo[basename];
	$path = "http://10.192.244.114:8080/Image/".$fname;

  }
}    

if($_REQUEST["id"]) modify($_REQUEST["id"]);
else add();
function modify($id){
	global $db_conn,$uploadimg,$path;
	openDatabase();
	$time = date("Y-m-d H:i:s", time());
    $sql = "UPDATE BoardInfo SET `description` = '"
					.$_REQUEST["description"].
					"',  `Master_chip_on_board` = '"
					.$_REQUEST["mcob"].
					"',  `Board_Rev` = '".$_REQUEST["boardrev"].
					"',  `Schematic_Rev` = '"
					.$_REQUEST["schematicrev"]."'";
	if($_REQUEST["boardnumber"]){
		$sql = $sql.", `Board_Number` = '".$_REQUEST["boardnumber"]."'";
	}
	if($uploadimg){
		$sql= $sql.", `Pic` = '".$path."'";
	}
	$sql= $sql.", `Last_Update` = '".$time."', `tag` = '".$_REQUEST["tag"]."'WHERE ( `ID` ='"
					.$id."')";		
    $result = mysqli_query($db_conn, $sql);
    closeDatabase();
    if($result){
	?>
		<div class="alert alert-success">
			<strong>Succeed!</strong> 
		</div>
		<script>
			location.href="./index.php?p=list_board";
		</script>
	<?
    }
    else{
    ?>
		<div class="alert alert-danger">
		<strong>Failed!</strong> 
		</div>
		<script>location.href="./index.php?p=update_board&id=<?echo $id;?>";</script>
	<?
    }
}
function add(){
	global $db_conn,$uploadimg,$path;
	openDatabase();
	$boardnumber = $_REQUEST['boardnumber'];
	$sql="select * from BoardInfo where Board_Number = '$boardnumber'";
	$result=mysqli_query($db_conn,$sql);
	$row=mysqli_fetch_array($result);
	if($row['ID']){
		?><script>location.href="../index.php?p=update_board&id=<?echo $row['ID'];?>";</script><?
	}else{
		$time = date("Y-m-d H:i:s", time());
		if($uploadimg){
			$sql = "INSERT INTO BoardInfo (`description`, `Master_chip_on_board`, `Board_Rev`, `Schematic_Rev`, `Pic`, `Owner_ID`, `Owner_Register_Date`, `Board_Number`,`Last_Update`,`tag`) VALUES ('"
					.$_REQUEST["description"]."','"
					.$_REQUEST["mcob"]."','"
					.$_REQUEST["boardrev"]."','"
					.$_REQUEST["schematicrev"]."','".$path."','".$_SESSION['username']."','".$time."','".$boardnumber."','".$time."','".$_REQUEST["tag"]."')";

		}else{
					$sql = "INSERT INTO BoardInfo (`description`, `Master_chip_on_board`, `Board_Rev`, `Schematic_Rev`, `Pic`, `Owner_ID`, `Owner_Register_Date`, `Board_Number`,`Last_Update`,`tag`) VALUES ('"
					.$_REQUEST["description"]."','"
					.$_REQUEST["mcob"]."','"
					.$_REQUEST["boardrev"]."','"
					.$_REQUEST["schematicrev"]."',' ','".$_SESSION['username']."','".$time."','".$boardnumber."','".$time."','".$_REQUEST["tag"]."')";
		}
		echo $sql;
		$result = mysqli_query($db_conn, $sql);
		closeDatabase();
		if($result){
		?>
			<div class="alert alert-success">
				<strong>Succeed!</strong> 
			</div>
			<script>
				location.href="./index.php?p=list_board";
			</script>
		<?
		}else{
		?>
			<div class="alert alert-danger">
				<strong>Failed!</strong> 
			</div>
			<script>location.href="./index.php?p=update_board&id=<?echo $id;?>";</script>
		<?
		}
	}
}



?>