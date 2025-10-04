package practicum.ru.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwner(Long userId);

    @Query("select i from Item i where (upper(i.description) like upper(concat('%', ?1, '%')) " +
            "or upper(i.name) like upper(concat('%', ?1, '%'))) and i.available")
    List<Item> search(String query);

    List<Item> findByRequestIdIn(List<Long> requests);
}
