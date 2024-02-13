package com.helospark.spark.builder.handlers.it;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import com.helospark.spark.builder.DiContainer;
import com.helospark.spark.builder.handlers.DialogWrapper;
import com.helospark.spark.builder.handlers.HandlerUtilWrapper;
import com.helospark.spark.builder.handlers.WorkingCopyManagerWrapper;
import com.helospark.spark.builder.handlers.codegenerator.CompilationUnitParser;
import com.helospark.spark.builder.handlers.codegenerator.component.fragment.builderclass.field.FullyQualifiedNameExtractor;
import com.helospark.spark.builder.handlers.codegenerator.component.helper.ITypeExtractor;
import com.helospark.spark.builder.handlers.codegenerator.component.helper.JlsVersionProvider;
import com.helospark.spark.builder.handlers.codegenerator.component.helper.PreferenceStoreProvider;
import com.helospark.spark.builder.handlers.codegenerator.component.helper.PreferenceStoreWrapper;
import com.helospark.spark.builder.handlers.codegenerator.component.helper.StagedBuilderStagePropertyInputDialogOpener;
import com.helospark.spark.builder.handlers.codegenerator.domain.BuilderField;

public class BaseBuilderGeneratorIT {
    protected ExecutionEvent dummyExecutionEvent = new ExecutionEvent();

    protected AbstractHandler underTest;

    @Mock
    protected ICompilationUnit iCompilationUnit;
    @Mock
    protected HandlerUtilWrapper handlerUtilWrapper;
    @Mock
    protected WorkingCopyManagerWrapper workingCopyManagerWrapper;
    @Mock
    protected CompilationUnitParser compilationUnitParser;
    @Mock
    protected PreferenceStoreProvider preferenceStoreProvider;
    @Mock
    protected PreferenceStoreWrapper preferenceStore;
    @Mock
    protected DialogWrapper dialogWrapper;
    @Mock
    protected IBuffer iBuffer;
    @Mock
    protected ITypeExtractor iTypeExtractor;
    @Mock
    protected FullyQualifiedNameExtractor fullyQualifiedNameExtractor;
    @Captor
    protected ArgumentCaptor<String> outputCaptor;
    @Mock
    protected StagedBuilderStagePropertyInputDialogOpener stagedBuilderStagePropertyInputDialogOpener;

    private AutoCloseable openMockAutoCloseable;

    protected void init() throws JavaModelException {
        this.openMockAutoCloseable = org.mockito.MockitoAnnotations.openMocks(this);
        DiContainer.clearDiContainer();

        // Override mock dependencies

        diContainerOverrides();

        // end of overrides

        DiContainer.initializeDiContainer();

        given(handlerUtilWrapper.getActivePartId(dummyExecutionEvent))
                                                                      .willReturn("org.eclipse.jdt.ui.CompilationUnitEditor");
        given(workingCopyManagerWrapper.getCurrentCompilationUnit(dummyExecutionEvent)).willReturn(iCompilationUnit);
        given(preferenceStoreProvider.providePreferenceStore()).willReturn(preferenceStore);
        given(iCompilationUnit.getBuffer()).willReturn(iBuffer);
        given(iTypeExtractor.extract(any(TypeDeclaration.class))).willReturn(empty());
        given(fullyQualifiedNameExtractor.getFullyQualifiedBaseTypeName(any(BuilderField.class))).willReturn(empty());
        setDefaultPreferenceStoreSettings();
        doNothing().when(iBuffer).setContents(outputCaptor.capture());

        DiContainer.initializeDiContainer();
    }

    protected void tearDown() throws Exception {
        if (openMockAutoCloseable != null) {
            openMockAutoCloseable.close();
        }
    }

    protected void diContainerOverrides() {
        DiContainer.addDependency(handlerUtilWrapper);
        DiContainer.addDependency(workingCopyManagerWrapper);
        DiContainer.addDependency(compilationUnitParser);
        DiContainer.addDependency(preferenceStoreProvider);
        DiContainer.addDependency(dialogWrapper);
        DiContainer.addDependency(iTypeExtractor);
        DiContainer.addDependency(fullyQualifiedNameExtractor);
        DiContainer.addDependency(stagedBuilderStagePropertyInputDialogOpener);
    }

    protected void setInput(String sourceAsString) throws JavaModelException {
        setCompilationUnitInput(iCompilationUnit, sourceAsString);
    }

    protected void setCompilationUnitInput(ICompilationUnit iCompilationUnitParameter, String sourceAsString) throws JavaModelException {
        char[] source = sourceAsString.toCharArray();
        CompilationUnit cu = parseAst(source);
        given(compilationUnitParser.parse(iCompilationUnitParameter)).willReturn(cu);
        given(iCompilationUnitParameter.getSource()).willReturn(sourceAsString);
    }

    protected void setDefaultPreferenceStoreSettings() {
        // general settings
        given(preferenceStore.getBoolean("override_previous_builder")).willReturn(true);
        given(preferenceStore.getString("create_builder_method_pattern")).willReturn(of("builder"));
        given(preferenceStore.getString("builder_class_name_pattern")).willReturn(of("Builder"));
        given(preferenceStore.getString("org.helospark.builder.copyBuilderInstanceMethodName")).willReturn(of("builderFrom"));
        given(preferenceStore.getString("build_method_name")).willReturn(of("build"));
        given(preferenceStore.getString("builders_method_name_pattern")).willReturn(of("with[FieldName]"));
        given(preferenceStore.getBoolean("generate_javadoc_on_builder_method")).willReturn(false);
        given(preferenceStore.getBoolean("generate_javadoc_on_builder_class")).willReturn(false);
        given(preferenceStore.getBoolean("generate_javadoc_on_each_builder_method")).willReturn(false);
        given(preferenceStore.getBoolean("add_nonnull_on_return")).willReturn(false);
        given(preferenceStore.getBoolean("add_nonnull_on_parameter")).willReturn(false);
        given(preferenceStore.getBoolean("add_generated_annotation")).willReturn(false);
        given(preferenceStore.getBoolean("org.helospark.builder.removePrefixAndPostfixFromBuilderNames")).willReturn(false);
        given(preferenceStore.getBoolean("org.helospark.builder.includeVisibleFieldsFromSuperclass")).willReturn(false);
        given(preferenceStore.getBoolean("org.helospark.builder.alwaysGenerateBuilderToFirstClass")).willReturn(true);
        given(preferenceStore.getBoolean("org.helospark.builder.initializeOptionalFieldsToEmpty")).willReturn(true);
        given(preferenceStore.getBoolean("org.helospark.builder.initializeCollectionToEmptyCollection")).willReturn(true);
        given(preferenceStore.getBoolean("org.helospark.builder.keepCustomMethodsInBuilder")).willReturn(true);

        // staged builder
        given(preferenceStore.getBoolean("org.helospark.builder.generateJavadocOnStageInterface")).willReturn(false);
        given(preferenceStore.getBoolean("org.helospark.builder.skipStaticBuilderMethod")).willReturn(false);
        given(preferenceStore.getString("org.helospark.builder.stagedEditorLastStageInterfaceName")).willReturn(of("IBuildStage"));
        given(preferenceStore.getString("org.helospark.builder.stagedEditorStageInterfaceName")).willReturn(of("I[FieldName]Stage"));
        given(preferenceStore.getBoolean("org.helospark.builder.addGeneratedAnnotationOnStageInterface")).willReturn(false);

        // regular builder
        given(preferenceStore.getBoolean("org.helospark.builder.showFieldFilterDialogForRegularBuilder")).willReturn(false);

        // prefix postfix
        given(preferenceStore.getString("org.eclipse.jdt.core.codeComplete.fieldPrefixes")).willReturn(of(""));
        given(preferenceStore.getString("org.eclipse.jdt.core.codeComplete.fieldSuffixes")).willReturn(of(""));
    }

    protected CompilationUnit parseAst(char[] source) {
        ASTParser parser = ASTParser.newParser(JlsVersionProvider.getLatestJlsVersion());
        parser.setSource(source);
        Map options = JavaCore.getOptions();
        JavaCore.setComplianceOptions(JavaCore.VERSION_17, options);
        parser.setCompilerOptions(options);
        CompilationUnit result = (CompilationUnit) parser.createAST(null);
        return result;
    }

    protected void assertEqualsJavaContents(String actualValue, String expectedValue) {
        // making sure that the formatting will not matter in the comparition
        // we parse and format them the same way
        String actual = parseAst(actualValue.toCharArray()).toString();
        String expected = parseAst(expectedValue.toCharArray()).toString();
        assertEquals(actual, expected);
    }

    public String readClasspathFile(String fileName) throws IOException, URISyntaxException {
        Path uri = Paths.get(this.getClass().getResource("/" + fileName).toURI());
        return new String(Files.readAllBytes(uri), StandardCharsets.UTF_8);
    }
}
