package org.mbari.m3.corelib.services;

import org.mbari.m3.corelib.model.Authorization;
import org.mbari.m3.corelib.services.AuthService;

import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2017-05-24T10:21:00
 */
public class NoopAuthService implements AuthService {


    @Override
    public Optional<Authorization> authorize() {
        return Optional.empty();
    }
}
