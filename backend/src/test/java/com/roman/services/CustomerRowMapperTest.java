package com.roman.services;

import com.roman.models.Customer;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomerRowMapperTest {

    @Test
    void itShouldMapRow() throws SQLException {
        // Given
        CustomerRowMapper rowMapper = new CustomerRowMapper();
        ResultSet resultSet = mock(ResultSet.class);

        when(resultSet.getInt("id")).thenReturn(1);
        when(resultSet.getString("name")).thenReturn("alex");
        when(resultSet.getString("email")).thenReturn("email");
        when(resultSet.getInt("age")).thenReturn(23);
        //When
        Customer actual = rowMapper.mapRow(resultSet, 1);
        //Then
        Customer expected = new Customer(1, "alex", "email", 23);

        assertThat(actual).isEqualToComparingFieldByField(expected);

    }
}