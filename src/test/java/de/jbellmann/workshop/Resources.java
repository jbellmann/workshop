package de.jbellmann.workshop;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.DataFormatException;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.rest.api.MethodOutcome;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Observation;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.core.io.ClassPathResource;

@Slf4j
@UtilityClass
public class Resources {

  public static final String PATIENT_ID_TOKEN = "\\{\\{patientId\\}\\}";

  public static IBaseResource loadResource(Class clazz, String path) throws IOException {
    String resource = IOUtils.toString(new ClassPathResource(path).getInputStream(), StandardCharsets.UTF_8);
    String fileExtension = FilenameUtils.getExtension(path);
    IParser parser;
    if(fileExtension.equals(".xml")) {
      parser = FhirContext.forR4().newXmlParser();
    } else if (fileExtension.equals(".json")) {
      parser = FhirContext.forR4().newJsonParser();
    } else {
      throw new DataFormatException("File extension has to be either '.json' or '.xml'");
    }

    return parser.parseResource(clazz, resource);
  }

  public static String loadResourceToString(String path) throws IOException {
    return IOUtils.toString(new ClassPathResource(path).getInputStream(), StandardCharsets.UTF_8);
  }

  public static void logOutcome(MethodOutcome outcome, IParser parser) {
    log.info("OUTCOME_ID:{}", outcome.getId());
    String asString = parser.encodeResourceToString(outcome.getResource());
    log.info(asString);
    log.info(outcome.getResource().toString());
    if(outcome.getResource().getClass().isAssignableFrom(Patient.class)) {
      Patient patient = (Patient) outcome.getResource();
      String subjectId = patient.getIdentifier().stream()
          .filter(i -> "urn:ietf:rfc:4122".equals(i.getSystem()))
          .map(i -> i.getValue())
          .findFirst()
          .orElse("UNKNOWN");

      log.info("SUBJECT_ID : {}", subjectId);
    } else if (outcome.getResource().getClass().isAssignableFrom(Observation.class)) {
      Observation observation = (Observation) outcome.getResource();
      String subjectId = observation.getSubject().getIdentifier().getValue();

      log.info("SUBJECT_ID : {}", subjectId);
    } else {
      log.info("NO SUBJECT_ID FOR RESOURCE OF TYPE : {}", outcome.getResource().getClass().getSimpleName());
    }
  }

}
