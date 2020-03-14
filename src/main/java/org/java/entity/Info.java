package org.java.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * 用于封装客户端向服务端传递的数据
 */
@Data
public class Info implements Serializable {

    private Integer type=2;//聊天类型   1：私聊  2：群聊
    private String content;//聊天内容
    private String toUser;//私聊的对象
}
