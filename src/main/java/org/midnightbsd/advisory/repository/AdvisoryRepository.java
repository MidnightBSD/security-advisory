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
package org.midnightbsd.advisory.repository;


import java.util.Date;
import java.util.List;
import org.midnightbsd.advisory.model.Advisory;
import org.midnightbsd.advisory.model.Product;
import org.midnightbsd.advisory.model.search.NvdItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** @author Lucas Holt */
public interface AdvisoryRepository extends JpaRepository<Advisory, Integer> {

  Advisory findOneByCveId(@Param("cveId") String cveId);

  List<Advisory> findByPublishedDateBetween(Date startDate, Date endDate);

  List<Advisory> findByLastModifiedDateBetween(Date startDate, Date endDate);

  Page<Advisory> findByLastModifiedDateBetween(Date startDate, Date endDate, Pageable page);

  Advisory findFirstByOrderByLastModifiedDateDesc();

  @Query(
      value =
          "SELECT distinct a FROM Advisory a JOIN a.products p JOIN p.vendor v WHERE p.name ="
              + " :productName ORDER BY a.cveId")
  List<Advisory> findByProductName(@Param("productName") String productName);

  @Query(value = "SELECT distinct a FROM Advisory a JOIN a.products p WHERE p in (:products) ORDER BY a.cveId")
  List<Advisory> findByProductsIn(@Param("products") List<Product> products);


  @Query(value = "SELECT distinct a FROM Advisory a JOIN a.products p WHERE a.publishedDate >= :startDate and p in (:products) ORDER BY a.cveId")
  List<Advisory> findByPublishedDateIsAfterProductsIn(@Param("startDate") Date startDate, @Param("products") List<Product> products);

  @Query(
      value =
          "SELECT distinct a FROM Advisory a INNER JOIN a.products p INNER JOIN p.vendor v WHERE"
              + " v.name = :vendorName ORDER BY a.cveId")
  List<Advisory> findByVendorName(@Param("vendorName") String vendorName);

  @Query(
      value =
          "SELECT distinct a FROM Advisory a JOIN a.products p JOIN p.vendor v WHERE v.name ="
              + " :vendorName and p.name like :productName ORDER BY a.cveId")
  List<Advisory> findByVendorNameAndProductsIsLike(
      @Param("vendorName") String vendorName, @Param("productName") String productName);
}
