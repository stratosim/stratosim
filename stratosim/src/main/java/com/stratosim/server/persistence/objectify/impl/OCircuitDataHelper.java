package com.stratosim.server.persistence.objectify.impl;

import static com.stratosim.server.persistence.objectify.OfyService.stratoSimOfy;

import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.googlecode.objectify.Ref;
import com.stratosim.server.persistence.objectify.entity.AbstractDownloadFormatEntity;
import com.stratosim.server.persistence.objectify.entity.CircuitCsv;
import com.stratosim.server.persistence.objectify.entity.CircuitPdf;
import com.stratosim.server.persistence.objectify.entity.CircuitPng;
import com.stratosim.server.persistence.objectify.entity.CircuitPs;
import com.stratosim.server.persistence.objectify.entity.CircuitSimulationPdf;
import com.stratosim.server.persistence.objectify.entity.CircuitSimulationPng;
import com.stratosim.server.persistence.objectify.entity.CircuitSimulationPs;
import com.stratosim.server.persistence.objectify.entity.CircuitSpice;
import com.stratosim.server.persistence.objectify.entity.CircuitSvg;
import com.stratosim.server.persistence.objectify.entity.CircuitThumbnail;
import com.stratosim.shared.filemodel.CircuitHash;
import com.stratosim.shared.filemodel.DownloadFormat;

public final class OCircuitDataHelper {

  public static Ref<? extends AbstractDownloadFormatEntity> getCircuitBinaryData(
      DownloadFormat format, CircuitHash circuitHash) {
    return load(entityClass(format), circuitHash);
  }

  private static Class<? extends AbstractDownloadFormatEntity> entityClass(DownloadFormat format) {
    switch (format) {
      case PS:
        return CircuitPs.class;
      case PDF:
        return CircuitPdf.class;
      case PNG:
        return CircuitPng.class;
      case SVG:
        return CircuitSvg.class;
      case SPICE:
        return CircuitSpice.class;
      case THUMBNAIL:
        return CircuitThumbnail.class;
      case CSV:
        return CircuitCsv.class;
      case SIMULATIONPS:
        return CircuitSimulationPs.class;
      case SIMULATIONPDF:
        return CircuitSimulationPdf.class;
      case SIMULATIONPNG:
        return CircuitSimulationPng.class;
      default:
        throw new IllegalArgumentException("Unconfigured DownloadFormat: " + format);
    }
  }
  
  private static <T extends AbstractDownloadFormatEntity> Ref<T> load(Class<T> clazz,
      CircuitHash circuitHash) {
    return stratoSimOfy().load().type(clazz).id(circuitHash.get());
  }

  private static <T extends AbstractDownloadFormatEntity> Map<String, T> loadAll(Class<T> clazz,
      ImmutableSet<CircuitHash> circuitHashes) {
    return stratoSimOfy().load().type(clazz).ids(FluentIterable.from(circuitHashes)
        .transform(new Function<CircuitHash, String>() {
          @Override
          public String apply(CircuitHash circuitHash) {
            return circuitHash.get();
          }
        }));
  }
}
