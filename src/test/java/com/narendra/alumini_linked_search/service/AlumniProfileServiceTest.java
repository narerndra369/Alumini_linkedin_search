package com.narendra.alumini_linked_search.service;

import com.narendra.alumini_linked_search.dto.AlumniProfileResponse;
import com.narendra.alumini_linked_search.dto.AlumniSearchRequest;
import com.narendra.alumini_linked_search.model.AlumniProfile;
import com.narendra.alumini_linked_search.repository.AlumniProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AlumniProfileServiceTest {

    @Mock
    private PhantomBusterService phantomService;

    @Mock
    private AlumniProfileRepository repo;

    @InjectMocks
    private AlumniProfileService alumniProfileService;

    @Captor
    private ArgumentCaptor<AlumniProfile> alumniProfileArgumentCaptor;

    @Test
    void searchAndSaveAlumniProfiles_ShouldFetchAndSaveProfilesWithoutPassoutYear() {

        AlumniSearchRequest searchRequest = new AlumniSearchRequest();
        searchRequest.setUniversity("MIT");
        searchRequest.setDesignation("Software Engineer");

        List<AlumniProfileResponse> mockPhantomResponses = Arrays.asList(
                new AlumniProfileResponse("Jane Doe", "Software Engineer", "MIT", "Boston, MA", "Building the future of AI", null),
                new AlumniProfileResponse("John Smith", "Data Scientist", "MIT", "New York, NY", "Data-driven decision making", 2022) // One can still have a year
        );

        when(phantomService.fetchAlumniProfiles(any(AlumniSearchRequest.class))).thenReturn(mockPhantomResponses);
        when(repo.save(any(AlumniProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));


        List<AlumniProfileResponse> result = alumniProfileService.searchAndSaveAlumniProfiles(searchRequest);


        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Jane Doe", result.get(0).getName());
        assertNull(result.get(0).getPassoutYear()); // Assert that the optional field is null in the response
        assertEquals(2022, result.get(1).getPassoutYear());


        verify(phantomService, times(1)).fetchAlumniProfiles(searchRequest);
        verify(repo, times(2)).save(alumniProfileArgumentCaptor.capture());


        List<AlumniProfile> capturedProfiles = alumniProfileArgumentCaptor.getAllValues();
        assertEquals("Jane Doe", capturedProfiles.get(0).getName());
        assertNull(capturedProfiles.get(0).getPassoutYear(), "Passout year should be null for Jane Doe");

        assertEquals("John Smith", capturedProfiles.get(1).getName());
        assertEquals(2022, capturedProfiles.get(1).getPassoutYear(), "Passout year should be 2022 for John Smith");
    }

    @Test
    void searchAndSaveAlumniProfiles_WhenNoResults_ShouldReturnEmptyListAndNotSave() {

        AlumniSearchRequest searchRequest = new AlumniSearchRequest();
        searchRequest.setUniversity("Unknown University");

        when(phantomService.fetchAlumniProfiles(any(AlumniSearchRequest.class))).thenReturn(Collections.emptyList());


        List<AlumniProfileResponse> result = alumniProfileService.searchAndSaveAlumniProfiles(searchRequest);


        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repo, never()).save(any(AlumniProfile.class));
    }


    @Test
    void getAllAlumniProfiles_ShouldFetchAndMapAllProfilesWithOptionalPassoutYear() {

        AlumniProfile profile1 = new AlumniProfile();
        profile1.setId(1L);
        profile1.setName("Alice");
        profile1.setCurrentRole("Project Manager");
        profile1.setUniversity("Stanford");
        profile1.setPassoutYear(null); // Explicitly set to null

        AlumniProfile profile2 = new AlumniProfile();
        profile2.setId(2L);
        profile2.setName("Bob");
        profile2.setCurrentRole("UX Designer");
        profile2.setUniversity("Stanford");
        profile2.setPassoutYear(2020);

        List<AlumniProfile> mockAlumniEntities = Arrays.asList(profile1, profile2);

        when(repo.findAll()).thenReturn(mockAlumniEntities);



        List<AlumniProfileResponse> result = alumniProfileService.getAllAlumniProfiles();



        assertNotNull(result);
        assertEquals(2, result.size());


        assertEquals("Alice", result.get(0).getName());
        assertEquals("Project Manager", result.get(0).getCurrentRole());
        assertNull(result.get(0).getPassoutYear(), "Alice's passout year should be null");


        assertEquals("Bob", result.get(1).getName());
        assertEquals("UX Designer", result.get(1).getCurrentRole());
        assertEquals(2020, result.get(1).getPassoutYear(), "Bob's passout year should be 2020");


        verify(repo, times(1)).findAll();
    }
}