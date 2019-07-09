package org.mbari.m3.corelib.services;

import org.mbari.m3.corelib.model.ImagedMoment;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Brian Schlining
 * @since 2019-07-09T10:01:00
 */
public interface AnnotationServiceV2 {

    CompletableFuture<List<ImagedMoment>> findImagedMomentsByVideoReferenceUuid(UUID videoReferenceUuid);

    CompletableFuture<List<UUID>> findModifiedVideoReferencesSince(Instant start);
    CompletableFuture<List<UUID>> findModifiedVideoReferencesBetween(Instant start, Instant end);


}
