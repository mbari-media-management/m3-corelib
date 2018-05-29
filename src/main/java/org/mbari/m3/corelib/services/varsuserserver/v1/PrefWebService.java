package org.mbari.m3.corelib.services.varsuserserver.v1;

import org.mbari.m3.corelib.model.PreferenceNode;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

/**
 * @author Brian Schlining
 * @since 2017-06-08T16:34:00
 */
public interface PrefWebService {


    @GET("prefs/{name}")
    Call<List<PreferenceNode>> findByName(@Path("name") String name);

    @GET("prefs/{name}/{key}")
    Call<PreferenceNode> findByNameAndKey(@Path("name") String name, @Path("key") String key);

    @GET("prefs/startswith/{name}")
    Call<List<PreferenceNode>> findByNameLike(@Path("name") String name);

    @FormUrlEncoded
    @POST("prefs")
    Call<PreferenceNode> create(@Field("name") String name,
                                @Field("key") String key,
                                @Field("value") String value,
                                @HeaderMap Map<String, String> headers);

    @FormUrlEncoded
    @PUT("prefs/{name}/{key}")
    Call<PreferenceNode> update(@Path("name") String name,
                                @Path("key") String key,
                                @Field("value") String value,
                                @HeaderMap Map<String, String> headers);

    @DELETE("prefs/{name}/{key}")
    Call<Void> delete(@Path("name") String name,
                                @Path("key") String key);
}
