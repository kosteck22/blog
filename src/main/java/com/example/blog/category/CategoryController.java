package com.example.blog.category;

import jakarta.validation.Valid;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<PagedModel<CategoryModel>> getCategoriesAsPage(@PageableDefault(size = 10) Pageable pageable) {
        Page<Category> categoryPage = categoryService.getCategoriesAsPage(pageable);

        if (categoryPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(PagedModel.empty());
        }

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(categoryPage, categoryModelAssembler));
    }

    @GetMapping("{id}")
    public ResponseEntity<CategoryModel> get(@PathVariable("id") Long categoryId) {
        Category category = categoryService.get(categoryId);

        return ResponseEntity.ok(categoryModelAssembler.toModel(category));
    }

    @PostMapping
    public ResponseEntity<CategoryModel> save(@Valid @RequestBody CategoryRequest categoryRequest) {
        Category category = categoryService.save(categoryRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(categoryModelAssembler.toModel(category));
    }

    @PutMapping("{id}")
    public ResponseEntity<CategoryModel> update(@PathVariable("id") Long categoryId,
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
