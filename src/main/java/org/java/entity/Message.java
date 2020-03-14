package org.java.entity;

import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.util.List;

/**
 * 消息对象，用于封装，服务器端返回到客户端的消息
 */
@Data
//@AllArgsConstructor//带所有参数的构造方法
//@NoArgsConstructor//无参构造方法
public class Message {

    private String msg;//欢迎信息
    private List<String> names;//存放所有用户姓名集合
    private String content;//用于保存聊天内容

    private Integer type=2; //聊天类型 1：私聊  2：群聊   （私聊时，才显示消息提示框 ）
    private String toUser;//消息接收者 （只有消息接收者在私聊的情况，才显示消息框）

    /**
     * 编写方法，将当前对象，转换成json
     * @return
     */
    public String toJson(){

        return JSON.toJSONString(this);
    }

}
