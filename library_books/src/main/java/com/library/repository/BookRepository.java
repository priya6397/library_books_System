package com.library.repository;

import com.library.model.Book;
import com.library.model.City;
import com.library.model.Library;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {
    boolean existsByTitle(String title);
    List<Book> findAllByOrderByTitleAsc();
    List<Book> findByLibraryCityId(Long cityId);
    List<Book> findByLibraryId(Long libraryId);
    List<Book> findByLibraryCityIdAndLibraryId(Long cityId, Long libraryId);
    boolean existsByLibrary(Library library);
}
