package com.example.ApiController;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.example.models.Post;
import com.example.services.PostService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1")
public class HomeApiController {
	@Autowired
	private PostService postService;
	
	Logger logger=LoggerFactory.getLogger(HomeApiController.class);
	
	@GetMapping("/home")
	public List<Post> posts() {
		logger.info("Logging posts at /api/v1/home");
		//toto neni dobry napad, lebo sa vracia vseko v ramci modelu a vynikne rekurzia!!!
		return postService.getAll();
	}
	
}
