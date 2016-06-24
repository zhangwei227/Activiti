/**
 * Copyright 2005-2015 Alfresco Software, Ltd. All rights reserved.
 * License rights for this program may be obtained from Alfresco Software, Ltd.
 * pursuant to a written agreement and any use of this program without such an
 * agreement is prohibited.
 */
package com.activiti.rest.editor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.activiti.model.editor.DecisionTableSaveRepresentation;
import com.activiti.model.editor.ModelRepresentation;
import com.activiti.model.editor.decisiontable.DecisionTableRepresentation;
import com.activiti.service.editor.AlfrescoDecisionTableService;
import com.activiti.service.exception.BadRequestException;
import com.activiti.service.exception.InternalServerErrorException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author yvoswillens
 * @author erikwinlof
 */
@RestController
@RequestMapping("/rest/decision-table-models")
public class DecisionTableResource {
	

    private static final Logger logger = LoggerFactory.getLogger(DecisionTableResource.class);
    
    @Autowired
    protected ObjectMapper objectMapper;
    
    @Autowired
    protected AlfrescoDecisionTableService decisionTableService;

    @RequestMapping(value = "/values", method = RequestMethod.GET, produces = "application/json")
    public List<DecisionTableRepresentation> getDecisionTables(HttpServletRequest request) {
        String[] decisionTableIds = request.getParameterValues("decisionTableId");
        if (decisionTableIds == null || decisionTableIds.length == 0) {
            throw new BadRequestException("No decisionTableId parameter(s) provided in the request");
        }
        return decisionTableService.getDecisionTables(decisionTableIds);
    }

    @RequestMapping(value = "/{decisionTableId}", method = RequestMethod.GET, produces = "application/json")
    public DecisionTableRepresentation getDecisionTable(@PathVariable Long decisionTableId) {
        return decisionTableService.getDecisionTable(decisionTableId);
    }
    
    @RequestMapping(value = "/{decisionTableId}/export", method = RequestMethod.GET)
    public void exportDecisionTable(HttpServletResponse response, @PathVariable Long decisionTableId) {
        decisionTableService.exportDecisionTable(response, decisionTableId);
    }
    
    @RequestMapping(value = "/import-decision-table", method = RequestMethod.POST, produces = "application/json")
    public ModelRepresentation importDecisionTable(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        return decisionTableService.importDecisionTable(request, file);
    }
    
    @RequestMapping(value = "/import-decision-table-text", method = RequestMethod.POST, produces = "application/json")
    public String importDecisionTableText(HttpServletRequest request, @RequestParam("file") MultipartFile file) {
        ModelRepresentation decisionTableRepresentation = decisionTableService.importDecisionTable(request, file);
        String json = null;
        try {
            json = objectMapper.writeValueAsString(decisionTableRepresentation);
        } catch (Exception e) {
            logger.error("Error writing imported decision table json", e);
            throw new InternalServerErrorException("Error writing imported decision table representation json");
        }
        return json;
    }

    @RequestMapping(value = "/history/{historyModelId}", method = RequestMethod.GET, produces = "application/json")
    public DecisionTableRepresentation getHistoricDecisionTable(@PathVariable Long historyModelId) {
        return decisionTableService.getHistoricDecisionTable(historyModelId);
    }
    
    @RequestMapping(value = "/history/{historyModelId}/export", method = RequestMethod.GET)
    public void exportHistoricDecisionTable(HttpServletResponse response, @PathVariable Long historyModelId) {
        decisionTableService.exportHistoricDecisionTable(response, historyModelId);
    }

    @RequestMapping(value = "/{decisionTableId}", method = RequestMethod.PUT, produces = "application/json")
    public DecisionTableRepresentation saveDecisionTable(@PathVariable Long decisionTableId, @RequestBody DecisionTableSaveRepresentation saveRepresentation) {
        return decisionTableService.saveDecisionTable(decisionTableId, saveRepresentation);
    }
}
