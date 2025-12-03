package com.ra2.users.ra2_users.logs;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

@Component
public class UserLogs {
    private final String logDirectory = "private/logs/";

    public UserLogs() {
        String path = "private/logs";
        Path fileDir = Paths.get(path);

        if(!Files.exists(fileDir)){
            try {
                Files.createDirectories(fileDir);
            } catch (IOException e) {
                System.err.println("Error al crear el fitxer logs: " + e);
            }
        }
    }

    public void error(String frase, String classe, String modulo){
        // Escriurà en el fitxer un error
        String fitxerAvui = obtenirFitxerAvui();
        Path currentFile = Paths.get(fitxerAvui);
        try {BufferedWriter writer = Files.newBufferedWriter(currentFile, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            String fechaHora = '[' + LocalDateTime.now().format(formato) + ']';

            String linea = fechaHora + " ERROR - " + classe + " - " + modulo + " - " + frase + "\n";
            writer.write(linea);
        } catch (Exception e) {
            System.err.println("Error: El log ".concat(fitxerAvui).concat(" és corrupte."));
        }

    }

    public void info(String frase, String classe, String modulo){
        //Escriurà info en el fitxer

        String fitxerAvui = obtenirFitxerAvui();
        Path currentFile = Paths.get(fitxerAvui);
        try {BufferedWriter writer = Files.newBufferedWriter(currentFile, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            String fechaHora = '[' + LocalDateTime.now().format(formato) + ']';

            String linea = fechaHora + " INFO - " + classe + " - " + modulo + " - " + frase + "\n";
            writer.write(linea);
        } catch (Exception e) {
            System.err.println("Error: El log ".concat(fitxerAvui).concat(" és corrupte."));
        }
    }

    public static String obtenirFitxerAvui(){
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String fecha = LocalDate.now().format(formato);
        return fecha.concat(".log");
    }
}
