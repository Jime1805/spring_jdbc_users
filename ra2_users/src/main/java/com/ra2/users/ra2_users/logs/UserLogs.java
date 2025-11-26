package com.ra2.users.ra2_users.logs;

import org.springframework.stereotype.Component;

@Component
public class UserLogs {
    private final String logDirectory = "private/logs/";

    public UserLogs() {
    }

    public void error(String frase, String classe, String modulo){
        // Escriurà en el fitxer un error
        try {
            
        } catch (Exception e) {
            
        }

    }

    public void info(String frase, String classe, String modulo){
        //Escriurà info en el fitxer
    }
}
