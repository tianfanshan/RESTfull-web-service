package com.shan.rest.webservices.restfullwebservices.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shan.rest.webservices.restfullwebservices.post.Post;

public interface PostRepository extends JpaRepository<Post, Integer>{

}
