package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;

import java.util.HashMap;

@Repository
public class UserRepositoryInMemory implements UserRepository {
    HashMap<Long, User> user = new HashMap<>();
}
