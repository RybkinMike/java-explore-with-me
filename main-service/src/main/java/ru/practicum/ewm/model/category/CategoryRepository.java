package ru.practicum.ewm.model.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("select MAX(id) " +
            "from Category as c ")
    Integer findMaxId();

    @Query("select id " +
            "from Category as c ")
    List<Integer> findAllId();

    Optional<Category> findByName(String name);
}
