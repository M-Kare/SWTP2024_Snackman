package de.hsrm.mi.swt.snackman;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SnackmanApplication {

	public static void main(String[] args) {
		//SpringApplication.run(SnackmanApplication.class, args);

		Map map = new Map(99);
        //map.printMap();

        char[][] laby = map.getMazeAsArray();

        for(int i = 0; i < laby[0].length; i++){
            for(int j = 0; j < laby.length; j++){
                System.out.print(laby[i][j]);
            }
            System.out.println();
        }
	}

}
