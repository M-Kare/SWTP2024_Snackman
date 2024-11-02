package de.hsrm.mi.web.springvuebeispiel;


import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@RestController
public class BeispielController {
    Logger logger = LoggerFactory.getLogger(BeispielController.class);
    
    @GetMapping("/api/getinfo")
    public BeispielDTO getBeispiel(HttpServletRequest request) {
        var remoteip = request.getRemoteAddr();
        var zeitstempel = new Date().toString();

        logger.info("=== von {} um {} ===", remoteip, zeitstempel);
        var hn = request.getHeaderNames();
        while (hn.hasMoreElements()) {
            var hname = hn.nextElement();
            var hvalue = request.getHeader(hname);
            logger.info("{} - {}:{}", remoteip, hname, hvalue);
        }

        return new BeispielDTO(remoteip, zeitstempel);
    }
}
