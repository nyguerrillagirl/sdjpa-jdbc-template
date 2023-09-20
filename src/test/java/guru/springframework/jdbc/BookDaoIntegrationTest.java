package guru.springframework.jdbc;


import guru.springframework.jdbc.dao.BookDao;

import guru.springframework.jdbc.domain.Book;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("local")
@DataJpaTest
@ComponentScan(basePackages = {"guru.springframework.jdbc.dao"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookDaoIntegrationTest {

    @Autowired
    BookDao bookDao;

    @Test
    void testGetBook() {
        Book fetchedBook = bookDao.getById(1L);
        assertThat(fetchedBook).isNotNull();
    }
    @Test
    void testGetBookByTitle() {
        Book fetchedBook = bookDao.findBookByTitle("Domain-Driven Design");
        assertThat(fetchedBook).isNotNull();
        Assertions.assertEquals("978-0321125217", fetchedBook.getIsbn());
        Assertions.assertEquals("Addison Wesley", fetchedBook.getPublisher());
    }
    @Test
    void testSaveNewBook() {
        Book newBook = new Book("The C++ Workshop", "978-1-83921-662-6", "Packt Publishing" );
        Book savedBook = bookDao.saveNewBook(newBook);

        assertThat(savedBook).isNotNull();
        Assertions.assertEquals("978-1-83921-662-6", savedBook.getIsbn());
    }

    @Test
    void testSaveNewBook2() {
        Book newBook = new Book("The C++ Workshop", "978-1-83921-662-6", "Packt Publishing");
        newBook.setAuthorId(1L);

        Book savedBook = bookDao.saveNewBook(newBook);

        assertThat(savedBook).isNotNull();
        Assertions.assertEquals("978-1-83921-662-6", savedBook.getIsbn());

    }

    @Test
    void testUpdateBook() {
        // First create a new author
        Book newBook = new Book("978-1-83921-662-6", "Packt Publishing", "The C++ Workshop");
        Book savedBook = bookDao.saveNewBook(newBook);

        // After saving, set the author
        savedBook.setAuthorId(1L);

        Book updatedBook = bookDao.updateBook(savedBook);

        assertThat(updatedBook.getAuthorId()).isEqualTo(1L);
    }

    @Test
    void testDeleteBook() {
        Book newBook = new Book("The Ultimate Atari 2600 Programming Guide", "918-9873-0009", "Atari Publishing");
        Book savedBook = bookDao.saveNewBook(newBook);

        // Now let's delete
        bookDao.deleteBookById(savedBook.getId());

        // Check it actually worked
        assertThrows(EmptyResultDataAccessException.class, () -> {
            bookDao.getById(savedBook.getId());
        });
    }
}
