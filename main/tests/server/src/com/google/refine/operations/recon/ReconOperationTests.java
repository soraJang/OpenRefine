/*******************************************************************************
 * Copyright (C) 2018, OpenRefine contributors
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package com.google.refine.operations.recon;

import static org.mockito.Mockito.mock;

import java.util.Properties;

import com.google.refine.RefineTest;
import com.google.refine.model.Project;
import com.google.refine.model.recon.ReconConfig;
import com.google.refine.model.recon.StandardReconConfig;
import com.google.refine.operations.OperationRegistry;
import com.google.refine.util.ParsingUtilities;
import com.google.refine.util.TestUtils;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;


public class ReconOperationTests extends RefineTest {
    private String json= "{"
            + "\"op\":\"core/recon\","
            + "\"description\":\"Reconcile cells in column researcher to type Q5\","
            + "\"columnName\":\"researcher\","
            + "\"config\":{"
            + "   \"mode\":\"standard-service\","
            + "   \"service\":\"https://tools.wmflabs.org/openrefine-wikidata/en/api\","
            + "   \"identifierSpace\":\"http://www.wikidata.org/entity/\","
            + "   \"schemaSpace\":\"http://www.wikidata.org/prop/direct/\","
            + "   \"type\":{\"id\":\"Q5\",\"name\":\"human\"},"
            + "   \"autoMatch\":true,"
            + "   \"columnDetails\":[],"
            + "   \"limit\":0"
            + "},"
            + "\"engineConfig\":{\"mode\":\"row-based\",\"facets\":[]}}";
    private Project project = mock(Project.class);
    
    private String processJson = ""
            + "    {\n" + 
            "       \"description\" : \"Reconcile cells in column researcher to type Q5\",\n" + 
            "       \"id\" : %d,\n" + 
            "       \"immediate\" : false,\n" + 
            "       \"onDone\" : [ {\n" + 
            "         \"action\" : \"createFacet\",\n" + 
            "         \"facetConfig\" : {\n" + 
            "           \"columnName\" : \"researcher\",\n" + 
            "           \"expression\" : \"forNonBlank(cell.recon.judgment, v, v, if(isNonBlank(value), \\\"(unreconciled)\\\", \\\"(blank)\\\"))\",\n" + 
            "           \"name\" : \"researcher: judgment\"\n" + 
            "         },\n" + 
            "         \"facetOptions\" : {\n" + 
            "           \"scroll\" : false\n" + 
            "         },\n" + 
            "         \"facetType\" : \"list\"\n" + 
            "       }, {\n" + 
            "         \"action\" : \"createFacet\",\n" + 
            "         \"facetConfig\" : {\n" + 
            "           \"columnName\" : \"researcher\",\n" + 
            "           \"expression\" : \"cell.recon.best.score\",\n" + 
            "           \"mode\" : \"range\",\n" + 
            "           \"name\" : \"researcher: best candidate's score\"\n" + 
            "         },\n" + 
            "         \"facetType\" : \"range\"\n" + 
            "       } ],\n" + 
            "       \"progress\" : 0,\n" + 
            "       \"status\" : \"pending\"\n" + 
            "     }";

    @BeforeSuite
    public void registerOperation() {
        OperationRegistry.registerOperation(getCoreModule(), "recon", ReconOperation.class);
        ReconConfig.registerReconConfig(getCoreModule(), "standard-service", StandardReconConfig.class);
    }
    
    @Test
    public void serializeReconOperation() throws Exception {
        TestUtils.isSerializedTo(ParsingUtilities.mapper.readValue(json, ReconOperation.class), json);
    }
    
    @Test
    public void serializeReconProcess() throws Exception {
        ReconOperation op = ParsingUtilities.mapper.readValue(json, ReconOperation.class);
        com.google.refine.process.Process process = op.createProcess(project, new Properties());
        TestUtils.isSerializedTo(process, String.format(processJson, process.hashCode()));
    }
}
