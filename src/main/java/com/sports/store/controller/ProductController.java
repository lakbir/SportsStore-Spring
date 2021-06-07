package com.sports.store.controller;

import com.sports.store.models.Category;
import com.sports.store.models.Product;
import com.sports.store.payload.response.MessageResponse;
import com.sports.store.repository.CategoryRepository;
import com.sports.store.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @PostMapping("/add/{idCategory}")
    public ResponseEntity<?> addProduct(@PathVariable("idCategory") long idCategory,@RequestBody Product product) {
        if (productRepository.existsByName(product.getName())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Product is already taken!"));
        }
        product.setCategory(categoryRepository.getOne(idCategory));
        productRepository.save(product);
        return ResponseEntity.ok(new MessageResponse("Product registered successfully!"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable("id") long id, @RequestBody Product product) {
        Optional<Product> prodData = productRepository.findById(id);
        if (prodData.isPresent()) {
            Product _prd = prodData.get();
            _prd.setName(product.getName());
            _prd.setDescription(product.getDescription());
            _prd.setPrice(product.getPrice());
            _prd.setImage(product.getImage());
            return new ResponseEntity<>(productRepository.save(_prd), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long prdId) {
        if (!productRepository.existsById(prdId)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Product is not exists!"));
        }
        try {
            productRepository.deleteById(prdId);
            return ResponseEntity.ok(new MessageResponse("Product deleted successfully!"));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> getAllProducts(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        try {
            List<Product> products = new ArrayList<Product>();
            Pageable paging = PageRequest.of(page, size);

            Page<Product> pagePrds;
            if (name == null)
                pagePrds = productRepository.findAll(paging);
            else
                pagePrds = productRepository.findByNameContainingOrDescriptionContaining(name, name, paging);

            products = pagePrds.getContent();

            Map<String, Object> response = new HashMap<>();
            response.put("products", products);
            response.put("currentPage", pagePrds.getNumber());
            response.put("totalItems", pagePrds.getTotalElements());
            response.put("totalPages", pagePrds.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/searchByCategory")
    public ResponseEntity<Map<String, Object>> getAllProductsByCategory(
            @RequestParam(required = false) Long idCategory,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size
    ) {

        try {
            List<Product> products = new ArrayList<Product>();
            Pageable paging = PageRequest.of(page, size);

            Page<Product> pagePrds;
            if (idCategory == null)
                pagePrds = productRepository.findAll(paging);
            else
                pagePrds = productRepository.findByCategoryId(idCategory, paging);

            products = pagePrds.getContent();

            Map<String, Object> response = new HashMap<>();
            response.put("products", products);
            response.put("currentPage", pagePrds.getNumber());
            response.put("totalItems", pagePrds.getTotalElements());
            response.put("totalPages", pagePrds.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getTagById(@PathVariable("id") long id) {
        Optional<Product> prdData = productRepository.findById(id);

        if (prdData.isPresent()) {
            return new ResponseEntity<>(prdData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
