package com.helospark.spark.builder.handlers.it;

import static java.util.Optional.of;
import static org.mockito.BDDMockito.given;

import org.eclipse.jdt.core.JavaModelException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.helospark.spark.builder.handlers.GenerateRegularBuilderHandler;

public class JacksonAnnotationWithRegularBuilderIT extends BaseBuilderGeneratorIT {

    @BeforeMethod
    public void beforeMethod() throws JavaModelException {
        super.init();
        underTest = new GenerateRegularBuilderHandler();

        given(preferenceStore.getBoolean("org.helospark.builder.addJacksonDeserializeAnnotation")).willReturn(true);
    }
    
    @AfterMethod
    public void afterMethod() throws Exception {
        super.tearDown();
    }

    @Test(dataProvider = "testCasesForRegularBuilder")
    public void testWithDefaultEnabled(String inputFile, String expectedOutputFile) throws Exception {
        // GIVEN
        String input = readClasspathFile(inputFile);
        String expectedResult = readClasspathFile(expectedOutputFile);
        super.setInput(input);

        // WHEN
        underTest.execute(dummyExecutionEvent);

        // THEN
        super.assertEqualsJavaContents(outputCaptor.getValue(), expectedResult);
    }

    @DataProvider(name = "testCasesForRegularBuilder")
    public Object[][] regularBuilderExampleFileProvider() {
        return new Object[][] {
                { "jackson/mail_input.tjava", "jackson/mail_with_default_methodnames_output.tjava" },
                { "jackson/mail_with_default_methodnames_output.tjava", "jackson/mail_with_default_methodnames_output.tjava" }, // previous removal
        };
    }

    @Test(dataProvider = "testCasesWithChangedDefault")
    public void testWithChangedDefault(String inputFile, String expectedOutputFile, String buildMethodName, String withName) throws Exception {
        // GIVEN
        given(preferenceStore.getString("build_method_name")).willReturn(of(buildMethodName));
        given(preferenceStore.getString("builders_method_name_pattern")).willReturn(of(withName));
        String input = readClasspathFile(inputFile);
        String expectedResult = readClasspathFile(expectedOutputFile);
        super.setInput(input);

        // WHEN
        underTest.execute(dummyExecutionEvent);

        // THEN
        super.assertEqualsJavaContents(outputCaptor.getValue(), expectedResult);
    }

    @Test
    public void testWithChangedBuilderName() throws Exception {
        // GIVEN
        given(preferenceStore.getString("builder_class_name_pattern")).willReturn(of("[className]Builder"));
        String input = readClasspathFile("jackson/mail_input.tjava");
        String expectedResult = readClasspathFile("jackson/mail_with_changed_builder_output.tjava");
        super.setInput(input);

        // WHEN
        underTest.execute(dummyExecutionEvent);

        // THEN
        super.assertEqualsJavaContents(outputCaptor.getValue(), expectedResult);
    }

    @DataProvider(name = "testCasesWithChangedDefault")
    public Object[][] testCasesWithChangedDefault() {
        return new Object[][] {
                { "jackson/mail_input.tjava", "jackson/mail_with_changed_with_method_output.tjava", "build", "[fieldName]" },
                { "jackson/mail_input.tjava", "jackson/mail_with_changed_build_method_output.tjava", "customBuild", "asd[FieldName]" },
        };
    }
}