package org.mbari.m3.corelib.services.panoptes.v1;

import org.junit.Test;
import static org.junit.Assert.*;
import org.mbari.io.FileUtilities;
import org.mbari.m3.corelib.Initializer;
import static org.mbari.m3.corelib.util.AsyncUtils.await;

import org.mbari.m3.corelib.model.ImageUploadResults;
import org.mbari.net.URLUtilities;

import java.io.File;
import java.net.URL;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author Brian Schlining
 * @since 2017-09-06T10:57:00
 */
public class PanoptesServiceTest {

    private Duration timeout = Duration.ofMillis(45000);
    private PanoptesService service = Initializer.getInjector().getInstance(PanoptesService.class);

    @Test
    public void testUpload() {
        URL imageUrl = getClass().getResource("/images/00_02_25_20.png");
        File imageFile = URLUtilities.toFile(imageUrl);
        CompletableFuture<ImageUploadResults> f = service.upload("i2MAP", "9999",
                "00_02_25_20.png", imageFile.toPath());
        Optional<ImageUploadResults> results = await(f, timeout);
        assertTrue(results.isPresent());

    }

}
