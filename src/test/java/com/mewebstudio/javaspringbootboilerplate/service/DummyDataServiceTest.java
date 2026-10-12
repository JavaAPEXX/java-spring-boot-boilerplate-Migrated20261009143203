package com.mewebstudio.javaspringbootboilerplate.service;

import com.mewebstudio.javaspringbootboilerplate.dto.request.user.CreateUserRequest;
import com.mewebstudio.javaspringbootboilerplate.entity.Role;
import com.mewebstudio.javaspringbootboilerplate.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DummyDataServiceTest {

    @Mock
    private RoleService roleService;

    @Mock
    private UserService userService;

    @InjectMocks
    private DummyDataService dummyDataService;

    @BeforeEach
    void setUp() {
        // No specific setup needed as Mockito handles initialization
    }

    @Test
    @DisplayName("Given no roles and no users exist, when run is called, then roles and users should be created")
    void givenNoRolesAndNoUsers_whenRun_thenRolesAndUsersAreCreated() throws Exception {
        // Arrange
        when(roleService.count()).thenReturn(0L);
        when(userService.count()).thenReturn(0L);

        // Act
        assertDoesNotThrow(() -> dummyDataService.run());

        // Assert
        verify(roleService, times(1)).saveList(anyList());
        verify(userService, times(2)).create(any(CreateUserRequest.class));
    }

    @Test
    @DisplayName("Given roles exist but no users, when run is called, then only users should be created")
    void givenRolesExistButNoUsers_whenRun_thenOnlyUsersAreCreated() throws Exception {
        // Arrange
        when(roleService.count()).thenReturn(2L);
        when(userService.count()).thenReturn(0L);

        // Act
        assertDoesNotThrow(() -> dummyDataService.run());

        // Assert
        verify(roleService, never()).saveList(anyList());
        verify(userService, times(2)).create(any(CreateUserRequest.class));
    }

    @Test
    @DisplayName("Given users exist but no roles, when run is called, then only roles should be created")
    void givenUsersExistButNoRoles_whenRun_thenOnlyRolesAreCreated() throws Exception {
        // Arrange
        when(roleService.count()).thenReturn(0L);
        when(userService.count()).thenReturn(2L);

        // Act
        assertDoesNotThrow(() -> dummyDataService.run());

        // Assert
        verify(roleService, times(1)).saveList(anyList());
        verify(userService, never()).create(any(CreateUserRequest.class));
    }

    @Test
    @DisplayName("Given both roles and users exist, when run is called, then no new data should be created")
    void givenBothRolesAndUsersExist_whenRun_thenNoNewDataIsCreated() throws Exception {
        // Arrange
        when(roleService.count()).thenReturn(2L);
        when(userService.count()).thenReturn(2L);

        // Act
        assertDoesNotThrow(() -> dummyDataService.run());

        // Assert
        verify(roleService, never()).saveList(anyList());
        verify(userService, never()).create(any(CreateUserRequest.class));
    }

    @Test
    @DisplayName("Given role service throws exception, when run is called, then exception should be propagated")
    void givenRoleServiceThrowsException_whenRun_thenExceptionIsPropagated() throws Exception {
        // Arrange
        when(roleService.count()).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> dummyDataService.run());
        verify(userService, never()).count();
    }

    @Test
    @DisplayName("Given user service throws exception during count, when run is called, then exception should be propagated")
    void givenUserServiceThrowsExceptionDuringCount_whenRun_thenExceptionIsPropag