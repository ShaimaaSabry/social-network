package com.socialnetwork.domain.inport;

import com.socialnetwork.domain.Post;
import com.socialnetwork.domain.User;
import com.socialnetwork.domain.UserBuilder;
import com.socialnetwork.repository.PostEntity;
import com.socialnetwork.repository.UserEntity;
import com.socialnetwork.rest.dto.CreatePostRequest;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureTestEntityManager
@Transactional
class PostServiceTest {
    @Autowired
    private PostService postService;

    @Autowired
    private TestEntityManager entityManager;

    @Nested
    class GetAll {

        @Test
        void whenUserIdIsValid_ReturnOnlyUserPostList() {
            // GIVEN
            UserEntity user1 = new UserEntity();
//            user1.setId(1L);
            entityManager.persist(user1);
            PostEntity post1 = new PostEntity();
            post1.setUser(user1);
            entityManager.persist(post1);
            PostEntity post2 = new PostEntity();
//            post2.setUser(new UserEntity());
            entityManager.persist(post2);

            // WHEN
            Set<Post> posts = postService.getAll(user1.getId());

            // THEN
            Assertions.assertEquals(1, posts.size());
            MatcherAssert.assertThat(posts, everyItem(hasProperty("user", hasProperty("id", is(user1.getId())))));
        }
    }

    @Nested
    class Create {

        @Test
        void whenCreatePostRequestIsValid_thenCreatePost() {
            // GIVEN
            UserEntity user = new UserEntity();
            user.setId(1L);
//            entityManager.persist(user);

            // WHEN
            CreatePostRequest createPostRequest = new CreatePostRequest("createdPost content.");
            Post post = postService.create(user.getId(), createPostRequest);

            // THEN
            PostEntity createdPost = entityManager.find(PostEntity.class, post.getId());
            Assertions.assertEquals(user.getId(), createdPost.getUser().getId());
            Assertions.assertEquals(createPostRequest.getContent(), createdPost.getContent());
        }
    }
}