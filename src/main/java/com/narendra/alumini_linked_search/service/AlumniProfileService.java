package com.narendra.alumini_linked_search.service;

import com.narendra.alumini_linked_search.dto.AlumniProfileResponse;
import com.narendra.alumini_linked_search.dto.AlumniSearchRequest;
import com.narendra.alumini_linked_search.model.AlumniProfile;
import com.narendra.alumini_linked_search.repository.AlumniProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlumniProfileService {

    private final PhantomBusterService phantomService;
    private final AlumniProfileRepository repo;

    public AlumniProfileService(PhantomBusterService phantomService, AlumniProfileRepository repo) {
        this.phantomService = phantomService;
        this.repo = repo;
    }

    public List<AlumniProfileResponse> searchAndSaveAlumniProfiles(AlumniSearchRequest searchRequest) {
        List<AlumniProfileResponse> profiles = phantomService.fetchAlumniProfiles(searchRequest);
        // Save to DB
        List<AlumniProfile> saved = profiles.stream().map(p -> {
            AlumniProfile entity = new AlumniProfile();
            entity.setName(p.getName());
            entity.setCurrentRole(p.getCurrentRole());
            entity.setUniversity(p.getUniversity());
            entity.setLocation(p.getLocation());
            entity.setLinkedinHeadline(p.getLinkedinHeadline());
            entity.setPassoutYear(p.getPassoutYear());
            return repo.save(entity);
        }).collect(Collectors.toList());
        return profiles;
    }

    public List<AlumniProfileResponse> getAllAlumniProfiles() {
        return repo.findAll().stream().map(p -> new AlumniProfileResponse(
                p.getName(), p.getCurrentRole(), p.getUniversity(),
                p.getLocation(), p.getLinkedinHeadline(), p.getPassoutYear()
        )).collect(Collectors.toList());
    }
}

