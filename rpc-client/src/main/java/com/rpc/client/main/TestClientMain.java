package com.rpc.client.main;

import com.rpc.bean.TestDemo;
import com.rpc.client.rpc.ServiceDiscovery;
import com.rpc.client.rpc.RpcClientProxy;
import com.rpc.client.rpc.ServiceDiscoveryImpl;
import com.rpc.client.rpc.ZkConfig;

public class TestClientMain {

    public static void main(String[] args) throws InterruptedException {
    	
    	// 创建zk的连接
        ServiceDiscovery serviceDiscovery = new
                ServiceDiscoveryImpl(ZkConfig.ZK_ADDRESS);

        // 创建代理对象
        RpcClientProxy rpcClientProxy = new RpcClientProxy(serviceDiscovery);
        
        // 调用10次体验负载均衡
        for(int i = 0; i < 10; i++) {
        	TestDemo gelloDemo = rpcClientProxy.clientProxy(TestDemo.class);
        	System.out.println(gelloDemo.test("我是第 " + (i+1) + "次客户端的发送数据"));
            Thread.sleep(1000);
        }
        
    }
    
}