package com.helospark.spark.builder.handlers.codegenerator.domain;

import java.util.Collections;
import java.util.List;

import javax.annotation.processing.Generated;

/**
 * One-time user preference that has to be entered by the user on every builder generation.
 * @author helospark
 */
public class RegularBuilderUserPreference {
    private boolean generateCopyMethod;
    private boolean addJacksonDeserializer;
    private boolean createDefaultConstructor;
    private boolean createPublicConstructorWithMandatoryFields;
    private List<BuilderField> builderFields;

    @Generated("SparkTools")
    private RegularBuilderUserPreference(Builder builder) {
        this.generateCopyMethod = builder.generateCopyMethod;
        this.addJacksonDeserializer = builder.addJacksonDeserializer;
        this.createDefaultConstructor = builder.createDefaultConstructor;
        this.createPublicConstructorWithMandatoryFields = builder.createPublicConstructorWithMandatoryFields;
        this.builderFields = builder.builderFields;
    }

    public boolean isGenerateCopyMethod() {
        return generateCopyMethod;
    }

    public List<BuilderField> getBuilderFields() {
        return builderFields;
    }

    public boolean isAddJacksonDeserializer() {
        return addJacksonDeserializer;
    }

    public boolean isCreateDefaultConstructor() {
        return createDefaultConstructor;
    }

    public boolean isCreatePublicConstructorWithMandatoryFields() {
        return createPublicConstructorWithMandatoryFields;
    }

    @Generated("SparkTools")
    public static Builder builder() {
        return new Builder();
    }

    @Generated("SparkTools")
    public static final class Builder {
        private boolean generateCopyMethod;
        private boolean addJacksonDeserializer;
        private boolean createDefaultConstructor;
        private boolean createPublicConstructorWithMandatoryFields;
        private List<BuilderField> builderFields = Collections.emptyList();

        private Builder() {
        }

        public Builder withGenerateCopyMethod(boolean generateCopyMethod) {
            this.generateCopyMethod = generateCopyMethod;
            return this;
        }

        public Builder withAddJacksonDeserializer(boolean addJacksonDeserializer) {
            this.addJacksonDeserializer = addJacksonDeserializer;
            return this;
        }

        public Builder withCreateDefaultConstructor(boolean createDefaultConstructor) {
            this.createDefaultConstructor = createDefaultConstructor;
            return this;
        }

        public Builder withCreatePublicConstructorWithMandatoryFields(boolean createPublicConstructorWithMandatoryFields) {
            this.createPublicConstructorWithMandatoryFields = createPublicConstructorWithMandatoryFields;
            return this;
        }

        public Builder withBuilderFields(List<BuilderField> builderFields) {
            this.builderFields = builderFields;
            return this;
        }

        public RegularBuilderUserPreference build() {
            return new RegularBuilderUserPreference(this);
        }
    }

}
