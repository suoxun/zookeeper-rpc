package com.rpc.server.main;

import java.io.IOException;

import com.rpc.bean.TestDemo;
import com.rpc.bean.TestDemoOne;
import com.rpc.server.rpc.RpcRegisterCenter;
import com.rpc.server.rpc.RpcRegisterCenterImpl;
import com.rpc.server.service.RpcService;

public class TestServerMain01 {
	
    public static void main(String[] args) throws IOException {
    	// 发布的接口class
        TestDemo helloDemo = new TestDemoOne();
        // rpc发布中心
        RpcRegisterCenter registerCenter = new RpcRegisterCenterImpl();
        // 服务Service
        RpcService rpcServer = new RpcService(registerCenter,"127.0.0.1:8080");
        // 绑定接口
        rpcServer.bind(helloDemo);
        // 注册到zookeeper上
        rpcServer.publisher();
        // 阻塞进程
        System.in.read();
    }
    
}