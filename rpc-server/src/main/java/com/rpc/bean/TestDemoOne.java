package com.rpc.bean;

import com.rpc.server.annotation.RpcAnnotation;

@RpcAnnotation(value = TestDemo.class)
public class TestDemoOne implements TestDemo {
	
    public String test(String msg) {
        return "我是8080端口,信息为:" + msg;
    }
    
}
