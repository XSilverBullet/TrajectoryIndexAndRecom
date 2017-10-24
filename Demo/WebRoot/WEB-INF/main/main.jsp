<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<title>ActTRS</title>
<body>
	<style type="text/css">
		#blockForPlaces
		{
			height:40px;
			width:20%;
			min-width:230px;
			vertical-align: top;
		}
		
		#messageShow
		{
			width:20%;
			font-family:华文新魏,微软雅黑,Cursive;
		}
		
		#action
		{
			text-align:center;
		}
		
		#ensure,#reset
		{
			margin-right:10px;
			margin-left:10px;
		}
		
		.secondLevel
		{
			padding-left:30px;
		}
		
		th
		{
			background:#265a88;
			color:white;
		}
		
		table#mainTable,th,td
		{
			border: 1px solid #9BBCC8;
			padding:0;
		}
		
		#messageShow th,#messageShow td
		{
			border:0 !important;
			border-top: 0 !important;
			padding:2px !important;
			font-size:15px;
		}
		
		#detailMessage
		{
			overflow-y:auto;
			border-top: 5px solid #9BBCC8;
		}
		
		#messageOverview
		{
			overflow-y:auto;
		}
		
		#summary
		{
			width:100%;
		}
		#detailMessage
		{
			overflow-y:auto;
		}
		
		.serialNo
		{
			width:35px;
			font-weight:bold;
		}
		.title
		{
			float:left;
			width:88px;
			font-weight:bold;
			font-family: sans-serif;
		}
		
		.value
		{
			min-height:25px;
			font-style: italic;
		}
		
		hr
		{
			border-top:1px solid #9bbcc8;
			margin:0;
			width:99%;
		}
		/*** copied from demo #39 添加自定义点覆盖物 ***/
		/* 定义自定义点样式 */
		.marketSpanStyle
		{
			white-space:nowrap;
		}
		.markerContentStyle
		{
			position:relative;
		}
		.markerContentStyle span
		{
			padding:5px;
			background-color: #FFFFFF;
			color:#000000;
			heigth:80px;
			border:2px solid #9BBCC8;
			position:absolute;
			top:-10px;left:25px;
			white-space:nowrap
			-webkit-border-radius:5px;
			border-radius:5px;
			font-weight:bold;
		}
	</style>
	
	<div class="table-responsive">
		<table id="mainTable" class="table">
			<tr>
				<th>Input</th>
				<th style="min-width:600px;">Map</th>
				<th>Detail Information</th>
			</tr>
			<tr>
				<td id="blockForPlaces">
					<label for="name">City:</label>
					<div class="secondLevel">
						<s:select id="places" theme="simple" name="places" listkey="key" listvalue="value" list="#request.places" class="form-control input-sm"/>
					</div>
				</td>
				<td rowspan="6" id="mapContainer">
				</td>
				<td id="messageShow" rowspan="5">
					<div id="summary">
						<table id="summaryTable" class="table">
							<tr style="display:none;">
								<td style="width:150px;"><label>Segment Number:</label></td>
								<td id="segmentNumber" class="value"></td>
							</tr>
							<tr>
								<td><label>Total Time:</label></td>
								<td id="totalTime" class="value"></td>
							</tr>
							<tr>
								<td><label>Total Length:</label></td>
								<td id="totalLength" class="value"></td>
							</tr>
						</table>
					</div>
					<div id="messageOverview">
						<table id="originalTable" style="display:none">
							<tr>
								<td rowspan="4" class="serialNo"></td>
								<td></td>
							</tr>
							<tr>
								<td></td>
							</tr>
							<tr>
								<td></td>
							</tr>
							<tr>
								<td></td>
							</tr>
						</table>
					</div>
						
					<div id="detailMessage">
						<div class="title">Type:</div>
						<div class="value type"></div>
						<hr>
						<div class="title">Name:</div>
						<div class="value name"></div>
						<hr>
						<div class="title">Address:</div>
						<div class="value address"></div>
						<hr>
						<div class="title">User Rating:</div>
						<div class="value userRating"></div>
						<hr>
						<div class="title">Phone:</div>
						<div class="value phone"></div>
						<hr>
						<div class="title">Telephone:</div>
						<div class="value telephone"></div>
						<hr>
						<div class="title">Longitude:</div>
						<div class="value longitude"></div>
						<hr>
						<div class="title">Latitude:</div>
						<div class="value latitude"></div>
					</div>
				</td>
			</tr>
			<tr>
				<td>
					<label for="name">Temporal Domain:</label>
					<div class="secondLevel">
						<div class="input-group input-group-sm">
						<span class="input-group-addon">Minimum Time(min):</span>
						<input type="text" id="minimumTime" disabled="disabled" class="form-control input-sm"/>
						</div>
						<br/>
						Maximal travel time(min):
						<div class="input-group input-group-sm">
							<input type="text" id="timeWithin" disabled="disabled" class="form-control"/>
							<span class="input-group-btn">
								<button class="btn" id="suggest">suggest</button>
							</span>
						</div>
					</div>
				</td>
			</tr>
			<tr>
				<td>
					<label for="name">Spatial Domain:</label>
					<div class="secondLevel">
						Start point:<input type="text" id="startPoint" class="form-control input-sm typeahead"/>
						<br/>
						Destination:<input type="text" id="destination" class="form-control input-sm typeahead"/>
					</div>
				</td>
			</tr>
			<tr>
				<td>
					<label for="name">Activity Domain:</label>
					<div class="secondLevel">
						<s:iterator var="topic" value="#request.topics">
							<div class="checkbox">
								<label class="checkbox-inline">
									<input type="checkbox" name="topic" value="<s:property value="#topic"/>" />
									<s:property value="#topic"/>
								</label>
							</div>
						</s:iterator>
					</div>
				</td>
			</tr>
			<tr>
				<td>
					<label for="name">Example:</label>
					<div class="secondLevel">
						<span class="input-group input-group-sm">
							<select id="example" class="form-control input-sm"></select>
							<span class="input-group-btn">
								<button class="btn" id="choose">Choose</button>
							</span>
						</span>
					</div>
				</td>
			</tr>
			
			<tr>
				<td id="action">
					<button id="ensure" class="btn btn-primary">Submit</button>
					<button id="reset" class="btn ">Reset</button>
				</td>
				<td style="text-align:center">
					<button id="show" class="btn" style="width:80%">show</button>
				</td>
			</tr>
		</table>
	</div>
	
	<script type="text/javascript" src="http://webapi.amap.com/maps?v=1.3&key=85cd744670d338f45f1ea81cc4a94c8e"></script>
	<script type="text/javascript">
		var map = new AMap.Map("mapContainer",{lang:"en"});    // 创建Map实例;
		var search,autoComplete;
		AMap.service(["AMap.PlaceSearch"], function(){ search= new AMap.PlaceSearch({pageSize: 1});});
		AMap.service(["AMap.Autocomplete"], function(){ autoComplete = new AMap.Autocomplete();});
		var startPointMarker=new AMap.Marker({icon:getIcon(-1),draggable:true,offset:new AMap.Pixel(-10,-25)});
		AMap.event.addListener(startPointMarker,"click",event_showPosition)
		AMap.event.addListener(startPointMarker,"dragend",getMinTime);
		AMap.event.addListener(startPointMarker,"dragging",event_showPosition);
		var destinationMarker=new AMap.Marker({content:getDestinationContent(),draggable:true,offset:new AMap.Pixel(-10,-25)});
		AMap.event.addListener(destinationMarker,"dragend",getMinTime);	
		AMap.event.addListener(destinationMarker,"dragging",event_showPosition);
		AMap.event.addListener(destinationMarker,"click",event_showPosition)
		
		AMap.event.addListener(destinationMarker,"dragstart",function(){
			destinationMarker.setContent(getDestinationContent());
		});	
		
		function event_showPosition(event)
		{
			showPosition(event.target);
		}
		
		function showPosition(marker)
		{
			$(".value").html("");
			
			var position=marker.getPosition();
			$(".value.longitude").html(position.getLng());
			$(".value.latitude").html(position.getLat());
		}
		
		function getDestinationContent()
		{
			//自定义点标记内容   
			var markerContent = document.createElement("div");
			markerContent.className = "markerContentStyle";
			
			//点标记中的图标
			var markerImg = document.createElement("div");
			markerImg.style.width = '23px';
			markerImg.style.height = '25px';
			//设定div背景图为你要的图片
			markerImg.style.backgroundImage = 'url(http://api.map.baidu.com/img/markers.png)';
			markerImg.style.backgroundPosition = '0px -'+10*25+'px';
			markerImg.id="markerDestination"
			
			markerContent.appendChild(markerImg);
			
			return markerContent;
		}
		
		//n=-1时获得起点的Icon；n=-2时获得终点的Icon
		function getIcon(n)
		{
			if(n==-1)
				n=12;
			else if(n==-2)
				n=10;
			
			var icon = new AMap.Icon({  
				image:"http://api.map.baidu.com/img/markers.png",
                imageOffset: new AMap.Pixel(0, 0 - n * 25),
                size:new AMap.Size(23, 25)}); // 设置图片偏移  
                
            return icon;
		}
		
		function clearMap()
		{
			map.clearMap();
			destinationMarker.setContent(getDestinationContent());
		}
		
		function setDetailMessage(poi)
		{
			$(".value.type").html(poi.type);
			$(".value.name").html(poi.name);
			$(".value.address").html(poi.address);
			$(".value.phone").html(poi.phone);
			$(".value.telephone").html(poi.telephone);
			$(".value.longitude").html(poi.location[0].toFixed(3));
			$(".value.latitude").html(poi.location[1].toFixed(3));
			$(".value.userRating").html(poi.userRating);
		}
		
		function getContent(n,poi)
		{
			//自定义点标记内容   
			var markerContent = document.createElement("div");
			markerContent.className = "markerContentStyle";
			
			markerContent.onmouseover=function(event){
				$(event.currentTarget).parents(".amap-marker").css("zIndex",1000);
			};
			markerContent.onmouseleave=function(event){
				$(event.currentTarget).parents(".amap-marker").css("zIndex",100);
			};
			
			//点标记中的图标
			var markerImg = document.createElement("div");
			markerImg.style.width = '23px';
			markerImg.style.height = '25px';
			//设定div背景图为你要的图片
			markerImg.style.backgroundImage = 'url(http://api.map.baidu.com/img/markers.png)';
			//设定背景图的坐标偏移量
			if(n==-2)
			{
				markerImg.style.backgroundPosition = '0px -'+10*25+'px';
				markerImg.id="markerDestination";
			}
			else
			{
				markerImg.style.backgroundPosition = '0px -'+n*25+'px';
				markerImg.id="marker"+n;
			}
			markerContent.appendChild(markerImg);
			
			//点标记中的文本
			var markerSpan = document.createElement("span");
			var content;
			if(n==-2)
			{
				content="Total Time:"+poi.totalTime.toFixed(2)+"(min)";
			}
			else
			{
				content="Type:"+poi.type+"</br>"+"Name:"+poi.name+"</br>"+"Total Time:"+poi.totalTime.toFixed(2)+"(min)";//+"Address:"+poi.address;
				markerContent.onclick=function()
				{
					setDetailMessage(poi);
				}
			}
			
			//if(poi.phone!=null && poi.phone!="")
				//content+="</br>"+"Phone:"+poi.phone;
			//if(poi.telephone!=null && poi.telephone!="")
				//content+="</br>"+"Telephone:"+poi.telephone;
			markerSpan.innerHTML =content; 
			markerContent.appendChild(markerSpan);
			markerSpan.className = "marketSpanStyle "+markerImg.id;
			if($("#show").val()=="show")
				markerSpan.style.display="none";
			
			return markerContent;
		}
		
		var places;
		
		function setCity()
		{
			map.setCity(places.val());
			autoComplete.setCity(places.val());
			
			$("#reset").click();
		}
		
		function recover()
		{
			$("input[id!='minimumTime']").prop("disabled",false);
			$("button,select").prop("disabled",false);
			startPointMarker.setDraggable(true);
			destinationMarker.setDraggable(true);
			places.prop("disabled",false);
		}
		
		function frozen()
		{
			$("input,button[id!='ensureButtonForMessageBox'][id!='cancelButtonForMessageBox'],select").prop("disabled",true);
			startPointMarker.setDraggable(false);
			destinationMarker.setDraggable(false);
			places.prop("disabled",true);
		}
		
		function reset()
		{
			clearMap();
			$(":text").val("");
			recover();
			$("#ensure,#timeWithin,#suggest,#show").prop("disabled",true);
			$(":checked").prop("checked",false);
			$("#suggest").val("suggest");
			clearInformation();
		}
		
		function clearInformation()
		{
			$(".table.clone").remove();
			$(".value").html("");
		}
		
		function timeToDistance(time)
		{
			return time/6*4000;
		}
		
		function distanceToTime(distance)
		{
			return parseFloat((distance/4000*6).toFixed(2));
		}
		
		function getMinTime()
		{
			//如果两个标注都在地图上，则输出两点间最小距离
		   	if(startPointMarker.getMap()!=null && destinationMarker.getMap()!=null)
		   	{
		   		frozen();
		   		var s=startPointMarker.getPosition(),d=destinationMarker.getPosition();
		   		var data={"sLng":s.lng,"sLat":s.lat,"dLng":d.lng,"dLat":d.lat,"place":places.val()};
				ajax("getValue_!getMinDistance",data,function(data)
				{
					$("#minimumTime").val(distanceToTime(data.distance));
					recover();
					suggestTime();
				});
		   	}
		}
		
		function suggestTime()
		{
			var time=parseFloat($("#minimumTime").val());
			if(!isNaN(time))
			{
				var n=$(":checkbox[name='topic']:checked").length;
				$("#suggest").text((time*(1+n*0.1)).toFixed(2));
				$("#suggest").prop("disabled",false);
			}
		}
		
		$(document).ready(function()
		{
			/*对英文的支持不好
			$('.typeahead').autocomplete({
			    source: function (query, process) {
			        autoComplete.search(query,function(status,autocompleteResult){
			        	if(status=="complete")
			        	{
			        		var result=new Array();
			        		if(autocompleteResult.count==0)
			        			return;
			        		var tips=autocompleteResult.tips;
			        		for(var i in tips)
			        		{
			        			result.push(tips[i].name);
			        		}
			        		process(result);
			        	}
			        });
			    }
			});*/
			
			$("#messageOverview").css("height",
					document.getElementById("messageShow").offsetHeight-document.getElementById("detailMessage").offsetHeight-
					document.getElementById("summary").offsetHeight);
			places=$("#places");
			
			places.change(setCity);
			
			$("#reset").click(reset);//必须在setCity之前，因为setCity会用到reset的触发事件
			setCity();
			
			$("#suggest").click(function()
			{
				$("#timeWithin").val($("#suggest").text());
			});
			
			$("#show").click(function(event){
				if(event.currentTarget.innerHTML=="show")
				{
					event.currentTarget.innerHTML="hide";
					$(".marketSpanStyle").css("display","block");
				}
				else
				{
					event.currentTarget.innerHTML="show";
					$(".marketSpanStyle").css("display","none");
				}
			});
			$(":text").change(function(event){
				var marker,location,target=event.target,location=$(target).val();
				
				if(target.id=="startPoint")	
				{
					marker=startPointMarker;
				}
				else if(target.id=="destination")
				{
					marker=destinationMarker;
				}
				else
				{
					return;
				}
				
				if(location==null || location=="")
				{
					marker.setMap(null);
					return;
				}
				
				search.setCity(places.val());
				marker.setMap(null);
				
				search.search(location,function(statues,results){
					if(statues!="complete" || results.poiList.count==0)
					{
						messageBox.alert("Can't find the place!");
						return;
					}
					
				   	var point=results.poiList.pois[0].location;
				   	marker.setPosition(point);
				   	marker.setMap(map);
				   	showPosition(marker);
				   	map.setFitView();
				   	
				   	getMinTime();
				});
			});
			
			var table=$("#originalTable")[0];
			var messageOverview=document.getElementById("messageOverview");
			$("#ensure").click(function(){
				var threshold=parseFloat($("#timeWithin").val());
				if(isNaN(threshold))
				{
					messageBox.alert("Please enter a right distance!");
					return;
				}
				if(threshold<($("#minimumTime").val()*1))
				{
					messageBox.alert("Please enter a distance greater than minimum distance!");
					return;
				}
				
				threshold=timeToDistance(threshold);//时间（min）转换成距离（m）
				
				var topic=$(":checkbox[name='topic']:checked");
				var length=topic.length;
				if(length==0)
				{
					messageBox.alert("You should choose 1 topic at least!");
					return;
				}
				var topics=new Array();
				for(var i=0;i<length;i++)
				{
					topics.push(topic[i].value);
				}
				
				frozen();
				clearInformation();
				clearMap();
				startPointMarker.setMap(map);
				destinationMarker.setMap(map);
				var start=startPointMarker.getPosition(),destination=destinationMarker.getPosition();
				
				ajax("getValue_!run",{"place":places.val(),"topics":topics,"threshold":threshold,
					"s":[start.lng,start.lat],"e":[destination.lng,destination.lat]},function(data){
					try
					{
						if(data.msg!=null)
						{
							messageBox.alert(data.msg);
							$("input[id!='minimumTime']").prop("disabled",false);
							return;
						}
						
						var pois=data.pois;
						var n=pois.length;
						var segmentNumber=0;
						var totalLength=0;
						for(var i=0;i<n;i++)
						{
							var points=new Array();
							segmentNumber+=pois[i].route.length-1;
							for(var j=0;j<pois[i].route.length;j++)
							{
								points.push(new AMap.LngLat(pois[i].route[j][0],pois[i].route[j][1]));
							}
							var polyline = new AMap.Polyline({map:map,path:points,strokeColor:i%2==0?"#FF0000":"#0000FF", strokeWeight:5, strokeOpacity:0.5});
							polyline.setMap(map);
						}
						$("#segmentNumber").html(segmentNumber);
						
						var totalTime=0,totalLength=0;
						for(var i=0;i<n;i++)
						{
							var t=table.cloneNode(true);
							t.id="table"+i;
							t.className="table clone";
							totalLength+=pois[i].length;
							pois[i].time=distanceToTime(pois[i].length)
							totalTime+=pois[i].time;
							pois[i].totalTime=totalTime;
							
							t.rows[0].cells[1].innerHTML=pois[i].type;
							t.rows[1].cells[0].innerHTML=pois[i].name;
							
							t.style.display="table";
							messageOverview.appendChild(t);
							
							//终点情况特殊
							if(i!=n-1)
							{
								t.rows[0].cells[0].innerHTML=String.fromCharCode(65+i)+":"; //展示第几个表格，从0开始
								if(i!=0)
								{
									t.rows[2].cells[0].innerHTML="From "+String.fromCharCode(64+i)+" to "+String.fromCharCode(65+i)+": "+pois[i].time+" (min)";
								}
								t.rows[3].cells[0].innerHTML+="From start point to "+String.fromCharCode(65+i)+": "+pois[i].totalTime.toFixed(2)+" (min)";
								var m=new AMap.Marker({map:map,offset:new AMap.Pixel(-10,-25),position:new AMap.LngLat(pois[i].location[0], pois[i].location[1]),content:getContent(i,pois[i])});
								m.setMap(map);
								t.onclick=function(event)
								{
									setDetailMessage(pois[event.currentTarget.id.substring(5)]);
								} 
							}
							else
							{
								t.rows[0].deleteCell(0);
								t.rows[2].cells[0].innerHTML="From "+String.fromCharCode(64+i)+" to destination: "+pois[i].time.toFixed(2)+" (min)";
								t.rows[3].cells[0].innerHTML="From start point to destination: "+pois[i].totalTime.toFixed(2)+" (min)";
								destinationMarker.setContent(getContent(-2,pois[i]));
								t.onclick=function(event)
								{
									showPosition(destinationMarker);
								} 
							}
						}
						$("#totalTime").html(totalTime.toFixed(2)+" (min)")
						$("#totalLength").html((totalLength/1000).toFixed(2)+" (km)");
						
						map.setFitView();
					}
					finally
					{
						recover();
					}
				},recover);
			});
			
			$(":checkbox").change(function(event){
				var n=$(":checkbox[name='topic']:checked").length;
				
				if(event.target.checked && n>5)
				{
					messageBox.alert("You can choose 5 topics at most!");
					event.target.checked=false;
					return;
				}
				
				suggestTime();
			});
		});
		
		//定义一个插件类 homeControlDiv，AMap为命名空间                
		AMap.typeControler = function(){};          
		AMap.typeControler.prototype = {
			addTo: function(map, dom){
			    dom.appendChild(this._getHtmlDom(map));
			},  
			_getHtmlDom:function(map){                
			    this.map = map;                
			    // 创建一个能承载控件的<div>容器                
			    var controlUI = document.createElement("div");
			    var controlUI1 = document.createElement("div");
			    var controlUI2 = document.createElement("div");
			    controlUI.appendChild(controlUI1);
			    controlUI.appendChild(controlUI2);
			    
			    this.container = controlUI;
			    var cssStr = 'position:absolute;right:10px;top:10px;height:28px;';
			    controlUI.style.cssText = cssStr;
			    cssStr = 'border:1px solid #ccc;font-size:14px;line-height:28px;text-align:center;background-color:#FFFFFF;';
			    cssStr += 'padding-left:10px;padding-right:10px;color:#000000;border-radius:3px;';
			    controlUI1.style.cssText = cssStr;		                  
			    controlUI1.innerHTML = "Map";
			    controlUI2.style.cssText = cssStr;		                  
			    controlUI2.innerHTML = "Satellite";
			    controlUI1.style.fontWeight = "bold";
			    controlUI1.style.marginBottom = "2px";
			    // 设置控件响应点击onclick事件                
			    controlUI1.onclick=function(){  
			    	controlUI2.style.fontWeight = "normal";
			    	controlUI1.style.fontWeight = "bold";
			    	
			        map.setLayers(new Array(new AMap.TileLayer()));
			    }        
			    controlUI2.onclick=function(){        
			    	controlUI1.style.fontWeight = "normal";
			    	controlUI2.style.fontWeight = "bold";
			        map.setLayers(new Array(new AMap.TileLayer.Satellite()));
			    }    
			    return controlUI;    
		   	}             
		};
		//新建自定义插件对象
		var typeControler=new AMap.typeControler(map); 
		map.addControl(typeControler);
	</script>
	<script type="text/javascript" src="scripts/examples.js"></script>
</body>