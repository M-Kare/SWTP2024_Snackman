// package de.hsrm.mi.swt.snackman.entities.mob.Chicken.Characters;

// import static org.junit.jupiter.api.Assertions.assertEquals;

// import java.util.List;

// import org.junit.jupiter.api.Test;

// import de.hsrm.mi.swt.snackman.entities.mobileObjects.eatingMobs.Chicken.Chicken;

// public class TalaChickenTest {

//     @Test
//     public void talaChickenMovementTest(){
//         Chicken chicken = new Chicken("TalaChickenMovementSkript");

//         List<String> visibleEnvironment = List.of("W", "W", "W", "L", "W", "L", "W", "L", "0");
//         List<String> result = chicken.act(visibleEnvironment);

//         int chosenDirectionIndex = Integer.parseInt(result.get(result.size() - 1));

//         assertEquals(" ", result.get(chosenDirectionIndex),
//                 "The Chicken should move to the empty space (' ') matching its new direction.");
//     }

//     @Test
//     public void talaChickenMovementTest2(){
//         Chicken chicken = new Chicken("TalaChickenMovementSkript_new");

//         List<String> visibleEnvironment = List.of("W", "W", "W", "L", "W",
//                                                     "SM", "L", "L", "L", "L",
//                                                     "L", "W", "H", "W", "L",
//                                                     "L", "W", "L", "W", "L",
//                                                     "L", "W", "L", "W", "L");

//         List<String> result = chicken.act(visibleEnvironment);

//         //int chosenDirectionIndex = Integer.parseInt(result.get(12));
//         int chosenDirectionIndex = Integer.parseInt(result.get(result.size() - 1));

//         assertEquals(" ", result.get(chosenDirectionIndex),
//                 "The Chicken should move to the empty space (' ') matching its new direction.");
//     }

//     @Test
//     public void testTalaCkickenFollowsSnackmanWithBigDistance(){
//         Chicken chicken = new Chicken("TalaChickenMovementSkript");

//         List<String> visibleEnvironment = List.of("W", "W", "W", "W", "L", "W", "W", "W", "W",
//                                                     "W", "L", "L", "L", "W", "L", "W", "L", "W",
//                                                     "W", "W", "L", "W", "W", "L", "L", "L", "W",
//                                                     "L", "L", "L", "L", "W", "L", "W", "W", "W",
//                                                     "W", "L", "W", "SM", "H", "L", "W", "L", "W", //H is Chicken
//                                                     "W", "L", "W", "L", "W", "L", "W", "L", "L",
//                                                     "W", "L", "W", "L", "W", "W", "W", "L", "W",
//                                                     "W", "W", "W", "L", "L", "W", "W", "W", "W",
//                                                     "W", "W", "W", "W", "L", "W", "W", "W", "W");

//         List<String> result = chicken.act(visibleEnvironment);

//         boolean istFreierIndexVorhanden = result.contains(" ");
//         assertEquals(true, istFreierIndexVorhanden, "Empty element, but Snackman is not reachable : "+ result);

//     }

//     @Test
//     public void testTalaChickenFollowsSnackmanButCannotReach(){
//     //weil z.B. Wäde im Weg sind
//         Chicken chicken = new Chicken("TalaChickenMovementSkript");

//         List<String> visibleEnvironment = List.of("W", "W", "W", "W", "W", "W", "W", "W", "W",
//                                                 "W", "SM", "W", "L", "W", "L", "L", "L", "W",
//                                                 "W", "W", "W", "L", "W", "L", "W", "L", "W",
//                                                 "W", "L", "W", "L", "W", "L", "W", "W", "W",
//                                                 "W", "L", "W", "L", "H", "L", "L", "L", "L",
//                                                 "W", "L", "W", "L", "W", "L", "W", "L", "L",
//                                                 "W", "L", "W", "L", "W", "L", "W", "L", "W",
//                                                 "W", "L", "W", "L", "L", "L", "W", "L", "W",
//                                                 "W", "L", "W", "L", "W", "W", "W", "L", "W");

//         List<String> result = chicken.act(visibleEnvironment);
//         boolean istFreierIndexVorhanden = result.contains(" ");
//         assertEquals(true, istFreierIndexVorhanden, "Empty element, but Snackman is not reachable : "+ result);

//     }
    
// }
