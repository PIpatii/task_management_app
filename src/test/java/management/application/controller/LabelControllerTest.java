package management.application.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import management.application.dto.label.CreateLabelRequestDto;
import management.application.dto.label.LabelDto;
import management.application.dto.label.UpdateLabelRequestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static management.application.helper.TestDataHelper.createCreateLabelRequestDto;
import static management.application.helper.TestDataHelper.createUpdateLabelRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "sendgrid.api-key=dummy")
public class LabelControllerTest {
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void beforeEach(@Autowired DataSource dataSource,
                           @Autowired WebApplicationContext webApplicationContext) throws SQLException {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/label/add-two-elements-to-label-table.sql"));
        }
    }

    @AfterEach
    public void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/label/remove-all-elements-from-label-table.sql"));
        }
    }

    @WithMockUser(username = "email", roles = "USER")
    @Test
    @DisplayName("get all labels")
    public void getAllLabels_success() throws Exception {
        int expectedSize = 2;

        MvcResult mvcResult = mockMvc.perform(
                        get("/labels")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(mvcResult.getResponse().getContentAsString());
        LabelDto[] actual = objectMapper.readValue(root.get("content").toString(),
                LabelDto[].class);

        assertEquals(expectedSize, actual.length);
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("create a label")
    public void createLabel_success() throws Exception {
        CreateLabelRequestDto requestDto = createCreateLabelRequestDto("orange","yellow");

        MvcResult mvcResult = mockMvc
                        .perform(post("/labels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        )
                .andExpect(status().isOk())
                .andReturn();

        LabelDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), LabelDto.class);

        assertEquals(requestDto.getColor(), actual.getColor());
        assertEquals(requestDto.getName(), actual.getName());
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("update a label by id")
    public void updateLabel_success() throws Exception {
        UpdateLabelRequestDto requestDto =createUpdateLabelRequestDto("orange","yellow");

        MvcResult mvcResult = mockMvc
                        .perform(put("/labels/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        )
                .andExpect(status().isOk())
                .andReturn();

        LabelDto actual = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), LabelDto.class);

        assertEquals(requestDto.getColor(), actual.getColor());
        assertEquals(requestDto.getName(), actual.getName());
    }

    @WithMockUser(username = "admin", roles = "ADMIN")
    @Test
    @DisplayName("delete a label by id")
    public void deleteLabel_success() throws Exception {
        mockMvc.perform(delete("/labels/{id}", 1L))
                .andExpect(status().isNoContent());
    }
}
