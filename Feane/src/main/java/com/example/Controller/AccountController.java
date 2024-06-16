package com.example.Controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
//import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.models.Account;
import com.example.services.AccountService;
import com.example.util.AppUtil;

import jakarta.validation.Valid;

@Controller
public class AccountController {
	@Autowired
	private AccountService accountService; 
	
	//toto mi nefunguje!
	//@Value("${spring.mvc.static-path-pattern}")
	//private String photo_prefix;
	
	@GetMapping("/register")
	public String register(Model model) {
		Account account=new Account();
		model.addAttribute("account", account);
		return "account_views/register";
	}
	
	@PostMapping("/register")
	public String register_user(@Valid @ModelAttribute Account account, BindingResult result) {
		if(result.hasErrors())
			return "account_views/register";
		
		accountService.save(account);
		return "redirect:/";
	}
	
	@GetMapping("/login")
	public String login(Model model) {
		return "account_views/login";
	}
	
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/profile")
	public String getProfile(Model model, Principal principal) {	
		String authUser="email";		
		if(principal!=null) 
			authUser=principal.getName();
		
		Optional<Account> optAccount=accountService.findByEmail(authUser);
		if(optAccount.isPresent()) {
			Account account=optAccount.get();			
			model.addAttribute("account", account);
			//photo je sice v account, ale vraj je zvykom to robit takto samsotatne (
			model.addAttribute("photo", account.getPhoto());  
			return "account_views/profile";
		}
		
		return "redirect:/?error"; 		
	}
	
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/profile")
	public String profile(@Valid @ModelAttribute Account account, BindingResult result, Principal principal) {
		if(result.hasErrors())
			return "account_views/profile";
		
		String authUser="email";		
		if(principal!=null) 
			authUser=principal.getName();
		
		Optional<Account> optAccount=accountService.findById(account.getId());
		if(optAccount.isPresent()) {
			Account dbAccount=optAccount.get();
			dbAccount.setFirstName(account.getFirstName());
			dbAccount.setLastName(account.getLastName());
			dbAccount.setDateOfBirth(account.getDateOfBirth());
			dbAccount.setGender(account.getGender());
			dbAccount.setEmail(account.getEmail());
			accountService.save(dbAccount);
			return "redirect:/";
		}
		
		return "redirect:/?error";
	}
	
	//sice uploadne photp spravne do /uploads ale nasledne ho nezobrazi ked idem na /profile...hodi na obr. error 403
	//skusal som vetko mozne, ale nic....po restarte vacsinou obrazok zobrazi, ale nie vzdy....fakt neviem co s tym,
	//je to akesi divne s tou dspring-security...ak sa odhlasim uz sa nenalogujem napr. po upoade, som zistil....
	//...kaslem na to, v jave mvc apku urcite robit nikdy nebudem!!!!!
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/profile/update_photo")
	public String updatePhoto(@RequestParam("file") MultipartFile file, RedirectAttributes attributes, Principal principal) {
		if(file.isEmpty()) {
			attributes.addFlashAttribute("error", "No file uploded");
			return "redirect:/profile";
		}
		System.out.println("start uploading photo...");
		String filename=StringUtils.cleanPath(file.getOriginalFilename());
		String extension=StringUtils.getFilenameExtension(filename);
		String fileNameWithoutExtension=filename.substring(0,filename.lastIndexOf('.'));
		
		try {
			int lenght=10;
			boolean useLetters=true;
			boolean useNumbers=true;
			String randomString=RandomStringUtils.random(lenght,useLetters, useNumbers);
			String finalPhotoFileName=fileNameWithoutExtension+"-"+randomString+"."+extension;
			String absFileLocation=AppUtil.getUploadedPath(finalPhotoFileName);
			String relFileLocation="uploads/"+finalPhotoFileName;
			//debud
			System.out.println(finalPhotoFileName);
			System.out.println(relFileLocation);
			System.out.println(absFileLocation);
			
			Path path=Paths.get(absFileLocation);
			Files.copy(file.getInputStream(),path, StandardCopyOption.REPLACE_EXISTING);
			attributes.addFlashAttribute("message", "Photo succesfuly uploaded");
			
			String authUser="email";		
			if(principal!=null) 
				authUser=principal.getName();
			Optional<Account> optAccount=accountService.findByEmail(authUser);
			if(optAccount.isPresent()) {
				Account dbAccount=optAccount.get();
				//toto je sprostost, lebo takto ulozim len foto ale ostatne zmeny, ak boli, tak stratim!!!
				//mal by byt len jeden post kde sa ulozia polozky a ak sa uploadne photo, tak aj foto...ale nie takto!  
				dbAccount.setPhoto(relFileLocation);		
				accountService.save(dbAccount);
				//vraj to malo pomoct abz sa zobrzila fotka, lebo vraj ta H2-Db je pomala...no nepomohlo!!! 403 pretrvava...				
				TimeUnit.SECONDS.sleep(1);
				return "redirect:/profile";
			}
			
			return "redirect:/?error";
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		return "redirect:/";
	}
	
}
