/*
 * Copyright (c) 2017-2023 Lucas Holt
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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Persisted state for an MCP streaming package-vulnerability scan, allowing clients to poll or
 * resume a long running scan of an operating system's installed packages.
 *
 * @author Lucas Holt
 */
@Entity
@Table(name = "mcp_scan_session")
@Getter
@Setter
@NoArgsConstructor
public class McpScanSession implements Serializable {

  @Serial private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "id", updatable = false, length = 36)
  private String id;

  @Column(name = "created_date", nullable = false)
  private Instant createdDate;

  @Column(name = "updated_date")
  private Instant updatedDate;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 20)
  private McpScanStatus status;

  @Column(name = "total_items", nullable = false)
  private int totalItems;

  @Column(name = "processed_items", nullable = false)
  private int processedItems;

  @Column(name = "vulnerable_count", nullable = false)
  private int vulnerableCount;

  @Column(name = "client_info", length = 255)
  private String clientInfo;

  @Column(name = "error", length = 1000)
  private String error;
}