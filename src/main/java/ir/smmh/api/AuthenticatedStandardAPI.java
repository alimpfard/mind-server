package ir.smmh.api;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A standard API is an API whose requests are JSON maps that contain the
 * 'method', 'parameters', and optionally 'authentication' keys, and whose
 * responses are JSON maps that contain the 'error_code', 'description', and
 * optionally 'results' keys.
 *
 * @param <M> An API-specific method type
 * @param <A> An API-specific authenticated type, usually a log-in session
 */
public abstract class AuthenticatedStandardAPI<A> extends StandardAPI {

    public abstract A authenticate(JSONObject authentication);

    public final int AUTHENTICATION_FAILED = defineError(12, "Authentication failed");

    @SuppressWarnings("unchecked")
    public @NotNull JSONObject processAuthenticatedMethod(@NotNull Method.AuthenticatedMethod<?> uncheckedMethod, @Nullable JSONObject authentication, @NotNull JSONObject parameters) {
        final Method.AuthenticatedMethod<A> method;
        try {
            method = (Method.AuthenticatedMethod<A>) uncheckedMethod;
        } catch (ClassCastException e) {
            System.err.println("Failed to cast an unchecked authenticated method");
            return respond(BUG);
        }
        @Nullable final A authenticated = authenticate(authentication);
        if (method instanceof Method.AuthenticationRequired) {
            final Method.AuthenticationRequired<A> required = (Method.AuthenticationRequired<A>) method;
            if (authenticated == null)
                return respond(AUTHENTICATION_FAILED);
            return required.process(authenticated, parameters);
        } else if (method instanceof Method.AuthenticationOptional) {
            final Method.AuthenticationOptional<A> optional = (Method.AuthenticationOptional<A>) method;
            return optional.process(authenticated, parameters);
        } else {
            System.err.println("You must not extend/implement Method.Authenticated directly");
            return respond(BUG);
        }
    }
}