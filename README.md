# Adventure Book API

REST API to browse a collection of adventure books and play through them.

## Build and run

Requirements: Java 21. Maven is not required — the project includes the Maven Wrapper.

Run tests:

    ./mvnw test

Start the application:

    ./mvnw spring-boot:run

Or build and run the JAR:

    ./mvnw package
    java -jar target/adventure-book-0.0.1-SNAPSHOT.jar

On Windows use `mvnw.cmd` instead of `./mvnw`.

The application starts on port 8080.

## How to try

After startup:

- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 console: http://localhost:8080/h2-console
  (JDBC URL `jdbc:h2:mem:adventurebook`, user `sa`, empty password)

## Assumptions

- Sample books from `src/main/resources/books/` are loaded at startup.
  Books are identified by title and author; already imported books are skipped.
- Files that cannot be turned into a book are skipped with a warning:
  empty files, missing title/author/difficulty, sections without id/type/text,
  options without `gotoId`, duplicate section ids within a book.
- Health has no upper limit; the spec defines only the start value (10) and death at 0.

See [docs/DECISIONS.md](docs/DECISIONS.md) for design decisions.

## Known limitations / what I would improve

- `Book → Section` and `Section → SectionOption` are unidirectional `@OneToMany`
  relations. Hibernate inserts child rows and then issues extra `UPDATE`s
  for the foreign key and order column. Negligible for startup import of a few
  books; for large volumes I would make the relations bidirectional
  (`@ManyToOne` on the child, `mappedBy` on the parent).
- Book categories are loaded lazily per book, so listing books issues one
  extra query per book (N+1). Fine for a small catalog; for large volumes
  I would fetch categories together with books (`@EntityGraph` or `join fetch`).
- The book list is not paginated. With a large catalog I would use
  Spring Data `Pageable` (`page` and `size` parameters).
- Books are always sorted by title. Sorting could be made configurable
  (e.g. `?sort=author`).
- A repeated identical choice request (e.g. a double click) is processed as a new move.
  The client could send the section it moves from (`{"option": 0, "fromSection": 20}`),
  and the server would reject the move with 409 if the game is no longer there.
- There is no authentication, and game ids are sequential, so anyone who knows
  or guesses a game id can make moves in that game. In production, games would
  belong to an authenticated player, and ids would not be guessable (e.g. UUIDs).