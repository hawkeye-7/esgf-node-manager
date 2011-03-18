/***************************************************************************
*                                                                          *
*  Organization: Lawrence Livermore National Lab (LLNL)                    *
*   Directorate: Computation                                               *
*    Department: Computing Applications and Research                       *
*      Division: S&T Global Security                                       *
*        Matrix: Atmospheric, Earth and Energy Division                    *
*       Program: PCMDI                                                     *
*       Project: Earth Systems Grid Federation (ESGF) Data Node Software   *
*  First Author: Gavin M. Bell (gavin@llnl.gov)                            *
*                                                                          *
****************************************************************************
*                                                                          *
*   Copyright (c) 2009, Lawrence Livermore National Security, LLC.         *
*   Produced at the Lawrence Livermore National Laboratory                 *
*   Written by: Gavin M. Bell (gavin@llnl.gov)                             *
*   LLNL-CODE-420962                                                       *
*                                                                          *
*   All rights reserved. This file is part of the:                         *
*   Earth System Grid Federation (ESGF) Data Node Software Stack           *
*                                                                          *
*   For details, see http://esgf.org/esg-node/                             *
*   Please also read this link                                             *
*    http://esgf.org/LICENSE                                               *
*                                                                          *
*   * Redistribution and use in source and binary forms, with or           *
*   without modification, are permitted provided that the following        *
*   conditions are met:                                                    *
*                                                                          *
*   * Redistributions of source code must retain the above copyright       *
*   notice, this list of conditions and the disclaimer below.              *
*                                                                          *
*   * Redistributions in binary form must reproduce the above copyright    *
*   notice, this list of conditions and the disclaimer (as noted below)    *
*   in the documentation and/or other materials provided with the          *
*   distribution.                                                          *
*                                                                          *
*   Neither the name of the LLNS/LLNL nor the names of its contributors    *
*   may be used to endorse or promote products derived from this           *
*   software without specific prior written permission.                    *
*                                                                          *
*   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS    *
*   "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT      *
*   LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS      *
*   FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL LAWRENCE    *
*   LIVERMORE NATIONAL SECURITY, LLC, THE U.S. DEPARTMENT OF ENERGY OR     *
*   CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,           *
*   SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT       *
*   LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF       *
*   USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND    *
*   ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,     *
*   OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT     *
*   OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF     *
*   SUCH DAMAGE.                                                           *
*                                                                          *
***************************************************************************/
package esg.node.components.registry;

import esg.common.generated.registration.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import java.io.File;
import java.io.FileOutputStream;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.*;

/**
   Description:
   
   Encapsulates the logic for fetching and generating this local
   node's registration form, as defined by the registration.xsd.

*/
public class RegistrationGleaner {

    private static final Log log = LogFactory.getLog(RegistrationGleaner.class);

    private Node myNode = null;
    private String registrationFile = "registration.xml";


    public RegistrationGleaner() {}

    public Node getMyRegistration() { return myNode; }

    public boolean saveRegistration() { return saveRegistration(myNode); }
    public boolean saveRegistration(Node node) {
		boolean success = false;
        if (node == null) {
            log.error("Node is ["+node+"]"); 
            return success;
        }
        log.info("Saving registration information for "+node.getHostname());
		try{
			JAXBContext jc = JAXBContext.newInstance(Node.class);
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(node, new FileOutputStream(this.registrationFile));
			success = true;
		}catch(Exception e) {
			e.printStackTrace();
		}
        
		return success;
	}

    
    /**
       Looks through the current system and gathers the configured
       node service information.  Takes that information and
       creates a local representation of this node's registration.
    */
    public RegistrationGleaner createMyRegistration() {
        log.info("Creating my registration representation...");
        Node node = new Node();
		try{
			node.setHostname("faux_hostname");
			node.setNamespace("faux_namespace");
			node.setOrganization("faux_organization");
			node.setSupportEmail("faux@email.address");
			node.setDN("faux/dn/value");
            
			GatewayNodeType gw = new GatewayNodeType();
            
			CA ca = new CA();
			ca.setEndpoint("ca-endpoint");
			ca.setHash("ca-hash");
			ca.setDN("ca-dn");
			gw.setCA(ca);
			
			OpenIDProvider openid = new OpenIDProvider();
			openid.setEndpoint("openid-endpoint");
			openid.setDN("openid-dn");
			gw.setOpenIDProvider(openid);
			
			GatewayPortal gwp = new GatewayPortal();
			gwp.setEndpoint("gateway-endpoint");
			gwp.setDN("gateway-dn");
			gw.setGatewayPortal(gwp);
			
			AttributeService attrSvc = new AttributeService();
			attrSvc.setEndpoint("attrsvc-endpoint");
			attrSvc.setDN("attrsvc-dn");
            
			// enforces max number of groups to be 64
			String groupNameBase = "group-name";
			String groupDescriptionBase = "group-description";
			String tmpName, tmpDesc;
			for(int i = 0; i < 2; i++) {
                tmpName = groupNameBase+i;
                tmpDesc = groupDescriptionBase+i;
                if ((tmpName == null) && (tmpDesc == null)) {
                    break;
                }
                Group newGroup = new Group();
                newGroup.setName(tmpName);
                newGroup.setDescription(tmpDesc);
                attrSvc.getGroup().add(newGroup);
            }
			gw.setAttributeService(attrSvc);
            
			AuthorizationService authSvc = new AuthorizationService();
			authSvc.setEndpoint("authsvc-endpoint");
			authSvc.setDN("authsvc-dn");
			gw.setAuthorizationService(authSvc);
			
			node.setGatewayNode(gw);
            
			DataNodeType dNode = new DataNodeType();
			dNode.setPolicyNode("datanode-policy-node");
            
			OAIRepository oaiRepo = new OAIRepository();
			oaiRepo.setEndpoint("oairepo-endpoint");
			oaiRepo.setDN("oairepo-dn");
			dNode.setOAIRepository(oaiRepo);			
            
			ThreddsService tds = new ThreddsService();
			tds.setEndpoint("thredds-endpoint");
			tds.setDN("thredds-dn");
			dNode.setThreddsService(tds);
            
			GridFTPService gftp = new GridFTPService();
			gftp.setEndpoint("gftp-endpoint");
			String serviceType = "gftp-svctype";
			if (serviceType != null) {
                if (serviceType.equals(GridFTPServiceType.REPLICATION.value())) {
                    gftp.setServiceType(GridFTPServiceType.REPLICATION);
                }else{
                    gftp.setServiceType(GridFTPServiceType.DOWNLOAD);
                }
            }else {
                gftp.setServiceType(GridFTPServiceType.DOWNLOAD);
            }
			dNode.setGridFTPService(gftp);
			
			node.setDataNode(dNode);
		} catch(Exception e) {
            log.error(e);
        }
        
        myNode = node;
        return this;
    }
    
    public RegistrationGleaner loadMyRegistration() {
        log.info("Loading my registration info from "+registrationFile);
		try{
			JAXBContext jc = JAXBContext.newInstance(Node.class);
            Unmarshaller u = jc.createUnmarshaller();
            JAXBElement<Node> root = u.unmarshal(new StreamSource(new File(this.registrationFile)),Node.class);
            myNode = root.getValue();
		}catch(Exception e) {
			log.error(e);
		}
        return this;
    }

    public Node createRegistration(String registrationString) {
        return null;
    }

    //Allow this class to be used as a command line tool for bootstrapping this node.
    public static void main(String[] args) {
        if(args.length > 0) {
            if(args[0].equals("bootstrap")) {
                System.out.println(args[0]+"ing...");
                (new RegistrationGleaner()).createMyRegistration().saveRegistration();
            }else if(args[0].equals("load")) {
                System.out.println(args[0]+"ing...");
                (new RegistrationGleaner()).loadMyRegistration().saveRegistration();
            }else {
                System.out.println("illegal arg: "+args[0]);
            }
        }
    }
    
}