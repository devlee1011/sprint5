package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.request.UserRequestDto;
import com.sprint.mission.discodeit.dto.request.UserStatusRequestDto;
import com.sprint.mission.discodeit.dto.request.user.UserCreateRequest2;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;
import com.sprint.mission.discodeit.service.UserService;
import com.sprint.mission.discodeit.service.UserStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "User API")
@Valid
public class UserController {

    private final UserService userService;
    private final UserStatusService userStatusService;

    @Operation(summary = "User 등록", operationId = "create")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User가 성공적으로 생성됨",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = User.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "같은 email 또는 username를 사용하는 User가 이미 존재함,",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject("User with email {email} already exists")
                    )
            )
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> create(@Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
                                       @RequestPart("userCreateRequest") @Validated UserRequestDto.UserCreateRequest userCreateRequest,
                                       @Parameter(description = "User 프로필 이미지")
                                       @RequestPart(name = "profile", required = false) MultipartFile profile) {

        User response = userService.create(userCreateRequest, profile);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "User 정보 수정", operationId = "update")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "404",
                    description = "User를 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject("User with id {userId} not found")
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "같은 email 또는 username을 사용하는 User가 이미 존재합니다.",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject("user with email {newEmail} already exists")
                    )
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "User 정보가 성공적으로 수정됨",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = User.class)
                    )
            )
    })
    @PatchMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<User> update(@Parameter(description = "수정할 User ID")
                                       @PathVariable("userId") UUID userId,
                                       @RequestPart @Validated UserRequestDto.UserUpdateRequest userUpdateRequest,
                                       @Parameter(description = "수정할 User 프로필 이미지")
                                       @RequestPart(required = false) MultipartFile profile) {
        User response = userService.update(userId, userUpdateRequest, profile);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @Operation(summary = "User 삭제", operationId = "delete")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "User가 성공적으로 삭제됨",
                    content = @Content()
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User를 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject("User with id {id} not found")
                    )
            )
    })
    @DeleteMapping(value = "/{userId}")
    public ResponseEntity<?> delete(@Parameter(description = "삭제할 User ID")
                                    @PathVariable("userId") UUID userId) {
        userService.delete(userId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @Operation(summary = "전체 User 목록 조회", operationId = "findAll")
    @ApiResponse(
            responseCode = "200",
            description = "User 목록 조회 성공",
            content = @Content(
                    mediaType = "*/*",
                    array = @ArraySchema(schema = @Schema(implementation = UserRequestDto.class))
            )
    )
    @GetMapping
    public ResponseEntity<List<UserDto>> findAll() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @Operation(summary = "User 온라인 상태 업데이트", operationId = "updateUserStatusByUserId")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "404",
                    description = "해당 User의 UserStatus를 찾을 수 없음",
                    content = @Content(
                            mediaType = "*/*",
                            examples = @ExampleObject("UserStatus with userId {userId} not found")
                    )
            ),
            @ApiResponse(
                    responseCode = "200",
                    description = "User 온라인 상태가 성공적으로 업데이트됨",
                    content = @Content(
                            mediaType = "*/*",
                            schema = @Schema(implementation = UserStatus.class)
                    )
            )
    })
    @PatchMapping(value = "/{userId}/userStatus")
    public ResponseEntity<UserStatus> updateUserStatusByUserId(@Parameter(description = "상태를 변경할 User ID")
                                                               @PathVariable(value = "userId") UUID userId,
                                                               @RequestBody @Validated UserStatusRequestDto.UserStatusUpdateRequest request) {
        UserStatus response = userStatusService.updateByUserId(userId, request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
}
