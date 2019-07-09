package org.mbari.m3.corelib;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.name.Names;
import com.typesafe.config.Config;

import org.mbari.m3.corelib.model.Authorization;
import org.mbari.m3.corelib.services.*;
import org.mbari.m3.corelib.services.annosaurus.v1.AnnoService;
import org.mbari.m3.corelib.services.annosaurus.v1.AnnoWebServiceFactory;
import org.mbari.m3.corelib.services.panoptes.v1.PanoptesService;
import org.mbari.m3.corelib.services.panoptes.v1.PanoptesWebServiceFactory;
import org.mbari.m3.corelib.services.vampiresquid.v1.VamService;
import org.mbari.m3.corelib.services.vampiresquid.v1.VamWebServiceFactory;
import org.mbari.m3.corelib.services.varskbserver.v1.KBConceptService;
import org.mbari.m3.corelib.services.varskbserver.v1.KBWebServiceFactory;
import org.mbari.m3.corelib.services.varsuserserver.v1.*;
import org.mbari.m3.corelib.util.PreferencesFactory;
import org.mbari.m3.corelib.util.WebPreferencesFactory;

import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

/**
 * @author Brian Schlining
 * @since 2017-05-11T16:00:00
 */
public class MBARIInjectorModule implements Module {

    private final Config config;
    private final Executor defaultExecutor = new ForkJoinPool();

    public MBARIInjectorModule() {
        this.config = Initializer.getConfig();
    }

    @Override
    public void configure(Binder binder) {
        configureAnnotationService(binder);
        configureAnnotationServiceV2(binder);
        configureMediaService(binder);
        configureConceptService(binder);
        configurePrefsServices(binder);
        configureUserServices(binder);
        configurePanoptes(binder);
    }

    private void configureAnnotationService(Binder binder) {
        String endpoint = config.getString("annotation.service.url");
        String clientSecret = config.getString("annotation.service.client.secret");
        Duration timeout = config.getDuration("annotation.service.timeout");
        AnnoWebServiceFactory factory = new AnnoWebServiceFactory(endpoint, timeout);
        AuthService authService = new BasicJWTAuthService(factory,
                new Authorization("APIKEY", clientSecret));
        binder.bind(String.class)
                .annotatedWith(Names.named("ANNO_ENDPOINT"))
                .toInstance(endpoint);
        binder.bind(AuthService.class)
                .annotatedWith(Names.named("ANNO_AUTH"))
                .toInstance(authService);
        binder.bind(AnnoWebServiceFactory.class).toInstance(factory);
        binder.bind(AnnotationService.class).to(AnnoService.class);
    }

    private void configureAnnotationServiceV2(Binder binder) {
        String endpoint = config.getString("annotation.service.v2.url");
        String clientSecret = config.getString("annotation.service.client.secret");
        Duration timeout = config.getDuration("annotation.service.timeout");
        org.mbari.m3.corelib.services.annosaurus.v2.AnnoWebServiceFactory factory =
                new org.mbari.m3.corelib.services.annosaurus.v2.AnnoWebServiceFactory(endpoint, timeout);
        AuthService authService = new BasicJWTAuthService(factory,
                new Authorization("APIKEY", clientSecret));
        binder.bind(String.class)
                .annotatedWith(Names.named("ANNO_ENDPOINT_V2"))
                .toInstance(endpoint);
        // THis is not needed as its configure in `configureAnnotationService`
//        binder.bind(AuthService.class)
//                .annotatedWith(Names.named("ANNO_AUTH"))
//                .toInstance(authService);
        binder.bind(org.mbari.m3.corelib.services.annosaurus.v2.AnnoWebServiceFactory.class)
                .toInstance(factory);
        binder.bind(AnnotationServiceV2.class)
                .to(org.mbari.m3.corelib.services.annosaurus.v2.AnnoService.class);
    }

    private void configureMediaService(Binder binder) {
        String endpoint = config.getString("media.service.url");
        String clientSecret = config.getString("media.service.client.secret");
        VamWebServiceFactory factory = new VamWebServiceFactory(endpoint);
        AuthService authService = new BasicJWTAuthService(factory,
                new Authorization("APIKEY", clientSecret));
        binder.bind(String.class)
                .annotatedWith(Names.named("MEDIA_ENDPOINT"))
                .toInstance(endpoint);
        binder.bind(AuthService.class)
                .annotatedWith(Names.named("MEDIA_AUTH"))
                .toInstance(authService);
        binder.bind(VamWebServiceFactory.class).toInstance(factory);
        binder.bind(MediaService.class).to(VamService.class);
    }

    private void configureConceptService(Binder binder) {
        String endpoint = config.getString("concept.service.url");
        Duration timeout = config.getDuration("concept.service.timeout");
        KBWebServiceFactory factory = new KBWebServiceFactory(endpoint, timeout, defaultExecutor);
        KBConceptService service = new KBConceptService(factory);
        // --- Using a local cache
        CachedConceptService cachedService = new CachedConceptService(service);

        binder.bind(String.class)
                .annotatedWith(Names.named("CONCEPT_ENDPOINT"))
                .toInstance(endpoint);
        binder.bind(KBWebServiceFactory.class).toInstance(factory);
        binder.bind(ConceptService.class).toInstance(cachedService);
        //binder.bind(ConceptService.class).toInstance(service);
    }

    private void configurePrefsServices(Binder binder) {
        String endpoint = config.getString("preferences.service.url");
        String clientSecret = config.getString("preferences.service.client.secret");
        Duration timeout = config.getDuration("preferences.service.timeout");
        PrefWebServiceFactory factory = new PrefWebServiceFactory(endpoint, timeout);
        RetrofitServiceFactory authFactory = new BasicJWTAuthServiceFactorySC(endpoint, timeout);
        AuthService authService = new BasicJWTAuthService(authFactory,
                new Authorization("APIKEY", clientSecret));
        KBPrefService preferencesService = new KBPrefService(factory, authService);
        binder.bind(Long.class)
                .annotatedWith(Names.named("PREFS_TIMEOUT"))
                .toInstance(timeout.toMillis());
        binder.bind(PreferencesService.class).toInstance(preferencesService);
        binder.bind(KBPrefService.class).toInstance(preferencesService);
        binder.bind(PreferencesFactory.class).to(WebPreferencesFactory.class);
    }

    private void configureUserServices(Binder binder) {
        String endpoint = config.getString("accounts.service.url");
        String clientSecret = config.getString("accounts.service.client.secret");
        Duration timeout = config.getDuration("accounts.service.timeout");
        UserWebServiceFactory factory = new UserWebServiceFactory(endpoint, timeout);
        RetrofitServiceFactory authFactory = new BasicJWTAuthServiceFactorySC(endpoint, timeout);
        AuthService authService = new BasicJWTAuthService(authFactory,
                new Authorization("APIKEY", clientSecret));
        KBUserService userService = new KBUserService(factory, authService);
        binder.bind(Long.class)
                .annotatedWith(Names.named("ACCOUNTS_TIMEOUT"))
                .toInstance(timeout.toMillis());
        binder.bind(UserService.class).toInstance(userService);
        binder.bind(KBUserService.class).toInstance(userService);
    }

    private void configurePanoptes(Binder binder) {
        String endpoint = config.getString("panoptes.service.url");
        String clientSecret = config.getString("panoptes.service.client.secret");
        Duration timeout = config.getDuration("panoptes.service.timeout");
        PanoptesWebServiceFactory factory = new PanoptesWebServiceFactory(endpoint, timeout);
        RetrofitServiceFactory authFactory = new BasicJWTAuthServiceFactorySC(endpoint, timeout);
        AuthService authService = new BasicJWTAuthService(authFactory,
                new Authorization("APIKEY", clientSecret));
        binder.bind(String.class)
                .annotatedWith(Names.named("PANOPTES_ENDPOINT"))
                .toInstance(endpoint);
        binder.bind(AuthService.class)
                .annotatedWith(Names.named("PANOPTES_AUTH"))
                .toInstance(authService);
        PanoptesService service = new PanoptesService(factory, authService);
        binder.bind(Long.class)
                .annotatedWith(Names.named("PANOPTES_TIMEOUT"))
                .toInstance(timeout.toMillis());
        binder.bind(PanoptesWebServiceFactory.class).toInstance(factory);
        binder.bind(ImageArchiveService.class).toInstance(service);

    }

}
