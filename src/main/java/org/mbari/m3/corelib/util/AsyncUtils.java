package org.mbari.m3.corelib.util;

import io.reactivex.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Brian Schlining
 * @since 2017-06-27T09:31:00
 */
public class AsyncUtils {

    private static final Logger log = LoggerFactory.getLogger(AsyncUtils.class);

    public static <T> Optional<T> await(CompletableFuture<T> f, Duration timeout) {
        Optional<T> r;
        try {
            r = Optional.ofNullable(f.get(timeout.toMillis(), TimeUnit.MILLISECONDS));
        }
        catch (Exception e) {
            log.info("An exception was thrown when waiting for a future to complete", e);
            r = Optional.empty();
        }
        return r;
    }

    public static <T, R> CompletableFuture<Collection<R>>
    collectAll(Collection<T> items,
               Function<T, CompletableFuture<R>> fn) {

        // Storage for data as futures complete
        CopyOnWriteArrayList<R> returnValues =
                new CopyOnWriteArrayList<>();

        CompletableFuture[] futures = items.stream()
                .map(fn)
                .map(r -> r.thenAccept(returnValues::add))
                .toArray(CompletableFuture[]::new);

        return CompletableFuture.allOf(futures)
                .thenApply(v -> returnValues);

    }

    /**
     * Convert a CompletableFuture to an rx Observable. This observable will
     * emit exactly one item.
     *
     * @param future The future to convert
     * @param <T> The return type of the future
     * @return An rx java Observable
     */
    public static <T> Observable<T> observe(CompletableFuture<T> future) {
        return Observable.fromFuture(future);
    }

    public static <T, R> Observable<R> observeAll(Collection<T> items,
               Function<T, CompletableFuture<R>> fn) {

        // Apply the function to each item
        List<CompletableFuture<R>> futures = items.stream()
                .map(fn)
                .collect(Collectors.toList());

        // Return a cold observable. Otherwise the observable may
        // emit it's values before you subscribe. Not usually what
        // you want.
        return Observable.defer(() -> {
            List<Observable<R>> observables = futures.stream()
                    .map(Observable::fromFuture)
                    .collect(Collectors.toList());
            return Observable.concat(observables);
        });

    }

}
