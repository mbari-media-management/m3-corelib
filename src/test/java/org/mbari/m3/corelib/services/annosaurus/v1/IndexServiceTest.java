package org.mbari.m3.corelib.services.annosaurus.v1;


import static org.junit.Assert.*;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Test;
import org.mbari.m3.corelib.Initializer;
import org.mbari.m3.corelib.gson.AnnotationCreator;
import org.mbari.m3.corelib.gson.ByteArrayConverter;
import org.mbari.m3.corelib.gson.DurationConverter;
import org.mbari.m3.corelib.gson.TimecodeConverter;
import org.mbari.m3.corelib.model.ImagedMoment;
import org.mbari.m3.corelib.model.Index;
import org.mbari.m3.corelib.services.AnnotationService;
import org.mbari.vcr4j.time.Timecode;

import static org.mbari.m3.corelib.util.AsyncUtils.*;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Brian Schlining
 * @since 2019-02-08T16:18:00
 */
public class IndexServiceTest {

    AnnotationService annoService = Initializer.getInjector().getInstance(AnnoService.class);
    Duration timeout = Duration.ofMillis(15000);

    private final UUID uuid = UUID.fromString("b418c2fb-7e76-4ed0-a4a8-929cec8cace0");
    private final UUID tempUuid = UUID.randomUUID();

    @Test
    public void testFindIndices() {
        CompletableFuture<List<Index>> f = annoService.findIndicesByVideoReferenceUuid(uuid);
        Optional<List<Index>> ids = await(f, timeout);
        assertTrue("Expected to find indices, but none were returned", ids.isPresent());
        List<Index> ix = ids.get();
        assertTrue("Expected data to be present", ix.size() > 10);

        //annoService.find
    }

    @Test
    public void testUpdatedIndices() {

        Gson gson = getGson();

        CompletableFuture<List<Index>> f = annoService.findIndicesByVideoReferenceUuid(uuid);
        Optional<List<Index>> ids = await(f, timeout);
        assertTrue("Expected to find indices, but none were returned", ids.isPresent());
        List<Index> ix = ids.get();
        assertTrue("Expected data to be present", ix.size() > 10);

        Index index = ix.get(0);
        Instant now = Instant.now();
        assertTrue(!now.equals(index.getRecordedDate()));
        index.setRecordedDate(now);
        List<Index> data = new ArrayList<>();
        data.add(index);
        System.out.println(gson.toJson(index));

        CompletableFuture<List<Index>> f1 = annoService.updateIndexRecordedTimestamps(data);
        Optional<List<Index>> ids1 = await(f1, timeout);
        assertTrue("Expected to find indices, but none were returned", ids1.isPresent());
        List<Index> ix1 = ids1.get();
        assertEquals(1, ix1.size());
        System.out.println(gson.toJson(ix1.get(0)));
        assertNotNull(ix1.get(0).getRecordedDate());
        assertEquals(now, ix1.get(0).getRecordedDate());



    }

    static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder()
                .setPrettyPrinting()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
                .registerTypeAdapter(ImagedMoment.class, new AnnotationCreator())
                .registerTypeAdapter(Duration.class, new DurationConverter())
                .registerTypeAdapter(Timecode.class, new TimecodeConverter())
                .registerTypeAdapter(byte[].class, new ByteArrayConverter());

        // Register java.time.Instant
        return Converters.registerInstant(gsonBuilder)
                .create();

    }
}
