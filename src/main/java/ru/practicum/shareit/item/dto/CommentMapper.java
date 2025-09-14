package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.Comment;

import java.time.LocalDateTime;

public class CommentMapper {
    public static CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setItemId(comment.getItemId());
        commentDto.setAuthorId(comment.getAuthorId());
        commentDto.setAuthorName(comment.getAuthorName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }

    public static Comment toComment(CommentDto commentDto, Long userId, String userName, Long itemId,
                                    LocalDateTime now) {
        Comment comment = new Comment();
        comment.setAuthorId(userId);
        comment.setAuthorName(userName);
        comment.setItemId(itemId);
        comment.setCreated(now);
        comment.setId(commentDto.getId());
        comment.setText(commentDto.getText());
        return comment;
    }
}
