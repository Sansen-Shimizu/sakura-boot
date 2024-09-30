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

package org.sansenshimizu.sakuraboot.hypermedia;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.BasicLinkBuilder;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.util.ToStringUtils;

/**
 * The abstract base class for all model classes used in API responses with
 * HATEOAS support.
 * This class extends {@link RepresentationModel} and provides common fields and
 * methods for model classes.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a model that inherits from {@link AbstractBasicModel}, follow these
 * steps:
 * </p>
 * <p>
 * Create a new model class:
 * </p>
 * <blockquote>
 *
 * <pre>
 * &#064;Relation(collectionRelation = "YourEntityNameForCollection")
 * public class YourModel extends AbstractBasicModel&lt;YourData&gt; {
 *
 *     public YourModel(YourData data) {
 *
 *         super(data);
 *     }
 * }
 * </pre>
 *
 * </blockquote>
 *
 * @param  <D> The {@link DataPresentation} type.
 * @author     Malcolm Rozé
 * @see        AbstractBasicModel#addLink(String, LinkRelation)
 * @see        AbstractBasicModel#addLink(String, String)
 * @since      0.1.0
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public abstract class AbstractBasicModel<D extends DataPresentation<?>>
    extends RepresentationModel<AbstractBasicModel<D>>
    implements Serializable {

    @Serial
    private static final long serialVersionUID = -5858351260857594841L;

    /**
     * The data that is included in the model. {@code @JsonUnwrapped} is used to
     * only get the field inside the data.
     */
    @JsonUnwrapped
    private final D data;

    /**
     * Constructor to create a model based on the given {@link DataPresentation}
     * and add HATEOAS links to the created model.
     *
     * @param data The {@link DataPresentation} to be represented by the model.
     */
    protected AbstractBasicModel(final D data) {

        this.data = data;
    }

    private static String buildLink(final String path) {

        return BasicLinkBuilder.linkToCurrentMapping() + "/" + path;
    }

    /**
     * Add HATEOAS links to the model.
     *
     * @param path     The path use by the endpoint in the controller.
     * @param relation The {@link LinkRelation} use by this link.
     */
    public void addLink(final String path, final LinkRelation relation) {

        add(Link.of(buildLink(path), relation));
    }

    /**
     * Add HATEOAS links to the model.
     *
     * @param path     The path use by the endpoint in the controller.
     * @param relation The {@code String} relation use by this link.
     */
    public void addLink(final String path, final String relation) {

        add(Link.of(buildLink(path), relation));
    }

    @Override
    public String toString() {

        final List<Pair<String, Object>> list = new ArrayList<>();
        list.add(Pair.of("data", data));
        list.add(Pair.of("links", getLinks().toList()));

        return ToStringUtils.toString(getClass().getSimpleName(), list);
    }
}
