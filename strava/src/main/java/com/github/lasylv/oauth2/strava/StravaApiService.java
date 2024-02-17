package com.github.lasylv.oauth2.strava;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StravaApiService {

    private final OAuth2AuthorizedClientService authorizedClientService;

    public StravaApiService(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    public List<ActivityRecord> getActivitiesFromStrava() {

        // Get the OAuth2AuthorizedClient for the user
        OAuth2AuthenticationToken authentication = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(authentication.getAuthorizedClientRegistrationId(), principal.getName());
        // Make an authenticated request to the Strava API
        RestTemplate restTemplate = new RestTemplate();


        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authorizedClient.getAccessToken().getTokenValue());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<List<ActivityRecord>> response = restTemplate.exchange(
                "https://www.strava.com/api/v3/activities/",
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {
                });

        if( response.getStatusCode()!= HttpStatus.OK) {
            // TODO exception
        }
        return response.getBody();
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record ActivityRecord(
            int resourceState,
            Athlete athlete,
            String name,
            double distance,
            int movingTime,
            int elapsedTime,
            double totalElevationGain,
            String type,
            String sportType,
            int workoutType,
            long id,
            LocalDateTime startDate,
            LocalDateTime startDateLocal,
            String timezone,
            double utcOffset,
            String locationCity,
            String locationState,
            String locationCountry,
            int achievementCount,
            int kudosCount,
            int commentCount,
            int athleteCount,
            int photoCount,
            Map map,
            boolean trainer,
            boolean commute,
            boolean manual,
            boolean isPrivate,
            String visibility,
            boolean flagged,
            String gearId,
            List<Double> startLatlng,
            List<Double> endLatlng,
            double averageSpeed,
            double maxSpeed,
            double averageCadence,
            int averageTemp,
            boolean hasHeartrate,
            double averageHeartrate,
            double maxHeartrate,
            boolean heartrateOptOut,
            boolean displayHideHeartrateOption,
            double elevHigh,
            double elevLow,
            long uploadId,
            String uploadIdStr,
            String externalId,
            boolean fromAcceptedTag,
            int prCount,
            int totalPhotoCount,
            boolean hasKudoed,
            double sufferScore,
            List<SegmentEffort> segmentEfforts
    ) {
        public record Athlete(int id, int resourceState) {}

        public record Map(String id, String summaryPolyline, int resourceState) {}

        public record SegmentEffort(
                long id,
                int resourceState,
                String name,
                Activity activity,
                Athlete athlete,
                int elapsedTime,
                int movingTime,
                LocalDateTime startDate,
                LocalDateTime startDateLocal,
                double distance,
                int startIndex,
                int endIndex,
                double averageCadence,
                boolean deviceWatts,
                double averageWatts,
                Segment segment,
                Integer komRank,
                Integer prRank,
                List<Object> achievements,
                boolean hidden
        ) {
            public record Activity(long id, int resourceState) {}

            public record Segment(
                    int id,
                    int resourceState,
                    String name,
                    String activityType,
                    double distance,
                    double averageGrade,
                    double maximumGrade,
                    double elevationHigh,
                    double elevationLow,
                    List<Double> startLatlng,
                    List<Double> endLatlng,
                    int climbCategory,
                    String city,
                    String state,
                    String country,
                    boolean isPrivate,
                    boolean hazardous,
                    boolean starred
            ) {}
        }
    }


}