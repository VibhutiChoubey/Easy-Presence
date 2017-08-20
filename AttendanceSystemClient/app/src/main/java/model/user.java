package model;

public class user  {

    public String user_id,user_class,user_name,user_deviceid;
    public int user_roll;

    public user(String user_class,int user_roll,  String user_name, String user_deviceid ) {
        this.user_class = user_class;
        this.user_roll = user_roll;
        this.user_name = user_name;
        this.user_deviceid = user_deviceid;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
