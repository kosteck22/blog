package com.example.blog.category;

import com.example.blog.comment.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
