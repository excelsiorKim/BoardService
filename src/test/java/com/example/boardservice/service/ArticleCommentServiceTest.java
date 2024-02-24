package com.example.boardservice.service;

import com.example.boardservice.domain.ArticleComment;
import com.example.boardservice.dto.ArticleCommentDto;
import com.example.boardservice.repository.ArticleCommentRepository;
import com.example.boardservice.repository.ArticleRepository;
import com.example.boardservice.repository.UserAccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@DisplayName("비즈니스 로직 - 댓글")
@ExtendWith(MockitoExtension.class)
class ArticleCommentServiceTest {
    @InjectMocks private ArticleCommentService sut;
    @Mock private ArticleCommentRepository articleCommentRepository;
    @Mock private ArticleRepository articleRepository;

    @Mock private UserAccountRepository userAccountRepository;
    private final TestFixture fixture = new TestFixture();

    @DisplayName("게시글 ID로 조회하면, 해당하는 댓글 리스트를 반환한다.")
    @Test
    void givenArticleId_whenSearchingArticleComments_thenReturnsArticleComments(){
        //Given
        Long articleId = 1L;
        ArticleComment expected =fixture.createArticleComment("content");
        given(articleCommentRepository.findByArticle_Id(articleId)).willReturn(List.of(expected));
        //When
        List<ArticleCommentDto> actual =  sut.searchArticleComments(articleId);
        //Then
        assertThat(actual).hasSize(1)
                        .first().hasFieldOrPropertyWithValue("content", expected.getContent());
        then(articleCommentRepository).should().findByArticle_Id(articleId);
    }

    @DisplayName("댓글 정보를 입력하면, 댓글을 저장한다.")
    @Test
    void givenArticleCommentInfo_whenSavingArticleComment_thenReturnsArticleComment(){
        //Given
        ArticleCommentDto dto = fixture.createArticleCommentDto("댓글");
        given(articleRepository.getReferenceById(dto.articleId())).willReturn(fixture.createArticle());
        given(userAccountRepository.getReferenceById(dto.userAccountDto().userId())).willReturn(fixture.createUserAccount());
        given(articleCommentRepository.save(any(ArticleComment.class))).willReturn(null);
        //When
        sut.saveArticleComment(dto);
        //Then
        then(articleRepository).should().getReferenceById(dto.articleId());
        then(userAccountRepository).should().getReferenceById(dto.userAccountDto().userId());
        then(articleCommentRepository).should().save(any(ArticleComment.class));
    }

    @DisplayName("댓글 저장을 시도했는데 맞는 게시글이 없으면, 경고 로그를 찍고 아무것도 안한다")
    @Test
    void givenNonexistentArticle_whenSavingArticleComment_thenLogsWarningAndDoesNothing(){
        //Given
        ArticleCommentDto dto = fixture.createArticleCommentDto("댓글");
        given(articleRepository.getReferenceById(dto.articleId())).willThrow(EntityNotFoundException.class);
        //When
        sut.saveArticleComment(dto);
        //Then
        then(articleRepository).should().getReferenceById(dto.articleId());
        then(userAccountRepository).shouldHaveNoInteractions();
        then(articleCommentRepository).shouldHaveNoMoreInteractions();
    }

    @DisplayName("댓글 정보를 입력하면, 댓글을 수정한다.")
    @Test
    void givenArticleCommentInfo_whenUpdatingArticleComment_thenUpdatesArticleComment(){
        //Given
        String oldContent = "content";
        String updatedContent = "댓글";
        ArticleComment articleComment = fixture.createArticleComment(oldContent);
        ArticleCommentDto dto = fixture.createArticleCommentDto(updatedContent);
        given(articleCommentRepository.getReferenceById(dto.id())).willReturn(articleComment);
        //When
        sut.updateArticleComment(dto);
        //Then
        assertThat(articleComment.getContent())
                .isNotEqualTo(oldContent)
                .isEqualTo(updatedContent);
        then(articleCommentRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("없는 댓글 정보를 수정하려고 하면, 경고 로그를 찍고 아무것도 안한다")
    @Test
    void givenNonexistentArticleComment_whenUpdatingArticleComment_thenLogsWarningAndDoesNothing(){
        //Given
        ArticleCommentDto dto = fixture.createArticleCommentDto("댓글");
        given(articleCommentRepository.getReferenceById(dto.id())).willThrow(EntityNotFoundException.class);
        //When
        sut.updateArticleComment(dto);
        //Then
        then(articleCommentRepository).should().getReferenceById(dto.id());
    }

    @DisplayName("댓글 ID를 입력하면, 댓글을 삭제한다")
    @Test
    void givenArticleCommentId_whenDeletingArticleComment_thenDeletesArticleComment(){
        //Given
        Long articleCommentId = 1L;
        String userId = "inderby";
        willDoNothing().given(articleCommentRepository).deleteByIdAndUserAccount_UserId(articleCommentId, userId);
        //When
        sut.deleteArticleComment(articleCommentId,userId);
        //Then
        then(articleCommentRepository).should().deleteByIdAndUserAccount_UserId(articleCommentId, userId);
    }
}