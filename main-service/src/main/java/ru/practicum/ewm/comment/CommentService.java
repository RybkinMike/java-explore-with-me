package ru.practicum.ewm.comment;

import ru.practicum.ewm.comment.model.ParticipationCommentDto;
import ru.practicum.ewm.comment.model.StatusUpdateComment;

import java.util.List;

public interface CommentService {
    List<ParticipationCommentDto> getAllCommentsForEvent(Long eventId, Integer from, Integer size);

    List<ParticipationCommentDto> getAllCommentsPrivate(Long userId, Integer from, Integer size);

    ParticipationCommentDto saveComment(ParticipationCommentDto participationCommentDto, Long userId, Long eventId);

    ParticipationCommentDto patchCommentPrivate(ParticipationCommentDto participationCommentDto, Long userId, Long commentId);

    ParticipationCommentDto getCommentPrivate(Long userId, Long commentId);

    void deleteCommentPrivate(Long userId, Long commentId);

    List<ParticipationCommentDto> getCommentsAdmin(Integer from, Integer size);

    ParticipationCommentDto patchCommentsAdmin(StatusUpdateComment statusUpdateComment, Long commentId);
}
