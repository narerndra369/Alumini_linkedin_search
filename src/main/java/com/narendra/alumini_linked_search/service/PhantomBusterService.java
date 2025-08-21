package com.narendra.alumini_linked_search.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.narendra.alumini_linked_search.dto.AlumniProfileResponse;
import com.narendra.alumini_linked_search.dto.AlumniSearchRequest;
import com.narendra.alumini_linked_search.dto.PhantomBusterResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Optional;
import java.util.Arrays;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class PhantomBusterService {

    private static final Logger logger = LoggerFactory.getLogger(PhantomBusterService.class);
    private static final String PHANTOMBUSTER_API_V2_URL = "https://api.phantombuster.com/api/v2/";

    private final RestTemplate restTemplate;

    @Value("blh72eKMk9RMeOxyVIv34u8aYCyLZeKtN05uMvU71Gw")
    public String apiKey;

    @Value("6899119040571770")
    public String agentId;

    public PhantomBusterService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<AlumniProfileResponse> fetchAlumniProfiles(AlumniSearchRequest request) {
        System.out.println(apiKey + "----" + agentId);
        try {
            String containerId = launchPhantom(request);
            logger.info("Phantom launched successfully with container ID: {}", containerId);

            waitForPhantom(containerId);

            logger.info("Fetching results...");
            List<PhantomBusterResult> results = fetchPhantomResults();
            System.out.println(results);
            return mapResultsToResponse(results, request);

        } catch (Exception e) {
            logger.error("Error during PhantomBuster integration: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    private String launchPhantom(AlumniSearchRequest request) {
        String launchUrl = PHANTOMBUSTER_API_V2_URL + "agents/launch";
        HttpHeaders headers = createHeaders();

        Map<String, Object> argument = Map.of(
                "search", buildLinkedInSearchUrl(request),
                "numberOfProfilesPerLaunch", 10,
                "sessionCookie", "AQEFAHUBAAAAABcUjREAAAGYGTPs6wAAAZjcPvNBTQAAGHVybjpsaTptZW1iZXI6MTExMDkxNTAxNnQLTFHZ47nhxvU5wpA2loCEbIyQet8V9SWL8dPoMIC_yYQaS-NC_3NLC3vvRx-O7fS4hQfwNYr3k3PllIi1I5dvTSBC8yoVNCm7UKgQiCOTnaFrGnH3VIi06wrhnbGFT6rNPr2gssiwvzSWo7Pf_QsEW8kBh0Uv6ukX5Y5FDZkvUIi_yqHtLUl61nbrStgBJ63G7Uw"
        );

        Map<String, Object> body = Map.of("id", agentId, "argument", argument);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(launchUrl, entity, Map.class);
        System.out.println("Response"+response);
        return (String) Objects.requireNonNull(response.getBody()).get("containerId");
    }

    private void waitForPhantom(String containerId) throws InterruptedException {
        logger.info("Waiting for Phantom to complete... This may take a few minutes.");
        Thread.sleep(120000); // Wait for 2 minutes
    }



    private List<AlumniProfileResponse> mapResultsToResponse(List<PhantomBusterResult> results, AlumniSearchRequest request) {
        if (results == null) {
            return Collections.emptyList();
        }
        return results.stream()
                .map(result -> new AlumniProfileResponse(
                        result.getName(),
                        result.getCurrentRole(),
                        request.getUniversity(),
                        result.getLocation(),
                        result.getLinkedinHeadline(),
                        request.getPassoutYear()
                ))
                .collect(Collectors.toList());
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Phantombuster-Key", apiKey);
        return headers;
    }

    private String buildLinkedInSearchUrl(AlumniSearchRequest request) {
        String keywords = String.format("%s %s %s",
                request.getUniversity(),
                request.getDesignation() != null ? request.getDesignation() : "",
                request.getPassoutYear() != null ? request.getPassoutYear() : ""
        ).trim();

//        String schoolEntityId = "urn:li:organization:14537413";

        String s= UriComponentsBuilder.fromHttpUrl("https://www.linkedin.com/search/results/people/")
//                .queryParam("heroEntityKey", schoolEntityId)
                .queryParam("keywords", keywords)
                .queryParam("origin", "FACETED_SEARCH")
                .queryParam("serviceCategory", "[\"602\"]")
                .toUriString();
        System.out.println("URL"+s);
        return s;
    }
    private List<PhantomBusterResult> fetchPhantomResults() {
        String fetchOutputUrl = UriComponentsBuilder
                .fromHttpUrl(PHANTOMBUSTER_API_V2_URL + "agents/fetch-output")
                .queryParam("id", this.agentId)
                .toUriString();

        logger.info("Fetching results from URL: {}", fetchOutputUrl);
        HttpHeaders headers = createHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            ResponseEntity<String> rawResponse = restTemplate.exchange(
                    fetchOutputUrl, HttpMethod.GET, entity, String.class
            );
            String responseBody = rawResponse.getBody();
            if (responseBody == null) {
                logger.warn("Received empty response body from fetch-output.");
                return Collections.emptyList();
            }

            JsonNode rootNode = objectMapper.readTree(responseBody);
            String consoleOutput = rootNode.path("output").asText();

            if (consoleOutput.isEmpty()) {
                logger.warn("Agent console output was empty.");
                return Collections.emptyList();
            }

            Optional<String> jsonUrlOptional = Arrays.stream(consoleOutput.split("\\r?\\n"))
                    .filter(line -> line.contains("JSON saved at https://phantombuster.s3.amazonaws.com"))

                    .flatMap(line -> Arrays.stream(line.split(" ")))
                    .filter(word -> word.startsWith("https://phantombuster.s3.amazonaws.com"))
                    .findFirst();


            if (jsonUrlOptional.isPresent()) {
                String jsonUrl = jsonUrlOptional.get();
                logger.info("Found and cleaned result JSON URL: {}", jsonUrl);

                ResponseEntity<String> resultJsonResponse = restTemplate.getForEntity(jsonUrl, String.class);
                String resultJson = resultJsonResponse.getBody();

                return objectMapper.readValue(resultJson, new TypeReference<List<PhantomBusterResult>>() {});
            } else {
                logger.warn("Could not find result JSON URL in the agent output. The agent might have failed or found no results.");
                return Collections.emptyList();
            }

        } catch (Exception e) {
            logger.error("Failed to fetch or parse PhantomBuster results: {}", e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}