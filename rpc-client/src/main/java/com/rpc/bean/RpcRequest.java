package com.rpc.bean;

import java.io.Serializable;

public class RpcRequest implements Serializable {

	// 需要序列化的对象必须有serialVersionUID
	private static final long serialVersionUID = 6252882026297513864L;
	
	private String className;
    private String methodName;
    private Object[] parameters;
    
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
}
