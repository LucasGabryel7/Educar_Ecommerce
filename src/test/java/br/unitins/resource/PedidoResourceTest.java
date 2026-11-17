package br.unitins.resource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.unitins.dto.AlunoDTO;
import br.unitins.dto.AlunoResponseDTO;
import br.unitins.dto.AulaTeoricaDTO;
import br.unitins.dto.AulaTeoricaResponseDTO;
import br.unitins.dto.ItemPedidoDTO;
import br.unitins.dto.PedidoDTO;
import br.unitins.dto.StatusPedidoDTO;
import br.unitins.model.StatusPedido;
import br.unitins.repository.AlunoRepository;
import br.unitins.repository.AulaTeoricaRepository;
import br.unitins.service.AlunoService;
import br.unitins.service.AulaTeoricaService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;

@QuarkusTest
public class PedidoResourceTest {

    @Inject
    AlunoService alunoService;

    @Inject
    AlunoRepository alunoRepository;

    @Inject
    AulaTeoricaService aulaTeoricaService;

    @Inject
    AulaTeoricaRepository aulaTeoricaRepository;

    private Long idAluno;
    private Long idOferta;

    @BeforeEach
    public void setup() {
        AlunoResponseDTO aluno = alunoRepository.findByCpf("99988877766")
                .map(AlunoResponseDTO::fromEntity)
                .orElseGet(() -> alunoService.cadastrar(new AlunoDTO(
                        "Aluno Pedido Rest Test",
                        "999.888.777-66",
                        "9999-8888",
                        "aluno.pedido.rest@email.com",
                        true
                )));
        idAluno = aluno.id();

        AulaTeoricaResponseDTO oferta = aulaTeoricaRepository.find("nome", "Aula Teórica para Pedido Rest").firstResultOptional()
                .map(AulaTeoricaResponseDTO::fromEntity)
                .orElseGet(() -> aulaTeoricaService.cadastrar(new AulaTeoricaDTO(
                        "Aula Teórica para Pedido Rest",
                        "Aula de teste para endpoint de pedidos",
                        new BigDecimal("60.00"),
                        5
                )));
        idOferta = oferta.id();
    }

    @Test
    public void testCadastrarEBuscarPorId() {
        ItemPedidoDTO item = new ItemPedidoDTO(idOferta, 2);
        PedidoDTO dto = new PedidoDTO(idAluno, List.of(item));

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(dto)
            .when()
                .post("/pedidos")
            .then()
                .statusCode(201)
                .body("id", notNullValue())
                .body("idAluno", equalTo(idAluno.intValue()))
                .body("status", equalTo("AGUARDANDO_PAGAMENTO"))
                .body("valorTotal", equalTo(120.0f))
            .extract()
                .path("id");

        given()
            .when()
                .get("/pedidos/" + id)
            .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("status", equalTo("AGUARDANDO_PAGAMENTO"));

        // Limpeza
        given()
            .when()
                .delete("/pedidos/" + id)
            .then()
                .statusCode(204);
    }

    @Test
    public void testCadastrarComAlunoInexistenteRetorna404() {
        PedidoDTO dto = new PedidoDTO(999999L, List.of(new ItemPedidoDTO(idOferta, 1)));

        given()
                .contentType(ContentType.JSON)
                .body(dto)
            .when()
                .post("/pedidos")
            .then()
                .statusCode(404);
    }

    @Test
    public void testCadastrarComAlunoInativoRetorna400() {
        AlunoResponseDTO inativo = alunoRepository.findByCpf("88877766655")
                .map(AlunoResponseDTO::fromEntity)
                .orElseGet(() -> alunoService.cadastrar(new AlunoDTO(
                        "Aluno Inativo Resource Test",
                        "888.777.666-55",
                        "9111-2222",
                        "aluno.inativo.resource@email.com",
                        false
                )));

        PedidoDTO dto = new PedidoDTO(inativo.id(), List.of(new ItemPedidoDTO(idOferta, 1)));

        given()
                .contentType(ContentType.JSON)
                .body(dto)
            .when()
                .post("/pedidos")
            .then()
                .statusCode(400);
    }

    @Test
    public void testCadastrarComOfertaInexistenteRetorna404() {
        PedidoDTO dto = new PedidoDTO(idAluno, List.of(new ItemPedidoDTO(999999L, 1)));

        given()
                .contentType(ContentType.JSON)
                .body(dto)
            .when()
                .post("/pedidos")
            .then()
                .statusCode(404);
    }

    @Test
    public void testCadastrarComDadosInvalidosRetorna400() {
        // Pedido sem itens
        PedidoDTO semItens = new PedidoDTO(idAluno, List.of());

        given()
                .contentType(ContentType.JSON)
                .body(semItens)
            .when()
                .post("/pedidos")
            .then()
                .statusCode(400);

        // Pedido sem aluno
        PedidoDTO semAluno = new PedidoDTO(null, List.of(new ItemPedidoDTO(idOferta, 1)));

        given()
                .contentType(ContentType.JSON)
                .body(semAluno)
            .when()
                .post("/pedidos")
            .then()
                .statusCode(400);
    }

    @Test
    public void testListarTodos() {
        given()
            .when()
                .get("/pedidos")
            .then()
                .statusCode(200);
    }

    @Test
    public void testBuscarPorIdInexistenteRetorna404() {
        given()
            .when()
                .get("/pedidos/999999")
            .then()
                .statusCode(404);
    }

    @Test
    public void testBuscarPorAluno() {
        // Aluno existente
        given()
            .when()
                .get("/pedidos/aluno/" + idAluno)
            .then()
                .statusCode(200);

        // Aluno inexistente
        given()
            .when()
                .get("/pedidos/aluno/999999")
            .then()
                .statusCode(404);
    }

    @Test
    public void testBuscarPorStatus() {
        // Status válido
        given()
            .when()
                .get("/pedidos/status/AGUARDANDO_PAGAMENTO")
            .then()
                .statusCode(200);

        // Status inválido
        given()
            .when()
                .get("/pedidos/status/STATUS_INEXISTENTE")
            .then()
                .statusCode(400);
    }

    @Test
    public void testAtualizarStatusEExcluir() {
        PedidoDTO dto = new PedidoDTO(idAluno, List.of(new ItemPedidoDTO(idOferta, 1)));

        Integer id = given()
                .contentType(ContentType.JSON)
                .body(dto)
            .when()
                .post("/pedidos")
            .then()
                .statusCode(201)
            .extract()
                .path("id");

        // 1. Atualizar status via JSON DTO (Jackson) e verificar que valores e itens não mudam
        StatusPedidoDTO statusPago = new StatusPedidoDTO(StatusPedido.PAGO);
        given()
                .contentType(ContentType.JSON)
                .body(statusPago)
            .when()
                .patch("/pedidos/" + id + "/status")
            .then()
                .statusCode(200)
                .body("status", equalTo("PAGO"))
                .body("valorTotal", equalTo(60.0f))
                .body("itens.size()", equalTo(1))
                .body("itens[0].precoUnitario", equalTo(60.0f))
                .body("itens[0].subtotal", equalTo(60.0f));

        // 2. Atualizar status via query param
        given()
            .when()
                .patch("/pedidos/" + id + "/status?status=CANCELADO")
            .then()
                .statusCode(200)
                .body("status", equalTo("CANCELADO"))
                .body("valorTotal", equalTo(60.0f))
                .body("itens.size()", equalTo(1));

        // 3. Status inválido recebido por JSON retorna 400
        given()
                .contentType(ContentType.JSON)
                .body("{\"status\": \"STATUS_INVALIDO\"}")
            .when()
                .patch("/pedidos/" + id + "/status")
            .then()
                .statusCode(400);

        // 4. Status nulo recebido por JSON retorna 400 (Bean Validation @NotNull)
        given()
                .contentType(ContentType.JSON)
                .body("{\"status\": null}")
            .when()
                .patch("/pedidos/" + id + "/status")
            .then()
                .statusCode(400);

        // 5. Status inválido via query param retorna 400
        given()
            .when()
                .patch("/pedidos/" + id + "/status?status=STATUS_INVALIDO")
            .then()
                .statusCode(400);

        // 6. Atualizar pedido inexistente via JSON retorna 404
        given()
                .contentType(ContentType.JSON)
                .body(statusPago)
            .when()
                .patch("/pedidos/999999/status")
            .then()
                .statusCode(404);

        // 7. Atualizar pedido inexistente via query param retorna 404
        given()
            .when()
                .patch("/pedidos/999999/status?status=PAGO")
            .then()
                .statusCode(404);

        // 8. Excluir pedido
        given()
            .when()
                .delete("/pedidos/" + id)
            .then()
                .statusCode(204);

        // 9. Excluir pedido inexistente retorna 404
        given()
            .when()
                .delete("/pedidos/" + id)
            .then()
                .statusCode(404);

        // 10. Buscar pedido excluído retorna 404
        given()
            .when()
                .get("/pedidos/" + id)
            .then()
                .statusCode(404);
    }
}
