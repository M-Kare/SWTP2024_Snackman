package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.util.FileSystemUtils;

import de.hsrm.mi.swt.snackman.SnackmanApplication;

class DefaultChickenTest {

    private static final Path workFolder = Paths.get("./extensions").toAbsolutePath();

    @BeforeAll
    static void fileSetUp() {
        try{
            tearDownAfter();  
        }catch(Exception e){
            System.out.println("No file to delete");
        }   
        SnackmanApplication.checkAndCopyResources();
    }

    @AfterAll
    static void tearDownAfter() throws IOException {
        if (Files.exists(workFolder)) {
            FileSystemUtils.deleteRecursively(workFolder.toFile());
        }
    }

    @Test
    public void chickenMovementTest_big(){

        System.out.println();
        Chicken chicken = new Chicken();

        List<String> visibleEnvironment = List.of("W", "W", "W", "L", "W",
                                                    "W", "L", "L", "L", "L",
                                                    "L", "W", "H", "W", "L",
                                                    "L", "W", "L", "W", "L",
                                                    "L", "W", "L", "W", "L", "0");

        List<String> result = chicken.act(visibleEnvironment);

        //int chosenDirectionIndex = Integer.parseInt(result.get(12));
        int chosenDirectionIndex = Integer.parseInt(result.get(result.size() - 1));

        assertEquals(" ", result.get(chosenDirectionIndex),
                "The Chicken should move to the empty space (' ') matching its new direction.");
    }

    @Test
    public void ChickenAvoidsGhost_big(){
        Chicken chicken = new Chicken();

        List<String> visibleEnvironment = List.of("W", "W", "W", "L", "W",
                                                    "SM", "L", "G", "L", "L",
                                                    "L", "L", "H", "L", "L",
                                                    "L", "L", "L", "L", "L",
                                                    "L", "W", "L", "W", "L", "0");

        List<String> result = chicken.act(visibleEnvironment);

        //int chosenDirectionIndex = Integer.parseInt(result.get(12));
        int chosenDirectionIndex = Integer.parseInt(result.get(result.size() - 1));

        assertTrue(Integer.parseInt(result.get(result.size() - 1)) != 0, "New ChickenIndex must not be the Index with the Ghost");

        assertEquals(" ", result.get(chosenDirectionIndex),
                "The Chicken should move to the empty space (' ') matching its new direction.");
    }

    @Test
    public void testChickenDoesntSeeSnackMan_big(){
        Chicken chicken = new Chicken();

        List<String> visibleEnvironment = List.of("W", "W", "W", "L", "W",
                                                    "SM", "L", "G", "SM", "L",
                                                    "L", "L", "H", "L", "L",
                                                    "L", "L", "L", "L", "L",
                                                    "L", "W", "L", "W", "L", "0");

        List<String> result = chicken.act(visibleEnvironment);

        int chosenDirectionIndex = Integer.parseInt(result.get(result.size() - 1));

        assertEquals(" ", result.get(chosenDirectionIndex),
                "The Chicken should move to the empty space (' ') matching its new direction.");
    }

    @Test
    public void testChickenMovesToSnack_big(){
        Chicken chicken = new Chicken();

        List<String> visibleEnvironment = List.of("W", "W", "W", "L", "W",
                                                    "SM", "L", "S", "L", "L",
                                                    "L", "L", "H", "L", "L",
                                                    "L", "L", "L", "L", "L",
                                                    "L", "W", "L", "W", "L", "0");

        List<String> result = chicken.act(visibleEnvironment);

        int chosenDirectionIndex = Integer.parseInt(result.getLast());

        assertEquals(" ", result.get(chosenDirectionIndex),
                "The Chicken should move to the empty space (' ') matching its new direction.");
    }

    @Test
    public void testChickenChoosesBetweenMultipleSnacks_big(){
        Chicken chicken = new Chicken();

        List<String> visibleEnvironment = List.of("W", "W", "W", "L", "W",
                                                    "SM", "S", "L", "L", "L",
                                                    "L", "L", "H", "S", "L",
                                                    "L", "L", "L", "L", "L",
                                                    "L", "W", "L", "W", "L", "0");

        List<String> result = chicken.act(visibleEnvironment);

        int chosenDirectionIndex = Integer.parseInt(result.get(result.size() - 1));

        assertEquals(" ", result.get(chosenDirectionIndex),
                "The Chicken should move to the empty space (' ') matching its new direction.");
    }

    @Test
    public void testChickenInComplexEnvironment_big(){
        Chicken chicken = new Chicken();

        List<String> visibleEnvironment = List.of("W", "W", "W", "L", "W",
                                                    "SM", "G", "L", "S", "L",
                                                    "L", "W", "H", "S", "L",
                                                    "L", "G", "S", "W", "L",
                                                    "L", "W", "L", "W", "L", "0");

        List<String> result = chicken.act(visibleEnvironment);

        int chosenDirectionIndex = Integer.parseInt(result.get(result.size() - 1));

        assertEquals(" ", result.get(chosenDirectionIndex),
                "The Chicken should move to the empty space (' ') matching its new direction.");
    }
    
    @Test
    public void testChickenWithRandomSelection_big(){
        Chicken chicken = new Chicken();

        List<String> visibleEnvironment = List.of("W", "W", "W", "L", "W",
                                                    "SM", "L", "L", "L", "L",
                                                    "L", "L", "H", "L", "L",
                                                    "L", "L", "L", "L", "L",
                                                    "L", "W", "L", "W", "L", "0");

        List<String> result = chicken.act(visibleEnvironment);

        int chosenDirectionIndex = Integer.parseInt(result.get(result.size() - 1));

        assertEquals(" ", result.get(chosenDirectionIndex),
                "The Chicken should move to the empty space (' ') matching its new direction.");
    }
}
