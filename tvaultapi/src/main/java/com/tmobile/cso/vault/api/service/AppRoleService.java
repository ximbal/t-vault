// =========================================================================
// Copyright 2018 T-Mobile, US
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// See the readme.txt file for additional language around disclaimer of warranties.
// =========================================================================

package com.tmobile.cso.vault.api.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableMap;
import com.tmobile.cso.vault.api.controller.ControllerUtil;
import com.tmobile.cso.vault.api.exception.LogMessage;
import com.tmobile.cso.vault.api.model.AppRole;
import com.tmobile.cso.vault.api.model.AppRoleIdSecretId;
import com.tmobile.cso.vault.api.model.AppRoleNameSecretId;
import com.tmobile.cso.vault.api.model.AppRoleSecretData;
import com.tmobile.cso.vault.api.model.SafeAppRoleAccess;
import com.tmobile.cso.vault.api.process.RequestProcessor;
import com.tmobile.cso.vault.api.process.Response;
import com.tmobile.cso.vault.api.utils.JSONUtil;
import com.tmobile.cso.vault.api.utils.ThreadLocalContext;

@Component
public class  AppRoleService {

	@Value("${vault.port}")
	private String vaultPort;
	
	@Autowired
	private RequestProcessor reqProcessor;

	@Value("${vault.auth.method}")
	private String vaultAuthMethod;

	private static Logger log = LogManager.getLogger(AppRoleService.class);
	/**
	 * Create AppRole
	 * @param token
	 * @param appRole
	 * @return
	 */
	public ResponseEntity<String> createAppRole(String token, AppRole appRole){
		String jsonStr = JSONUtil.getJSON(appRole);
		log.debug(JSONUtil.getJSON(ImmutableMap.<String, String>builder().
			      put(LogMessage.USER, ThreadLocalContext.getCurrentMap().get(LogMessage.USER).toString()).
				  put(LogMessage.ACTION, "Create AppRole").
			      put(LogMessage.MESSAGE, String.format("Trying to create AppRole [%s]", jsonStr)).
			      put(LogMessage.APIURL, ThreadLocalContext.getCurrentMap().get(LogMessage.APIURL).toString()).
			      build()));
		if (!ControllerUtil.areAppRoleInputsValid(appRole)) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"errors\":[\"Invalid input values for AppRole creation\"]}");
		}
		jsonStr = ControllerUtil.convertAppRoleInputsToLowerCase(jsonStr);
		Response response = reqProcessor.process("/auth/approle/role/create", jsonStr,token);
		if(response.getHttpstatus().equals(HttpStatus.NO_CONTENT)) {
			return ResponseEntity.status(HttpStatus.OK).body("{\"messages\":[\"AppRole created succssfully\"]}");
		}
		log.error(JSONUtil.getJSON(ImmutableMap.<String, String>builder().
			      put(LogMessage.USER, ThreadLocalContext.getCurrentMap().get(LogMessage.USER).toString()).
				  put(LogMessage.ACTION, "Create AppRole").
			      put(LogMessage.MESSAGE, "Creation of AppRole failed").
			      put(LogMessage.RESPONSE, response.getResponse()).
			      put(LogMessage.STATUS, response.getHttpstatus().toString()).
			      put(LogMessage.APIURL, ThreadLocalContext.getCurrentMap().get(LogMessage.APIURL).toString()).
			      build()));
		return ResponseEntity.status(response.getHttpstatus()).body(response.getResponse());	
	}
	/**
	 * Reads approle information
	 * @param token
	 * @param rolename
	 * @return
	 */
	public ResponseEntity<String> readAppRole(String token, String rolename){
		log.debug(JSONUtil.getJSON(ImmutableMap.<String, String>builder().
			      put(LogMessage.USER, ThreadLocalContext.getCurrentMap().get(LogMessage.USER).toString()).
				  put(LogMessage.ACTION, "Read AppRole").
			      put(LogMessage.MESSAGE, String.format("Trying to read AppRole [%s]", rolename)).
			      put(LogMessage.APIURL, ThreadLocalContext.getCurrentMap().get(LogMessage.APIURL).toString()).
			      build()));
		Response response = reqProcessor.process("/auth/approle/role/read","{\"role_name\":\""+rolename+"\"}",token);
		log.debug(JSONUtil.getJSON(ImmutableMap.<String, String>builder().
			      put(LogMessage.USER, ThreadLocalContext.getCurrentMap().get(LogMessage.USER).toString()).
				  put(LogMessage.ACTION, "Read AppRole").
			      put(LogMessage.MESSAGE, "Reading AppRole completed").
			      put(LogMessage.STATUS, response.getHttpstatus().toString()).
			      put(LogMessage.APIURL, ThreadLocalContext.getCurrentMap().get(LogMessage.APIURL).toString()).
			      build()));
		return ResponseEntity.status(response.getHttpstatus()).body(response.getResponse());	
	}
	/**
	 * Gets roleid for approle
	 * @param token
	 * @param rolename
	 * @return
	 */
	public ResponseEntity<String> readAppRoleRoleId(String token, String rolename){
		log.debug(JSONUtil.getJSON(ImmutableMap.<String, String>builder().
			      put(LogMessage.USER, ThreadLocalContext.getCurrentMap().get(LogMessage.USER).toString()).
				  put(LogMessage.ACTION, "Read AppRoleId").
			      put(LogMessage.MESSAGE, String.format("Trying to read AppRoleId [%s]", rolename)).
			      put(LogMessage.APIURL, ThreadLocalContext.getCurrentMap().get(LogMessage.APIURL).toString()).
			      build()));
		Response response = reqProcessor.process("/auth/approle/role/readRoleID","{\"role_name\":\""+rolename+"\"}",token);
		log.debug(JSONUtil.getJSON(ImmutableMap.<String, String>builder().
			      put(LogMessage.USER, ThreadLocalContext.getCurrentMap().get(LogMessage.USER).toString()).
				  put(LogMessage.ACTION, "Read AppRoleId").
			      put(LogMessage.MESSAGE, "Reading AppRoleId completed").
			      put(LogMessage.STATUS, response.getHttpstatus().toString()).
			      put(LogMessage.APIURL, ThreadLocalContext.getCurrentMap().get(LogMessage.APIURL).toString()).
			      build()));
		return ResponseEntity.status(response.getHttpstatus()).body(response.getResponse());	
	}
	/**
	 * Creates secret id for approle auth
	 * @param token
	 * @param jsonStr
	 * @return
	 */
	public ResponseEntity<String> createsecretId(String token, AppRoleSecretData appRoleSecretData){
		String jsonStr = JSONUtil.getJSON(appRoleSecretData);
		log.debug(JSONUtil.getJSON(ImmutableMap.<String, String>builder().
			      put(LogMessage.USER, ThreadLocalContext.getCurrentMap().get(LogMessage.USER).toString()).
				  put(LogMessage.ACTION, "Create SecretId").
			      put(LogMessage.MESSAGE, String.format("Trying to create SecretId [%s]", jsonStr)).
			      put(LogMessage.APIURL, ThreadLocalContext.getCurrentMap().get(LogMessage.APIURL).toString()).
			      build()));
		jsonStr = ControllerUtil.convertAppRoleSecretIdToLowerCase(jsonStr);
		Response response = reqProcessor.process("/auth/approle/secretid/create", jsonStr,token);
		if(response.getHttpstatus().equals(HttpStatus.NO_CONTENT)) {
			log.debug(JSONUtil.getJSON(ImmutableMap.<String, String>builder().
				      put(LogMessage.USER, ThreadLocalContext.getCurrentMap().get(LogMessage.USER).toString()).
					  put(LogMessage.ACTION, "Create SecretId").
				      put(LogMessage.MESSAGE, "Create SecretId completed successfully").
				      put(LogMessage.STATUS, response.getHttpstatus().toString()).
				      put(LogMessage.APIURL, ThreadLocalContext.getCurrentMap().get(LogMessage.APIURL).toString()).
				      build()));
			return ResponseEntity.status(HttpStatus.OK).body("{\"messages\":[\"Secret ID created for AppRole\"]}");
		}
		log.error(JSONUtil.getJSON(ImmutableMap.<String, String>builder().
			      put(LogMessage.USER, ThreadLocalContext.getCurrentMap().get(LogMessage.USER).toString()).
				  put(LogMessage.ACTION, "Create SecretId").
			      put(LogMessage.MESSAGE, "Create SecretId failed").
			      put(LogMessage.RESPONSE, response.getResponse()).
			      put(LogMessage.STATUS, response.getHttpstatus().toString()).
			      put(LogMessage.APIURL, ThreadLocalContext.getCurrentMap().get(LogMessage.APIURL).toString()).
			      build()));
		return ResponseEntity.status(response.getHttpstatus()).body(response.getResponse());	
	}
	/**
	 * Reads secret id for a given rolename
	 * @param token
	 * @param rolename
	 * @return
	 */
	public ResponseEntity<String> readSecretId(String token, String rolename){
		log.debug(JSONUtil.getJSON(ImmutableMap.<String, String>builder().
			      put(LogMessage.USER, ThreadLocalContext.getCurrentMap().get(LogMessage.USER).toString()).
				  put(LogMessage.ACTION, "Read SecretId").
			      put(LogMessage.MESSAGE, String.format("Trying to read SecretId [%s]", rolename)).
			      put(LogMessage.APIURL, ThreadLocalContext.getCurrentMap().get(LogMessage.APIURL).toString()).
			      build()));
		Response response = reqProcessor.process("/auth/approle/secretid/lookup","{\"role_name\":\""+rolename+"\"}",token);
		log.debug(JSONUtil.getJSON(ImmutableMap.<String, String>builder().
			      put(LogMessage.USER, ThreadLocalContext.getCurrentMap().get(LogMessage.USER).toString()).
				  put(LogMessage.ACTION, "Read SecretId").
			      put(LogMessage.MESSAGE, "Read SecretId completed").
			      put(LogMessage.STATUS, response.getHttpstatus().toString()).
			      put(LogMessage.APIURL, ThreadLocalContext.getCurrentMap().get(LogMessage.APIURL).toString()).
			      build()));
		return ResponseEntity.status(response.getHttpstatus()).body(response.getResponse());	
	}
	/**
	 * Deletes Secret id
	 * @param token
	 * @param jsonStr
	 * @return
	 */
	public ResponseEntity<String> deleteSecretId(String token, AppRoleNameSecretId appRoleNameSecretId){
		String jsonStr = JSONUtil.getJSON(appRoleNameSecretId);
		log.debug(JSONUtil.getJSON(ImmutableMap.<String, String>builder().
			      put(LogMessage.USER, ThreadLocalContext.getCurrentMap().get(LogMessage.USER).toString()).
				  put(LogMessage.ACTION, "Delete SecretId").
			      put(LogMessage.MESSAGE, String.format("Trying to delete SecretId [%s]", jsonStr)).
			      put(LogMessage.APIURL, ThreadLocalContext.getCurrentMap().get(LogMessage.APIURL).toString()).
			      build()));
		Response response = reqProcessor.process("/auth/approle/secret/delete",jsonStr,token);
		if(response.getHttpstatus().equals(HttpStatus.NO_CONTENT)) {
			log.debug(JSONUtil.getJSON(ImmutableMap.<String, String>builder().
				      put(LogMessage.USER, ThreadLocalContext.getCurrentMap().get(LogMessage.USER).toString()).
					  put(LogMessage.ACTION, "Delete SecretId").
				      put(LogMessage.MESSAGE, "Deletion of SecretId completed successfully").
				      put(LogMessage.STATUS, response.getHttpstatus().toString()).
				      put(LogMessage.APIURL, ThreadLocalContext.getCurrentMap().get(LogMessage.APIURL).toString()).
				      build()));
			return ResponseEntity.status(HttpStatus.OK).body("{\"messages\":[\"SecretId for AppRole deleted\"]}");
		}
		log.error(JSONUtil.getJSON(ImmutableMap.<String, String>builder().
			      put(LogMessage.USER, ThreadLocalContext.getCurrentMap().get(LogMessage.USER).toString()).
				  put(LogMessage.ACTION, "Delete SecretId").
			      put(LogMessage.MESSAGE, "Deletion of SecretId failed").
			      put(LogMessage.RESPONSE, response.getResponse()).
			      put(LogMessage.STATUS, response.getHttpstatus().toString()).
			      put(LogMessage.APIURL, ThreadLocalContext.getCurrentMap().get(LogMessage.APIURL).toString()).
			      build()));
		return ResponseEntity.status(response.getHttpstatus()).body(response.getResponse());	
	}
	/**
	 * Deletes an approle
	 * @param token
	 * @param rolename
	 * @return
	 */
	public ResponseEntity<String> deleteAppRole(String token, AppRole appRole){
		String jsonStr = JSONUtil.getJSON(appRole);
		log.debug(JSONUtil.getJSON(ImmutableMap.<String, String>builder().
			      put(LogMessage.USER, ThreadLocalContext.getCurrentMap().get(LogMessage.USER).toString()).
				  put(LogMessage.ACTION, "Delete AppRoleId").
			      put(LogMessage.MESSAGE, String.format("Trying to delete AppRoleId [%s]", jsonStr)).
			      put(LogMessage.APIURL, ThreadLocalContext.getCurrentMap().get(LogMessage.APIURL).toString()).
			      build()));
		Response response = reqProcessor.process("/auth/approle/role/delete",jsonStr,token);
		if(response.getHttpstatus().equals(HttpStatus.NO_CONTENT)) {
			log.debug(JSONUtil.getJSON(ImmutableMap.<String, String>builder().
				      put(LogMessage.USER, ThreadLocalContext.getCurrentMap().get(LogMessage.USER).toString()).
					  put(LogMessage.ACTION, "Delete AppRole").
				      put(LogMessage.MESSAGE, "Delete AppRole completed").
				      put(LogMessage.STATUS, response.getHttpstatus().toString()).
				      put(LogMessage.APIURL, ThreadLocalContext.getCurrentMap().get(LogMessage.APIURL).toString()).
				      build()));
			return ResponseEntity.status(HttpStatus.OK).body("{\"messages\":[\"AppRole deleted\"]}");
		}
		log.error(JSONUtil.getJSON(ImmutableMap.<String, String>builder().
			      put(LogMessage.USER, ThreadLocalContext.getCurrentMap().get(LogMessage.USER).toString()).
				  put(LogMessage.ACTION, "Delete AppRole").
			      put(LogMessage.MESSAGE, "Reading AppRole failed").
			      put(LogMessage.RESPONSE, response.getResponse()).
			      put(LogMessage.STATUS, response.getHttpstatus().toString()).
			      put(LogMessage.APIURL, ThreadLocalContext.getCurrentMap().get(LogMessage.APIURL).toString()).
			      build()));
		return ResponseEntity.status(response.getHttpstatus()).body(response.getResponse());	
	}
	/**
	 * Login using appRole
	 * @param appRoleIdSecretId
	 * @return
	 */
	public ResponseEntity<String> login(AppRoleIdSecretId appRoleIdSecretId){
		String jsonStr = JSONUtil.getJSON(appRoleIdSecretId);
		log.debug(JSONUtil.getJSON(ImmutableMap.<String, String>builder().
			      put(LogMessage.USER, ThreadLocalContext.getCurrentMap().get(LogMessage.USER).toString()).
				  put(LogMessage.ACTION, "AppRole Login").
			      put(LogMessage.MESSAGE, "Trying to authenticate with AppRole").
			      put(LogMessage.APIURL, ThreadLocalContext.getCurrentMap().get(LogMessage.APIURL).toString()).
			      build()));
		Response response = reqProcessor.process("/auth/approle/login",jsonStr,"");
		if(HttpStatus.OK.equals(response.getHttpstatus())) {
			log.debug(JSONUtil.getJSON(ImmutableMap.<String, String>builder().
				      put(LogMessage.USER, ThreadLocalContext.getCurrentMap().get(LogMessage.USER).toString()).
					  put(LogMessage.ACTION, "AppRole Login").
				      put(LogMessage.MESSAGE, "AppRole Authentication Successful").
				      put(LogMessage.STATUS, response.getHttpstatus().toString()).
				      put(LogMessage.APIURL, ThreadLocalContext.getCurrentMap().get(LogMessage.APIURL).toString()).
				      build()));
			return ResponseEntity.status(response.getHttpstatus()).body(response.getResponse());
		}
		else {
			log.error(JSONUtil.getJSON(ImmutableMap.<String, String>builder().
				      put(LogMessage.USER, ThreadLocalContext.getCurrentMap().get(LogMessage.USER).toString()).
					  put(LogMessage.ACTION, "AppRole Login").
				      put(LogMessage.MESSAGE, "AppRole Authentication failed.").
				      put(LogMessage.RESPONSE, response.getResponse()).
				      put(LogMessage.STATUS, response.getHttpstatus().toString()).
				      put(LogMessage.APIURL, ThreadLocalContext.getCurrentMap().get(LogMessage.APIURL).toString()).
				      build()));
			return ResponseEntity.status(response.getHttpstatus()).body("{\"errors\":[\"Approle Login Failed.\"]}" + "HTTP STATUSCODE  :" + response.getHttpstatus());
		}
	}
	/**
	 * Associates an AppRole to a Safe
	 * @param token
	 * @param safeAppRoleAccess
	 * @return
	 */
	public ResponseEntity<String> associateApprole(String token, SafeAppRoleAccess safeAppRoleAccess){
		ResponseEntity<String> response = associateApproletoSafe(token,safeAppRoleAccess);
		if(response.getStatusCode().equals(HttpStatus.NO_CONTENT)) {
			return ResponseEntity.status(HttpStatus.OK).body("{\"messages\":[\"Approle associated to SDB\"]}");
		}
		else if(response.getStatusCode().equals(HttpStatus.OK)) {
			return ResponseEntity.status(HttpStatus.OK).body("{\"messages\":[\"Approle associated to SDB\"]}");
		}
		else {
			return ResponseEntity.status(response.getStatusCode()).body(response.toString());	
		}
	}
	
	/**
	 * Associates an AppRole to a Safe
	 * @param token
	 * @param safeAppRoleAccess
	 * @return
	 */
	private ResponseEntity<String>associateApproletoSafe(String token,  SafeAppRoleAccess safeAppRoleAccess){
		String jsonstr = JSONUtil.getJSON(safeAppRoleAccess);
		log.debug(JSONUtil.getJSON(ImmutableMap.<String, String>builder().
			      put(LogMessage.USER, ThreadLocalContext.getCurrentMap().get(LogMessage.USER).toString()).
				  put(LogMessage.ACTION, "Associate AppRole to SDB").
			      put(LogMessage.MESSAGE, String.format ("Trying to associate AppRole to SDB [%s]", jsonstr)).
			      put(LogMessage.APIURL, ThreadLocalContext.getCurrentMap().get(LogMessage.APIURL).toString()).
			      build()));		
		
		Map<String,Object> requestMap = ControllerUtil.parseJson(jsonstr);
		
		String approle = requestMap.get("role_name").toString();
		String path = requestMap.get("path").toString();
		String access = requestMap.get("access").toString();
		
		approle = (approle !=null) ? approle.toLowerCase() : approle;
		//path = (path != null) ? path.toLowerCase() : path;
		access = (access != null) ? access.toLowerCase(): access;
		
		if(ControllerUtil.isValidSafePath(path) && ControllerUtil.isValidSafe(path, token)){

			log.info("Associate approle to SDB -  path :" + path + "valid" );

			String folders[] = path.split("[/]+");
			
			String policy ="";
			
			switch (access){
				case "read": policy = "r_" + folders[0].toLowerCase() + "_" + folders[1] ; break ; 
				case "write": policy = "w_"  + folders[0].toLowerCase() + "_" + folders[1] ;break; 
				case "deny": policy = "d_"  + folders[0].toLowerCase() + "_" + folders[1] ;break; 
			}
			
			if("".equals(policy)){
				return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("{\"errors\":[\"Incorrect access requested. Valid values are read,write,deny \"]}");
			}
			

			log.info("Associate approle to SDB -  policy :" + policy + " is being configured" );
			
			//Call controller to update the policy for approle
			Response approleControllerResp = ControllerUtil.configureApprole(approle,policy,token);
			if(HttpStatus.OK.equals(approleControllerResp.getHttpstatus()) || (HttpStatus.NO_CONTENT.equals(approleControllerResp.getHttpstatus()))) {
					
				log.info("Associate approle to SDB -  policy :" + policy + " is associated" );
				log.debug(JSONUtil.getJSON(ImmutableMap.<String, String>builder().
					      put(LogMessage.USER, ThreadLocalContext.getCurrentMap().get(LogMessage.USER).toString()).
						  put(LogMessage.ACTION, "Associate AppRole to SDB").
					      put(LogMessage.MESSAGE, "Association of AppRole to SDB success").
					      put(LogMessage.STATUS, approleControllerResp.getHttpstatus().toString()).
					      put(LogMessage.APIURL, ThreadLocalContext.getCurrentMap().get(LogMessage.APIURL).toString()).
					      build()));
				Map<String,String> params = new HashMap<String,String>();
				params.put("type", "app-roles");
				params.put("name",approle);
				params.put("path",path);
				params.put("access",access);
				Response metadataResponse = ControllerUtil.updateMetadata(params,token);
				if(HttpStatus.NO_CONTENT.equals(metadataResponse.getHttpstatus())){
					log.debug(JSONUtil.getJSON(ImmutableMap.<String, String>builder().
						      put(LogMessage.USER, ThreadLocalContext.getCurrentMap().get(LogMessage.USER).toString()).
							  put(LogMessage.ACTION, "Add AppRole To SDB").
						      put(LogMessage.MESSAGE, "AppRole is successfully associated").
						      put(LogMessage.STATUS, metadataResponse.getHttpstatus().toString()).
						      put(LogMessage.APIURL, ThreadLocalContext.getCurrentMap().get(LogMessage.APIURL).toString()).
						      build()));
					return ResponseEntity.status(HttpStatus.OK).body("{\"messages\":[\"Approle :" + approle + " is successfully associated with SDB\"]}");		
				}else{
					log.debug(JSONUtil.getJSON(ImmutableMap.<String, String>builder().
						      put(LogMessage.USER, ThreadLocalContext.getCurrentMap().get(LogMessage.USER).toString()).
							  put(LogMessage.ACTION, "Add AppRole To SDB").
						      put(LogMessage.MESSAGE, "AppRole configuration failed.").
						      put(LogMessage.RESPONSE, metadataResponse.getResponse()).
						      put(LogMessage.STATUS, metadataResponse.getHttpstatus().toString()).
						      put(LogMessage.APIURL, ThreadLocalContext.getCurrentMap().get(LogMessage.APIURL).toString()).
						      build()));
					//Trying to revert the metadata update in case of failure
					approleControllerResp = ControllerUtil.configureAWSRole(approle,policy,token);
					if(approleControllerResp.getHttpstatus().equals(HttpStatus.NO_CONTENT)){
						log.error(JSONUtil.getJSON(ImmutableMap.<String, String>builder().
							      put(LogMessage.USER, ThreadLocalContext.getCurrentMap().get(LogMessage.USER).toString()).
								  put(LogMessage.ACTION, "Add AppRole To SDB").
							      put(LogMessage.MESSAGE, "Reverting user policy update failed").
							      put(LogMessage.RESPONSE, metadataResponse.getResponse()).
							      put(LogMessage.STATUS, metadataResponse.getHttpstatus().toString()).
							      put(LogMessage.APIURL, ThreadLocalContext.getCurrentMap().get(LogMessage.APIURL).toString()).
							      build()));
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"errors\":[\"Role configuration failed.Please try again\"]}");
					}else{
						log.error(JSONUtil.getJSON(ImmutableMap.<String, String>builder().
							      put(LogMessage.USER, ThreadLocalContext.getCurrentMap().get(LogMessage.USER).toString()).
								  put(LogMessage.ACTION, "Add ARole To SDB").
							      put(LogMessage.MESSAGE, "Reverting user policy update failed").
							      put(LogMessage.RESPONSE, metadataResponse.getResponse()).
							      put(LogMessage.STATUS, metadataResponse.getHttpstatus().toString()).
							      put(LogMessage.APIURL, ThreadLocalContext.getCurrentMap().get(LogMessage.APIURL).toString()).
							      build()));
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"errors\":[\"Role configuration failed.Contact Admin \"]}");
					}
				}
			
		}else {
			log.error(JSONUtil.getJSON(ImmutableMap.<String, String>builder().
				      put(LogMessage.USER, ThreadLocalContext.getCurrentMap().get(LogMessage.USER).toString()).
					  put(LogMessage.ACTION, "Associate AppRole to SDB").
				      put(LogMessage.MESSAGE, "Association of AppRole to SDB failed").
				      put(LogMessage.RESPONSE, approleControllerResp.getResponse()).
				      put(LogMessage.STATUS, approleControllerResp.getHttpstatus().toString()).
				      put(LogMessage.APIURL, ThreadLocalContext.getCurrentMap().get(LogMessage.APIURL).toString()).
				      build()));
				log.error( "Associate Approle" +approle + "to sdb FAILED");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"messages\":[\"Approle :" + approle + " failed to be associated with SDB\"]}");		
			}
		} else {
			log.error(JSONUtil.getJSON(ImmutableMap.<String, String>builder().
				      put(LogMessage.USER, ThreadLocalContext.getCurrentMap().get(LogMessage.USER).toString()).
					  put(LogMessage.ACTION, "Associate AppRole to SDB").
				      put(LogMessage.MESSAGE, "Association of AppRole to SDB failed").
				      put(LogMessage.RESPONSE, "Invalid Path").
				      put(LogMessage.APIURL, ThreadLocalContext.getCurrentMap().get(LogMessage.APIURL).toString()).
				      build()));
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"messages\":[\"Approle :" + approle + " failed to be associated with SDB.. Invalid Path specified\"]}");		
		
		}
	}
	
}
