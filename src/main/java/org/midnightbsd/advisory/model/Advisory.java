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
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import javax.persistence.*;

import lombok.*;

/** @author Lucas Holt */
@Entity
@Table(name = "advisory")
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Advisory implements Serializable {

  @JsonIgnore private static final long serialVersionUID = -2412883956384879806L;

  @Id
  @SequenceGenerator(name = "advisory_id_seq", sequenceName = "advisory_id_seq", allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "advisory_id_seq")
  @Column(name = "id", updatable = false)
  private int id;

  @Column(name = "cve_id", nullable = false)
  private String cveId;

  @Column(name = "description", columnDefinition = "TEXT", length = 65616)
  private String description;

  @Column(name = "published_date")
  @Temporal(value = TemporalType.DATE)
  private Date publishedDate;

  @Column(name = "last_modified_date")
  @Temporal(value = TemporalType.DATE)
  private Date lastModifiedDate;

  @Column(name = "severity", nullable = false)
  private String severity;

  @Column(name = "problem_type", length = 100)
  private String problemType;

  @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinTable(
      name = "advisory_package_fixed_map",
      joinColumns = @JoinColumn(name = "advisory_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "package_fixed_id", referencedColumnName = "id"))
  private Set<PackageFixed> fixedPackages;

  // product save bug might be the cascasde type.  try cascade = { CascadeType.MERGE,
  // CascadeType.REMOVE,
  //                      CascadeType.REFRESH, CascadeType.DETACH },
  @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinTable(
      name = "advisory_product_map",
      joinColumns = @JoinColumn(name = "advisory_id", referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"))
  private Set<Product> products;

  @JsonIgnore
  @OneToMany(mappedBy = "advisory")
  private Set<ConfigNode> configNodes;

  @OneToMany(mappedBy = "advisory")
  private Set<CvssMetrics3> cvssMetrics3;
}
