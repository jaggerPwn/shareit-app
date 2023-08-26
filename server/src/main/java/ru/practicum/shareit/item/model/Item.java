package ru.practicum.shareit.item.model;

import lombok.*;
import org.springframework.lang.Nullable;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

@Data
@Builder
@Entity
@Table(name = "items", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String description;
    private Boolean available;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private User user;
    @Nullable
    private Integer requestId;
}