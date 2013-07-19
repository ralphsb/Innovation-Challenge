

$(document).ready(function() {
$("#addNote").click(function() {
	addNote('val'); 
	return false; 
});
$("#addFolder").click(function() {
	addFolder('val'); 
	return false; 
});

$("#folderClick").click(function() {
	newFolder(); 
	addFolder('val'); 
	addNote('val'); 
	return false; 
});

$("#newFolderSave").click(function() {
if ($('#newFolderName').val() !=""){	
addFolder($('#newFolderName').val()); }
$('#newFileModal').modal('hide');
$("#newFolderName").val("");
return false; 
});



});



function addNote(val) {
	$("#notes").append('<div class="span2"><div class="filename-col" ><img class="file" alt="Photos" draggable="true" src="/innovationChallenge/img/note.png"/></div>'+
	'<a href="/home/exam3"draggable="true" hidefocus="hideFocus" target="_self">'+val+'</a></div>');
}; 

function addFolder(val) {
	$("#folders").append('<div class="span2"><div class="filename-col" ><img class="file" alt="Photos" draggable="true" src="/innovationChallenge/img/folder.png"/></div>'+
	'<a href="/home/exam3"draggable="true" hidefocus="hideFocus" target="_self">'+val+'</a></div>'); 

}; 

function newFolder() {
	$("#folders").html('');
	$("#notes").html(''); 
}; 