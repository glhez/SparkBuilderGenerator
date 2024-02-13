package com.helospark.spark.builder.handlers.codegenerator.component;

import static com.helospark.spark.builder.preferences.PluginPreferenceList.ADD_GENERATED_ANNOTATION;
import static com.helospark.spark.builder.preferences.PluginPreferenceList.CREATE_PUBLIC_DEFAULT_CONSTRUCTOR;

import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;

import com.helospark.spark.builder.PluginLogger;
import com.helospark.spark.builder.handlers.codegenerator.component.fragment.constructor.ConstructorInsertionFragment;
import com.helospark.spark.builder.handlers.codegenerator.component.helper.GeneratedAnnotationPopulator;
import com.helospark.spark.builder.handlers.codegenerator.domain.BuilderField;
import com.helospark.spark.builder.handlers.codegenerator.domain.CompilationUnitModificationDomain;
import com.helospark.spark.builder.handlers.codegenerator.domain.ConstructorParameterSetterBuilderField;
import com.helospark.spark.builder.preferences.PreferencesManager;

/**
 * Generates a public default constructor.
 * This constructor is required for JPA serialization.
 * Something like:
 * 
 * <pre>
 * public Clazz() {
 * }
 * </pre>
 * 
 * @author helospark
 */
public class DefaultConstructorAppender {
    private ConstructorInsertionFragment constructorInsertionFragment;
    private PreferencesManager preferencesManager;
    private GeneratedAnnotationPopulator generatedAnnotationPopulator;
    private PluginLogger pluginLogger;

    public DefaultConstructorAppender(ConstructorInsertionFragment constructorInsertionFragment, PreferencesManager preferencesManager,
            GeneratedAnnotationPopulator generatedAnnotationPopulator) {
        this.constructorInsertionFragment = constructorInsertionFragment;
        this.preferencesManager = preferencesManager;
        this.generatedAnnotationPopulator = generatedAnnotationPopulator;
        pluginLogger = new PluginLogger();
    }

    public void addDefaultConstructorIfNeeded(CompilationUnitModificationDomain domain, List<BuilderField> fields) {
        if (shouldCreateDefaultConstructor(domain.getOriginalType(), fields)) {
            MethodDeclaration defaultConstructor = createConstructor(domain);
            constructorInsertionFragment.insertMethodToFirstPlace(domain.getOriginalType(), domain.getListRewrite(), defaultConstructor);
        }
    }

    private boolean shouldCreateDefaultConstructor(AbstractTypeDeclaration originalType, List<BuilderField> fields) {
        return preferencesManager.getBooleanPreference(CREATE_PUBLIC_DEFAULT_CONSTRUCTOR)
                && !hasDefaultConstructor(originalType)
                && !hasSuperConstructorFields(fields);
    }

    private boolean hasDefaultConstructor(AbstractTypeDeclaration originalType) {
        return ((List<BodyDeclaration>) originalType.bodyDeclarations())
                                                                        .stream()
                                                                        .filter(MethodDeclaration.class::isInstance)
                                                                        .map(MethodDeclaration.class::cast)
                                                                        .filter(MethodDeclaration::isConstructor)
                                                                        .anyMatch(method -> method.parameters().isEmpty());
    }

    private boolean hasSuperConstructorFields(List<BuilderField> fields) {
        boolean hasSuperConstructorFields = fields.stream().anyMatch(ConstructorParameterSetterBuilderField.class::isInstance);
        if (hasSuperConstructorFields) {
            pluginLogger.info("Skipping default constructor generation, because there are super constructor fields");
        }
        return hasSuperConstructorFields;
    }

    private MethodDeclaration createConstructor(CompilationUnitModificationDomain domain) {
        AST ast = domain.getAst();

        Block emptyBody = ast.newBlock();
        MethodDeclaration defaultConstructor = ast.newMethodDeclaration();
        defaultConstructor.setBody(emptyBody);
        defaultConstructor.setConstructor(true);
        defaultConstructor.setName(ast.newSimpleName(domain.getOriginalType().getName().toString()));
        defaultConstructor.modifiers().add(ast.newModifier(ModifierKeyword.PUBLIC_KEYWORD));

        if (preferencesManager.getBooleanPreference(ADD_GENERATED_ANNOTATION)) {
            generatedAnnotationPopulator.addGeneratedAnnotation(ast, defaultConstructor);
        }

        return defaultConstructor;
    }

}
