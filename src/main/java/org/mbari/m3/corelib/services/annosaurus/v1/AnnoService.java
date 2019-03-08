/*
 * @(#)AnnoService.java   2019.03.08 at 02:48:09 PST
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

import java.net.URL;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;
import org.mbari.m3.corelib.model.*;
import org.mbari.m3.corelib.services.AnnotationService;
import org.mbari.m3.corelib.services.AuthService;
import org.mbari.m3.corelib.services.RetrofitWebService;
import retrofit2.Call;

/**
 * @author Brian Schlining
 * @since 2017-05-23T10:09:00
 */
public class AnnoService implements AnnotationService, RetrofitWebService {

    private final AnnoWebService annoService;
    private final AssociationWebService assService;
    private final ImageWebService imageService;
    private final AncillaryDataWebService dataService;
    private final IndexWebService indexService;
    private final Map<String, String> defaultHeaders;
    private final Map<String, String> bulkHeaders;

    /**
     *
     * @param serviceFactory
     * @param authService
     */
    @Inject
    public AnnoService(AnnoWebServiceFactory serviceFactory,
            @Named("ANNO_AUTH") AuthService authService) {
        annoService = serviceFactory.create(AnnoWebService.class, authService);
        assService = serviceFactory.create(AssociationWebService.class, authService);
        imageService = serviceFactory.create(ImageWebService.class, authService);
        dataService = serviceFactory.create(AncillaryDataWebService.class, authService);
        indexService = serviceFactory.create(IndexWebService.class, authService);
        defaultHeaders = new HashMap<>();
        defaultHeaders.put("Accept", "application/json");
        defaultHeaders.put("Accept-Charset", "utf-8");
        bulkHeaders = new HashMap<>(defaultHeaders);
        bulkHeaders.put("Content-Type", "application/json");
    }

    private void addField(Map<String, String> map, String key, Object value) {
        if (value != null) {
            map.put(key, asString(value));
        }
    }

    @Override
    public CompletableFuture<AnnotationCount> countAnnotations(UUID videoReferenceUuid) {
        return sendRequest(annoService.countByVideoReferenceUuid(videoReferenceUuid));
    }

    @Override
    public CompletableFuture<ConceptCount> countObservationsByConcept(String concept) {
        return sendRequest(annoService.countObservationsByConcept(concept));
    }

    @Override
    public CompletableFuture<List<AnnotationCount>> countAnnotationsGroupByVideoReferenceUuid() {
        return sendRequest(annoService.countAllGroupByVideoReferenceUuid());
    }

    @Override
    public CompletableFuture<List<AnnotationCount>> countImagedMomentsGroupByVideoReferenceUuid() {
        return sendRequest(annoService.countImagedMomentsGroupByVideoReferenceUuid());
    }

    @Override
    public CompletableFuture<Annotation> createAnnotation(Annotation annotation) {
        Long durationMillis = (annotation.getDuration() == null) ? null : annotation.getDuration()
                .toMillis();
        Long elapsedTimeMilliis = (annotation.getElapsedTime() == null)
            ? null : annotation.getElapsedTime()
                    .toMillis();
        Call<Annotation> call = annoService.create(annotation.getVideoReferenceUuid(),
                annotation.getConcept(),
                annotation.getObserver(),
                annotation.getObservationTimestamp(),
                annotation.getTimecode(),
                elapsedTimeMilliis,
                annotation.getRecordedTimestamp(),
                durationMillis,
                annotation.getGroup(),
                annotation.getActivity(),
                defaultHeaders);

        return sendRequest(call);
    }

    /**
     *
     * @param annotations
     * @return
     */
    public CompletableFuture<Collection<Annotation>> createAnnotations(
            Collection<Annotation> annotations) {
        return sendRequest(annoService.create(annotations, bulkHeaders));
    }

    @Override
    public CompletableFuture<Association> createAssociation(UUID observationUuid,
            Association association) {
        Call<Association> call = assService.create(observationUuid,
                association.getLinkName(),
                association.getToConcept(),
                association.getLinkValue(),
                association.getMimeType(),
                defaultHeaders);

        return sendRequest(call);
    }

    @Override
    public CompletableFuture<Image> createImage(Image image) {
        String url = (image.getUrl() == null) ? null : image.getUrl()
                .toExternalForm();
        String timecode = asString(image.getTimecode());
        Long elapsedTimeMillis = (image.getElapsedTime() == null) ? null : image.getElapsedTime()
                .toMillis();

        Call<Image> call = imageService.create(image.getVideoReferenceUuid(),
                url,
                timecode,
                elapsedTimeMillis,
                image.getRecordedTimestamp(),
                image.getFormat(),
                image.getWidth(),
                image.getHeight(),
                image.getDescription(),
                defaultHeaders);

        return sendRequest(call);
    }

    /**
     *
     * @param ancillaryData
     * @return
     */
    public CompletableFuture<List<AncillaryData>> createOrUpdateAncillaryData(
            List<AncillaryData> ancillaryData) {
        return sendRequest(dataService.createOrUpdate(ancillaryData, bulkHeaders));
    }

    /**
     *
     * @param videoReferenceUuid
     * @return
     */
    public CompletableFuture<AncillaryDataDeleteCount> deleteAncillaryDataByVideoReference(
            UUID videoReferenceUuid) {
        return sendRequest(dataService.deleteByVideoReference(videoReferenceUuid));
    }

    @Override
    public CompletableFuture<Boolean> deleteAnnotation(UUID observationUuid) {
        return sendRequest(annoService.delete(observationUuid, defaultHeaders));
    }

    /**
     *
     * @param observationUuids
     * @return
     */
    public CompletableFuture<Void> deleteAnnotations(Collection<UUID> observationUuids) {
        return sendRequest(annoService.delete(observationUuids, bulkHeaders));
    }

    @Override
    public CompletableFuture<Boolean> deleteAssociation(UUID associationUuid) {
        return sendRequest(assService.delete(associationUuid));
    }

    /**
     *
     * @param associationUuids
     * @return
     */
    public CompletableFuture<Void> deleteAssociations(Collection<UUID> associationUuids) {
        return sendRequest(assService.delete(associationUuids, bulkHeaders));
    }

    @Override
    public CompletableFuture<Boolean> deleteImage(UUID imageReferenceUuid) {
        return sendRequest(imageService.delete(imageReferenceUuid));
    }

    @Override
    public CompletableFuture<List<String>> findActivities() {
        return sendRequest(annoService.findActivities());
    }

    @Override
    public CompletableFuture<AncillaryData> findAncillaryData(UUID observationUuid) {
        return sendRequest(annoService.findAncillaryData(observationUuid));
    }

    /**
     *
     * @param videoReferenceUuid
     * @return
     */
    public CompletableFuture<List<AncillaryData>> findAncillaryDataByVideoReference(
            UUID videoReferenceUuid) {
        return sendRequest(dataService.findByVideoReferenceUuid(videoReferenceUuid));
    }

    @Override
    public CompletableFuture<List<Annotation>> findAnnotations(UUID videoReferenceUuid) {
        return findAnnotations(videoReferenceUuid, null, null);
    }

    @Override
    public CompletableFuture<List<Annotation>> findAnnotations(UUID videoReferenceUuid, Long limit,
            Long offset) {
        return sendRequest(annoService.findByVideoReferenceUuid(videoReferenceUuid, limit, offset));
    }

    /**
     * Find all annotations for a given video reference using a giving page size
     * to avoid timeouts
     * @param videoReferenceUuid
     * @param pageSize
     * @param pageTimeout
     * @param executor
     * @return
     */
    public CompletableFuture<List<Annotation>> findAnnotations(UUID videoReferenceUuid,
            int pageSize, Duration pageTimeout, ExecutorService executor) {

        CompletableFuture<List<Annotation>> future = new CompletableFuture<>();
        countAnnotations(videoReferenceUuid).thenAccept(
            ac -> findAnnotations(ac.getVideoReferenceUuid(),
                    ac.getCount(),
                    0,
                    pageSize,
                    pageTimeout,
                    executor).whenComplete((annotations, ex) -> {
                    if (ex != null) {
                        future.completeExceptionally(ex);
                    }
                    else {
                        future.complete(annotations);
                    }
                }));

        return future;

    }

    /**
     *
     * @param videoReferenceUuid
     * @param limit
     * @param offset
     * @param pageSize
     * @param pageTimeout
     * @param executor
     * @return
     */
    public CompletableFuture<List<Annotation>> findAnnotations(UUID videoReferenceUuid, long limit,
            long offset, int pageSize, Duration pageTimeout, ExecutorService executor) {


        CompletableFuture<List<Annotation>> cf = new CompletableFuture<>();

        Runnable task = () -> {
                    List<Annotation> annotations = new ArrayList<>();
                    int pageCount = (int) Math.ceil(limit / (double) pageSize);
                    for (int i = 0; i < pageCount; i++) {
                        long offsetForPage = i * pageSize;

                        CompletableFuture<List<Annotation>> future = findAnnotations(
                            videoReferenceUuid,
                                    (long) pageSize,
                                    offsetForPage);

                        try {
                            List<Annotation> annos = future.get(pageTimeout.toMillis(),
                                    TimeUnit.MILLISECONDS);
                            annotations.addAll(annos);
                        }
                        catch (Exception e) {
                            String msg = "Failed to load page chunk (" + offsetForPage + " to " +
                                offsetForPage + pageSize + ")";
                            Exception e0 = new RuntimeException(msg, e);
                            cf.completeExceptionally(e0);
                            break;
                        }
                    }
                    cf.complete(annotations);
                };

        executor.submit(task);

        return cf;
    }

    /**
     *
     * @param associationUuid
     * @return
     */
    public CompletableFuture<Association> findAssociationByUuid(UUID associationUuid) {
        return sendRequest(assService.findByUuid(associationUuid));
    }

    /**
     *
     * @param imageReferenceUuid
     * @return
     */
    public CompletableFuture<List<Annotation>> findByImageReference(UUID imageReferenceUuid) {
        return sendRequest(annoService.findByImageReferenceUuid(imageReferenceUuid));
    }

    @Override
    public CompletableFuture<Annotation> findByUuid(UUID observationUuid) {
        return sendRequest(annoService.findByUuid(observationUuid));
    }

    /**
     *
     * @param videoReferenceUuid
     * @param linkName
     * @return
     */
    public CompletableFuture<List<Association>> findByVideoReferenceAndLinkName(
            UUID videoReferenceUuid, String linkName) {
        return sendRequest(assService.findByVideoReferenceAndLinkName(videoReferenceUuid,
                linkName));
    }

    @Override
    public CompletableFuture<List<String>> findGroups() {
        return sendRequest(annoService.findGroups());
    }

    /**
     *
     * @param url
     * @return
     */
    public CompletableFuture<Image> findImageByUrl(URL url) {
        return sendRequest(imageService.findByUrl(url.toExternalForm()));
    }

    /**
     *
     * @param imageReferenceUuid
     * @return
     */
    public CompletableFuture<Image> findImageByUuid(UUID imageReferenceUuid) {
        return sendRequest(imageService.findByUuid(imageReferenceUuid));
    }

    @Override
    public CompletableFuture<List<Image>> findImagesByVideoReferenceUuid(UUID videoReferenceUuid) {
        return sendRequest(imageService.findByVideoReferenceUuid(videoReferenceUuid));
    }

    /**
     *
     * @param videoReferenceUuid
     * @return
     */
    public CompletableFuture<List<Index>> findIndicesByVideoReferenceUuid(UUID videoReferenceUuid) {
        return sendRequest(indexService.findByVideoReferenceUuid(videoReferenceUuid));
    }

    /**
     *
     * @param videoReferenceUuid
     * @param data
     * @return
     */
    public CompletableFuture<Collection<AncillaryData>> merge(UUID videoReferenceUuid,
            Collection<AncillaryData> data) {
        return sendRequest(annoService.merge(videoReferenceUuid, data, bulkHeaders));
    }

    public CompletableFuture<ConceptsRenamed> renameConcepts(String oldConcept, String newConcept) {
        return sendRequest(annoService.renameConcepts(oldConcept, newConcept, defaultHeaders));
    }

    @Override
    public CompletableFuture<Annotation> updateAnnotation(Annotation annotation) {
        Map<String, String> fieldMap = new HashMap<>();
        addField(fieldMap, "video_reference_uuid", annotation.getVideoReferenceUuid());
        addField(fieldMap, "concept", annotation.getConcept());
        addField(fieldMap, "observer", annotation.getObserver());
        addField(fieldMap, "timecode", annotation.getTimecode());
        Long elapsedTimeMillis = (annotation.getElapsedTime() == null)
            ? null : annotation.getElapsedTime()
                    .toMillis();
        addField(fieldMap, "elapse_time_millis", elapsedTimeMillis);
        addField(fieldMap, "recorded_timestamp", annotation.getRecordedTimestamp());
        Long durationMillis = (annotation.getDuration() == null) ? null : annotation.getDuration()
                .toMillis();
        addField(fieldMap, "duration_millis", durationMillis);
        addField(fieldMap, "group", annotation.getGroup());
        addField(fieldMap, "activity", annotation.getActivity());

        return sendRequest(annoService.update(annotation.getObservationUuid(),
                fieldMap,
                defaultHeaders));
    }

    /**
     *
     * @param annotations
     * @return
     */
    public CompletableFuture<Collection<Annotation>> updateAnnotations(
            Collection<Annotation> annotations) {
        return sendRequest(annoService.update(annotations, bulkHeaders));
    }

    @Override
    public CompletableFuture<Association> updateAssociation(Association association) {
        Map<String, String> fieldMap = new HashMap<>();
        addField(fieldMap, "link_name", association.getLinkName());
        addField(fieldMap, "to_concept", association.getToConcept());
        addField(fieldMap, "link_value", association.getLinkValue());
        addField(fieldMap, "mime_type", association.getMimeType());

        return sendRequest(assService.update(association.getUuid(), fieldMap, defaultHeaders));
    }

    /**
     *
     * @param associations
     * @return
     */
    public CompletableFuture<Collection<Association>> updateAssociations(
            Collection<Association> associations) {
        return sendRequest(assService.update(associations));
    }

    @Override
    public CompletableFuture<Image> updateImage(Image image) {
        Map<String, String> fieldMap = new HashMap<>();
        addField(fieldMap, "video_reference_uuid", image.getVideoReferenceUuid());
        String url = (image.getUrl() == null) ? null : image.getUrl()
                .toExternalForm();
        addField(fieldMap, "url", url);
        addField(fieldMap, "timecode", image.getTimecode());
        Long elapsedTimeMillis = (image.getElapsedTime() == null) ? null : image.getElapsedTime()
                .toMillis();
        addField(fieldMap, "elapsed_time_millis", elapsedTimeMillis);
        addField(fieldMap, "recorded_timestamp", image.getRecordedTimestamp());
        addField(fieldMap, "format", image.getFormat());
        addField(fieldMap, "width_pixels", image.getWidth());
        addField(fieldMap, "height_pixels", image.getHeight());
        addField(fieldMap, "description", image.getDescription());

        return sendRequest(imageService.update(image.getImageReferenceUuid(),
                fieldMap,
                defaultHeaders));
    }

    /**
     *
     * @param indices
     * @return
     */
    public CompletableFuture<List<Index>> updateIndexRecordedTimestamps(Collection<Index> indices) {
        return sendRequest(indexService.update(indices, bulkHeaders));
    }

    /**
     *
     * @param annotations
     * @return
     */
    public CompletableFuture<Collection<Annotation>> updateRecordedTimestampsForTapes(
            Collection<Annotation> annotations) {
        return sendRequest(annoService.updateRecordedTimestampForTapes(annotations, bulkHeaders));
    }
}
