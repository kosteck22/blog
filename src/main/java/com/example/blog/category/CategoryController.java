package com.example.blog.category;

import com.example.blog.comment.Comment;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<?> getCategoriesAsPage(@PageableDefault(size = 10) Pageable pageable) {
        Page<Category> categoryPage = categoryService.getCategoriesAsPage(pageable);

        if (categoryPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(PagedModel.empty());
        }

        return ResponseEntity.ok(categoryPage);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> get(@PathVariable("id") Long categoryId) {
        Category category = categoryService.get(categoryId);

        return ResponseEntity.ok(category);
    }

    @PostMapping
    public ResponseEntity<Category> save(@Valid @RequestBody CategoryRequest categoryRequest) {
        Category category = categoryService.save(categoryRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(category);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long categoryId) {
        categoryService.delete(categoryId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}
