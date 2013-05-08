package com.guardian2;

public class User {
	private String name;
	private Integer userId, privilege;
	private String rfid;
	
	public User(){
		
	}
	
	public User(String name, String rfid, int privilege){
		this.setName(name);
		this.setRfid(rfid);
		this.setPrivilege(privilege);
		
	}
	public User(int userId, String name, String rfid, int privilege){
		this.setName(name);
		this.setUserId(userId);
		this.setRfid(rfid);
		this.setPrivilege(privilege);
	}
	public String getRfid() {
		return rfid;
	}
	public void setRfid(String rfid) {
		this.rfid = rfid;
	}
	public int getPrivilege() {
		return privilege;
	}
	public void setPrivilege(int privilege) {
		this.privilege = privilege;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
