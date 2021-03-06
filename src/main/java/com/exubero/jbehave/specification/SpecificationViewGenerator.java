package com.exubero.jbehave.specification;

import com.exubero.jbehave.specification.model.ReportModel;
import com.exubero.jbehave.specification.model.StoryResultSet;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.jbehave.core.configuration.Keywords;
import org.jbehave.core.model.StoryMaps;
import org.jbehave.core.reporters.ReportsCount;
import org.jbehave.core.reporters.ViewGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Properties;

import static com.exubero.jbehave.specification.model.ReportModel.TITLE_PROP;

public class SpecificationViewGenerator implements ViewGenerator {

    private final StoryResultSet storyResultSet;
    private final Keywords keywords;

    public SpecificationViewGenerator(StoryResultSet storyResultSet, Keywords keywords) {
        this.storyResultSet = storyResultSet;
        this.keywords = keywords;
    }

    @Override
    public void generateMapsView(File outputDirectory, StoryMaps storyMaps, Properties viewResources) {
        throw new UnsupportedOperationException("generateMapsView");
    }

    @Override
    public void generateReportsView(File outputDirectory, List<String> formats, Properties viewResources) {
        if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
            throw new RuntimeException("Unable to create report directory " + outputDirectory.getAbsolutePath());
        }

        File specificationFile = new File(outputDirectory, "specification.html");
        try(Writer writer = new FileWriter(specificationFile)) {
            applySpecificationTemplate(viewResources, writer);
            System.out.println("Specification written to " + specificationFile.getCanonicalPath());
        } catch (IOException e) {
            throw new RuntimeException("Failed to write report " + specificationFile.getAbsolutePath(), e);
        }
    }

    private void applySpecificationTemplate(Properties viewResources, Writer writer) {
        MustacheFactory mustacheFactory = new DefaultMustacheFactory();
        Mustache mustache = mustacheFactory.compile("specification.mustache");
        mustache.execute(writer, new ReportModel(storyResultSet, keywords, viewResources));
    }

    @Override
    public ReportsCount getReportsCount() {
        return storyResultSet.summaryStatistics().getReportsCount();
    }

    @Override
    public Properties defaultViewProperties() {
        Properties properties = new Properties();
        properties.setProperty(TITLE_PROP, "Specifications");
        return properties;
    }


}
