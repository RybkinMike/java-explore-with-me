package ru.practicum.ewm.model.compilaation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.compilaation.Compilation;

import java.util.List;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    List<Compilation> findCompilationByPinned(boolean pinned, Pageable page);

    Page<Compilation> findByPinned(boolean pinned, Pageable page);
}
