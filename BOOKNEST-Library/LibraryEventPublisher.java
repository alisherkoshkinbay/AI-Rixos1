package kz.teamworks.library.observer;

import java.util.ArrayList;
import java.util.List;

/** Observer pattern: sends change events to the dashboard and other subscribers. */
public class LibraryEventPublisher {
    private final List<LibraryObserver> observers = new ArrayList<>();

    public void subscribe(LibraryObserver observer) {
        if (observer != null && !observers.contains(observer)) observers.add(observer);
    }

    public void unsubscribe(LibraryObserver observer) { observers.remove(observer); }

    public void publish(String message) {
        for (LibraryObserver observer : new ArrayList<>(observers)) {
            observer.onLibraryChanged(message);
        }
    }
}
