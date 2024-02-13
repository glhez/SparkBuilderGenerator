package com.helospark.spark.builder.dialogs.domain;

import java.util.Collections;
import java.util.List;

import javax.annotation.processing.Generated;

/**
 * Data for the dialog.
 * @author helospark
 */
public class RegularBuilderDialogData {
    private List<RegularBuilderFieldIncludeFieldIncludeDomain> regularBuilderFieldIncludeFieldIncludeDomains;
    private boolean shouldCreateCopyMethod;
    private boolean addJacksonDeserializeAnnotation;
    private boolean createDefaultConstructor;
    private boolean createPublicConstructorWithMandatoryFields;

    @Generated("SparkTools")
    private RegularBuilderDialogData(Builder builder) {
        this.regularBuilderFieldIncludeFieldIncludeDomains = builder.regularBuilderFieldIncludeFieldIncludeDomains;
        this.shouldCreateCopyMethod = builder.shouldCreateCopyMethod;
        this.addJacksonDeserializeAnnotation = builder.addJacksonDeserializeAnnotation;
        this.createDefaultConstructor = builder.createDefaultConstructor;
        this.createPublicConstructorWithMandatoryFields = builder.createPublicConstructorWithMandatoryFields;
    }

    public List<RegularBuilderFieldIncludeFieldIncludeDomain> getRegularBuilderFieldIncludeFieldIncludeDomains() {
        return regularBuilderFieldIncludeFieldIncludeDomains;
    }

    public boolean shouldCreateCopyMethod() {
        return shouldCreateCopyMethod;
    }

    public boolean isAddJacksonDeserializeAnnotation() {
        return addJacksonDeserializeAnnotation;
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
        private List<RegularBuilderFieldIncludeFieldIncludeDomain> regularBuilderFieldIncludeFieldIncludeDomains = Collections.emptyList();
        private boolean shouldCreateCopyMethod;
        private boolean addJacksonDeserializeAnnotation;
        private boolean createDefaultConstructor;
        private boolean createPublicConstructorWithMandatoryFields;

        private Builder() {
        }

        public Builder withRegularBuilderFieldIncludeFieldIncludeDomains(List<RegularBuilderFieldIncludeFieldIncludeDomain> regularBuilderFieldIncludeFieldIncludeDomains) {
            this.regularBuilderFieldIncludeFieldIncludeDomains = regularBuilderFieldIncludeFieldIncludeDomains;
            return this;
        }

        public Builder withShouldCreateCopyMethod(boolean shouldCreateCopyMethod) {
            this.shouldCreateCopyMethod = shouldCreateCopyMethod;
            return this;
        }

        public Builder withAddJacksonDeserializeAnnotation(boolean addJacksonDeserializeAnnotation) {
            this.addJacksonDeserializeAnnotation = addJacksonDeserializeAnnotation;
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

        public RegularBuilderDialogData build() {
            return new RegularBuilderDialogData(this);
        }
    }

}
