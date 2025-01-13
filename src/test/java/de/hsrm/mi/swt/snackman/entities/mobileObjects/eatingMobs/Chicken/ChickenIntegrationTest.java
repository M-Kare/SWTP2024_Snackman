package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.python.util.PythonInterpreter;
import org.springframework.util.FileSystemUtils;

import de.hsrm.mi.swt.snackman.SnackmanApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


/**
 * Integration tests for Chicken movement logic with Jython and Java integration.
 * This class ensures that the 'Chicken' Java class correctly interacts with
 * the 'ChickenMovementSkript.py' Python logic.
 */
public class ChickenIntegrationTest {

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

    /**
     * Verifies that the Chicken can interact with the Python script directly,
     * using a Jython interpreter and chooses the correct empty square (" ").
     */
    @Test
    void testChickenMovement() {
        try (PythonInterpreter pyInterp = new PythonInterpreter()) {
            pyInterp.exec("import sys");
            pyInterp.exec("sys.path.append('./extensions/chicken/')");

            String mapAroundChicken = "'W', 'W', 'W', 'L', 'W', " +
                    "'SM', 'L', 'W', 'L', 'L'," +
                    "'L', 'L', 'H', 'W', 'L'," +
                    "'L', 'L', 'W', 'L', 'L', " +
                    "'L', 'W', 'L', 'W', 'L', '0'";
            pyInterp.exec("from ChickenMovementSkript import choose_next_square");
            pyInterp.exec("result = choose_next_square([" + mapAroundChicken + "])");

            String result = pyInterp.get("result").toString();

            String expectedResult = "['X', 'X', 'X', ' ', 3]";
            assertEquals(expectedResult, result,
                    "The Python script should correctly determine the next move (' ').");
        }
    }
}