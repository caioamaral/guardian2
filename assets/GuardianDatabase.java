package com.guardian2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQuery;
import android.util.Log;


public class GuardianDatabase extends SQLiteOpenHelper{
	
	public static final String USERS_TABLE_NAME = "users";
	public static final String ROOMS_TABLE_NAME = "rooms";
	public static final String DATABASE_NAME =  "guardiantest" +".db";
	private static int DATABASE_VERSION = 1;
	public static final int USER_ID_COLUMN = 0;
	public static final int USER_NAME_COLUMN = 1;
	public static final int USER_RFID_COLUMN = 2;
	public static final int USER_PRIVILEGE_COLUMN = 3;
	public static final int ROOM_ID_COLUMN = 0;
	public static final int ROOM_NAME_COLUMN = 1;
	public static final int ROOM_REQUIRED_PRIVILEGE_COLUMN = 2;

	
	public GuardianDatabase(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase){
		createTableUsers(sqLiteDatabase);
		createTableRooms(sqLiteDatabase);
		
	}
	
	private void createTableUsers(SQLiteDatabase sqLiteDatabase) {
		String qs = "CREATE TABLE " + USERS_TABLE_NAME + " (" +
				"user_id" + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"name" + " TEXT NOT NULL, " +
				"rfid" + " TEXT NOT NULL, " +
				"privilege" + " INTEGER NOT NULL" +");";
		sqLiteDatabase.execSQL(qs);
	}
	
	private void createTableRooms(SQLiteDatabase sqLiteDatabase) {
		String qs = "CREATE TABLE " + ROOMS_TABLE_NAME + " (" +
				"room_id" + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"name" + " TEXT, " +
				"required_privilege" + " INTEGER" +");";
		sqLiteDatabase.execSQL(qs);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase,
							int oldv, int newv)
	{
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +
								USERS_TABLE_NAME + ";");
		createTableUsers(sqLiteDatabase);
		sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +
				ROOMS_TABLE_NAME + ";");
		createTableUsers(sqLiteDatabase);
	}
	
	public UsersCursor getUserByRfid(UsersCursor.SortBy sortBy, String rfid){
		String sql = UsersCursor.QUERY_USERS1 + rfid + "' ORDER BY " + sortBy.toString();
		SQLiteDatabase d = getReadableDatabase();
		UsersCursor c = (UsersCursor) d.rawQueryWithFactory(new UsersCursor.Factory(), sql, null, null);
		if(c!=null && c.getCount()>0){
			c.moveToFirst();
			return c;
		}
		return null;
	}
	
	public UsersCursor getUsers(UsersCursor.SortBy sortBy){
		String sql = UsersCursor.QUERY_USERS + sortBy.toString();
		SQLiteDatabase d = getReadableDatabase();
		UsersCursor c = (UsersCursor) d.rawQueryWithFactory(new UsersCursor.Factory(), sql, null, null);
		if(c!=null && c.getCount()>0){
			c.moveToFirst();
			return c;
		}
		return null;
	}
	
	public RoomsCursor getRooms(RoomsCursor.SortBy sortBy){
		String sql = RoomsCursor.QUERY_ROOMS + sortBy.toString();
		SQLiteDatabase d = getReadableDatabase();
		RoomsCursor c = (RoomsCursor) d.rawQueryWithFactory(new RoomsCursor.Factory(), sql, null, null);
		c.moveToFirst();
		return c;
	}

	
	public static class RoomsCursor extends SQLiteCursor{
		public static enum SortBy{
			name,
			required_privilege
		}
		private static final String QUERY_ROOMS = 
				"SELECT rooms.room_id, name, required_privilege " +
				"FROM rooms " +
				"ORDER BY ";

		private RoomsCursor(SQLiteDatabase db, SQLiteCursorDriver driver,
			String editTable, SQLiteQuery query){
			super(db, driver, editTable, query);
		}
		private static class Factory implements SQLiteDatabase.CursorFactory{
			@Override
			public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver driver, String editTable,SQLiteQuery query){
				return new UsersCursor(db, driver, editTable, query);
			}
		}
		public String getColRoomsNames(){
			return getString(getColumnIndexOrThrow("rooms.name"));
		}
		
		public int getColRoomsPrivilege(){
			return getInt(getColumnIndexOrThrow("rooms.required_privilege"));
		}
		
		
	}

	public static class UsersCursor extends SQLiteCursor{
		public static enum SortBy{
			user_id,
			name,
			rfid,
			privilege
		}
		private static final String QUERY_USERS = 
				"SELECT users.user_id, name, rfid, privilege " +
				"FROM users " +
				"ORDER BY ";
		private static final String QUERY_USERS1 = 
				"SELECT users.user_id, users.name, users.rfid, users.privilege " +
				"FROM users " +
				"WHERE users.rfid = '";
		private UsersCursor(SQLiteDatabase db, SQLiteCursorDriver driver,
			String editTable, SQLiteQuery query){
			super(db, driver, editTable, query);
		}
		private static class Factory implements SQLiteDatabase.CursorFactory{
			@Override
			public Cursor newCursor(SQLiteDatabase db, SQLiteCursorDriver driver, String editTable,SQLiteQuery query){
				return new UsersCursor(db, driver, editTable, query);
			}
		}
		public String[] getColumns(){
			return getColumnNames();
		}
		public int getColUserId(){
			return getInt(0);
		}
		public String getColUserNames(){
			return getString(1);
		}
		public String getColUserRfid(){
			return getString(2);
		}
		public int getColUserPrivilege(){
			return getInt(3);
		}	
	}
	
	public int addUser(String name, String rfid, int privilege){
		int flag = 1;
		String sql = "INSERT INTO users " +
				"(user_id, name, rfid, privilege) " +
				"VALUES (NULL, '" + name + "', '" + rfid + "', '" + privilege + "')";
		//Object[] bindArgs = new Object[]{name, rfid, privilege};
		try{
			getWritableDatabase().execSQL(sql);
		}catch(SQLException e){
			Log.e("Error writing new user", e.toString());
			flag = 0;
		}
		return flag;
	}
	public int editUser(int user_id, String name, String rfid, int privilege){
		int flag = 1;
		String sql = "UPDATE users " +
				"SET name = ?, " +
				" rfid = ?, "+
				" privilege = ? "+
				"WHERE user_id = ? ";
		Object[] bindArgs = new Object[]{name, rfid, privilege, user_id};
		try{
			getWritableDatabase().execSQL(sql,bindArgs);
		}catch(SQLException e){
			Log.e("Error writing new user", e.toString());
			flag = 0;
		}
		return flag;
	}
	@SuppressLint("DefaultLocale")
	public int deleteUser(int user_id){
		int flag = 1;
		String sql = String.format("DELETE FROM users " +
				"WHERE user_id = '%d' ",
				user_id);
		try{
			getWritableDatabase().execSQL(sql);
		}catch(SQLException e){
			Log.e("Error deleting user", e.toString());
			flag = 0;
		}
		return flag;
	}
	public void addRoom(String name, int required_privilege){
		String sql = "INSERT INTO users " +
				"(user_id, name, required_privilege) " +
				"VALUES " +
				"(NULL,    ?,    ?)";
		Object[] bindArgs = new Object[]{name, required_privilege};
		try{
			getWritableDatabase().execSQL(sql,bindArgs);
		}catch(SQLException e){
			Log.e("Error writing new room", e.toString());
		}
	}
	
	public int editRoom(int room_id, String name, int required_privilege){
		int flag = 1;
		String sql = "UPDATE rooms " +
				"SET name = ?, " +
				" required_privilege = ? "+
				"WHERE room_id = ? ";
		Object[] bindArgs = new Object[]{name, required_privilege, room_id};
		try{
			getWritableDatabase().execSQL(sql,bindArgs);
		}catch(SQLException e){
			flag = 0;
			Log.e("Error writing new room", e.toString());

		}
		return flag;
	}
	@SuppressLint("DefaultLocale")
	public void deleteRoom(int room_id){
		String sql = String.format("DELETE FROM rooms " +
				"WHERE room_id = '%d' ",
				room_id);
		try{
			getWritableDatabase().execSQL(sql);
		}catch(SQLException e){
			Log.e("Error releting room", e.toString());
		}
	}
	
	
}
