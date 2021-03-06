package chemtrail;

import java.net.URL;

import javax.xml.namespace.QName;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.message.addressing.ReferencePropertiesType;
import org.apache.axis.message.addressing.Address;
import org.globus.exec.client.GramJob;
import org.globus.exec.client.GramJobListener;
import org.globus.exec.generated.MultiJobDescriptionType;
import org.globus.exec.generated.StateEnumeration;
import org.globus.exec.generated.JobDescriptionType;
import org.globus.exec.generated.FilePairType;
import org.globus.exec.utils.ManagedJobConstants;
import org.globus.exec.utils.ManagedJobFactoryConstants;
import org.globus.exec.utils.client.ManagedJobFactoryClientHelper;
import org.globus.wsrf.impl.security.authentication.Constants;
import org.globus.wsrf.impl.security.authorization.Authorization;
import org.globus.wsrf.impl.security.authorization.HostAuthorization;
import org.globus.wsrf.impl.SimpleResourceKey;

public class SubmitJob implements GramJobListener  {

	private static Object waiter = new Object();

	public static Object getWaiter() {
		return waiter;
	}



	public void submitJob(MultiJobDescriptionType multi) throws Exception {
			// create factory epr
			String factoryType = ManagedJobFactoryConstants.FACTORY_TYPE.MULTI;
			try {
				System.out.println("Preparing for job submission");
			
				EndpointReferenceType factoryEndpoint = Chemtrail.getFactoryEPR(Chemtrail.contact,factoryType);
				ReferencePropertiesType props = new ReferencePropertiesType();
				SimpleResourceKey key = 
						  new SimpleResourceKey(ManagedJobConstants.RESOURCE_KEY_QNAME, "Multi");
				props.add(key.toSOAPElement());
				factoryEndpoint.setProperties(props);

				// setup security
				System.out.println("Setting security parameters");
				Authorization authz = HostAuthorization.getInstance();
				Integer xmlSecurity = Constants.ENCRYPTION;

				boolean batchMode = false;
				boolean limitedDelegation = true;

				// generate job uuid
				System.out.println("Generating Job UUID");
				UUIDGen uuidgen   = UUIDGenFactory.getUUIDGen();
				String submissionID = "uuid:" + uuidgen.nextUUID();

				GramJob job = new GramJob(multi);
				job.setAuthorization(authz);
				job.setMessageProtectionType(xmlSecurity);
				job.setDelegationEnabled(true);
				job.addListener(this);
				
				System.out.println("Submitting job");
				job.submit(factoryEndpoint,
				batchMode,
				limitedDelegation,
				submissionID);
				System.out.println("Multijob submitted");
			}
			catch (Exception e) {
				e.printStackTrace();
			}	
	}

        // GramJob calls this method when a job changes its state
        // It's part of GramJobListener Interface
        public void stateChanged(GramJob job) {
        
                StateEnumeration jobState = job.getState();
                System.out.println("got state notifiation: job is in state " + jobState);
		
                if (jobState.equals(StateEnumeration.Done)
                                || jobState.equals(StateEnumeration.Failed)) {
                        System.out.print("job finished. destroying job resource ... ");
                        try {
                                job.removeListener(this);
                                job.destroy();
                        } catch (Exception e) {
                                e.printStackTrace();
                        } finally {
                                System.out.println("done");
                                synchronized (waiter) {
                                        waiter.notify();
                                }
                        }
                }
        }
}
