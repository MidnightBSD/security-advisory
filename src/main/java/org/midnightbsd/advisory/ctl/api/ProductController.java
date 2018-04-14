package org.midnightbsd.advisory.ctl.api;

import org.midnightbsd.advisory.model.Product;
import org.midnightbsd.advisory.repository.ProductRepository;
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
@RequestMapping("/api/product")
public class ProductController {
    
    private final ProductRepository productRepository;

    @Autowired
    public ProductController(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public ResponseEntity<Page<Product>> list(Pageable page) {
        return ResponseEntity.ok(productRepository.findAll(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> get(@PathVariable("id") int id) {
        return ResponseEntity.ok(productRepository.findOne(id));
    }

    @GetMapping("/name/{name}/version/{version}")
    public ResponseEntity<Product> get(@PathVariable("name") String name, @PathVariable("version") String version) {
        return ResponseEntity.ok(productRepository.findByNameAndVersion(name, version));
    }
}
