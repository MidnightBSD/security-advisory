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

import java.util.Optional;

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
    public ResponseEntity<Page<Product>> list(final Pageable page) {
        return ResponseEntity.ok(productRepository.findAll(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> get(@PathVariable("id") final int id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent())
            return ResponseEntity.ok(product.get());
        else
            return ResponseEntity.notFound().build();
    }

    @GetMapping("/name/{name}/version/{version}")
    public ResponseEntity<Product> get(@PathVariable("name") final String name, @PathVariable("version") final String version) {
        return ResponseEntity.ok(productRepository.findByNameAndVersion(name, version));
    }
}
