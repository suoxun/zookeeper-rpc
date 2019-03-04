package com.rpc.client.rpc;

import java.lang.reflect.Proxy;

public class RpcClientProxy {

    private ServiceDiscovery serviceDiscovery;

    public RpcClientProxy(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    /**
     * 创建客户端的远程代理,通过远程代理进行访问
     * @param interfaceCls
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
	public <T> T clientProxy(final Class<T> interfaceCls){
        // 使用到了动态代理
        return (T)Proxy.newProxyInstance(interfaceCls.getClassLoader(),
                new Class[]{interfaceCls},new RemoteInvocationHandler(serviceDiscovery));
    }

}