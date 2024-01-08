package com.sun.myPlus;

import com.jiujie.framework.adapter.vo.ResponseResult;
import com.jiujie.framework.mybatis.dao.pojo.Page;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.XmlConstants;
import org.mybatis.generator.config.TableConfiguration;

import java.util.*;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

public class ServiceAndControllerGeneratorPlugin extends PluginAdapter {

    private String dfer;

    // 项目目录，一般为 src/main/java
    private String targetProject;

    private String apiTargetProject;

    private String webTargetProject;

    private String xmlTargetProject;

    // service包名，如：com.thinkj2ee.cms.service.service
    private String servicePackage;

    // service实现类包名，如：com.thinkj2ee.cms.service.service.impl
    private String serviceImplPackage;
    // Controlle类包名，如：com.thinkj2ee.cms.service.controller
    private String controllerPackage;

    private String daoPackage;

    private String xmlPackage;
    // service接口名前缀
    private String servicePreffix;

    // service接口名后缀
    private String serviceSuffix;

    // service接口的父接口
    //private String superServiceInterface;

    // service实现类的父类
    private String superServiceImpl;
    // controller类的父类
    private String superController;

    private String classRequestMapping;

    // dao接口基类
    private String superDaoInterface;

    // Example类的包名

    private String recordType;

    private String modelName;

    private String entityName;
    private String fullEntityName;
    private String entityConditionName;
    private String fullEntityConditionName;

    private String voExtPackage;
    private String VoName;
    private String fullVoName;
    private String voExtName;
    private String fullVoExtName;


    private String description;
    private String detailTable;
    private String mainTable;

    private String daoName;
    private String fullDaoName;
    private String fullServiceName;
    private String serviceImplName;
    private String fullServiceImplName;
    private String fullControllerName;

    public ServiceAndControllerGeneratorPlugin() {
    }

    @Override
    public boolean validate(List<String> warnings) {
        boolean valid = true;

       /* if (!stringHasValue(properties
                .getProperty("targetProject"))) { //$NON-NLS-1$
            warnings.add(getString("ValidationError.18", //$NON-NLS-1$
                    "MapperConfigPlugin", //$NON-NLS-1$
                    "targetProject")); //$NON-NLS-1$
            valid = false;
        }
        if (!stringHasValue(properties.getProperty("servicePackage"))) { //$NON-NLS-1$
            warnings.add(getString("ValidationError.18", //$NON-NLS-1$
                    "MapperConfigPlugin", //$NON-NLS-1$
                    "servicePackage")); //$NON-NLS-1$
            valid = false;
        }
        if (!stringHasValue(properties.getProperty("serviceImplPackage"))) { //$NON-NLS-1$
            warnings.add(getString("ValidationError.18", //$NON-NLS-1$
                    "MapperConfigPlugin", //$NON-NLS-1$
                    "serviceImplPackage")); //$NON-NLS-1$
            valid = false;
        }
*/
        targetProject = properties.getProperty("targetProject");
        apiTargetProject = properties.getProperty("apiTargetProject");
        webTargetProject = properties.getProperty("webTargetProject");
        xmlTargetProject = properties.getProperty("xmlTargetProject");

        voExtPackage = properties.getProperty("VoExtPackage");
        servicePackage = properties.getProperty("servicePackage");
        serviceImplPackage = properties.getProperty("serviceImplPackage");
        controllerPackage = properties.getProperty("controllerPackage");
        xmlPackage = properties.getProperty("xmlPackage");
        daoPackage = properties.getProperty("daoPackage");

        servicePreffix = properties.getProperty("servicePreffix");
        servicePreffix = stringHasValue(servicePreffix) ? servicePreffix : "";
        serviceSuffix = properties.getProperty("serviceSuffix");
        serviceSuffix = stringHasValue(serviceSuffix) ? serviceSuffix : "";

        superServiceImpl = properties.getProperty("superServiceImpl");
        superDaoInterface = properties.getProperty("superDaoInterface");

        superController = properties.getProperty("superController");
        classRequestMapping = properties.getProperty("classRequestMapping");

        return valid;
    }

    @Override
    public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(IntrospectedTable introspectedTable) {
        List<GeneratedXmlFile> xmlFileList = new ArrayList<>();
        GeneratedXmlFile xmlFile = generateMapper(introspectedTable);
        xmlFileList.add(xmlFile);
        return xmlFileList;
    }

    private GeneratedXmlFile generateMapper(IntrospectedTable introspectedTable) {
        String baseRecordType = introspectedTable.getBaseRecordType();


        Document document = new Document(XmlConstants.MYBATIS3_MAPPER_PUBLIC_ID, XmlConstants.MYBATIS3_MAPPER_SYSTEM_ID);
        XmlElement mapper = new XmlElement("mapper");
        mapper.addAttribute(new Attribute("namespace", fullDaoName));
        document.setRootElement(mapper);

        if (mainTable!=null  || detailTable!=null){
            XmlElement sql1 = new XmlElement("sql");
            sql1.addAttribute(new Attribute("id", "condition"));
            mapper.addElement(sql1);

            XmlElement sql2 = new XmlElement("sql");
            sql2.addAttribute(new Attribute("id", "from"));
            String tableMain="";
            String tableDetail="";
            TableConfiguration tableConfiguration = introspectedTable.getTableConfiguration();
            String tableName = tableConfiguration.getTableName();
            if (detailTable!=null){
                tableMain = tableName;
                tableDetail = tableName + "_detail";
            }
            if (mainTable!=null){
                tableMain = tableName.substring(0,tableName.lastIndexOf("_"));
                tableDetail = tableName;
            }
            TextElement sqlTextElement = new TextElement("FROM\n" +
                    "        "+tableMain+" M\n" +
                    "        INNER JOIN "+tableDetail+" d ON d.main_id = M.id");
            sql2.addElement(sqlTextElement);
            mapper.addElement(sql2);


            XmlElement selectCount = new XmlElement("select");
            if (mainTable!=null){
                selectCount.addAttribute(new Attribute("id", "countJoinMain"));
            }
            if (detailTable!=null){
                selectCount.addAttribute(new Attribute("id", "countIncludeDetail"));
            }
            selectCount.addAttribute(new Attribute("resultType", "long"));
            selectCount.addAttribute(new Attribute("parameterType", "hashmap"));
            TextElement textElement = new TextElement(" SELECT count(*)");
            selectCount.addElement(textElement);
            XmlElement include1 = new XmlElement("include");
            include1.addAttribute(new Attribute("refid", "from"));
            XmlElement include2 = new XmlElement("include");
            include2.addAttribute(new Attribute("refid", "condition"));
            selectCount.addElement(include1);
            selectCount.addElement(include2);
            mapper.addElement(selectCount);

            XmlElement resultMap = new XmlElement("resultMap");
            resultMap.addAttribute(new Attribute("id", modelName));
            resultMap.addAttribute(new Attribute("autoMapping", "true"));
            resultMap.addAttribute(new Attribute("type", fullVoExtName));
            XmlElement resultMapId = new XmlElement("id");
            resultMapId.addAttribute(new Attribute("property", "id"));
            resultMapId.addAttribute(new Attribute("column", "id"));
            resultMap.addElement(resultMapId);
            if (mainTable!=null){
                XmlElement association = new XmlElement("association");
                association.addAttribute(new Attribute("property", "main"));
                association.addAttribute(new Attribute("autoMapping", "true"));
                association.addAttribute(new Attribute("javaType", voExtPackage+"."+mainTable+"VOExt"));
                XmlElement associationId = new XmlElement("id");
                associationId.addAttribute(new Attribute("property", "id"));
                associationId.addAttribute(new Attribute("column", "main_id"));
                association.addElement(associationId);
                resultMap.addElement(association);
            }
            if (detailTable!=null){
                XmlElement collection = new XmlElement("collection");
                collection.addAttribute(new Attribute("property", "detailList"));
                collection.addAttribute(new Attribute("autoMapping", "true"));
                collection.addAttribute(new Attribute("ofType", voExtPackage+"."+detailTable+"VOExt"));
                XmlElement collectionId = new XmlElement("id");
                collectionId.addAttribute(new Attribute("property", "id"));
                collectionId.addAttribute(new Attribute("column", "detail_id"));
                collection.addElement(collectionId);
                resultMap.addElement(collection);
            }
            mapper.addElement(resultMap);

            XmlElement selectList = new XmlElement("select");
            if (mainTable!=null){
                selectList.addAttribute(new Attribute("id", "findListJoinMain"));
            }
            if (detailTable!=null){
                selectList.addAttribute(new Attribute("id", "findListIncludeDetail"));
            }
            selectList.addAttribute(new Attribute("resultMap", modelName));
            selectList.addAttribute(new Attribute("parameterType", "hashmap"));
            String selectPrefixSql="";
            if (mainTable!=null){
                selectPrefixSql = "SELECT d.*, M.*,m.id as main_id";
            }
            if (detailTable!=null){
                selectPrefixSql = "SELECT M.*,d.*,d.id as detail_id";
            }
            TextElement selectListTextElement = new TextElement(selectPrefixSql);
            selectList.addElement(selectListTextElement);
            selectList.addElement(include1);
            selectList.addElement(include2);
            XmlElement selectListChoose = new XmlElement("choose");
            selectList.addElement(selectListChoose);
            XmlElement selectListWhen = new XmlElement("when");
            selectListWhen.addAttribute(new Attribute("test", "sort!=null"));
            TextElement selectListWhenTextElement = new TextElement("<![CDATA[${sort}]]>");
            selectListWhen.addElement(selectListWhenTextElement);
            selectListChoose.addElement(selectListWhen);
            XmlElement selectListOtherwise = new XmlElement("otherwise");
            TextElement selectListOtherwiseTextElement = new TextElement("order by d.create_ts desc,d.id");
            selectListOtherwise.addElement(selectListOtherwiseTextElement);
            XmlElement selectListPage = new XmlElement("if");
            selectList.addElement(selectListPage);
            selectListPage.addAttribute(new Attribute("test", "page != null and page.pageSize > 0"));
            TextElement selectListPageTextElement = new TextElement("<![CDATA[ limit #{page.pageSize} offset (#{page.recordStart}-1) ]]>");
            selectListPage.addElement(selectListPageTextElement);
            mapper.addElement(selectList);
        }

        GeneratedXmlFile xmlFile = new GeneratedXmlFile(document, modelName + "Mapper.xml", xmlPackage, xmlTargetProject, false, context.getXmlFormatter());
        return xmlFile;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        recordType = introspectedTable.getBaseRecordType();
        fullEntityName = recordType;
        entityName = recordType.substring(recordType.lastIndexOf(".") + 1);
        entityConditionName = entityName + "Condition";
        fullEntityConditionName = recordType.substring(0, recordType.lastIndexOf(".") + 1) + entityName + "Condition";
        modelName = entityName.replace("Entity", "");

        fullServiceName = servicePackage + "." + servicePreffix + modelName + serviceSuffix;

        serviceImplName = modelName + serviceSuffix;
        fullServiceImplName = serviceImplPackage + "." + serviceImplName;

        fullControllerName = controllerPackage.concat(".").concat(modelName).concat("Controller");

        daoName = modelName + "Mapper";
        fullDaoName = daoPackage + "." + daoName;


        //获取table内property属性值
        TableConfiguration tableConfiguration = introspectedTable.getTableConfiguration();
        description = tableConfiguration.getProperty("description");

        detailTable = tableConfiguration.getProperty("detailTable");
        mainTable = tableConfiguration.getProperty("mainTable");

        String fullVOName = tableConfiguration.getProperty("domainVOName");
        VoName = fullVOName.substring(fullVOName.lastIndexOf(".") + 1);
        fullVoName = voExtPackage + "." + VoName;

        voExtName = VoName + "Ext";
        fullVoExtName = voExtPackage + "." + voExtName;

        List<GeneratedJavaFile> answer = new ArrayList<>();
        GeneratedJavaFile gjf = generateServiceInterface(introspectedTable);
        GeneratedJavaFile gjf2 = generateServiceImpl(introspectedTable);
        GeneratedJavaFile gjf3 = generateController(introspectedTable);
        GeneratedJavaFile gjf4 = generateVOExt(introspectedTable);
        GeneratedJavaFile gjf5 = generateDaoInterface(introspectedTable);
        answer.add(gjf);
        answer.add(gjf2);
        answer.add(gjf3);
        answer.add(gjf4);
        answer.add(gjf5);
        return answer;
    }

    /**
     * 生成dao
     *
     * @param introspectedTable
     * @return
     */
    private GeneratedJavaFile generateDaoInterface(IntrospectedTable introspectedTable) {
        //接口名称
        Interface daoInterface = new Interface(new FullyQualifiedJavaType(fullDaoName));

        //添加导入类
        Set<FullyQualifiedJavaType> fullyQualifiedJavaTypeSet = new HashSet<>();
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("java.util.Map"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("java.util.List"));
        daoInterface.addImportedTypes(fullyQualifiedJavaTypeSet);

        daoInterface.setVisibility(JavaVisibility.PUBLIC);

        if (mainTable != null) {
            //添加方法
            Method method = new Method("countJoinMain");
            //设置返回值
            FullyQualifiedJavaType methodReturnType = new FullyQualifiedJavaType("long");
            method.setReturnType(methodReturnType);
            //设置参数
            FullyQualifiedJavaType paramType1 = new FullyQualifiedJavaType("Map<String, Object>");
            Parameter parameter = new Parameter(paramType1, "params");
            method.addParameter(parameter);
            daoInterface.addMethod(method);

            Method method1 = new Method("findListJoinMain");
            method1.setReturnType(new FullyQualifiedJavaType("List<?>"));
            method1.addParameter(new Parameter(new FullyQualifiedJavaType("Map<String, Object>"), "params"));
            daoInterface.addMethod(method1);
        }

        if (detailTable != null) {
            //添加方法
            Method method = new Method("countIncludeDetail");
            //设置返回值
            FullyQualifiedJavaType methodReturnType = new FullyQualifiedJavaType("long");
            method.setReturnType(methodReturnType);
            //设置参数
            FullyQualifiedJavaType paramType1 = new FullyQualifiedJavaType("Map<String, Object>");
            Parameter parameter = new Parameter(paramType1, "params");
            method.addParameter(parameter);
            daoInterface.addMethod(method);

            Method method1 = new Method("findListIncludeDetail");
            method1.setReturnType(new FullyQualifiedJavaType("List<?>"));
            method1.addParameter(new Parameter(new FullyQualifiedJavaType("Map<String, Object>"), "params"));
            daoInterface.addMethod(method1);
        }


        GeneratedJavaFile gjf = new GeneratedJavaFile(daoInterface, targetProject, context.getJavaFormatter());
        return gjf;
    }


    /**
     * 生成voext包装拓展类
     *
     * @param introspectedTable
     * @return
     */
    private GeneratedJavaFile generateVOExt(IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType voext = new FullyQualifiedJavaType(fullVoExtName);
        TopLevelClass clazz = new TopLevelClass(voext);
        //描述类的作用域修饰符
        clazz.setVisibility(JavaVisibility.PUBLIC);

        Set<FullyQualifiedJavaType> fullyQualifiedJavaTypeSet = new HashSet<>();
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("lombok.Getter"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("lombok.Setter"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("java.util.List"));
        clazz.addImportedTypes(fullyQualifiedJavaTypeSet);

        //类添加注解
        clazz.addAnnotation("@Setter");
        clazz.addAnnotation("@Getter");


        //描述类 的实现接口类
        FullyQualifiedJavaType superFullyQualifiedJavaType = new FullyQualifiedJavaType(VoName);
        clazz.setSuperClass(superFullyQualifiedJavaType);

        if (detailTable != null) {
            Field field = new Field("detailList", new FullyQualifiedJavaType("List<" + detailTable + "VOExt>"));
            field.setVisibility(JavaVisibility.PRIVATE);
            clazz.addField(field);
        }

        if (mainTable != null) {
            Field field = new Field("main", new FullyQualifiedJavaType(mainTable + "VOExt"));
            field.setVisibility(JavaVisibility.PRIVATE);
            clazz.addField(field);
        }


        GeneratedJavaFile gjf = new GeneratedJavaFile(clazz, apiTargetProject, context.getJavaFormatter());
        return gjf;
    }


    /**
     * 生成service接口
     *
     * @param introspectedTable
     * @return
     */
    private GeneratedJavaFile generateServiceInterface(IntrospectedTable introspectedTable) {
        //接口名称
        Interface serviceInterface = new Interface(new FullyQualifiedJavaType(fullServiceName));

        //添加导入类
        Set<FullyQualifiedJavaType> fullyQualifiedJavaTypeSet = new HashSet<>();
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("com.jiujie.framework.mybatis.dao.pojo.Page"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("com.jiujie.mlh.production.utils.PageWithParams"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("com.jiujie.framework.adapter.vo.ResponseResult"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType(fullVoExtName));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("java.util.List"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("java.util.Set"));
        serviceInterface.addImportedTypes(fullyQualifiedJavaTypeSet);

        //类的作用域
        serviceInterface.setVisibility(JavaVisibility.PUBLIC);

        //添加方法
        Method method = new Method("getPage");
        //设置返回值
        FullyQualifiedJavaType methodReturnType = new FullyQualifiedJavaType("Page");
        method.setReturnType(methodReturnType);
        //设置参数
        FullyQualifiedJavaType paramType1 = new FullyQualifiedJavaType("PageWithParams<" + voExtName + ">");
        Parameter parameter = new Parameter(paramType1, "pageWithParams");
        method.addParameter(parameter);
        serviceInterface.addMethod(method);

        Method method1 = new Method("getOneById");
        method1.setReturnType(new FullyQualifiedJavaType(voExtName));
        method1.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "id"));
        serviceInterface.addMethod(method1);

        Method method2 = new Method("findList");
        method2.setReturnType(new FullyQualifiedJavaType("List<" + voExtName + ">"));
        method2.addParameter(new Parameter(new FullyQualifiedJavaType(voExtName), "voExtParams"));
        serviceInterface.addMethod(method2);

        Method method6 = new Method("getOneByCondition");
        method6.setReturnType(new FullyQualifiedJavaType(voExtName));
        method6.addParameter(new Parameter(new FullyQualifiedJavaType(voExtName), "voExtParams"));
        serviceInterface.addMethod(method6);

        Method method3 = new Method("count");
        method3.setReturnType(new FullyQualifiedJavaType("Long"));
        method3.addParameter(new Parameter(new FullyQualifiedJavaType(voExtName), "voExtParams"));
        serviceInterface.addMethod(method3);

        Method method4 = new Method("save");
        method4.setReturnType(new FullyQualifiedJavaType("ResponseResult"));
        method4.addParameter(new Parameter(new FullyQualifiedJavaType(voExtName), "voExt"));
        method4.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "currentUserId"));
        method4.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "realName"));
        serviceInterface.addMethod(method4);

        Method method5 = new Method("del");
        method5.setReturnType(new FullyQualifiedJavaType("ResponseResult"));
        method5.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "id"));
        method5.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "currentUserId"));
        serviceInterface.addMethod(method5);

        Method method7 = new Method("delByCondition");
        method7.setReturnType(new FullyQualifiedJavaType("ResponseResult"));
        method7.addParameter(new Parameter(new FullyQualifiedJavaType(voExtName), "voExtParams"));
        method7.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "currentUserId"));
        serviceInterface.addMethod(method7);

        Method method9 = new Method("batchSave");
        method9.setReturnType(new FullyQualifiedJavaType("ResponseResult"));
        method9.addParameter(new Parameter(new FullyQualifiedJavaType("Set<" + voExtName + ">"), "set"));
        method9.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "currentUserId"));
        method9.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "realName"));
        serviceInterface.addMethod(method9);



        if (mainTable!=null || detailTable!=null){
            String method8Name="";
            if (mainTable!=null){
                method8Name = "getPageJoinMain";
            }
            if (detailTable!=null){
                method8Name = "getPageIncludeDetail";
            }
            Method method8 = new Method(method8Name);
            method8.setReturnType(new FullyQualifiedJavaType("Page"));
            method8.addParameter(new Parameter(new FullyQualifiedJavaType("PageWithParams<" + voExtName + ">"), "pageWithParams"));
            serviceInterface.addMethod(method8);
        }


        GeneratedJavaFile gjf = new GeneratedJavaFile(serviceInterface, apiTargetProject, context.getJavaFormatter());
        return gjf;
    }

    /**
     * 生成serviceImpl实现类
     *
     * @param introspectedTable
     * @return
     */
    private GeneratedJavaFile generateServiceImpl(IntrospectedTable introspectedTable) {
        TopLevelClass clazz = new TopLevelClass(new FullyQualifiedJavaType(fullServiceImplName));

        Set<FullyQualifiedJavaType> fullyQualifiedJavaTypeSet = new HashSet<>();
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("com.jiujie.framework.mybatis.dao.pojo.Page"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("com.jiujie.mlh.production.utils.*"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("com.jiujie.framework.base.utils.UUIDUtils"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("com.jiujie.framework.adapter.vo.ResponseResult"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("javax.annotation.Resource"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("java.util.Map"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("java.util.HashMap"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("java.util.List"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("java.util.Set"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("java.util.ArrayList"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("org.springframework.stereotype.Service"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("org.springframework.transaction.annotation.Transactional"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("com.jiujie.framework.base.vo.BaseVO"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType(superServiceImpl));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType(fullVoExtName));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType(fullEntityConditionName));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType(fullEntityName));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType(fullDaoName));
        if (detailTable != null) {
            fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType(fullEntityName.substring(0,fullEntityName.lastIndexOf(".")+1) + detailTable + "Entity"));
            fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType(voExtPackage+"." + detailTable + "VOExt"));
        }
        clazz.addImportedTypes(fullyQualifiedJavaTypeSet);

        //描述类的作用域修饰符
        clazz.setVisibility(JavaVisibility.PUBLIC);

        //描述类 的实现接口类
        clazz.addSuperInterface(new FullyQualifiedJavaType(fullServiceName));
        clazz.setSuperClass(new FullyQualifiedJavaType("BaseServiceImpl"));

        //加入注解
        clazz.addAnnotation("@Service");
        clazz.addAnnotation("@Transactional(rollbackFor = Exception.class)");

        if (detailTable != null) {
            Field field = new Field(firstCharToLowCase(detailTable) + "Service", new FullyQualifiedJavaType("I" + detailTable + "Service"));
            field.setVisibility(JavaVisibility.PRIVATE);
            field.addAnnotation("@Resource");
            clazz.addField(field);
        }

        Field field1 = new Field(firstCharToLowCase(daoName), new FullyQualifiedJavaType(daoName));
        field1.setVisibility(JavaVisibility.PRIVATE);
        field1.addAnnotation("@Resource");
        clazz.addField(field1);

        Method method = new Method("getPage");
        //方法注解
        method.addAnnotation("@Override");
        //修饰符
        method.setVisibility(JavaVisibility.PUBLIC);
        //设置返回值
        method.setReturnType(new FullyQualifiedJavaType("Page"));
        //设置参数
        method.addParameter(new Parameter(new FullyQualifiedJavaType("PageWithParams<" + voExtName + ">"), "pageWithParams"));
        //方法体，逻辑代码
        List<String> bodyLineList = new ArrayList<>();
        bodyLineList.add(entityConditionName + " condition = new " + entityConditionName + "();");
        bodyLineList.add("AddCondition.addCondition(condition.createCriteria(),pageWithParams.getCondition());");
        bodyLineList.add("condition.setPage(pageWithParams);");
        bodyLineList.add("Page page = this.getMyBatisDao().selectPageByCondition(condition);");
        bodyLineList.add("page.setRecords(BeanCopy.createNewList(page.getRecords(), " + voExtName + ".class));");
        bodyLineList.add("return page;");
        method.addBodyLines(bodyLineList);
        clazz.addMethod(method);

        Method method1 = new Method("getOneById");
        method1.addAnnotation("@Override");
        method1.setVisibility(JavaVisibility.PUBLIC);
        method1.setReturnType(new FullyQualifiedJavaType(voExtName));
        method1.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "id"));
        List<String> bodyLineList1 = new ArrayList<>();
        bodyLineList1.add(entityName + " entity = this.getMyBatisDao().selectByPrimaryKey(" + entityName + ".class, id);");
        bodyLineList1.add("if (entity==null)return null;");
        bodyLineList1.add(voExtName + " voExt = new " + voExtName + "();");
        bodyLineList1.add("BeanCopy.copyProperties(entity,voExt);");
        if (detailTable != null) {
            bodyLineList1.add(detailTable + "VOExt detailVOExt = new " + detailTable + "VOExt();");
            bodyLineList1.add("detailVOExt.setMainId(id);");
            bodyLineList1.add("voExt.setDetailList(" + firstCharToLowCase(detailTable) + "Service.findList(detailVOExt));");
        }
        bodyLineList1.add("return voExt;");
        method1.addBodyLines(bodyLineList1);
        clazz.addMethod(method1);

        Method method2 = new Method("findList");
        method2.addAnnotation("@Override");
        method2.setVisibility(JavaVisibility.PUBLIC);
        method2.setReturnType(new FullyQualifiedJavaType("List<" + voExtName + ">"));
        method2.addParameter(new Parameter(new FullyQualifiedJavaType(voExtName), "voExtParams"));
        List<String> bodyLineList2 = new ArrayList<>();
        bodyLineList2.add(entityConditionName + " condition = new " + entityConditionName + "();");
        bodyLineList2.add("AddCondition.addCondition(condition.createCriteria(),voExtParams);");
        bodyLineList2.add("List<" + entityName + "> entityList = this.getMyBatisDao().selectByCondition(condition);");
        bodyLineList2.add("return BeanCopy.createNewList(entityList," + voExtName + ".class);");
        method2.addBodyLines(bodyLineList2);
        clazz.addMethod(method2);

        Method method6 = new Method("getOneByCondition");
        method6.addAnnotation("@Override");
        method6.setVisibility(JavaVisibility.PUBLIC);
        method6.setReturnType(new FullyQualifiedJavaType(voExtName));
        method6.addParameter(new Parameter(new FullyQualifiedJavaType(voExtName), "voExtParams"));
        List<String> bodyLineList6 = new ArrayList<>();
        bodyLineList6.add("List<" + voExtName + "> list = this.findList(voExtParams);");
        bodyLineList6.add("if (list.size()==1)return list.get(0);");
        bodyLineList6.add("return null;");
        method6.addBodyLines(bodyLineList6);
        clazz.addMethod(method6);

        Method method3 = new Method("count");
        method3.addAnnotation("@Override");
        method3.setVisibility(JavaVisibility.PUBLIC);
        method3.setReturnType(new FullyQualifiedJavaType("Long"));
        method3.addParameter(new Parameter(new FullyQualifiedJavaType(voExtName), "voExtParams"));
        List<String> bodyLineList3 = new ArrayList<>();
        bodyLineList3.add(entityConditionName + " condition = new " + entityConditionName + "();");
        bodyLineList3.add("AddCondition.addCondition(condition.createCriteria(),voExtParams);");
        bodyLineList3.add("return this.getMyBatisDao().countByCondition(condition);");
        method3.addBodyLines(bodyLineList3);
        clazz.addMethod(method3);

        Method method4 = new Method("save");
        method4.addAnnotation("@Override");
        method4.setVisibility(JavaVisibility.PUBLIC);
        method4.setReturnType(new FullyQualifiedJavaType("ResponseResult"));
        method4.addParameter(new Parameter(new FullyQualifiedJavaType(voExtName), "voExt"));
        method4.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "currentUserId"));
        method4.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "realName"));
        List<String> bodyLineList4 = new ArrayList<>();
        bodyLineList4.add(entityName + " entity = new " + entityName + "();");
        bodyLineList4.add("BeanCopy.copyProperties(voExt,entity);");
        bodyLineList4.add("SqlUtil.setOperateTimeAndUserId(entity,currentUserId,realName);");
        bodyLineList4.add("String mainId;");
        bodyLineList4.add("if (StringUtils.isNotBlank(voExt.getId())) {\n" +
                "            mainId = voExt.getId();\n" +
                "            this.getMyBatisDao().updateNotNull(entity);\n" +
                "        } else {\n" +
                "            mainId = UUIDUtils.getStringValue();\n" +
                "            entity.setId(mainId);\n" +
                "            this.getMyBatisDao().insert(entity);\n" +
                "        }");
        if (detailTable != null) {
            bodyLineList4.add("List<" + detailTable + "Entity> addDetailEntityList = new ArrayList<>();\n" +
                    "        List<" + detailTable + "Entity> updateDetailEntityList = new ArrayList<>();\n" +
                    "        List<" + detailTable + "Entity> delEntityList = new ArrayList<>();\n" +
                    "        List<" + detailTable + "VOExt> detailList = voExt.getDetailList();\n" +
                    "        if (detailList != null && detailList.size() > 0) {\n" +
                    "            for (" + detailTable + "VOExt detailVOExt : detailList) {\n" +
                    "                detailVOExt.setMainId(mainId);\n" +
                    "                " + detailTable + "Entity detailEntity = new " + detailTable + "Entity();\n" +
                    "                BeanCopy.copyProperties(detailVOExt, detailEntity);\n" +
                    "                SqlUtil.setOperateTimeAndUserId(detailEntity, currentUserId, realName);\n" +
                    "                if (StringUtils.isNotBlank(detailEntity.getId())) {\n" +
                    "                    if (Integer.valueOf(BaseVO.RowStateEnum.DELETED.getValue()).equals(detailEntity.getRowState()) ){\n" +
                    "                        delEntityList.add(detailEntity);\n" +
                    "                    }else {\n" +
                    "                        updateDetailEntityList.add(detailEntity);\n" +
                    "                    }\n" +
                    "                } else {\n" +
                    "                    detailEntity.setId(null);\n" +
                    "                    addDetailEntityList.add(detailEntity);\n" +
                    "                }\n" +
                    "            }\n" +
                    "        }\n" +
                    "        this.getMyBatisDao().insertList(addDetailEntityList);\n" +
                    "        this.getMyBatisDao().updateNotNull(updateDetailEntityList);\n" +
                    "        this.getMyBatisDao().delete(delEntityList);");
        }
        bodyLineList4.add("return new ResponseResult(true,\"\",\"提交成功\");");
        method4.addBodyLines(bodyLineList4);
        clazz.addMethod(method4);

        Method method5 = new Method("del");
        method5.addAnnotation("@Override");
        method5.setVisibility(JavaVisibility.PUBLIC);
        method5.setReturnType(new FullyQualifiedJavaType("ResponseResult"));
        method5.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "id"));
        method5.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "currentUserId"));
        List<String> bodyLineList5 = new ArrayList<>();
        bodyLineList5.add("this.getMyBatisDao().deleteByPrimaryKey(" + entityName + ".class,id);");
        if (detailTable != null) {
            bodyLineList5.add(detailTable + "VOExt detailVOExt = new " + detailTable + "VOExt();");
            bodyLineList5.add("detailVOExt.setMainId(id);");
            bodyLineList5.add(firstCharToLowCase(detailTable) + "Service.delByCondition(detailVOExt, currentUserId);");
        }
        bodyLineList5.add("return new ResponseResult(true, \"\", \"删除成功\");");
        method5.addBodyLines(bodyLineList5);
        clazz.addMethod(method5);

        Method method7 = new Method("delByCondition");
        method7.addAnnotation("@Override");
        method7.setVisibility(JavaVisibility.PUBLIC);
        method7.setReturnType(new FullyQualifiedJavaType("ResponseResult"));
        method7.addParameter(new Parameter(new FullyQualifiedJavaType(voExtName), "voExtParams"));
        method7.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "currentUserId"));
        List<String> bodyLineList7 = new ArrayList<>();
        bodyLineList7.add(entityConditionName + " condition = new " + entityConditionName + "();");
        bodyLineList7.add("AddCondition.addCondition(condition.createCriteria(), voExtParams);");
        bodyLineList7.add("this.getMyBatisDao().deleteByCondition(condition);");
        bodyLineList7.add("return new ResponseResult(true, \"\", \"删除成功\");");
        method7.addBodyLines(bodyLineList7);
        clazz.addMethod(method7);

        Method method9 = new Method("batchSave");
        method9.addAnnotation("@Override");
        method9.setVisibility(JavaVisibility.PUBLIC);
        method9.setReturnType(new FullyQualifiedJavaType("ResponseResult"));
        method9.addParameter(new Parameter(new FullyQualifiedJavaType("Set<" + voExtName + ">"), "set"));
        method9.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "currentUserId"));
        method9.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "realName"));
        List<String> bodyLineList9 = new ArrayList<>();
        bodyLineList9.add("List<"+entityName+"> addList = new ArrayList<>();");
        bodyLineList9.add("List<"+entityName+"> updateList = new ArrayList<>();");
        bodyLineList9.add("for ("+voExtName+" voExt : set) {\n" +
                "            "+entityName+" en = new "+entityName+"();\n" +
                "            BeanCopy.copyProperties(voExt,en);\n" +
                "            SqlUtil.setOperateTimeAndUserId(en,currentUserId,realName);\n" +
                "            if (StringUtils.isBlank(voExt.getId())){\n" +
                "                en.setId(null);\n" +
                "                addList.add(en);\n" +
                "            }else {\n" +
                "                updateList.add(en);\n" +
                "            }\n" +
                "        }");

        bodyLineList9.add(" this.getMyBatisDao().insertList(addList);\n" +
                "        this.getMyBatisDao().updateNotNull(updateList);\n" +
                "        return new ResponseResult(true,\"\",\"批量保存完成\");");
        method9.addBodyLines(bodyLineList9);
        clazz.addMethod(method9);

        if (mainTable!=null || detailTable!=null){
            String method8Name="";
            if (mainTable!=null){
                method8Name = "getPageJoinMain";
            }
            if (detailTable!=null){
                method8Name = "getPageIncludeDetail";
            }
            Method method8 = new Method(method8Name);
            //方法注解
            method8.addAnnotation("@Override");
            //修饰符
            method8.setVisibility(JavaVisibility.PUBLIC);
            //设置返回值
            method8.setReturnType(new FullyQualifiedJavaType("Page"));
            //设置参数
            method8.addParameter(new Parameter(new FullyQualifiedJavaType("PageWithParams<" + voExtName + ">"), "pageWithParams"));
            //方法体，逻辑代码
            List<String> bodyLineList8 = new ArrayList<>();
            bodyLineList8.add("Map<String, Object> params = new HashMap<>();");
            bodyLineList8.add("params.put(\"condition\", pageWithParams.getCondition());");
            bodyLineList8.add("if (!StringUtils.isAnyBlank(pageWithParams.getSortType(), pageWithParams.getSortBy())) {");
            bodyLineList8.add("params.put(\"sort\", String.format(\"order by m.%s %s\", pageWithParams.getSortBy(), pageWithParams.getSortType()));");
            bodyLineList8.add("}");
            String bodyLineList8Count="";
            if (mainTable!=null){
                bodyLineList8Count = "countJoinMain";
            }
            if (detailTable!=null){
                bodyLineList8Count = "countIncludeDetail";
            }
            bodyLineList8.add("long count = "+firstCharToLowCase(daoName)+"."+bodyLineList8Count+"(params);");
            bodyLineList8.add("pageWithParams.setTotalRecord(count);");
            bodyLineList8.add("if (count == 0) {");
            bodyLineList8.add("return pageWithParams;");
            bodyLineList8.add("}");
            bodyLineList8.add("params.put(\"page\", pageWithParams);");
            String bodyLineList8FindList="";
            if (mainTable!=null){
                bodyLineList8FindList = "findListJoinMain";
            }
            if (detailTable!=null){
                bodyLineList8FindList = "findListIncludeDetail";
            }
            bodyLineList8.add("pageWithParams.setRecords("+firstCharToLowCase(daoName)+"."+bodyLineList8FindList+"(params));");
            bodyLineList8.add("return pageWithParams;");
            method8.addBodyLines(bodyLineList8);
            clazz.addMethod(method8);
        }


        GeneratedJavaFile gjf2 = new GeneratedJavaFile(clazz, targetProject, context.getJavaFormatter());
        return gjf2;
    }


    // 生成controller类
    private GeneratedJavaFile generateController(IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType controller = new FullyQualifiedJavaType(fullControllerName);
        TopLevelClass clazz = new TopLevelClass(controller);

        Set<FullyQualifiedJavaType> fullyQualifiedJavaTypeSet = new HashSet<>();
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("com.jiujie.framework.mybatis.dao.pojo.Page"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("org.springframework.web.bind.annotation.*"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("com.jiujie.mlh.production.utils.PageWithParams"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("com.jiujie.framework.adapter.vo.ResponseResult"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("java.util.List"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("java.util.Set"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("io.swagger.annotations.Api"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("com.github.xiaoymin.knife4j.annotations.ApiSort"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("javax.annotation.Resource"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("com.github.xiaoymin.knife4j.annotations.ApiOperationSupport"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("io.swagger.annotations.ApiOperation"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType(superController));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType(fullVoExtName));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType(fullServiceName));
        clazz.addImportedTypes(fullyQualifiedJavaTypeSet);

        //描述类的作用域修饰符
        clazz.setVisibility(JavaVisibility.PUBLIC);

        clazz.setSuperClass(new FullyQualifiedJavaType("BaseController"));

        clazz.addAnnotation("@RestController");
        clazz.addAnnotation("@RequestMapping(\"" + classRequestMapping + "/" + modelName + "\")");
        clazz.addAnnotation("@Api(tags = \"" + description + "\")");
        clazz.addAnnotation("@ApiSort(0)");


        //添加Service成员变量
        String fieldServiceName = firstCharToLowCase(serviceImplName);
        Field daoField = new Field(fieldServiceName, new FullyQualifiedJavaType(fullServiceName));
        //描述成员属性 的注解
        daoField.addAnnotation("@Resource");
        //描述成员属性修饰符
        daoField.setVisibility(JavaVisibility.PRIVATE);
        clazz.addField(daoField);

        int ApiOperationSupportOrder = 0;

        Method method = new Method("getPage");
        method.setVisibility(JavaVisibility.PUBLIC);
        method.addAnnotation("@ApiOperation(\"单表分页\")");
        method.addAnnotation("@ApiOperationSupport(order = " + (++ApiOperationSupportOrder) + ")");
        method.addAnnotation("@PostMapping(\"/getPage.json\")");
        method.setReturnType(new FullyQualifiedJavaType("Page"));
        Parameter parameter = new Parameter(new FullyQualifiedJavaType("PageWithParams<" + voExtName + ">"), "pageWithParams");
        parameter.addAnnotation("@RequestBody");
        method.addParameter(parameter);
        List<String> bodyLineList = new ArrayList<>();
        bodyLineList.add("return " + fieldServiceName + ".getPage(pageWithParams);");
        method.addBodyLines(bodyLineList);
        clazz.addMethod(method);

        Method method1 = new Method("getOneById");
        method1.setVisibility(JavaVisibility.PUBLIC);
        method1.addAnnotation("@ApiOperation(\"根据id获取一条数据\")");
        method1.addAnnotation("@ApiOperationSupport(order = " + (++ApiOperationSupportOrder) + ")");
        method1.addAnnotation("@GetMapping(\"/getOneById.json\")");
        method1.setReturnType(new FullyQualifiedJavaType(voExtName));
        Parameter parameter1 = new Parameter(new FullyQualifiedJavaType("String"), "id");
        parameter1.addAnnotation("@RequestParam(\"id\")");
        method1.addParameter(parameter1);
        List<String> bodyLineList1 = new ArrayList<>();
        bodyLineList1.add("return " + fieldServiceName + ".getOneById(id);");
        method1.addBodyLines(bodyLineList1);
        clazz.addMethod(method1);

        Method method2 = new Method("findList");
        method2.setVisibility(JavaVisibility.PUBLIC);
        method2.addAnnotation("@ApiOperation(\"获取数据列表\")");
        method2.addAnnotation("@ApiOperationSupport(order = " + (++ApiOperationSupportOrder) + ")");
        method2.addAnnotation("@PostMapping(\"/findList.json\")");
        method2.setReturnType(new FullyQualifiedJavaType("List<" + voExtName + ">"));
        Parameter parameter2 = new Parameter(new FullyQualifiedJavaType(voExtName), "voExtParams");
        parameter2.addAnnotation("@RequestBody");
        method2.addParameter(parameter2);
        List<String> bodyLineList2 = new ArrayList<>();
        bodyLineList2.add("return " + fieldServiceName + ".findList(voExtParams);");
        method2.addBodyLines(bodyLineList2);
        clazz.addMethod(method2);

        Method method6 = new Method("getOneByCondition");
        method6.setVisibility(JavaVisibility.PUBLIC);
        method6.addAnnotation("@ApiOperation(\"根据其它查询条件获取一条数据\")");
        method6.addAnnotation("@ApiOperationSupport(order = " + (++ApiOperationSupportOrder) + ")");
        method6.addAnnotation("@PostMapping(\"/getOneByCondition.json\")");
        method6.setReturnType(new FullyQualifiedJavaType(voExtName));
        Parameter parameter6 = new Parameter(new FullyQualifiedJavaType(voExtName), "voExtParams");
        parameter6.addAnnotation("@RequestBody");
        method6.addParameter(parameter6);
        List<String> bodyLineList6 = new ArrayList<>();
        bodyLineList6.add("return " + fieldServiceName + ".getOneByCondition(voExtParams);");
        method6.addBodyLines(bodyLineList6);
        clazz.addMethod(method6);

        Method method3 = new Method("count");
        method3.setVisibility(JavaVisibility.PUBLIC);
        method3.addAnnotation("@ApiOperation(\"获取数量\")");
        method3.addAnnotation("@ApiOperationSupport(order = " + (++ApiOperationSupportOrder) + ")");
        method3.addAnnotation("@PostMapping(\"/count.json\")");
        method3.setReturnType(new FullyQualifiedJavaType("Long"));
        Parameter parameter3 = new Parameter(new FullyQualifiedJavaType(voExtName), "voExtParams");
        parameter3.addAnnotation("@RequestBody");
        method3.addParameter(parameter3);
        List<String> bodyLineList3 = new ArrayList<>();
        bodyLineList3.add("return " + fieldServiceName + ".count(voExtParams);");
        method3.addBodyLines(bodyLineList3);
        clazz.addMethod(method3);

        Method method4 = new Method("save");
        method4.setVisibility(JavaVisibility.PUBLIC);
        method4.addAnnotation("@ApiOperation(\"新增和修改，id是空为新增，id有值为修改\")");
        method4.addAnnotation("@ApiOperationSupport(order = " + (++ApiOperationSupportOrder) + ")");
        method4.addAnnotation("@PostMapping(\"/save.json\")");
        method4.setReturnType(new FullyQualifiedJavaType("ResponseResult"));
        Parameter parameter4 = new Parameter(new FullyQualifiedJavaType(voExtName), "voExt");
        parameter4.addAnnotation("@RequestBody");
        method4.addParameter(parameter4);
        List<String> bodyLineList4 = new ArrayList<>();
        bodyLineList4.add("return " + fieldServiceName + ".save(voExt,getCurrentUserId(),getCurrentUser().getRealName());");
        method4.addBodyLines(bodyLineList4);
        clazz.addMethod(method4);

        Method method5 = new Method("del");
        method5.setVisibility(JavaVisibility.PUBLIC);
        method5.addAnnotation("@ApiOperation(\"根据id删除\")");
        method5.addAnnotation("@ApiOperationSupport(order = " + (++ApiOperationSupportOrder) + ")");
        method5.addAnnotation("@PostMapping(\"/del.json\")");
        method5.setReturnType(new FullyQualifiedJavaType("ResponseResult"));
        Parameter parameter5 = new Parameter(new FullyQualifiedJavaType("String"), "id");
        parameter5.addAnnotation("@RequestParam(\"id\")");
        method5.addParameter(parameter5);
        List<String> bodyLineList5 = new ArrayList<>();
        bodyLineList5.add("return " + fieldServiceName + ".del(id,getCurrentUserId());");
        method5.addBodyLines(bodyLineList5);
        clazz.addMethod(method5);

        Method method7 = new Method("batchSave");
        method7.setVisibility(JavaVisibility.PUBLIC);
        method7.addAnnotation("@ApiOperation(\"批量新增\")");
        method7.addAnnotation("@ApiOperationSupport(order = " + (++ApiOperationSupportOrder) + ")");
        method7.addAnnotation("@PostMapping(\"/batchSave.json\")");
        method7.setReturnType(new FullyQualifiedJavaType("ResponseResult"));
        Parameter parameter7 = new Parameter(new FullyQualifiedJavaType("Set<"+voExtName+">"), "set");
        parameter7.addAnnotation("@RequestBody");
        method7.addParameter(parameter7);
        List<String> bodyLineList7 = new ArrayList<>();
        bodyLineList7.add("return " + fieldServiceName + ".batchSave(set,getCurrentUserId(),getCurrentUser().getRealName());");
        method7.addBodyLines(bodyLineList7);
        clazz.addMethod(method7);


        if (mainTable!=null || detailTable!=null){
            String method8Name = "";
            if (mainTable!=null){
                method8Name = "getPageJoinMain";
            }
            if (detailTable!=null){
                method8Name = "getPageIncludeDetail";
            }
            Method method8 = new Method(method8Name);
            method8.setVisibility(JavaVisibility.PUBLIC);
            method8.addAnnotation("@ApiOperation(\"主从表分页\")");
            method8.addAnnotation("@ApiOperationSupport(order = " + (++ApiOperationSupportOrder) + ")");
            method8.addAnnotation("@PostMapping(\"/"+method8Name+".json\")");
            method8.setReturnType(new FullyQualifiedJavaType("Page"));
            Parameter parameter8 = new Parameter(new FullyQualifiedJavaType("PageWithParams<" + voExtName + ">"), "pageWithParams");
            parameter8.addAnnotation("@RequestBody");
            method8.addParameter(parameter8);
            List<String> bodyLineList8 = new ArrayList<>();
            bodyLineList8.add("return " + fieldServiceName + "."+method8Name+"(pageWithParams);");
            method8.addBodyLines(bodyLineList8);
            clazz.addMethod(method8);
        }


        GeneratedJavaFile gjf2 = new GeneratedJavaFile(clazz, webTargetProject, context.getJavaFormatter());
        return gjf2;
    }


    private String firstCharToLowCase(String str) {
        char[] chars = new char[1];
        //String str="ABCDE1234";
        chars[0] = str.charAt(0);
        String temp = new String(chars);
        if (chars[0] >= 'A' && chars[0] <= 'Z') {
            return str.replaceFirst(temp, temp.toLowerCase());
        }
        return str;
    }
}
