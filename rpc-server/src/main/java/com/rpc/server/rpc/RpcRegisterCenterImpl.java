package com.rpc.server.rpc;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcRegisterCenterImpl implements RpcRegisterCenter{
	
	private static final Logger logger = LoggerFactory.getLogger(RpcRegisterCenterImpl.class);

    private CuratorFramework curatorFramework;
    
    public RpcRegisterCenterImpl(){
    	// 创建zk的连接
        curatorFramework = CuratorFrameworkFactory.builder().
        		// zk集群地址
                connectString(ZkConfig.ZK_ADDRESS).
                // session超时时间,如果超过了4秒钟,则进行下面的配置重连操作
                sessionTimeoutMs(4000).
                // 如果服务端挂了重连10次,每次间隔1000毫秒
                retryPolicy(new ExponentialBackoffRetry(1000,
                        10)).build();
        // 启动客户端连接zk
        curatorFramework.start();
    }

    public void register(String serviceName, String serviceAddress) {
    	
        // 注册服务中心的永久节点路径
        String servicePath = ZkConfig.ZK_REGISTER_PATH + "/" + serviceName;

        try {
        	
            // 判断 /registrys/test-server 永久节点是否存在,不存在则创建
            if(curatorFramework.checkExists().forPath(servicePath) == null){
            	// creatingParentsIfNeeded的意思为如果父节点不存在,把父节点也创建了
                curatorFramework.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(servicePath, "0".getBytes());
            }
            
            // 临时节点服务地址的完整路径
            String addressPath = servicePath + "/" + serviceAddress;
            // 注册服务地址,返回注册的路径
            String registerNode = curatorFramework.create().withMode(CreateMode.EPHEMERAL).forPath(addressPath, "0".getBytes());
            logger.info("服务注册成功了,临时注册节点地址为:{}", registerNode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}