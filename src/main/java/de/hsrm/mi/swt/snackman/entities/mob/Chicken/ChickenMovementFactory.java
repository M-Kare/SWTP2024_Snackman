package de.hsrm.mi.swt.snackman.entities.mob.Chicken;

import org.python.core.Py;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;
import org.springframework.beans.factory.FactoryBean;

public class ChickenMovementFactory implements FactoryBean<ChickenMovementService> {

      @Override
      public ChickenMovementService getObject() throws Exception {

            // The python classpath is usually set by environment variable
            // or included in the java project classpath but it can also be set
            // programmatically. Here I hard code it just for the example.
            // This is not required if we use jython standalone JAR
            PySystemState systemState = Py.getSystemState();
            systemState.path.append(new PyString("C:\\jython2.7.1\\Lib"));

            // Here is the actual code that interprets our python file.
            PythonInterpreter interpreter = new PythonInterpreter();
            interpreter.execfile("ChickenMovementSkript.py");
            PyObject buildingObject = interpreter.get("ChickenMovementSkript").__call__();

            // Cast the created object to our Java interface
            return (ChickenMovementService) buildingObject.__tojava__(ChickenMovementService.class);
      }

      @Override
      public Class<?> getObjectType() {
            return ChickenMovementService.class;
      }

}