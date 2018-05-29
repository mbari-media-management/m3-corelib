package org.mbari.m3.corelib.services.varsuserserver.v1;

import org.mbari.m3.corelib.model.PreferenceNode;
import org.mbari.m3.corelib.services.AuthService;
import org.mbari.m3.corelib.services.PreferencesService;
import org.mbari.m3.corelib.services.RetrofitWebService;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author Brian Schlining
 * @since 2017-06-09T08:27:00
 */
public class KBPrefService implements PreferencesService, RetrofitWebService {

    private final PrefWebService prefService;

    private final Map<String, String> defaultHeaders;

    @Inject
    public KBPrefService(PrefWebServiceFactory serviceFactory, @Named("PREFS_AUTH") AuthService authService) {
        prefService = serviceFactory.create(PrefWebService.class, authService);
        defaultHeaders = new HashMap<>();
        defaultHeaders.put("Accept", "application/json");
        defaultHeaders.put("Accept-Charset", "utf-8");
    }

    @Override
    public CompletableFuture<PreferenceNode> create(PreferenceNode node) {
        return sendRequest(prefService.create(node.getName(),
                node.getKey(),
                node.getValue(),
                defaultHeaders));
    }

    @Override
    public CompletableFuture<Optional<PreferenceNode>> update(PreferenceNode node) {
        return sendRequest(prefService.update(node.getName(),
                node.getKey(),
                node.getValue(),
                defaultHeaders))
                .thenApply(Optional::ofNullable);
    }

    @Override
    public CompletableFuture<Void> delete(PreferenceNode node) {
        return sendRequest(prefService.delete(node.getName(), node.getKey()));
    }

    @Override
    public CompletableFuture<List<PreferenceNode>> findByName(String nodeName) {
        return sendRequest(prefService.findByName(nodeName));
    }

    @Override
    public CompletableFuture<List<PreferenceNode>> findByNameLike(String nodeName) {
        return sendRequest(prefService.findByNameLike(nodeName));
    }

    @Override
    public CompletableFuture<Optional<PreferenceNode>> findByNameAndKey(String nodeName, String key) {
        return sendRequest(prefService.findByNameAndKey(nodeName, key))
                .thenApply(Optional::ofNullable);
    }


}
