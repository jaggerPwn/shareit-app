package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validation.CreationValidation;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private int id;
    @NotBlank(groups = CreationValidation.class, message = "Item name cannot be null")
    private String name;
    @NotBlank(groups = CreationValidation.class, message = "Item description cannot be null")
    private String description;
    @NotNull(groups = CreationValidation.class, message = "Available cannot be null")
    private Boolean available;
    private List<CommentDto> comments;
    private Integer requestId;
}
