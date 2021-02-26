package ru.sberbank.pprb.sbbol.partners;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;

public class JsonRestClient {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final MockMvc mvc;

    public JsonRestClient(WebApplicationContext webApplicationContext) {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    public <T> T post(String urlTemplate, Object object, Class<T> valueType) {
        return request(urlTemplate, HttpMethod.POST, object, valueType);
    }

    public <T> T get(String urlTemplate, Object object, Class<T> valueType) {
        return request(urlTemplate, HttpMethod.GET, object, valueType);
    }

    public <T> T request(String urlTemplate, HttpMethod httpMethod, Object object, Class<T> valueType) {
        try {
            String inputJSON = objectMapper.writeValueAsString(object);

            MvcResult mvcResult = mvc.perform(
                    MockMvcRequestBuilders.request(httpMethod, urlTemplate).
                            contentType(MediaType.APPLICATION_JSON_VALUE).
                            content(inputJSON)
            ).andReturn();

            if (mvcResult.getResponse().getStatus() != HttpStatus.OK.value()) {
                throw new RuntimeException("Error calling '" + urlTemplate + "' method, Status: '" + mvcResult.getResponse().getStatus() + "', Content: '" + mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8) +"'");
            }
            String result = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
            if (StringUtils.isNotEmpty(result)) {
                return objectMapper.readValue(result, valueType);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}