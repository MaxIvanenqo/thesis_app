package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import oshi.SystemInfo;

public class HardwareDetect {
    public static String getHardware(){
        try {
            String path =System.getProperty("user.dir");
            Process p = Runtime.getRuntime().exec(path+"/hwdetector", null);
            InputStream is = p.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            return br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "not detected";
    }
}
