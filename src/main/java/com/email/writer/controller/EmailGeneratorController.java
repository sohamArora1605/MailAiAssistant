package com.email.writer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.email.writer.request.EmailRequest;
import com.email.writer.service.emailAssistant;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@CrossOrigin(origins="*")
@RequestMapping("api/email")
public class EmailGeneratorController {
	
	private final emailAssistant emailAsst;
	@PostMapping("/handle")
	public ResponseEntity<String> handleEmailTask(@RequestBody EmailRequest emailRequest) {
	    return ResponseEntity.ok(emailAsst.mailHandler(emailRequest));
	}

	
	@GetMapping("/test")
	public ResponseEntity<String> test() {
	    return ResponseEntity.ok("API is working!");
	}

	
}
