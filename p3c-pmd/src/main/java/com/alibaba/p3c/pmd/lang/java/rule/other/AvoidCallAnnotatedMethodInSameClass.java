package com.alibaba.p3c.pmd.lang.java.rule.other;

import com.alibaba.p3c.pmd.lang.java.rule.AbstractAliRule;
import net.sourceforge.pmd.lang.java.ast.*;

import java.util.List;

public class AvoidCallAnnotatedMethodInSameClass extends AbstractAliRule {

    @Override
    public Object visit(ASTClassOrInterfaceBody node, Object data) {
        List<ASTClassOrInterfaceBodyDeclaration> bodyDeclarations = node.findChildrenOfType(ASTClassOrInterfaceBodyDeclaration.class);
        for (ASTClassOrInterfaceBodyDeclaration bodyDeclaration : bodyDeclarations) {
            ASTMethodDeclarator astMethodDeclarator = bodyDeclaration.getFirstDescendantOfType(ASTMethodDeclarator.class);
            String methodName = astMethodDeclarator.getImage();
            List<ASTAnnotation> descendants = bodyDeclaration.findDescendantsOfType(ASTAnnotation.class);
            if (!descendants.isEmpty()) {
                List<ASTName> astNames = node.findDescendantsOfType(ASTName.class);
                for (ASTName astName : astNames) {
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
