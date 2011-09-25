/*
 * gvNIX. Spring Roo based RAD tool for Conselleria d'Infraestructures
 * i Transport - Generalitat Valenciana
 * Copyright (C) 2010 CIT - Generalitat Valenciana
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gvnix.service.roo.addon.ws.export;

import java.util.List;

import org.springframework.roo.model.JavaType;

/**
 * Operation for export a web service base on a WSDL declaration (Contract
 * First).
 * 
 * @author Ricardo García at <a href="http://www.disid.com">DiSiD Technologies
 *         S.L.</a> made for <a href="http://www.cit.gva.es">Conselleria
 *         d'Infraestructures i Transport</a>
 */
public interface WSExportWsdlOperations {

    /**
     * Export WSDL Web Service Contract First to Java code using GvNIX
     * annotations.
     * 
     * @param url
     *            from WSDL file to export.
     * @return implementation classes
     */
    public List<JavaType> exportWSDL2Java(String url);

    /**
     * Create new files with '@GvNIX' annotations in SRC_MAIN_JAVA.
     * 
     * TODO: To be removed. This method is used internally by service and
     * depends on a sequence of calls, so its STRONGLY RECOMENDED remove it from
     * interface and maintain it in implementation
     * 
     * @return implementation classes
     */
    public List<JavaType> updateAnnotationsToGvNIX();
}
