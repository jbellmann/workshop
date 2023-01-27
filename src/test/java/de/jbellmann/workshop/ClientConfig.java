package de.jbellmann.workshop;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.interceptor.BearerTokenAuthInterceptor;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.ehrbase.client.openehrclient.OpenEhrClientConfig;
import org.ehrbase.client.openehrclient.defaultrestclient.DefaultRestClient;
import org.ehrbase.webtemplate.templateprovider.FileBasedTemplateProvider;
import org.springframework.context.annotation.Bean;

public class ClientConfig {

  public static final String TOKEN = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJnLTF1eTdMZGgySmRTZTVCa1NlMkRQNHdHM1JBaEtub1UyWlhBTE9KNElvIn0.eyJleHAiOjE2NzQ3ODAyNTUsImlhdCI6MTY3NDc3OTk1NSwianRpIjoiYTRiMDZkOWYtYWRhMi00Y2Q1LTliNTMtOGFiYzU2NjcwYWI3IiwiaXNzIjoiaHR0cDovL2tleWNsb2FrOjgwODIvcmVhbG1zL3dvcmtzaG9wIiwiYXVkIjoiYWNjb3VudCIsInN1YiI6IjExNTVkN2YyLWQwZmQtNDkxOC04OGU2LWVkNThkZTk4ZmIxZiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImZoaXItYnJpZGdlIiwiYWNyIjoiMSIsInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJkZWZhdWx0LXJvbGVzLXdvcmtzaG9wIiwib2ZmbGluZV9hY2Nlc3MiLCJ1bWFfYXV0aG9yaXphdGlvbiJdfSwicmVzb3VyY2VfYWNjZXNzIjp7ImZoaXItYnJpZGdlIjp7InJvbGVzIjpbInVtYV9wcm90ZWN0aW9uIl19LCJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6ImVtYWlsIHByb2ZpbGUiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsImNsaWVudElkIjoiZmhpci1icmlkZ2UiLCJjbGllbnRIb3N0IjoiMTkyLjE2OC44MC4xIiwicHJlZmVycmVkX3VzZXJuYW1lIjoic2VydmljZS1hY2NvdW50LWZoaXItYnJpZGdlIiwiY2xpZW50QWRkcmVzcyI6IjE5Mi4xNjguODAuMSJ9.PZecQnXa8NCBH_F9sq6RiGAS_AvOPFhkUfTR5sysYZGFGFFAao5zhvMwvDRKZyqDMtD2e4h7GvR46Kxww6slGhBEPFib0IuesuqSoj0iKRhE_qarN3yS2EZITSwAQEEL2mzX1xiF1V9G4dJrbGJRFuCj226EZi2YCtxt3W1jy9W43SLsYWcr7DRRiRSQ8Zdsr1Q6qHn8e89NFzQBqd_RMENDD95lrgM73rHwl6uy6cQNLatn-PDiKXZgUoP_A_7G6Z3lcvthRnCNDH75HD0Y1BpNMl5zevshd2ne3LC4J-8pqvYXT45n0vqeLHFuY3tMAnC_OWZ-rboxTD40QIHrXw";

  @Bean
  public DefaultRestClient ehrbaseClient() throws URISyntaxException {
    CloseableHttpClient httpClient = HttpClientBuilder.create()
        .addInterceptorLast(
        (HttpRequestInterceptor) (httpRequest, httpContext) -> httpRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + TOKEN)
        ).build();

    DefaultRestClient client = new DefaultRestClient(
        new OpenEhrClientConfig(new URI("http://localhost:8080/ehrbase/")),
        new FileBasedTemplateProvider(Path.of("src")),
        httpClient);

    return client;
  }

  @Bean
  public IGenericClient fhirClient() {
    FhirContext context = FhirContext.forR4();
    context.getRestfulClientFactory().setSocketTimeout(60 * 1000);
    IGenericClient client = context.newRestfulGenericClient("http://localhost:8888/fhir-bridge/fhir/");
    client.registerInterceptor(new BearerTokenAuthInterceptor(TOKEN));
    return client;
  }
}
