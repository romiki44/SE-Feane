package com.example.Controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.models.Account;
import com.example.models.Post;
import com.example.services.AccountService;
import com.example.services.PostService;

import jakarta.validation.Valid;



@Controller
public class PostController {
	@Autowired
	private PostService postService;	
	@Autowired
	private AccountService accountService;
	
	@GetMapping("/posts/{id}")
	public String getAllPosts(@PathVariable Long id, Model model, Principal principal) {
		Optional<Post> optPost=postService.getById(id);
		String authUser="email";
		
		if(optPost.isPresent()) {
			Post post=optPost.get();
			model.addAttribute("post", post);
			
			//get username of current logged in user
			//String authUserName=SecurityContextHolder.getContext().getAuthentication().getName();
			
			if(principal!=null) 
				authUser=principal.getName();
			
			if(authUser.equals(post.getAccount().getEmail()))				
				model.addAttribute("isOwner", true);
			else
				model.addAttribute("isOwner", false);
			
			return "posts_views/post";
		}
		
		return "404";
	}
	
	@GetMapping("/posts/add")
	@PreAuthorize("isAuthenticated()")
	public String getPostAdd(Model model, Principal principal) {
		String authUser="email";		
		if(principal!=null) 
			authUser=principal.getName();
		
		Optional<Account> optAccount=accountService.findByEmail(authUser);
		if(optAccount.isPresent()) {
			Post post=new Post();
			post.setAccount(optAccount.get());
			model.addAttribute("post", post);
			return "posts_views/post_add";
		}
		
		return "redirect:/"; 
	}
	
	@PostMapping("/posts/add")
	@PreAuthorize("isAuthenticated()")
	public String postAdd(@Valid @ModelAttribute Post post, BindingResult result, Principal principal) {
		if(result.hasErrors())
			return "posts_views/post_add";
		
		String authUser="email";		
		if(principal!=null) 
			authUser=principal.getName();
		
		if(post.getAccount().getEmail().compareToIgnoreCase(authUser)!=0)
			return "redirect:/?error";
		
		postService.save(post);	
		
		return "redirect:/posts/"+post.getId();
	}
	
	@GetMapping("/posts/{id}/edit")
	@PreAuthorize("isAuthenticated()")
	public String getPostEdit(@PathVariable Long id, Model model) {		
		Optional<Post> optPost=postService.getById(id);
		if(optPost.isPresent()) {
			Post post=optPost.get();
			model.addAttribute("post", post);
			return "posts_views/post_edit";
		}
			
		return "404"; 
	}
	
	@PostMapping("/posts/{id}/edit")
	@PreAuthorize("isAuthenticated()")
	public String postEdit(@PathVariable Long id, @Valid @ModelAttribute Post post, BindingResult result, Principal principal) {
		if(result.hasErrors())
			return "posts_views/post_edit";
		
		String authUser="email";		
		if(principal!=null) 
			authUser=principal.getName();
		
		if(post.getAccount().getEmail().compareToIgnoreCase(authUser)!=0)
			return "redirect:/?error";
		
		Optional<Post> optPost=postService.getById(id);
		if(optPost.isPresent()) {
			Post dbPost=optPost.get();
			dbPost.setTitle(post.getTitle());
			dbPost.setBody(post.getBody());			
			postService.save(dbPost);	
			return "redirect:/posts/"+post.getId();
		}		
		
		return "404";
	}
	
	@GetMapping("/posts/{id}/delete")
	@PreAuthorize("isAuthenticated()")
	public String getPostDelete(@PathVariable Long id, Principal principal) {
		String authUser="email";		
		if(principal!=null) 
			authUser=principal.getName();		
		
		Optional<Post> optPost=postService.getById(id);
		if(optPost.isPresent()) {
			Post dbPost=optPost.get();
			
			if(dbPost.getAccount().getEmail().compareToIgnoreCase(authUser)!=0)
				return "redirect:/?error";
			
			postService.delete(dbPost);			
			return "redirect:/";
		}		
		
		return "404";
	}
}
