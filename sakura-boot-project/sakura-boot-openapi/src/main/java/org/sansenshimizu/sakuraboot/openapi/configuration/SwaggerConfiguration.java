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

package org.sansenshimizu.sakuraboot.openapi.configuration;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.links.Link;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;

import org.sansenshimizu.sakuraboot.DataPresentation;
import org.sansenshimizu.sakuraboot.hypermedia.api.Hypermedia;
import org.sansenshimizu.sakuraboot.openapi.api.annotations.SwaggerUpdateOperation;
import org.sansenshimizu.sakuraboot.relationship.one.DataPresentation1RelationshipAnyToMany;
import org.sansenshimizu.sakuraboot.relationship.one.DataPresentation1RelationshipAnyToOne;

/**
 * The class SwaggerConfiguration customizes the {@code OpenAPI} that can be
 * configured with {@link InformationConfiguration}.
 *
 * @author Malcolm Rozé
 * @see    InformationConfiguration
 * @since  0.1.0
 */
@EnableConfigurationProperties(InformationConfiguration.class)
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(SpringDocConfiguration.class)
@ConditionalOnWebApplication
@RequiredArgsConstructor
public class SwaggerConfiguration {

    /**
     * The constant for "findAll" operation.
     */
    private static final String FIND_ALL = "findAll";

    /**
     * The information configuration class.
     */
    private final InformationConfiguration informationConfiguration;

    /**
     * Update the {@code OpenAPI}.
     *
     * @return an {@link OpenApiCustomizer}.
     */
    @Bean
    @ConditionalOnMissingBean
    public OpenApiCustomizer openApiCustomizer() {

        return this::updateOpenApi;
    }

    private void updateOpenApi(final OpenAPI openApi) {

        addLinksComponent(openApi);
        openApi.info(getInfo());
    }

    private static void addLinksComponent(final OpenAPI openApi) {

        openApi.getPaths()
            .entrySet()
            .stream()
            .filter(e -> e.getValue().getGet() != null)
            .forEach(e -> addLinkToComponent(openApi, e));
    }

    private static void addLinkToComponent(
        final OpenAPI openApi, final Map.Entry<String, PathItem> e) {

        final Operation operation = e.getValue().getGet();
        final Link linksItem
            = new Link().operationId(operation.getOperationId());

        if (!operation.getOperationId().contains(FIND_ALL)) {

            linksItem.addParameter("id", "$response.body#/id");
        }
        final String key;

        if (e.getKey().contains("{")) {

            key = e.getKey().replace("/{id}", "_id");
        } else {

            key = e.getKey();
        }
        openApi.getComponents().addLinks(key.replace("/", ""), linksItem);
    }

    private Info getInfo() {

        final Info info = new Info().title(informationConfiguration.name())
            .version(informationConfiguration.version())
            .description(informationConfiguration.description());
        final InformationConfiguration.License license
            = informationConfiguration.license();

        if (license != null) {

            info.license(new License().name(license.name()).url(license.url()));
        }
        final InformationConfiguration.Contact contact
            = informationConfiguration.contact();

        if (contact != null) {

            info.contact(new Contact().name(contact.name())
                .email(contact.email())
                .url(contact.url()));
        }
        return info;
    }

    /**
     * Update the operations in {@code OpenAPI}.
     *
     * @return an {@link OperationCustomizer}.
     */
    @Bean
    @ConditionalOnMissingBean
    public OperationCustomizer operationCustomizer() {

        return SwaggerConfiguration::updateOperation;
    }

    private static Operation updateOperation(
        final Operation operation, final HandlerMethod handlerMethod) {

        if (handlerMethod.hasMethodAnnotation(SwaggerUpdateOperation.class)) {

            final Class<?> beanType = handlerMethod.getBeanType();

            /* @formatter:off */
            if (beanType.getGenericInterfaces().length != 0
                && beanType.getGenericInterfaces()[0]
                instanceof final ParameterizedType parameterizedType) {
                /* @formatter:on */
                final Type actualTypeArgument;

                if (parameterizedType.getActualTypeArguments().length > 2) {

                    actualTypeArgument
                        = parameterizedType.getActualTypeArguments()[2];
                } else {

                    actualTypeArgument
                        = parameterizedType.getActualTypeArguments()[0];
                }

                if (actualTypeArgument instanceof final Class<?> classType) {

                    updateResponseOperation(operation, handlerMethod, classType,
                        beanType);
                }
            }
        }
        return operation;
    }

    private static void updateResponseOperation(
        final Operation operation, final HandlerMethod handlerMethod,
        final Class<?> classType, final Class<?> beanType) {

        removeIdFromPostRequestBody(operation, classType);

        if (Hypermedia.class.isAssignableFrom(beanType)) {

            final Class<?> modelType = getModelType(handlerMethod);
            updateSchemaOnHypermediaResponse(operation, modelType, beanType,
                "200");
            updateSchemaOnHypermediaResponse(operation, modelType, beanType,
                "201");
        } else {

            updateSchemaOnResponse(operation, classType, "200");
            updateSchemaOnResponse(operation, classType, "201");
        }
    }

    private static void removeIdFromPostRequestBody(
        final Operation operation, final Class<?> classType) {

        if (operation.getOperationId().contains("save")) {

            final ResolvedSchema data
                = removeIdRecursively(classType, new ArrayList<>());
            operation.getRequestBody()
                .getContent()
                .get(MediaType.APPLICATION_JSON_VALUE)
                .setSchema(data.schema);
        }
    }

    private static ResolvedSchema removeIdRecursively(
        final Class<?> classType, final List<Class<?>> visited) {

        final ResolvedSchema data = getResolvedSchema(classType);
        data.schema.title(classType.getSimpleName());
        data.schema.getProperties().remove("id");

        if (visited.contains(classType)) {

            return data;
        }
        visited.add(classType);

        if (DataPresentation1RelationshipAnyToOne.class
            .isAssignableFrom(classType)
            || DataPresentation1RelationshipAnyToMany.class
                .isAssignableFrom(classType)) {

            for (final Field field: classType.getDeclaredFields()) {

                final Class<?> relationalClassType;

                if (DataPresentation.class.isAssignableFrom(field.getType())) {

                    relationalClassType = field.getType();
                    /* @formatter:off */
                } else if (Set.class.isAssignableFrom(field.getType())
                    && field.getGenericType() instanceof final
                    ParameterizedType parameterizedType
                    /* @formatter:on */
                    && parameterizedType
                        .getActualTypeArguments()[0] instanceof final Class<
                            ?> argumentClass
                    && DataPresentation.class.isAssignableFrom(argumentClass)) {

                    relationalClassType = argumentClass;
                } else {

                    continue;
                }

                @SuppressWarnings("unchecked")
                final Map<String, Schema<?>> properties
                    = data.schema.getProperties();
                properties.put(field.getName(),
                    removeIdRecursively(relationalClassType, visited).schema);
            }
        }
        return data;
    }

    private static void updateSchemaOnResponse(
        final Operation operation, final Class<?> classType,
        final String responseCode) {

        final Schema<?> schema = getSchema(operation, responseCode);

        if (schema == null) {

            return;
        }

        if (operation.getOperationId().contains(FIND_ALL)) {

            schema.set$ref(null);
            final ResolvedSchema page = getResolvedSchema(Page.class);
            page.schema
                .title(Page.class.getSimpleName() + classType.getSimpleName());

            @SuppressWarnings("unchecked")
            final Map<String, Schema<?>> properties
                = page.schema.getProperties();
            properties.put("content",
                new Schema<>().$ref(classType.getSimpleName()));
            schema.addAllOfItem(page.schema);
        } else {

            schema.set$ref(classType.getSimpleName());
        }
    }

    private static void updateSchemaOnHypermediaResponse(
        final Operation operation, final Class<?> classType,
        final Class<?> handlerType, final String responseCode) {

        final Schema<?> schema = getSchema(operation, responseCode);

        if (schema == null) {

            return;
        }
        final ResolvedSchema model
            = addDtoSchemaToModel(getResolvedSchema(classType), classType);
        model.schema.title(classType.getSimpleName());
        final String collectionString = "collection";

        if (operation.getOperationId().contains(FIND_ALL)) {

            schema.set$ref(null);
            final ResolvedSchema pagedModel
                = getResolvedSchema(PagedModel.class);
            pagedModel.schema.title(
                PagedModel.class.getSimpleName() + classType.getSimpleName());
            final ResolvedSchema pageMetadata
                = getResolvedSchema(PagedModel.PageMetadata.class);
            pageMetadata.schema
                .title(PagedModel.PageMetadata.class.getSimpleName());
            ((Schema<?>) pagedModel.schema.getProperties().get("_embedded"))
                .getProperties()
                .put("objectList", new ArraySchema().items(model.schema));

            @SuppressWarnings("unchecked")
            final Map<String, Schema<?>> properties
                = pagedModel.schema.getProperties();
            properties.put("page", pageMetadata.schema);
            schema.addAllOfItem(pagedModel.schema);

            final String linkPath
                = handlerType.getDeclaredAnnotation(RequestMapping.class)
                    .value()[0].replace("/", "");

            operation.getResponses()
                .get(responseCode)
                .addLink(collectionString, new Link().$ref(linkPath));
        } else {

            schema.set$ref(null);
            schema.addAllOfItem(model.schema);
            final String path
                = handlerType.getDeclaredAnnotation(RequestMapping.class)
                    .value()[0].replace("/", "");
            operation.getResponses()
                .get(responseCode)
                .addLink("self", new Link().$ref(path + "_id"))
                .addLink(collectionString, new Link().$ref(path));
        }
    }

    private static Class<?> getModelType(final HandlerMethod handlerMethod) {

        /* @formatter:off */
        if (handlerMethod.getBeanType().getGenericInterfaces()[1]
            instanceof final ParameterizedType parameterizedType
            && parameterizedType.getActualTypeArguments()[1]
            instanceof final Class<?> classType
            && classType.getGenericSuperclass()
            instanceof final ParameterizedType parameterizedTypeResult
            && parameterizedTypeResult.getActualTypeArguments()[1]
            instanceof final Class<?> classTypeResult) {
            /* @formatter:on */

            return classTypeResult;
        }
        return Object.class;
    }

    @Nullable
    private static Schema<?> getSchema(
        final Operation operation, final String responseCode) {

        final ApiResponse apiResponse
            = operation.getResponses().get(responseCode);

        if (apiResponse == null) {

            return null;
        }
        return apiResponse.getContent()
            .get(MediaType.APPLICATION_JSON_VALUE)
            .getSchema();
    }

    private static ResolvedSchema getResolvedSchema(final Class<?> classType) {

        return ModelConverters.getInstance()
            .resolveAsResolvedSchema(new AnnotatedType(classType));
    }

    private static ResolvedSchema addDtoSchemaToModel(
        final ResolvedSchema actualSchema, final Class<?> classType) {

        /*@formatter:off*/
        if (classType.getGenericSuperclass()
            instanceof final ParameterizedType parameterizedType
            /*@formatter:on*/
            && parameterizedType
                .getActualTypeArguments()[0] instanceof final Class<
                    ?> dtoClass) {

            actualSchema.schema.addAllOfItem(
                actualSchema.referencedSchemas.get(dtoClass.getSimpleName()));
        }
        return actualSchema;
    }
}
