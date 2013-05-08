package com.guardian2;

import com.guardian2.GuardianDatabase.UsersCursor;

import android.content.Context;

public class UserController {
	private final Context mContext;
	GuardianDatabase databaseHelper;
	
	public UserController(Context context){
		mContext = context;
		databaseHelper = new GuardianDatabase(mContext);
	}
	
	public User constructUser(String name, String rfid, int privilege){
		User user = new User();
		user.setName(name);
		user.setRfid(rfid);
		user.setPrivilege(privilege);
		return user;
	}
	

	
	public int requestAddNewUser(String name, String rfid, int privilege){
		int flag = databaseHelper.addUser(name, rfid, privilege);
		return flag;
	}
	
	public int requestAddNewUser(User user){
		int flag = databaseHelper.addUser(user.getName(), user.getRfid(), user.getPrivilege());
		return flag;
	}
	
	public int requestEditUser(Integer userId, String name, String rfid, int privilege){
		int flag = databaseHelper.editUser(userId, name, rfid, privilege);
		return flag;
	}
	
	public int requestEditUser(User user, String newName, String newRfid, int newPrivilege){
		int flag = databaseHelper.editUser(user.getUserId(), newName, newRfid, newPrivilege);
		return flag;
	}
	
	public int requestChangePrivilege(User user, int newPrivilege){
		int flag = databaseHelper.editUser(user.getUserId(), user.getName(), user.getRfid(), newPrivilege);
		return flag;
	}
	
	public int requestChangeName(User user, String newName){
		int flag = databaseHelper.editUser(user.getUserId(), newName, user.getRfid(), user.getPrivilege());
		return flag;
	}
	
	public int requestChangeRfid(User user, String newRfid){
		int flag = databaseHelper.editUser(user.getUserId(), user.getName(), newRfid, user.getPrivilege());
		return flag;
	}
	
	public int requestDeleteUser(User user){
		int flag = databaseHelper.deleteUser(user.getUserId());
		return flag;
	}	
	
	public int requestDeleteUser(int userId){
		int flag = databaseHelper.deleteUser(userId);
		return flag;
	}
	
	public UsersCursor getUsers(UsersCursor.SortBy sortBy){
		UsersCursor c;
		c = databaseHelper.getUsers(sortBy);
		return c;
	}

	public User getUserByRfid(String valueOf) {
		UsersCursor c;
		User user = new User();
		c = databaseHelper.getUserByRfid(UsersCursor.SortBy.name, valueOf);

		if(c == null){
			user.setUserId(00000); 
			user.setName("Not Registered");
			user.setRfid("0000000000");
			user.setPrivilege(0);
			return user;
		}
		else {
			user.setUserId(c.getColUserId()); 
			user.setName(c.getColUserNames());
			user.setRfid(c.getColUserRfid());
			user.setPrivilege(c.getColUserPrivilege());
			return user;
		}
	}
}
