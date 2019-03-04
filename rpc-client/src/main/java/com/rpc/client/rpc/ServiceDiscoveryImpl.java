package com.rpc.client.rpc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class ServiceDiscoveryImpl implements ServiceDiscovery{

    List<String> repos = new ArrayList<>();

    private CuratorFramework curatorFramework;

    public ServiceDiscoveryImpl(String address) {
        curatorFramework = CuratorFrameworkFactory.builder().
                connectString(address).
                sessionTimeoutMs(4000).
                retryPolicy(new ExponentialBackoffRetry(1000,
                        10)).build();
        curatorFramework.start();
    }

    public String discover(String serviceName) {
    	
        String path = ZkConfig.ZK_REGISTER_PATH + "/" + serviceName;
        try {
            repos = curatorFramework.getChildren().forPath(path);
        } catch (Exception e) {
            throw new RuntimeException("获取子节点异常:{}", e);
        }
        
        // 动态发现服务节点的变化
        registerWatcher(path);
        
        // 负载均衡随机算法(返回调用的服务地址)
        if (repos == null || repos.size() == 0){
            return null;
        } else if(repos.size() == 1){
        	 return repos.get(0);
        } else {
        	int len = repos.size();
            Random random = new Random();
            return repos.get(random.nextInt(len));
        }
    }

    @SuppressWarnings("resource")
	private void registerWatcher(final String path){
        PathChildrenCache childrenCache = new PathChildrenCache
                (curatorFramework,path,true);

        PathChildrenCacheListener pathChildrenCacheListener = new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent pathChildrenCacheEvent) throws Exception {
                repos = curatorFramework.getChildren().forPath(path);
            }
        };
        childrenCache.getListenable().addListener(pathChildrenCacheListener);
        try {
            childrenCache.start();
        } catch (Exception e) {
           throw new RuntimeException("Watcher机制监听临时子节点出现异常,异常为:{}", e);
        }
    }
    
}