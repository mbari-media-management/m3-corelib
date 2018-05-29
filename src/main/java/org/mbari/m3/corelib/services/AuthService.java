package org.mbari.m3.corelib.services;

import org.mbari.m3.corelib.model.Authorization;

import java.util.Optional;

/**
 * @author Brian Schlining
 * @since 2017-05-24T08:49:00
 */
public interface AuthService {

    Optional<Authorization> authorize();

}
