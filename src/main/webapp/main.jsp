<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2020/3/10
  Time: 16:23
  To change this template use File | Settings | File Templates.
--%>
<%@ page isELIgnored="false" contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <style>
        table,tr,td,th{
            border: 1px black solid;
            border-collapse: collapse;
        }
    </style>
    <link type="text/css" rel="stylesheet" href="${pageContext.request.contextPath}/css/notiflix-1.3.0.min.css">

    <script src="${pageContext.request.contextPath}/js/jquery-1.12.4.js"></script>
    <script src="${pageContext.request.contextPath}/js/notiflix-1.3.0.min.js"></script>
    <script>
            var ws = null;
            var name = '${sessionScope.name}';//获得当前用户名
            var target = "ws://localhost:8888/chat?user="+name;


            window.onbeforeunload = function(event) {
                //alert("##########>>>>>>>>");
                ws.onclose = function() {
                };
                ws.close();
            }



            $(function () {
                //建立连接，同时判断当前浏览器是否支持webSocket
                if ('WebSocket' in window) {
                    ws = new WebSocket(target);
                } else if ('MozWebSocket' in window) {
                    ws = new MozWebSocket(target);
                } else {
                    alert("你的浏览器不支持websocket");
                    return;
                }
                ws.onopen = function () {
                   //alert("已创建连接");
                };

                /***********显示消息框插件配置***********/
                Notiflix.Notify.Init();
                Notiflix.Report.Init();
                Notiflix.Confirm.Init();
                Notiflix.Loading.Init({
                    clickToClose:true
                });

                //服务器有消息返回时，回调的方法
                ws.onmessage = function (event) {

                    //只时，返回的数据，只是像json，它并不真正的json格式
                    //将返回的数据，转换成json格式，赋值给指定的变量
                    eval("var t = "+event.data); // t：即为转换后的json对象


                    //判断，如果有欢迎信息，就在左侧显示
                    var msg = t.msg;
                    if(msg!=undefined && msg!=""){
                        $("<div>"+msg+"</div>").appendTo("#left");
                    }

                    //判断是否有聊天的消息
                    var content = t.content;
                    if(content!=undefined && content!=""){

                        //判断，当前的聊天内容是否为私聊，并且当前用户是消息的接收者才显示消息框
                        if(t.type==1 && t.toUser==name){
                            Notiflix.Notify.Success(t.content);//显示提示消息框
                        }
                        //主界面显示聊天内容
                        $("<div>"+content+"</div>").appendTo("#left");
                    }



                    //判断，如果有用户列表，就显示在右侧
                    var names = t.names;
                    if(names!=undefined && names!=""){
                        //先清空原有的用户列表
                        $("#right").html("");
                        //拼接用户列表
                        var vals = "";
                        $.each(names,function (index,k) {
                            vals+="<input type='checkbox' name='name' value='"+k+"'>"+k;
                            vals+="<Br>";
                        })
                        $("#right").html(vals);
                    }

                };


                //给发送消息的按钮，绑定事件，点击时，将消息发送到服务端
                $("#btn").click(function () {

                    //取得消息框的值
                    var msg = $("#msg").val();

                    //获得复选框，选中的值，判断当前是要群聊还是要私聊
                    var users = $("#right :checked");

                    //定义一个变量，用于拼接所有选中的用户名称
                    var toUsers ="";

                    $.each(users,function (index,k) { //k即为当前选中的复选框
                        toUsers+=k.value+",";
                    })

                    // alert(toUsers);

                    //alert("当前选择的用户个数为:"+users.size());

                    //创建一个对象，封装要向服务端传递的数据
                    var obj=null;

                    //目前暂时只与一个用户私聊
                    if(users.size()==0){

                       //群聊
                        obj={
                            type:2, //群聊
                            content:msg
                        };

                    }else{
                        //私聊
                        obj={
                            type:1, //私聊
                            content:msg,//内容
                            toUser: toUsers //私聊的对象      aaa,bbb,
                        }
                    }

                    //将当前对象，转换成json
                    var json = JSON.stringify(obj);

                    //通过管道发送消息
                    ws.send(json);

                    //清空消息框的内容
                    $("#msg").val("");
                });

            });
    </script>
</head>
<body style="margin: 0px">
    <Table width="100%" height="100%">
        <Tr height="80%">
            <Td width="80%" id="left" valign="top"></Td>
            <Td id="right" valign="top"></Td>
        </Tr>
        <Tr>
            <Td colspan="2">
                [<B style="color: red">${sessionScope.name}</B>]
                消息:<input type="text" size="40px" id="msg">
                     <input type="button" id="btn" value="发送">
            </Td>
        </Tr>
    </Table>
</body>
</html>
