package it.cript.model;

import javax.crypto.SecretKey;

public class Response {
    private Integer status;
    private String message;
    private String internalMessage;
    private Object data;
    private Integer countData;
    private String secretKey;
    private String jsonOriginale;
    private String JsonCriptato;
    private String clientID;
    
    public String getClientID() {
		return clientID;
	}
	public void setClientID(String clientID) {
		this.clientID = clientID;
	}
	public String getSecretKey() {
		return secretKey;
	}
	public void setSecretKey(String key) {
		this.secretKey = key;
	}
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
	public void setEncryptedJson(String encryptedJson) {
		// TODO Auto-generated method stub
		
	}
	public void setEncryptedJson_originale(String jsonOriginale) {
		// TODO Auto-generated method stub
		
	}
	public void setStato(String string) {
		// TODO Auto-generated method stub
		
	}
	public String getJsonOriginale() {
		return jsonOriginale;
	}
	public void setJsonOriginale(String jsonOriginale) {
		this.jsonOriginale = jsonOriginale;
	}
	public String getJsonCriptato() {
		return JsonCriptato;
	}
	public void setJsonCriptato(String jsonCriptato) {
		JsonCriptato = jsonCriptato;
	}
	
	

}