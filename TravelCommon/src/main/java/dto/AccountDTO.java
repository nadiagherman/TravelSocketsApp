package dto;

import java.io.Serializable;

public class AccountDTO implements Serializable {
    public String name;
    public String password;
    public final Integer id;

    public AccountDTO(String name, String password) {
        this(null, name, password);
    }

    public AccountDTO(Integer id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public AccountDTO(Integer accountId) {
        id = accountId;
    }
}
