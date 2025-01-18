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

package org.sansenshimizu.sakuraboot.bulk.api.presentation.controllers;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.groups.ConvertGroup;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.SuperController;
import org.sansenshimizu.sakuraboot.bulk.api.business.services.SaveAllService;
import org.sansenshimizu.sakuraboot.hypermedia.api.annotations.ApplyHypermediaOnCollection;
import org.sansenshimizu.sakuraboot.log.api.annotations.Logging;
import org.sansenshimizu.sakuraboot.openapi.api.annotations.SwaggerBasicApiResponse;
import org.sansenshimizu.sakuraboot.openapi.api.annotations.SwaggerCreateApiResponse;
import org.sansenshimizu.sakuraboot.openapi.api.annotations.SwaggerUpdateOperation;

/**
 * The base controller interface for saveAll operation.
 * <p>
 * <b>Example:</b>
 * </p>
 * <p>
 * To create a controller interface that inherits from
 * {@link SaveAllController}, follow these steps:
 * </p>
 * <p>
 * Create a new controller interface:
 * </p>
 * <blockquote>
 * 
 * <pre>
 * public interface YourController
 *     extends //
 *     SaveAllController&lt;YourEntity, YourIdType, YourDataPresentation&gt; {
 *     // Add your methods signature here
 * }
 * </pre>
 * 
 * </blockquote>
 * <p>
 * To create a controller class that implements {@link SaveAllController},
 * follow these steps:
 * </p>
 * <p>
 * Create a new controller class:
 * </p>
 * <blockquote>
 * 
 * <pre>
 * &#064;RestController
 * &#064;RequestMapping("/yourPath")
 * public class YourController
 *     implements //
 *     SaveAllController&lt;YourEntity, YourIdType, YourDataPresentation&gt; {
 * 
 *     // Or implements your interface that extends SaveController.
 *     private final YourService service;
 * 
 *     public YourController(final YourService service) {
 * 
 *         this.service = service;
 *     }
 * 
 *     public YourService getService() {
 * 
 *         return this.service;
 *     }
 * }
 * </pre>
 * 
 * </blockquote>
 *
 * @param  <E> The {@link DataPresentation} type use in the service layer.
 * @param  <I> The ID of type Comparable and Serializable.
 * @param  <D> The {@link DataPresentation} type use to map the given JSON to an
 *             entity or DTO.
 * @author     Malcolm Rozé
 * @see        SaveAllController#getService()
 * @see        SaveAllController#saveAll(Collection)
 * @since      0.1.2
 */
@SuppressWarnings("InterfaceMayBeAnnotatedFunctional")
public interface SaveAllController<E extends DataPresentation<I>,
    I extends Comparable<? super I> & Serializable,
    D extends DataPresentation<I>> extends SuperController<E, I> {

    @Override
    SaveAllService<E, I> getService();

    /**
     * Save all the provided {@link DataPresentation}.
     *
     * @param  datas All the {@link DataPresentation} to be created. Must be a
     *               valid and complete representation of the
     *               {@link DataPresentation}.
     * @return       A ResponseEntity containing all the created
     *               {@link DataPresentation} and the HTTP status code 201
     *               (Created) on
     *               success. The response also includes a "Location" header
     *               with the URI
     *               of the newly created resource. If one of the provided
     *               {@link DataPresentation} is invalid or contains incomplete
     *               information, or if it already has an ID, the response will
     *               have HTTP
     *               status code 400 (Bad Request).
     */
    @SwaggerCreateApiResponse("Create all entities")
    @SwaggerBasicApiResponse
    @SwaggerUpdateOperation
    @PostMapping(
        value = "/bulk",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ApplyHypermediaOnCollection
    @Logging
    @SuppressWarnings("java:S1452")
    default ResponseEntity<List<?>> saveAll(
        @RequestBody final Collection<@Valid @ConvertGroup(
            to = DataPresentation.FullData.class) D> datas) {

        @SuppressWarnings("unchecked")
        final Collection<DataPresentation<I>> castDatas
            = (Collection<DataPresentation<I>>) datas;
        final List<DataPresentation<I>> newDatas
            = getService().saveAll(castDatas);

        return ResponseEntity.created(
            MvcUriComponentsBuilder.fromController(getClass()).build().toUri())
            .body(newDatas);
    }
}