package com.stratosim.server.persistence.objectify.entity;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;

@Entity(name = "circuitSvg")
@Cache(expirationSeconds = 0)  // no expiration
public class CircuitSvg extends AbstractDownloadFormatEntity {
}
