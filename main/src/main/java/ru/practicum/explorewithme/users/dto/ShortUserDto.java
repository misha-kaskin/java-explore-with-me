package ru.practicum.explorewithme.users.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShortUserDto {
    private Long id;
    private String name;
}
