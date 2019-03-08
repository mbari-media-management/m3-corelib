/*
 * @(#)AnnoWebService.java   2019.03.08 at 02:48:38 PST
 *
 * Copyright 2011 MBARI
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mbari.m3.corelib.services.annosaurus.v1;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.mbari.m3.corelib.model.*;
import org.mbari.vcr4j.time.Timecode;
import retrofit2.Call;
import retrofit2.http.*;

/**
 * @author Brian Schlining
 * @since 2017-05-22T16:17:00
 */
public interface AnnoWebService {

    /**
     * Counts the annotations for every videoreference
     * @return
     */
    @GET("observations/counts")
    Call<List<AnnotationCount>> countAllGroupByVideoReferenceUuid();

    @GET("observations/videoreference/count/{uuid}")
    Call<AnnotationCount> countByVideoReferenceUuid(@Path("uuid") UUID videoReferenceUuid);

    /**
     * Counts the imaged moments for every videoreference. This number can often
     * be less the `countAllGroupByVideoReferenceUuid` as an imaged-moment may
     * contain more than one `observation` (a.k.a. annotation)
     * @return
     */
    @GET("imagedmoments/counts")
    Call<List<AnnotationCount>> countImagedMomentsGroupByVideoReferenceUuid();

    /**
     * Counts the number of individual observations that use the provided concept
     * @param concept
     * @return
     */
    @GET("observations/concept/count/{concept}")
    Call<ConceptCount> countObservationsByConcept(@Path("concept") String concept);

    /**
     *
     * @param uuid
     * @param concept
     * @param observer
     * @param observationTimestamp
     * @param timecode
     * @param recordedTimestamp
     * @param durationMillis
     * @param group
     * @param activity
     * @param headers Should include the token type: "Authorization":"BEARER [jwtstuff]"
     * @return
     */
    @FormUrlEncoded
    @POST("annotations")
    Call<Annotation> create(@Field("video_reference_uuid") UUID uuid,
            @Field("concept") String concept, @Field("observer") String observer,
            @Field("observation_timestamp") Instant observationTimestamp,
            @Field("timecode") Timecode timecode,
            @Field("elapsed_time_millis") Long elapsedTimeMillis,
            @Field("recorded_timestamp") Instant recordedTimestamp,
            @Field("duration_millis") Long durationMillis,
            @Field("group") String group,
            @Field("activity") String activity,
            @HeaderMap Map<String, String> headers);

    @FormUrlEncoded
    @PUT("observations/concept/rename")
    Call<ConceptsRenamed> renameConcepts(@Field("old") String oldConcept,
                                             @Field("new") String newConcept,
                                             @HeaderMap Map<String, String> headers);

    @POST("annotations/bulk")
    Call<Collection<Annotation>> create(@Body Collection<Annotation> annotations,
            @HeaderMap Map<String, String> headers);

    @DELETE("observations/{uuid}")
    Call<Boolean> delete(@Path("uuid") UUID observationUuid,
            @HeaderMap Map<String, String> headers);

    @POST("observations/delete")
    Call<Void> delete(@Body Collection<UUID> observationUuids,
            @HeaderMap Map<String, String> headers);

    @GET("observations/activities")
    Call<List<String>> findActivities();

    @GET("imagedmoments/videoreference")
    Call<List<UUID>> findAllVideoReferenceUuids();

    @GET("ancillarydata/observation/{uuid}")
    Call<AncillaryData> findAncillaryData(@Path("uuid") UUID observationUuid);

    /**
     * This looks up the single imaged moment that the image is associated with, but
     * since this can contain several observations, it maps to a list of annotations.
     * @param uuid The image reference UUID
     * @return Annotations that are associated with this image
     */
    @GET("annotations/imagereference/{uuid}")
    Call<List<Annotation>> findByImageReferenceUuid(@Path("uuid") UUID uuid);

    @GET("annotations/{uuid}")
    Call<Annotation> findByUuid(@Path("uuid") UUID observationUuid);

    @GET("annotations/videoreference/{uuid}")
    Call<List<Annotation>> findByVideoReferenceUuid(@Path("uuid") UUID uuid,
            @Query("limit") Long limit, @Query("offset") Long offset);

    @GET("observations/groups")
    Call<List<String>> findGroups();

    @PUT("ancillarydata/merge/{uuid}")
    Call<Collection<AncillaryData>> merge(@Path("uuid") UUID videoReferenceUuid,
            @Body Collection<AncillaryData> data, @HeaderMap Map<String, String> headers);

    @FormUrlEncoded
    @PUT("annotations/{uuid}")
    Call<Annotation> update(@Path("uuid") UUID annotationUuid,
            @FieldMap Map<String, String> fields, @HeaderMap Map<String, String> headers);

    @PUT("annotations/bulk")
    Call<Collection<Annotation>> update(@Body Collection<Annotation> annotations,
            @HeaderMap Map<String, String> headers);

    @PUT("annotations/tapetime")
    Call<Collection<Annotation>> updateRecordedTimestampForTapes(
            @Body Collection<Annotation> annotations, @HeaderMap Map<String, String> headers);
}
