package com.example.blog.tag;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/tags")
public class TagController {
    private final TagService tagService;
    private final TagModelAssembler tagModelAssembler;
    private final PagedResourcesAssembler<Tag> pagedResourcesAssembler;

    public TagController(TagService tagService, TagModelAssembler tagModelAssembler, PagedResourcesAssembler pagedResourcesAssembler) {
        this.tagService = tagService;
        this.tagModelAssembler = tagModelAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @GetMapping
    public ResponseEntity<PagedModel<TagModel>> getTagsAsPage(@PageableDefault(size = 5) Pageable pageable) {
        Page<Tag> tagPage = tagService.getTagsAsPage(pageable);

        if (tagPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(PagedModel.empty());
        }

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(tagPage, tagModelAssembler));
    }
}
