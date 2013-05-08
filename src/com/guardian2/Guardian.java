package com.guardian2;

import android.app.Application;

public class Guardian extends Application{
	private int roomPrivilege;

    public int getRoomPrivilege() {
        return roomPrivilege;
    }

    public void setRoomPrivilege(int newPrivilege) {
        this.roomPrivilege = newPrivilege;
    }

}
