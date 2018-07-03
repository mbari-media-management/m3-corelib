package org.mbari.m3.corelib.services.annosaurus.v1;

import org.mbari.m3.corelib.model.AncillaryData;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

import java.util.List;
import java.util.Map;

/**
 * @author Brian Schlining
 * @since 2018-07-03T14:06:00
 */
public interface AncillaryDataWebService {

    /**
     * Create or updated. Unset fields have default values of zero, so this is NOT
     * a good implementation
     * @param data
     * @param headers
     * @return
     */
    @POST("ancillarydata/bulk")
    Call<List<AncillaryData>> createOrUpdate(@Body List<AncillaryData> data,
                                             @HeaderMap Map<String, String> headers);

}
