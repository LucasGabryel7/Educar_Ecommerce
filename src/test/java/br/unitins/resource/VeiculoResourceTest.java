package br.unitins.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.unitins.dto.AutoescolaDTO;
import br.unitins.dto.AutoescolaResponseDTO;
import br.unitins.dto.CategoriaHabilitacaoDTO;
import br.unitins.dto.CategoriaHabilitacaoResponseDTO;
import br.unitins.dto.DocumentoVeiculoDTO;
import br.unitins.dto.VeiculoDTO;
import br.unitins.repository.AutoescolaRepository;
import br.unitins.repository.CategoriaHabilitacaoRepository;
import br.unitins.service.AutoescolaService;
import br.unitins.service.CategoriaHabilitacaoService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;

@QuarkusTest
public class VeiculoResourceTest {

    @Inject
    AutoescolaRepository autoescolaRepository;

    @Inject
    AutoescolaService autoescolaService;

    @Inject
    CategoriaHabilitacaoRepository categoriaRepository;

    @Inject
    CategoriaHabilitacaoService categoriaService;

    private Long idAutoescola;
    private Long idCategoria;

    @BeforeEach
    public void setup() {
        AutoescolaResponseDTO auto = autoescolaRepository.findByCnpj("88222333000144")
                .map(AutoescolaResponseDTO::fromEntity)
                .orElseGet(() -> autoescolaService.cadastrar(new AutoescolaDTO(
                        "Autoescola Base Veiculos",
                        "88.222.333/0001-44",
                        "3215-8888",
                        "base.veiculo@autoescola.com",
                        "Rua Veiculo, 20"
                )));
        idAutoescola = auto.id();

        CategoriaHabilitacaoResponseDTO cat = categoriaRepository.find("upper(nome)", "B").firstResultOptional()
                .map(CategoriaHabilitacaoResponseDTO::fromEntity)
                .orElseGet(() -> categoriaService.cadastrar(new CategoriaHabilitacaoDTO("B", "Carros de Passeio")));
        idCategoria = cat.id();
    }

    @Test
    public void testCadastrarEBuscarPorId() {
        DocumentoVeiculoDTO doc = new DocumentoVeiculoDTO("CRLV-REST-01", LocalDate.of(2028, 12, 31));
        VeiculoDTO dto = new VeiculoDTO(
                "RES1A23",
                "Fiat",
                "Argo",
                2023,
                true,
                idAutoescola,
                idCategoria,
                doc
        );

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(dto)
            .when()
                .post("/veiculos")
            .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("placa", equalTo("RES1A23"))
                .body("modelo", equalTo("Argo"))
                .body("documento.numero", equalTo("CRLV-REST-01"))
            .extract()
                .path("id");

        given()
            .when()
                .get("/veiculos/" + id)
            .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("placa", equalTo("RES1A23"));
    }

    @Test
    public void testCadastrarPlacaInvalidaRetorna400() {
        DocumentoVeiculoDTO doc = new DocumentoVeiculoDTO("DOC-INV", LocalDate.of(2028, 12, 31));
        VeiculoDTO dtoInvalido = new VeiculoDTO(
                "PLACA_ERRADA",
                "Fiat",
                "Mobi",
                1800,
                true,
                idAutoescola,
                idCategoria,
                doc
        );

        given()
                .contentType(ContentType.JSON)
                .body(dtoInvalido)
            .when()
                .post("/veiculos")
            .then()
                .statusCode(400);
    }

    @Test
    public void testCadastrarPlacaDuplicadaRetorna400() {
        String placa = "DUP1B22";

        DocumentoVeiculoDTO doc1 = new DocumentoVeiculoDTO("CRLV-DUP-01", LocalDate.of(2028, 12, 31));
        VeiculoDTO dto1 = new VeiculoDTO(
                placa,
                "Chevrolet",
                "Onix",
                2022,
                true,
                idAutoescola,
                idCategoria,
                doc1
        );

        given()
                .contentType(ContentType.JSON)
                .body(dto1)
            .when()
                .post("/veiculos")
            .then()
                .statusCode(201);

        DocumentoVeiculoDTO doc2 = new DocumentoVeiculoDTO("CRLV-DUP-02", LocalDate.of(2028, 12, 31));
        VeiculoDTO dto2 = new VeiculoDTO(
                placa,
                "Chevrolet",
                "Tracker",
                2023,
                true,
                idAutoescola,
                idCategoria,
                doc2
        );

        given()
                .contentType(ContentType.JSON)
                .body(dto2)
            .when()
                .post("/veiculos")
            .then()
                .statusCode(400);
    }

    @Test
    public void testBuscarPorPlaca() {
        String placa = "PLC1C33";
        DocumentoVeiculoDTO doc = new DocumentoVeiculoDTO("CRLV-PLC-01", LocalDate.of(2028, 12, 31));
        VeiculoDTO dto = new VeiculoDTO(
                placa,
                "Hyundai",
                "HB20",
                2022,
                true,
                idAutoescola,
                idCategoria,
                doc
        );

        given()
                .contentType(ContentType.JSON)
                .body(dto)
            .when()
                .post("/veiculos")
            .then()
                .statusCode(201);

        given()
            .when()
                .get("/veiculos/placa/" + placa)
            .then()
                .statusCode(200)
                .body("placa", equalTo(placa))
                .body("marca", equalTo("Hyundai"));
    }

    @Test
    public void testListarTodos() {
        given()
            .when()
                .get("/veiculos")
            .then()
                .statusCode(200);
    }

    @Test
    public void testBuscarPorIdInexistenteRetorna404() {
        given()
            .when()
                .get("/veiculos/999999")
            .then()
                .statusCode(404);
    }

    @Test
    public void testAtualizarEExcluir() {
        DocumentoVeiculoDTO doc = new DocumentoVeiculoDTO("CRLV-ATU-01", LocalDate.of(2028, 12, 31));
        VeiculoDTO dto = new VeiculoDTO(
                "ATU1D44",
                "Renault",
                "Kwid",
                2021,
                true,
                idAutoescola,
                idCategoria,
                doc
        );

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(dto)
            .when()
                .post("/veiculos")
            .then()
                .statusCode(201)
            .extract()
                .path("id");

        DocumentoVeiculoDTO docAtualizado = new DocumentoVeiculoDTO("CRLV-ATU-RENOVADO", LocalDate.of(2029, 12, 31));
        VeiculoDTO dtoAtualizado = new VeiculoDTO(
                "ATU1D44",
                "Renault",
                "Kwid Zen",
                2021,
                false,
                idAutoescola,
                idCategoria,
                docAtualizado
        );

        given()
                .contentType(ContentType.JSON)
                .body(dtoAtualizado)
            .when()
                .put("/veiculos/" + id)
            .then()
                .statusCode(200)
                .body("modelo", equalTo("Kwid Zen"))
                .body("ativo", equalTo(false))
                .body("documento.numero", equalTo("CRLV-ATU-RENOVADO"));

        given()
            .when()
                .delete("/veiculos/" + id)
            .then()
                .statusCode(204);

        given()
            .when()
                .get("/veiculos/" + id)
            .then()
                .statusCode(404);
    }
}
