package org.openmrs.module.labintegration.api.communication.hl7.messages;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.openmrs.Patient;
import org.openmrs.api.PatientService;
import org.openmrs.module.labintegration.api.communication.hl7.messages.testdata.HL7TestOrder;
import org.openmrs.module.labintegration.api.hl7.messages.HL7OrderMessageGenerator;
import org.openmrs.test.BaseModuleContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@ContextConfiguration(locations = { "classpath*:moduleApplicationContext.xml", "classpath*:applicationContext-service.xml",
        "classpath*:test-labContext.xml" }, inheritLocations = false)
public class HL7OrderMessageGeneratorTest extends BaseModuleContextSensitiveTest {
	
	private static final String DATASET = "lab-dataset.xml";
	
	private static final String EXPECTED_FILE = "OML_O21.hl7";
	
	private static final int PATIENT_ID = 10;
	
	@Autowired
	private HL7OrderMessageGenerator hl7OrderMessageGenerator;
	
	@Autowired
	private PatientService patientService;
	
	@Test
	public void shouldGenerateMessage() throws Exception {
		executeDataSet(DATASET);
		Patient patient = patientService.getPatient(PATIENT_ID);
		HL7TestOrder order = new HL7TestOrder(patient);
		
		String msg = hl7OrderMessageGenerator.createMessage(order.value(), "NW");

		String expected = readExpected();
		assertFalse(msg.contains("\n"));
		assertFalse(expected.contains("\n"));
		assertEquals(StringUtils.countMatches(expected, "\r"),
				StringUtils.countMatches(msg, "\r"));
		assertEquals(expected, msg);
	}
	
	private String readExpected() throws IOException {
		InputStream in = null;
		try {
			in = getClass().getClassLoader().getResourceAsStream(EXPECTED_FILE);
			String expected = IOUtils.toString(in);
			
			// remove lfs from file
			expected = expected.replace("\r\n", "\r");
			return expected.replace("\n", "\r");
		}
		finally {
			IOUtils.closeQuietly(in);
		}
	}
}
