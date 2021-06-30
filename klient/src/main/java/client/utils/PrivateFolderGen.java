package client.utils;

import client.Global;

import java.io.File;

public class PrivateFolderGen {
    private static String pathGen(){
        return "localdb/"+ Global.getUsername()+"__private_folder/";
    }

    public static String key(String user, String keyType){
        String path = pathGen()+"/key/"+keyType+user+".key";
        File file = new File(path);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        return path;
    }

    public static String name(){
        String path = pathGen()+"/device_name.o";
        File file = new File(path);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        return path;
    }

    public static String username(){
        String path = pathGen()+"/username.o";
        File file = new File(path);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        return path;
    }

}
