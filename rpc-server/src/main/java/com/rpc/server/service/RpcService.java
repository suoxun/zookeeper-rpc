package com.rpc.server.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rpc.server.annotation.RpcAnnotation;
import com.rpc.server.rpc.ProcessorHandler;
import com.rpc.server.rpc.RpcRegisterCenter;

/**
 * 发布远程服务
 * @author suoxun
 *
 */
public class RpcService {
	
	private static final Logger logger = LoggerFactory.getLogger(RpcService.class);
	
    // 创建一个线程池
    private static final ExecutorService executorService=Executors.newCachedThreadPool();

    // 注册中心
    private RpcRegisterCenter rpcRegisterCenter;
    // 服务发布地址
    private String serviceAddress;

    // 存放服务名称和服务对象之间的关系
    Map<String,Object> handlerMap = new HashMap<>();

    public RpcService(RpcRegisterCenter rpcRegisterCenter, String serviceAddress) {
        this.rpcRegisterCenter = rpcRegisterCenter;
        this.serviceAddress = serviceAddress;
    }

    /**
     * 绑定服务名称和服务对象
     * @param services
     */
    public void bind(Object... services){
        for(Object service : services){
            RpcAnnotation annotation = service.getClass().getAnnotation(RpcAnnotation.class);
            String serviceName = annotation.value().getName();
            // 绑定服务接口名称对应的服务
            handlerMap.put(serviceName, service);
        }
    }

    public void publisher(){
    	
    	// ServerSocket服务端,阻塞IO去监听客户端的请求
        ServerSocket serverSocket = null;
        
        try{
        	
        	// 127.0.0.1 、 8080
            String[] addrs = serviceAddress.split(":");
            
            // 启动一个服务监听
            serverSocket = new ServerSocket(Integer.parseInt(addrs[1]));

            for(String interfaceName : handlerMap.keySet()){
            	rpcRegisterCenter.register(interfaceName, serviceAddress);
            	logger.info("注册服务成功,接口地址名称路径为:{},网络地址为:{}",interfaceName,serviceAddress);            }

            // 死循环监听
            while(true) {
            	// 阻塞IO
                Socket socket = serverSocket.accept(); 
                // 通过线程池去处理请求
                executorService.execute(new ProcessorHandler(socket,handlerMap));
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(serverSocket != null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
    
}