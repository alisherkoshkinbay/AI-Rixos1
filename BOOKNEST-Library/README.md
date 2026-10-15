# BOOKNEST — Library Management System

Java 17 desktop application built with Swing for the assignment **№8: Кітапхананы басқару жүйесі (каталог, қарызға беру, айыппұл)**.

## Features
- Dashboard with catalogue, availability, active loans and overdue-book statistics.
- Book catalogue: add, search, sort by title/author/availability, and remove books.
- Lending: issue a book to a reader, set a due date, and register returns.
- Late-return fines: 200 ₸ per overdue day, calculated automatically.
- Observer-driven UI refresh after changes.
- Automatic local persistence in `library-data.ser`.
- Sample catalogue and lending records appear on first start.

## Design patterns used
1. **Singleton** — `repository/LibraryRepository.java`: one shared repository loads and saves the app's data.
2. **Strategy** — `strategy/BookSortStrategy.java` plus `ByTitleStrategy`, `ByAuthorStrategy`, `ByAvailabilityStrategy`: sorting behaviour can be switched at runtime.
3. **Observer** — `observer/LibraryEventPublisher.java` and `LibraryObserver.java`: the UI reacts to catalogue and lending changes.
4. **Facade** — `service/LibraryFacade.java`: a single API for adding/removing books, lending/returning and calculating fines.
5. **Iterator** — `iterator/BookCatalog.java`: custom iterator used to traverse a snapshot of the catalogue.

## Open in IntelliJ IDEA
1. Unzip this project folder.
2. Open IntelliJ IDEA → **Open** → select the `library-management-system` folder (the folder containing `pom.xml`).
3. Set Project SDK to JDK 17 or 21.
4. Wait for Maven to import the project.
5. Open `src/main/java/kz/teamworks/library/Main.java` and click the green Run icon beside `main()`.

If Maven is not installed, IntelliJ's bundled Maven should still be able to import the `pom.xml`. The source code itself uses only standard Java/Swing APIs and has no external dependencies.

## Run on Windows
Double-click `run.bat` if JDK 17+ is available and the packaged `target/library-management-system-1.0.0.jar` exists. For the first source run in IntelliJ, Maven will compile the application.

## Storage and fine policy
The data file `library-data.ser` is created in the application's current working directory. Do not delete it unless you intend to reset stored data. This is a classroom demo, so a flat local file is used instead of a database. The fine policy is fixed at **200 ₸ per overdue day** and can be adjusted in `LibraryFacade.FINE_PER_DAY_KZT`.
