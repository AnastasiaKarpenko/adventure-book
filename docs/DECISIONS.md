# Design Decisions

## 1. Section identity

**Context:** Section ids in the JSON files are unique only within a book
(e.g. every book has a section 1).

**Options:**
- Composite primary key (book id + section number)
- Generated technical id + section number as a regular column

**Decision:** Generated technical id, with a unique constraint on
(book_id, number).

**Reason:** Same uniqueness guarantee as a composite key, but simpler
JPA mapping (no `@EmbeddedId` / key class).

## 2. Game state

**Context:** While playing, the current section and the player's health
must be tracked.

**Options:**
- Server-side: a `Game` entity stored in the database
- Client-side: the client sends current section and health on every request

**Decision:** Server-side `Game` entity.

**Reason:** Clients cannot tamper with health or jump to arbitrary
sections. Also required for saving and resuming progress (objective 5).

## 3. Making a choice

**Context:** A player picks one of the options of the current section.

**Options:**
- By option position within the current section (`{ "option": 1 }`)
- By target section id (`{ "gotoId": 20 }`)

**Decision:** By option position.

**Reason:** Prevents jumping to arbitrary sections. Two options may lead
to the same section with different consequences; only the position
distinguishes them.

## 4. Reading a book

**Context:** Objective 3 asks to read a book and jump between sections;
objective 4 adds consequences.

**Options:**
- Reading only through a `Game` (start a game, then make choices)
- Additionally, free access to any section (`GET /books/{id}/sections/{number}`)

**Decision:** Reading only through a `Game`. Objective 3 is the navigation
between sections; objective 4 adds consequences to the same flow.

**Reason:** Free section access bypasses the game rules (e.g. reading the
ending directly) and would become redundant once games exist.


## 5. Categories

**Context:** The spec lists example categories (FICTION, SCIENCE, HORROR,
ADVENTURE, etc.) but does not say whether the list is fixed or open.
The provided JSON books have no categories.

**Options:**
- Fixed set as an enum
- Free-form strings on each book
- Separate `category` table (many-to-many with books)

**Decision:** Separate `category` table.
- The four categories from the spec are created at startup.
- New categories are created explicitly (`POST /categories`).
- Attaching a category to a book (`PUT /books/{id}/categories/{name}`)
  accepts only existing categories; unknown names return 404.
- Names are normalized to upper case.
- A category can be deleted only if no book uses it; otherwise 409.

**Reason:** Supports both readings of the spec (fixed list and "etc.")
without code changes. Separating creation from attaching prevents typos
from silently creating new categories. Refusing to delete used categories
avoids accidental data loss.


## 6. Invalid books

**Context:** By the spec's rules, all provided sample books are invalid:
each contains a non-ending section without options (section 666), and
*Pirates of the Jade Sea* also references a non-existent section 999.
`dragon-quest.json` is empty. The spec does not say how invalid books
should be handled.

**Options:**
- Reject invalid books on load
- Load all books with a validity flag and the list of validation errors
- Relax rule 4 to apply only to reachable sections

**Decision:**
- All parseable books are loaded with `valid` and `validationErrors`.
- Invalid books can be listed, searched and categorized, but a game cannot
  be started for them (409 with the errors).
- Unparseable or empty files are skipped with a warning in the log.
- A corrected copy of *The Prisoner* (without section 666) is added for
  demonstration. The original files are not modified.

**Reason:** Follows the spec's validation rules strictly, while keeping
browsing features usable and making validation problems visible.
Playing an invalid book could leave the player stuck in a section with no
options, or send them to a section that does not exist. Rejecting all
books would leave the application with nothing to play.