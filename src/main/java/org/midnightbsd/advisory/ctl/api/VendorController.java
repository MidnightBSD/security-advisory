package org.midnightbsd.advisory.ctl.api;

import org.midnightbsd.advisory.model.Vendor;
import org.midnightbsd.advisory.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lucas Holt
 */
@RestController
@RequestMapping("/api/vendor")
public class VendorController {

    private final VendorRepository vendorRepository;

    @Autowired
    public VendorController(final VendorRepository vendorRepository) {
        this.vendorRepository = vendorRepository;
    }

    @GetMapping
    public ResponseEntity<Page<Vendor>> list(Pageable page) {
        return ResponseEntity.ok(vendorRepository.findAll(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vendor> get(@PathVariable("id") int id) {
        return ResponseEntity.ok(vendorRepository.findOne(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Vendor> get(@PathVariable("name") String name) {
        return ResponseEntity.ok(vendorRepository.findOneByName(name));
    }

}
