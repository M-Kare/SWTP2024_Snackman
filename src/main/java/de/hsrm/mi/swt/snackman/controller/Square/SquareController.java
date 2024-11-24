package de.hsrm.mi.swt.snackman.controller.Square;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.hsrm.mi.swt.snackman.controller.Snack.SnackDTO;
import de.hsrm.mi.swt.snackman.entities.Snack.Snack;
import de.hsrm.mi.swt.snackman.services.Square.SquareService;

@RestController
@RequestMapping("/api")
public class SquareController {

    @Autowired
    private SquareService squareService;

    Logger log = LoggerFactory.getLogger(SquareController.class);

    @GetMapping("/square")
    public SquareDTO getSquare() {
        log.info("Get Square");
        var square = squareService.getSquare();
        return SquareDTO.fromSquare(square);
    }

    //NEW
    @DeleteMapping("/snack")
    public boolean removeSnack(@RequestBody SnackDTO snackDTO) {
        Snack snack = new Snack(snackDTO.snackType());
        snack.setPosition(snackDTO.position());
        boolean removed = squareService.removeSnack(snack);
        if (removed) {
            log.info("Snack removed successfully");
        } else {
            log.info("Snack not found");
        }
        return removed;
    }
    //NEW
}

