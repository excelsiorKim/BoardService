package com.example.boardservice.dto;

import com.example.boardservice.domain.Article;
import com.example.boardservice.domain.ArticleComment;
import com.example.boardservice.domain.UserAccount;

import java.time.LocalDateTime;

public record ArticleCommentDto(
        Long id,
        Long articleId,
        UserAccountDto userAccountDto,
        Long parentCommentId,
        String content,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
    public static ArticleCommentDto of(Long articleId, UserAccountDto userAccountDto, String contents) {
        return ArticleCommentDto.of(articleId, userAccountDto, null, contents);
    }

    public static ArticleCommentDto of(Long articleId, UserAccountDto userAccountDto, Long parentCommentId, String contents) {
        return ArticleCommentDto.of(null, articleId, userAccountDto, parentCommentId, contents, null, null, null, null);
    }

    public static ArticleCommentDto of(Long id, Long articleId, UserAccountDto userAccountDto, Long parentCommentId,String content, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleCommentDto(id, articleId, userAccountDto,parentCommentId, content, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static ArticleCommentDto from(ArticleComment entity) {
        return new ArticleCommentDto(
                entity.getId(),
                entity.getArticle().getId(),
                UserAccountDto.from(entity.getUserAccount()),
                entity.getParentCommentId(),
                entity.getContent(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    public ArticleComment toEntity(Article entity, UserAccount userAccount) {
        return ArticleComment.of(
                entity,
                userAccount,
                content
        );
    }

}