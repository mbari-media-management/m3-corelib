package org.mbari.m3.corelib.services.vampiresquid.v1;

import org.mbari.m3.corelib.model.Media;
import retrofit2.Call;
import retrofit2.http.*;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Brian Schlining
 * @since 2017-05-27T12:31:00
 */
public interface VamWebService {

    @GET("media/videoreference/{uuid}")
    Call<Media> findByUuid(@Path("uuid") UUID uuid);

    @GET("media/videoreference/filename/{filename}")
    Call<List<Media>> findByFilename(@Path("filename") String filename);

    @GET("media/sha512/{sha512}")
    Call<Media> findBySha512(@Path("sha512") String hexSha512);

    @GET("media/uri/{uri}")
    Call<Media> findByUri(@Path("uri") URI uri);

    @GET("media/videosequence/{name}")
    Call<List<Media>> findByVideoSequenceName(@Path("name") String videoSequenceName);

    @GET("media/video/{name}")
    Call<List<Media>> findByVideoName(@Path("name") String videoName);

    @GET("videosequences/names")
    Call<List<String>> findAllVideoSequenceNames();

    @GET("media/camera/{camera_id}/{timestamp}")
    Call<List<Media>> findByCameraIdAndTimestamp(@Path("camera_id") String cameraId,
                                                 @Path("timestamp") Instant timestamp);

    @GET("media/camera/{camera_id}/{start_timestamp}/{end_timestamp}")
    Call<List<Media>> findByCameraIdAndDates(@Path("camera_id") String cameraId,
            @Path("start_timestamp") Instant startTimestamp,
            @Path("end_timestamp") Instant endTimestamp);


    @GET("media/concurrent/{uuid}")
    Call<List<Media>> findConcurrent(@Path("uuid") UUID uuid);

    @FormUrlEncoded
    @POST("media")
    Call<Media> create(@Field("video_sequence_name") String videoSequenceName,
                       @Field("camera_id") String cameraId,
                       @Field("video_name") String videoName,
                       @Field("uri") URI uri,
                       @Field("start_timestamp") Instant startTimestamp,
                       @Field("duration_millis") Duration duration,
                       @Field("container") String container,
                       @Field("video_codec") String videoCodec,
                       @Field("audio_codec") String audioCodec,
                       @Field("width") Integer width,
                       @Field("height") Integer height,
                       @Field("frame_rate") Double frameRate,
                       @Field("sizeBytes") Long sizeBytes,
                       @Field("video_description") String videoDescription,
                       @Field("sha512") byte[] sha512,
                       @HeaderMap Map<String, String> headers);

    @FormUrlEncoded
    @PUT("videos/{uuid}")
    Call<Media> update(@Path("uuid") UUID videoUuid,
            @FieldMap Map<String, String> fields,
            @HeaderMap Map<String, String> headers);

    @GET("videosequences/cameras")
    Call<List<String>> findAllCameraIds();

    @GET("videosequences/names/camera/{camera_id}")
    Call<List<String>> findVideoSequenceNamesByCameraId(@Path("camera_id") String cameraId);

    @GET("videos/names/videosequence/{name}")
    Call<List<String>> findVideoNamesByVideoSequenceName(@Path("name") String name);

}
