package com.example.blog.tag;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/tags")
public class TagController {
    private final TagService tagService;
    private final TagModelAssembler tagModelAssembler;
    private final PagedResourcesAssembler<Tag> pagedResourcesAssembler;

    public TagController(TagService tagService, TagModelAssembler tagModelAssembler, PagedResourcesAssembler<Tag> pagedResourcesAssembler) {
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

    @GetMapping("/post/{id}")
    public ResponseEntity<PagedModel<TagModel>> getTagsForPost(@PathVariable("id") Long postId,
                                                          @PageableDefault(size = 5) Pageable pageable) {
        Page<Tag> tagPage = tagService.getTagsForPostAsPage(postId, pageable);

        if (tagPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(PagedModel.empty());
        }

        return ResponseEntity.ok(pagedResourcesAssembler.toModel(tagPage, tagModelAssembler));
    }

    @GetMapping("{id}")
    public ResponseEntity<TagModel> get(@PathVariable("id") Long tagId) {
        Tag tag = tagService.getTagById(tagId);

        return ResponseEntity.ok(tagModelAssembler.toModel(tag));
    }

    @PostMapping
    public ResponseEntity<TagModel> save(@Valid @RequestBody TagRequest request) {
        Tag tag = tagService.save(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(tagModelAssembler.toModel(tag));
    }

    @PutMapping("{id}")
    public ResponseEntity<TagModel> update(@PathVariable("id") Long tagId, @Valid @RequestBody TagRequest request) {
        Tag tag = tagService.update(tagId, request);

        return ResponseEntity.ok(tagModelAssembler.toModel(tag));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> delete(@PathVariable("id") Long tagId) {
        tagService.delete(tagId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
