package com.project.turtlely.domain.measurement.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.turtlely.domain.measurement.dto.GptAnalysisResponse;
import com.project.turtlely.domain.measurement.exception.MeasurementErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GptService {

    @Value("${openai.secret-key}")
    private String apiKey;

    public GptAnalysisResponse requestPostureAnalysis(double currentCva, double currentCra, String postureType, int totalWatchTimeMinutes) {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();
        String url = "https://api.openai.com/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        String systemPrompt = "You are an expert orthopedic and rehabilitation AI medical advisor specialized in forward head posture (turtle neck syndrome). " +
                "You must provide a response strictly in JSON format matching the requested schema. Do not include any markdown or backticks like ```json.";

        String userPrompt = String.format(
                "Our user's current posture analysis results are as follows:\n" +
                        "- Current CVA (Craniovertebral Angle): %.2f degrees\n" +
                        "- Current CRA (Craniocervical Angle): %.2f degrees\n" +
                        "- Diagnosed Posture Type: %s\n" +
                        "- Total exercise video watch time over the last 3 months: %d minutes\n\n" +
                        "Please generate a medical report including:\n" +
                        "1. general_opinion: A comprehensive diagnosis and advice in Korean (around 2-3 sentences).\n" +
                        "2. top3_diseases: A list of 3 expected potential diseases/symptoms (in Korean) if this posture persists (e.g., 목디스크, 근막통증증후군).\n" +
                        "3. prediction_graph: A sequence of 6 objects representing the 'current month' and the 'subsequent 5 months' (e.g., '6월', '7월', ..., '11월'). " +
                        "Each object must contain 'month' (e.g., '6월') and 'angle' (predicted Cva angle). " +
                        "Calculate the improvement trend logically: assume that higher total exercise watch time leads to better recovery and a faster increase toward normal CVA angle (50+ degrees). If watch time is 0 or very low, the improvement should be minimal or slightly worsening.",
                currentCva, currentCra, postureType, totalWatchTimeMinutes
        );

        Map<String, Object> requestBody = Map.of(
                "model", "gpt-5.4-mini",
                "response_format", Map.of("type", "json_object"),
                "messages", List.of(
                        Map.of("role", "system", "content", systemPrompt),
                        Map.of("role", "user", "content", userPrompt)
                ),
                "temperature", 0.7
        );

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            String rawResponse = response.getBody();

            Map<String, Object> responseMap = objectMapper.readValue(rawResponse, Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseMap.get("choices");
            Map<String, Object> firstChoice = choices.get(0);
            Map<String, Object> messageMap = (Map<String, Object>) firstChoice.get("message");
            String jsonContent = (String) messageMap.get("content");

            return objectMapper.readValue(jsonContent, GptAnalysisResponse.class);
        } catch (Exception e) {
            throw new MeasurementErrorCode.MeasurementCustomException(MeasurementErrorCode.LLM_SERVER_ERROR);
        }
    }
}