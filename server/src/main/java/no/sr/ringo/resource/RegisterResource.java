package no.sr.ringo.resource;

import com.google.inject.servlet.RequestScoped;
import com.sun.jersey.spi.container.ResourceFilters;
import no.sr.ringo.account.RegisterUseCase;
import no.sr.ringo.account.RegistrationData;
import no.sr.ringo.account.RegistrationProcessResult;
import no.sr.ringo.account.ValidationResult;
import org.json.JSONException;
import org.json.JSONStringer;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.Serializable;

/**
 * Register a new user (customer account) in the system.
 * PEPPOL network.
 *
 * @author adam
 */
@Path("/register")
@ResourceFilters(ClientVersionNumberResponseFilter.class)
@RequestScoped
public class RegisterResource  {

    private final RegisterUseCase registerUseCase;

    @Inject
    public RegisterResource(RegisterUseCase registerUseCase) {

        super();
        this.registerUseCase = registerUseCase;
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public String post(String regDataJSON) {

        try {
            RegistrationData registrationData = RegistrationData.fromJson(regDataJSON);

            ValidationResult validation = registerUseCase.validateData(registrationData);
            if (!validation.isValid()) {
                RegistrationResponse result = new RegistrationResponse(validation.isValid(), validation.getMessage(), RegistrationProcessResult.RegistrationSource.RINGO.name());

                throw new InvalidUserInputWebException(result.toJSON());
            }

            RegistrationProcessResult registrationProcessResult = registerUseCase.registerUser(registrationData);


            RegistrationResponse result = new RegistrationResponse(registrationProcessResult.isSuccess(), registrationProcessResult.getMessage(), registrationProcessResult.getSource());
            if (!registrationProcessResult.isSuccess()) {
                throw new InvalidUserInputWebException(registrationProcessResult.getMessage());
            } else {
                return result.toJSON();
            }

        } catch (JSONException e) {
            throw new WebApplicationException(e);
        }
    }

    /**
     * The response of the registration.
     */
    private static class RegistrationResponse implements Serializable{

        private RegistrationStatus status;
        private String message;
        private String source;

        RegistrationResponse(Boolean status, String message, String source) {
            this.status = status ? RegistrationStatus.OK : RegistrationStatus.ERROR;
            this.message = message;
            this.source = source;
        }

        public String toJSON() throws JSONException {
            return new JSONStringer().object().key("status").value(this.status).key("message").value(this.message).key("source").value(this.source).endObject().toString();
        }

        static enum  RegistrationStatus {
            OK, ERROR
        }
    }

}
