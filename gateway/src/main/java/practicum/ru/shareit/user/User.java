package practicum.ru.shareit.user;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor
public class User {
    private Long id;
    private String name;
    private String email;
}
