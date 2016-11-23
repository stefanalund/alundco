package co.alund.apollo;

import com.spotify.apollo.Environment;
import com.spotify.apollo.Request;
import com.spotify.apollo.RequestContext;
import com.spotify.apollo.Response;
import com.spotify.apollo.Status;
import com.spotify.apollo.httpservice.HttpService;
import com.spotify.apollo.httpservice.LoadingException;
import com.spotify.apollo.route.AsyncHandler;
import com.spotify.apollo.route.Middleware;
import com.spotify.apollo.route.Route;
import com.spotify.apollo.route.SyncHandler;

import okio.ByteString;

import java.util.Calendar;
import java.util.Optional;

/**
 * Backend service for my About Me web site.
 *
 * It uses a synchronous route to evaluate addition and a middleware
 * that translates uncaught exceptions into error code 418.
 *
 */
final class AlundApp {

	private static final String FULLNAME = "Stefan Ålund";

	private static Calendar birthday;

	public static void main(String... args) throws LoadingException {
		HttpService.boot(AlundApp::init, "alund-co-service", args);
	}

	static void init(Environment environment) {
		SyncHandler<Response<Integer>> addHandler = context -> add(context.request());
		
		birthday = Calendar.getInstance();
		birthday.set(1980, Calendar.JUNE, 2);

		environment.routingEngine()
		.registerAutoRoute(Route.with(exceptionHandler(), "GET", "/add", addHandler))
		.registerRoute(Route.sync("GET", "/name", AlundApp::name));
	}

	public static Response<ByteString> name(RequestContext requestContext) {
		return Response.ok().withPayload(ByteString.encodeUtf8(FULLNAME));
	}

	/**
	 * A simple adder of request parameters {@code t1} and {@code t2}
	 *
	 * @param request  The request to handle the addition for
	 * @return A response of an integer representing the sum
	 */
	static Response<Integer> add(Request request) {
		Optional<String> t1 = request.parameter("t1");
		Optional<String> t2 = request.parameter("t2");
		if (t1.isPresent() && t2.isPresent()) {
			int result = Integer.valueOf(t1.get()) + Integer.valueOf(t2.get());
			return Response.forPayload(result);
		} else {
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
