package org.mbari.m3.corelib.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import org.mbari.m3.corelib.model.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Configures and interceptor that handles the Basic JWT handshake using the Authservice
 * you provide. This may make a request to the server for authentication if a
 * JWT token is missing or if it's expired. No Auth is applied to GET requests
 *
 * @author Brian Schlining
 * @since 2017-05-23T15:45:00
 */
public class BasicJWTAuthInterceptor implements Interceptor {

    private final AtomicReference<Authorization> authorization = new AtomicReference<>();

    private final AuthService authService;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public BasicJWTAuthInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        if (original.method().equalsIgnoreCase("GET")) {
            return chain.proceed(chain.request());
        }
        else {
            Authorization a = authorization.updateAndGet(this::reauthorize);
            Request request = original.newBuilder()
                    .header("Authorization", a.toString())
                    .method(original.method(), original.body())
                    .build();
            return chain.proceed(request);
        }

    }

    private Authorization reauthorize(Authorization a) {
        if ((a == null) || isExpired(a)) {
            log.debug("Reauthorizing using {}", authService);
            a = authService.authorize()
                    .orElseGet(() -> {
                        // TODO handle error
                        return new Authorization("ERROR", "key not found");
                    });
        }
        return a;
    }

    private boolean isExpired(Authorization a) {
        try {
            DecodedJWT jwt = JWT.decode(a.getAccessToken());
            Instant iat = jwt.getExpiresAt().toInstant();
            return iat.isBefore(Instant.now());
        }
        catch (Exception e) {
            return true;
        }
    }
}
