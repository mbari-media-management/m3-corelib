package org.mbari.m3.corelib.services.annosaurus.v2;

import org.mbari.m3.corelib.model.ImagedMoment;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2019-07-09T10:16:00
 */
public interface ImagedMomentWebService {

    @GET("imagedmoments/videoreference/{uuid}")
    Call<List<ImagedMoment>> findByVideoReferenceUuid(@Path("uuid") UUID videoReferenceUuid);

    @GET("imagedmoments/videoreferences/modified/{start}")
    Call<List<UUID>> findModifiedVideoReferencesSince(@Path("start") Instant start);

    @GET("imagedmoments/videoreferences/modified/{start}/{end}")
    Call<List<UUID>> findModifiedVideoReferencesBetween(@Path("start") Instant start,
                                                        @Path("end") Instant end);
}
