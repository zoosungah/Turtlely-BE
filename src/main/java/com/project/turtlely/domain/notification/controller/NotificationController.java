package com.project.turtlely.domain.notification.controller;

import com.project.turtlely.domain.notification.dto.NotificationResponse.NotificationListDto;
import com.project.turtlely.domain.notification.exception.NotificationSuccessCode;
import com.project.turtlely.domain.notification.service.NotificationService;
import com.project.turtlely.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "알림", description = "알림 관련 API")
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "최근 7일간 알림 목록 조회", description = "최근 7일간 수신된 알림 목록을 페이징하여 조회합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "최근 7일간 알림 목록 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"isSuccess\": true,\n" +
                                            "  \"code\": \"NOTI200_1\",\n" +
                                            "  \"message\": \"최근 7일간의 알림 목록 조회가 완료되었습니다.\",\n" +
                                            "  \"result\": {\n" +
                                            "    \"notification_list\": [\n" +
                                            "      {\n" +
                                            "        \"notification_id\": 501,\n" +
                                            "        \"type\": \"MONTHLY\",\n" +
                                            "        \"content\": \"N월 월간 리포트가 완성되었습니다!\",\n" +
                                            "        \"is_read\": false,\n" +
                                            "        \"created_at\": \"2026-04-13T10:00:00\"\n" +
                                            "      },\n" +
                                            "      {\n" +
                                            "        \"notification_id\": 502,\n" +
                                            "        \"type\": \"BATTERY\",\n" +
                                            "        \"content\": \"터틀목 배터리가 20% 남았습니다. 배터리를 충전해 주세요.\",\n" +
                                            "        \"is_read\": true,\n" +
                                            "        \"created_at\": \"2026-04-13T08:30:00\"\n" +
                                            "      }\n" +
                                            "    ]\n" +
                                            "  }\n" +
                                            "}"
                            )
                    )
            )
    })
    @GetMapping
    public ApiResponse<NotificationListDto> getNotifications(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        NotificationListDto result = notificationService.getRecentNotifications(userDetails.getUsername(), pageable);

        return ApiResponse.onSuccess(NotificationSuccessCode.NOTIFICATION_GET_SUCCESS, result);
    }

    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 상태(isRead=true)로 변경합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "알림 읽음 처리 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"isSuccess\": true,\n" +
                                            "  \"code\": \"NOTI200_2\",\n" +
                                            "  \"message\": \"알림 읽음 처리가 완료되었습니다.\",\n" +
                                            "  \"result\": null\n" +
                                            "}"
                            )
                    )
            )
    })
    @PatchMapping("/{notificationId}/read")
    public ApiResponse<Void> markNotificationAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal UserDetails userDetails) {

        notificationService.readNotification(notificationId, userDetails.getUsername());

        return ApiResponse.onSuccess(NotificationSuccessCode.NOTIFICATION_READ_SUCCESS, null);
    }

    @Operation(summary = "알림 전체 삭제", description = "사용자의 모든 알림 내역을 삭제합니다.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "알림 전체 삭제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class),
                            examples = @ExampleObject(
                                    value = "{\n" +
                                            "  \"isSuccess\": true,\n" +
                                            "  \"code\": \"NOTI_DELETE_200\",\n" +
                                            "  \"message\": \"모든 알림 내역이 삭제되었습니다.\",\n" +
                                            "  \"result\": null\n" +
                                            "}"
                            )
                    )
            )
    })
    @DeleteMapping
    public ApiResponse<Void> deleteAllNotifications(@AuthenticationPrincipal UserDetails userDetails) {

        notificationService.deleteAllNotifications(userDetails.getUsername());

        return ApiResponse.onSuccess(NotificationSuccessCode.NOTIFICATION_DELETE_SUCCESS, null);
    }
}