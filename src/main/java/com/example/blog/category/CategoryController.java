package com.example.blog.category;

import com.example.blog.entity.Category;
import com.example.blog.entity.Post;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryModelAssembler categoryModelAssembler;
    private final PagedResourcesAssembler<Category> pagedResourcesAssembler;

    public CategoryController(CategoryService categoryService,
                              CategoryModelAssembler categoryModelAssembler,
                              PagedResourcesAssembler<Category> pagedResourcesAssembler) {
        this.categoryService = categoryService;
        this.categoryModelAssembler = categoryModelAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @GetMapping
    public ResponseEntity<PagedModel<CategoryResponse>> getCategoriesAsPage(@PageableDefault(size = 5) Pageable pageable) {
        Page<Category> categoryPage = categoryService.getCategoriesAsPage(pageable);

        if (categoryPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(PagedModel.empty());
        }

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(categoryPage, categoryModelAssembler));
    }

    @GetMapping("{id}")
    public ResponseEntity<CategoryResponse> get(@PathVariable("id") Long categoryId) {
        Category category = categoryService.get(categoryId);

        return ResponseEntity.ok(categoryModelAssembler.toModel(category));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> save(@Valid @RequestBody CategoryRequest categoryRequest) {
        Category category = categoryService.save(categoryRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(categoryModelAssembler.toModel(category));
    }

    @PutMapping("{id}")
    public ResponseEntity<CategoryResponse> update(@PathVariable("id") Long categoryId,
                                                   @Valid @RequestBody CategoryRequest categoryRequest) {
        Category category = categoryService.update(categoryId, categoryRequest);

        return ResponseEntity.ok(categoryModelAssembler.toModel(category));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long categoryId) {
        categoryService.delete(categoryId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }
}