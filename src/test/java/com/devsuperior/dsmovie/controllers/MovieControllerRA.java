package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

import io.restassured.http.ContentType;
import net.minidev.json.JSONObject;

public class MovieControllerRA {

	private String movieTitle;
	private Long existingMovieId, nonExistingMovieId;
	private String clientUsername, clientPassword, adminUsername, adminPassord;
	private String adminToken, clientToken, invalidToken;
	private Map<String, Object> postMovieInstance;

	@BeforeEach
	public void setUp() throws Exception {

		movieTitle = "The Witcher";
		existingMovieId = 1L;
		nonExistingMovieId = 999L;

		adminUsername = "maria@gmail.com";
		adminPassord = "123456";
		clientUsername = "alex@gmail.com";
		clientPassword = "123456";

		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassord);
		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		invalidToken = clientToken + "ABCD";

		postMovieInstance = new HashMap<>();
		postMovieInstance.put("title", "Test Movie");
		postMovieInstance.put("score", 0.0);
		postMovieInstance.put("count", 0);
		postMovieInstance.put("image",
				"https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");

	}

	@Test
	public void findAll_ShouldReturnOk_WhenMovieNoArgumentsGiven() {

		given()
				.get("/movies")
				.then()
				.statusCode(200)
				.body("content.title", hasItems("The Witcher", "Venom: Tempo de Carnificina"));

	}

	@Test
	public void findAll_ShouldReturnPagedMovies_WhenMovieTitleParamIsNotEmpty() {

		given()
				.get("/movies?title={movieTitle}", movieTitle)
				.then()
				.statusCode(200)
				.body("content[0].title", equalTo("The Witcher"))
				.body("content[0].score", is(4.25F))
				.body("content[0].count", is(4))
				.body("content[0].image",
						equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg"));

	}

	@Test
	public void findById_ShouldReturnMovie_WhenIdExists() {

		given()
				.get("/movies/{id}", existingMovieId)
				.then()
				.statusCode(200)
				.body("title", equalTo("The Witcher"))
				.body("score", is(4.25F))
				.body("count", is(4))
				.body("image",
						equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg"));

	}

	@Test
	public void findById_ShouldReturnNotFound_WhenIdDoesNotExist() {

		given()
				.get("/movies/{id}", nonExistingMovieId)
				.then()
				.statusCode(404)
				.body("error", equalTo("Recurso não encontrado"));

	}

	@Test
	public void insert_ShouldReturnUnprocessableEntity_WhenAdminLoggedAndBlankTitle() throws JSONException {

		postMovieInstance.put("title", "");
		JSONObject newMovie = new JSONObject(postMovieInstance);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + adminToken)
				.body(newMovie)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.post("/movies")
				.then()
				.statusCode(422)
				.body("error", equalTo("Dados inválidos"));

	}

	@Test
	public void insert_ShouldReturnForbidden_WhenClientLogged() throws Exception {

		JSONObject newMovie = new JSONObject(postMovieInstance);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + clientToken)
				.body(newMovie)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.post("/movies")
				.then()
				.statusCode(403);
	}

	@Test
	public void insert_ShouldReturnUnauthorized_WhenInvalidToken() throws Exception {

		JSONObject newMovie = new JSONObject(postMovieInstance);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + invalidToken)
				.body(newMovie)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.post("/movies")
				.then()
				.statusCode(401);

	}

}
