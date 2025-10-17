package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.restassured.http.ContentType;

import com.devsuperior.dsmovie.tests.TokenUtil;

public class ScoreControllerRA {

	private String clientUsername, clientPassword, clientToken;
	private Map<String, Object> postScoreInstance;

	@BeforeEach
	public void setUp() throws Exception {

		baseURI = "http://localhost:8080";
		clientUsername = "alex@gmail.com";
		clientPassword = "123456";
		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		postScoreInstance = new HashMap<>();
		postScoreInstance.put("movieId", 1);
		postScoreInstance.put("score", 4);

	}

	@Test
	public void saveScore_ShouldReturnNotFound_WhenMovieIdDoesNotExist() throws Exception {

		postScoreInstance.put("movieId", 31);
		JSONObject newScore = new JSONObject(postScoreInstance);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + clientToken)
				.body(newScore)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.put("/scores")
				.then()
				.statusCode(404)
				.body("error", equalTo("Recurso não encontrado"));

	}

	@Test
	public void saveScore_ShouldReturnUnprocessableEntity_WhenMissingMovieId() throws Exception {

		postScoreInstance = new HashMap<>();
		postScoreInstance.put("score", 4);
		JSONObject newScore = new JSONObject(postScoreInstance);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + clientToken)
				.body(newScore)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.put("/scores")
				.then()
				.statusCode(422)
				.body("errors[0].fieldName", equalTo("movieId"))
				.body("errors[0].message", equalTo("Campo requerido"));

	}

	@Test
	public void saveScore_ShouldReturnUnprocessableEntity_WhenScoreIsLessThanZero() throws Exception {

		postScoreInstance.put("score", -4);
		JSONObject newScore = new JSONObject(postScoreInstance);

		given()
				.header("Content-type", "application/json")
				.header("Authorization", "Bearer " + clientToken)
				.body(newScore)
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.when()
				.put("/scores")
				.then()
				.statusCode(422)
				.body("errors[0].fieldName", equalTo("score"))
				.body("errors[0].message", equalTo("Valor mínimo 0"));

	}
}
