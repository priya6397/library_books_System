package com.library.impl;

import com.library.exception.BookIssuedException;
import com.library.exception.DuplicateEntryException;
import com.library.exception.ResourceNotFoundException;
import com.library.model.Book;
import com.library.model.Library;
import com.library.payload.request.BookRequest;
import com.library.payload.response.BookResponse;
import com.library.repository.BookRepository;
import com.library.repository.IssuedBookRepository;
import com.library.repository.LibraryRepository;
import com.library.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private LibraryRepository libraryRepository;
    @Autowired
    private IssuedBookRepository issuedBookRepository;
    @Override
    @Transactional
    public BookResponse createBook(BookRequest bookRequest) {
        Book book = new Book();
        book.setTitle(bookRequest.getTitle());
        book.setAuthorName(bookRequest.getAuthorName());
        book.setBookType(bookRequest.getBookType());
        book.setPublisherName(bookRequest.getPublisherName());
        book.setPublisherEmail(bookRequest.getPublisherEmail());
        book.setPublisherNo(bookRequest.getPublisherNo());
        book.setPrice(bookRequest.getPrice());
        book.setQuantity(bookRequest.getQuantity());
        Library library = libraryRepository.findById(bookRequest.getLibraryId())
                .orElseThrow(() -> new ResourceNotFoundException("Library not found"));
        book.setLibrary(library);
        book.setCreatedAt(LocalDate.now());

        if (bookRepository.existsByTitle(book.getTitle())) {
            throw new DuplicateEntryException("Book with the same title already exists");
        }
        book = bookRepository.save(book);
        return mapToBookResponse(book);
    }

    @Override
    @Transactional
    public BookResponse getBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return mapToBookResponse(book);
    }

    @Override
    @Transactional
    public List<BookResponse> getAllBooks() {
        List<Book> books = bookRepository.findAllByOrderByTitleAsc();
        return books.stream().map(this::mapToBookResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookResponse updateBook(Long id, BookRequest bookRequest) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        book.setTitle(bookRequest.getTitle());
        book.setAuthorName(bookRequest.getAuthorName());
        book.setBookType(bookRequest.getBookType());
        book.setPublisherName(bookRequest.getPublisherName());
        book.setPublisherEmail(bookRequest.getPublisherEmail());
        book.setPublisherNo(bookRequest.getPublisherNo());
        book.setPrice(bookRequest.getPrice());
        book.setQuantity(bookRequest.getQuantity());
        Library library = libraryRepository.findById(bookRequest.getLibraryId())
                .orElseThrow(() -> new ResourceNotFoundException("Book not found"));
        book.setLibrary(library);
        book.setUpdatedAt(LocalDate.now());
        book = bookRepository.save(book);
        return mapToBookResponse(book);
    }

    @Override
    @Transactional
//    public void deleteBook(Long id) {
//        Book book = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book not found"));
//        bookRepository.delete(book);
//    }

    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book not found"));

//        boolean isIssued = issuedBookRepository.existsByBook(book);
//        if (isIssued) {
//            throw new BookIssuedException("Cannot delete book as it is issued to users");
//        }
//
//        bookRepository.delete(book);
        book.setActive(false);
        bookRepository.save(book);
    }

    @Transactional
    private BookResponse mapToBookResponse(Book book) {
        BookResponse bookResponse = new BookResponse();
        bookResponse.setId(book.getId());
        bookResponse.setTitle(book.getTitle());
        bookResponse.setBookType(book.getBookType());
        bookResponse.setPrice(book.getPrice());
        bookResponse.setQuantity(book.getQuantity());
        bookResponse.setAuthorName(book.getAuthorName());
        bookResponse.setPublisherEmail(book.getPublisherEmail());
        bookResponse.setPublisherName(book.getPublisherName());
        bookResponse.setPublisherNo(book.getPublisherNo());
        bookResponse.setLibrary(book.getLibrary());
        bookResponse.setCreatedAt(book.getCreatedAt());
        return bookResponse;
    }

    @Override
    @Transactional
    public List<BookResponse> getAllBooks(Long cityId, Long libraryId) {
        List<Book> books;
        if (cityId != null && libraryId != null) {
            books = bookRepository.findByLibraryCityIdAndLibraryIdAndIsActive(cityId, libraryId, true);
        } else if (cityId != null) {
            books = bookRepository.findByLibraryCityIdAndIsActive(cityId, true);
        } else if (libraryId != null) {
            books = bookRepository.findByLibraryIdAndIsActive(libraryId, true);
        } else {
            books = bookRepository.findByIsActive(true);
        }
        return books.stream().map(this::mapToBookResponse).collect(Collectors.toList());
    }
}
