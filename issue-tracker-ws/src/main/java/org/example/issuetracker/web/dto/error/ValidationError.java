package org.example.issuetracker.web.dto.error;

import lombok.Data;

@Data
public class ValidationError {

	private String code;
	private String message;
}
