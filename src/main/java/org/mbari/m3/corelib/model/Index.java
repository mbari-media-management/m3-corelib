package org.mbari.m3.corelib.model;

import org.mbari.vcr4j.time.Timecode;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2019-02-08T15:03:00
 */
public class Index {

    private UUID uuid;
    private UUID videoReferenceUuid;
    private Duration elapsedTime;
    private Instant recordedTimestamp;
    private Timecode timecode;
    private Instant lastUpdatedTime;

    public Index() {}

    public Index(UUID uuid, UUID videoReferenceUuid, Duration elapsedTime) {
        this.uuid = uuid;
        this.videoReferenceUuid = videoReferenceUuid;
        this.elapsedTime = elapsedTime;
    }

    public Index(UUID uuid, UUID videoReferenceUuid, Instant recordedTimestamp) {
        this.uuid = uuid;
        this.videoReferenceUuid = videoReferenceUuid;
        this.recordedTimestamp = recordedTimestamp;
    }

    public Index(UUID uuid, UUID videoReferenceUuid, Timecode timecode) {
        this.uuid = uuid;
        this.videoReferenceUuid = videoReferenceUuid;
        this.timecode = timecode;
    }

    public Index(UUID uuid, UUID videoReferenceUuid, Duration elapsedTime, Instant recordedTimestamp, Timecode timecode) {
        this.uuid = uuid;
        this.videoReferenceUuid = videoReferenceUuid;
        this.elapsedTime = elapsedTime;
        this.recordedTimestamp = recordedTimestamp;
        this.timecode = timecode;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getVideoReferenceUuid() {
        return videoReferenceUuid;
    }

    public void setVideoReferenceUuid(UUID videoReferenceUuid) {
        this.videoReferenceUuid = videoReferenceUuid;
    }

    public Duration getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(Duration elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public Instant getRecordedTimestamp() {
        return recordedTimestamp;
    }

    public void setRecordedTimestamp(Instant recordedTimestamp) {
        this.recordedTimestamp = recordedTimestamp;
    }

    public Timecode getTimecode() {
        return timecode;
    }

    public void setTimecode(Timecode timecode) {
        this.timecode = timecode;
    }

    public Instant getLastUpdatedTime() {
        return lastUpdatedTime;
    }
}
