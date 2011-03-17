package com.zarcode.platform.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.google.appengine.api.users.User;
import com.zarcode.common.PlatformCommon;
import com.zarcode.platform.dao.LoaderDao;
import com.zarcode.platform.model.AbstractLoaderDO;

public class JDOLoaderServlet extends HttpServlet {
	
	private Logger logger = Logger.getLogger(JDOLoaderServlet.class.getName());
	
	
	private StringBuilder uploadMessages = null;
	
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
    		throws IOException {
    	logger.info("doPost(): Entered"); 
    	loadDataObjects(req);
    	logger.info("doPost(): Processing Done"); 
    	resp.setContentType("text/html");
    	StringBuilder page = new StringBuilder();
    	page.append("<html><body>");
        page.append(uploadMessages.toString());
        page.append("<br><br><a href=\"/_admin\">Return to Admin Console</a>");
        page.append("</body></html>");
    	logger.info("doPost(): Returing: " + page.toString()); 
    	resp.getWriter().println(page.toString());
    }
	
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
    	loadDataObjects(req);
        resp.setContentType("text/html");
    	StringBuilder page = new StringBuilder();
    	page.append("<html><body>");
        page.append(uploadMessages.toString());
        page.append("<br><br><a href=\"/_admin\">Return to Admin Console</a>");
        page.append("</body></html>");
        resp.getWriter().println(page.toString());
    }
    
    /**
     * Use reflection and create an object based upon incoming XML.
     * @param className
     * @return
     */
    private Object createObject(String className) {
        Object object = null;
        try {
            Class classDefinition = Class.forName(className);
            object = classDefinition.newInstance();
        } 
        catch (InstantiationException e) {
        	logger.severe(e.getMessage());
        } 
        catch (IllegalAccessException e) {
        	logger.severe(e.getMessage());
        } 
        catch (ClassNotFoundException e) {
        	logger.severe(e.getMessage());
        }
        return object;
     }
    
    private void addMsg(String str) {
    	uploadMessages.append(str);
    	uploadMessages.append("<br>");
    }

    /**
     * This method parses incoming XML and creates object to loaded into my
     * JDO datastore.
     * 
     * @param req
     * @return
     */
    private void loadDataObjects(HttpServletRequest req) {
    	int i = 0;
    	int j = 0;
    	int k = 0;
    	String msg = null;
    	User user = null;
    	Node node = null;
    	NodeList list = null;
    	String nodeName = null;
    	Object target = null;
    	AbstractLoaderDao dao = null;
    	NamedNodeMap map = null;
    	
    	Class cls = null;
    	String access = "method";
    	
    	logger.info("loadDataObjects(): Entered"); 
    	
    	uploadMessages = new StringBuilder();
    	uploadMessages.append("<p>");
    	
    	HttpSession session = req.getSession();
    	
    	if (session != null) {
    		user = (User)session.getAttribute("USER");
    		if (user != null) {
    			try {
    				StringBuilder sb = new StringBuilder();
    		        String line = null;
    		        boolean xmlOn = false;
    		        Pattern p = Pattern.compile("<jdo-loader.*>");
    		        Matcher m = null;
    		        
    		        BufferedReader reader = req.getReader();
    		        while ((line = reader.readLine()) != null) {
    		        	m = p.matcher(line);
    		        	if (m.matches()) {
    		        		xmlOn = true;
    		        	}
    		        	if (xmlOn) {
    		        		sb.append(line).append("\n");
    		        	}
    		        	if (line.equalsIgnoreCase("</jdo-loader>")) {
    		        		xmlOn = false;
    		        	}
    		        }

    		        logger.info("loadDataObjects(): XML: " + sb.toString());
    				// DataInputStream in = new DataInputStream(req.getInputStream());
    		        StringReader sr = new StringReader(sb.toString());
    				InputSource src = new InputSource(sr);
    				
    				DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    	            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
    	            
    	            logger.info("loadDataObjects(): Try parsing now!");
    	            Document doc = docBuilder.parse(src);
    	            sr.close();

    	            //
    	            // normalize text representation
    	            //
    	            doc.getDocumentElement().normalize();
    	            logger.info("loadDataObjects(): Root element of the doc is " +  doc.getDocumentElement().getNodeName());

    	            // NodeList list = doc.getElementsByTagName("dataObject");
    	            Node firstChild = doc.getFirstChild();
    	            
    	            if (firstChild.getNodeName().equalsIgnoreCase("jdo-loader")) {
	    	            
	    	            //
	    	            // check if we should remove existing objects
	    	            //
    	            	map = firstChild.getAttributes();
	        			Node clobberClass = map.getNamedItem("clobberClass");
	        			Node clobberFilter = map.getNamedItem("clobberFilter");
	        			Node loaderDao = map.getNamedItem("loaderDao");
	        			//
	        			// do we have a loader dao?
	        			//
	        			if (loaderDao != null) {
    	            		//
    	            		// Loader User DAO
    	            		//
	        				String userDaoName = loaderDao.getNodeValue();
	        				Class userDaoClass = Class.forName(userDaoName);
	        				if (userDaoClass != null) {
	        					dao = (AbstractLoaderDao)createObject(userDaoName);
	        					if (dao == null) {
	        						//
	    	            			// Loader Basic DAO
	    	            			//
		        					dao =  new LoaderDao();
	        					}
	        				} 
	        				else {
	        					//
	    	            		// Loader Basic DAO
	    	            		//
		        				dao =  new LoaderDao();
	        				}
	        			}
	        			else {
    	            		//
    	            		// Loader Basic DAO
    	            		//
	        				dao =  new LoaderDao();
	        			}
	        			//
	        			// should we clobber existing data types?
	        			//
	        			if (clobberClass != null) {
	        				String objectName = clobberClass.getNodeValue();
	        				
	        				cls = Class.forName(objectName);
	        				if (clobberFilter != null) {
	        					String filter = clobberFilter.getNodeValue();
	        					logger.info("loadDataObjects(): Trying to delete all objects -- " + filter);
	        					long count = dao.deleteAll(cls);
	        					logger.info("loadDataObjects(): # of objects deleted: " + count);
	        				}
	        				else {
	        					logger.info("loadDataObjects(): Trying to delete all [" + objectName + "] object(s)");
	        					long count = dao.deleteAll(cls);
	        					logger.info("loadDataObjects(): # of objects deleted: " + count);
	        					
	        				}
	        			}
	        			else {
	        				logger.info("loadDataObjects(): Update existing!!");
	        			}
    	            	list = firstChild.getChildNodes();
	    	            int count = list.getLength();
	    	            int objectsAdded = 0;
	    	            //
	    	            // process data objects
	    	            //
	    	            for(i=0; i<count; i++) {
	    	                node = list.item(i);
	    	                if (node != null) {
	    	                	nodeName = node.getNodeName();
	    	                	if (nodeName.equalsIgnoreCase("#text")) {
	    	                		continue;
	    	                	}
	    	                	if (nodeName.equalsIgnoreCase("#comment")) {
	    	                		continue;
	    	                	}
	    	                	if (nodeName.equalsIgnoreCase("dataObject")) {
	    	                		target = _createDataObject(node);
	    	                		if (target != null) {
	    	                			// save object
	    	                			logger.info("loadDataObjects(): Adding dataObject: " + target.toString());
	    	                			dao.loadObject(target);
	    	                			objectsAdded++;
	    	                		}
	    	                	}
	    	                	else {
	    	                		logger.warning("loadDataObjects(): Invalid node name --> " + nodeName);
	    	                		addMsg("XML ERROR: Expecting '<dataObject>', but found: " + nodeName + "\n");
	    	                	}
	    	                }
	    	            }
	    				msg = "Number of object(s) uploaded:  " + objectsAdded;
	    				addMsg(msg);
    	            }
    	            else {
    	            	logger.warning("loadDataObjects(): Expecting 'jdo-loader', but found: " + firstChild.getNodeName());
    	            	addMsg("XML ERROR: Expecting '<jdo-loader>', but found: " + firstChild.getNodeName() + "\n");
    	            }
    			}
    			catch (Exception e) {
    				logger.severe("loadDataObjects(): [EXCEPTION]\n" + getStackTrace(e));
    	            addMsg("EXCEPTION: " + getStackTrace(e));
    			}
    		}
    		else {
    			msg = "Session does not exists [USER]";
	    		addMsg(msg);
    		}
    	}
    	else {
    		msg = "Session does not exists [SESSION]";
	    	addMsg(msg);
    	}
    	uploadMessages.append("</p>");
    }
    
    private Object _createDataObject(Node node) {
    	int j = 0;
    	Class cls = null;
    	Class nextCls = null;
    	Object target = null;
    	String access = "method";
    	Node next = null;
    	NamedNodeMap map1 = null;
    	NamedNodeMap map2 = null;
    	Element elem = null;
    	
    	logger.info("Start processing node: " + node);
    	
    	try {
	    	map1 = node.getAttributes();
	    	Node namespace = map1.getNamedItem("namespace");
	    	Node ac = map1.getNamedItem("access");
	    	if (namespace != null) {
	        	String objectName = namespace.getNodeValue();
	        	if (ac != null) {
	        		access = ac.getNodeName();
	        	}
	        	logger.info("name=" + objectName);
	        	
	        	NodeList fields = node.getChildNodes();
	        	cls = Class.forName(objectName);
	        	if (cls != null) {
		        	target = createObject(objectName);
		        	for (j=0; j<fields.getLength(); j++)  {
		        		next = fields.item(j);
		        		String val = null;
		        		String key = next.getNodeName();
		        		
		        		if (key == "#text") {
		        			continue;
		        		}
		        		
		        		logger.info("key=" + key + " childNodes: " + next.getChildNodes().item(0));
		        		
		        		//
		        		// handle embedded dataObject
		        		//
		        		if (key.equalsIgnoreCase("dataObject")) {
		        			map2 = next.getAttributes();
		        			Node memberName = map2.getNamedItem("memberName");
		        			Node ns = map2.getNamedItem("namespace");
		        			nextCls = Class.forName(ns.getNodeValue());
		        			if (nextCls != null) {
		        				Object createdObject = _createDataObject(next);
		        				if (createdObject != null) {
		        					String mName = memberName.getNodeValue();
		        					Class args[] = new Class[1];
		        					args[0] = nextCls;
		        					mName = PlatformCommon.capitalize(mName);
		        					Method m = cls.getMethod("set" + mName, args);
		        					Object arglist[] = new Object[1];
		        					arglist[0] = createdObject;
		            				m.invoke(target, arglist);
		        				}
		        				else {
		        					logger.warning("Unable to create data object.");
		        				}
		        			}
		        			else {
		        				logger.warning("Unable to find class [" + 
		        					ns.getNodeValue() + 
		        					"] to create data object");
		        			}
	        			}
		        		//
		        		// handle basic ordinal type of the dataObject
		        		//
		        		else {
		        			if (next.getChildNodes().item(0) != null) {
		        				val = next.getChildNodes().item(0).getNodeValue();
		        			}
			        		logger.info("key=" + key + " val=" + val);
			        		if (access.equalsIgnoreCase("method")) {
			        			usePublicSetter(target, cls, key, val);
			        		}
			        		else if (access.equalsIgnoreCase("member") ) {
			        			usePublicMembers(target, cls, key, val);
			        		}
			        		else {
			        			usePublicSetter(target, cls, key, val);
			        		}
		        		}
		        	}
	        	}
	        	else {
	        		logger.severe("Unable to find objectName: " + objectName);
	        		addMsg("DATAOBJECT ERROR: Unable to find objectName: " + objectName + "\n");
	        	}
	    	}
	    	else {
	    		logger.severe("Namespace not provided -- Unable to create dataObject");
	        	addMsg("DATAOBJECT ERROR:  Namespace not provided -- Unable to create dataObject" + namespace + "\n");
	    	}
    	}
    	catch (Exception e) {
    	    addMsg("CREATE OBJECT EXCEPTION: " + getStackTrace(e));
    		logger.severe("[EXCEPTION]\n" + getStackTrace(e));
    	}
    	
    	if (target != null) {
    		((AbstractLoaderDO)target).postCreation();
    	}
    	
    	return target;
    }
    
    
    private void usePublicSetter(Object target, Class cls, String key, String val) {
    	int i = 0;
    	String methodName = null;
    	String temp = null;
    	Method m = null;
    	
    	logger.info("usePublicSetter(): key=" + key + " val=" + val);
    			
    	try {
    		methodName = "set" + key;
    		Method mList[] = cls.getDeclaredMethods();
    		boolean foundMethod = false;
            for (i=0; i<mList.length; i++) {
            	m = mList[i];
            	methodName = m.getName();
            	temp = "set" + key;
            	if (methodName.equalsIgnoreCase(temp)) {
            		foundMethod = true;
            		logger.info("usePublicSetter(): method=" + m.getName());
            		Class pvec[] = m.getParameterTypes();
            		Class type = pvec[0];
            		Object arglist[] = new Object[1];
            		if (type == Integer.TYPE) {
            			arglist[0] = Integer.parseInt(val);
            			m.invoke(target, arglist);
            		}
            		else if (type == Long.TYPE) {
						logger.info("usePublicSetter(): setting long - " + key);
            			arglist[0] = Long.parseLong(val);
            			m.invoke(target, arglist);
    				}
					else if (type == Short.TYPE) {
						logger.info("usePublicSetter(): setting short - " + key);
    					arglist[0] = Short.parseShort(val);
            			m.invoke(target, arglist);
    				}
					else if (type == Double.TYPE) {
						logger.info("usePublicSetter(): setting double - " + key);
    					arglist[0] = Double.parseDouble(val);
            			m.invoke(target, arglist);
    				}
					else if (type == Float.TYPE) {
						logger.info("usePublicSetter(): setting float - " + key);
    					arglist[0] = Float.parseFloat(val);
            			m.invoke(target, arglist);
    				}
					else if (type == String.class) {
						logger.info("usePublicSetter(): setting String - " + key);
    					arglist[0] = val;
            			m.invoke(target, arglist);
    				}
					else if (type == Boolean.TYPE) {
						logger.info("usePublicSetter(): setting Boolean - " + key);
    					arglist[0] = Boolean.parseBoolean(val);
            			m.invoke(target, arglist);
    				}
					else if (type == Byte.TYPE) {
						logger.info("usePublicSetter(): setting Byte - " + key);
    					arglist[0] = Byte.parseByte(val);
            			m.invoke(target, arglist);
    				}
					else if (type == char.class) {
						if (val != null && val.length() > 0) {
							logger.info("usePublicSetter(): setting char - " + key);
							arglist[0] = val.charAt(0);
							m.invoke(target, arglist);
						}
    				}
    				else {
    					logger.warning("usePublicSetter(): Unable to find data type=" + type);
    					addMsg("SET METHOD ERROR: Unable to find data type: " + type  + "\n");
    				}
            	}
            }
            if (!foundMethod) {
            	addMsg("SET METHOD ERROR: Unable to find matching public method for " + key  + "\n");
            }

    	}
	    catch (Exception e) {
			logger.severe("usePublicSetter(): [EXCEPTION]\n" + getStackTrace(e));
    	    addMsg("SET METHOD EXCEPTION: " + getStackTrace(e));
	    }
    }
    
    private void usePublicMembers(Object target, Class cls, String key, String val) {
    	int k = 0;
    	Field fld = null;
    	
    	try {
			Field fieldlist[] = cls.getDeclaredFields();
			for (k=0; k<fieldlist.length; k++) {
				fld = fieldlist[k];
				if (fld == null) {
					continue;
				}
				logger.info("usePublicMembers(): Found field=" + fld.getName());
				if (fld.getName().equalsIgnoreCase(key)) {
					if (fld.getType() == Integer.TYPE) {
						logger.info("usePublicMembers(): setting int - " + key);
    					fld.setInt(target, Integer.parseInt(val));
    				}
					else if (fld.getType() == Long.TYPE) {
						logger.info("usePublicMembers(): setting long - " + key);
    					fld.setLong(target, Long.parseLong(val));
    				}
					else if (fld.getType() == Short.TYPE) {
						logger.info("usePublicMembers(): setting short - " + key);
    					fld.setShort(target, Short.parseShort(val));
    				}
					else if (fld.getType() == Double.TYPE) {
						logger.info("usePublicMembers(): setting double - " + key);
    					fld.setDouble(target, Double.parseDouble(val));
    				}
					else if (fld.getType() == Float.TYPE) {
						logger.info("usePublicMembers(): setting float - " + key);
    					fld.setFloat(target, Float.parseFloat(val));
    				}
					else if (fld.getType() == String.class) {
						logger.info("usePublicMembers(): setting String - " + key);
    					fld.set(target, val);
    					fld.setDouble(target, Double.parseDouble(val));
    				}
					else if (fld.getType() == Boolean.TYPE) {
						logger.info("usePublicMembers(): setting Boolean - " + key);
    					fld.setBoolean(target, Boolean.parseBoolean(val));
    				}
					else if (fld.getType() == Byte.TYPE) {
						logger.info("usePublicMembers(): setting Byte - " + key);
    					fld.setByte(target, Byte.parseByte(val));
    				}
					else if (fld.getType() == char.class) {
						if (val != null && val.length() > 0) {
							logger.info("usePublicMembers(): setting char - " + key);
							fld.setChar(target, val.charAt(0));
						}
    				}
    				else {
    					logger.warning("usePublicMembers(): Unable to find data type=" + fld.getType());
    					addMsg("SET MEMBER ERROR: Unable to find data type: " + fld.getType()  + "\n");
    				}
				}
			}
		}
	    catch (IllegalAccessException e) {
			logger.severe("usePublicMembers(): [EXCEPTION]\n" + getStackTrace(e));
    	    addMsg("SET MEMBER EXCEPTION: " + getStackTrace(e));
	    }
    }
    
    private String getStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String str = "\n" + sw.toString();
        return str;
	}
}