/*
 * gvNIX. Spring Roo based RAD tool for Generalitat Valenciana
 * Copyright (C) 2013 Generalitat Valenciana
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/copyleft/gpl.html>.
 */
package org.gvnix.addon.jpa.audit;

import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.details.ClassOrInterfaceTypeDetails;
import org.springframework.roo.classpath.details.annotations.populator.AbstractAnnotationValues;
import org.springframework.roo.classpath.details.annotations.populator.AutoPopulate;
import org.springframework.roo.classpath.details.annotations.populator.AutoPopulationUtils;
import org.springframework.roo.model.JavaType;

/**
 * Represents a parsed {@link GvNIXJpaAuditUserService} values
 * 
 * @author gvNIX Team
 * @since 1.3.0
 */
public class JpaAuditUserServiceAnnotationValues extends
        AbstractAnnotationValues {

    public static final JavaType JPA_AUDIT_USER_SERV_ANNOTATION = new JavaType(
            GvNIXJpaAuditUserService.class);

    @AutoPopulate
    JavaType userType;

    public JpaAuditUserServiceAnnotationValues(
            final ClassOrInterfaceTypeDetails governorPhysicalTypeDetails) {
        super(governorPhysicalTypeDetails, JPA_AUDIT_USER_SERV_ANNOTATION);
        AutoPopulationUtils.populate(this, annotationMetadata);
        if (userType == null) {
            userType = new JavaType(String.class);
        }
    }

    /**
     * Constructor
     * 
     * @param governorPhysicalTypeMetadata
     */
    public JpaAuditUserServiceAnnotationValues(
            final PhysicalTypeMetadata governorPhysicalTypeMetadata) {
        super(governorPhysicalTypeMetadata, JPA_AUDIT_USER_SERV_ANNOTATION);
        AutoPopulationUtils.populate(this, annotationMetadata);
        if (userType == null) {
            userType = new JavaType(String.class);
        }
    }

    /**
     * @return RevisionLog configuration
     */
    public JavaType getUserType() {
        return userType;
    }
}
