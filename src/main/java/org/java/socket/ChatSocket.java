package org.java.socket;

import com.alibaba.fastjson.JSON;
import org.java.entity.Info;
import org.java.entity.Message;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@ServerEndpoint("/chat") //ws://localhost:7777/chat
public class ChatSocket {

    //该集合，用于保存，所有与服务器建立好的连接管道
    //定义成static的目的是，该集合只能被初始化一次(如果每一次都初始化，就会导致之前的连接管道丢失)
    private static List<Session> sesList = new ArrayList<>();

    //存放所有的用户名
    private static List<String> names = new ArrayList<>();

    //定义集合，存储每一个用户对应的管道
    private static Map<String,Session> sesMap = new HashMap<>();

    //定义一个变量，保存当前登录的用户名
    private String user;

    @OnOpen
    public void open(Session ses) throws IOException {

        System.out.println("客户端与服务端已建立连接");

        //得到当前管道的用户名
        String args = ses.getQueryString(); //得到请求路径后携带的参数  user=AAA          user AAA
        user = args.split("=")[1];

        //将每一个用户名与对应的管道存储在map中
        sesMap.put(user,ses);

        //将当前管道，存放在list中
        sesList.add(ses);
        //将用户名存放在list集合
        names.add(user);

        //当用户登录时，向所有的用户管道，推送一条欢迎信息

        //组装欢迎信息
        String msg = "欢迎[<b style='color:red'>"+user+"</b>]来到聊天室！";

        //把欢迎信息，以及，用户列表，封装成Message对象
        Message message = new Message();
        message.setMsg(msg);//欢迎信息
        message.setNames(names);//用户列表

        //向所有管道，发送欢迎消息-------------广播------------把message对象，转换成json,进行广播
        broadcast(message.toJson());
    }

    //广播消息的方法
    private void broadcast(String json) throws IOException {

        //对管道的集合循环，向每一个管道发送一条消息
        for (Session session : sesList) {

            session.getBasicRemote().sendText(json);
        }
    }


    /**
     * 关闭管道时，调用的方法
     * session:即为，正在关闭的管道
     */
    @OnClose
    public void close(Session session) throws IOException {

        //将当前管道，从集合中进行移除
        sesList.remove(session);

        //将当前用户名，从用户列表中移除
        names.remove(user);

        //组装消息，提示有用户离开聊天室
        String msg = "[<b style='color:green'>"+user+"</b>]离开了聊天室";

        //组装Message用于封装消息
        Message message  = new Message();
        message.setMsg(msg);
        message.setNames(names);

        broadcast(message.toJson());
    }


    /**
     * 接收消息的方法
     * Session:哪一个管道发送的消息
     * json:消息的内容
     */
    @OnMessage
    public void showMsg(Session ses,String json) throws IOException {


        //将json转换成Info对象
        Info info = JSON.parseObject(json,Info.class);

        //创建SimpleDateFormat用于转换时间格式
        SimpleDateFormat sf =new SimpleDateFormat("HH:mm:ss");

        //判断是群聊还是私聊
        if(info.getType()==1){
            //私聊
            String toUsers = info.getToUser();//获得所有要私聊的用户名称  aaa,bbb,

            //按,将字符串切割成数组  包含所有的用户名称
            String[] users = toUsers.split(",");

            String val = "<B style='color:blue'>(悄悄话~~~)"+user+"</B>说:"+info.getContent()+"  "+sf.format(new Date());
            //组装消息内容

            //将消息封成Message
            Message message = new Message();
            message.setContent(val);
            message.setType(1);//私聊

            //取得每一个用户的管道
            for(String toUser:users){
                //根据用户名，取得对应的管道
                Session toSession  = sesMap.get(toUser);
                message.setToUser(toUser);//私聊的用户
                //发到送对方的管道以及自己的管道
                toSession.getBasicRemote().sendText(message.toJson());
            }

            ses.getBasicRemote().sendText(message.toJson());


        }else{
            //群聊

            //组装消息内容
            String val = "<B style='color:blue'>"+user+"</B>说:"+info.getContent()+"  "+sf.format(new Date());

            //将消息封成Message
            Message message = new Message();
            message.setContent(val);

            //广播消息
            broadcast(message.toJson());
        }










    }




}
