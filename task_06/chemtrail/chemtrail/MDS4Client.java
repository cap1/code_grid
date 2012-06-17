package chemtrail;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;

import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.Stub;
import javax.xml.transform.TransformerException;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.AttributedURI;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;

import org.apache.axis.message.MessageElement;
import org.oasis.wsrf.servicegroup.EntryType;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.globus.mds.aggregator.types.AggregatorContent;
import org.globus.mds.aggregator.types.AggregatorData;

import org.globus.axis.util.Util;
import org.globus.wsrf.WSRFConstants;
import org.globus.wsrf.impl.security.authorization.NoAuthorization;
import org.globus.wsrf.utils.AnyHelper;
import org.oasis.wsrf.properties.QueryExpressionType;
import org.oasis.wsrf.properties.QueryResourcePropertiesResponse;
import org.oasis.wsrf.properties.QueryResourceProperties_Element;
import org.oasis.wsrf.properties.QueryResourceProperties_PortType;
import org.oasis.wsrf.properties.WSResourcePropertiesServiceAddressingLocator;


public class MDS4Client
{

     

        /**
        * Query the data from the MDS4 information service
        *
        * @param stringAddress  The address (URI) of the MDS4 information service
        * @param stringExpression       The XPath query expression
        * @return       The collection of the registered ManagedJobFactoryServices
        */
        public QueryResourcePropertiesResponse query(String stringAddress, 
                String stringExpression)
        throws MalformedURIException, ServiceException, RemoteException 
        {
                Util.registerTransport();
                final WSResourcePropertiesServiceAddressingLocator locator =
                        new WSResourcePropertiesServiceAddressingLocator();

                System.out.println("Adress of the information service: " + stringAddress);
                final EndpointReferenceType epr = 
                        new EndpointReferenceType(new Address(stringAddress));

                System.out.println("Query expression: " + stringExpression);
                final QueryExpressionType query = new QueryExpressionType();
                query.setDialect(WSRFConstants.XPATH_1_DIALECT);
                query.setValue(stringExpression); 

                final QueryResourceProperties_PortType port = 
                        locator.getQueryResourcePropertiesPort(epr);
                final Stub portStub = (Stub) port;

                // Deafult Security: Host authorization + XML encryption
                //Authorization authz = HostAuthorization.getInstance();
                //Integer xmlSecurity = Constants.ENCRYPTION;
                portStub._setProperty(org.globus.wsrf.security.Constants.AUTHORIZATION,
                        NoAuthorization.getInstance());
                portStub._setProperty(org.globus.wsrf.security.Constants.GSI_ANONYMOUS,
                        Boolean.TRUE);

                final QueryResourceProperties_Element request = 
                        new QueryResourceProperties_Element();
                request.setQueryExpression(query);

                System.out.println("Starting the query...");
                final QueryResourcePropertiesResponse response = 
                        port.queryResourceProperties(request);
                System.out.println("Query finished...");

                return response;
        }

        /**
        * Query the ManagedJobFactoryServices from the MDS4 information service
        *
        * @param stringAddress  The address (URI) of the MDS4 information service
        * @param stringExpression       The XPath query expression
        * @return       The collection of the registered ManagedJobFactoryServices
        */
        public ArrayList queryForManagedJobFactoryService(String stringAddress)
        {
                ArrayList listServices = new ArrayList();
                String stringQuery = "//*" +
                                "[local-name()='Entry' " +
                                        "and child::*[local-name()='MemberServiceEPR' " +
                                                "and child::*[local-name()='Address' "+ 
                                                "and contains(text(),'ManagedJobFactoryService')] " +
                                                "and child::*[local-name()='ReferenceProperties' " +
                                                        "and child::*[local-name()='ResourceID' " +
                                                        "and text()='Fork'" +
                                "]     ]  ]]";
                try {
                        final QueryResourcePropertiesResponse response = 
                                query(stringAddress, stringQuery);
                        MessageElement[] entries = response.get_any();
                        for(int i=0;entries!=null && i<entries.length;i++) {
                                EntryType entryType = null;
                                //DEBUG:                        System.out.println(">"+entries[i]);
                                try {
                                        entryType = 
                                                (EntryType)ObjectDeserializer.toObject(entries[i],EntryType.class);
                                } catch (Exception e) {
                                        System.err.println("Unable to deserialize entry: " + e);
                                        e.printStackTrace();
                                }


                                EndpointReferenceType eprType = entryType.getMemberServiceEPR();
                                if (eprType == null) {
                                        throw new Exception("Member service EPR null for entry type");
                                }

                                AttributedURI uri = eprType.getAddress();
                                listServices.add(uri);
                        }
                } catch (Exception e) {
                        e.printStackTrace();
                }
                return listServices;
        }


}
