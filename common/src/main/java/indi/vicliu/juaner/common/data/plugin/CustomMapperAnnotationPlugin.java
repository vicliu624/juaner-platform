/**
 * @Auther: vicliu
 * Date: 2021/3/11 下午8:52
 * @Description:
 */

package indi.vicliu.juaner.common.data.plugin;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.plugins.MapperAnnotationPlugin;

import java.util.List;

public class CustomMapperAnnotationPlugin extends MapperAnnotationPlugin {

    @Override
    public boolean validate(List<String> warnings){
        return true;
    }

    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        interfaze.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Repository"));
        interfaze.addAnnotation("@Repository");
        return true;
    }
}
