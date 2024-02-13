package com.helospark.spark.builder.handlers.codegenerator.component.fragment.constructor;

import static com.helospark.spark.builder.preferences.PluginPreferenceList.ADD_GENERATED_ANNOTATION;

import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier.ModifierKeyword;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.helospark.spark.builder.handlers.codegenerator.component.helper.CamelCaseConverter;
import com.helospark.spark.builder.handlers.codegenerator.component.helper.GeneratedAnnotationPopulator;
import com.helospark.spark.builder.handlers.codegenerator.domain.BuilderField;
import com.helospark.spark.builder.preferences.PreferencesManager;

/**
 * Creates the method definition of the class initializing constructor.
 * Generated code is something like:
 * 
 * <pre>
 * private Clazz(Builder builder);
 * </pre>
 * 
 * @author helospark
 */
public class PrivateConstructorMethodDefinitionCreationFragment {
    private PreferencesManager preferencesManager;
    private GeneratedAnnotationPopulator generatedAnnotationPopulator;
    private CamelCaseConverter camelCaseConverter;

    public PrivateConstructorMethodDefinitionCreationFragment(PreferencesManager preferencesManager,
            GeneratedAnnotationPopulator generatedAnnotationPopulator,
            CamelCaseConverter camelCaseConverter) {
        this.preferencesManager = preferencesManager;
        this.generatedAnnotationPopulator = generatedAnnotationPopulator;
        this.camelCaseConverter = camelCaseConverter;
    }

    public MethodDeclaration createPrivateConstructorDefinition(AST ast, AbstractTypeDeclaration originalType, TypeDeclaration builderType,
            List<BuilderField> builderFields) {

        MethodDeclaration method = ast.newMethodDeclaration();
        method.setConstructor(true);
        method.setName(ast.newSimpleName(originalType.getName().toString()));
        if (preferencesManager.getBooleanPreference(ADD_GENERATED_ANNOTATION)) {
            generatedAnnotationPopulator.addGeneratedAnnotation(ast, method);
        }
        method.modifiers().add(ast.newModifier(ModifierKeyword.PRIVATE_KEYWORD));

        SingleVariableDeclaration methodParameterDeclaration = ast.newSingleVariableDeclaration();
        methodParameterDeclaration.setType(ast.newSimpleType(ast.newName(builderType.getName().toString())));
        methodParameterDeclaration.setName(ast.newSimpleName(camelCaseConverter.toLowerCamelCase(builderType.getName().toString())));

        method.parameters().add(methodParameterDeclaration);
        return method;
    }
}
