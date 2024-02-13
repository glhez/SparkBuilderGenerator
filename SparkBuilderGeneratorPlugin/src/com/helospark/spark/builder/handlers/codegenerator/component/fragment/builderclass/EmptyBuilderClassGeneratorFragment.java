package com.helospark.spark.builder.handlers.codegenerator.component.fragment.builderclass;

import static com.helospark.spark.builder.preferences.PluginPreferenceList.ADD_GENERATED_ANNOTATION;
import static com.helospark.spark.builder.preferences.PluginPreferenceList.ADD_JACKSON_DESERIALIZE_ANNOTATION;
import static com.helospark.spark.builder.preferences.PluginPreferenceList.BUILDER_CLASS_NAME_PATTERN;
import static com.helospark.spark.builder.preferences.PluginPreferenceList.GENERATE_JAVADOC_ON_BUILDER_CLASS;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.helospark.spark.builder.handlers.codegenerator.component.fragment.builderclass.builderclass.JsonPOJOBuilderAdderFragment;
import com.helospark.spark.builder.handlers.codegenerator.component.helper.GeneratedAnnotationPopulator;
import com.helospark.spark.builder.handlers.codegenerator.component.helper.JavadocGenerator;
import com.helospark.spark.builder.handlers.codegenerator.component.helper.TemplateResolver;
import com.helospark.spark.builder.preferences.PreferencesManager;

/**
 * Fragment to create empty builder class with Javadoc.
 * Generated code is something like:
 * 
 * <pre>
 * public static final class Builder {
 *
 * }
 * </pre>
 * 
 * @author helospark
 */
public class EmptyBuilderClassGeneratorFragment {
    private static final String CLASS_NAME_REPLACEMENT_PATTERN = "className";
    private GeneratedAnnotationPopulator generatedAnnotationPopulator;
    private PreferencesManager preferencesManager;
    private JavadocGenerator javadocGenerator;
    private TemplateResolver templateResolver;
    private JsonPOJOBuilderAdderFragment jsonPOJOBuilderAdderFragment;

    public EmptyBuilderClassGeneratorFragment(GeneratedAnnotationPopulator generatedAnnotationPopulator, PreferencesManager preferencesManager,
            JavadocGenerator javadocGenerator,
            TemplateResolver templateResolver, JsonPOJOBuilderAdderFragment jsonPOJOBuilderAdderFragment) {
        this.generatedAnnotationPopulator = generatedAnnotationPopulator;
        this.preferencesManager = preferencesManager;
        this.javadocGenerator = javadocGenerator;
        this.templateResolver = templateResolver;
        this.jsonPOJOBuilderAdderFragment = jsonPOJOBuilderAdderFragment;
    }

    public TypeDeclaration createBuilderClass(AST ast, AbstractTypeDeclaration originalType) {
        TypeDeclaration builderType = ast.newTypeDeclaration();
        builderType.setName(ast.newSimpleName(getBuilderName(originalType)));

        if (preferencesManager.getBooleanPreference(ADD_GENERATED_ANNOTATION)) {
            generatedAnnotationPopulator.addGeneratedAnnotation(ast, builderType);
        }
        builderType.modifiers().add(ast.newModifier(ModifierKeyword.PUBLIC_KEYWORD));
        builderType.modifiers().add(ast.newModifier(ModifierKeyword.STATIC_KEYWORD));
        builderType.modifiers().add(ast.newModifier(ModifierKeyword.FINAL_KEYWORD));

        if (preferencesManager.getBooleanPreference(ADD_JACKSON_DESERIALIZE_ANNOTATION)) {
            jsonPOJOBuilderAdderFragment.addJsonPOJOBuilder(ast, builderType);
        }

        if (preferencesManager.getBooleanPreference(GENERATE_JAVADOC_ON_BUILDER_CLASS)) {
            Javadoc javadoc = javadocGenerator.generateJavadoc(ast,
                                                               String.format(Locale.ENGLISH, "Builder to build {@link %s}.",
                                                                             originalType.getName().toString()),
                                                               Collections.emptyMap());
            builderType.setJavadoc(javadoc);
        }

        return builderType;
    }

    private String getBuilderName(AbstractTypeDeclaration originalType) {
        Map<String, String> replacementMap = new HashMap<>();
        replacementMap.put(CLASS_NAME_REPLACEMENT_PATTERN, originalType.getName().toString());
        return templateResolver.resolveTemplate(preferencesManager.getStringPreference(BUILDER_CLASS_NAME_PATTERN), replacementMap);
    }
}
