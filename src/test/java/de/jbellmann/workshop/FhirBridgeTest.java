package de.jbellmann.workshop;

import static de.jbellmann.workshop.Resources.PATIENT_ID_TOKEN;
import static de.jbellmann.workshop.Resources.loadResourceToString;
import static de.jbellmann.workshop.Resources.logOutcome;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.ehrbase.client.openehrclient.defaultrestclient.DefaultRestClient;
import org.hl7.fhir.r4.model.Patient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(classes = {ClientConfig.class})
@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class FhirBridgeTest {

  public static final String PATIENT_ID_KEY = "PatientIdKey";
  private final Map<String,String> db = new HashMap<>();

  @Autowired
  IGenericClient fhirClient;
  @Autowired
  DefaultRestClient ehrbaseClient;

  private IParser parser;

  @BeforeAll
  public void setup() {
    String patientId = UUID.randomUUID().toString();
    this.db.put(PATIENT_ID_KEY, patientId);
    log.info("Patient-ID created : {}", patientId);
    this.parser = FhirContext.forR4().newJsonParser();
  }
  @Test
  @Order(1)
  void createPatient() throws IOException {
    String resource = loadResourceToString("/Patient/create-patient.json");
    resource = resource.replaceAll(PATIENT_ID_TOKEN, db.get(PATIENT_ID_KEY));

    MethodOutcome outcome = fhirClient.create().resource(resource).execute();
    logOutcome(outcome, this.parser);
    Patient patient = (Patient) outcome.getResource();
    String fhirPatientResourceId = patient.getId();

    log.info("FHIR_RESOURCE_ID PATIENT : {}", fhirPatientResourceId);

    Assertions.assertNotNull(outcome.getId());
    Assertions.assertEquals(true, outcome.getCreated());
  }

  @Order(2)
  @ParameterizedTest
  @ValueSource(strings={
      "/Observation/BloodPressure/create-blood-pressure.json",
      "/Observation/BloodPressure/create-blood-pressure-absent.json",
      "/Observation/BloodPressure/create-blood-pressure_diastolic-magnitude-max.json",
      "/Observation/BloodPressure/create-blood-pressure_diastolic-magnitude-min.json"
  })
  void createBloodPressureObservations(String templatePath) throws IOException {
    String resource = loadResourceToString(templatePath);
    resource = resource.replaceAll(PATIENT_ID_TOKEN, db.get(PATIENT_ID_KEY));

    MethodOutcome outcome = fhirClient.create().resource(resource).execute();
    logOutcome(outcome, this.parser);

    Assertions.assertNotNull(outcome.getId());
    Assertions.assertEquals(true, outcome.getCreated());
  }
}
