package de.hsrm.mi.swt.snackman;

import de.hsrm.mi.swt.snackman.services.MapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SnackmanApplication {

    @Autowired
    private MapService mapService;

	public static void main(String[] args) {
		SpringApplication.run(SnackmanApplication.class, args);
	}

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            System.out.println("Anwendung gestartet. Labyrinth wird initialisiert...");
            char[][] maze = mapService.getMazeAsArray();
            printMaze(maze);
        };
    }

    private void printMaze(char[][] maze) {
        for (char[] row : maze) {
            System.out.println(new String(row));
        }
    }
}
