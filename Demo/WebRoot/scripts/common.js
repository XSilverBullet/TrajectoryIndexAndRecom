var messageBox;

$(document).ready(function()
{
	messageBox=$("#messageBox");
	contentForMessageBox=$("#contentForMessageBox");
	ensureButtonForMessageBox=$("#ensureButtonForMessageBox");
	cancelButtonForMessageBox=$("#cancelButtonForMessageBox");
	
	messageBox.modal(
	{
		backdrop:"static",
		show: false
	});
	
	messageBox.alert=function(content)
	{
		contentForMessageBox.html(content);
		
		ensureButtonForMessageBox.unbind("click");
		ensureButtonForMessageBox.css("display","inline-block");
		cancelButtonForMessageBox.css("display","none");
		
		ensureButtonForMessageBox.click(function(){
			messageBox.modal("hide");
		});
		messageBox.modal("show");
	}
	
	messageBox.confirm=function(content,ensure,cancel)
	{
		contentForMessageBox.html(content);
		
		ensureButtonForMessageBox.unbind("click");
		cancelButtonForMessageBox.unbind("click");
		ensureButtonForMessageBox.css("display","inline-block");
		cancelButtonForMessageBox.css("display","inline-block");
		
		ensureButtonForMessageBox.click(function(){
			messageBox.result=true;
			if(ensure!=null)
				ensure();
			messageBox.modal("hide");
		});
		
		cancelButtonForMessageBox.click(function(){
			messageBox.result=false;
			if(cancel!=null)
				cancel();
			messageBox.modal("hide");
		});
		messageBox.modal("show");
	}
});

function closeMessageBox()
{
	messageBox.modal("hide");
}

function ajax(url,data,success,error,async)
{
	if(async==null)
		async=true;
	$.ajax({
		type : "post",
		url : url,
		async : async,
		data : data,
		cache : false,
		dataType : "json",
		success : function(data) 
		{
			if(data.error==null)
			{
				success(data);
			}
			else
			{
				messageBox.alert(data.error);
			}
		},
		error : function(data) 
		{
			messageBox.alert(data);
			if(error!=null)
				error(data);
		}
	});
}