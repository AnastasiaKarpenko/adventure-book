# Adventure Book API

REST API to browse a collection of adventure books and play through them.

## Build and run

_TODO_

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