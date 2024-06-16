package com.example.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.models.Authority;
import com.example.models.Post;
import com.example.services.AuthorityService;
import com.example.services.PostService;


@Controller
public class HomeController {
	@Autowired
	private PostService postService;
	
	//https://www.baeldung.com/spring-circular-view-path-error
	@GetMapping("/")
	public String home(Model model) {
		List<Post> posts=postService.getAll();
		model.addAttribute("posts", posts);
		return "home_views/home";
	}
	
	@GetMapping("/editor")
	public String editor(Model model) {		
		return "home_views/editor";
	}	
	
	@GetMapping("/book")
	public String book(Model model) {		
		return "home_views/book";
	}
}
