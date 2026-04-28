package org.midnightbsd.advisory.ctl.api;

import java.util.Date;
import java.util.List;
import org.midnightbsd.advisory.dto.AdvisoryDto;
import org.midnightbsd.advisory.services.AdvisoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import us.springett.parsers.cpe.Cpe;
import us.springett.parsers.cpe.CpeParser;
import us.springett.parsers.cpe.exceptions.CpeParsingException;

@RestController
@RequestMapping("/api/cpe")
public class CpeController {

  @Autowired private AdvisoryService advisoryService;

  // cpe:2.3:a:apache:mod_dav_svn:1.14.2:::::midnightbsd2:x64
  // cpe:2.3:a:eric_allman:sendmail:5.58:*:*:*:*:*:*:*
  @GetMapping("partial-match")
  public ResponseEntity<List<AdvisoryDto>> partialMatch(
      @RequestParam(name = "cpe") String cpe,
      @RequestParam(required = false, name = "startDate")
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          Date startDate,
      @RequestParam(required = false, name = "includeVersion", defaultValue = "false")
          Boolean includeVersion)
      throws CpeParsingException {
    Cpe parsed = parse(cpe);

    if (includeVersion == null || !includeVersion) {
      return ResponseEntity.ok(
          advisoryService.getByVendorAndProduct(parsed.getVendor(), parsed.getProduct(), startDate)
              .stream()
              .map(AdvisoryDto::from)
              .toList());
    }

    return ResponseEntity.ok(
        advisoryService
            .getByVendorAndProductAndVersion(
                parsed.getVendor(), parsed.getProduct(), parsed.getVersion(), startDate)
            .stream()
            .map(AdvisoryDto::from)
            .toList());
  }

  Cpe parse(String cpe) throws CpeParsingException {
    String localCpe = cpe;
    // mports didn't include the trailing other field at the end of the identifier.
    // this wouldn't parse properly in this tool but did work in the NVD search tool.
    if (cpe.startsWith("cpe:2.3") && (cpe.endsWith("x64") || cpe.endsWith("x86"))) {
      localCpe = cpe + ":0";
    }
    return CpeParser.parse(localCpe);
  }
}
