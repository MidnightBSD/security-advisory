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
package org.midnightbsd.advisory.dto;

import java.time.Instant;
import org.midnightbsd.advisory.model.McpScanSession;

/** Public view of an {@link McpScanSession} for polling streamed scan progress. */
public record McpScanSessionDto(
    String id,
    String status,
    int totalItems,
    int processedItems,
    int vulnerableCount,
    Instant createdDate,
    Instant updatedDate,
    String error) {

  public static McpScanSessionDto from(McpScanSession session) {
    return new McpScanSessionDto(
        session.getId(),
        session.getStatus() != null ? session.getStatus().name() : null,
        session.getTotalItems(),
        session.getProcessedItems(),
        session.getVulnerableCount(),
        session.getCreatedDate(),
        session.getUpdatedDate(),
        session.getError());
  }
}