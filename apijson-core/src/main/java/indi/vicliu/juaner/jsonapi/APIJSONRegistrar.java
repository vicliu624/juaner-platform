/**
 * @Auther: vicliu
 * Date: 2021/2/18 下午7:00
 * @Description:
 */

package indi.vicliu.juaner.jsonapi;


import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class APIJSONRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private ResourceLoader resourceLoader;

    private Environment environment;

    private Map<String,String> accessTables;

    APIJSONRegistrar() {
        accessTables = new HashMap<>();
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        registerJSONAPITable(importingClassMetadata, registry);
    }

    public void registerJSONAPITable(AnnotationMetadata metadata,
                                     BeanDefinitionRegistry registry) {
        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        scanner.setResourceLoader(this.resourceLoader);
//        Map<String, Object> attrs = metadata
//                .getAnnotationAttributes(EnableJSONAPI.class.getName());
        AnnotationTypeFilter annotationTypeFilter = new AnnotationTypeFilter(
                Table.class);
        scanner.addIncludeFilter(annotationTypeFilter);
//        Set<String> basePackages = getBasePackages(metadata);
//        for (String basePackage : basePackages) {
//            Set<BeanDefinition> candidateComponents = scanner
//                    .findCandidateComponents(basePackage);
//            for (BeanDefinition candidateComponent : candidateComponents) {
//                if (candidateComponent instanceof AnnotatedBeanDefinition) {
//                    // verify annotated class is an interface
//                    AnnotatedBeanDefinition beanDefinition = (AnnotatedBeanDefinition) candidateComponent;
//                    AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
//                    Assert.isTrue(annotationMetadata.isConcrete(),
//                            "@JSONAPITable can only be specified on an concrete class");
//                    Map<String, Object> attributes = annotationMetadata
//                            .getAnnotationAttributes(
//                                    JSONAPITable.class.getCanonicalName());
//                    String name = getTableName(attributes);
//                    try {
//                        accessTables.put(Class.forName(annotationMetadata.getClassName()).getSimpleName(),name);
//                    } catch (ClassNotFoundException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
    }

    protected ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
            @Override
            protected boolean isCandidateComponent(
                    AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                if (beanDefinition.getMetadata().isIndependent()) {
                    if (!beanDefinition.getMetadata().isAnnotation()) {
                        isCandidate = true;
                    }
                }
                return isCandidate;
            }
        };
    }

    protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
        Map<String, Object> attributes = importingClassMetadata
                .getAnnotationAttributes(EnableAPIJSON.class.getCanonicalName());

        Set<String> basePackages = new HashSet<>();
        for (String pkg : (String[]) attributes.get("value")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (String pkg : (String[]) attributes.get("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (Class<?> clazz : (Class[]) attributes.get("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }

        if (basePackages.isEmpty()) {
            basePackages.add(
                    ClassUtils.getPackageName(importingClassMetadata.getClassName()));
        }
        return basePackages;
    }

    private String getTableName(Map<String, Object> param) {
        if (param == null) {
            return null;
        }
        String value = (String) param.get("realTableName");
        if (!StringUtils.hasText(value)) {
            throw new IllegalStateException("Either 'name' or 'value' must be provided in @"
                    + Table.class.getSimpleName());
        }else {
            return value;
        }
    }

    public Map<String, String> getAccessTables() {
        return accessTables;
    }
}
