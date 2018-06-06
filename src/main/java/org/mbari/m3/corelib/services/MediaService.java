package org.mbari.m3.corelib.services;

import org.mbari.m3.corelib.model.Media;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Brian Schlining
 * @since 2017-05-11T15:42:00
 */
public interface MediaService {

    CompletableFuture<Media> create(String videoSequenceName,
            String cameraId,
            String videoName,
            URI uri,
            Instant startTimestamp);

    CompletableFuture<Media> update(UUID videoReferenceUuid,
            Instant startTimestamp,
            Duration duration);

    CompletableFuture<Media> findByUuid(UUID uuid);

    CompletableFuture<Media> findBySha512(byte[] sha512);

    CompletableFuture<Media> findByUri(URI uri);

    CompletableFuture<List<Media>> findByVideoSequenceName(String videoSequenceName);

    CompletableFuture<List<Media>> findByVideoName(String videoName);

    CompletableFuture<List<String>> findAllVideoSequenceNames();

    CompletableFuture<List<Media>> findByCameraIdAndTimestamp(String cameraId, Instant timestamp);

    CompletableFuture<List<Media>> findByCameraIdAndDate(String cameraId, Instant startTimestamp,
            Instant endTimestamp);

    CompletableFuture<List<Media>> findByVideoSequenceNameAndTimestamp(String videoSequenceName, Instant timestamp);

    CompletableFuture<List<String>> findAllCameraIds();

    CompletableFuture<List<Media>> findConcurrentByVideoReferenceUuid(UUID uuid);

    CompletableFuture<List<Media>> findByFilename(String filename);

    /**
     *
     * @return A list of VideoSequence names available for the camera id
     */
    CompletableFuture<List<String>> findVideoSequenceNamesByCameraId(String cameraId);

    CompletableFuture<List<String>> findVideoNamesByVideoSequenceName(String videoSequenceName);

}
