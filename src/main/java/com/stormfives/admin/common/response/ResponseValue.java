package com.stormfives.admin.common.response;

/**
 * Created by fly on 17/12/05.
 */
public class ResponseValue {

    private static final long serialVersionUID = 4994712878622993977L;

    private Object data;
    private boolean success = true;


    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

}
