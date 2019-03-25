package com.alibaba.p3c.pmd.lang.java.rule.other;

import com.alibaba.p3c.pmd.lang.java.rule.AbstractAliRule;
import net.sourceforge.pmd.lang.java.ast.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AvoidCallAnnotatedMethodInSameClassRule extends AbstractAliRule {

    private static final String[] ANNOTATION_SET= {"Transactional","Async"};


    @Override
    public Object visit(ASTClassOrInterfaceBody node, Object data) {
        System.out.println("new version 1");
        List<ASTClassOrInterfaceBodyDeclaration> bodyDeclarations = node.findChildrenOfType(ASTClassOrInterfaceBodyDeclaration.class);
        body:
        for (ASTClassOrInterfaceBodyDeclaration bodyDeclaration : bodyDeclarations) {
            ASTMethodDeclarator astMethodDeclarator = bodyDeclaration.getFirstDescendantOfType(ASTMethodDeclarator.class);
            if (astMethodDeclarator == null) {
                continue;
            }
            String methodName = astMethodDeclarator.getImage();
            List<ASTType> argsTypes = astMethodDeclarator.findDescendantsOfType(ASTType.class);
            List<ASTAnnotation> astAnnotations = bodyDeclaration.findDescendantsOfType(ASTAnnotation.class);
            exFor:
            for (ASTAnnotation astAnnotation : astAnnotations) {
                ASTName astName = astAnnotation.getFirstDescendantOfType(ASTName.class);
                String image = astName.getImage();

                for (String s : ANNOTATION_SET) {
                    if (s.equals(image)) {
                        break exFor;
                    }
                }
                continue body;
            }
            if (!astAnnotations.isEmpty()) {
                List<ASTBlockStatement> astBlockStatements = node.findDescendantsOfType(ASTBlockStatement.class);
                for (ASTBlockStatement astBlockStatement : astBlockStatements) {
                    ASTName astName = astBlockStatement.getFirstDescendantOfType(ASTName.class);
                    if (astName == null) {
                        continue;
                    }
                    String image = astName.getImage();
                    if (image.equals(methodName)) {
                        ASTArguments astArguments = astBlockStatement.getFirstDescendantOfType(ASTArguments.class);
                        int argumentCount = astArguments.getArgumentCount();
                        int size = argsTypes.size();
                        if (argumentCount!=size) {
                            continue;
                        }
                        //当前进行比对的方法节点
                        ASTClassOrInterfaceBodyDeclaration bodyDeclaration1 = astName.getFirstParentOfType(ASTClassOrInterfaceBodyDeclaration.class);
                        //比对所有参数类型
                        List<ASTPrimaryPrefix> argumentsPrefix = astArguments.findDescendantsOfType(ASTPrimaryPrefix.class);
                        int i=0;
                        boolean isSameArgsType=true;
                        for (ASTPrimaryPrefix prefix : argumentsPrefix) {
                            String typeName=null;
                            ASTLiteral astLiteral = prefix.getFirstDescendantOfType(ASTLiteral.class);
                            if (astLiteral == null) {
                                ASTName astName1 = prefix.getFirstDescendantOfType(ASTName.class);
                                String arg = astName1.getImage();
                                typeName = getVarType(node,bodyDeclaration1, arg);
                            } else {
                                Class<?> type = astLiteral.getType();
                                typeName = type.getName();
                            }
                            String typeImage = argsTypes.get(i).getTypeImage();
                            if (!(typeImage.toLowerCase().equals(typeName.toLowerCase()))&&
                                    !((typeImage.equals("int")|| typeImage.equals("Integer")) && (typeName.equals("int") || typeName.equals("Integer")))) {
                                isSameArgsType=false;
                                break;
                            }
                            i++;
                        }
                        if (isSameArgsType) {
                            addViolationWithMessage(data, astName,"java.other.AvoidCallAnnotatedMethodInSameClassRule.violation.msg");
                        }
                    }
                }
            }
        }
        return super.visit(node, data);
    }

    private String getVarType(ASTClassOrInterfaceBody node, ASTClassOrInterfaceBodyDeclaration bodyDeclaration, String arg) {
        List<ASTFieldDeclaration> descendantsOfType = node.findDescendantsOfType(ASTFieldDeclaration.class);
        for (ASTFieldDeclaration astFieldDeclaration : descendantsOfType) {
            ASTVariableDeclaratorId firstDescendantOfType = astFieldDeclaration.getFirstDescendantOfType(ASTVariableDeclaratorId.class);
            if (firstDescendantOfType.getImage().equals(arg)) {
                ASTType astType = astFieldDeclaration.getFirstDescendantOfType(ASTType.class);
                String typeImage = astType.getTypeImage();
                return typeImage;
            }
        }
        List<ASTVariableDeclaratorId> variableDeclaratorIds = bodyDeclaration.findDescendantsOfType(ASTVariableDeclaratorId.class);
        for (ASTVariableDeclaratorId id : variableDeclaratorIds) {
            String image = id.getImage();
            if (image.equals(arg)) {
                ASTType typeNode;
                ASTLocalVariableDeclaration localVariableDeclaration = id.getFirstParentOfType(ASTLocalVariableDeclaration.class);
                if (localVariableDeclaration == null) {
                    ASTFormalParameter astFormalParameter = id.getFirstParentOfType(ASTFormalParameter.class);
                    typeNode = astFormalParameter.getTypeNode();
                } else {
                    typeNode = localVariableDeclaration.getTypeNode();
                }
                String typeImage = typeNode.getTypeImage();
                return typeImage;
            }
        }
        throw new IllegalArgumentException(arg + " is not a variable");
    }
}
