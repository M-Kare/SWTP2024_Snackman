package de.hsrm.mi.swt.snackman.controller.Snack;

import de.hsrm.mi.swt.snackman.services.Snack.SnackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SnackController {

    @Autowired
    private SnackService snackService;

    Logger log = LoggerFactory.getLogger(SnackController.class);

    @GetMapping("/snack")
    public List<SnackDTO> getSnacks() {
        log.info("Get Snacks");
        var snacks = snackService.getSnacks();

        List<SnackDTO> snackDTOS = snacks
                .stream()
                .map(SnackDTO::fromSnack)
                .collect(Collectors.toList());

        return snackDTOS;
    }
}
