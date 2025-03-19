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
package org.midnightbsd.advisory.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serial;
import java.io.Serializable;
import jakarta.persistence.*;

import lombok.*;

/** @author Lucas Holt */
@Entity
@Table(name = "config_node_cpe")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigNodeCpe implements Serializable {

  @Serial
  @JsonIgnore
  private static final long serialVersionUID = -26545549510501502L;

  @Id
  @SequenceGenerator(
      name = "config_node_cpe_id_seq",
      sequenceName = "config_node_cpe_id_seq",
      allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "config_node_cpe_id_seq")
  @Column(name = "id", updatable = false)
  private int id;

  @Column(name = "vulnerable")
  private Boolean vulnerable;

  @Column(name = "cpe23Uri", length = 300)
  private String cpe23Uri;

  @Column(name = "match_criteria_id")
  private String matchCriteriaId;

  @Column(name = "version_end_excluding")
  private String versionEndExcluding;

  @Column(name = "version_end_including")
  private String versionEndIncluding;

  @Column(name = "version_start_excluding")
  private String versionStartExcluding;

  @Column(name = "version_start_including")
  private String versionStartIncluding;

  @ManyToOne
  @JoinColumn(name = "config_node_id")
  private ConfigNode configNode;
}
