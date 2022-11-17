package com.shan.rest.webservices.restfullwebservices.user;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.shan.rest.webservices.restfullwebservices.jpa.PostRepository;
import com.shan.rest.webservices.restfullwebservices.jpa.UserRepository;
import com.shan.rest.webservices.restfullwebservices.post.Post;
import com.shan.rest.webservices.restfullwebservices.versioning.VersioningPersonController;

import jakarta.validation.Valid;

@RestController
public class UserJpaResource {
	
	private UserRepository userRepository;
	
	private PostRepository postRepository;
	
	public UserJpaResource(UserRepository userRepository,PostRepository postRepository) {
		this.userRepository = userRepository;
		this.postRepository = postRepository;
	}

	@GetMapping("/jpa/users")
	public List<User> retreveAllUsers(){
		return userRepository.findAll();
	}
	
	@GetMapping("/jpa/users/{id}")
	public EntityModel<User> retreveUserById(@PathVariable int id) {
		Optional<User> user = userRepository.findById(id);
		if(user.isEmpty()) {
			throw new UserNotFoundException("id: "  + id);
		}else {
			EntityModel<User> entityModel = EntityModel.of(user.get());
			WebMvcLinkBuilder link = linkTo(methodOn(this.getClass()).retreveAllUsers());
			entityModel.add(link.withRel("all-users"));
			return entityModel;
//			return user.get();
		}
	}
	
	@GetMapping("/jpa/users/{id}/user")
	public Optional<User> getUserById(@PathVariable int id) {
		return userRepository.findById(id);
	}
	
	@PostMapping("/jpa/users")
	public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
		User savedUser = userRepository.save(user);
		
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(savedUser.getId())
				.toUri();
		
		return ResponseEntity.created(location).build();
	}
	
	@DeleteMapping("/jpa/users/{id}")
	public void deleteUser(@PathVariable int id) {
		userRepository.deleteById(id);
	}
	
	@GetMapping("/jpa/users/{id}/posts")
	public List<Post> retrievePostsForAUser(@PathVariable int id) {
		Optional<User> user = userRepository.findById(id);
		if(user.isEmpty()) {
			throw new UserNotFoundException("id: " + id);
		}else {
			return user.get().getPosts();
		}
		
	}
	
	@GetMapping("/jpa/posts")
	public List<Post> getPosts() {
		List<Post> posts = postRepository.findAll();
		return posts;
	}

	@PostMapping("/jpa/users/{id}/posts")
	public ResponseEntity<Object> createPostForUser(@Valid @RequestBody Post post,@PathVariable int id) {

		Optional<User> user = userRepository.findById(id);
		if(user.isEmpty()) {
			throw new UserNotFoundException("id: " + id);
		}
		post.setUser(user.get());
		Post savedPost = postRepository.save(post);
		
		URI location = ServletUriComponentsBuilder
				.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(savedPost.getId()) 
				.toUri();
		
		return ResponseEntity.created(location).build();
	}
	
	@GetMapping("/jpa/users/{userId}/posts/{postId}")
	public Optional<Post> getPost(@PathVariable int userId,@PathVariable int postId) {
		Optional<User> user = userRepository.findById(userId);
		if(user.isEmpty()) { 
			throw new UserNotFoundException("id: " + userId);
		}
		Optional<Post> post = postRepository.findById(postId);
		return post;
	}
}


























