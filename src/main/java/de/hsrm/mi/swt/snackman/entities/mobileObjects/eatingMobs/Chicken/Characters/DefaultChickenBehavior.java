package de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken.Characters;

import java.util.ArrayList;
import java.util.List;

import java.util.Properties;

import org.python.util.PythonInterpreter;
import org.slf4j.LoggerFactory;

import org.python.core.PyList;
import org.python.core.PyObject;
import org.slf4j.Logger;

public class DefaultChickenBehavior implements Behavior{

    private PythonInterpreter pythonInterpreter = null;
    private Properties pythonProps = new Properties();
    private final Logger logger = LoggerFactory.getLogger(DefaultChickenBehavior.class);


    @Override
    public List<String> execute(List<String> squares) {
        initJython();
        return executeMovementSkript(squares);
       }

    public void initJython() {
        pythonProps.setProperty("python.path", "src/main/java/de/hsrm/mi/swt/snackman/entities/mobileObjects/eatingMobs/Chicken/Characters/MovementSkripts");
        PythonInterpreter.initialize(System.getProperties(), pythonProps, new String[0]);
        this.pythonInterpreter = new PythonInterpreter();
        logger.info("Initialised jython for chicken movement");
    }

    /**
     * Executes the chicken's movement script written in Python and determines the
     * next move.
     *
     * @param squares a list of squares visible from the chicken's current position.
     * @return a list of moves resulting from the Python script's execution.
     */
    public List<String> executeMovementSkript(List<String> squares) {
        try {
            logger.debug("Running python chicken script with: {}", squares.toString());
            pythonInterpreter.exec("from ChickenMovementSkript import choose_next_square");
            PyObject func = pythonInterpreter.get("choose_next_square");
            PyObject result = func.__call__(new PyList(squares));

            if (result instanceof PyList) {
                PyList pyList = (PyList) result;
                logger.debug("Python chicken script return: {}", pyList);
                return convertPythonList(pyList);
            }

            throw new Exception("Python chicken script did not load.");
        } catch (Exception ex) {
            logger.error("Error while executing chicken python script: ", ex);
            ex.printStackTrace();
        }
        return squares;
    }

    /**
     * Converts a Python list to a Java list.
     *
     * @param pyList the Python list to convert.
     * @return the corresponding Java list.
     */
    private List<String> convertPythonList(PyList pyList) {
        List<String> javaList = new ArrayList<>();
        for (Object item : pyList) {
            javaList.add(item.toString());
        }
        logger.debug("Python script result is {}", javaList);
        return javaList;
    }
    
}
