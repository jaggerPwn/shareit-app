package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Comment;

import java.util.List;
import java.util.stream.Collectors;

public class CommentMapper {

    public static CommentDto commentToCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .authorName(comment.getUser().getName())
                .build();
    }

    public static Comment commentDtoToComment(CommentDto comment) {
        return Comment.builder()
                .id(comment.getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .build();
    }

    public static List<CommentDto> listCommentToCommentDto(List<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::commentToCommentDto)
                .collect(Collectors.toList());
    }
}
