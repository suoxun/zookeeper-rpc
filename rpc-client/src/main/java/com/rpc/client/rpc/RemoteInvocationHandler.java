package com.rpc.client.rpc;


import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.rpc.bean.RpcRequest;

public class RemoteInvocationHandler implements InvocationHandler {
	
    private ServiceDiscovery serviceDiscovery;

    public RemoteInvocationHandler(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 组装请求
        RpcRequest request = new RpcRequest();
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameters(args);

        // 根据接口名称得到对应的服务地址
        String serviceAddress = serviceDiscovery.discover(request.getClassName());
        
        //通过tcp传输协议进行传输
        TCPTransport tcpTransport = new TCPTransport(serviceAddress);
        
        //发送请求
        return tcpTransport.send(request);
    }
}
