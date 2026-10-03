package com.anastasia.adventurebook.importer;

import com.anastasia.adventurebook.model.Book;
import com.anastasia.adventurebook.repository.BookRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;

@Component
public class BookImporter implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(BookImporter.class);

    private static final String BOOKS_LOCATION = "classpath:books/*.json";

    private final JsonMapper jsonMapper;
    private final BookJsonMapper bookJsonMapper;
    private final BookRepository bookRepository;
    private final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

    public BookImporter(JsonMapper jsonMapper, BookJsonMapper bookJsonMapper, BookRepository bookRepository) {
        this.jsonMapper = jsonMapper;
        this.bookJsonMapper = bookJsonMapper;
        this.bookRepository = bookRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws IOException {
        Resource[] files = resourceResolver.getResources(BOOKS_LOCATION);
        int imported = 0;
        for (Resource file : files) {
            if (importFile(file)) {
                imported++;
            }
        }
        log.info("Book import finished: {} imported, {} skipped", imported, files.length - imported);
    }

    private boolean importFile(Resource file) {
        String fileName = file.getFilename();
        try (InputStream in = file.getInputStream()) {
            BookJson json = jsonMapper.readValue(in, BookJson.class);
            if (json == null) {
                log.warn("Skipping {}: file is empty", fileName);
                return false;
            }
            Book book = bookJsonMapper.toEntity(json);
            if (bookRepository.existsByTitleAndAuthor(book.getTitle(), book.getAuthor())) {
                log.info("Skipping {}: book '{}' is already imported", fileName, book.getTitle());
                return false;
            }
            bookRepository.save(book);
            log.info("Imported '{}' from {}", book.getTitle(), fileName);
            return true;
        } catch (JacksonException e) {
            log.warn("Skipping {}: not a valid book JSON ({})", fileName, e.getOriginalMessage());
        } catch (BookImportException e) {
            log.warn("Skipping {}: {}", fileName, e.getMessage());
        } catch (IOException e) {
            log.warn("Skipping {}: cannot read file ({})", fileName, e.getMessage());
        }
        return false;
    }
}