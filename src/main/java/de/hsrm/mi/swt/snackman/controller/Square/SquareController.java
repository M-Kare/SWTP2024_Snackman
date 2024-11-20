package de.hsrm.mi.swt.snackman.controller.Square;

import de.hsrm.mi.swt.snackman.entities.Square.Square;
import de.hsrm.mi.swt.snackman.services.Square.SquareService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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


}
