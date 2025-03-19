/*
 * Copyright (c) 2017-2021 Lucas Holt
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */
package org.midnightbsd.advisory.services;

import java.util.*;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.midnightbsd.advisory.model.ConfigNodeCpe;
import org.midnightbsd.advisory.model.search.CvssMetric;
import org.midnightbsd.advisory.model.search.Instance;
import org.midnightbsd.advisory.model.search.NvdItem;
import org.midnightbsd.advisory.repository.AdvisoryRepository;
import org.midnightbsd.advisory.repository.search.NvdSearchRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.springett.parsers.cpe.Cpe;
import us.springett.parsers.cpe.CpeParser;

/** @author Lucas Holt */
@Slf4j
@Service
public class SearchService {

  private final NvdSearchRepository nvdSearchRepository;

  private final AdvisoryRepository advisoryRepository;

  public SearchService(NvdSearchRepository nvdSearchRepository, AdvisoryRepository advisoryRepository) {
    this.nvdSearchRepository = nvdSearchRepository;
    this.advisoryRepository = advisoryRepository;
  }

  public Page<NvdItem> find(String term, Pageable page) {
    return nvdSearchRepository.findByCveIdContainsOrDescriptionContainsAllIgnoreCase(
        term, term, page);
  }

  @CacheEvict(value = "search", allEntries = true)
  @Transactional
  @Async
  public void indexAllNvdItems() {
    try {
      Pageable pageable = PageRequest.of(0, 100);

      Page<org.midnightbsd.advisory.model.Advisory> advisories =
          advisoryRepository.findAll(pageable);
      for (int i = 0; i < advisories.getTotalPages(); i++) {
        final ArrayList<NvdItem> items = new ArrayList<>();

        for (final org.midnightbsd.advisory.model.Advisory adv : advisories) {
          items.add(convert(adv));
        }

        log.debug("Saving a page of advisories to elasticsearch. pg {}", i);
        nvdSearchRepository.saveAll(items);

        pageable = PageRequest.of(i + 1, 100);
        advisories = advisoryRepository.findAll(pageable);
      }
    } catch (final Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  @CacheEvict(value = "search", allEntries = true)
  @Transactional
  @Async
  public void indexRecentEntries(Date since) {
    try {
      Pageable pageable = PageRequest.of(0, 100);
      Date endDate = Calendar.getInstance().getTime();

      Page<org.midnightbsd.advisory.model.Advisory> advisories =
              advisoryRepository.findByLastModifiedDateBetween(since, endDate, pageable);
      for (int i = 0; i < advisories.getTotalPages(); i++) {
        final ArrayList<NvdItem> items = new ArrayList<>();

        for (final org.midnightbsd.advisory.model.Advisory adv : advisories) {
          items.add(convert(adv));
        }

        log.debug("Saving a page of advisories to elasticsearch. pg {}", i);
        nvdSearchRepository.saveAll(items);

        pageable = PageRequest.of(i + 1, 100);
        advisories = advisoryRepository.findByLastModifiedDateBetween(since, endDate, pageable);
      }
    } catch (final Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  @CacheEvict(value = "search", allEntries = true)
  @Transactional
  public void index(@NonNull final org.midnightbsd.advisory.model.Advisory adv) {
    log.info("Indexing advisory {} id: {}", adv.getCveId(), adv.getId());
    nvdSearchRepository.save(convert(adv));
  }

  public NvdItem convert(@NonNull final org.midnightbsd.advisory.model.Advisory adv) {
    log.trace("Converting advisory {} id: {}", adv.getCveId(), adv.getId());

    final NvdItem nvdItem = new NvdItem();

    nvdItem.setId(adv.getId());

    nvdItem.setCveId(adv.getCveId());
    nvdItem.setDescription(adv.getDescription());
    nvdItem.setVersion(Calendar.getInstance().getTimeInMillis());
    nvdItem.setPublishedDate(adv.getPublishedDate());
    nvdItem.setLastModifiedDate(adv.getLastModifiedDate());

    List<CvssMetric> metrics = new ArrayList<>();
    if (adv.getCvssMetrics3() != null) {
      for (var metric : adv.getCvssMetrics3()) {
        CvssMetric metrics3 = new CvssMetric();

        metrics3.setSource(metric.getSource());
        metrics3.setType(metric.getType());
        metrics3.setExploitabilityScore(metric.getExploitabilityScore());
        metrics3.setImpactScore(metric.getImpactScore());

        metrics3.setAccessComplexity(metric.getAccessComplexity());
        metrics3.setAccessVector(metric.getAccessVector());
        metrics3.setAuthentication(metric.getAuthentication());
        metrics3.setAvailabilityImpact(metric.getAvailabilityImpact());
        metrics3.setConfidentialityImpact(metric.getConfidentialityImpact());
        metrics3.setIntegrityImpact(metric.getIntegrityImpact());
        metrics3.setAttackVector(metric.getAttackVector());
        metrics3.setVersion(metric.getVersion());
        metrics3.setBaseScore(metric.getBaseScore());
        metrics3.setBaseSeverity(metric.getBaseSeverity());
        metrics3.setScope(metric.getScope());
        metrics3.setVectorString(metric.getVectorString());
        metrics3.setUserInteraction(metric.getUserInteraction());
        metrics3.setAttackComplexity(metric.getAttackComplexity());
        metrics3.setPrivilegesRequired(metrics3.getPrivilegesRequired());

        metrics.add(metrics3);
      }
    }
    nvdItem.setCvssMetrics3(metrics);

    final List<Instance> instances = new ArrayList<>();
    if (adv.getConfigNodes() != null) {
      for (final org.midnightbsd.advisory.model.ConfigNode node : adv.getConfigNodes()) {
        for (var configNodeCpe : node.getConfigNodeCpes()) {
          try {
            Cpe parsed = CpeParser.parse(configNodeCpe.getCpe23Uri());
            final Instance inst = getInstance(configNodeCpe, parsed);
            instances.add(inst);
          } catch (final Exception e) {
            log.error("Error parsing CPE: {}", configNodeCpe.getCpe23Uri(), e);
          }
        }
      }
    }

    nvdItem.setInstances(instances);

    return nvdItem;
  }

  private static Instance getInstance(ConfigNodeCpe configNodeCpe, Cpe parsed) {
    final Instance inst = new Instance();
    inst.setVendor(parsed.getVendor());
    inst.setProduct(parsed.getProduct());
    inst.setVersion(parsed.getVersion());
    inst.setVersionEndExcluding(configNodeCpe.getVersionEndExcluding());
    inst.setVersionEndIncluding(configNodeCpe.getVersionEndIncluding());
    inst.setVersionStartExcluding(configNodeCpe.getVersionStartExcluding());
    inst.setVersionStartIncluding(configNodeCpe.getVersionStartIncluding());
    inst.setVulnerable(configNodeCpe.getVulnerable());
    return inst;
  }
}
