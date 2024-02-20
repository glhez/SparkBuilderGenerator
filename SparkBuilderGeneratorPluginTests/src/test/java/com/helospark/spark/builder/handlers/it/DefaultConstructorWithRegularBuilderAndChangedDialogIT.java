package com.helospark.spark.builder.handlers.it;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Arrays;
import java.util.Optional;

import org.eclipse.jdt.core.JavaModelException;
import org.mockito.Mock;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.helospark.spark.builder.DiContainer;
import com.helospark.spark.builder.dialogs.domain.RegularBuilderDialogData;
import com.helospark.spark.builder.dialogs.domain.RegularBuilderFieldIncludeFieldIncludeDomain;
import com.helospark.spark.builder.handlers.GenerateRegularBuilderHandler;
import com.helospark.spark.builder.handlers.codegenerator.RegularBuilderUserPreferenceDialogOpener;

public class DefaultConstructorWithRegularBuilderAndChangedDialogIT extends BaseBuilderGeneratorIT {
    @Mock
    private RegularBuilderUserPreferenceDialogOpener regularBuilderUserPreferenceDialogOpener;

    @BeforeMethod
    public void beforeMethod() throws JavaModelException {
        super.init();
        underTest = new GenerateRegularBuilderHandler();

        given(preferenceStore.getBoolean("org.helospark.builder.createPublicDefaultConstructor")).willReturn(true);
        given(preferenceStore.getBoolean("org.helospark.builder.showFieldFilterDialogForRegularBuilder")).willReturn(true);
    }

    @AfterMethod
    public void afterMethod() throws Exception {
        super.tearDown();
    }

    @Override
    protected void diContainerOverrides() {
        super.diContainerOverrides();
        DiContainer.addTestDependency(regularBuilderUserPreferenceDialogOpener);
    }

    @Test(dataProvider = "testCasesForRegularBuilder")
    public void test(String inputFile, String expectedOutputFile, boolean createDefaultConstructor) throws Exception {
        // GIVEN
        RegularBuilderDialogData dialogResult = RegularBuilderDialogData.builder()
                                                                        .withAddJacksonDeserializeAnnotation(false)
                                                                        .withShouldCreateCopyMethod(false)
                                                                        .withCreateDefaultConstructor(createDefaultConstructor)
                                                                        .withRegularBuilderFieldIncludeFieldIncludeDomains(
                                                                                                                           Arrays.asList(new RegularBuilderFieldIncludeFieldIncludeDomain(
                                                                                                                                   true, false,
                                                                                                                                   "from"),
                                                                                                                                         new RegularBuilderFieldIncludeFieldIncludeDomain(
                                                                                                                                                 true,
                                                                                                                                                 false,
                                                                                                                                                 "to")))
                                                                        .build();
        given(regularBuilderUserPreferenceDialogOpener.open(any())).willReturn(Optional.of(dialogResult));

        underTest = new GenerateRegularBuilderHandler();
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
                { "default_constructor/mail_input.tjava", "default_constructor/mail_output_with_default_constructor.tjava", true },
                { "default_constructor/mail_input.tjava", "default_constructor/mail_output_without_default_constructor.tjava", false }
        };
    }

}