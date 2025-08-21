package com.narendra.alumini_linked_search.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

// This DTO maps the fields from the JSON result file of the Phantom.
// The @JsonProperty names must match the column names in your Phantom's output.
@Data
@JsonIgnoreProperties(ignoreUnknown = true) // Ignores any fields in the JSON not defined here
public class PhantomBusterResult {

    @JsonProperty("fullName") // Example JSON field name
    private String name;

    @JsonProperty("headline") // Example JSON field name
    private String linkedinHeadline;

    @JsonProperty("job") // Example JSON field name
    private String currentRole;

    @JsonProperty("school") // Example JSON field name
    private String university;

    @JsonProperty("location") // Example JSON field name
    private String location;

    @Override
    public String toString() {
        return "PhantomBusterResult{" +
                "name='" + name + '\'' +
                ", linkedinHeadline='" + linkedinHeadline + '\'' +
                ", currentRole='" + currentRole + '\'' +
                ", university='" + university + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
// Passout year is often not a direct field. You might need to parse it
    // from the education history text, which is more complex.
    // For simplicity, we'll leave it null if not directly available.
}