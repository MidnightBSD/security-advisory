package org.midnightbsd.advisory.ctl.api;

import org.midnightbsd.advisory.model.Advisory;
import org.midnightbsd.advisory.repository.AdvisoryRepository;
import org.midnightbsd.advisory.repository.ConfigNodeCpeRepository;
import org.midnightbsd.advisory.services.AdvisoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import us.springett.parsers.cpe.Cpe;
import us.springett.parsers.cpe.CpeParser;
import us.springett.parsers.cpe.exceptions.CpeParsingException;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/cpe")
public class CpeController {

    @Autowired
    private AdvisoryService advisoryService;

    // cpe:2.3:a:eric_allman:sendmail:5.58:*:*:*:*:*:*:*
    @GetMapping("partial-match")
    public ResponseEntity<List<Advisory>> partialMatch(@RequestParam(name="cpe") String cpe,
            @RequestParam(required = false, name = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date startDate,
            @RequestParam(required = false, name="includeVersion", defaultValue = "false") Boolean includeVersion) throws CpeParsingException {
        Cpe parsed = CpeParser.parse(cpe);

        if (includeVersion == null || !includeVersion) {
            return ResponseEntity.ok(advisoryService.getByVendorAndProduct(parsed.getVendor(), parsed.getProduct(), startDate));
        }

        return ResponseEntity.ok(advisoryService.getByVendorAndProductAndVersion(parsed.getVendor(), parsed.getProduct(), parsed.getVersion(), startDate));
    }
}
