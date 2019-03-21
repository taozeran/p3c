package com.alibaba.p3c.pmd.lang.java.rule.other;

import com.alibaba.p3c.pmd.lang.java.rule.AbstractAliRule;
import net.sourceforge.pmd.lang.java.ast.*;

import java.util.List;

public class AvoidCallAnnotatedMethodInSameClassRule extends AbstractAliRule {

    @Override
    public Object visit(ASTClassOrInterfaceBody node, Object data) {
        List<ASTClassOrInterfaceBodyDeclaration> bodyDeclarations = node.findChildrenOfType(ASTClassOrInterfaceBodyDeclaration.class);
        for (ASTClassOrInterfaceBodyDeclaration bodyDeclaration : bodyDeclarations) {
            ASTMethodDeclarator astMethodDeclarator = bodyDeclaration.getFirstDescendantOfType(ASTMethodDeclarator.class);
            if (astMethodDeclarator == null) {
                continue;
            }
            String methodName = astMethodDeclarator.getImage();
            List<ASTNormalAnnotation> astAnnotations = bodyDeclaration.findDescendantsOfType(ASTNormalAnnotation.class);
            if (!astAnnotations.isEmpty()) {
                List<ASTBlockStatement> astBlockStatements = node.findDescendantsOfType(ASTBlockStatement.class);
                for (ASTBlockStatement astBlockStatement : astBlockStatements) {
                    ASTName astName = astBlockStatement.getFirstDescendantOfType(ASTName.class);
                    if (astName == null) {
                        continue;
                    }
                    String image = astName.getImage();
                    if (image.equals(methodName)) {
                        addViolationWithMessage(data, astName,"java.other.AvoidCallAnnotatedMethodInSameClass.violation.msg");
                    }
                }
            }
        }
        return super.visit(node, data);
    }
}
