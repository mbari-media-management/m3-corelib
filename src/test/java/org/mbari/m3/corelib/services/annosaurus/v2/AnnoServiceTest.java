package org.mbari.m3.corelib.services.annosaurus.v2;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mbari.m3.corelib.Initializer;
import org.mbari.m3.corelib.model.ImagedMoment;
import org.mbari.m3.corelib.services.AnnotationServiceV2;
import org.mbari.m3.corelib.util.AsyncUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Brian Schlining
 * @since 2019-07-09T10:54:00
 */
public class AnnoServiceTest {
    AnnotationServiceV2 annoService = Initializer.getInjector()
            .getInstance(AnnoService.class);

    Duration timeout = Duration.ofMillis(7500);

    private final UUID videoReferenceUuid = UUID.fromString("9a1145c5-f600-4f45-af2c-c7867e8dff6f");

    @Test
    public void testFindImagedMoments() {
        CompletableFuture<List<ImagedMoment>> future = annoService.findImagedMomentsByVideoReferenceUuid(videoReferenceUuid);
        List<ImagedMoment> imagedMoments = AsyncUtils.await(future, timeout).orElse(new ArrayList<>());
        assertTrue(!imagedMoments.isEmpty());
    }

    @Test
    public void testFindModifiedVideoReferencesSince() {
        Instant start = Instant.now().minus(Duration.ofDays(30));
        CompletableFuture<List<UUID>> future = annoService.findModifiedVideoReferencesSince(start);
        List<UUID> uuids = AsyncUtils.await(future, timeout).orElse(new ArrayList<>());
        assertTrue(!uuids.isEmpty());
    }

    @Test
    public void testFindModifiedVideoReferencesBetween() {
        Instant start = Instant.now().minus(Duration.ofDays(30));
        Instant end = Instant.now();
        CompletableFuture<List<UUID>> future = annoService.findModifiedVideoReferencesBetween(start, end);
        List<UUID> uuids = AsyncUtils.await(future, timeout).orElse(new ArrayList<>());
        assertTrue(!uuids.isEmpty());

    }

}
