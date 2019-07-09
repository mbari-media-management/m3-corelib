package org.mbari.m3.corelib.services.annosaurus.v2;

import org.mbari.m3.corelib.model.ImagedMoment;
import org.mbari.m3.corelib.services.AnnotationServiceV2;
import org.mbari.m3.corelib.services.AuthService;
import org.mbari.m3.corelib.services.RetrofitWebService;

import javax.inject.Inject;
import javax.inject.Named;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Brian Schlining
 * @since 2019-07-09T09:52:00
 */
public class AnnoService implements AnnotationServiceV2, RetrofitWebService {

//    private final AnnoWebService annoService;
    private final ImagedMomentWebService momentService;
    private final Map<String, String> defaultHeaders;

    @Inject
    public AnnoService(AnnoWebServiceFactory serviceFactory,
                       @Named("ANNO_AUTH")AuthService authService) {

//        annoService = serviceFactory.create(AnnoWebService.class, authService);
        momentService = serviceFactory.create(ImagedMomentWebService.class, authService);
        defaultHeaders = new HashMap<>();
        defaultHeaders.put("Accept", "application/json");
        defaultHeaders.put("Accept-Charset", "utf-8");

    }

    @Override
    public CompletableFuture<List<ImagedMoment>> findImagedMomentsByVideoReferenceUuid(UUID videoReferenceUuid) {
        return sendRequest(momentService.findByVideoReferenceUuid(videoReferenceUuid));
    }

    @Override
    public CompletableFuture<List<UUID>> findModifiedVideoReferencesSince(Instant start) {
        return sendRequest(momentService.findModifiedVideoReferencesSince(start));
    }

    @Override
    public CompletableFuture<List<UUID>> findModifiedVideoReferencesBetween(Instant start, Instant end) {
        return sendRequest(momentService.findModifiedVideoReferencesBetween(start, end));
    }
}
