package com.helospark.spark.builder.handlers.codegenerator.component.remover;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.dom.AbstractTypeDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.rewrite.ASTRewrite;

import com.helospark.spark.builder.handlers.codegenerator.component.helper.MethodExtractor;
import com.helospark.spark.builder.handlers.codegenerator.component.remover.helper.GeneratedAnnotationContainingBodyDeclarationFilter;
import com.helospark.spark.builder.handlers.codegenerator.component.remover.helper.IsPrivatePredicate;
import com.helospark.spark.builder.handlers.codegenerator.domain.CompilationUnitModificationDomain;

/**
 * Removes the previously generated class initializing private constructor.
 * @author helospark
 */
public class PrivateConstructorRemover implements BuilderRemoverChainItem {
    private IsPrivatePredicate isPrivatePredicate;
    private GeneratedAnnotationContainingBodyDeclarationFilter generatedAnnotationContainingBodyDeclarationFilter;

    public PrivateConstructorRemover(IsPrivatePredicate isPrivatePredicate,
            GeneratedAnnotationContainingBodyDeclarationFilter generatedAnnotationContainingBodyDeclarationFilter) {
        this.isPrivatePredicate = isPrivatePredicate;
        this.generatedAnnotationContainingBodyDeclarationFilter = generatedAnnotationContainingBodyDeclarationFilter;
    }

    @Override
    public void remove(ASTRewrite rewriter, AbstractTypeDeclaration mainType, CompilationUnitModificationDomain modificationDomain) {
        List<MethodDeclaration> privateConstructors = extractPrivateConstructors(mainType);
        List<MethodDeclaration> annotatedConstructors = generatedAnnotationContainingBodyDeclarationFilter.filterAnnotatedClasses(privateConstructors);
        if (!annotatedConstructors.isEmpty()) {
            annotatedConstructors.stream()
                    .forEach(constructor -> rewriter.remove(constructor, null));
        } else {
            privateConstructors.stream()
                    .findFirst()
                    .ifPresent(constructor -> rewriter.remove(constructor, null));
        }
    }

    private List<MethodDeclaration> extractPrivateConstructors(AbstractTypeDeclaration mainType) {
        return Arrays.stream(MethodExtractor.getMethods(mainType))
                .filter(method -> method.isConstructor())
                .filter(method -> method.parameters().size() == 1)
                .filter(isPrivatePredicate)
                .collect(Collectors.toList());
    }

}
