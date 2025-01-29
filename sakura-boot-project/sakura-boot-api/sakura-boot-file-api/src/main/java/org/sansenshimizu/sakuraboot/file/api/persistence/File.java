/*
 * Copyright (C) 2023-2025 Malcolm Rozé.
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

package org.sansenshimizu.sakuraboot.file.api.persistence;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Types;
import java.util.List;

import jakarta.persistence.Embeddable;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.annotations.JdbcTypeCode;
import org.springframework.lang.Nullable;

import org.sansenshimizu.sakuraboot.util.ToStringUtils;

/**
 * The base class for files.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create and use a {@link File} in one of your entity class, follow
 * these steps:
 * </p>
 * <p>
 * Add a {@link File} class in you entity class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;Entity
 * &#064;Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
 * public class YourEntity extends AbstractBasicEntity&lt;YourIdType&gt; {
 *
 *     &#064;Id
 *     &#064;GeneratedValue(strategy = GenerationType.AUTO)
 *     &#064;Column(nullable = false)
 *     private Long id;
 *
 *     &#064;Embedded
 *     private BasicFile file;
 *
 *     // To change the inner properties name in the database
 *     &#064;Embedded
 *     &#064;AttributeOverride(
 *         name = "bytes",
 *         column = @Column(name = "other_bytes"))
 *     &#064;AttributeOverride(
 *         name = "filename",
 *         column = @Column(name = "other_filename"))
 *     private BasicFile otherFile;
 *
 *     YourEntity() {}
 *     // Need an empty package-private constructor for JPA.
 *     // Add your fields and getter method here ...
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @author Malcolm Rozé
 * @since  0.1.2
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class File implements Serializable {

    @Serial
    private static final long serialVersionUID = -8750058125414972233L;

    /**
     * The bytes of the file.
     */
    @JdbcTypeCode(Types.BINARY)
    @Nullable
    @EqualsAndHashCode.Exclude
    private byte[] bytes;

    /**
     * The filename of the file.
     */
    @Nullable
    private String filename;

    /**
     * The content type of the file.
     */
    @Nullable
    private String contentType;

    @Override
    public String toString() {

        return ToStringUtils.toString("File",
            List.of(Pair.of("filename", filename)));
    }
}
