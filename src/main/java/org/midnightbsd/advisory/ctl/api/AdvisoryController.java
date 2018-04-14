package org.midnightbsd.advisory.ctl.api;

import org.midnightbsd.advisory.model.Advisory;
import org.midnightbsd.advisory.services.AdvisoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private final AdvisoryService advisoryService;

    @Autowired
    public AdvisoryController(final AdvisoryService advisoryService) {
        this.advisoryService = advisoryService;
    }

    @GetMapping
    public ResponseEntity<Page<Advisory>> list(Pageable page) {
        return ResponseEntity.ok(advisoryService.get(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Advisory> get(@PathVariable("id") int id) {
        return ResponseEntity.ok(advisoryService.get(id));
    }

    @GetMapping("/cve/{cveId}")
    public ResponseEntity<Advisory> get(@PathVariable("cveId") String cveId) {
        return ResponseEntity.ok(advisoryService.getByCveId(cveId));
    }

    @GetMapping("product/{name}")
    public ResponseEntity<List<Advisory>> getbyProduct(@PathVariable("name") String name) {
        return ResponseEntity.ok(advisoryService.getByProduct(name));
    }

    @GetMapping("vendor/{name}")
    public ResponseEntity<List<Advisory>> getbyVendor(@PathVariable("name") String name) {
        return ResponseEntity.ok(advisoryService.getByVendor(name));
    }

    @GetMapping("vendor/{name}/product/{product}")
    public ResponseEntity<List<Advisory>> getbyProduct(@PathVariable("name") String name, @PathVariable("product") String product) {
        return ResponseEntity.ok(advisoryService.getByVendorAndProduct(name, product));
    }
}
