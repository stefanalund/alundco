package co.alund.apollo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.spotify.apollo.Environment;
import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;
import com.spotify.apollo.httpservice.HttpService;
import com.spotify.apollo.httpservice.LoadingException;
import com.spotify.apollo.route.AsyncHandler;
import com.spotify.apollo.route.JsonSerializerMiddlewares;
import com.spotify.apollo.route.Middleware;
import com.spotify.apollo.route.Route;
import com.spotify.apollo.route.SyncHandler;

import co.alund.apollo.data.Person;
import okio.ByteString;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

/**
 * Backend service for my About Me web site.
 *
 * It uses a synchronous route to evaluate addition and a middleware
 * that translates uncaught exceptions into error code 418.
 *
 */
final class AlundApp {

	private static List<Person> family;
	
	private static ObjectMapper objectMapper = new ObjectMapper();
	private static ObjectWriter objectWriter = objectMapper.writer();

	public static void main(String... args) throws LoadingException {
		HttpService.boot(AlundApp::init, "alund-co-service", args);
	}

	static void init(Environment environment) {
		family = Arrays.asList(
				new Person("Stefan Ålund", Person.MALE, "1980-06-02"), 
				new Person("Anna Ålund", Person.FEMALE, "1980-02-02"),
				new Person("Ella Ålund", Person.FEMALE, "2008-07-13"),
				new Person("Ines Ålund", Person.FEMALE, "2011-03-22"),
				new Person("Hugo Ålund", Person.MALE, "2014-03-17"));
		
		environment.routingEngine()
		.registerRoute(Route.sync("GET", "/stefan", AlundApp::name).withMiddleware(
				JsonSerializerMiddlewares.jsonSerializeResponse(objectWriter)))
		.registerRoute(Route.sync("GET", "/family", AlundApp::familyList).withMiddleware(
						JsonSerializerMiddlewares.jsonSerializeResponse(objectWriter)));
	}
	
	public static Response<Person> name(RequestContext requestContext) {
		try {
			return Response.ok().withPayload(family.get(0));
		} catch (Exception e) {
			return Response.forStatus(Status.BAD_REQUEST);
		}
	}
	
	public static Response<List<Person>> familyList(RequestContext requestContext) {
		try {
			return Response.ok().withPayload(family);
		} catch (Exception e) {
			return Response.forStatus(Status.BAD_REQUEST);
		}
	}

	/**
	 * A generic middleware that maps uncaught exceptions to error code 418
	 */
	static <T> Middleware<SyncHandler<Response<T>>, SyncHandler<Response<T>>> exceptionMiddleware() {
		return handler -> requestContext -> {
			try {
				return handler.invoke(requestContext);
			} catch (RuntimeException e) {
				return Response.forStatus(Status.IM_A_TEAPOT);
			}
		};
	}

	/**
	 * Async version of {@link #exceptionMiddleware()}
	 */
	static <T> Middleware<SyncHandler<Response<T>>, AsyncHandler<Response<T>>> exceptionHandler() {
		return AlundApp.<T>exceptionMiddleware().and(Middleware::syncToAsync);
	}
}
