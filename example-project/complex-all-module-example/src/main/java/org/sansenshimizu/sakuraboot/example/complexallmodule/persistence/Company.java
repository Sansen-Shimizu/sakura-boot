/*
 * Copyright (C) 2023-2024 Malcolm Rozé.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sansenshimizu.sakuraboot.example.complexallmodule.persistence;

import java.io.Serial;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.basic.persistence.AbstractBasicEntity;
import org.sansenshimizu.sakuraboot.file.api.persistence.File;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Company extends AbstractBasicEntity<UUID> {

    @Serial
    private static final long serialVersionUID = 8075672169790602082L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    @Nullable
    private UUID id;

    @Embedded
    @Nullable
    private Address address;

    @Nullable
    private String name;

    @Embedded
    @AttributeOverride(name = "bytes", column = @Column(name = "logo_bytes"))
    @AttributeOverride(
        name = "filename",
        column = @Column(name = "logo_filename"))
    @AttributeOverride(
        name = "contentType",
        column = @Column(name = "logo_content_type"))
    @Nullable
    private File logo;

    @Embedded
    @AttributeOverride(name = "bytes", column = @Column(name = "banner_bytes"))
    @AttributeOverride(
        name = "filename",
        column = @Column(name = "banner_filename"))
    @AttributeOverride(
        name = "contentType",
        column = @Column(name = "banner_content_type"))
    @Nullable
    private File banner;

    @Nullable
    private LocalDate createdDate;
}
