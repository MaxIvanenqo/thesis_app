package wwsis.ivanenqo.server.utils.crypto;

import java.security.SecureRandom;

public class RandomCodeGen {
    public static String generate(int n){
        SecureRandom r = new SecureRandom();
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < n; ++i){
            code.append(r.nextInt(10));
        }
        return code.toString();
    }
}
