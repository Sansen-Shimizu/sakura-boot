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

package org.sansenshimizu.sakuraboot.test.file.api;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.file.api.persistence.File;
import org.sansenshimizu.sakuraboot.test.BasicTest;

/**
 * This interface provides common methods for testing file services and
 * controllers.
 *
 * @param  <E> The {@link DataPresentation} type.
 * @param  <I> The ID of type Comparable and Serializable.
 * @author     Malcolm Rozé
 * @see        BasicTest
 * @since      0.1.2
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface SuperFileTest<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable> extends BasicTest<E, I> {

    /**
     * Get all the file field names.
     *
     * @return A list of file field names.
     */
    default List<String> getFileFieldNames() {

        return Arrays.stream(getUtil().getEntityClass().getDeclaredFields())
            .filter(field -> field.getType() == File.class)
            .map(Field::getName)
            .toList();
    }

    /**
     * Get an invalid file field name.
     *
     * @return A file field name.
     */
    default String getInvalidFileFieldName() {

        return "invalidFileFieldName";
    }
}
