package com.juicy.study.springboot.web;


import com.juicy.study.springboot.web.domain.posts.Posts;
import com.juicy.study.springboot.web.domain.posts.PostsRepository;
import com.juicy.study.springboot.web.dto.PostsResponseDto;
import com.juicy.study.springboot.web.dto.PostsSaveRequestDto;
import com.juicy.study.springboot.web.dto.PostsUpdateRequestDto;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PostsApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @After
    public void cleanAll() throws Exception{
        postsRepository.deleteAll();
    }

    @Test
    public void Posts_insertTest() throws Exception{
        String title = "title";
        String content = "content";

        PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder()
                .title(title)
                .content(content)
                .author("author")
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts";

        ResponseEntity<Long> responseEntity = restTemplate.postForEntity(url, requestDto, Long.class);

        assertThat(responseEntity.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isGreaterThan(0L);

        List<Posts> list = postsRepository.findAll();
        assertThat(list.get(0).getTitle())
                .isEqualTo(title);
        assertThat(list.get(0).getContent())
                .isEqualTo(content);
    }

    @Test
    public void Posts_PutTest() throws Exception{
        String title = "title";
        String content = "content";
        Posts savedPost = postsRepository.save(Posts.builder()
                .title(title)
                .content(content)
                .author("author")
                .build());

        Long updateId = savedPost.getId();
        String expectedTitle = "title2";
        String expectedContent = "content2";

        PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder()
                .title(expectedTitle)
                .content(expectedContent)
                .build();

        String url = "http://localhost:" + port + "/api/v1/posts/" + updateId;

        HttpEntity<PostsUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto);

        ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.PUT, requestEntity ,Long.class);

        assertThat(responseEntity.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isGreaterThan(0L);

        List<Posts> list = postsRepository.findAll();
        assertThat(list.get(0).getTitle())
                .isEqualTo(expectedTitle);
        assertThat(list.get(0).getContent())
                .isEqualTo(expectedContent);
    }

    @Test
    public void Posts_GetTest() throws Exception{
        String title = "title";
        String content = "content";
        Posts savedPost = postsRepository.save(Posts.builder()
                .title(title)
                .content(content)
                .author("author")
                .build());

        Long getId = savedPost.getId();

        String url = "http://localhost:" + port + "/api/v1/posts/" + getId;

        ResponseEntity<PostsResponseDto> responseEntity = restTemplate.getForEntity(url, PostsResponseDto.class);

        assertThat(responseEntity.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();

        List<Posts> list = postsRepository.findAll();
        assertThat(list.get(0).getTitle())
                .isEqualTo(responseEntity.getBody().getTitle());
        assertThat(list.get(0).getContent())
                .isEqualTo(responseEntity.getBody().getContent());
    }
}
