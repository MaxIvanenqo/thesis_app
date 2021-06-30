package wwsis.ivanenqo.server.models;

import wwsis.ivanenqo.server.utils.ArrayKit;
import wwsis.ivanenqo.server.utils.IntByteArray;

import java.util.Objects;

public class UserShort {

    private final String email;

    private final String full_name;

    private final String phone_number;

    private final String identificator;

    private final String id;

    public UserShort(String identificator, String id, String email, String full_name, String phone_number) {
        this.id = id;
        this.identificator = identificator;
        this.email = email;
        this.full_name = full_name;
        this.phone_number = phone_number;
    }

    public byte[] toBytes() {
        return ArrayKit.joinArrays(
                IntByteArray.convertToBytes(this.id.getBytes().length),
                this.id.getBytes(),
                IntByteArray.convertToBytes(this.identificator.getBytes().length),
                this.identificator.getBytes(),
                IntByteArray.convertToBytes(this.email.getBytes().length),
                this.email.getBytes(),
                IntByteArray.convertToBytes(this.full_name.getBytes().length),
                this.full_name.getBytes()
        );
    }

    @Override
    public boolean equals(Object o) {
//      equals i hashCode są potrzebne dla kontrolowania dublikatów w HashSet.
//      zapytanie klienta o auto uzupełnianie może mięć tylko kilka liter.
//      serwer szuka w bazie danych wpisy "LIKE %zapytanie%". Zapytanie np "max"
//      może się trafić kilka razy dla jednego użytkownika. HashSet rozwiązuje problem
//      możliwych dublikatów
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserShort userShort = (UserShort) o;
        return Objects.equals(email, userShort.email) &&
                Objects.equals(full_name, userShort.full_name) &&
                Objects.equals(phone_number, userShort.phone_number);
    }


    @Override
    public int hashCode() {
        return Objects.hash(email, full_name, phone_number);
    }
}
