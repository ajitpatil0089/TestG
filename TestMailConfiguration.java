package com.applifire.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import atg.taglib.json.util.JSONArray;
import atg.taglib.json.util.JSONException;
import atg.taglib.json.util.JSONObject;

import com.applifire.db.entity.repository.aws.AwsMailConfigRepository;
import com.applifire.entity.aws.AwsMailConfig;
import com.applifire.entity.aws.AwsObjects;
import com.applifire.entity.aws.AwsTableDetails;
import com.applifire.execpetion.BuzzorPersistentException;
import com.applifire.execpetion.ServiceException;
import com.applifire.fw.util.ServiceUtil;
import com.applifire.projectVersioning.AwsProjectVersioning;
import com.applifire.web.ui.advice.RuntimeLoginInfoHelper;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AppMailConfigServiceImpl implements AppMailConfigService, AwsProjectVersioning {

	@Autowired
	private AwsMailConfigRepository awsMailConfigRepository;

	@Autowired
	private RuntimeLoginInfoHelper runtimeLoginInfoHelper;

	@Autowired
	private AwsTaskService awsTaskService;

	@Autowired
	private EntityService entityService;
	
	@Autowired
	AwsObjectBuilderService awsObjectBuilderService;
	@Override
	public AwsMailConfig getMailConfigRecord() throws ServiceException {
		final AwsMailConfig awsMailConfig = awsMailConfigRepository.getMailConfig(runtimeLoginInfoHelper.getProjectId(), runtimeLoginInfoHelper.getProjectVersionId(),
				runtimeLoginInfoHelper.getAppCreatorId());
		if (awsMailConfig != null) {
			awsMailConfig.setPassword("");
			return awsMailConfig;
		} else {
			return null;
		}
	}

	@Override
	public String save(final AwsMailConfig awsMailConfig) throws ServiceException {
		final AwsMailConfig mailConfig = awsMailConfigRepository.getMailConfig(runtimeLoginInfoHelper.getProjectId(), runtimeLoginInfoHelper.getProjectVersionId(),
				runtimeLoginInfoHelper.getAppCreatorId());
		if (mailConfig != null && mailConfig.getMailConfigId().equals(awsMailConfig.getMailConfigId())) {
			mailConfig.setConfigName(awsMailConfig.getConfigName());
			mailConfig.setDefaultCheck(awsMailConfig.getDefaultCheck());
			mailConfig.setUserName(awsMailConfig.getUserName());
			mailConfig.setPassword(awsMailConfig.getPassword());
			mailConfig.setFromUser(awsMailConfig.getFromUser());
			mailConfig.setSmtpAuth(awsMailConfig.getSmtpAuth());
			mailConfig.setSmtpHost(awsMailConfig.getSmtpHost());
			mailConfig.setSmtpPort(awsMailConfig.getSmtpPort());
			mailConfig.setSmtpsPort(awsMailConfig.getSmtpsPort());
			mailConfig.setSmtpSsl(awsMailConfig.getSmtpSsl());
			mailConfig.setSmtpTls(awsMailConfig.getSmtpTls());
			mailConfig.setUpdatedBy(runtimeLoginInfoHelper.getUserId());
			mailConfig.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
			awsMailConfigRepository.updateAwsAppsInfo(mailConfig);
		} else {
			awsMailConfig.setMailConfigId(ServiceUtil.createRandomId());
			awsMailConfig.setProjectId(runtimeLoginInfoHelper.getProjectId());
			awsMailConfig.setProjectVersionId(runtimeLoginInfoHelper.getProjectVersionId());
			awsMailConfig.setAppCreatorId(runtimeLoginInfoHelper.getAppCreatorId());
			awsMailConfig.setCreatedBy(runtimeLoginInfoHelper.getUserId());
			awsMailConfig.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			awsMailConfig.setUpdatedBy(runtimeLoginInfoHelper.getUserId());
			awsMailConfig.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
			awsMailConfig.setActiveStatus(true);
			awsMailConfigRepository.createAwsAppsInfo(awsMailConfig);
		}
		awsTaskService.addAppConfigTask(ProcessNameEnum.APP_CONFIG, runtimeLoginInfoHelper);
		return "success";
	}

	/*
	 * private String updateXml(AwsMailConfig awsMailConfig) { try { Gson gson =
	 * new Gson(); String data = gson.toJson(awsMailConfig); String baseUrl =
	 * radSetup
	 * .getIdeProperties().getRadContainerManagerURL(runtimeLoginInfoHelper
	 * .getDockerContainerModel().getIpAddress()); HashMap<String, String>
	 * headers = new HashMap<String, String>(); String key = "CKI"; String value
	 * = runtimeLoginInfoHelper.getCKI(); headers.put(key, value); APICaller
	 * caller = new APICaller(baseUrl +
	 * "appMailConfigUpdate/updateAppConfigXml", "POST", headers);
	 * caller.sendPayLoad(data); APIResponse apiResponse = caller.getResponse();
	 * if(new
	 * JSONObject(apiResponse.getMessage()).getJSONObject("response").getBoolean
	 * ("success")) { try { AwsTask awsTask =
	 * awsTaskRepository.findTaskByName(runtimeLoginInfoHelper.getProjectId(),
	 * runtimeLoginInfoHelper.getProjectVersionId(),
	 * ProcessNameEnum.BUILDPROJECT.getProcessName()); if(awsTask != null) {
	 * awsTask.setProcessStatus(ProcessStatusEnum.ASSIGNED.getStatusId());
	 * awsTaskRepository.updateTask(awsTask); } } catch
	 * (BuzzorPersistentException | ServiceException e) { e.printStackTrace();
	 * return "failure"; } return "success"; } else { return "failure"; } }
	 * catch(IOException e) { e.printStackTrace(); } catch (JSONException e) {
	 * e.printStackTrace(); } return "success"; }
	 */
	public JSONObject createDesignJson() throws atg.taglib.json.util.JSONException, IOException{

		try {
			//AwsTableDetails awsTableDetails2 = tableRepository.findById(awsTableDetails.getTableId());
			File f1 = new File("/home/shikha/Downloads/country.json");
			//		}
			JSONObject inputJSON = new JSONObject(new String(Files.readAllBytes(f1.toPath())));
			JSONObject json = new JSONObject();
			json.put("entityName",inputJSON.getString("entityName"));
			json.put("displayEntityName",inputJSON.getString("uiName"));//

			/*json.put("tableId",inputJSON.getString("tableId"));
			json.put("tableName",inputJSON.getString("tableName"));
			json.put("tableType",inputJSON.getString("tableType"));*/
			json.put("builderPattern",false);//inserted manually false
			json.put("description","abc");
			json.put("displayTableScript",""); 
			json.put("displayEntityName","");
			json.put("displayJson","json");
			json.put("status","");
			json.put("sourceDetails",null);//null
			json.put("businessComponent",inputJSON.getString("id"));
			json.put("domain",inputJSON.getString("domain"));
			json.put("systemTable",false);
			json.put("createEntity",true);
			json.put("createRepository", "true");
			json.put("createWebService", "true");
			json.put("abstractRepository", "false");
			json.put(  "abstractWebService", "false");
			json.put(  "abstractRepositoryImpl", "null");
			json.put( "abstractWebServiceImpl", "null");
			json.put(  "multiTenant", "1");
			json.put("multiLangSupport",false);
			json.put("createUI",true);
			json.put("multiPlatformSupport","");
			json.put("supportedPlatforms","");
			json.put("uiModel","C");
			json.put("uiTemplate","781e7efc-1173-49f3-a491-5f5251a95780");
			json.put("mobileTemplate",null);
			json.put("dataAccess","3");
			json.put("repoScope","1");
			json.put("serviceScope","1");
			json.put("transactionLocking","2");
			json.put("cacheType",1);
			json.put("cacheIsolated","0");
			json.put("cacheDisable","0");
			json.put("cacheExpiry","0");
			json.put("cacheAlwaysRefresh","0");
			json.put("dataFetchStrategy","2");
			json.put("enableHistory",false);
			json.put("aggregateRoot",true);
			json.put("aggregateRootId",null);
			json.put("collection",false);
			json.put("readable",true);
			json.put("writable",true); 
			json.put("serializable",false);
			json.put("hardDelete",false);
			json.put("entityAuditing",true);
			json.put("bridgeReadOnly", false);
			json.put("inheritance",false);
			json.put("delimiter","#appfire#");
			json.put("versionId",1);
			json.put("parentId",null);
			json.put("inheritanceJson",null);
			json.put("entityRelationsData","[]");
			json.put("customFunctions",null);//giving error if we pass braces	    
			json.put("tableIndexData","[]");
			json.put("entityRelations",null);
			json.put("awsTableAggregate",null);
			json.put("awsTableIndexs",null);



			//			json.put("dataAccess",3);
			//			json.put("parentId",null);
			//			json.put("objectType",null);
			//			

			JSONObject tableArray=inputJSON.getJSONObject("table");
			json.put("tableName",tableArray.getString("name"));
			json.put("tableType",tableArray.getString("type"));
			json.put("tableId",tableArray.getString("id"));

//			JSONObject displayJsonArray=inputJSON.getJSONObject("displayJson");
			json.put("displayJson",inputJSON.get("displayJson"));	


			//JSONArray newfieldMetaDataArray=new JSONArray();
			JSONArray fieldMetaDataJsonArray=inputJSON.getJSONArray("fieldMetaData");
			/*for (Iterator iterator = fieldMetaDataJsonArray.iterator(); iterator.hasNext();) {
				JSONObject fieldJsonObject = (JSONObject) iterator.next();
				newfieldMetaDataArray.add(fieldJsonObject);
			}*/
			json.put("awsTableFieldDetail", fieldMetaDataJsonArray);
//			json.put("create", inputJSON.getJSONArray("create"));
			//			System.out.println(json);


			return json;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;


	}
	@Override
	public String testMailConfig(final AwsMailConfig awsMailConfig) throws IOException, ServiceException {
		String statusMsg = "";
		try {

			ObjectMapper objectMapper=new ObjectMapper();
			String object = createDesignJson().toString();
			System.out.println("Object "+ object );
			//AwsTableDetails awsTableDetail=objectMapper.readValue(createDesignJson().toString(), AwsTableDetails.class);
			AwsObjects awsobjectDetail=objectMapper.readValue(createDesignJson().toString(), AwsObjects.class);
			try {
				awsObjectBuilderService.save("", "", awsobjectDetail);
				//call here saveEntities method using object of EntityService
				//@Autowired here EntityService object
			} catch (BuzzorPersistentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (org.json.JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}	
		try {
			final Properties props = new Properties();
			props.put("mail.smtp.user", awsMailConfig.getUserName());
			props.put("mail.smtp.host", awsMailConfig.getSmtpHost());
			props.put("mail.smtp.port", awsMailConfig.getSmtpPort());
			props.put("mail.smtp.auth", "true");

			if (awsMailConfig.getSmtpTls()) {
				props.put("mail.smtp.starttls.enable", "true");
			}
			if (awsMailConfig.getSmtpSsl()) {
				props.setProperty("mail.smtp.socketFactory.port", String.valueOf(awsMailConfig.getSmtpsPort()));
				props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			}

			final String userName = awsMailConfig.getUserName();
			final String password = awsMailConfig.getPassword();
			final Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(userName, password);
				}
			});

			final Message message = new MimeMessage(session);
			if (awsMailConfig.getFromUser() != "") {
				message.setFrom(new InternetAddress(awsMailConfig.getFromUser()));
			} else {
				message.setFrom(new InternetAddress(awsMailConfig.getUserName()));
			}
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(awsMailConfig.getUserName()));
			message.setSubject("Applifire: Mail configuration test mail");
			message.setText("Hi,\n Mail configuration test successful");

			Transport.send(message);
			statusMsg = "success";

		} catch (final AuthenticationFailedException e) {
			e.printStackTrace();
			return e.getMessage();
		} catch (final MessagingException e) {
			e.printStackTrace();
			return e.getMessage();
		}
		return statusMsg;
	}

	/*
	 * @Override public void addEntry(JSONArray JSON, RuntimeLoginInfoHelper
	 * loginInfoHelper) throws JSONException, BuzzorPersistentException,
	 * ServiceException, JsonParseException, JsonMappingException, IOException {
	 * RadMailConfig radMailConfig = radMailConfigRepository.getMailConfig();
	 * AwsMailConfig awsMailConfig = new AwsMailConfig();
	 * awsMailConfig.setConfigName(radMailConfig.getConfigName());
	 * awsMailConfig.setDefaultCheck(radMailConfig.getDefaultCheck());
	 * awsMailConfig.setUserName(radMailConfig.getUserName());
	 * awsMailConfig.setPassword(radMailConfig.getPassword());
	 * awsMailConfig.setFromUser(radMailConfig.getFromUser());
	 * awsMailConfig.setSmtpAuth(radMailConfig.getSmtpAuth());
	 * awsMailConfig.setSmtpHost(radMailConfig.getSmtpHost());
	 * awsMailConfig.setSmtpPort(radMailConfig.getSmtpPort());
	 * awsMailConfig.setSmtpsPort(radMailConfig.getSmtpsPort());
	 * awsMailConfig.setSmtpSsl(radMailConfig.getSmtpSsl());
	 * awsMailConfig.setSmtpTls(radMailConfig.getSmtpTls());
	 * awsMailConfig.setProjectId(loginInfoHelper.getProjectId());
	 * awsMailConfig.setProjectVersionId(loginInfoHelper.getProjectVersionId());
	 * awsMailConfig.setAppCreatorId(loginInfoHelper.getAppCreatorId());
	 * awsMailConfig.setCreatedBy(loginInfoHelper.getUserId());
	 * awsMailConfig.setCreatedDate(new Timestamp(System.currentTimeMillis()));
	 * awsMailConfig.setUpdatedBy(loginInfoHelper.getUserId());
	 * awsMailConfig.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
	 * awsMailConfig.setActiveStatus(true); awsMailConfig.setVersionId(1l);
	 * awsMailConfigRepository.createAwsAppsInfo(awsMailConfig); }
	 */

	@Override
	public void cloneObjects(final RuntimeLoginInfoHelper oldProjectInfoHelper, final RuntimeLoginInfoHelper newProjectInfoHepler) throws ServiceException {
		// TODO Auto-generated method stub

	}
System.out.println("LINE HAS BEEN ADDED TO CHECK STATUS");

}
