/***************************************************************************
*                                                                          *
*  Organization: Lawrence Livermore National Lab (LLNL)                    *
*   Directorate: Computation                                               *
*    Department: Computing Applications and Research                       *
*      Division: S&T Global Security                                       *
*        Matrix: Atmospheric, Earth and Energy Division                    *
*       Program: PCMDI                                                     *
*       Project: Earth Systems Grid (ESG) Data Node Software Stack         *
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
*   Earth System Grid (ESG) Data Node Software Stack, Version 1.0          *
*                                                                          *
*   For details, see http://esg-repo.llnl.gov/esg-node/                    *
*   Please also read this link                                             *
*    http://esg-repo.llnl.gov/LICENSE                                      *
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

/**
   Description:

**/
package esg.node.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.impl.*;

public abstract class Gateway extends AbstractDataNodeComponent{

    private static final Log log = LogFactory.getLog(Gateway.class);
    private String serviceURL = null;

    protected boolean isValid = false;
    protected boolean isAvailable = false;
    
    public Gateway(String name, String serviceURL) { 
	super(name); 
	this.serviceURL = serviceURL;
    }

    public Gateway(String name) { this(name,null); }

    public abstract void init();
    protected void setServiceURL(String serviceURL) { this.serviceURL = serviceURL; }
    public String getServiceURL() { return serviceURL; }
    public boolean isValid() { return isValid; }
    public boolean isAvailable() { return isAvailable; }
    
    
    //-----------------------------------------------------------------
    //Delgating: remote method wrappers.
    //-----------------------------------------------------------------
    //override me (all services should have a "ping" remote method! according to me! :-)
    public abstract boolean ping();

    //Present the gateway with a token and callback address for 
    //making calls back to the data node services.
    public abstract boolean registerToGateway();
    //-----------------------------------------------------------------
    


    //-----------------------------------------------------------------
    //Overriding superclass to perform gateway object specific unregistration
    //from the data node manager. (unrelated to RPC, internal management)
    //-----------------------------------------------------------------
    public void unregister() {
	DataNodeManager dataNodeManager = getDataNodeManager();
	if(dataNodeManager == null) return;
	if(this instanceof Gateway) { dataNodeManager.removeGateway(this); }
	//Note: don't have to null out mgr like in the super class
	//because I am never holding on to a mgr handle directly ;-)
    }
    //-----------------------------------------------------------------


    public String toString() {
	return "Gateway: ["+getName()+"] -> ["+(serviceURL == null ? "NULL" : serviceURL)+"] ("+isValid+")";
    }
    
}
