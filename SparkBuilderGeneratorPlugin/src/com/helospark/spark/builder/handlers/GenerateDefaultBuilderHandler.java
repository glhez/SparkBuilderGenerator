package com.helospark.spark.builder.handlers;

import static com.helospark.spark.builder.preferences.PluginPreferenceList.DEFAULT_BUILDER_GENERATOR;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.helospark.spark.builder.DiContainer;
import com.helospark.spark.builder.preferences.PreferencesManager;

/**
 * Eclipse handler to generate default builder.
 * 
 * @author helospark
 */
public class GenerateDefaultBuilderHandler extends AbstractHandler {
    private GenerateBuilderExecutor generateBuilderExecutor;
    private PreferencesManager preferencesManager;

    public GenerateDefaultBuilderHandler() {
        this(DiContainer.getDependency(StateInitializerGenerateBuilderExecutorDecorator.class),
                DiContainer.getDependency(PreferencesManager.class));
    }

    public GenerateDefaultBuilderHandler(GenerateBuilderExecutor generateBuilderExecutor, PreferencesManager preferencesManager) {
        this.generateBuilderExecutor = generateBuilderExecutor;
        this.preferencesManager = preferencesManager;
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        BuilderType defaultBuilderType = preferencesManager.getEnumValue(DEFAULT_BUILDER_GENERATOR);
        generateBuilderExecutor.execute(event, defaultBuilderType);
        return null;
    }

}
