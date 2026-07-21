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
                "You must provide a response strictly in JSON format matching the requested schema. Do not include any markdown formatting or backticks like ```json.";

        String userPrompt = String.format(
                "Our user's current posture analysis results are as follows:\n" +
                        "- Current CVA (Craniovertebral Angle): %.2f degrees\n" +
                        "- Current CRA (Craniocervical Angle): %.2f degrees\n" +
                        "- Diagnosed Posture Type: %s\n" +
                        "- Total exercise video watch time over the last 3 months: %d minutes\n\n" +
                        "Please generate a medically logical report including the following strictly defined JSON keys:\n\n" +
                        //1. 경추 건강 점수 산출
                        /* - 100점 만점에서 시작해서 목 상태에 따라 비례해서 감점
                         *  - CVA 표존인 48.7도에서 멀어질수록, CRA 표준 145도에서 멀어질수록 감점*/
                        // - 백엔드에서 결정된 postureType(%s) 바운더리 내에서만 점수 부여 (CRA 오차로 세부 보정)
                        "1. cervical_health_score: An integer score from 0 to 100. " +
                        "You MUST assign the score strictly within the boundary determined by the given Diagnosed Posture Type ('%s'):\n" +
                        "   - If Diagnosed Posture Type is '정상' (Normal): Score MUST be between 85 and 100.\n" +
                        "   - If Diagnosed Posture Type is '주의' (Caution): Score MUST be between 70 and 84.\n" +
                        "   - If Diagnosed Posture Type is '위험' (Warning/Severe): Score MUST be below 69.\n" +
                        "   * CRITICAL RULE: Under NO circumstances should a '위험' posture receive a score higher than 69!\n" +
                        "   (Fine-tune the exact score within the allowed range based on CRA deviation from 145 degrees).\n\n" +
                        //2. 예상 질병 top3
                        /* - 단순 문자열 배열이 아닌 프론트 UI 바 채울 수 있게 'name(질병명)'과 'probability(0~100 확률)'를 가진 객체 구조로
                         * - 이 확률은 현재 거북목 심각도와 비례해야 함*/
                        "2. top3_diseases: A list of 3 expected potential diseases/symptoms if this posture persists. " +
                        "Each object in the list must strictly contain 'name' (in Korean, e.g., '목디스크') and 'score' (a calculated risk probability between 0.00 and 1.00 based on severity, e.g., 0.85).\n" +
                        "3. prediction_graph: A sequence of 6 objects representing the 'current month' and the 'subsequent 5 months' (e.g., '6월', '7월', ..., '11월'). " +
                        "Each object must contain 'month' (e.g., '6월') and 'angle' (predicted CVA angle). Higher exercise watch time leads to a faster recovery toward 50+ degrees.",
                currentCva, currentCra, postureType, totalWatchTimeMinutes, postureType, postureType
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