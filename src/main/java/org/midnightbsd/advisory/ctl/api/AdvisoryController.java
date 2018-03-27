package org.midnightbsd.advisory.ctl.api;

import org.midnightbsd.advisory.model.Advisory;
import org.midnightbsd.advisory.services.AdvisoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Lucas Holt
 */
@RestController
@RequestMapping("/api/advisory")
public class AdvisoryController {

    @Autowired
    private AdvisoryService advisoryService;

    @GetMapping
    public ResponseEntity<List<Advisory>> list() {
        return ResponseEntity.ok(advisoryService.list());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Advisory> get(@PathVariable("id") int id) {
        return ResponseEntity.ok(advisoryService.get(id));
    }

    @GetMapping("/cve/{cveId}")
    public ResponseEntity<Advisory> get(@PathVariable("cveId") String cveId) {
        return ResponseEntity.ok(advisoryService.getByCveId(cveId));
    }
}
