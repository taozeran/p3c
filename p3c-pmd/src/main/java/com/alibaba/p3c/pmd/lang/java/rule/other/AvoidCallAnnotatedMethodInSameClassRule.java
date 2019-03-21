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
                        List<ASTPrimaryPrefix> argumentsPrefix = astArguments.findDescendantsOfType(ASTPrimaryPrefix.class);
                        for (ASTPrimaryPrefix prefix : argumentsPrefix) {
                            String typeName=null;
                            ASTLiteral astLiteral = prefix.getFirstDescendantOfType(ASTLiteral.class);
                            if (astLiteral == null) {
                                ASTName astName1 = prefix.getFirstDescendantOfType(ASTName.class);
                                String arg = astName1.getImage();
                                typeName = getVarType(node,bodyDeclaration, arg);
                            } else {
                                Class<?> type = astLiteral.getType();
                                typeName = type.getName();
                            }
                        }

                        int argumentCount = astArguments.getArgumentCount();
                        int size = argsTypes.size();
                        addViolationWithMessage(data, astName,"java.other.AvoidCallAnnotatedMethodInSameClassRule.violation.msg");
                    }
                }
            }
        }
        return super.visit(node, data);
    }

    private String getVarType(ASTClassOrInterfaceBody node, ASTClassOrInterfaceBodyDeclaration bodyDeclaration, String arg) {
        return null;
    }
}
