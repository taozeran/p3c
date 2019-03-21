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
                        addViolationWithMessage(data, astName,"java.other.AvoidCallAnnotatedMethodInSameClassRule.violation.msg");
                    }
                }
            }
        }
        return super.visit(node, data);
    }
}
