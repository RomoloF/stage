package it.cript.model;

public class Response {
    private Integer status;
    private String message;
    private String internalMessage;
    private Object data;
    private Integer countData;

    public Integer getStatus() {
       return status;
    }
    public void setStatus(Integer status) {
       this.status = status;
    }
    public String getMessage() {
       return message;
    }
    public void setMessage(String message) {
       this.message = message;
    }
    public String getInternalMessage() {
       return internalMessage;
    }
    public void setInternalMessage(String internalMessage) {
       this.internalMessage = internalMessage;
    }
    public Object getData() {
       return data;
    }
    public void setData(Object data) {
       this.data = data;
    }
    public Integer getCountData() {
       return countData;
    }
    public void setCountData(Integer countData) {
       this.countData = countData;
    }

}