/**
 * 
 */
var examples=[
             {"startPoint":"Beijing East Railway Station","destination":"Capital International Airport","activity":["Bank","Restaurant"],"max":40},
             {"startPoint":"Tsinghua University","destination":"Peking University","activity":["Bank","Parking lot"],"max":3.8},
             {"startPoint":"Tian'anmen","destination":"Olympic Forest Park","activity":["Hotel","Parking lot","Market","Toilet"],"max":25},
             {"startPoint":"Badaling","destination":"Beijing West Railway Station","activity":["Hotel","School","Shop","Toilet"],"max":100},
             {"startPoint":"Yuanmingyuan","destination":"Beijing Jiaotong University","activity":["Company","School","Shop"],"max":17.5},
             {"startPoint":"Beihai Park","destination":"Nanshuangqiao","activity":["Hotel","Market","Restaurant"],"max":35},
             {"startPoint":"Sun Park","destination":"Daningcun","activity":["Company","Market","Restaurant","School","Toilet"],"max":58},
             {"startPoint":"Shangqing Bridge","destination":"Houcun","activity":["Hospital","Restaurant","Toilet"],"max":62},
             {"startPoint":"Shahezhen","destination":"Beijing Nanyuan Airport","activity":["Parking lot","Market","School","Toilet","Restaurant"],"max":78},
             {"startPoint":"Tanzhesizhen","destination":"Changyingxiang","activity":["Parking lot","Company","School"],"max":88}
             ];

$(document).ready(function()
{
	for(var i in examples)
	{
		$("#example").append($("<option>").val(i).text(parseInt(i)+1)); 
	}
	
	$("#choose").click(function(){
		reset();
		frozen();
		var i=$("#example").val();
		
		var example=examples[parseInt(i)];
		$("#startPoint").val(example.startPoint);
		$("#destination").val(example.destination);
		$("#timeWithin").val(example.max);
		for(var i in example.activity)
		{
			var t=$(":checkbox[name='topic'][value='"+example.activity[i]+"']");
			t.prop("checked",true);
		}
		places.val("北京");
		search.setCity("北京");
		
		//查找起点
		search.search($("#startPoint").val(),function(statues,results){
			if(statues!="complete" || results.poiList.count==0)
			{
				messageBox.alert("Can't find the place!");
				return;
			}
			
		   	var point=results.poiList.pois[0].location;
		   	startPointMarker.setPosition(point);
		   	
		   	//查找终点
		   	search.search($("#destination").val(),function(statues,results){
				if(statues!="complete" || results.poiList.count==0)
				{
					messageBox.alert("Can't find the place!");
					return;
				}
				
			   	var point=results.poiList.pois[0].location;
			   	destinationMarker.setPosition(point);
			   	
			   	var s=startPointMarker.getPosition(),d=destinationMarker.getPosition();
		   		var data={"sLng":s.lng,"sLat":s.lat,"dLng":d.lng,"dLat":d.lat,"place":places.val()};
				ajax("getValue_!getMinDistance",data,function(data)
				{
					$("#minimumTime").val(distanceToTime(data.distance));
					suggestTime();
					$("#ensure").click();
				});
		   	});
		});
		
		//不能直接用下面两句，一是事件是异步的，二是无法处理没找到起点或终点的情况
//		$("#startPoint,#destination").change();
//		$("#ensure").click();
	});
});