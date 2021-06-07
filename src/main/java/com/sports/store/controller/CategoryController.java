package com.sports.store.controller;

import com.sports.store.models.Category;
import com.sports.store.payload.response.MessageResponse;
import com.sports.store.repository.CategoryRepository;
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
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @PostMapping("/add")
    public ResponseEntity<?> addCategory(@RequestBody Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Category is already taken!"));
        }
        categoryRepository.save(category);
        return ResponseEntity.ok(new MessageResponse("Category registered successfully!"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable("id") long id, @RequestBody Category category) {
        Optional<Category> catData = categoryRepository.findById(id);
        if (catData.isPresent()) {
            Category _cat = catData.get();
            _cat.setName(category.getName());
            _cat.setDescription(category.getDescription());
            return new ResponseEntity<>(categoryRepository.save(_cat), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long catId) {
        if (!categoryRepository.existsById(catId)) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: Category is not exists!"));
        }
        try {
            categoryRepository.deleteById(catId);
            return ResponseEntity.ok(new MessageResponse("Category deleted successfully!"));
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> getAllCategories(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        try {
            List<Category> categories = new ArrayList<Category>();
            Pageable paging = PageRequest.of(page, size);

            Page<Category> pageCats;
            if (name == null)
                pageCats = categoryRepository.findAll(paging);
            else
                pageCats = categoryRepository.findByNameContaining(name, paging);

            categories = pageCats.getContent();

            Map<String, Object> response = new HashMap<>();
            response.put("categories", categories);
            response.put("currentPage", pageCats.getNumber());
            response.put("totalItems", pageCats.getTotalElements());
            response.put("totalPages", pageCats.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable("id") long id) {
        Optional<Category> catData = categoryRepository.findById(id);

        if (catData.isPresent()) {
            return new ResponseEntity<>(catData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/getAllCategories")
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
}
