package com.sun.myPlus;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.config.TableConfiguration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

public class ServiceAndControllerGeneratorPlugin extends PluginAdapter {

    // 项目目录，一般为 src/main/java
    private String targetProject;

    private String apiTargetProject;

    private String webTargetProject;

    // service包名，如：com.thinkj2ee.cms.service.service
    private String servicePackage;

    // service实现类包名，如：com.thinkj2ee.cms.service.service.impl
    private String serviceImplPackage;
    // Controlle类包名，如：com.thinkj2ee.cms.service.controller
    private String controllerPackage;
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

    private String fullServiceName;
    private String serviceImplName;
    private String fullServiceImplName;
    private String controllerName;

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
        voExtPackage = properties.getProperty("VoExtPackage");
        servicePackage = properties.getProperty("servicePackage");
        serviceImplPackage = properties.getProperty("serviceImplPackage");
        servicePreffix = properties.getProperty("servicePreffix");
        servicePreffix = stringHasValue(servicePreffix) ? servicePreffix : "";
        serviceSuffix = properties.getProperty("serviceSuffix");
        serviceSuffix = stringHasValue(serviceSuffix) ? serviceSuffix : "";
        //superServiceInterface = properties.getProperty("superServiceInterface");
        superServiceImpl = properties.getProperty("superServiceImpl");
        superDaoInterface = properties.getProperty("superDaoInterface");
        controllerPackage = properties.getProperty("controllerPackage");
        superController = properties.getProperty("superController");
        classRequestMapping = properties.getProperty("classRequestMapping");

        return valid;
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
        controllerName = controllerPackage.concat(".").concat(modelName).concat("Controller");

        //获取table内property属性值
        TableConfiguration tableConfiguration = introspectedTable.getTableConfiguration();
        description = tableConfiguration.getProperty("description");

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
        answer.add(gjf);
        answer.add(gjf2);
        answer.add(gjf3);
        answer.add(gjf4);
        return answer;
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
        clazz.addImportedTypes(fullyQualifiedJavaTypeSet);

        //类添加注解
        clazz.addAnnotation("@Setter");
        clazz.addAnnotation("@Getter");


        //描述类 的实现接口类
        FullyQualifiedJavaType superFullyQualifiedJavaType = new FullyQualifiedJavaType(VoName);
        clazz.setSuperClass(superFullyQualifiedJavaType);

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

        Method method3 = new Method("count");
        method3.setReturnType(new FullyQualifiedJavaType("Long"));
        method3.addParameter(new Parameter(new FullyQualifiedJavaType(voExtName), "voExtParams"));
        serviceInterface.addMethod(method3);

        Method method4 = new Method("save");
        method4.setReturnType(new FullyQualifiedJavaType("ResponseResult"));
        method4.addParameter(new Parameter(new FullyQualifiedJavaType(voExtName), "voExt"));
        method4.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "currentUserId"));
        serviceInterface.addMethod(method4);

        Method method5 = new Method("del");
        method5.setReturnType(new FullyQualifiedJavaType("ResponseResult"));
        method5.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "id"));
        method5.addParameter(new Parameter(new FullyQualifiedJavaType("String"), "currentUserId"));
        serviceInterface.addMethod(method5);


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
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("com.jiujie.framework.adapter.vo.ResponseResult"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("java.util.List"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("org.springframework.stereotype.Service"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("org.springframework.transaction.annotation.Transactional"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType(superServiceImpl));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType(fullVoExtName));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType(fullEntityConditionName));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType(fullEntityName));
        clazz.addImportedTypes(fullyQualifiedJavaTypeSet);

        //描述类的作用域修饰符
        clazz.setVisibility(JavaVisibility.PUBLIC);

        //描述类 的实现接口类
        clazz.addSuperInterface(new FullyQualifiedJavaType(fullServiceName));
        clazz.setSuperClass(new FullyQualifiedJavaType("BaseServiceImpl"));

        //加入注解
        clazz.addAnnotation("@Service");
        clazz.addAnnotation("@Transactional(rollbackFor = Exception.class)");

        /*String daoFieldType = introspectedTable.getMyBatis3JavaMapperType();
        String daoFieldName = firstCharToLowCase(daoFieldType.substring(daoFieldType.lastIndexOf(".") + 1));
        //描述类的成员属性
        Field daoField = new Field(daoFieldName, new FullyQualifiedJavaType(daoFieldType));
        //描述成员属性 的注解
        daoField.addAnnotation("@Autowired");
        //描述成员属性修饰符
        daoField.setVisibility(JavaVisibility.PRIVATE);
        clazz.addField(daoField);*/

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
        List<String> bodyLineList4 = new ArrayList<>();
        bodyLineList4.add(entityName + " entity = new " + entityName + "();");
        bodyLineList4.add("BeanCopy.copyProperties(voExt,entity);");
        bodyLineList4.add("SqlUtil.setOperateTimeAndUserId(entity,currentUserId);");
        bodyLineList4.add("if (StringUtils.isNotBlank(voExt.getId())) {\n" +
                "            this.getMyBatisDao().updateNotNull(entity);\n" +
                "        } else {\n" +
                "            entity.setId(null);\n" +
                "            this.getMyBatisDao().insert(entity);\n" +
                "        }");
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
        bodyLineList5.add("return new ResponseResult(true, \"\", \"删除成功\");");
        method5.addBodyLines(bodyLineList5);
        clazz.addMethod(method5);


        GeneratedJavaFile gjf2 = new GeneratedJavaFile(clazz, targetProject, context.getJavaFormatter());
        return gjf2;
    }


    // 生成controller类
    private GeneratedJavaFile generateController(IntrospectedTable introspectedTable) {
        FullyQualifiedJavaType controller = new FullyQualifiedJavaType(controllerName);
        TopLevelClass clazz = new TopLevelClass(controller);

        Set<FullyQualifiedJavaType> fullyQualifiedJavaTypeSet = new HashSet<>();
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("com.jiujie.framework.mybatis.dao.pojo.Page"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("org.springframework.web.bind.annotation.*"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("com.jiujie.mlh.production.utils.PageWithParams"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("com.jiujie.framework.adapter.vo.ResponseResult"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("java.util.List"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("io.swagger.annotations.Api"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("com.github.xiaoymin.knife4j.annotations.ApiSort"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("com.jiujie.mlh.production.pd.service.IQualityInspectionService"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("javax.annotation.Resource"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("com.github.xiaoymin.knife4j.annotations.ApiOperationSupport"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType("io.swagger.annotations.ApiOperation"));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType(superController));
        fullyQualifiedJavaTypeSet.add(new FullyQualifiedJavaType(fullVoExtName));
        clazz.addImportedTypes(fullyQualifiedJavaTypeSet);

        //描述类的作用域修饰符
        clazz.setVisibility(JavaVisibility.PUBLIC);

        clazz.setSuperClass(new FullyQualifiedJavaType("BaseController"));

        clazz.addAnnotation("@RestController");
        clazz.addAnnotation("@RequestMapping(\"" + classRequestMapping+"/"+ modelName + "\")");
        clazz.addAnnotation("@Api(tags = \""+description+"\")");
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
        method.addAnnotation("@ApiOperationSupport(order = "+(++ApiOperationSupportOrder)+")");
        method.addAnnotation("@PostMapping(\"/getPage.json\")");
        method.setReturnType(new FullyQualifiedJavaType("Page"));
        Parameter parameter = new Parameter(new FullyQualifiedJavaType("PageWithParams<" + voExtName + ">"), "pageWithParams");
        parameter.addAnnotation("@RequestBody");
        method.addParameter(parameter);
        List<String> bodyLineList = new ArrayList<>();
        bodyLineList.add("return "+fieldServiceName+".getPage(pageWithParams);");
        method.addBodyLines(bodyLineList);
        clazz.addMethod(method);

        Method method1 = new Method("getOneById");
        method1.setVisibility(JavaVisibility.PUBLIC);
        method1.addAnnotation("@ApiOperation(\"根据id获取一条数据\")");
        method1.addAnnotation("@ApiOperationSupport(order = "+(++ApiOperationSupportOrder)+")");
        method1.addAnnotation("@GetMapping(\"/getOneById.json\")");
        method1.setReturnType(new FullyQualifiedJavaType(voExtName));
        Parameter parameter1 = new Parameter(new FullyQualifiedJavaType("String"), "id");
        parameter1.addAnnotation("@RequestParam(\"id\")");
        method1.addParameter(parameter1);
        List<String> bodyLineList1 = new ArrayList<>();
        bodyLineList1.add("return "+fieldServiceName+".getOneById(id);");
        method1.addBodyLines(bodyLineList1);
        clazz.addMethod(method1);

        Method method2 = new Method("findList");
        method2.setVisibility(JavaVisibility.PUBLIC);
        method2.addAnnotation("@ApiOperation(\"获取数据列表\")");
        method2.addAnnotation("@ApiOperationSupport(order = "+(++ApiOperationSupportOrder)+")");
        method2.addAnnotation("@PostMapping(\"/findList.json\")");
        method2.setReturnType(new FullyQualifiedJavaType("List<" + voExtName + ">"));
        Parameter parameter2 = new Parameter(new FullyQualifiedJavaType(voExtName), "voExtParams");
        parameter2.addAnnotation("@RequestBody");
        method2.addParameter(parameter2);
        List<String> bodyLineList2 = new ArrayList<>();
        bodyLineList2.add("return "+fieldServiceName+".findList(voExtParams);");
        method2.addBodyLines(bodyLineList2);
        clazz.addMethod(method2);

        Method method3 = new Method("count");
        method3.setVisibility(JavaVisibility.PUBLIC);
        method3.addAnnotation("@ApiOperation(\"获取数量\")");
        method3.addAnnotation("@ApiOperationSupport(order = "+(++ApiOperationSupportOrder)+")");
        method3.addAnnotation("@PostMapping(\"/count.json\")");
        method3.setReturnType(new FullyQualifiedJavaType("Long"));
        Parameter parameter3 = new Parameter(new FullyQualifiedJavaType(voExtName), "voExtParams");
        parameter3.addAnnotation("@RequestBody");
        method3.addParameter(parameter3);
        List<String> bodyLineList3 = new ArrayList<>();
        bodyLineList3.add("return "+fieldServiceName+".count(voExtParams);");
        method3.addBodyLines(bodyLineList3);
        clazz.addMethod(method3);

        Method method4 = new Method("save");
        method4.setVisibility(JavaVisibility.PUBLIC);
        method4.addAnnotation("@ApiOperation(\"新增和修改，id是空为新增，id有值为修改\")");
        method4.addAnnotation("@ApiOperationSupport(order = "+(++ApiOperationSupportOrder)+", ignoreParameters={\"voExt.createTs\",\"voExt.createUserId\",\"voExt.updateTs\",\"voExt.updateUserId\",\"voExt.rowNo\",\"voExt.rowState\"})");
        method4.addAnnotation("@PostMapping(\"/save.json\")");
        method4.setReturnType(new FullyQualifiedJavaType("ResponseResult"));
        Parameter parameter4 = new Parameter(new FullyQualifiedJavaType(voExtName), "voExt");
        parameter4.addAnnotation("@RequestBody");
        method4.addParameter(parameter4);
        List<String> bodyLineList4 = new ArrayList<>();
        bodyLineList4.add("return "+fieldServiceName+".save(voExt,getCurrentUserId());");
        method4.addBodyLines(bodyLineList4);
        clazz.addMethod(method4);

        Method method5 = new Method("del");
        method5.setVisibility(JavaVisibility.PUBLIC);
        method5.addAnnotation("@ApiOperation(\"根据id删除\")");
        method5.addAnnotation("@ApiOperationSupport(order = "+(++ApiOperationSupportOrder)+")");
        method5.addAnnotation("@PostMapping(\"/del.json\")");
        method5.setReturnType(new FullyQualifiedJavaType("ResponseResult"));
        Parameter parameter5 = new Parameter(new FullyQualifiedJavaType("String"), "id");
        parameter5.addAnnotation("@RequestParam(\"id\")");
        method5.addParameter(parameter5);
        List<String> bodyLineList5 = new ArrayList<>();
        bodyLineList5.add("return "+fieldServiceName+".del(id,getCurrentUserId());");
        method5.addBodyLines(bodyLineList5);
        clazz.addMethod(method5);


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
