package com.helospark.spark.builder.handlers.it;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

import java.util.List;

import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.helospark.spark.builder.handlers.GenerateRegularBuilderHandler;
import com.helospark.spark.builder.handlers.GenerateStagedBuilderHandler;
import com.helospark.spark.builder.handlers.it.dummyService.NoDialogOperationPerformedStagedBuilderDialogAnswerProvider;

public class BuilderWithSuperClassFieldsE2ETest extends BaseBuilderGeneratorIT {
    private NoDialogOperationPerformedStagedBuilderDialogAnswerProvider dialogAnswerProvider = new NoDialogOperationPerformedStagedBuilderDialogAnswerProvider();

    @Mock
    private IType firstSuperClassType;
    @Mock
    private ICompilationUnit firstSuperClassICompilationUnit;

    @Mock
    private IType secondSuperClassType;
    @Mock
    private ICompilationUnit secondSuperClassICompilationUnit;

    @BeforeMethod
    public void beforeMethod() throws JavaModelException {
        super.init();
        underTest = new GenerateRegularBuilderHandler();

        // set first superclass
        given(firstSuperClassType.getCompilationUnit()).willReturn(firstSuperClassICompilationUnit);
        given(firstSuperClassType.getElementName()).willReturn("TestSuperClass");

        // set second superclass
        given(secondSuperClassType.getCompilationUnit()).willReturn(secondSuperClassICompilationUnit);
        given(secondSuperClassType.getElementName()).willReturn("TestSuperSuperClass");

        given(preferenceStore.getBoolean("org.helospark.builder.includeVisibleFieldsFromSuperclass")).willReturn(true);
    }

    @Test(dataProvider = "simpleExtendsDataProvider")
    public void testSimpleExtends(String inputFile, String superClassFile, String expectedOutputFile) throws Exception {
        // GIVEN
        given(iTypeExtractor.extract(any(TypeDeclaration.class)))
                .willReturn(of(firstSuperClassType))
                .willReturn(empty());
        String superClassInput = readClasspathFile(superClassFile);
        super.setCompilationUnitInput(firstSuperClassICompilationUnit, superClassInput);

        String input = readClasspathFile(inputFile);
        String expectedResult = readClasspathFile(expectedOutputFile);
        super.setInput(input);

        // WHEN
        underTest.execute(dummyExecutionEvent);

        // THEN
        super.assertEqualsJavaContents(outputCaptor.getValue(), expectedResult);
    }

    @DataProvider(name = "simpleExtendsDataProvider")
    public Object[][] simpleExtendsDataProvider() {
        return new Object[][] {
                { "superclass_test_extends_input.tjava", "superclass_test_superclass_input.tjava", "superclass_output.tjava" },
                { "superclass_test_extends_input.tjava", "superclass_test_superclass_in_different_package_input.tjava", "superclass_output_with_different_package.tjava" },
                { "superclass_test_extends_input.tjava", "superclass_test_superclass_in_default_package_input.tjava", "superclass_output_with_different_package.tjava" },
                { "superclass_test_extends_input_in_default_package.tjava", "superclass_test_superclass_in_default_package_input.tjava",
                        "superclass_output_in_same_default_package.tjava" },
        };
    }

    @Test
    public void testBuilderWithTwoSuperclassesShouldConcatenatesFieldsFromAll() throws Exception {
        // GIVEN
        given(iTypeExtractor.extract(any(TypeDeclaration.class)))
                .willReturn(of(firstSuperClassType))
                .willReturn(of(secondSuperClassType))
                .willReturn(empty());
        String superClassInput = readClasspathFile("superclass_test_superclass_input.tjava");
        super.setCompilationUnitInput(firstSuperClassICompilationUnit, superClassInput);

        String secondSuperClassInput = readClasspathFile("super_superclass_test_input.tjava");
        super.setCompilationUnitInput(secondSuperClassICompilationUnit, secondSuperClassInput);

        String input = readClasspathFile("superclass_test_extends_input.tjava");
        String expectedResult = readClasspathFile("superclass_with_two_superclasses_output.tjava");
        super.setInput(input);

        // WHEN
        underTest.execute(dummyExecutionEvent);

        // THEN
        super.assertEqualsJavaContents(outputCaptor.getValue(), expectedResult);
    }

    @Test
    public void testSimpleExtendsWithStagedBuilder() throws Exception {
        // GIVEN
        underTest = new GenerateStagedBuilderHandler();
        given(iTypeExtractor.extract(any(TypeDeclaration.class)))
                .willReturn(of(firstSuperClassType))
                .willReturn(empty());
        String superClassInput = readClasspathFile("superclass_test_superclass_input.tjava");
        super.setCompilationUnitInput(firstSuperClassICompilationUnit, superClassInput);

        String input = readClasspathFile("superclass_test_extends_input.tjava");
        String expectedResult = readClasspathFile("superclass_test_output_with_staged_builder.tjava");
        super.setInput(input);

        // no change in order, all mandatory fields
        given(stagedBuilderStagePropertyInputDialogOpener.open(any(List.class))).willAnswer(invocation -> dialogAnswerProvider.provideAnswer(invocation));

        // WHEN
        underTest.execute(dummyExecutionEvent);

        // THEN
        super.assertEqualsJavaContents(outputCaptor.getValue(), expectedResult);
    }

    @Test
    public void testSimpleExtendsShouldNotIncludeSuperclassesWhenPreferencesIsTurnedOff() throws Exception {
        // GIVEN
        given(preferenceStore.getBoolean("org.helospark.builder.includeVisibleFieldsFromSuperclass")).willReturn(false);

        given(iTypeExtractor.extract(any(TypeDeclaration.class)))
                .willReturn(of(firstSuperClassType))
                .willReturn(empty());
        String superClassInput = readClasspathFile("superclass_test_superclass_input.tjava");
        super.setCompilationUnitInput(firstSuperClassICompilationUnit, superClassInput);

        String input = readClasspathFile("superclass_test_extends_input.tjava");
        String expectedResult = readClasspathFile("superclass_test_with_disabled_preference.tjava");
        super.setInput(input);

        // WHEN
        underTest.execute(dummyExecutionEvent);

        // THEN
        super.assertEqualsJavaContents(outputCaptor.getValue(), expectedResult);
    }

}
