package com.email.writer.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.email.writer.request.EmailRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class emailAssistant {

	private final WebClient webclient=WebClient.create();
	
	
	@Value("${GROQ_API_KEY}")
	private String GROQ_API_KEY;
	
	@Value("${GROQ_URL}")
	private String GROQ_URL;
	
	public String mailHandler(EmailRequest emailRequest)
	{
		String prompt=promptBuilder(emailRequest);
		
		Map<String, Object> requestBody=Map.of(
				"model", "qwen/qwen3-32b",
				"messages", new Object[] {
						Map.of(
								"role", "user",
								"content", prompt
						)
				},
				"temperature", 0.6,
				"max_completion_tokens", 4096,
				"top_p", 0.95,
				"stream", false,
				"reasoning_effort", "default"
		);
		
		String response=webclient.post()
		.uri(GROQ_URL)
		.header("Content-Type","application/json")
		.header("Authorization", "Bearer " + GROQ_API_KEY)
		.bodyValue(requestBody)
		.retrieve()
		.bodyToMono(String.class).block();
		
		return extractResponseContent(response);
	}

	private String extractResponseContent(String response) {
		try {
			ObjectMapper mapper=new ObjectMapper();
			JsonNode rootNode=mapper.readTree(response);
			String content = rootNode.path("choices")
					.get(0)
					.path("message")
					.path("content")
					.asText();
			
			// Remove <think> tags from Qwen models
			return content.replaceAll("(?s)<think>.*?</think>", "").trim();
		}
		catch (Exception e)
		{
			return "error processing request"+e.getMessage();
		}
	}

	private String promptBuilder(EmailRequest emailRequest) {
	    String taskType = emailRequest.getTaskType();
	    StringBuilder prompt = new StringBuilder();

	    if ("summarise".equalsIgnoreCase(taskType)) {
	        prompt.append("You are a smart AI assistant that summarizes emails.\n");
	        prompt.append("Summarize the message in 3–6 clear bullet points.\n");
	        prompt.append("Do NOT reply, thank, greet, or comment.\n");
	        prompt.append("Just extract facts as points.\n\n");

	        prompt.append("Email content:\n");
	        prompt.append(emailRequest.getContent()).append("\n\n");

	        prompt.append("Summary:\n");

	    } else if ("translate".equalsIgnoreCase(taskType)) {
	        prompt.append("You are a language assistant.\n");
	        prompt.append("Translate the following email to ");
	        prompt.append(emailRequest.getTargetLanguage()).append(".\n");
	        prompt.append("Do not explain. Only translate the content.\n\n");

	        prompt.append("Email content:\n");
	        prompt.append(emailRequest.getContent()).append("\n\n");

	        prompt.append("Translated output:\n");

	    } else {
	        // Default: reply with tone
	        prompt.append("You are a smart mail AI assistant.\n");
	        prompt.append("Do NOT include greetings, subject lines, or sign-offs.\n");
	        prompt.append("Reply in the specified tone only.\n\n");

	        prompt.append("Tone: ");
	        if (emailRequest.getTone() == null || emailRequest.getTone().isBlank()) {
	            prompt.append("professional");
	        } else {
	            prompt.append(emailRequest.getTone());
	        }
	        prompt.append("\n\n");

	        prompt.append("Instructions:\n");
	        prompt.append("- If yes/no question: reply positively if tone is friendly/loving.\n");
	        prompt.append("- Reply negatively only if message is inappropriate or spammy.\n");
	        prompt.append("- Match the tone and length of original.\n\n");

	        prompt.append("Email content:\n");
	        prompt.append(emailRequest.getContent()).append("\n\n");

	        prompt.append("Reply:\n");
	    }

	    return prompt.toString();
	}



	
	
	
	
}
